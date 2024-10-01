
ReBoggled (development) is deployed on my linux box at reboggled.duckdns.org

Due to some of the quirks of my verizon issued router, services will be using non-conventional 
ports as exposed through the router. The actual services run on defaults but the port forwarding
maps to different public ports.

sshd    : 2222 (22)
mariadb : 3333 (3306)
tomcat  : 8085 (8080)

When accessing the server, use the LHS ports.
So for example to access the website we go to `http://reboggled.duckdns.org:8085`

# Accessing the development server

This box has a developer account reboggled-dev which launches
the spring runtime.

You access this account through public key authentication.

Please ask me to email you a copy of the private key associated with this 
account.

## SQL

To facilitate remote deployment and database management a service account is
setup for the development database. The account is reboggled_dev_sa@'%'.
Currently, this account is locked by password. Please contact me for this
credential. Note the non-standard port 3333 used. Add -P 3333 to your connection config.

# Deploying

Deployment to the dev server is a simple bash script that will
build the maven artifact, copy it to the server. Nuke and rebuild the DB
and populate development data. Finally, it will launch the spring runtime
in tmux.

```bash
cd <ReBoggled-dir>
./deploy/deploy.sh
```

## SSH

The deployment script uses ssh and it's tool-set. The script does not specify where
to find the private key for your connection. You must setup an entry in your ssh config
to ensure the program calls can resolve properly. Here is an example config entry.

```conf
Host reboggled.duckdns.org
    HostName reboggled.duckdns.org
    IdentityFile ~/.ssh/reboggled_dev_id_rsa
    IdentitiesOnly yes
    User reboggled-dev
    Port 2222
```

The crucial point here is making sure IdentityFile points to the correct location.

## SQL Auth

As mentioned before the service account is password protected. During the deployment
you will be prompted to enter this password.

## TMUX 

For the uninitiated, tmux is a multiplexer/session manager for the shell.
To access the session started by the deployment script.

1. ssh into the server 
    ```bash
    ssh reboggled-dev@reboggled.duckdns.org
    ```
2. attach to the session
    ```bash
    tmux attach
    ```
* to keep the instance running <C-B><C-D> to detach from the session
which will persist when the remote shell terminates.
