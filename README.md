# vars-kb-server

![MBARI logo](src/site/resources/images/logo-mbari-3b.png)

This vars-kb-server is REST service used by a variety of applications at [MBARI](https://www.mbari.org/) that need consistent names for animals, equipment, geology, and other objects of interest. It structures the terms in a hierarchy, allowing groups to model phylogentic trees or other parent-child relationships.

User documentation is at <https://docs.mbari.org/vars-kb-server/>.

## Development

### Getting Started

You will need the following installed to build the vars-kb-server from source:

- [SBT](http://www.scala-sbt.org/)
- [Java 17](https://jdk.java.net/17/)

In addition you will need to provide a GitHub access token to access [MBARI's Maven repository on GitHub](https://github.com/mbari-org/maven). Follow the instructions at <https://github.com/mbari-org/maven> to generate a token. Then export it as an environment variable:

```bash
export GITHUB_USERNAME 'your github username'
export GITHUB_TOKEN 'your github token'
```

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
