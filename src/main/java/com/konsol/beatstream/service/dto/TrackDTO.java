package com.konsol.beatstream.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.konsol.beatstream.domain.Track} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrackDTO implements Serializable {

    private String id;

    @NotNull
    private String title;

    private Long duration;

    private Boolean liked;

    private String audioFileId;

    private String coverImageFileId;

    private ArtistDTO artist;

    private AlbumDTO album;

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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public String getAudioFileId() {
        return audioFileId;
    }

    public void setAudioFileId(String audioFileId) {
        this.audioFileId = audioFileId;
    }

    public String getCoverImageFileId() {
        return coverImageFileId;
    }

    public void setCoverImageFileId(String coverImageFileId) {
        this.coverImageFileId = coverImageFileId;
    }

    public ArtistDTO getArtist() {
        return artist;
    }

    public void setArtist(ArtistDTO artist) {
        this.artist = artist;
    }

    public AlbumDTO getAlbum() {
        return album;
    }

    public void setAlbum(AlbumDTO album) {
        this.album = album;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrackDTO)) {
            return false;
        }

        TrackDTO trackDTO = (TrackDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trackDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrackDTO{" +
            "id='" + getId() + "'" +
            ", title='" + getTitle() + "'" +
            ", duration=" + getDuration() +
            ", liked='" + getLiked() + "'" +
            ", audioFileId='" + getAudioFileId() + "'" +
            ", coverImageFileId='" + getCoverImageFileId() + "'" +
            ", artist=" + getArtist() +
            ", album=" + getAlbum() +
            "}";
    }
}
