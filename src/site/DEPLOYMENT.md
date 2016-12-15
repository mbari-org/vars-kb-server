# How to deploy (at MBARI)

To deploy do the following:

## On development computer:

Build and deploy a new docker image:

```
sbt pack
docker build -t hohonuuli/vars-kb-server .
docker login --username=hohonuuli
docker push hohonuuli/vars-kb-server
```

## On Portal

```
docker pull hohonuuli/vars-kb-server
sudo /usr/bin/systemctl restart docker.vars-kb-server
```

# Setup for deployment

This has already been done, but these notes are for reference:

1. Copy `docker.vars-kb-server.service` onto portal at `/etc/systemd/system`. I did it as myself (brian) and did not have to monkey with file permissions at all.
2. Run a test using `/usr/bin/systemctl start docker.vars-kb-server` and verify that it works.
3. Enable it with `/usr/bin/systemctl enable docker.vars-kb-server`. You can verify the status using `/usr/bin/systemctl status docker.vars-kb-server`