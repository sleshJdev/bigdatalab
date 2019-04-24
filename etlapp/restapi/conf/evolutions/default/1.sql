# -- create user table

# --- !Ups

CREATE TABLE IF NOT EXISTS appuser
(
  id       LONG PRIMARY KEY AUTO_INCREMENT,
  login    VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(50) NOT NULL,
);

# --- !Downs

DROP TABLE IF EXISTS appuser;

