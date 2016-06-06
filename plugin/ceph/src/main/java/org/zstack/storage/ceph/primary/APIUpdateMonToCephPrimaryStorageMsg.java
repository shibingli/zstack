package org.zstack.storage.ceph.primary;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.storage.primary.PrimaryStorageMessage;

import java.util.List;

/**
 * Created by Mei Lei on 6/6/2016.
 */
public class APIUpdateMonToCephPrimaryStorageMsg extends APIMessage implements PrimaryStorageMessage {
    @APIParam(resourceType = CephPrimaryStorageVO.class, emptyString = false)
    private String primaryStorageUuid;

    @APIParam(resourceType = CephPrimaryStorageMonVO.class, emptyString = false)
    private String monUuid;

    @APIParam(maxLength = 255, required = false)
    private String hostname;

    @APIParam(maxLength = 255, required = false)
    private String sshUsername;

    @APIParam(maxLength = 255, required = false)
    private String sshPassword;

    @APIParam(numberRange = {1, 65535}, required = false)
    private Integer sshPort;

    @APIParam(numberRange = {1, 65535}, required = false)
    private Integer monPort;


    @Override
    public String getPrimaryStorageUuid() {
        return primaryStorageUuid;
    }

    public void setPrimaryStorageUuid(String primaryStorageUuid) {
        this.primaryStorageUuid = primaryStorageUuid;
    }

    public String getMonUuid() {
        return monUuid;
    }

    public void setMonUuid(String monUuid) {
        this.monUuid = monUuid;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getSshUsername() {
        return sshUsername;
    }

    public void setSshUsername(String sshUsername) {
        this.sshUsername = sshUsername;
    }

    public String getSshPassword() {
        return sshPassword;
    }

    public void setSshPassword(String sshPassword) {
        this.sshPassword = sshPassword;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    public Integer getMonPort() {
        return monPort;
    }

    public void setMonPort(Integer monPort) {
        this.monPort = monPort;
    }
}
