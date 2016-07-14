package org.zstack.storage.snapshot;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.CloudBusCallBack;
import org.zstack.core.scheduler.SchedulerJob;
import org.zstack.core.scheduler.SchedulerFacadeImpl;
import org.zstack.header.message.MessageReply;
import org.zstack.header.storage.snapshot.CreateVolumeSnapshotMsg;
import org.zstack.header.storage.snapshot.CreateVolumeSnapshotReply;
import org.zstack.header.storage.snapshot.VolumeSnapshotConstant;
import org.zstack.header.volume.APICreateVolumeSnapshotEvent;
import org.zstack.identity.AccountManager;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Mei Lei on 7/11/16.
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class CreateVolumeSnapshotJob implements SchedulerJob {
    @Autowired
    private transient CloudBus bus;
    @Autowired
    private transient AccountManager acntMgr;

    private Date startDate;
    private int interval;
    private String schedulerName;
    private String jobName;
    private String jobGroup;
    private String triggerGroup;
    private String triggerName;
    private Timestamp createDate;
    private String volumeUuid;
    private String name;
    private String description;

    @Override
    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    private static final CLogger logger = Utils.getLogger(SchedulerFacadeImpl.class);
    public CreateVolumeSnapshotJob() {

    }

    public void run() {

        CreateVolumeSnapshotMsg cmsg = new CreateVolumeSnapshotMsg();
        cmsg.setName(getName());
        cmsg.setDescription(getDescription());
        cmsg.setVolumeUuid(getVolumeUuid());
        cmsg.setAccountUuid(acntMgr.getOwnerAccountUuidOfResource(getVolumeUuid()));
        bus.makeTargetServiceIdByResourceUuid(cmsg, VolumeSnapshotConstant.SERVICE_ID, getVolumeUuid());
        bus.send(cmsg, new CloudBusCallBack() {
            @Override
            public void run(MessageReply reply) {
                APICreateVolumeSnapshotEvent evt = new APICreateVolumeSnapshotEvent(cmsg.getId());
                if (reply.isSuccess()) {
                    CreateVolumeSnapshotReply creply = (CreateVolumeSnapshotReply) reply;
                    evt.setInventory(creply.getInventory());
                } else {
                    evt.setErrorCode(reply.getError());
                }
                bus.publish(evt);
            }
        });
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Override
    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    @Override
    public String getTriggerGroup() {
        return triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    @Override
    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public static CLogger getLogger() {
        return logger;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
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

    public String getVolumeUuid() {
        return volumeUuid;
    }

    public void setVolumeUuid(String volumeUuid) {
        this.volumeUuid = volumeUuid;
    }


}
