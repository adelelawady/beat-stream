package com.konsol.beatstream.domain;

import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A ReferanceDownloadTask.
 */
@Document(collection = "referance_download_task")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReferanceDownloadTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("referance_id")
    private String referanceId;

    @Field("referance_type")
    private String referanceType;

    @Field("referance_track_id")
    private String referanceTrackId;

    @Field("referance_status")
    private String referanceStatus;

    @Field("referance_schedule_date")
    private Instant referanceScheduleDate;

    @Field("referance_log")
    private String referanceLog;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public ReferanceDownloadTask id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferanceId() {
        return this.referanceId;
    }

    public ReferanceDownloadTask referanceId(String referanceId) {
        this.setReferanceId(referanceId);
        return this;
    }

    public void setReferanceId(String referanceId) {
        this.referanceId = referanceId;
    }

    public String getReferanceType() {
        return this.referanceType;
    }

    public ReferanceDownloadTask referanceType(String referanceType) {
        this.setReferanceType(referanceType);
        return this;
    }

    public void setReferanceType(String referanceType) {
        this.referanceType = referanceType;
    }

    public String getReferanceTrackId() {
        return this.referanceTrackId;
    }

    public ReferanceDownloadTask referanceTrackId(String referanceTrackId) {
        this.setReferanceTrackId(referanceTrackId);
        return this;
    }

    public void setReferanceTrackId(String referanceTrackId) {
        this.referanceTrackId = referanceTrackId;
    }

    public String getReferanceStatus() {
        return this.referanceStatus;
    }

    public ReferanceDownloadTask referanceStatus(String referanceStatus) {
        this.setReferanceStatus(referanceStatus);
        return this;
    }

    public void setReferanceStatus(String referanceStatus) {
        this.referanceStatus = referanceStatus;
    }

    public Instant getReferanceScheduleDate() {
        return this.referanceScheduleDate;
    }

    public ReferanceDownloadTask referanceScheduleDate(Instant referanceScheduleDate) {
        this.setReferanceScheduleDate(referanceScheduleDate);
        return this;
    }

    public void setReferanceScheduleDate(Instant referanceScheduleDate) {
        this.referanceScheduleDate = referanceScheduleDate;
    }

    public String getReferanceLog() {
        return this.referanceLog;
    }

    public ReferanceDownloadTask referanceLog(String referanceLog) {
        this.setReferanceLog(referanceLog);
        return this;
    }

    public void setReferanceLog(String referanceLog) {
        this.referanceLog = referanceLog;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReferanceDownloadTask)) {
            return false;
        }
        return getId() != null && getId().equals(((ReferanceDownloadTask) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReferanceDownloadTask{" +
            "id=" + getId() +
            ", referanceId='" + getReferanceId() + "'" +
            ", referanceType='" + getReferanceType() + "'" +
            ", referanceTrackId='" + getReferanceTrackId() + "'" +
            ", referanceStatus='" + getReferanceStatus() + "'" +
            ", referanceScheduleDate='" + getReferanceScheduleDate() + "'" +
            ", referanceLog='" + getReferanceLog() + "'" +
            "}";
    }
}
