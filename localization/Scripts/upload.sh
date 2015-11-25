#!/bin/bash

if [ "$1" = "" ] ; then 
   echo Usage: $0 FILENAME ...
   exit 1
fi

CREDS="admin:admin"
SERVER="http://localhost:8080"

for file in $@ ; do
    echo "UPLOADING $file ..."
    curl -X POST -H "Content-type: multipart/form-data" -u ${CREDS} -F "file=@$file" $SERVER/installed/upload
    echo ""
done
exit 0




# curl -i -X POST -H "Content-type: multipart/form-data" -u ${CREDS} -F "file=@$1" $SERVER/installed/upload

