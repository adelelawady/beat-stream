package com.konsol.beatstream.service.audioPlugins.Spotify;

import static com.konsol.beatstream.service.bucket.BucketManager.rootPath;

import com.konsol.beatstream.config.AppSettingsConfiguration;
import com.konsol.beatstream.domain.TaskNode;
import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.domain.enumeration.DownloadStatus;
import com.konsol.beatstream.repository.TaskNodeRepository;
import com.konsol.beatstream.repository.TrackRepository;
import com.konsol.beatstream.service.TrackService;
import com.konsol.beatstream.service.audioPlugins.youtube.YouTubeVideoInfoScraper;
import com.konsol.beatstream.service.audioPlugins.youtube.YoutubeDownloader;
import com.konsol.beatstream.service.bucket.BucketManager;
import com.konsol.beatstream.web.websocket.TaskService;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpotifyDownloader {

    String startupPath = Paths.get("").toAbsolutePath().toString();

    private TaskNode taskNode = null;
    // Path to the yt-dlp.exe file
    private String YTDLP_PATH;
    private String FFMPEG_PATH;
    private boolean enableDublicated = false;
    String chromedriver;

    public SpotifyDownloader() {
        try {
            YTDLP_PATH = AppSettingsConfiguration.getSettings()
                .getProperty("beatstream.settings.plugins.yt-dl_path", startupPath + "\\plugins\\yt-dlp.exe");
            FFMPEG_PATH = AppSettingsConfiguration.getSettings()
                .getProperty("beatstream.settings.plugins.ffmpeg_path", startupPath + "\\plugins\\ffmpeg.exe");
            enableDublicated = Boolean.parseBoolean(
                AppSettingsConfiguration.getSettings().getProperty("beatstream.settings.spotify.duplicated.enabled", "false")
            );
            chromedriver = AppSettingsConfiguration.getSettings()
                .getProperty("beatstream.settings.plugins.chromedriver_path", startupPath + "\\plugins\\chromedriver.exe");
        } catch (Exception e) {
            enableDublicated = false;
            YTDLP_PATH = startupPath + "\\plugins\\yt-dlp.exe";
            FFMPEG_PATH = startupPath + "\\plugins\\ffmpeg.exe";
            chromedriver = startupPath + "\\plugins\\chromedriver.exe";
        }
    }

    //  private final String FFMPEG_PATH = startupPath + "\\plugins\\ffmpeg.exe";
    private static final Logger LOG = LoggerFactory.getLogger(SpotifyDownloader.class);

    @Autowired
    TaskNodeRepository taskNodeRepository;

    @Autowired
    TaskService taskService;

    @Autowired
    TrackRepository trackRepository;

    @Autowired
    TrackService trackService;

    public void downloadYouTubeVideoAsMP3(String videoUrl, String videoId, String outputPath, Track track)
        throws IOException, InterruptedException {
        // Prepare the yt-dlp command
        String command =
            "\"" + YTDLP_PATH + "\" -x --audio-format mp3 --ffmpeg-location \"" + FFMPEG_PATH + "\" -o " + outputPath + " " + videoUrl;

        Pattern genericPattern = Pattern.compile("[a-zA-Z0-9@.]+");

        //[download]   0.0% of    3.75MiB at  495.49KiB/s ETA 00:07
        // Run the command
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // Capture the process output
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                LOG.debug(line);

                if (taskNode != null) {
                    taskNode.setTaskLog(taskNode.getTaskLog() + line + "\n");
                    taskNodeRepository.save(taskNode);
                    if (line.contains("[download]") && line.contains("ETA")) {
                        Matcher matcher = genericPattern.matcher(line);
                        String percentage = "";
                        String filesize = "";
                        String speed = "";
                        String eta1 = "";
                        String eta2 = "";
                        int index = 0;
                        while (matcher.find()) {
                            String match = matcher.group();
                            if (index == 1) percentage = match;
                            else if (index == 3) filesize = match;
                            else if (index == 5) speed = match;
                            else if (index == 8) eta1 = match;
                            else if (index == 9) eta2 = match;
                            index++;
                        }
                        if (!percentage.isEmpty() && !filesize.isEmpty() && !speed.isEmpty() && !eta1.isEmpty() && !eta2.isEmpty()) {
                            taskNode.setProgressPercentage(BigDecimal.valueOf(Double.parseDouble(percentage)));
                            taskNode.setDownloadFilesize(filesize);
                            taskNode.setDownloadSpeed(speed);
                            taskNode.setDownloadEta(eta1 + ":" + eta2);
                            taskNodeRepository.save(taskNode);
                            taskService.sendTaskNodes();
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.debug(e.getMessage());
        }

        // Wait for the process to finish
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            taskNode.setStatus(DownloadStatus.CONVERSION);
            taskNodeRepository.save(taskNode);
            taskService.sendTaskNodes();
            try {
                Path youtubeDownloadDirPath = rootPath.resolve("spotifyDl");
                String outputPathString = youtubeDownloadDirPath + "\\" + videoId + "\\" + videoId + ".mp3";
                Path outputPath1 = Path.of(outputPathString);

                Track track1 = extrackAndSaveTrackData(track, outputPath1, videoUrl);
                taskService.sendTaskNodes();
                moveAndConnectDownloadedFile(track1, outputPath1);
                File file = new File(outputPath);
                file.delete();
                taskService.sendTaskNodes();
            } catch (Exception e) {
                LOG.debug("Video Conversation  Failed at: " + e.getMessage());
                trackService.delete(track.getId());
            }

            LOG.debug("Download complete!");
        } else {
            LOG.debug("Download failed. Exit code: " + exitCode);
        }
    }

    private void moveAndConnectDownloadedFile(Track track, Path outputPath) {
        try {
            trackService.connectTrackToAudioFile(track.getId(), outputPath.toString());
        } catch (Exception e) {
            LOG.debug(e.getMessage());
            throw e;
        }
    }

    private Track extrackAndSaveTrackData(Track track, Path outputPath, String videoUrl) {
        if (!outputPath.toFile().exists()) {
            trackService.delete(track.getId());
            throw new RuntimeException("File Not Found");
        }
        try {
            // Read the audio file and its metadata using jaudiotagger
            AudioFile audioFile = AudioFileIO.read(outputPath.toFile());
            Tag tag = audioFile.getTag();

            // Extract metadata
            String artist = tag.getFirst(FieldKey.ARTIST);
            String title = tag.getFirst(FieldKey.TITLE);
            String album = tag.getFirst(FieldKey.ALBUM);
            String genre = tag.getFirst(FieldKey.GENRE);
            String year = tag.getFirst(FieldKey.YEAR);

            AudioHeader audioHeader = audioFile.getAudioHeader();

            try {
                title = YouTubeVideoInfoScraper.fetchYouTubeVideoTitle(videoUrl);

                if (taskNode != null) {
                    taskNode.setTaskName("[TASK] " + title);
                    taskNodeRepository.save(taskNode);
                    taskService.sendTaskNodes();
                }
            } catch (Exception e) {
                title = tag.getFirst(FieldKey.TITLE);
            }

            track.setTitle(title);
            track.setDuration((long) audioHeader.getTrackLength());

            return trackRepository.save(track);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void AddSpotifySong(String spotifyUrl, String playlistId, TaskNode _taskNode, String ownerId) throws Exception {
        taskNode = _taskNode;

        BucketManager manager = new BucketManager();

        try {
            manager.createBucket("spotifyDl");
            manager.createBucket(ownerId);
            manager.createBucket(ownerId + "\\" + "audioFiles");
        } catch (IOException e) {
            LOG.debug(e.getMessage());
            throw new Exception(e);
        }

        String foundYoutubeVideoId = GrapeYoutubeVideoIdFromSpotifyUrl(spotifyUrl);

        if (foundYoutubeVideoId == null) {
            throw new Exception("Connote download Spotify Song :" + spotifyUrl);
        }
        /**
         * GET SPORTIFY YOUTUBE VIDEO URL PLEASE
         *
         */

        Path spotifyDownloadDirPath = rootPath.resolve("spotifyDl");
        String videoUrl = "https://www.youtube.com/watch?v=" + foundYoutubeVideoId; // Replace with actual URL
        String outputPath = spotifyDownloadDirPath + "\\" + foundYoutubeVideoId + "/" + foundYoutubeVideoId; // Output location and file format

        if (!YouTubeVideoInfoScraper.isValidYouTubeUrl(videoUrl)) {
            LOG.debug("Invalid YouTube Video URL: " + videoUrl);
            throw new Exception("Invalid YouTube Video URL: " + videoUrl);
        }

        if (foundYoutubeVideoId == null) {
            LOG.debug("spotify Video url Is: null");
            throw new Exception("spotify Video url Is: null");
        }

        if (
            !enableDublicated &&
            trackRepository.findByRefIdAndRefTypeAndOwnerIdAndPlaylistsIn(spotifyUrl, "SPOTIFY", ownerId, List.of(playlistId)).isPresent()
        ) {
            LOG.debug("spotify Video already exists: " + spotifyUrl);
            taskService.sendClientMessage("spotify music already exists: " + spotifyUrl);
            throw new Exception("spotify Video already exists: " + spotifyUrl);
        } else {
            Track track;
            if (taskNode.getTrackId() == null || taskNode.getTrackId().isEmpty()) {
                track = trackService.createTrack(spotifyUrl, "SPOTIFY", playlistId, ownerId);
            } else {
                Optional<Track> trackOptional = trackRepository.findById(taskNode.getTrackId());
                track = trackOptional.orElseGet(() -> trackService.createTrack(spotifyUrl, "SPOTIFY", playlistId, ownerId));
            }

            try {
                if (taskNode != null) {
                    taskNode.setTrackId(track.getId());
                    taskNodeRepository.save(taskNode);
                }
                downloadYouTubeVideoAsMP3(videoUrl, foundYoutubeVideoId, outputPath, track);
                taskService.sendTaskNodes();
            } catch (Exception e) {
                trackService.delete(track.getId());
                throw e;
            }
        }
    }

    public String GrapeYoutubeVideoIdFromSpotifyUrl(String spotifyUrl) {
        System.setProperty("webdriver.chrome.driver", chromedriver); // Replace with your chromedriver path

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode (without a GUI)
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--incognito"); // Optional: use incognito mode
        options.addArguments(
            "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
        );
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("–-charset=UTF-8"); // Set the charset to UTF-8

        // Create WebDriver instance
        WebDriver driver = new ChromeDriver(options);
        boolean videoFound = false;
        String videoId = "";
        String videoTitle = "";
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
            driver.get(spotifyUrl);

            WebElement trackName = driver.findElement(By.cssSelector("span[data-testid=\"entityTitle\"]"));
            WebElement trackArtist = driver.findElement(By.cssSelector("a[data-testid=\"creator-link\"]"));

            String tracktitle = trackName.getText();
            String artisttitle = trackArtist.getText();

            driver.get("https://www.youtube.com/");
            WebElement searchBox = driver.findElement(By.name("search_query"));
            searchBox.sendKeys(tracktitle + " " + artisttitle);
            searchBox.submit();

            // Wait for results to load
            try {
                Thread.sleep(3000); // 3-second delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Find the first non-ad result
            List<WebElement> results = driver.findElements(By.cssSelector("ytd-video-renderer"));
            WebElement firstResult = null;
            for (WebElement result : results) {
                if (result.findElements(By.cssSelector("ytd-ad-slot-renderer")).isEmpty()) {
                    firstResult = result;
                    break;
                }
            }

            if (firstResult != null) {
                WebElement titleElement = firstResult.findElement(By.id("video-title"));
                videoTitle = titleElement.getText();
                String videoUrl = titleElement.getAttribute("href");

                videoId = extractYouTubeVideoId(videoUrl);

                videoFound = videoId != null;
            }
            driver.quit();
            if (videoFound) {
                return videoId;
            } else {
                return null;
            }
        } catch (Exception e) {
            LOG.debug(e.getMessage());
            return null;
        }
    }

    private static final String YOUTUBE_URL_REGEX =
        "^(?:https?:\\/\\/)?(?:www\\.|m\\.)?(?:youtube\\.com\\/watch\\?v=|youtu.be\\/)([a-zA-Z0-9_-]{11})";
    private static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile(YOUTUBE_URL_REGEX);
    private static final String TRACK_REGEX = "^https://open\\.spotify\\.com/track/([a-zA-Z0-9]+)$";
    private static final String PLAYLIST_REGEX = "^https://open\\.spotify\\.com/playlist/([a-zA-Z0-9]+)$";

    public static boolean isSpotifyLink(String url) {
        return url.matches(TRACK_REGEX) || url.matches(PLAYLIST_REGEX);
    }

    public static String getSpotifyLinkType(String url) {
        if (url.contains("track")) {
            return "Track";
        } else if (url.contains("playlist")) {
            return "Playlist";
        }
        return "Invalid";
    }

    public static String extractYouTubeVideoId(String url) {
        Matcher matcher = YOUTUBE_URL_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1); // Extracts the video ID
        } else {
            return null; // Not a valid YouTube URL
        }
    }

    private static String extractVideoId(String url) {
        String regex = "[?&]v=([^&]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1); // Return the captured group
        }
        return null; // Return null if no match is found
    }
}
