SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `user_table`;
DROP TABLE IF EXISTS `group_table`;
DROP TABLE IF EXISTS `urgency`;
DROP TABLE IF EXISTS `task`;
DROP TABLE IF EXISTS `group_member`;
DROP TABLE IF EXISTS `hibernate_sequences`;
DROP TABLE IF EXISTS `offer_membership`;
DROP TABLE IF EXISTS `offer_task_group`;
DROP TABLE IF EXISTS `offer_task_user`;
DROP TABLE IF EXISTS `task_group`;
DROP TABLE IF EXISTS `task_participant`;
DROP TABLE IF EXISTS `task_participant_group`;
DROP TABLE IF EXISTS `task_work`;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `user_table` (
  `user_id` bigint(20) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `pwdhash` varchar(255) NOT NULL,
  `pwdsalt` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_USERNAME` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `group_table` (
  `group_id` bigint(20) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`group_id`),
  UNIQUE KEY `UK_GROUP_NAME` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `urgency` (
  `urgency_id` bigint(20) NOT NULL,
  `last_update` datetime NOT NULL,
  `value` double NOT NULL,
  PRIMARY KEY (`urgency_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `task` (
  `task_type` varchar(31) NOT NULL,
  `task_id` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `priority` int(11) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `work_estimate` double DEFAULT NULL,
  `hours_to_peak` double DEFAULT NULL,
  `date_deadline` datetime DEFAULT NULL,
  `urgency_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`task_id`),
  KEY `FK_TASK_TO_URGENCY` (`urgency_id`),
  CONSTRAINT `FK_TASK_TO_URGENCY` FOREIGN KEY (`urgency_id`) REFERENCES `urgency` (`urgency_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




CREATE TABLE `group_member` (
  `group_member_id` bigint(20) NOT NULL,
  `role` varchar(255) NOT NULL,
  `group_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`group_member_id`),
  KEY `FK_GROUPMEMBER_TO_GROUP` (`group_id`),
  KEY `FK_GROUPMEMBER_TO_USER` (`user_id`),
  CONSTRAINT `FK_GROUPMEMBER_TO_GROUP` FOREIGN KEY (`group_id`) REFERENCES `group_table` (`group_id`),
  CONSTRAINT `FK_GROUPMEMBER_TO_USER` FOREIGN KEY (`user_id`) REFERENCES `user_table` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `hibernate_sequences` (
  `sequence_name` varchar(255) DEFAULT NULL,
  `sequence_next_hi_value` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `offer_membership` (
  `offer_id` bigint(20) NOT NULL,
  `offerer_id` bigint(20) DEFAULT NULL,
  `group_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`offer_id`),
  KEY `FK_OFFER_MEMBERSHIP_TO_GROUP` (`group_id`),
  KEY `FK_OFFER_MEMBERSHIP_TO_USER` (`user_id`),
  KEY `FK_OFFER_MEMBERSHIP_TO_OFFERER` (`offerer_id`),
  CONSTRAINT `FK_OFFER_MEMBERSHIP_TO_GROUP` FOREIGN KEY (`group_id`) REFERENCES `group_table` (`group_id`),
  CONSTRAINT `FK_OFFER_MEMBERSHIP_TO_OFFERER` FOREIGN KEY (`offerer_id`) REFERENCES `user_table` (`user_id`),
  CONSTRAINT `FK_OFFER_MEMBERSHIP_TO_USER` FOREIGN KEY (`user_id`) REFERENCES `user_table` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `offer_task_group` (
  `offer_id` bigint(20) NOT NULL,
  `offerer_id` bigint(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `group_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`offer_id`),
  KEY `FK_OFFER_TASK_GROUP_TO_GROUP` (`group_id`),
  KEY `FK_OFFER_TASK_GROUP_TO_TASK` (`task_id`),
  KEY `FK_OFFER_TASK_GROUP_TO_USER` (`offerer_id`),
  CONSTRAINT `FK_OFFER_TASK_GROUP_TO_TASK` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`),
  CONSTRAINT `FK_OFFER_TASK_GROUP_TO_USER` FOREIGN KEY (`offerer_id`) REFERENCES `user_table` (`user_id`),
  CONSTRAINT `FK_OFFER_TASK_GROUP_TO_GROUP` FOREIGN KEY (`group_id`) REFERENCES `group_table` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `offer_task_user` (
  `offer_id` bigint(20) NOT NULL,
  `offerer_id` bigint(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `offered_to_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`offer_id`),
  KEY `FK_OFFER_TASK_USER_TO_OFFEREE` (`offered_to_id`),
  KEY `FK_OFFER_TASK_USER_TO_TASK` (`task_id`),
  KEY `FK_OFFER_TASK_USER_TO_OFFERER` (`offerer_id`),
  CONSTRAINT `FK_OFFER_TASK_USER_TO_OFFEREE` FOREIGN KEY (`offered_to_id`) REFERENCES `user_table` (`user_id`),
  CONSTRAINT `FK_OFFER_TASK_USER_TO_OFFERER` FOREIGN KEY (`offerer_id`) REFERENCES `user_table` (`user_id`),
  CONSTRAINT `FK_OFFER_TASK_USER_TO_TASK` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `task_group` (
  `task_id` bigint(20) NOT NULL,
  `group_id` bigint(20) NOT NULL,
  PRIMARY KEY (`task_id`,`group_id`),
  KEY `FK_TASK_GROUP_TO_GROUP` (`group_id`),
  CONSTRAINT `FK_TASK_GROUP_TO_TASK` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`),
  CONSTRAINT `FK_TASK_GROUP_TO_GROUP` FOREIGN KEY (`group_id`) REFERENCES `group_table` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `task_participant` (
  `taskparticipant_id` bigint(20) NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  `solo` bit(1) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`taskparticipant_id`),
  KEY `FK_TASK_PARTICIPANT_TO_TASK` (`task_id`),
  KEY `FK_TASK_PARTICIPANT_TO_USER` (`user_id`),
  CONSTRAINT `FK_TASK_PARTICIPANT_TO_USER` FOREIGN KEY (`user_id`) REFERENCES `user_table` (`user_id`),
  CONSTRAINT `FK_TASK_PARTICIPANT_TO_TASK` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `task_participant_group` (
  `taskparticipant_id` bigint(20) NOT NULL,
  `group_id` bigint(20) NOT NULL,
  PRIMARY KEY (`taskparticipant_id`,`group_id`),
  KEY `FK_TASK_PARTICIPANT_GROUP_TO_GROUP` (`group_id`),
  CONSTRAINT `FK_TASK_PARTICIPANT_GROUP_TO_GROUP` FOREIGN KEY (`group_id`) REFERENCES `group_table` (`group_id`),
  CONSTRAINT `FK_TASK_PARTICIPANT_GROUP_TO_TASK_PARTICIPANT` FOREIGN KEY (`taskparticipant_id`) REFERENCES `task_participant` (`taskparticipant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `task_work` (
  `taskwork_id` bigint(20) NOT NULL,
  `manhours` double NOT NULL,
  `owning_task_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`taskwork_id`),
  KEY `FK_TASK_WORK_TO_USER` (`user_id`),
  KEY `FK_TASK_WORK_TO_TASK` (`owning_task_id`),
  CONSTRAINT `FK_TASK_WORK_TO_TASK` FOREIGN KEY (`owning_task_id`) REFERENCES `task` (`task_id`),
  CONSTRAINT `FK_TASK_WORK_TO_USER` FOREIGN KEY (`user_id`) REFERENCES `user_table` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


