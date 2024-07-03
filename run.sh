#!/bin/bash

# create a named pipe
mkfifo pipe4

# init angular server
cd angular-frontend/
npm install &
npm run start > pipe4 &
ANGULAR_PID=$! # save process PID

# init spring server
cd ..
cd spring-boot-backend/
./mvnw package 
cd target/
java -jar *jar > pipe4 &
SPRINGBOOT_PID=$! # save process PID

echo "backend running on http://localhost:8080/transport"
echo "frontend running on http://localhost:4200/"
echo "press ctrl + c to stop the backend and frontend"
echo $ANGULAR_PID 
echo $SPRINGBOOT_PID

function kill_processes() {
    kill $ANGULAR_PID $SPRINGBOOT_PID
    echo "... killing" $ANGULAR_PID $SPRINGBOOT_PID
}

# stop both servers
trap kill_processes SIGINT SIGTERM

wait

