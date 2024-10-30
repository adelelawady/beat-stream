package com.konsol.beatstream.web.rest;

import static com.konsol.beatstream.service.bucket.BucketManager.rootPath;

import com.konsol.beatstream.domain.BeatStreamFile;
import com.konsol.beatstream.domain.Playlist;
import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.repository.PlaylistRepository;
import com.konsol.beatstream.repository.TrackRepository;
import com.konsol.beatstream.service.BeatStreamFileService;
import com.konsol.beatstream.service.TrackService;
import com.konsol.beatstream.service.UserService;
import com.konsol.beatstream.service.api.dto.TrackUpdate;
import com.konsol.beatstream.service.mapper.TrackMapper;
import com.konsol.beatstream.service.mapper.TrackMapperImpl;
import com.konsol.beatstream.web.api.TrackApiDelegate;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TrackApiResource implements TrackApiDelegate {

    @Autowired
    TrackService trackService;

    @Autowired
    BeatStreamFileService beatStreamFileService;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private TrackMapper trackMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Override
    public ResponseEntity<Resource> playTrack(String id, String rangeHeader) {
        Optional<Track> track = trackService.findOneDomain(id);

        if (track.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        Track trackCountUpdate = track.get();
        trackCountUpdate.setPlayCount(trackCountUpdate.getPlayCount() + 1);
        trackRepository.save(trackCountUpdate);

        Optional<BeatStreamFile> beatStreamFile = beatStreamFileService.findOneDomain(track.get().getAudioFileId());

        if (beatStreamFile.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        Path bucketPath = rootPath.resolve(track.get().getOwnerId() + "\\" + "audioFiles");
        // Path to the audio file
        Path filePath = Path.of(bucketPath + "\\" + track.get().getAudioFileId());

        if (!Files.exists(filePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // Get the content length (size of the file)
        long contentLength = 0;
        try {
            contentLength = Files.size(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // If no "Range" header is provided, send the entire file
        if (rangeHeader == null || rangeHeader.isEmpty()) {
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(contentLength)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + track.get().getAudioFileId() + ".mp3" + "\"")
                .body(resource);
        }

        // Parse the Range header to determine which part of the file to send
        List<HttpRange> httpRanges = HttpRange.parseRanges(rangeHeader);
        HttpRange httpRange = httpRanges.get(0);

        // Calculate the byte range for this request
        long start = httpRange.getRangeStart(contentLength);
        long end = httpRange.getRangeEnd(contentLength);
        long rangeLength = end - start + 1;

        // Return the partial content
        Resource finalResource = resource;
        long finalContentLength = contentLength;
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + track.get().getAudioFileId() + ".mp3" + "\"")
            .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + contentLength)
            .contentLength(rangeLength)
            .body(
                new Resource() {
                    @Override
                    public InputStream getInputStream() throws IOException {
                        RandomAccessFile randomAccessFile = new RandomAccessFile(filePath.toFile(), "r");
                        randomAccessFile.seek(start); // Move to the starting byte
                        return new InputStream() {
                            private long bytesRead = 0;

                            @Override
                            public int read() throws IOException {
                                if (bytesRead >= rangeLength) {
                                    return -1; // End of stream
                                }
                                bytesRead++;
                                return randomAccessFile.read(); // Read one byte at a time
                            }

                            @Override
                            public int read(byte[] b, int off, int len) throws IOException {
                                if (bytesRead >= rangeLength) {
                                    return -1; // End of stream
                                }
                                int bytesToRead = (int) Math.min(len, rangeLength - bytesRead);
                                int bytesReadNow = randomAccessFile.read(b, off, bytesToRead);
                                if (bytesReadNow > 0) {
                                    bytesRead += bytesReadNow;
                                }
                                return bytesReadNow;
                            }

                            @Override
                            public void close() throws IOException {
                                randomAccessFile.close(); // Ensure the file is closed when the stream is closed
                            }
                        };
                    }

                    @Override
                    public boolean exists() {
                        return true; // Resource exists
                    }

                    @Override
                    public URL getURL() throws IOException {
                        return null;
                    }

                    @Override
                    public URI getURI() throws IOException {
                        return null;
                    }

                    @Override
                    public File getFile() throws IOException {
                        return finalResource.getFile();
                    }

                    @Override
                    public long contentLength() throws IOException {
                        return finalContentLength;
                    }

                    @Override
                    public long lastModified() throws IOException {
                        return 0;
                    }

                    @Override
                    public Resource createRelative(String relativePath) throws IOException {
                        return null;
                    }

                    @Override
                    public String getFilename() {
                        return finalResource.getFilename();
                    }

                    @Override
                    public String getDescription() {
                        return "";
                    }
                    // Implement other necessary methods if needed...
                }
            );
    }

    @Override
    public ResponseEntity<com.konsol.beatstream.service.api.dto.Track> updateTrack(TrackUpdate trackUpdate) {
        Optional<Track> track = trackService.findOneDomain(trackUpdate.getId());

        if (track.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        if (!userService.getCurrentUser().getId().equals(track.get().getOwnerId())) {
            throw new RuntimeException("Must Be Updated By It's Owner Only");
        }

        Track trackCountUpdate = track.get();

        // update title
        if (trackUpdate.getTitle() != null) {
            trackCountUpdate.setTitle(trackUpdate.getTitle());
        }

        // update playlists
        if (trackUpdate.getPlaylists() != null && !trackUpdate.getPlaylists().isEmpty()) {
            trackUpdate
                .getPlaylists()
                .forEach(playlist -> {
                    Optional<Playlist> playlist1 = playlistRepository.findById(playlist);
                    if (playlist1.isPresent() && playlist1.get().getOwnerId().equals(userService.getCurrentUser().getId())) {
                        Playlist foundPlaylist = playlist1.get();
                        foundPlaylist.addTrack(trackCountUpdate);
                        playlistRepository.save(foundPlaylist);
                        trackCountUpdate.getPlaylists().add(foundPlaylist);
                    }
                });
        }

        return ResponseEntity.ok(trackMapper.TrackIntoTrackDTO(trackRepository.save(trackCountUpdate)));
    }
}
