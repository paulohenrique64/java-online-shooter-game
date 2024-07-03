#!/bin/bash

# create a named pipe
mkfifo pipe4

# init spring server
cd spring-boot-backend/
./mvnw package 
cd target/
java -jar *jar > pipe4 &
SPRINGBOOT_PID=$! # save process PID

echo "backend running on http://localhost:8080/transport"
echo "press ctrl + c to stop the backend and frontend"
echo $SPRINGBOOT_PID

function kill_processes() {
    kill $SPRINGBOOT_PID
    echo "... killing" $SPRINGBOOT_PID
}

# stop both servers
trap kill_processes SIGINT SIGTERM

wait