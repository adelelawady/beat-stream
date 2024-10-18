package com.konsol.beatstream.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A ListeningHistory.
 */
@Document(collection = "listening_history")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ListeningHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("timestamp")
    private String timestamp;

    @DBRef
    @Field("track")
    @JsonIgnoreProperties(value = { "artist", "album", "playlists" }, allowSetters = true)
    private Track track;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public ListeningHistory id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public ListeningHistory timestamp(String timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Track getTrack() {
        return this.track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public ListeningHistory track(Track track) {
        this.setTrack(track);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ListeningHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((ListeningHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ListeningHistory{" +
            "id=" + getId() +
            ", timestamp='" + getTimestamp() + "'" +
            "}";
    }
}
