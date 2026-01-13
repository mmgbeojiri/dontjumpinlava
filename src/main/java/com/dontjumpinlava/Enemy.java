package com.dontjumpinlava;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

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
}