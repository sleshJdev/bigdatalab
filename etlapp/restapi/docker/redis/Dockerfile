FROM redis as import

EXPOSE 6379

COPY redis-dump.csv /

RUN nohup bash -c "redis-server" & sleep 5s \
        && cat /redis-dump.csv | redis-cli --pipe \
        && redis-cli shutdown save \
        && mkdir /dump \
        && cp /data/* /dump/

FROM redis

COPY --from=import /dump /data