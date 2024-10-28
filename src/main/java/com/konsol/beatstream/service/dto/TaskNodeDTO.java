package com.konsol.beatstream.service.dto;

import com.konsol.beatstream.domain.enumeration.DownloadStatus;
import com.konsol.beatstream.domain.enumeration.DownloadType;
import com.konsol.beatstream.domain.enumeration.ReferenceType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.konsol.beatstream.domain.TaskNode} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaskNodeDTO implements Serializable {

    private String id;

    @NotNull
    private ReferenceType referenceType;

    @NotNull
    private Long referenceId;

    @NotNull
    private String taskName;

    private String taskLog;

    private Long trackId;

    private Instant scheduledStartTime;

    private BigDecimal startDelayMinutes;

    private BigDecimal startDelayHours;

    private BigDecimal elapsedHours;

    private BigDecimal elapsedMinutes;

    private BigDecimal progressPercentage;

    private String downloadFilesize;

    private String downloadSpeed;

    private String downloadEta;

    private BigDecimal nodeIndex;

    @NotNull
    private DownloadStatus status;

    @NotNull
    private DownloadType type;

    private BigDecimal failCount;

    private BigDecimal retryCount;

    private BigDecimal maxRetryCount;

    private TaskNodeDTO parentTask;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskLog() {
        return taskLog;
    }

    public void setTaskLog(String taskLog) {
        this.taskLog = taskLog;
    }

    public Long getTrackId() {
        return trackId;
    }

    public void setTrackId(Long trackId) {
        this.trackId = trackId;
    }

    public Instant getScheduledStartTime() {
        return scheduledStartTime;
    }

    public void setScheduledStartTime(Instant scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    public BigDecimal getStartDelayMinutes() {
        return startDelayMinutes;
    }

    public void setStartDelayMinutes(BigDecimal startDelayMinutes) {
        this.startDelayMinutes = startDelayMinutes;
    }

    public BigDecimal getStartDelayHours() {
        return startDelayHours;
    }

    public void setStartDelayHours(BigDecimal startDelayHours) {
        this.startDelayHours = startDelayHours;
    }

    public BigDecimal getElapsedHours() {
        return elapsedHours;
    }

    public void setElapsedHours(BigDecimal elapsedHours) {
        this.elapsedHours = elapsedHours;
    }

    public BigDecimal getElapsedMinutes() {
        return elapsedMinutes;
    }

    public void setElapsedMinutes(BigDecimal elapsedMinutes) {
        this.elapsedMinutes = elapsedMinutes;
    }

    public BigDecimal getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(BigDecimal progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getDownloadFilesize() {
        return downloadFilesize;
    }

    public void setDownloadFilesize(String downloadFilesize) {
        this.downloadFilesize = downloadFilesize;
    }

    public String getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(String downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public String getDownloadEta() {
        return downloadEta;
    }

    public void setDownloadEta(String downloadEta) {
        this.downloadEta = downloadEta;
    }

    public BigDecimal getNodeIndex() {
        return nodeIndex;
    }

    public void setNodeIndex(BigDecimal nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public void setStatus(DownloadStatus status) {
        this.status = status;
    }

    public DownloadType getType() {
        return type;
    }

    public void setType(DownloadType type) {
        this.type = type;
    }

    public BigDecimal getFailCount() {
        return failCount;
    }

    public void setFailCount(BigDecimal failCount) {
        this.failCount = failCount;
    }

    public BigDecimal getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(BigDecimal retryCount) {
        this.retryCount = retryCount;
    }

    public BigDecimal getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(BigDecimal maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public TaskNodeDTO getParentTask() {
        return parentTask;
    }

    public void setParentTask(TaskNodeDTO parentTask) {
        this.parentTask = parentTask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskNodeDTO)) {
            return false;
        }

        TaskNodeDTO taskNodeDTO = (TaskNodeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, taskNodeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskNodeDTO{" +
            "id='" + getId() + "'" +
            ", referenceType='" + getReferenceType() + "'" +
            ", referenceId=" + getReferenceId() +
            ", taskName='" + getTaskName() + "'" +
            ", taskLog='" + getTaskLog() + "'" +
            ", trackId=" + getTrackId() +
            ", scheduledStartTime='" + getScheduledStartTime() + "'" +
            ", startDelayMinutes=" + getStartDelayMinutes() +
            ", startDelayHours=" + getStartDelayHours() +
            ", elapsedHours=" + getElapsedHours() +
            ", elapsedMinutes=" + getElapsedMinutes() +
            ", progressPercentage=" + getProgressPercentage() +
            ", downloadFilesize='" + getDownloadFilesize() + "'" +
            ", downloadSpeed='" + getDownloadSpeed() + "'" +
            ", downloadEta='" + getDownloadEta() + "'" +
            ", nodeIndex=" + getNodeIndex() +
            ", status='" + getStatus() + "'" +
            ", type='" + getType() + "'" +
            ", failCount=" + getFailCount() +
            ", retryCount=" + getRetryCount() +
            ", maxRetryCount=" + getMaxRetryCount() +
            ", parentTask=" + getParentTask() +
            "}";
    }
}
