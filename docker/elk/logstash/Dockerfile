ARG ELK_VERSION

# https://github.com/elastic/logstash-docker
FROM docker.elastic.co/logstash/logstash-oss:${ELK_VERSION}

# Add your logstash plugins setup here
RUN logstash-plugin install logstash-input-beats

COPY ./mysql-connector-java-8.0.11.jar /