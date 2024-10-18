package com.konsol.beatstream.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Track.
 */
@Document(collection = "track")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Track implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("title")
    private String title;

    @Field("duration")
    private Long duration;

    @Field("liked")
    private Boolean liked;

    @Field("audio_file_id")
    private String audioFileId;

    @Field("cover_image_file_id")
    private String coverImageFileId;

    @DBRef
    @Field("artist")
    private Artist artist;

    @DBRef
    @Field("album")
    @JsonIgnoreProperties(value = { "artist", "genres" }, allowSetters = true)
    private Album album;

    @DBRef
    @Field("playlists")
    @JsonIgnoreProperties(value = { "tracks" }, allowSetters = true)
    private Set<Playlist> playlists = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Track id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Track title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDuration() {
        return this.duration;
    }

    public Track duration(Long duration) {
        this.setDuration(duration);
        return this;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Boolean getLiked() {
        return this.liked;
    }

    public Track liked(Boolean liked) {
        this.setLiked(liked);
        return this;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public String getAudioFileId() {
        return this.audioFileId;
    }

    public Track audioFileId(String audioFileId) {
        this.setAudioFileId(audioFileId);
        return this;
    }

    public void setAudioFileId(String audioFileId) {
        this.audioFileId = audioFileId;
    }

    public String getCoverImageFileId() {
        return this.coverImageFileId;
    }

    public Track coverImageFileId(String coverImageFileId) {
        this.setCoverImageFileId(coverImageFileId);
        return this;
    }

    public void setCoverImageFileId(String coverImageFileId) {
        this.coverImageFileId = coverImageFileId;
    }

    public Artist getArtist() {
        return this.artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Track artist(Artist artist) {
        this.setArtist(artist);
        return this;
    }

    public Album getAlbum() {
        return this.album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Track album(Album album) {
        this.setAlbum(album);
        return this;
    }

    public Set<Playlist> getPlaylists() {
        return this.playlists;
    }

    public void setPlaylists(Set<Playlist> playlists) {
        if (this.playlists != null) {
            this.playlists.forEach(i -> i.removeTrack(this));
        }
        if (playlists != null) {
            playlists.forEach(i -> i.addTrack(this));
        }
        this.playlists = playlists;
    }

    public Track playlists(Set<Playlist> playlists) {
        this.setPlaylists(playlists);
        return this;
    }

    public Track addPlaylist(Playlist playlist) {
        this.playlists.add(playlist);
        playlist.getTracks().add(this);
        return this;
    }

    public Track removePlaylist(Playlist playlist) {
        this.playlists.remove(playlist);
        playlist.getTracks().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Track)) {
            return false;
        }
        return getId() != null && getId().equals(((Track) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Track{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", duration=" + getDuration() +
            ", liked='" + getLiked() + "'" +
            ", audioFileId='" + getAudioFileId() + "'" +
            ", coverImageFileId='" + getCoverImageFileId() + "'" +
            "}";
    }
}
