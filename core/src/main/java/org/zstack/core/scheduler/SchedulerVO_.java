package org.zstack.core.scheduler;

import javax.persistence.EnumType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by Mei Lei on 7/13/16.
 */
@StaticMetamodel(SchedulerVO.class)
public class SchedulerVO_ {
    public static volatile SingularAttribute<SchedulerVO, String> uuid;
    public static volatile SingularAttribute<SchedulerVO, String> schedulerName;
    public static volatile SingularAttribute<SchedulerVO, Timestamp> startDate;
    public static volatile SingularAttribute<SchedulerVO, Integer> schedulerInterval;
    public static volatile SingularAttribute<SchedulerVO, Timestamp> createDate;
    public static volatile SingularAttribute<SchedulerVO, String> jobName;
    public static volatile SingularAttribute<SchedulerVO, String> jobGroup;
    public static volatile SingularAttribute<SchedulerVO, String> triggerName;
    public static volatile SingularAttribute<SchedulerVO, String> triggerGroup;
    public static volatile SingularAttribute<SchedulerVO, String> jobClassName;
    public static volatile SingularAttribute<SchedulerVO, String> jobDataMap;
    public static volatile SingularAttribute<SchedulerVO, EnumType> status;

}
