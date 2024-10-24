package com.konsol.beatstream.service.audioPlugins.youtube;

import static com.konsol.beatstream.service.bucket.BucketManager.rootPath;

import com.konsol.beatstream.domain.ReferanceDownloadTask;
import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.domain.User;
import com.konsol.beatstream.repository.ReferanceDownloadTaskRepository;
import com.konsol.beatstream.repository.TrackRepository;
import com.konsol.beatstream.service.TrackService;
import com.konsol.beatstream.service.UserService;
import com.konsol.beatstream.service.audioPlugins.ReferanceTrack.ReferanceTrackHandler;
import com.konsol.beatstream.service.bucket.BucketManager;
import com.konsol.beatstream.service.impl.PlaylistServiceImpl;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    String videoId = "9OSVh-4Jovc";
    ReferanceDownloadTask referanceDownloadTask;

    @Autowired
    UserService userService;

    @Autowired
    ReferanceDownloadTaskRepository referanceDownloadTaskRepository;

    String startupPath = Paths.get("").toAbsolutePath().toString();

    // Path to the yt-dlp.exe file
    private final String YTDLP_PATH = startupPath + "\\plugins\\yt-dlp.exe";
    private final String FFMPEG_PATH = startupPath + "\\plugins\\ffmpeg.exe";

    public void AddYoutubeVideo(String video_Id, String playlistId) throws Exception {
        this.videoId = video_Id;

        User user = userService.getCurrentUser();
        BucketManager manager = new BucketManager();

        try {
            manager.createBucket("youTubeDL");
            manager.createBucket(user.getId());
            manager.createBucket(user.getId() + "\\" + "audioFiles");
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

        if (trackRepository.findByRefIdAndRefTypeAndOwnerId(videoId, "YOUTUBE", user.getId()).isPresent()) {
            LOG.debug("YouTube Video already exists: " + videoId);
            throw new Exception("YouTube Video already exists: " + videoId);
        } else {
            Track track = trackService.createTrack(videoId, "YOUTUBE", playlistId);

            referanceDownloadTask = referanceTrackHandler.createDownloadTask(video_Id, "YOUTUBE", track.getId());

            try {
                downloadYouTubeVideoAsMP3(videoUrl, outputPath, track);
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

        // Run the command
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        referanceDownloadTask.setReferanceStatus("ProcessStarted");
        referanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);
        // Capture the process output
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            referanceDownloadTask.setReferanceStatus("Downloading");
            referanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);
            while ((line = reader.readLine()) != null) {
                LOG.debug(line);
                referanceDownloadTask.setReferanceLog(referanceDownloadTask.getReferanceLog() + "\n" + line);
                referanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);
            }
        }

        // Wait for the process to finish
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            try {
                referanceDownloadTask.setReferanceStatus("ExtractingAudio");
                referanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);
                Path youtubeDownloadDirPath = rootPath.resolve("youTubeDL");
                String outputPathString = youtubeDownloadDirPath + "\\" + videoId + "\\" + videoId + ".mp3";
                Path outputPath1 = Path.of(outputPathString);
                referanceDownloadTask.setReferanceStatus("SavingFile");
                referanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);
                Track track1 = extrackAndSaveTrackData(track, outputPath1, videoUrl);
                moveAndConnectDownloadedFile(track1, outputPath1);
                File file = new File(outputPath);
                file.delete();
                referanceDownloadTask.setReferanceStatus("AddingAudioToPlayList");
                referanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);
            } catch (Exception e) {
                LOG.debug("YouTube Video already exists: " + videoId);
                trackService.delete(track.getId());
            }
            referanceDownloadTask.setReferanceStatus("DownloadComplete");
            referanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);
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
