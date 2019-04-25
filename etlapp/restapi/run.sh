#!/usr/bin/env bash

sbt dist
docker build -t bigdatalab:restapi .
docker run --rm -ti --name dev_bigdatalabrestapi -p 9000:9000 -p 9902:9902 -p 8802:8802 bigdatalab:restapi