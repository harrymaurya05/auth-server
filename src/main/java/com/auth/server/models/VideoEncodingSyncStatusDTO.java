package com.auth.server.models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "channelOrderSyncStatus")
//@CompoundIndexes({ @CompoundIndex(name = "tenant_channel", def = "{'tenantCode' :  1, 'channelCode' :  1}", unique = true) })
public class VideoEncodingSyncStatusDTO implements Serializable {

    private static final long           serialVersionUID    = 8742974020680542729L;

    @Id
    private String                      id;

    private String                      channelCode;
    private String                      tenantCode;
    private Channel.SyncExecutionStatus syncExecutionStatus = Channel.SyncExecutionStatus.IDLE;
    private String                      message;
    private long                        totalMileStones;
    private long                        currentMileStone;
    private int                         lastSuccessfulImportCount;
    private long                        todaysSuccessfulImportCount;
    private int                         lastFailedImportCount;
    private int                         todaysFailedImportCount;
    private boolean                     lastSyncSuccessful;
    private Date                        lastSyncTime;
    private Date                        lastSyncFailedNotificationTime;
    private Date                        lastSuccessfulOrderReceivedTime;
    private Date                        created;
    private Date                        lastMileStoneUpdateTime;

    public VideoEncodingSyncStatusDTO(String channelCode) {
        this.channelCode = channelCode;
        this.created = DateUtils.getCurrentTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLastMileStoneUpdateTime() {
        return lastMileStoneUpdateTime;
    }

    public void setLastMileStoneUpdateTime(Date lastMileStoneUpdateTime) {
        this.lastMileStoneUpdateTime = lastMileStoneUpdateTime;
    }

    public float getPercentageComplete() {
        if (totalMileStones == 0) {
            return 0;
        }
        return ((float) (currentMileStone * 10000 / totalMileStones)) / 100;
    }


    public void setSyncExecutionStatus(Channel.SyncExecutionStatus syncExecutionStatus) {
        this.syncExecutionStatus = syncExecutionStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(Date lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public long getTotalMileStones() {
        return totalMileStones;
    }

    public void setTotalMileStones(long totalMileStones) {
        this.totalMileStones = totalMileStones;
    }

    public long getCurrentMileStone() {
        return currentMileStone;
    }

    public void setCurrentMileStone(long currentMileStone) {
        this.currentMileStone = currentMileStone;
    }

    public void reset() {
        syncExecutionStatus = Channel.SyncExecutionStatus.IDLE;
        message = null;
        totalMileStones = 0;
        currentMileStone = 0;
        lastSyncSuccessful = false;
        lastMileStoneUpdateTime = null;
    }

    public void setMileStone(String message) {
        currentMileStone++;
        lastMileStoneUpdateTime = new Date();
    }

    public int getLastSuccessfulImportCount() {
        return lastSuccessfulImportCount;
    }

    public void setLastSuccessfulImportCount(int lastSuccessfulImportCount) {
        this.lastSuccessfulImportCount = lastSuccessfulImportCount;
    }

    public long getTodaysSuccessfulImportCount() {
        return todaysSuccessfulImportCount;
    }

    public void setTodaysSuccessfulImportCount(long todaysSuccessfulImportCount) {
        this.todaysSuccessfulImportCount = todaysSuccessfulImportCount;
    }

    public int getLastFailedImportCount() {
        return lastFailedImportCount;
    }

    public void setLastFailedImportCount(int lastFailedImportCount) {
        this.lastFailedImportCount = lastFailedImportCount;
    }

    public int getTodaysFailedImportCount() {
        return todaysFailedImportCount;
    }

    public Date getLastSyncFailedNotificationTime() {
        return lastSyncFailedNotificationTime;
    }

    public void setLastSyncFailedNotificationTime(Date lastSyncFailedNotificationTime) {
        this.lastSyncFailedNotificationTime = lastSyncFailedNotificationTime;
    }

    public void setTodaysFailedImportCount(int todaysFailedImportCount) {
        this.todaysFailedImportCount = todaysFailedImportCount;
    }

    public Date getLastSuccessfulOrderReceivedTime() {
        return lastSuccessfulOrderReceivedTime;
    }

    public void setLastSuccessfulOrderReceivedTime(Date lastSuccessfulOrderReceivedTime) {
        this.lastSuccessfulOrderReceivedTime = lastSuccessfulOrderReceivedTime;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public boolean isLastSyncSuccessful() {
        return lastSyncSuccessful;
    }

    public void setLastSyncSuccessful(boolean lastSyncSuccessful) {
        this.lastSyncSuccessful = lastSyncSuccessful;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}