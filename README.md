# vars-kb-server

![MBARI logo](src/site/resources/images/logo-mbari-3b.png)

This is a RESTful read-only service for retrieving information from MBARI's knowledgebase (KB). It can return:

- the hierarchy of concepts (e.g. phylogeny) found in the KB; both ancestors (up) and descendants (down) for a given concept.
- information about a specific concepts including alternate names, media and link-realizations (descriptions)
- Links (linktemplates) that can be applied to a particular concept when annotating

This server has dependencies on the [vars-kb](https://github.com/hohonuuli/vars-kb) project.

This project is built using [SBT](http://www.scala-sbt.org/) and [Java 17](https://jdk.java.net/17/)

## Development

### Getting Started

#### Build Java application

```bash
git clone https://github.com/hohonuuli/vars-kb-server
cd vars-kb-server
sbt pack
```

#### Build docker image

```bash
sbt pack && docker build -t mbari/vars-kb-server:latest .
```
