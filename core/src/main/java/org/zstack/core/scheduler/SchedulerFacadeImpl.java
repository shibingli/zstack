package org.zstack.core.scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.errorcode.SysErrors;
import org.zstack.header.message.Message;
import org.zstack.utils.gson.JSONObjectUtil;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by Mei Lei on 6/22/16.
 */
public class SchedulerFacadeImpl extends AbstractService implements SchedulerFacade {
    @Autowired
    private transient CloudBus bus;
    @Autowired
    private transient ErrorFacade errf;
    @Autowired
    protected transient DatabaseFacade dbf;
    private Scheduler scheduler;


    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIDeleteSchedulerMsg) {
            handle((APIDeleteSchedulerMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIDeleteSchedulerMsg msg) {
        APIDeleteSchedulerEvent evt = new APIDeleteSchedulerEvent(msg.getId());
        SimpleQuery<SchedulerVO> q = dbf.createQuery(SchedulerVO.class);
        q.select(SchedulerVO_.jobName);
        q.add(SchedulerVO_.uuid, SimpleQuery.Op.EQ, msg.getUuid());
        String jobName = q.findValue();
        SimpleQuery<SchedulerVO> q2 = dbf.createQuery(SchedulerVO.class);
        q2.select(SchedulerVO_.jobGroup);
        q2.add(SchedulerVO_.uuid, SimpleQuery.Op.EQ, msg.getUuid());
        String jobGroup = q2.findValue();
        try {
            scheduler.deleteJob(jobKey(jobName, jobGroup));
            dbf.removeByPrimaryKey(msg.getUuid(), SchedulerVO.class);
            bus.publish(evt);
        } catch (SchedulerException e) {
            evt.setErrorCode(errf.instantiateErrorCode(SysErrors.DELETE_RESOURCE_ERROR, e.getMessage()));
            e.printStackTrace();
        }

    }

    public String getId() {
        return bus.makeLocalServiceId(SchedulerConstant.SERVICE_ID);
    }

    public boolean start() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        List<SchedulerVO> schedulerRecords = dbf.listAll(SchedulerVO.class);
        Iterator<SchedulerVO> schedulerRecordsIterator = schedulerRecords.iterator();
        while (schedulerRecordsIterator.hasNext()) {
            SchedulerVO schedulerRecord = schedulerRecordsIterator.next();
            try {
                SchedulerJob rebootJob = (SchedulerJob) JSONObjectUtil.toObject(schedulerRecord.getJobData(), Class.forName(schedulerRecord.getJobClassName()));
                schedulerRunner(rebootJob, false);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public boolean stop() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void schedulerRunner(SchedulerJob schedulerJob) {
        schedulerRunner(schedulerJob, true);
    }

    private void schedulerRunner(SchedulerJob schedulerJob, boolean saveDB) {
        SchedulerVO vo = new SchedulerVO();
        Timestamp create = new Timestamp(System.currentTimeMillis());
        Timestamp start = new Timestamp(schedulerJob.getStartDate().getTime());
        String jobData = JSONObjectUtil.toJsonString(schedulerJob);
        String jobClassName = schedulerJob.getClass().getName();
        if (saveDB) {
            vo.setUuid(Platform.getUuid());
            vo.setSchedulerName(schedulerJob.getSchedulerName());
            vo.setStartDate(start);
            vo.setSchedulerInterval(schedulerJob.getSchedulerInterval());
            vo.setCreateDate(create);
            vo.setJobName(schedulerJob.getJobName());
            vo.setJobGroup(schedulerJob.getJobGroup());
            vo.setTriggerName(schedulerJob.getTriggerName());
            vo.setTriggerGroup(schedulerJob.getTriggerGroup());
            vo.setJobClassName(jobClassName);
            vo.setJobData(jobData);
        }

        try {

            JobDetail job = newJob(SchedulerRunner.class)
                    .withIdentity(schedulerJob.getJobName(), schedulerJob.getJobGroup())
                    .usingJobData("jobClassName", jobClassName)
                    .usingJobData("jobData", jobData)
                    .build();

            Trigger trigger = newTrigger()
                    .withIdentity(schedulerJob.getTriggerName(), schedulerJob.getTriggerGroup())
                    .startAt(schedulerJob.getStartDate())
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(schedulerJob.getSchedulerInterval())
                            .repeatForever())
                    .build();

            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }

        if (saveDB) {
            vo.setStatus("enabled");
            dbf.persist(vo);
        }
    }
}
