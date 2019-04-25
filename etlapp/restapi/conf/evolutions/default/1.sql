# -- create user table

# --- !Ups

CREATE TABLE IF NOT EXISTS APPUSER
(
  ID       LONG PRIMARY KEY AUTO_INCREMENT,
  LOGIN    VARCHAR(50) UNIQUE NOT NULL,
  PASSWORD VARCHAR(50) NOT NULL,
);

# --- !Downs

DROP TABLE IF EXISTS APPUSER;
