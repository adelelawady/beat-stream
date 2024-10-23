package com.konsol.beatstream.service.audioPlugins.SoundCloud;

import static com.konsol.beatstream.service.bucket.BucketManager.rootPath;

import com.konsol.beatstream.domain.ReferanceDownloadTask;
import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.domain.User;
import com.konsol.beatstream.repository.ReferanceDownloadTaskRepository;
import com.konsol.beatstream.repository.TrackRepository;
import com.konsol.beatstream.service.TrackService;
import com.konsol.beatstream.service.UserService;
import com.konsol.beatstream.service.audioPlugins.ReferanceTrack.ReferanceTrackHandler;
import com.konsol.beatstream.service.audioPlugins.youtube.YouTubeVideoInfoScraper;
import com.konsol.beatstream.service.bucket.BucketManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
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
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SoundCloudDownloader {

    @Autowired
    TrackService trackService;

    @Autowired
    TrackRepository trackRepository;

    @Autowired
    ReferanceTrackHandler referanceTrackHandler;

    ReferanceDownloadTask referanceDownloadTask;

    @Autowired
    UserService userService;

    @Autowired
    ReferanceDownloadTaskRepository referanceDownloadTaskRepository;

    // Path to the yt-dlp.exe file
    private final String YTDLP_PATH = "C:\\Users\\adel\\KONSOL\\yt-dlp.exe";
    private final String FFMPEG_PATH = "C:\\Users\\adel\\KONSOL\\ffmpeg.exe";

    // Method to download a song from SoundCloud using yt-dlp.exe
    public void downloadSong(String soundCloudUrl, Track track, String playList) throws IOException {
        // Ensure the yt-dlp.exe exists
        File ytDlpFile = new File(YTDLP_PATH);
        if (!ytDlpFile.exists()) {
            throw new IOException("yt-dlp.exe not found at " + YTDLP_PATH);
        }

        User user = userService.getCurrentUser();

        BucketManager manager = new BucketManager();

        try {
            manager.createBucket("soundCloudDl");
            manager.createBucket(user.getId());
            manager.createBucket(user.getId() + "\\" + "audioFiles");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Path youtubeDownloadDirPath = rootPath.resolve("soundCloudDl");
        String outputPath = youtubeDownloadDirPath + "\\" + track.getId(); // Output location and file format

        // Construct the yt-dlp.exe command
        String[] command = { YTDLP_PATH, "-o", outputPath, soundCloudUrl };

        // Execute the yt-dlp.exe command
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // Combine stdout and stderr

        // Start the process
        Process process = processBuilder.start();
        referanceDownloadTask.setReferanceStatus("ProcessStarted");
        referanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);

        // Capture the process output (download progress or errors)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            referanceDownloadTask.setReferanceStatus("Downloading");
            referanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Output download progress or errors
                referanceDownloadTask.setReferanceLog(referanceDownloadTask.getReferanceLog() + "\n" + line);
                referanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);
            }
        }

        // Wait for the process to finish and check the exit code
        try {
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                referanceDownloadTask.setReferanceStatus("ExtractingAudio");
                referanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);

                Path outputPath1 = Path.of(outputPath);

                Path outputPath2 = Path.of(outputPath1.toString() + "_Converted.mp3");
                convertOpusToMp3(outputPath1.toString(), outputPath1.toString() + "_Converted.mp3");

                referanceDownloadTask.setReferanceStatus("SavingFile");
                referanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);
                Track track1 = extrackAndSaveTrackData(track, outputPath2, soundCloudUrl);

                moveAndConnectDownloadedFile(track1, outputPath2);
                File file = new File(outputPath);
                file.delete();
                referanceDownloadTask.setReferanceStatus("AddingAudioToPlayList");
                referanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);

                referanceDownloadTask.setReferanceStatus("DownloadComplete");
                referanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);
                System.out.println("Download complete!");
            } else {
                System.out.println("Download failed with exit code: " + exitCode);
            }
        } catch (Exception e) {
            trackService.delete(track.getId());
        }
    }

    public void moveAndConnectDownloadedFile(Track track, Path outputPath) {
        try {
            trackService.connectTrackToAudioFile(track.getId(), outputPath.toString());
        } catch (Exception e) {
            throw e;
        }
    }

    public static String[] extractUserAndSongFromUrl(String url) {
        // Regex to match the SoundCloud URL pattern
        String regex = "https?://soundcloud\\.com/([^/]+)/([^/]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            String userName = matcher.group(1); // First capturing group (user/artist name)
            String songName = matcher.group(2); // Second capturing group (song name)
            return new String[] { userName, songName };
        } else {
            return new String[] { "User not found", "Song not found" };
        }
    }

    public void addSoundCloudLink(String soundCloudId, String playList) {
        String[] soundmedtaData = extractUserAndSongFromUrl(soundCloudId);
        String soundUniqCloudId = soundmedtaData[0] + "-" + soundmedtaData[1];

        User user = userService.getCurrentUser();
        if (trackRepository.findByRefIdAndRefTypeAndOwnerId(soundUniqCloudId, "SOUNDCLOUD", user.getId()).isPresent()) {
            throw new RuntimeException("YouTube Video already exists: " + soundCloudId);
        }

        Track track = trackService.createTrack(soundUniqCloudId, "SOUNDCLOUD", playList);

        referanceDownloadTask = referanceTrackHandler.createDownloadTask(soundUniqCloudId, "SOUNDCLOUD", track.getId());

        try {
            downloadSong(soundCloudId, track, playList);
        } catch (Exception e) {
            trackService.delete(track.getId());
        }
    }

    // Method to extract the hash from a URL and convert it to a number
    public static String getHashFromUrlAsNumber(String url) {
        // Find the position of the '#' symbol
        int hashIndex = url.indexOf('#');

        // If no hash exists, return 0
        if (hashIndex == -1) {
            return "0";
        }

        // Extract the part after the '#'
        String hash = url.substring(hashIndex + 1);

        // Simple hash function: sum of character codes
        int hashValue = 0;
        for (int i = 0; i < hash.length(); i++) {
            hashValue += hash.charAt(i);
        }

        return hashValue + "";
    }

    public Track extrackAndSaveTrackData(Track track, Path outputPath, String videoUrl) {
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
                title = getSongNameFromUrl(videoUrl);
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

    /**
     * Converts an Opus file to MP3 format using FFmpeg.
     *
     * @param inputFilePath  The path to the input Opus file.
     * @param outputFilePath The path to the output MP3 file.
     * @throws IOException If an I/O error occurs during the process.
     * @throws InterruptedException If the conversion process is interrupted.
     */
    public void convertOpusToMp3(String inputFilePath, String outputFilePath) throws IOException, InterruptedException {
        // Prepare the FFmpeg command
        ProcessBuilder processBuilder = new ProcessBuilder(
            FFMPEG_PATH,
            "-i",
            inputFilePath,
            "-codec:a",
            "libmp3lame",
            "-qscale:a",
            "2",
            outputFilePath
        );

        // Redirect error stream to standard output
        processBuilder.redirectErrorStream(true);

        // Start the conversion process
        Process process = processBuilder.start();

        // Read the output from the process
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Print the output for debugging
            }
        }

        // Wait for the process to complete and check the exit value
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("FFmpeg process exited with code: " + exitCode);
        }

        System.out.println("Conversion completed successfully!");
    }

    /**
     * Extracts the song name from a given SoundCloud URL.
     *
     * @param url The SoundCloud URL of the song.
     * @return The name of the song, or an error message if the song cannot be found.
     * @throws IOException If an I/O error occurs during fetching the URL.
     */
    public static String getSongNameFromUrl(String url) throws IOException {
        // Fetch the document from the URL
        Document document = Jsoup.connect(url).get();

        // Use a CSS selector to find the song title in the HTML
        Elements titleElements = document.select("meta[property='og:title']");

        if (!titleElements.isEmpty()) {
            Element titleElement = titleElements.first();
            return titleElement.attr("content"); // Extract the content attribute
        }

        return "Song title not found.";
    }
}
