package org.zstack.core.scheduler;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/**
 * Created by root on 7/11/16.
 */
public interface SchedulerJob extends Job {
    Date getStartDate();
    Timestamp getCreateDate();
    int getInterval();
    String getSchedulerName();
    String getJobName();
    String getJobGroup();
    String getTriggerName();
    String getTriggerGroup();
}
