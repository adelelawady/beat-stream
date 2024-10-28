package com.konsol.beatstream.service.audioPlugins.Spotify;

import static com.konsol.beatstream.service.bucket.BucketManager.rootPath;

import com.konsol.beatstream.domain.TaskNode;
import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.domain.enumeration.DownloadStatus;
import com.konsol.beatstream.repository.TaskNodeRepository;
import com.konsol.beatstream.repository.TrackRepository;
import com.konsol.beatstream.service.TrackService;
import com.konsol.beatstream.service.bucket.BucketManager;
import com.konsol.beatstream.web.websocket.TaskService;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpotifyDownloader {

    String startupPath = Paths.get("").toAbsolutePath().toString();

    private TaskNode taskNode = null;
    // Path to the yt-dlp.exe file
    private final String YTDLP_PATH = startupPath + "\\plugins\\yt-dlp.exe";
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

    private void downloadSong(String songUrl, Track track, TaskNode _taskNode, String ownerId) throws Exception {
        taskNode = _taskNode;
        BucketManager manager = new BucketManager();

        try {
            manager.createBucket("spotifyDL");
            manager.createBucket(ownerId);
            manager.createBucket(ownerId + "\\" + "audioFiles");
        } catch (IOException e) {
            LOG.debug(e.getMessage());
            throw new Exception(e);
        }
        Path spotifyDL = rootPath.resolve("spotifyDL");

        Pattern genericPattern = Pattern.compile("[a-zA-Z0-9@.]+");

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                YTDLP_PATH,
                "-o",
                spotifyDL + "\\" + track.getId() + "/" + track.getId(), // Output format
                songUrl
            );

            String outputPath = spotifyDL + "\\" + track.getId(); // Output location and file format

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read output from the process
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
            }

            int exitCode = process.waitFor(); // Wait for the process to complete
            if (exitCode == 0) {
                taskNode.setStatus(DownloadStatus.CONVERSION);
                taskNodeRepository.save(taskNode);
                taskService.sendTaskNodes();
                try {
                    Path outputPath1 = Path.of(outputPath);

                    Track track1 = extrackAndSaveTrackData(track, outputPath1, songUrl);
                    taskService.sendTaskNodes();
                    moveAndConnectDownloadedFile(track1, outputPath1);
                    File file = new File(outputPath);
                    file.delete();
                    taskService.sendTaskNodes();
                } catch (Exception e) {
                    LOG.debug("YouTube Video already exists: " + songUrl);
                    trackService.delete(track.getId());
                }

                LOG.debug("Download complete!");
            } else {
                System.out.println("Download failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred: " + e.getMessage());
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
                title = getSongTitle(videoUrl);

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

    public String getSongTitle(String spotifyUrl) {
        String songTitle = null;

        try {
            // Connect to the Spotify page
            Document document = Jsoup.connect(spotifyUrl).get();

            // Select the HTML element that contains the song title
            // Spotify uses <h1> for titles, usually the first h1 will be the title
            Element titleElement = document.select("h1").first();
            if (titleElement != null) {
                songTitle = titleElement.text();
            }
        } catch (IOException e) {
            System.err.println("Error fetching the song title: " + e.getMessage());
        }

        return songTitle;
    }

    public void AddSpotifySong(String video_Id, String playlistId, TaskNode _taskNode, String ownerId) throws Exception {
        taskNode = _taskNode;
        BucketManager manager = new BucketManager();

        try {
            manager.createBucket("spotifyDL");
            manager.createBucket(ownerId);
            manager.createBucket(ownerId + "\\" + "audioFiles");
        } catch (IOException e) {
            LOG.debug(e.getMessage());
            throw new Exception(e);
        }

        if (video_Id == null) {
            LOG.debug("Spotify Video Id Is: null");
            throw new Exception("Spotify Video Id Is: null");
        }

        if (trackRepository.findByRefIdAndRefTypeAndOwnerIdAndPlaylistsIn(video_Id, "SPOTIFY", ownerId, List.of(playlistId)).isPresent()) {
            LOG.debug("Spotify Video already exists: " + video_Id);
            throw new Exception("Spotify Video already exists: " + video_Id);
        } else {
            Track track = trackService.createTrack(video_Id, "SPOTIFY", playlistId, ownerId);
            try {
                if (taskNode != null) {
                    taskNode.setTrackId(track.getId());
                    taskNodeRepository.save(taskNode);
                }
                downloadSong(video_Id, track, taskNode, ownerId);
                taskService.sendTaskNodes();
            } catch (Exception e) {
                trackService.delete(track.getId());
                throw e;
            }
        }
    }
}
