#!/usr/bin/env bash

while :; do
    # count - number order to be generated, e.g. count=3
    # exception - exception message to be logged, e.g. &exception=OrderError
    # latency - response latency in seconds, e.g. &latency=3
    curl "http://localhost:9889/v1/orders/generate?count=3&exception=OrderError&latency=2";
    sleep 2s;
done