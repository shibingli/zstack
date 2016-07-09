package org.zstack.header.volume;

import org.zstack.header.message.APICreateMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by root on 7/11/16.
 */
public class APICreateVolumeSnapshotSchedulerMsg extends APICreateMessage implements  VolumeMessage {
    @APIParam
    private String schedulerName;
    @APIParam
    private int interval;
    @APIParam
    private long startTimeStamp;

    private String jobName;
    private String jobGroup;
    private String triggerGroup;
    private String triggerName;
    /**
     * @desc volume uuid. See :ref:`VolumeInventory`
     */
    @APIParam(resourceType = VolumeVO.class, checkAccount = true, operationTarget = true)
    private String volumeUuid;
    /**
     * @desc snapshot name. Max length of 255 characters
     */
    @APIParam(maxLength = 255)
    private String name;
    /**
     * @desc snapshot description. Max length of 2048 characters
     */
    @APIParam(required = false, maxLength = 2048)
    private String description;

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }


    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getTriggerGroup() {
        return triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    @Override
    public String getVolumeUuid() {
        return volumeUuid;
    }

    public void setVolumeUuid(String volumeUuid) {
        this.volumeUuid = volumeUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
