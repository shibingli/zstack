package org.zstack.core.scheduler;

import org.springframework.context.annotation.ComponentScan;
import org.zstack.header.Component;

/**
 * Created by root on 6/22/16.
 */
@ComponentScan
public interface SchedulerFacade extends Component{
    void schedulerRunner(SchedulerJob job);
}
