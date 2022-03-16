#!/usr/bin/env bash

echo "--- Building vars-kb-server (reminder: run docker login first!!)"

VCS_REF=`git tag | sort -V | tail -1`


sbt pack \
    && docker buildx build --platform linux/amd64,linux/arm64 \
        -t mbari/vars-kb-server:${VCS_REF} \
        -t mbari/vars-kb-server:latest \
        --push . \
    && docker pull mbari/vars-kb-server:latest
