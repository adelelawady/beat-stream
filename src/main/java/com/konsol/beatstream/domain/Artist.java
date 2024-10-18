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
 * A Artist.
 */
@Document(collection = "artist")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Artist implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("name")
    private String name;

    @Field("bio")
    private String bio;

    @Field("cover_image_file_id")
    private String coverImageFileId;

    @DBRef
    @Field("genres")
    @JsonIgnoreProperties(value = { "artists", "albums" }, allowSetters = true)
    private Set<Genre> genres = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Artist id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Artist name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return this.bio;
    }

    public Artist bio(String bio) {
        this.setBio(bio);
        return this;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCoverImageFileId() {
        return this.coverImageFileId;
    }

    public Artist coverImageFileId(String coverImageFileId) {
        this.setCoverImageFileId(coverImageFileId);
        return this;
    }

    public void setCoverImageFileId(String coverImageFileId) {
        this.coverImageFileId = coverImageFileId;
    }

    public Set<Genre> getGenres() {
        return this.genres;
    }

    public void setGenres(Set<Genre> genres) {
        if (this.genres != null) {
            this.genres.forEach(i -> i.removeArtist(this));
        }
        if (genres != null) {
            genres.forEach(i -> i.addArtist(this));
        }
        this.genres = genres;
    }

    public Artist genres(Set<Genre> genres) {
        this.setGenres(genres);
        return this;
    }

    public Artist addGenre(Genre genre) {
        this.genres.add(genre);
        genre.getArtists().add(this);
        return this;
    }

    public Artist removeGenre(Genre genre) {
        this.genres.remove(genre);
        genre.getArtists().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Artist)) {
            return false;
        }
        return getId() != null && getId().equals(((Artist) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Artist{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", bio='" + getBio() + "'" +
            ", coverImageFileId='" + getCoverImageFileId() + "'" +
            "}";
    }
}
