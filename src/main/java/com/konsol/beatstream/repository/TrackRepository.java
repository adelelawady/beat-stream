package com.konsol.beatstream.repository;

import com.konsol.beatstream.domain.Playlist;
import com.konsol.beatstream.domain.Track;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Track entity.
 */
@Repository
public interface TrackRepository extends MongoRepository<Track, String> {
    Optional<Track> findByRefIdAndRefTypeAndOwnerIdAndPlaylistsIn(String refId, String refType, String ownerId, List<String> playlists);
}
