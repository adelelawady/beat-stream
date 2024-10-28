package com.konsol.beatstream.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.konsol.beatstream.domain.enumeration.DownloadStatus;
import com.konsol.beatstream.domain.enumeration.DownloadType;
import com.konsol.beatstream.domain.enumeration.ReferenceType;
import com.konsol.beatstream.service.TaskNode.TaskNodeEntityListener;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A TaskNode.
 */
@Document(collection = "task_node")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaskNode extends AbstractAuditingEntity<String> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("ownerId")
    private String ownerId;

    @NotNull
    @Field("reference_type")
    private ReferenceType referenceType;

    @NotNull
    @Field("reference_id")
    private String referenceId;

    @NotNull
    @Field("task_name")
    private String taskName;

    @Field("task_log")
    private String taskLog = "";

    @Field("track_id")
    private String trackId;

    @Field("playlist_id")
    private String playlistId;

    @Field("scheduled_start_time")
    private Instant scheduledStartTime;

    @Field("start_delay_minutes")
    private BigDecimal startDelayMinutes;

    @Field("start_delay_hours")
    private BigDecimal startDelayHours;

    @Field("elapsed_hours")
    private BigDecimal elapsedHours;

    @Field("elapsed_minutes")
    private BigDecimal elapsedMinutes;

    @Field("progress_percentage")
    private BigDecimal progressPercentage;

    @Field("download_filesize")
    private String downloadFilesize;

    @Field("download_speed")
    private String downloadSpeed;

    @Field("download_eta")
    private String downloadEta;

    @Field("node_index")
    private BigDecimal nodeIndex;

    @NotNull
    @Field("status")
    private DownloadStatus status = DownloadStatus.PENDING;

    @NotNull
    @Field("type")
    private DownloadType type;

    @Field("canRetry")
    private boolean canRetry = true;

    @Field("retry_count")
    private BigDecimal retryCount = new BigDecimal(0);

    @Field("max_retry_count")
    private BigDecimal maxRetryCount = new BigDecimal(3);

    @DBRef
    @Field("childTasks")
    @JsonIgnoreProperties(value = { "childTasks", "parentTask" }, allowSetters = true)
    private Set<TaskNode> childTasks = new HashSet<>();

    @DBRef
    @Field("parentTask")
    @JsonIgnoreProperties(value = { "childTasks", "parentTask" }, allowSetters = true)
    private TaskNode parentTask;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public TaskNode id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ReferenceType getReferenceType() {
        return this.referenceType;
    }

    public TaskNode referenceType(ReferenceType referenceType) {
        this.setReferenceType(referenceType);
        return this;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceId() {
        return this.referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public TaskNode taskName(String taskName) {
        this.setTaskName(taskName);
        return this;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskLog() {
        return this.taskLog;
    }

    public TaskNode taskLog(String taskLog) {
        this.setTaskLog(taskLog);
        return this;
    }

    public void setTaskLog(String taskLog) {
        this.taskLog = taskLog;
    }

    public String getTrackId() {
        return this.trackId;
    }

    public TaskNode trackId(String trackId) {
        this.setTrackId(trackId);
        return this;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public Instant getScheduledStartTime() {
        return this.scheduledStartTime;
    }

    public TaskNode scheduledStartTime(Instant scheduledStartTime) {
        this.setScheduledStartTime(scheduledStartTime);
        return this;
    }

    public void setScheduledStartTime(Instant scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    public BigDecimal getStartDelayMinutes() {
        return this.startDelayMinutes;
    }

    public TaskNode startDelayMinutes(BigDecimal startDelayMinutes) {
        this.setStartDelayMinutes(startDelayMinutes);
        return this;
    }

    public void setStartDelayMinutes(BigDecimal startDelayMinutes) {
        this.startDelayMinutes = startDelayMinutes;
    }

    public BigDecimal getStartDelayHours() {
        return this.startDelayHours;
    }

    public TaskNode startDelayHours(BigDecimal startDelayHours) {
        this.setStartDelayHours(startDelayHours);
        return this;
    }

    public void setStartDelayHours(BigDecimal startDelayHours) {
        this.startDelayHours = startDelayHours;
    }

    public BigDecimal getElapsedHours() {
        return this.elapsedHours;
    }

    public TaskNode elapsedHours(BigDecimal elapsedHours) {
        this.setElapsedHours(elapsedHours);
        return this;
    }

    public void setElapsedHours(BigDecimal elapsedHours) {
        this.elapsedHours = elapsedHours;
    }

    public BigDecimal getElapsedMinutes() {
        return this.elapsedMinutes;
    }

    public TaskNode elapsedMinutes(BigDecimal elapsedMinutes) {
        this.setElapsedMinutes(elapsedMinutes);
        return this;
    }

    public void setElapsedMinutes(BigDecimal elapsedMinutes) {
        this.elapsedMinutes = elapsedMinutes;
    }

    public BigDecimal getProgressPercentage() {
        return this.progressPercentage;
    }

    public TaskNode progressPercentage(BigDecimal progressPercentage) {
        this.setProgressPercentage(progressPercentage);
        return this;
    }

    public void setProgressPercentage(BigDecimal progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getDownloadFilesize() {
        return this.downloadFilesize;
    }

    public TaskNode downloadFilesize(String downloadFilesize) {
        this.setDownloadFilesize(downloadFilesize);
        return this;
    }

    public void setDownloadFilesize(String downloadFilesize) {
        this.downloadFilesize = downloadFilesize;
    }

    public String getDownloadSpeed() {
        return this.downloadSpeed;
    }

    public TaskNode downloadSpeed(String downloadSpeed) {
        this.setDownloadSpeed(downloadSpeed);
        return this;
    }

    public void setDownloadSpeed(String downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public String getDownloadEta() {
        return this.downloadEta;
    }

    public TaskNode downloadEta(String downloadEta) {
        this.setDownloadEta(downloadEta);
        return this;
    }

    public void setDownloadEta(String downloadEta) {
        this.downloadEta = downloadEta;
    }

    public BigDecimal getNodeIndex() {
        return this.nodeIndex;
    }

    public TaskNode nodeIndex(BigDecimal nodeIndex) {
        this.setNodeIndex(nodeIndex);
        return this;
    }

    public void setNodeIndex(BigDecimal nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    public DownloadStatus getStatus() {
        return this.status;
    }

    public TaskNode status(DownloadStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(DownloadStatus status) {
        this.status = status;
    }

    public DownloadType getType() {
        return this.type;
    }

    public TaskNode type(DownloadType type) {
        this.setType(type);
        return this;
    }

    public void setType(DownloadType type) {
        this.type = type;
    }

    public BigDecimal getRetryCount() {
        return this.retryCount;
    }

    public TaskNode retryCount(BigDecimal retryCount) {
        this.setRetryCount(retryCount);
        return this;
    }

    public void setRetryCount(BigDecimal retryCount) {
        this.retryCount = retryCount;
    }

    public BigDecimal getMaxRetryCount() {
        return this.maxRetryCount;
    }

    public TaskNode maxRetryCount(BigDecimal maxRetryCount) {
        this.setMaxRetryCount(maxRetryCount);
        return this;
    }

    public void setMaxRetryCount(BigDecimal maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public Set<TaskNode> getChildTasks() {
        return this.childTasks;
    }

    public void setChildTasks(Set<TaskNode> taskNodes) {
        if (this.childTasks != null) {
            this.childTasks.forEach(i -> i.setParentTask(null));
        }
        if (taskNodes != null) {
            taskNodes.forEach(i -> i.setParentTask(this));
        }
        this.childTasks = taskNodes;
    }

    public TaskNode childTasks(Set<TaskNode> taskNodes) {
        this.setChildTasks(taskNodes);
        return this;
    }

    public TaskNode addChildTasks(TaskNode taskNode) {
        this.childTasks.add(taskNode);
        taskNode.setParentTask(this);
        return this;
    }

    public TaskNode removeChildTasks(TaskNode taskNode) {
        this.childTasks.remove(taskNode);
        taskNode.setParentTask(null);
        return this;
    }

    public TaskNode getParentTask() {
        return this.parentTask;
    }

    public void setParentTask(TaskNode taskNode) {
        this.parentTask = taskNode;
    }

    public TaskNode parentTask(TaskNode taskNode) {
        this.setParentTask(taskNode);
        return this;
    }

    public void incrementRetryCount() {
        retryCount = retryCount.add(new BigDecimal(1));
    }

    public void markAsFailed() {
        status = DownloadStatus.FAILED;
    }

    public void markInProgress() {
        status = DownloadStatus.IN_PROGRESS;
    }

    public void markCompleted() {
        status = DownloadStatus.COMPLETED;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskNode)) {
            return false;
        }
        return getId() != null && getId().equals(((TaskNode) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskNode{" +
            "id=" + getId() +
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
            ", retryCount=" + getRetryCount() +
            ", maxRetryCount=" + getMaxRetryCount() +
            "}";
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isCanRetry() {
        return canRetry;
    }

    public void setCanRetry(boolean canRetry) {
        this.canRetry = canRetry;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }
}
