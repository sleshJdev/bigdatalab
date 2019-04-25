FROM openjdk:11.0.3-jdk

RUN apt-get install -y unzip

EXPOSE 8802
EXPOSE 9902
EXPOSE 9000

ADD ./target/universal/restapi-1.0-SNAPSHOT.zip /
RUN unzip restapi-1.0-SNAPSHOT.zip
WORKDIR /restapi-1.0-SNAPSHOT

ENTRYPOINT nohup bash -c "java -cp lib/com.h2database.h2*.jar org.h2.tools.Server -tcp -web -webAllowOthers -webPort 8802 -tcpAllowOthers -tcpPort 9902" & bin/restapi
