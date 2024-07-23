import { Weapon } from "./weapon.js";

export class Player {
    constructor(username, app) {
        this.username = username;
        this.app = app;
        this.speedPlayer = 5;
        this.weaponUse = 'none';
        this.setup();
        this.app.ticker.add(this.gameLoop.bind(this));
        this.alive = true;
    }

    setInitialPosition(x, y) {
        this.sprite.x = x;
        this.sprite.y = y;
        this.weapon.position(x, y);
    }

    setup() {
        const texture = PIXI.Texture.from('static/imagens/player.png');
        this.sprite = new PIXI.Sprite(texture);
        this.sprite.anchor.set(0.5);

        this.weapon = new Weapon(this.app, 'static/imagens/rifle.png', 10, 20, 10);
        this.weapon.position(this.sprite.x, this.sprite.y);
        this.weapon.addToStage();

        // player location on spawn
        // let this positions random after
        this.setInitialPosition(this.app.renderer.width / 2, (this.app.renderer.height / 2) - 45);
    }

    setPosition(x, y) {
        this.sprite.x = x;
        this.sprite.y = y;
        this.weapon.position(this.sprite.x, this.sprite.y);
    }

    gameLoop() { }

    delete() {
        this.sprite.destroy();
        this.weapon.sprite.destroy();
        this.weapon.removeFromStage();
    }
}
