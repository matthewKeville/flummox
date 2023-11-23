#!/bin/bash

# if http --check-status --ignore-stdin --timeout=2.5 HEAD pie.dev/get &> /dev/null; then
#     echo 'OK!'
# else
#     case $? in
#         2) echo 'Request timed out!' ;;
#         3) echo 'Unexpected HTTP 3xx Redirection!' ;;
#         4) echo 'HTTP 4xx Client Error!' ;;
#         5) echo 'HTTP 5xx Server Error!' ;;
#         6) echo 'Exceeded --max-redirects=<n> redirects!' ;;
#         *) echo 'Other Error!' ;;
#     esac
# fi


################################################################################ 
# Return a json list of events
################################################################################ 

# http --print b GET localhost:8080/api/lobby

################################################################################ 
# Get Guest Session
################################################################################ 

# Connect to the server and get a Session (cookie)
# cookie is of the form : JSESSIONID=05C6E7FF74A007394B2974D3802FCEA0
# cookie=$(http --print h GET localhost:8080 | grep "Set-Cookie" | cut -d' ' -f2);
# echo ${cookie:0:-1}

################################################################################ 
# Get User Session
################################################################################ 

# Connect to the server and get an Authenticated User Session (cookie)
cookie=$(http --ignore-stdin --print h --form \
  POST localhost:8080/login username='matt' password='test'\
  | grep "Set-Cookie" | cut -d' ' -f2);
cookie=${cookie:0:-1}

# (sanity check) hit an authenticated endpoint
http --ignore-stdin GET localhost:8080/secret.html Cookie:${cookie}

