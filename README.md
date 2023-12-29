# Generating keys

Use Git bash (if on windows) to run the following:

```
openssl genrsa -out keypair.pem 2048
openssl rsa -in keypair.pem -pubout -out publickey.crt
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out pkcs8.key
```

# Docker compose

After making changes, you can rebuild and run containers with the newest changes using:

```
docker-compose up --build
```

# Env vars

Need to provide the following env vars when launching spring boot app:

```
FACEBOOK_APP_ID=
FACEBOOK_APP_CLIENT_SECRET=
```

This can also be launched as docker container. For that, need to uncomment the "web" code section in compose.yml
and create .env file with these vars in the root folder. .env.example is provided as a template
