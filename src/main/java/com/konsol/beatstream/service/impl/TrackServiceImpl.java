package com.konsol.beatstream.service.impl;

import static com.konsol.beatstream.service.bucket.BucketManager.rootPath;

import com.konsol.beatstream.domain.BeatStreamFile;
import com.konsol.beatstream.domain.Playlist;
import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.domain.User;
import com.konsol.beatstream.repository.BeatStreamFileRepository;
import com.konsol.beatstream.repository.TrackRepository;
import com.konsol.beatstream.service.BeatStreamFileService;
import com.konsol.beatstream.service.PlaylistService;
import com.konsol.beatstream.service.TrackService;
import com.konsol.beatstream.service.UserService;
import com.konsol.beatstream.service.dto.TrackDTO;
import com.konsol.beatstream.service.mapper.TrackMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing {@link com.konsol.beatstream.domain.Track}.
 */
@Service
public class TrackServiceImpl implements TrackService {

    private static final Logger LOG = LoggerFactory.getLogger(TrackServiceImpl.class);

    private final TrackRepository trackRepository;

    private final TrackMapper trackMapper;

    private final BeatStreamFileService beatStreamFileService;

    private final UserService userService;

    private final PlaylistService playlistService;

    private final BeatStreamFileRepository beatStreamFileRepository;

    public TrackServiceImpl(
        TrackRepository trackRepository,
        TrackMapper trackMapper,
        BeatStreamFileService beatStreamFileService,
        UserService userService,
        PlaylistService playlistService,
        BeatStreamFileRepository beatStreamFileRepository
    ) {
        this.trackRepository = trackRepository;
        this.trackMapper = trackMapper;
        this.beatStreamFileService = beatStreamFileService;
        this.userService = userService;
        this.playlistService = playlistService;
        this.beatStreamFileRepository = beatStreamFileRepository;
    }

    @Override
    public TrackDTO save(TrackDTO trackDTO) {
        LOG.debug("Request to save Track : {}", trackDTO);
        Track track = trackMapper.toEntity(trackDTO);
        track = trackRepository.save(track);
        return trackMapper.toDto(track);
    }

    @Override
    public TrackDTO update(TrackDTO trackDTO) {
        LOG.debug("Request to update Track : {}", trackDTO);
        Track track = trackMapper.toEntity(trackDTO);
        track = trackRepository.save(track);
        return trackMapper.toDto(track);
    }

    @Override
    public Optional<TrackDTO> partialUpdate(TrackDTO trackDTO) {
        LOG.debug("Request to partially update Track : {}", trackDTO);

        return trackRepository
            .findById(trackDTO.getId())
            .map(existingTrack -> {
                trackMapper.partialUpdate(existingTrack, trackDTO);

                return existingTrack;
            })
            .map(trackRepository::save)
            .map(trackMapper::toDto);
    }

    @Override
    public Page<TrackDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Tracks");
        return trackRepository.findAll(pageable).map(trackMapper::toDto);
    }

    @Override
    public Optional<TrackDTO> findOne(String id) {
        LOG.debug("Request to get Track : {}", id);
        return trackRepository.findById(id).map(trackMapper::toDto);
    }

    @Override
    public Optional<Track> findOneDomain(String id) {
        return trackRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        Track track = trackRepository.findById(id).orElse(null);

        if (track == null) {
            return;
        }

        //delete the file if available
        if (track.getAudioFileId() != null && !Objects.equals(track.getAudioFileId(), "")) {
            Optional<BeatStreamFile> beatStreamFileDTO = beatStreamFileService.findOneDomain(track.getAudioFileId());

            if (beatStreamFileDTO.isPresent()) {
                File file = new File(rootPath.resolve(track.getOwnerId() + "\\" + "audioFiles" + "\\" + track.getAudioFileId()).toUri());
                file.delete();
                beatStreamFileService.delete(track.getAudioFileId());

                beatStreamFileService.delete(track.getAudioFileId());
            }
        }
        //TODO delete image file

        for (Playlist playlist : track.getPlaylists()) {
            playlist.getTracks().remove(track);
            playlistService.save(playlist);
        }
        LOG.debug("Request to delete Track : {}", id);
        trackRepository.deleteById(id);
    }

    @Override
    public boolean validateMp3Track(MultipartFile file) {
        if (file.isEmpty()) {
            LOG.error("file.isEmpty()");
            return false;
        }

        // MIME type check
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("audio/mpeg")) {
            LOG.error("contentType");
            return false;
        }

        // Proceed with file handling if the file passes all checks
        // file.transferTo(new File("path/to/save/" + fileName));

        return true;
    }

    @Override
    public Track extractTrackMetadata(MultipartFile file) {
        Track track = new Track();
        // Save the uploaded file temporarily to read the metadata
        File tempFile = null;
        try {
            tempFile = File.createTempFile("uploaded-", ".mp3");
            file.transferTo(tempFile);

            try {
                // Read the audio file and its metadata using jaudiotagger
                AudioFile audioFile = AudioFileIO.read(tempFile);
                Tag tag = audioFile.getTag();

                // Extract metadata
                String artist = tag.getFirst(FieldKey.ARTIST);
                String title = tag.getFirst(FieldKey.TITLE);
                String album = tag.getFirst(FieldKey.ALBUM);
                String genre = tag.getFirst(FieldKey.GENRE);
                String year = tag.getFirst(FieldKey.YEAR);

                AudioHeader audioHeader = audioFile.getAudioHeader();
                track.setTitle(title);
                track.setDuration((long) audioHeader.getTrackLength());

                return track;
            } catch (Exception e) {
                tempFile.delete();
            } finally {
                // Delete the temporary file
                tempFile.delete();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new Track();
    }

    @Override
    public Track extractTrackCover(MultipartFile file) {
        File tempFile = null;
        try {
            // Save the uploaded file temporarily to read the metadata
            tempFile = File.createTempFile("uploaded-", ".mp3");
            file.transferTo(tempFile);
            // Read the audio file and its metadata using jaudiotagger
            AudioFile audioFile = AudioFileIO.read(tempFile);
            Tag tag = audioFile.getTag();
            AudioHeader audioHeader = audioFile.getAudioHeader();

            // Extract cover art if available
            List<Artwork> artworkList = tag.getArtworkList();
            if (!artworkList.isEmpty()) {
                Artwork artwork = artworkList.get(0); // Get the first artwork (cover image)
                byte[] imageData = artwork.getBinaryData(); // Get image data

                // Save the image to a file (e.g., "cover.jpg")
                File imageFile = new File("cover.jpg");
                try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                    fos.write(imageData);
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        } finally {
            // Delete the temporary file
            if (tempFile != null) {
                tempFile.delete();
            }
        }
        return null;
    }

    @Override
    public Track createTrack(String name, MultipartFile audioFile, MultipartFile cover, String playlistId) {
        if (!validateMp3Track(audioFile)) {
            throw new IllegalArgumentException("Mp3 file is not valid");
        }

        if (playlistId == null) {
            throw new RuntimeException("Play list Not found");
        }

        Optional<Playlist> playlist = playlistService.findOneDomain(playlistId);

        if (playlist.isEmpty()) {
            throw new RuntimeException("Playlist Not found");
        }

        User user = userService.getCurrentUser();
        BeatStreamFile beatStreamAudioFile = beatStreamFileService.uploadAudioFile(audioFile);
        Track track = extractTrackMetadata(audioFile);
        if (Objects.equals(track.getTitle(), "")) {
            track.title(name);
        }

        if (track.getPlaylists() == null) {
            track.setPlaylists(new LinkedHashSet<>());
        }
        track.getPlaylists().add(playlist.get());
        track.setOwnerId(user.getId());
        track.audioFileId(beatStreamAudioFile.getId());

        track = this.trackRepository.save(track);

        Playlist playlist1 = playlist.get();
        playlist1.getTracks().add(track);

        playlistService.save(playlist1);

        return this.trackRepository.save(track);
    }

    @Override
    public Track createTrack(String refId, String refType, String playlistId) {
        if (playlistId == null) {
            throw new RuntimeException("Play list Not found");
        }

        Optional<Playlist> playlist = playlistService.findOneDomain(playlistId);

        if (playlist.isEmpty()) {
            throw new RuntimeException("Playlist Not found");
        }

        //  User user = userService.getCurrentUser();

        Track track = new Track();

        track.setRefId(refId);
        track.setRefType(refType);

        if (track.getPlaylists() == null) {
            track.setPlaylists(new LinkedHashSet<>());
        }
        track.getPlaylists().add(playlist.get());
        track.setOwnerId("user-1");

        Playlist playlist1 = playlist.get();
        playlist1.getTracks().add(track);
        track.setTitle(refId);
        track = this.trackRepository.save(track);
        playlistService.save(playlist1);

        return this.trackRepository.save(track);
    }

    @Override
    public Track connectTrackToAudioFile(String trackId, String filePth) {
        File file = new File(filePth);

        if (file.exists()) {
            Track track = trackRepository.findById(trackId).orElse(null);

            if (track == null) {
                throw new RuntimeException("track does not exist");
            }
            BeatStreamFile beatStreamFile = new BeatStreamFile();
            //User user = userService.getCurrentUser();
            Path bucketPath = rootPath.resolve("user-1" + "\\" + "audioFiles");
            beatStreamFile.bucket("user-1" + "\\" + "audioFiles");
            beatStreamFile.name(file.getName());
            beatStreamFile.setSize(file.length());
            beatStreamFile.setType("audio");

            beatStreamFile = beatStreamFileRepository.save(beatStreamFile);
            beatStreamFile.setFullPath(bucketPath + "\\" + beatStreamFile.getId());

            beatStreamFile = beatStreamFileRepository.save(beatStreamFile);

            Path source = Paths.get(filePth);
            Path destination = Paths.get(bucketPath + "\\" + beatStreamFile.getId());

            try {
                Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            track.setAudioFileId(beatStreamFile.getId());
            return trackRepository.save(track);
        } else {
            throw new RuntimeException("File does not exist");
        }
    }

    @Override
    public com.konsol.beatstream.service.api.dto.Track TrackIntoTrackDTO(com.konsol.beatstream.domain.Track track) {
        com.konsol.beatstream.service.api.dto.Track track1 = new com.konsol.beatstream.service.api.dto.Track();
        track1.setId(track.getId());
        track1.title(track.getTitle());
        track1.playCount(BigDecimal.valueOf(track.getPlayCount()));
        track1.duration(new BigDecimal(track.getDuration()));
        track1.setPlaylists(track.getPlaylists().stream().map(Playlist::getId).toList());

        return track1;
    }
}
