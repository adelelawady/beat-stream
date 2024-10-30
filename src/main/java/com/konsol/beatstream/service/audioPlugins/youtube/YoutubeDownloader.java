package com.konsol.beatstream.service.audioPlugins.youtube;

import static com.konsol.beatstream.service.bucket.BucketManager.rootPath;

import com.konsol.beatstream.config.AppSettingsConfiguration;
import com.konsol.beatstream.domain.TaskNode;
import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.domain.User;
import com.konsol.beatstream.domain.enumeration.DownloadStatus;
import com.konsol.beatstream.repository.TaskNodeRepository;
import com.konsol.beatstream.repository.TrackRepository;
import com.konsol.beatstream.service.TrackService;
import com.konsol.beatstream.service.UserService;
import com.konsol.beatstream.service.audioPlugins.ReferanceTrack.ReferanceTrackHandler;
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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YoutubeDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(YoutubeDownloader.class);

    @Autowired
    TrackService trackService;

    @Autowired
    TrackRepository trackRepository;

    @Autowired
    ReferanceTrackHandler referanceTrackHandler;

    String videoId;

    @Autowired
    UserService userService;

    @Autowired
    TaskService taskService;

    @Autowired
    TaskNodeRepository taskNodeRepository;

    String startupPath = Paths.get("").toAbsolutePath().toString();

    private TaskNode taskNode = null;
    // Path to the yt-dlp.exe file
    private String YTDLP_PATH;
    private String FFMPEG_PATH;
    private boolean enableDublicated = false;

    public YoutubeDownloader() {
        try {
            YTDLP_PATH = AppSettingsConfiguration.getSettings()
                .getProperty("beatstream.settings.plugins.yt-dl_path", startupPath + "\\plugins\\yt-dlp.exe");
            FFMPEG_PATH = AppSettingsConfiguration.getSettings()
                .getProperty("beatstream.settings.plugins.ffmpeg_path", startupPath + "\\plugins\\ffmpeg.exe");
            enableDublicated = Boolean.parseBoolean(
                AppSettingsConfiguration.getSettings().getProperty("beatstream.settings.youtube.duplicated.enabled", "false")
            );
        } catch (Exception e) {
            enableDublicated = false;
            YTDLP_PATH = startupPath + "\\plugins\\yt-dlp.exe";
            FFMPEG_PATH = startupPath + "\\plugins\\ffmpeg.exe";
        }
    }

    public void AddYoutubeVideo(String video_Id, String playlistId, TaskNode _taskNode, String ownerId) throws Exception {
        this.videoId = video_Id;
        taskNode = _taskNode;
        //User user = userService.getCurrentUser();
        BucketManager manager = new BucketManager();

        try {
            manager.createBucket("youTubeDL");
            manager.createBucket(ownerId);
            manager.createBucket(ownerId + "\\" + "audioFiles");
        } catch (IOException e) {
            LOG.debug(e.getMessage());
            throw new Exception(e);
        }
        Path youtubeDownloadDirPath = rootPath.resolve("youTubeDL");
        String videoUrl = "https://www.youtube.com/watch?v=" + videoId; // Replace with actual URL
        String outputPath = youtubeDownloadDirPath + "\\" + videoId + "/" + videoId; // Output location and file format

        if (!YouTubeVideoInfoScraper.isValidYouTubeUrl(videoUrl)) {
            LOG.debug("Invalid YouTube Video URL: " + videoUrl);
            throw new Exception("Invalid YouTube Video URL: " + videoUrl);
        }

        if (videoId == null) {
            LOG.debug("YouTube Video Id Is: null");
            throw new Exception("YouTube Video Id Is: null");
        }

        if (
            !enableDublicated &&
            trackRepository.findByRefIdAndRefTypeAndOwnerIdAndPlaylistsIn(videoId, "YOUTUBE", ownerId, List.of(playlistId)).isPresent()
        ) {
            LOG.debug("YouTube Video already exists: " + videoId);
            taskService.sendClientMessage("Youtube music already exists: " + videoId);
            throw new Exception("YouTube Video already exists: " + videoId);
        } else {
            Track track;
            if (taskNode.getTrackId() == null || taskNode.getTrackId().isEmpty()) {
                track = trackService.createTrack(videoId, "YOUTUBE", playlistId, ownerId);
            } else {
                Optional<Track> trackOptional = trackRepository.findById(taskNode.getTrackId());
                track = trackOptional.orElseGet(() -> trackService.createTrack(videoId, "YOUTUBE", playlistId, ownerId));
            }

            try {
                if (taskNode != null) {
                    taskNode.setTrackId(track.getId());
                    taskNodeRepository.save(taskNode);
                }
                downloadYouTubeVideoAsMP3(videoUrl, outputPath, track);
                taskService.sendTaskNodes();
            } catch (Exception e) {
                trackService.delete(track.getId());
                throw e;
            }
        }
    }

    public void downloadYouTubeVideoAsMP3(String videoUrl, String outputPath, Track track) throws IOException, InterruptedException {
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
                Path youtubeDownloadDirPath = rootPath.resolve("youTubeDL");
                String outputPathString = youtubeDownloadDirPath + "\\" + videoId + "\\" + videoId + ".mp3";
                Path outputPath1 = Path.of(outputPathString);

                Track track1 = extrackAndSaveTrackData(track, outputPath1, videoUrl);
                taskService.sendTaskNodes();
                moveAndConnectDownloadedFile(track1, outputPath1);
                File file = new File(outputPath);
                file.delete();
                taskService.sendTaskNodes();
            } catch (Exception e) {
                LOG.debug("YouTube Video already exists: " + videoId);
                trackService.delete(track.getId());
            }

            LOG.debug("Download complete!");
        } else {
            LOG.debug("Download failed. Exit code: " + exitCode);
        }
    }

    public void moveAndConnectDownloadedFile(Track track, Path outputPath) {
        try {
            trackService.connectTrackToAudioFile(track.getId(), outputPath.toString());
        } catch (Exception e) {
            LOG.debug(e.getMessage());
            throw e;
        }
    }

    public Track extrackAndSaveTrackData(Track track, Path outputPath, String videoUrl) throws Exception {
        if (!outputPath.toFile().exists()) {
            trackService.delete(track.getId());
            throw new Exception("File Not Found");
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
            throw new Exception(e);
        }
    }
}
