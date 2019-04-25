# -- create user table

# --- !Ups

CREATE USER viewer PASSWORD 'viewer';
GRANT SELECT ON MARKETING_PROMOTIONS TO viewer;

# --- !Downs

DROP USER 'viewer'
