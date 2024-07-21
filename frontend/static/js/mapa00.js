import { Player } from './player.js';

// game
const tileSize = 110;
// let wallPosition = []; // Corrigido para inicializar como um array vazio []
let app;
let player;
let localPlayerList = [];

// websockets
var stompClient = null;
var userdata = null;

// keyboard keys
let keys = {};

// connecting to web sockets server "routes"
function connect() {
    stompClient = Stomp.client('ws://localhost:8080/socket');

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

        function updateGame(playerList) {
            var size = Object.keys(playerList).length;

            for (let i = 0; i < size; i++) {
                let include = false;

                // update all players positions
                for (let j = 0; j < localPlayerList.length; j++) {
                    if (localPlayerList[j].username === playerList[i].username) {
                        include = true;
                        localPlayerList[j].setPosition(playerList[i].x, playerList[i].y);
                    }
                }

                // create players
                if (!include) {
                    player = new Player(playerList[i].username, app);
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
            console.log("============== MAP GAME ===============");
            console.log(mapGame);
                      
            // receive mapGame from server
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

            updateGame(gameData.room.playerList);
        });

        stompClient.subscribe('/log/game-data', function (response) {
            let gameData = JSON.parse(response.body)
            updateGame(gameData.room.playerList)
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
        });
        document.querySelector(".gameDiv").addEventListener('mouseup', () => {
            console.log("Parei de atirar");
        });

        // this.app.renderer.plugins.interaction.on('mousemove', this.onMouseMove.bind(this));


    });
}

connect();
