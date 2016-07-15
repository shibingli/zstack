CREATE TABLE  `zstack`.`ImageStoreBackupStorageVO` (
    `uuid` varchar(32) NOT NULL UNIQUE,
    `hostname` varchar(255) NOT NULL UNIQUE,
    `username` varchar(255) NOT NULL,
    `password` varchar(255) NOT NULL,
    `sshPort` int unsigned NOT NULL,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE AccountVO modify column name varchar(255) NOT NULL;
ALTER TABLE UserVO modify column name varchar(255) NOT NULL;
CREATE TABLE  `zstack`.`SchedulerVO` (
    `uuid` varchar(32) NOT NULL UNIQUE,
    `schedulerName` varchar(255),
    `schedulerInterval` int unsigned NOT NULL,
    `jobName` varchar(255),
    `jobGroup` varchar(255),
    `triggerName` varchar(255),
    `triggerGroup` varchar(255),
    `jobClassName` varchar(255),
    `jobData` varchar(65535),
    `status` varchar(255),
    `createDate` timestamp DEFAULT CURRENT_TIMESTAMP,
    `startDate` timestamp NOT NULL,
    PRIMARY KEY  (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
