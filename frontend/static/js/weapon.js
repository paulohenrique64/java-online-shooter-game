export class Weapon {
    constructor(app, texturePath, damage, speedBulet, waitToFire) {
        this.app = app;
        this.bulletSpeed = speedBulet;
        this.damage = damage;
        this.bullets = [];
        this.sprite = new PIXI.Sprite(PIXI.Texture.from(texturePath));
        this.sprite.anchor.set(0.5);
        this.setupEvents();
        this.app.ticker.add(this.gameLoop.bind(this));
        this.isFiring = false;
        this.fireRate = waitToFire;
        this.framesSinceLastFire = 0;
    }

    setupEvents() { }

    gameLoop() { }

    position(playerX, playerY) {
        this.sprite.x = playerX;
        this.sprite.y = playerY;
    }

    onMouseMove(event) {
        const mousePosition = event.data.global;
        const dx = mousePosition.x - this.sprite.x;
        const dy = mousePosition.y - this.sprite.y;
        const angle = Math.atan2(dy, dx);
        this.sprite.rotation = angle;
    }

    addToStage() {
        this.app.stage.addChild(this.sprite);
    }

    removeFromStage() {
        this.app.stage.removeChild(this.sprite);
    }
}
