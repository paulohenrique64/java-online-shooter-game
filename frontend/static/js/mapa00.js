import { Player } from './player.js';

const tileSize = 110;
let wallPosition = []; // Corrigido para inicializar como um array vazio []
let app;
let player;

window.onload = function() {
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

    const mapGame = [
        [1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
        [1, 0, 0, 0, 0, 0, 0, 0, 0, 1],
        [1, 0, 1, 1, 0, 0, 1, 1, 0, 1],
        [1, 0, 0, 0, 0, 0, 0, 0, 0, 1],
        [1, 0, 1, 0, 1, 1, 0, 1, 0, 1],
        [1, 0, 0, 1, 0, 0, 0, 0, 0, 1],
        [1, 0, 0, 0, 0, 0, 1, 0, 0, 1],
        [1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
    ];

    for (let i = 0; i < mapGame.length; i++) {
        for (let j = 0; j < mapGame[0].length; j++) {
            if (mapGame[i][j] === 1) {
                const tileSprite = new PIXI.Graphics();
                tileSprite.beginFill(0x000000);
                tileSprite.drawRect(0, 0, tileSize, tileSize);
                tileSprite.endFill();
                tileSprite.x = j * tileSize;
                tileSprite.y = i * tileSize;

                wallPosition.push({ x: tileSprite.x, y: tileSprite.y });

                app.stage.addChild(tileSprite);
            }
        }
    }

    player = new Player(app, wallPosition);
    app.stage.addChild(player.sprite);

};
