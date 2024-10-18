package com.konsol.beatstream.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.konsol.beatstream.domain.Playlist} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlaylistDTO implements Serializable {

    private String id;

    @NotNull
    private String title;

    private String description;

    private Set<TrackDTO> tracks = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<TrackDTO> getTracks() {
        return tracks;
    }

    public void setTracks(Set<TrackDTO> tracks) {
        this.tracks = tracks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlaylistDTO)) {
            return false;
        }

        PlaylistDTO playlistDTO = (PlaylistDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, playlistDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlaylistDTO{" +
            "id='" + getId() + "'" +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", tracks=" + getTracks() +
            "}";
    }
}
