package org.zstack.core.scheduler;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Mei Lei on 7/11/16.
 */
@Entity
@Table
@Inheritance(strategy= InheritanceType.JOINED)
public class SchedulerVO {
    @Id
    @Column
    private String uuid;
    @Column
    private String schedulerName;
    @Column
    private Timestamp createDate;
    @Column
    private Timestamp startDate;
    @Column
    private int schedulerInterval;
    @Column
    private String jobName;
    @Column
    private String jobGroup;
    @Column
    private String triggerName;
    @Column
    private String triggerGroup;
    /**
     * @desc jobClassName define the job
     */
    @Column
    private String jobClassName;
    @Column
    private String jobDataMap;
    @Column
    private String status;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public int getSchedulerInterval() {
        return schedulerInterval;
    }

    public void setSchedulerInterval(int schedulerInterval) {
        this.schedulerInterval = schedulerInterval;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
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

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerGroup() {
        return triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    public String getJobClassName() {
        return jobClassName;
    }

    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getJobDataMap() {
        return jobDataMap;
    }

    public void setJobDataMap(String jobDataMap) {
        this.jobDataMap = jobDataMap;
    }
    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }
}
