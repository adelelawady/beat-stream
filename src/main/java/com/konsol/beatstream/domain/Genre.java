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
 * A Genre.
 */
@Document(collection = "genre")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Genre implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("name")
    private String name;

    @DBRef
    @Field("artists")
    @JsonIgnoreProperties(value = { "genres" }, allowSetters = true)
    private Set<Artist> artists = new HashSet<>();

    @DBRef
    @Field("albums")
    @JsonIgnoreProperties(value = { "artist", "genres" }, allowSetters = true)
    private Set<Album> albums = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Genre id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Genre name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Artist> getArtists() {
        return this.artists;
    }

    public void setArtists(Set<Artist> artists) {
        this.artists = artists;
    }

    public Genre artists(Set<Artist> artists) {
        this.setArtists(artists);
        return this;
    }

    public Genre addArtist(Artist artist) {
        this.artists.add(artist);
        return this;
    }

    public Genre removeArtist(Artist artist) {
        this.artists.remove(artist);
        return this;
    }

    public Set<Album> getAlbums() {
        return this.albums;
    }

    public void setAlbums(Set<Album> albums) {
        if (this.albums != null) {
            this.albums.forEach(i -> i.removeGenre(this));
        }
        if (albums != null) {
            albums.forEach(i -> i.addGenre(this));
        }
        this.albums = albums;
    }

    public Genre albums(Set<Album> albums) {
        this.setAlbums(albums);
        return this;
    }

    public Genre addAlbum(Album album) {
        this.albums.add(album);
        album.getGenres().add(this);
        return this;
    }

    public Genre removeAlbum(Album album) {
        this.albums.remove(album);
        album.getGenres().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Genre)) {
            return false;
        }
        return getId() != null && getId().equals(((Genre) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Genre{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
