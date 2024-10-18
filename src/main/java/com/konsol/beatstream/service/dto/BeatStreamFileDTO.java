package com.konsol.beatstream.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.konsol.beatstream.domain.BeatStreamFile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BeatStreamFileDTO implements Serializable {

    private String id;

    @NotNull
    private String name;

    private Long size;

    private String bucket;

    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BeatStreamFileDTO)) {
            return false;
        }

        BeatStreamFileDTO beatStreamFileDTO = (BeatStreamFileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, beatStreamFileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BeatStreamFileDTO{" +
            "id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", size=" + getSize() +
            ", bucket='" + getBucket() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
