package com.konsol.beatstream.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.konsol.beatstream.domain.ListeningHistory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ListeningHistoryDTO implements Serializable {

    private String id;

    private String timestamp;

    private TrackDTO track;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public TrackDTO getTrack() {
        return track;
    }

    public void setTrack(TrackDTO track) {
        this.track = track;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ListeningHistoryDTO)) {
            return false;
        }

        ListeningHistoryDTO listeningHistoryDTO = (ListeningHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, listeningHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ListeningHistoryDTO{" +
            "id='" + getId() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", track=" + getTrack() +
            "}";
    }
}
