#!/usr/bin/env bash

sbt pack && \
  docker build -t mbari/vars-kb-server . && \
  docker push mbari/vars-kb-server
