# -- create user table

# --- !Ups

CREATE TABLE IF NOT EXISTS appuser
(
  id       LONG PRIMARY KEY ,
  login    VARCHAR(50) UNIQUE,
  password VARCHAR(50),
);

# --- !Downs

DROP TABLE IF EXISTS appuser;

