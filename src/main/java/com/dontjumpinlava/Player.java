package com.dontjumpinlava;
import java.io.InputStream;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public class Player extends Component {
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

    int translateDown=0;
    boolean debounce = true;
    boolean ableToWallJump = false;
    int direction = 1;

    public Player(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
     

    }
    @Override
    public void onAdded() {
        this.imageEntity = entity;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void changeX(double x) {
        this.x += x;
    }
    public void changeY(double y) {
        this.y += y;
    }
      
    public void setVelX(double dx) {
        this.dx = dx;
    }
    public void setVelY(double dy) {
        this.dy = dy;
    }
    public void changeVelX(double dx) {
        this.dx += dx;
    }
    public void changeVelY(double dy) {
        this.dy += dy;
    }
    public void changeJumping(int dj) {
        this.jumping += dj;
    }
    public void setJumping(int j) {
        this.jumping = j;
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

    void checkWallJump() {
        if (falling > 10 || jumping > 10) {
            ableToWallJump = true;
            Globals.terminalVelocity = 1;
        }
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
            if(Globals.keyDown > 0) {
                return;
            }
            
            
            
        }
        solid = 10;

        if (fixdy < 0) {
            this.y += -fixdy;
        
        }
        if (fixdx < 0) {
            this.x += -fixdx;
            checkWallJump();
        }
        if (fixdy > 0) {
            this.y += -fixdy;
        }
        if (fixdx > 0) {
            this.x += -fixdx;
            checkWallJump();
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
        if (this.dx > 0) {
            direction = 1;
        } else if (this.dx < 0) {
            direction = -1;
        };
        Globals.terminalVelocity = 22;
        ableToWallJump = false;
        fixCollisionInDirection(this.dx, 0);
        if ( solid > 0) {
                this.dx = 0;
        
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

   void debug(ImageView image) {
    System.out.println(image.getParent());
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
        //debug(image);
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
        //debug(image);
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
        //debug(image);

        
        imageEntity.getViewComponent().clearChildren();
        imageEntity.getViewComponent().addChild(image);
    }


    public void paintSprite(){
        imageEntity.setX(scratchX - Globals.cameraX);
        imageEntity.setY(scratchY + Globals.cameraY);
        //System.out.println(playeraction);

        if (ableToWallJump) {
            if (direction < 0 ) {
            changeImage("HangOn2.png", 32, 40);
            } else {
                changeImage("Hang.png", 32, 40);
            }
            return;
        }

        if (falling > 4) {
            /*
            if (this.dy > 0){
                changeImage("fall.png");
            } else {
                changeImage("fall2.png", 36);
            }  */
            

            
            changeImage("player.png", 32-(dy*0.5), 32+(dy*0.5));
        

            return;
        }

        /*
        if (Globals.keyDown > 0) {
            System.out.println("Crouch");
            translateDown = -16;
            Globals.deacceleration = 0.99;
            if (debounce) {
            this.dx += 2 * Math.signum(dx);
            debounce = false; 
            }

            changeImage("Crouch.png", 32, 16);
            return;
        } else {
            debounce = true;
            translateDown = 0;
            Globals.deacceleration = 0.4;
        }
        */

        if (playeraction.equalsIgnoreCase("turn")) {
            changeImage("player.png", 32, 32);
            return;
        }

        if ((this.dx) > 1) {
            changeImage("run.png", 35, 32);
            return;
        }

        if ((this.dx) < -1) {
            changeImage("run2.png", 35, 32);
            return;
        }

        changeImage("player.png");
    }


    @Override
    public void onUpdate(double tpf) {
        if (Globals.levelStart){
            //System.out.println("Ability to Wall Jump: " + ableToWallJump);
            //System.out.println("Direction: " + direction);
            if (!Globals.editor){
                moveSpriteX();
                moveSpriteY();       
            } 

            if (Globals.bouncePlayer > 0) {
                Globals.bouncePlayer -= 1;
                this.dy = 6;
                falling = 4;
                jumping = 1;
            }
            

            Globals.playerX = this.x;
            Globals.playerY = this.y;

            scratchX = this.x + Globals.width/2 - size/2 ;
            scratchY = -this.y +  Globals.height/2 - size/2 - translateDown;

            paintSprite();
        }
    }
}
