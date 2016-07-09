package org.zstack.core.scheduler;

import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;

/**
 * Created by Mei Lei on 7/5/16.
 */
public class SchedulerApiInterceptor implements ApiMessageInterceptor {
    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        // TO DO : meilei
        return msg;
    }
}
