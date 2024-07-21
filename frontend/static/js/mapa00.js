import { Player } from './player.js';

// game
const tileSize = 110;
let app;
let player;
let localPlayerList = [];
let userdata = undefined;

// websockets
var stompClient = Stomp.client('ws://localhost:8080/socket');

// keyboard keys
let keys = {};

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
        // GAME LOGIC
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

                // update all players positions
                for (let j = 0; j < localPlayerList.length; j++) {
                    if (localPlayerList[j].username === playerList[i].username) {
                        include = true;
                        localPlayerList[j].setPosition(playerList[i].x, playerList[i].y);
                        localPlayerList[j].weapon.sprite.rotation = playerList[i].weapon.angle;
                    }
                }

                // create players
                if (!include) {
                    player = new Player(playerList[i].username, app, gameData.room.gameMap.positionWall);
                    player.setInitialPosition(playerList[i].x, playerList[i].y);
                    localPlayerList.push(player);
                    app.stage.addChild(player.sprite);
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

        //
        // WEBSOCKETS
        //

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
                    let selfPlayer = getSelfPlayer();
                    selfPlayer.weapon.app.renderer.plugins.interaction.on('mousemove', selfPlayer.weapon.onMouseMove.bind(selfPlayer.weapon));
                })
        });

        stompClient.subscribe('/log/game-data', function (response) {
            let gameData = JSON.parse(response.body)
            updateGame(gameData)
        });

        // starting game
        stompClient.send("/app/start-game", {}, JSON.stringify({}));

        //
        // player and keyboard config
        //

        window.addEventListener('keydown', (key) => keyDown(key));
        window.addEventListener('keyup', (key) => keyUp(key));

        function keyDown(key) {
            keys[key.keyCode] = true;
        }
    
        function keyUp(key) {
            keys[key.keyCode] = false;
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
        // weapon and mouse config
        //

        document.querySelector(".gameDiv").addEventListener('mousedown', () => {
            console.log("Estou atirando");
            stompClient.send("/app/weapon-movement", {}, JSON.stringify({angle: getSelfPlayer().weapon.sprite.rotation}));
        });

        document.querySelector(".gameDiv").addEventListener('mouseup', () => {
            console.log("Parei de atirar");
            console.log(getSelfPlayer());
        });
        document.querySelector(".gameDiv").addEventListener('mousemove', () => {
            console.log("Rodando mouse");
            stompClient.send("/app/weapon-movement", {}, JSON.stringify({angle: getSelfPlayer().weapon.sprite.rotation}));
        });
    });
}

loadUserData()
connect();
