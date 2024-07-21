export class Weapon {
    constructor(app, texturePath, damage, speedBulet, waitToFire, wallsPositions) {
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
        this.wallsPositions = wallsPositions;
    }

    setupEvents() {
        document.querySelector(".gameDiv").addEventListener('mousedown', () => {
            this.isFiring = true;
        });
        document.querySelector(".gameDiv").addEventListener('mouseup', () => {
            this.isFiring = false;
        });
        this.app.renderer.plugins.interaction.on('mousemove', this.onMouseMove.bind(this));
    }

    fire() {
        const createBullet = () => {
            let bullet = new PIXI.Sprite.from("static/imagens/bullet.png");
            bullet.anchor.set(0.5);
        
            const angle = this.sprite.rotation;
            const tipX = this.sprite.x + Math.cos(angle) * this.sprite.height;
            const tipY = this.sprite.y + Math.sin(angle) * this.sprite.height;
        
            bullet.x = tipX;
            bullet.y = tipY;
            bullet.rotation = angle;
            bullet.speed = this.bulletSpeed;
            this.app.stage.addChild(bullet);
        
            return bullet;
        };
        
        let bullet = createBullet();
        this.bullets.push(bullet);
    }

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

    

    gameLoop() {
        if (this.isFiring && this.framesSinceLastFire >= this.fireRate) {
            this.fire();
            this.framesSinceLastFire = 0;
        }
    
        this.framesSinceLastFire += 1;
    
        for (let i = 0; i < this.bullets.length; i++) {
            let bullet = this.bullets[i];
            
            bullet.x += Math.cos(bullet.rotation) * bullet.speed;
            bullet.y += Math.sin(bullet.rotation) * bullet.speed;
            
            if (bullet.x < 0 || bullet.x > this.app.renderer.width ||
                bullet.y < 0 || bullet.y > this.app.renderer.height) {
    
                this.app.stage.removeChild(bullet);
                this.bullets.splice(i, 1);
                i--;
            } else {
                this.checkPosition(bullet);
            }
        }
    }

    checkPosition(bullet) {
        const bulletSize = 9;
        const tileSize = 110;
    
        // for (let i = 0; i < this.wallsPositions.length; i++) {
        //     const wall = this.wallsPositions[i];
    
        //     const bulletLeft = bullet.x - bulletSize / 2;
        //     const bulletRight = bullet.x + bulletSize / 2;
        //     const bulletTop = bullet.y - bulletSize / 2;
        //     const bulletBottom = bullet.y + bulletSize / 2;
    
        //     const wallLeft = wall.x;
        //     const wallRight = wall.x + tileSize;
        //     const wallTop = wall.y;
        //     const wallBottom = wall.y + tileSize;
    
        //     if (bulletRight > wallLeft && bulletLeft < wallRight &&
        //         bulletBottom > wallTop && bulletTop < wallBottom) {
        //             this.app.stage.removeChild(bullet);
        //             this.bullets.splice(this.bullets.indexOf(bullet), 1);
        //             break;
        //     }
        // }
    }

    addToStage() {
        this.app.stage.addChild(this.sprite);
    }

    removeFromStage() {
        this.app.stage.removeChild(this.sprite);
    }
}
