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
 * A Album.
 */
@Document(collection = "album")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("title")
    private String title;

    @Field("release_date")
    private String releaseDate;

    @Field("cover_image_file_id")
    private String coverImageFileId;

    @DBRef
    @Field("artist")
    @JsonIgnoreProperties(value = { "genres" }, allowSetters = true)
    private Artist artist;

    @DBRef
    @Field("genres")
    @JsonIgnoreProperties(value = { "artists", "albums" }, allowSetters = true)
    private Set<Genre> genres = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Album id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Album title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public Album releaseDate(String releaseDate) {
        this.setReleaseDate(releaseDate);
        return this;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getCoverImageFileId() {
        return this.coverImageFileId;
    }

    public Album coverImageFileId(String coverImageFileId) {
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

    public Album artist(Artist artist) {
        this.setArtist(artist);
        return this;
    }

    public Set<Genre> getGenres() {
        return this.genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Album genres(Set<Genre> genres) {
        this.setGenres(genres);
        return this;
    }

    public Album addGenre(Genre genre) {
        this.genres.add(genre);
        return this;
    }

    public Album removeGenre(Genre genre) {
        this.genres.remove(genre);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Album)) {
            return false;
        }
        return getId() != null && getId().equals(((Album) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Album{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", releaseDate='" + getReleaseDate() + "'" +
            ", coverImageFileId='" + getCoverImageFileId() + "'" +
            "}";
    }
}
