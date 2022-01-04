#!/usr/bin/env bash

echo "--- Building vars-kb-server (reminder: run docker login first!!)"

BUILD_DATE=`date -u +"%Y-%m-%dT%H:%M:%SZ"`
VCS_REF=`git tag | sort -V | tail -1`

sbt pack && \
    docker build --build-arg BUILD_DATE=$BUILD_DATE \
                 --build-arg VCS_REF=$VCS_REF \
                  -t mbari/vars-kb-server:${VCS_REF} \
                  -t mbari/vars-kb-server:latest . && \
    docker push mbari/vars-kb-server


# sbt pack && \
#     docker buildx build \
#         --platform linux/amd64,linux/arm64 \
#         --push \
#         -t mbari/vars-kb-server:${VCS_REF} \
#         -t mbari/vars-kb-server:latest . 

# For M1 use:
# docker buildx build --load -t mbari/vars-kb-server:${VCS_REF} -t mbari/vars-kb-server:latest .
