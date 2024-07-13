import { Weapon } from "./weapon.js";

export class Player {
    constructor(app, positionWall) {
        this.app = app;
        this.positionWall = positionWall;
        this.speedPlayer = 5;
        this.keys = {};
        this.setup();
        this.setupKeyboard();
        this.gameLoop();
        this.weaponUse = 'none';
    }

    setup() {
        const texture = PIXI.Texture.from('static/imagens/player.png');
        this.sprite = new PIXI.Sprite(texture);
        this.sprite.anchor.set(0.5);
        this.sprite.x = this.app.renderer.width / 2;
        this.sprite.y = (this.app.renderer.height / 2) - 45;

        this.weapon = new Weapon(this.app, 'static/imagens/rifle.png', 10, 20, 10, this.positionWall);
        this.weapon.position(this.sprite.x, this.sprite.y);
        this.weapon.addToStage();
    }

    setupKeyboard() {
        window.addEventListener('keydown', (key) => this.keyDown(key));
        window.addEventListener('keyup', (key) => this.keyUp(key));
    }

    keyDown(key) {
        this.keys[key.keyCode] = true;
    }

    keyUp(key) {
        this.keys[key.keyCode] = false;
    }

    move(x, y) {
        this.sprite.x += x;
        this.sprite.y += y;

        this.checkPosition();

        this.weapon.position(this.sprite.x, this.sprite.y);
    }

    checkPosition() {
        const antX = this.sprite.x;
        const antY = this.sprite.y;
        const playerSize = 30;
        const tileSize = 110;
    
        for (let i = 0; i < this.positionWall.length; i++) {
            const wall = this.positionWall[i];
    
            const playerLeft = this.sprite.x - playerSize / 2;
            const playerRight = this.sprite.x + playerSize / 2;
            const playerTop = this.sprite.y - playerSize / 2;
            const playerBottom = this.sprite.y + playerSize / 2;
    
            const wallLeft = wall.x;
            const wallRight = wall.x + tileSize;
            const wallTop = wall.y;
            const wallBottom = wall.y + tileSize;
    
            if (playerRight > wallLeft && playerLeft < wallRight &&
                playerBottom > wallTop && playerTop < wallBottom) {
            
                const overlapX = Math.min(playerRight - wallLeft, wallRight - playerLeft);
                const overlapY = Math.min(playerBottom - wallTop, wallBottom - playerTop);
    
                if (overlapX < overlapY) {
                    if (playerRight > wallLeft && antX < wall.x) {
                        this.sprite.x = wallLeft - playerSize / 2;
                    } else if (playerLeft < wallRight && antX > wall.x) {
                        this.sprite.x = wallRight + playerSize / 2;
                    }
                } else {
                    if (playerBottom > wallTop && antY < wall.y) {
                        this.sprite.y = wallTop - playerSize / 2;
                    } else if (playerTop < wallBottom && antY > wall.y) {
                        this.sprite.y = wallBottom + playerSize / 2;
                    }
                }
            }
        }
    }

    gameLoop() {
        this.app.ticker.add(() => {
            if (this.keys["87"]) { // W key
                this.move(0, -this.speedPlayer);
            }
            if (this.keys["83"]) { // S key
                this.move(0, this.speedPlayer);
            }
            if (this.keys["65"]) { // A key
                this.move(-this.speedPlayer, 0);
            }
            if (this.keys["68"]) { // D key
                this.move(this.speedPlayer, 0);
            }
        });
    }
}
