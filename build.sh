#!/usr/bin/env bash

sbt pack

docker build -t hohonuuli/vars-kb-server . && \
  docker push hohonuuli/vars-kb-server
