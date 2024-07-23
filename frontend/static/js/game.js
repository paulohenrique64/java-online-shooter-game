import { Player } from './player.js';

// websockets
var stompClient = Stomp.client('ws://localhost:8080/socket');

// keyboard keys
let keys = {};

// game
const tileSize = 110;
let app;
let userdata = undefined;
let localPlayerList = [];
let bullets = [];
let positionWall;


function loadUserData() {
    const options = {
        credentials: 'include',
        method: "GET",
    };

    fetch('http://localhost:8080/userdata', options)
        .then(response => {
           response.json()
            .then(responseJson => {
                userdata = responseJson.user;
            })
            .catch(error => {
                console.log(error);
            })
        })
        .catch(error => {
            console.log(error);
        })
}

// connecting to web sockets server "routes"
function connect() {
    stompClient.connect({}, function (frame) {
        //
        //
        // game map render and some configurations
        //
        //

        app = new PIXI.Application({
            width: window.innerWidth,
            height: window.innerHeight,
            backgroundColor: 0xDDDDDD
        });
    
        document.querySelector(".gameDiv").appendChild(app.view);
    
        app.loader.baseUrl = 'static/imagens';
        app.loader.add("sprite01", "player.png")
                  .add("sprite02", "rifle.png")
                  .add("sprite03", "bullet.png");
    
        app.loader.onProgress.add(showProgress);
        app.loader.onComplete.add(doneLoading);
        app.loader.onError.add(reportError);
    
        app.loader.load();
    
        function showProgress(e) {
            console.log(e.progress);
        }

        function reportError(e)  {
            console.error("ERROR: "+ e.message);
        }

        function doneLoading(e) {
            console.log("CONCLUIMOS")
        }

        //
        //
        // game client logic and websockets configurations and implementations
        //
        //

        app.ticker.add(gameLoop);

        function getSelfPlayer() {
            for (let j = 0; j < localPlayerList.length; j++) 
                if (localPlayerList[j].username === userdata.name) 
                    return localPlayerList[j];
        }

        async function updateGame(gameData) {
            let playerList = gameData.room.playerList;
            var size = Object.keys(playerList).length;

            for (let i = 0; i < size; i++) {
                let include = false;

                // update all players data
                for (let j = 0; j < localPlayerList.length; j++) {
                    if (localPlayerList[j].username === playerList[i].username) {
                        include = true;
                        localPlayerList[j].setPosition(playerList[i].position.x, playerList[i].position.y);
                        localPlayerList[j].weapon.sprite.rotation = playerList[i].weapon.angle;
                        localPlayerList[j].alive = playerList[i].alive;
                    }
                }

                // create players
                if (!include) {
                    let player = new Player(playerList[i].username, app);
                    player.setInitialPosition(playerList[i].position.x, playerList[i].position.y);
                    localPlayerList.push(player);
                    app.stage.addChild(player.sprite);
                }
            }

            // delete death players
            for (let i = 0; i < localPlayerList.length; i++) {
                if (!playerList[i].alive) {
                    console.log(`THE PLAYER ${localPlayerList[i].username} DIED`);
                    // localPlayerList[i].delete();
                    // localPlayerList.splice(i, i);
                    // console.log(localPlayerList.length);
                }
            }

            // delete inactive players
            if (localPlayerList.length !== size) {
                for (let i = 0; i < localPlayerList.length; i++) {
                    let exist = false;
    
                    for (let j = 0; j < size; j++) {
                        if (localPlayerList[i].username === playerList[j].username) {
                            exist = true;
                        }
                    }
    
                    if (!exist) {
                        console.log(`THE PLAYER ${localPlayerList[i].username} DISCONNECTED`);
                        localPlayerList[i].delete();
                        localPlayerList.splice(i, i);
                        console.log(localPlayerList.length);
                    }
                }
            }
        }

        stompClient.subscribe('/log/start-game', function (response) {
            let gameData = JSON.parse(response.body)
            let mapGame = gameData.room.gameMap.map;

            // receive mapGame from server
            // console.log("============== MAP GAME ===============");
            // console.log(mapGame); 

            for (let i = 0; i < mapGame.length; i++) {
                for (let j = 0; j < mapGame[0].length; j++) {
                    if (mapGame[i][j] === 1) {
                        const tileSprite = new PIXI.Graphics();
                        tileSprite.beginFill(0x000000);
                        tileSprite.drawRect(0, 0, tileSize, tileSize);
                        tileSprite.endFill();
                        tileSprite.x = j * tileSize;
                        tileSprite.y = i * tileSize;
                        app.stage.addChild(tileSprite);
                    }
                }
            }

            updateGame(gameData)
                .then(() => {
                    positionWall = gameData.room.gameMap.positionWall;
                    let selfPlayer = getSelfPlayer();
                    selfPlayer.weapon.app.renderer.plugins.interaction.on('mousemove', selfPlayer.weapon.onMouseMove.bind(selfPlayer.weapon));
                })
        });

        stompClient.subscribe('/log/game-data', function (response) {
            let gameData = JSON.parse(response.body)
            updateGame(gameData)
        });

        stompClient.subscribe('/log/fire', function (response) {
            let fireData = JSON.parse(response.body);
            let bullet = fireData.bullet;
            fire(bullet.position.x, bullet.position.y, bullet.angle, bullet.speed);
        });

        // starting game
        stompClient.send("/app/start-game", {}, JSON.stringify({}));

        //
        //
        // player movement and keyboard config
        //
        //

        window.addEventListener('keydown', (key) => keyDown(key));
        window.addEventListener('keyup', (key) => keyUp(key));

        function keyDown(key) {
            keys[key.keyCode] = true;
        }
    
        function keyUp(key) {
            keys[key.keyCode] = false;
        }

        let i = 0;
        while (localPlayerList.length === 2) {
            console.log(i + 1);
            i = i + 1;
        }

        app.ticker.add(() => {
            if (keys["87"]) { 
                // W key
                stompClient.send("/app/player-position", {}, JSON.stringify({key: 'u'}));
            }
            if (keys["83"]) { 
                // S key
                stompClient.send("/app/player-position", {}, JSON.stringify({key: 'd'}));
            }
            if (keys["65"]) { 
                // A key
                stompClient.send("/app/player-position", {}, JSON.stringify({key: 'l'}));
            }
            if (keys["68"]) { 
                // D key
                stompClient.send("/app/player-position", {}, JSON.stringify({key: 'r'}));
            }
        });

        //
        //
        // weapon, shooter and mouse config
        //
        //

        document.querySelector(".gameDiv").addEventListener('mousedown', () => {
            getSelfPlayer().weapon.isFiring = true;
            console.log("firing");
        });

        document.querySelector(".gameDiv").addEventListener('mouseup', () => {
            getSelfPlayer().weapon.isFiring = false;
            console.log("stopped fire");
        });

        document.querySelector(".gameDiv").addEventListener('mousemove', () => {
            stompClient.send("/app/weapon-movement", {}, JSON.stringify({angle: getSelfPlayer().weapon.sprite.rotation}));
            console.log("rotating mouse");
        });

        function fire(tipX, tipY, angle, speed) {
            const createBullet = () => {
                let bullet = new PIXI.Sprite.from("static/imagens/bullet.png");
                let selfPlayer = getSelfPlayer();

                bullet.anchor.set(0.5);

                // const angle = selfPlayer.weapon.sprite.rotation;
                // const tipX = selfPlayer.sprite.x + Math.cos(angle) * selfPlayer.sprite.height;
                // const tipY = selfPlayer.sprite.y + Math.sin(angle) * selfPlayer.sprite.height;

                bullet.x = tipX;
                bullet.y = tipY;
                bullet.rotation = angle;
                bullet.speed = speed;
                app.stage.addChild(bullet);
                return bullet;
            };
            
            let bullet = createBullet();
            bullets.push(bullet);
        }

        function gameLoop() {
            let selfPlayer = getSelfPlayer();

            if (!selfPlayer.alive) 
                stompClient.send("/app/respawn", {}, JSON.stringify({}));

            if (selfPlayer) {
                const angle = selfPlayer.weapon.sprite.rotation;

                if (selfPlayer.weapon.isFiring && selfPlayer.weapon.framesSinceLastFire >= selfPlayer.weapon.fireRate) {
                    stompClient.send("/app/fire", {}, JSON.stringify({x: selfPlayer.sprite.x + Math.cos(angle) * selfPlayer.sprite.height, y: selfPlayer.sprite.y + Math.sin(angle) * selfPlayer.sprite.height, angle: selfPlayer.weapon.sprite.rotation}));
                    selfPlayer.weapon.framesSinceLastFire = 0;
                }
    
                selfPlayer.weapon.framesSinceLastFire += 1;
    
                for (let i = 0; i < bullets.length; i++) {       
                    bullets[i].x += Math.cos(bullets[i].rotation) * bullets[i].speed;
                    bullets[i].y += Math.sin(bullets[i].rotation) * bullets[i].speed;
    
                    if (!checkBulletPlayerCollision(bullets[i]))
                        checkBulletMapCollision(bullets[i]);
                }
            }
        }

        function checkBulletMapCollision(bullet) {
            const bulletSize = 9;
            const tileSize = 110;

            if (bullet.x < 0 || bullet.x > app.renderer.width ||
                bullet.y < 0 || bullet.y > app.renderer.height) {
                app.stage.removeChild(bullet);
                bullets.splice(bullets.indexOf(bullet), 1);
                return true;
            } 
    
            for (let i = 0; i < positionWall.length; i++) {
                const wall = positionWall[i];
        
                const bulletLeft = bullet.x - bulletSize / 2;
                const bulletRight = bullet.x + bulletSize / 2;
                const bulletTop = bullet.y - bulletSize / 2;
                const bulletBottom = bullet.y + bulletSize / 2;
        
                const wallLeft = wall.x;
                const wallRight = wall.x + tileSize;
                const wallTop = wall.y;
                const wallBottom = wall.y + tileSize;
        
                if (bulletRight > wallLeft && bulletLeft < wallRight &&
                    bulletBottom > wallTop && bulletTop < wallBottom) {
                        app.stage.removeChild(bullet);
                        bullets.splice(bullets.indexOf(bullet), 1);
                        return true;
                }
            }

            return false;
        }

        function checkBulletPlayerCollision(bullet) {
            const bulletSize = 9;
            const playerSize = 35;
    
            for (let i = 0; i < localPlayerList.length; i++) {
                const player = localPlayerList[i];
        
                const bulletLeft = bullet.x - bulletSize / 2;
                const bulletRight = bullet.x + bulletSize / 2;
                const bulletTop = bullet.y - bulletSize / 2;
                const bulletBottom = bullet.y + bulletSize / 2;
        
                const playerLeft = player.sprite.x - playerSize / 2;
                const playerRight = player.sprite.x + playerSize / 2;
                const playerTop = player.sprite.y - playerSize / 2;
                const playerBottom = player.sprite.y + playerSize / 2;
        
                if (bulletRight > playerLeft && bulletLeft < playerRight &&
                    bulletBottom > playerTop && bulletTop < playerBottom) {
                        app.stage.removeChild(bullet);
                        bullets.splice(bullets.indexOf(bullet), 1);
                        return true;
                }
            }

            return false;
        }


    });
}

loadUserData();
connect();
