package org.zstack.core.scheduler;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.errorcode.ErrorFacade;

import java.sql.Timestamp;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by Mei Lei on 6/22/16.
 */
public class SchedulerFacadeImpl implements SchedulerFacade {
    @Autowired
    private CloudBus bus;
    @Autowired
    private ErrorFacade errf;
    private Scheduler scheduler;
    @Autowired
    protected DatabaseFacade dbf;

    public String getId() {
        return bus.makeLocalServiceId(SchedulerConstant.SERVICE_ID);
    }
    public boolean start() {
        // need to load all scheduler in db and run
        return true;
    }

    public boolean stop() {
        return true;
    }

    public void schedulerRunner(SchedulerJob schedulerJob) {
        // 1. write DB
        // 2. call quartz
        //newJob(job.getClass());
        SchedulerVO vo = new SchedulerVO();
        Timestamp create = new Timestamp(System.currentTimeMillis());
        Timestamp start = new Timestamp(schedulerJob.getStartDate().getTime());
        vo.setUuid(Platform.getUuid());
        vo.setSchedulerName(schedulerJob.getSchedulerName());
        vo.setStartDate(start);
        vo.setIntervalHour(schedulerJob.getInterval());
        vo.setCreateDate(create);
        vo.setJobName(schedulerJob.getJobName());
        vo.setJobGroup(schedulerJob.getJobGroup());
        vo.setTriggerName(schedulerJob.getTriggerName());
        vo.setTriggerGroup(schedulerJob.getTriggerGroup());
        vo.setJobClassName(schedulerJob.getClass().getName());
        vo.setJobDataMap("test");

        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(schedulerJob.getClass())
                    .withIdentity(vo.getJobName(), vo.getJobGroup())
                    .build();
            Trigger trigger = newTrigger()
                    .withIdentity(vo.getTriggerName(), vo.getTriggerGroup())
                    .startAt(schedulerJob.getStartDate())
                    .withSchedule(simpleSchedule()
                            //.withIntervalInHours(vo.getInterval())
                            .withIntervalInSeconds(vo.getIntervalHour())
                            .repeatForever())
                    .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);
            vo.setStatus("enabled");
            dbf.persist(vo);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }

    }
}
