DROP TABLE IF EXISTS `schedule`;
CREATE TABLE `schedule` (
  `id`   BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `creator_name` VARCHAR(255) NOT NULL,
  `event_type` VARCHAR(255) NOT NULL,
  `event_target` VARCHAR(255) NOT NULL,
  `event_time` timestamp NOT NULL,
  PRIMARY KEY (`id`, `event_time`),
  UNIQUE (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
