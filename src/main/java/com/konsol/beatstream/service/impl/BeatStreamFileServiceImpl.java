package com.konsol.beatstream.service.impl;

import static com.konsol.beatstream.service.bucket.BucketManager.rootPath;

import com.konsol.beatstream.domain.BeatStreamFile;
import com.konsol.beatstream.domain.User;
import com.konsol.beatstream.repository.BeatStreamFileRepository;
import com.konsol.beatstream.service.BeatStreamFileService;
import com.konsol.beatstream.service.UserService;
import com.konsol.beatstream.service.bucket.BucketManager;
import com.konsol.beatstream.service.dto.BeatStreamFileDTO;
import com.konsol.beatstream.service.mapper.BeatStreamFileMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing {@link com.konsol.beatstream.domain.BeatStreamFile}.
 */
@Service
public class BeatStreamFileServiceImpl implements BeatStreamFileService {

    private static final Logger LOG = LoggerFactory.getLogger(BeatStreamFileServiceImpl.class);

    private final BeatStreamFileRepository beatStreamFileRepository;

    private final BeatStreamFileMapper beatStreamFileMapper;

    private final BucketManager bucketManager;

    private final UserService userService;

    public BeatStreamFileServiceImpl(
        BeatStreamFileRepository beatStreamFileRepository,
        BeatStreamFileMapper beatStreamFileMapper,
        BucketManager bucketManager,
        UserService userService
    ) {
        this.beatStreamFileRepository = beatStreamFileRepository;
        this.beatStreamFileMapper = beatStreamFileMapper;
        this.bucketManager = bucketManager;
        this.userService = userService;
    }

    @Override
    public BeatStreamFileDTO save(BeatStreamFileDTO beatStreamFileDTO) {
        LOG.debug("Request to save BeatStreamFile : {}", beatStreamFileDTO);
        BeatStreamFile beatStreamFile = beatStreamFileMapper.toEntity(beatStreamFileDTO);
        beatStreamFile = beatStreamFileRepository.save(beatStreamFile);
        return beatStreamFileMapper.toDto(beatStreamFile);
    }

    @Override
    public BeatStreamFileDTO update(BeatStreamFileDTO beatStreamFileDTO) {
        LOG.debug("Request to update BeatStreamFile : {}", beatStreamFileDTO);
        BeatStreamFile beatStreamFile = beatStreamFileMapper.toEntity(beatStreamFileDTO);
        beatStreamFile = beatStreamFileRepository.save(beatStreamFile);
        return beatStreamFileMapper.toDto(beatStreamFile);
    }

    @Override
    public Optional<BeatStreamFileDTO> partialUpdate(BeatStreamFileDTO beatStreamFileDTO) {
        LOG.debug("Request to partially update BeatStreamFile : {}", beatStreamFileDTO);

        return beatStreamFileRepository
            .findById(beatStreamFileDTO.getId())
            .map(existingBeatStreamFile -> {
                beatStreamFileMapper.partialUpdate(existingBeatStreamFile, beatStreamFileDTO);

                return existingBeatStreamFile;
            })
            .map(beatStreamFileRepository::save)
            .map(beatStreamFileMapper::toDto);
    }

    @Override
    public List<BeatStreamFileDTO> findAll() {
        LOG.debug("Request to get all BeatStreamFiles");
        return beatStreamFileRepository
            .findAll()
            .stream()
            .map(beatStreamFileMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<BeatStreamFileDTO> findOne(String id) {
        LOG.debug("Request to get BeatStreamFile : {}", id);
        return beatStreamFileRepository.findById(id).map(beatStreamFileMapper::toDto);
    }

    @Override
    public Optional<BeatStreamFile> findOneDomain(String id) {
        return beatStreamFileRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete BeatStreamFile : {}", id);
        beatStreamFileRepository.deleteById(id);
    }

    @Override
    public BeatStreamFile uploadAudioFile(MultipartFile resource) {
        if (resource == null) {
            return null;
        }
        BeatStreamFile beatStreamFile = new BeatStreamFile();
        User user = userService.getCurrentUser();

        try {
            Path bucketPath = rootPath.resolve(user.getId() + "\\" + "audioFiles");
            bucketManager.createBucket(user.getId());
            bucketManager.createBucket(user.getId() + "\\" + "audioFiles");
            beatStreamFile.bucket(user.getId() + "\\" + "audioFiles");
            beatStreamFile.name(resource.getName());
            beatStreamFile.setSize(resource.getSize());
            beatStreamFile.setType("audio");
            beatStreamFile.setFullPath(bucketPath + "\\" + resource.getName() + "\\" + user.getId());
            beatStreamFile = beatStreamFileRepository.save(beatStreamFile);
            bucketManager.uploadFile(beatStreamFile.getId(), user.getId() + "\\" + "audioFiles", resource.getInputStream());

            return beatStreamFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
