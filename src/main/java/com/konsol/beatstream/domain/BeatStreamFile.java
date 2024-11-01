package com.konsol.beatstream.domain;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A BeatStreamFile.
 */
@Document(collection = "beat_stream_file")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BeatStreamFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("name")
    private String name;

    @Field("size")
    private Long size;

    @Field("bucket")
    private String bucket;

    @Field("type")
    private String type;

    @Field("fullPath")
    private String fullPath;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public BeatStreamFile id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public BeatStreamFile name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return this.size;
    }

    public BeatStreamFile size(Long size) {
        this.setSize(size);
        return this;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getBucket() {
        return this.bucket;
    }

    public BeatStreamFile bucket(String bucket) {
        this.setBucket(bucket);
        return this;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getType() {
        return this.type;
    }

    public BeatStreamFile type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BeatStreamFile)) {
            return false;
        }
        return getId() != null && getId().equals(((BeatStreamFile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BeatStreamFile{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", size=" + getSize() +
            ", bucket='" + getBucket() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }
}
