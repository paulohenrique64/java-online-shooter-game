FROM nginx
WORKDIR /app

COPY ./game.html ./game.html
COPY ./home.html ./home.html
COPY ./lobby.html ./lobby.html
COPY ./login.html ./login.html
COPY ./register.html ./register.html

COPY ./static ./static
COPY ./static/js/lobby.js ./static/js/lobby.js 
COPY ./static/js/game.js ./static/js/game.js 


COPY ./nginx.conf /etc/nginx/nginx.conf

RUN nginx