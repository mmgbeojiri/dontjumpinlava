package com.dontjumpinlava;
import java.io.InputStream;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public class Enemy extends Component { 
        double x; 
    double y;
    double size = 32;
    Entity imageEntity;

    double dx;
    double dy;

    double scratchX =  0.0;
    double scratchY = 0.0;

    double tileGridX = 0.0;
    double tileGridY = 0.0;

    double playerTileIndex = 0.0;
    String underTile = "";
    String shapeOfTile = "";

    int solid = 0;

    double fixdx = 0.0;
    double fixdy = 0.0;
    double modx = 0.0;
    double mody = 0.0;

    int playerframe = 0;
    double temp = 0;
    String playeraction = "";

    int falling = 99;
    int jumping = 99;

    int hitSmall = 1;

    String type = "";

    double enemyWidth = 32;
    double enemyHeight = 32;
    int spawnIndex = -1;
    
    public Enemy(double x, double y, double size, String type) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.type = type;
        this.dx = 0.5;   
    }
    @Override
    public void onAdded() {
        this.imageEntity = entity;
    }
    

    public void getTile(double x, double y) {
        tileGridX = Math.floor(x/32)+1;
        tileGridY = Math.ceil(y/32);
        playerTileIndex = -(tileGridY+1) + ((tileGridX+1)*(Globals.gridHeight));
        // the y value is flipped, so we get the next row, and subtract by tilegridy+1
        if (tileGridY < 0) {
            underTile = "Air.png";
        }

        try {
            underTile = Globals.tileGrid.get((int)playerTileIndex);
        } catch (java.lang.IndexOutOfBoundsException e) {
            underTile = "Air.png";
        }
        shapeOfTile = Globals.tileShape[Integer.valueOf(Globals.getIDfromBlock(underTile))];
        
        //System.out.println("Tile Grid X: "+tileGridX + " Tile Grid Y: "+ tileGridY + " Tile: " + underTile + "\tTile Shape: "+ shapeOfTile);
    }

    public void fixCollisionAtPoint(double x, double y, String part) {
    
        getTile(x, y);
        if (shapeOfTile.equalsIgnoreCase("")) {
            return;
        }

        modx = (32+x) % 32;
        mody = (32+y) % 32;
        
        if (shapeOfTile.equalsIgnoreCase("=")) {
            //System.out.println("Y: " + Math.floor(y) + "\tModY: " + mody + "\tFixdy: " + fixdy + "\tdy:" + Math.floor(dy));
            if ((!part.equalsIgnoreCase("feet")) || Math.ceil(mody) - this.dy < 32) { // mody - fixdy < 32 
                return;
            }

            
            
            
        }
        solid = 10;

        if (fixdy < 0) {
            this.y += -fixdy;
        
        }
        if (fixdx < 0) {
            this.x += -fixdx;
        }
        if (fixdy > 0) {
            this.y += -fixdy;
        }
        if (fixdx > 0) {
            this.x += -fixdx;
        }


    }

    public void fixCollisionInDirection(double dx, double dy) {
        solid = 0;
        fixdx = dx;
        fixdy = dy;

        
        fixCollisionAtPoint(this.x + hitSmall, this.y - hitSmall, "");
        fixCollisionAtPoint(this.x + hitSmall, this.y - Globals.playerHeight, "");
        fixCollisionAtPoint(this.x + hitSmall, this.y - (Globals.playerHeight*2)+hitSmall,"feet");
         
        fixCollisionAtPoint(this.x + (Globals.playerWidth*2)-hitSmall, this.y - 1,"");
        fixCollisionAtPoint(this.x + (Globals.playerWidth*2)-hitSmall, this.y - Globals.playerHeight,"");
        fixCollisionAtPoint(this.x + (Globals.playerWidth*2)-hitSmall, this.y - (Globals.playerHeight*2)+hitSmall,"feet");
         
    }

    public void moveSpriteX(){
        this.x += this.dx;
        fixCollisionInDirection(this.dx, 0);
        if ( solid > 0) {
            

                this.dx *= -1;

        
        }
    }
    public void moveSpriteY(){
        this.y += this.dy;
        falling += 1;
        fixCollisionInDirection(0, this.dy);
        if ( solid > 0) {
            if (this.dy < 0) {
                falling = 0;

            } else {
                jumping = 99;
            }
                this.dy = 0;

        }
    }
    public void changeImage(String texture){
        ImageView image = new ImageView();
        InputStream ris = getClass().getResourceAsStream("/assets/textures/"+texture);
        Image img = new Image(ris);
        image.setImage(img);
        image.setPreserveRatio(true);
        image.setSmooth(true);
        image.setFitWidth(size*32);
        
        //image.setFitWidth(size);
        image.toFront();
        imageEntity.getViewComponent().clearChildren();
        imageEntity.getViewComponent().addChild(image);
    }

    public void changeImage(String texture, double width){
        ImageView image = new ImageView();
        InputStream ris = getClass().getResourceAsStream("/assets/textures/"+texture);
        Image img = new Image(ris);
        image.setImage(img);
        image.setPreserveRatio(true);
        image.setSmooth(true);
        image.setFitWidth(width);
        image.setTranslateX(-(width-(size*32))/2);
        //image.setFitWidth(size);
        image.toFront();
        imageEntity.getViewComponent().clearChildren();
        imageEntity.getViewComponent().addChild(image);
    }

    public void changeImage(String texture, double width, double height){
        ImageView image = new ImageView();
        InputStream ris = getClass().getResourceAsStream("/assets/textures/"+texture);
        Image img = new Image(ris);
        image.setImage(img);
        image.setPreserveRatio(false);
        image.setSmooth(true);
        image.setFitWidth(width);
        image.setFitHeight(height);
        image.setTranslateX(-(width-(size*32))/2);
        //image.setFitWidth(size);
        image.toFront();

        
        imageEntity.getViewComponent().clearChildren();
        imageEntity.getViewComponent().addChild(image);
    }

    String costume = "EnemyStand.png";

    public void paintSprite(){
        imageEntity.setX(scratchX - Globals.cameraX);
        imageEntity.setY(scratchY + Globals.cameraY);
        //System.out.println(playeraction);

        changeImage(costume, enemyWidth, enemyHeight);
    }

    double frame = 0;

    public void moveEnemy() {
        if (this.type.equalsIgnoreCase("Squish")) {
            frame += 0.1;
            if (frame > 10) {
                imageEntity.removeFromWorld();
            }
        }
        if (this.type.equalsIgnoreCase("Op")) {
            this.dy -= 0.3;
            if (this.dy < -22) {
                this.dy = -22;
            }
            moveSpriteY();
             

            if (this.dx < 1) {
                this.dx += 0.1;
            }

            if (this.dx > -1) {
                this.dx -= 0.1;
            }
            enemyWidth = 32;
            enemyWidth = 32;
            costume = "EnemyStand.png";
            if (falling < 4) {
                enemyWidth = 32-(dy*0.5);
                enemyHeight = 32+(dy*0.5);
            }

            if (this.dx > 0) {
                costume = "EnemyRightRun.png";
                enemyWidth = 37;
            } else if (this.dx < 0) {
                costume = "EnemyLeftRun.png";
                enemyWidth = 37;
            } 
            
           
            
            moveSpriteX();
            frame += 0.25;
            paintSprite();

            if (Math.abs(Globals.playerX - this.x) < this.entityHitbox && Math.abs(Globals.playerY - this.y) < entityHitbox) {
                if (Globals.bopY > 0) {
                    type = "squish";
                    costume = "EnemySquashed.png";
                    translateDown = 16;
                    enemyWidth = 48;
                    enemyHeight = 16;
                    frame = 0;
                    Globals.bouncePlayer = 5;
                }
            }
            

            
        }


    }

    int entityHitbox = 50;
    double translateDown = 0;

    @Override
    public void onUpdate(double tpf) {
            moveEnemy();
            
              
             
            



            scratchX = this.x + Globals.width/2 - size/2 ;
            scratchY = -this.y +  Globals.height/2 - size/2 + (translateDown);

            if (!type.equals("")){
                paintSprite();
            }
        
    }
}