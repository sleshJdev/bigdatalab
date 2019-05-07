#!/usr/bin/env bash

while :; do
    sleep 10s;
    curl "http://localhost:9889/v1/orders/generate?exception=OrderError&latency=3";
done