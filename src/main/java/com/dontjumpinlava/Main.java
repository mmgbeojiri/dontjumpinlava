package com.dontjumpinlava;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;


class Globals {
    public static double cameraX = 0;
    public static double cameraY = 0;

    public static int width = 960;//960
    public static int height = 540;
    public static int twoForty = (width/2)-32;
    public static int oneEighty = (height/2)-32;
    public static int cloneCountX = (int) Math.ceil(width/32)+1;

    public static int cloneCountY = (int) Math.ceil(height/32)+2;

    public static ArrayList<String> tileGrid = new ArrayList<>();
    public static int gridWidth = 100;
    public static int gridHeight = 40;

    public static int tileIndex = 0;

    public static double playerX = 0;
    public static double playerY = 0;

    public static double playerHeight = 16;
    public static double playerWidth = 16;

    public static double NEGTINY = -0.01;
    
    public static boolean editor = false;

    public static double mouseX;
    public static double mouseY;
    public static boolean mouseDown = false;
    public static boolean mousePressed = false;


    // this is the main tilegrid not player
    public static double tileGridX;
    public static double tileGridY;
    public static String chosenBrush = "Stone.png";

    public static String levelStore = "";

    public static double timer=0.0;

    public static final String atoz = "abcdefghijklmnopqrstuvwxyz";

    public static String[] blockID = {"Bedrock.png", "Air.png", "Grass.png", "Dirt.png", "CompactGrass.png", "CompactDirt.png","Stone.png","Nonsolid.png",
    "Rail.png","Water.png","WaterTop1.png","WaterTop2.png","WaterGlass.png"};

    public static int readIndex = 0;
    public static char letter; 

    public static int levelNumber = 2;

    public static Scanner Input = new Scanner(System.in); 

    public static boolean levelStart = true;

    public static boolean doneLoading = false;
}

class Block extends Component {
    double x; 
    double y;
    double size;
    int tileIndex = 0;
    Entity imageEntity;

    double scratchX =  0.0;
    double scratchY = 0.0;

    ImageView cachedImageView = null;
    String currentTextureName = null;
    InputStream is;
    ImageView imageview = new ImageView();
    Image image;
    String texName;
    



    private static final Map<String, Image> IMAGE_CACHE = new HashMap<>();

    public Block(double x, double y, double size,String image) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.tileIndex = Globals.tileIndex;
        currentTextureName = image;
        imageview.setCache(true);
        imageview.setImage(new Image("/assets/textures/" + image));
        imageview.setFitWidth(size);
        imageview.setFitHeight(size);
        
        imageview.setPreserveRatio(true);
        // set initial view node once
        this.texName = image;
        
    }

    @Override
    public void onAdded() {
        super.onAdded();
        imageEntity = getEntity(); // reliable way to get the owner entity

        /*System.out.println(
            "Tileindex: " + tileIndex + "\tX: " + x + "\tY: " + y
            +"\tGlobals.tileGridX" + (Math.floor(x/32)+1)
            +"\tGlobals.tileGridY"+ Math.ceil(y/32)
         + "\tGridHeight: " + Globals.gridHeight
        );*/

        imageEntity.getViewComponent().clearChildren();
        imageEntity.getViewComponent().addChild(imageview);
    }

    private void stupid(int ti) {
        this.tileIndex += ti;

        if (this.tileIndex < 0 || this.tileIndex >= Globals.tileGrid.size()) {
            //System.out.print(tileIndex);
            return;
        }
        
        //System.out.println("Tile Index: "+this.tileIndex);
        
        texName = Globals.tileGrid.get(this.tileIndex);
        //texName = Globals.tileGrid.get(Globals.tileIndex);

    }

    public void loopTileX(int tileSkip) {
        x += tileSkip * 32;
        Globals.tileIndex += (tileSkip * Globals.gridHeight);
        stupid(tileSkip * Globals.gridHeight);

    }

    public void loopTileY(int tileSkip) {
        y += tileSkip*32;
        Globals.tileIndex += tileSkip;
        stupid(tileSkip);
    }



    private void updateTextureIfNeeded() {
        
        if (this.tileIndex < 0 || this.tileIndex >= Globals.tileGrid.size()) {
            //System.out.print(tileIndex);
            return;
        }

        texName = Globals.tileGrid.get(this.tileIndex);
        if (texName.equalsIgnoreCase("WaterTop1.png")) {
            texName = "WaterTop" + (int) ((Math.floor(Globals.timer*4)%2)+1) + ".png";
        }

        //String texName = randomBlocks[(int) Math.floor(Math.random()*3)]; // or Globals.tileGrid.get(tileIndex);
        
        
        if (texName == null) {
            return;
        }

        if (!texName.equals(currentTextureName)) {
            //System.out.println("hello i am peter and i am green" + Math.random());
            currentTextureName = texName;

            Image img = IMAGE_CACHE.get(texName);
            if (img == null) {
                String resourcePath = "/assets/textures/" + texName;
                try (InputStream ris = getClass().getResourceAsStream(resourcePath)) {
                    if (ris == null) {
                        System.err.println("Texture not found: " + texName);
                        return;
                    }
                    // specify requested width/height to avoid oversized images in memory
                    img = new Image(ris, (int) size, (int) size, true, true);
                    IMAGE_CACHE.put(texName, img);
                } catch (Exception ex) {
                    System.err.println("Failed to load texture " + texName + ": " + ex.getMessage());
                    return;
                }
            }

            // just replace the image on the existing ImageView
            imageview.setImage(img);

            if (texName.equalsIgnoreCase("Water.png") || texName.contains("WaterTop")){
                imageview.setOpacity(0.5f);
            } else{
                imageview.setOpacity(1);
            }
            
            //System.out.println(imageview);
            imageview.toFront();

            imageEntity.getViewComponent().clearChildren();
            imageEntity.getViewComponent().addChild(imageview);

        }
        
    }

    public void doneLoading() {

        /*
        Line 464: 
        tileGridX = Math.floor(x/32)+1;
        tileGridY = Math.ceil(y/32);
        playerTileIndex = -(tileGridY+1) + ((tileGridX+1)*(Globals.gridHeight));
        // the y value is flipped, so we get the next row, and subtract by tilegridy+1

        Line 1148:
        Globals.tileGridX = Math.floor(x/32)+1;
        Globals.tileGridY = Math.ceil(y/32);


        tileIndex = ((Globals.tileGridX)*(Globals.gridHeight)) - Globals.tileGridY;
        */
        Globals.tileGridX =  Math.floor(x/32)+2;
        Globals.tileGridY =  Math.ceil(y/32)-2;
        this.tileIndex =  (int) Globals.tileGridY + ((int) (Globals.tileGridX)*( Globals.gridHeight));

        /*System.out.println(
            "NTileindex: " + this.tileIndex + "\tX: " + x + "\tY: " + y
         + "\tTile Grid X: "+ Globals.tileGridX +"\tTile Grid Y: "+ Globals.tileGridY
         + "\tGridHeight: " + Globals.gridHeight
        );*/
    }

    
    @Override
    public void onUpdate(double tpf) {


        
            if (Math.abs(x - Globals.cameraX) > (Globals.cloneCountX*16)){
                if (x < Globals.cameraX) {
                    loopTileX(Globals.cloneCountX);
                } else {
                    loopTileX(Globals.cloneCountX*-1);
                }
            }

            if (Math.abs(y + Globals.cameraY) > (Globals.cloneCountY*16)){
                if (y < -Globals.cameraY) {
                    loopTileY(Globals.cloneCountY);

                } else {
                    loopTileY(Globals.cloneCountY*-1);
                }
            }
        
        
        updateTextureIfNeeded();


        scratchX = x + Globals.width/2 - size/2 ;
        scratchY = y + Globals.height/2 - size/2;

        imageEntity.setX(scratchX - Globals.cameraX);
        imageEntity.setY(scratchY + Globals.cameraY);
    }
}

class BlockBrush extends Block {




    String tile = "";




    private static final Map<String, Image> IMAGE_CACHE = new HashMap<>();
    
    private void updateTexture(String texture) {
        imageview.setOpacity(0.5f);
        if (!Globals.editor) {
            imageview.setOpacity(0);
        }
        if (this.tileIndex < 0 || this.tileIndex >= Globals.tileGrid.size()) {
            //System.out.print(tileIndex);
            return;
        }

        texName = texture;
        //String texName = randomBlocks[(int) Math.floor(Math.random()*3)]; // or Globals.tileGrid.get(tileIndex);
        
        
        if (texName == null) {
            return;
        }

        if (!texName.equals(currentTextureName)) {
            //System.out.println("hello i am peter and i am green" + Math.random());
            currentTextureName = texName;

            Image img = IMAGE_CACHE.get(texName);
            if (img == null) {
                String resourcePath = "/assets/textures/" + texName;
                try (InputStream ris = getClass().getResourceAsStream(resourcePath)) {
                    if (ris == null) {
                        System.err.println("Texture not found: " + texName);
                        return;
                    }
                    // specify requested width/height to avoid oversized images in memory
                    img = new Image(ris, (int) size, (int) size, true, true);
                    IMAGE_CACHE.put(texName, img);
                } catch (Exception ex) {
                    System.err.println("Failed to load texture " + texName + ": " + ex.getMessage());
                    return;
                }
            }

            // just replace the image on the existing ImageView
            imageview.setImage(img);
            
            //System.out.println(imageview);
            imageview.toFront();
            
            


            imageEntity.getViewComponent().clearChildren();
            imageEntity.getViewComponent().addChild(imageview);

        }
        
    }

    public BlockBrush(double x, double y, double size) {
        super(x, y, size, Globals.chosenBrush);
        currentTextureName = Globals.chosenBrush;
        imageview.setCache(true);
        imageview.setImage(new Image("/assets/textures/" + Globals.chosenBrush));
        imageview.setFitWidth(size);
        imageview.setFitHeight(size);
        imageview.setOpacity(Globals.editor ? 0.5 : 0);
        imageview.setPreserveRatio(true);
        // set initial view node once
        this.tile = Globals.chosenBrush;
        
    }

    public void editorBrush() {
        if (Globals.editor == false || Globals.mouseDown) {
            return;
        };
        super.x = (32 * (Globals.tileGridX-2))+16;
        super.y = (-32 * (Globals.tileGridY-2))-16;
        this.tile = Globals.chosenBrush;
    }
    @Override 
    public void onAdded(){
        super.onAdded();
        imageEntity = this.entity;
    }

    @Override
    public void onUpdate(double tpf) { 
        editorBrush();
        updateTexture(tile);

        
        
        scratchX = x + Globals.width/2 - size/2 ;
        scratchY = y + Globals.height/2 - size/2;
        
        imageEntity.setX(scratchX - Globals.cameraX);
        imageEntity.setY(scratchY + Globals.cameraY);
        //System.out.println("GlobalsX: " + Globals.tileGridX + " GlobalsY: " + Globals.tileGridY + "X: " + imageEntity.getX() + "Y: " + imageEntity.getY() + "Tile: "+ tile);
    }
}


class Player extends Component {
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
        //System.out.println("Tile Grid X: "+tileGridX + " Tile Grid Y: "+ tileGridY + " Tile: " + underTile);
    }

    
    public void fixCollisionAtPoint(double x, double y) {
    
        getTile(x, y);
        if ( !underTile.equalsIgnoreCase("Air.png")) {
            solid = 10;
            modx = x % 32;
            mody = y % 32;
            
            /*if (fixdy < 0) {
                this.y += 0.01-mody;
                System.out.println(" fixdy: " + fixdy + " mody: " + mody);
                //this.y -=mody;
            }
            if (fixdx < 0) {
                this.x += 0.01-modx;
                //this.y -=mody;
            }
            if (fixdy > 0) {
                this.y += -0.01-mody;
                //this.y -=mody;
            } 

            if (fixdx > 0) {
                System.out.println(" fixdx: " + fixdx + " modx: " + modx);
                this.x += -0.01-modx;
                //this.y -=mody;
                System.out.println(underTile);
            }*/
            if (fixdy < 0) {
                this.y += -fixdy;
            }
            if(fixdx < 0) {
                this.x+= -fixdx;
            }
            if (fixdy > 0) {
                this.y += -fixdy;
            }
            if(fixdx > 0) {
                this.x+= -fixdx;
            }
        } 

        //

    }

    public void fixCollisionInDirection(double dx, double dy) {
        solid = 0;
        fixdx = dx;
        fixdy = dy;

        
        fixCollisionAtPoint(this.x + hitSmall, this.y - hitSmall);
        fixCollisionAtPoint(this.x + hitSmall, this.y - Globals.playerHeight);
        fixCollisionAtPoint(this.x + hitSmall, this.y - (Globals.playerHeight*2)+hitSmall);
         
        fixCollisionAtPoint(this.x + (Globals.playerWidth*2)-hitSmall, this.y - 1);
        fixCollisionAtPoint(this.x + (Globals.playerWidth*2)-hitSmall, this.y - Globals.playerHeight);
        fixCollisionAtPoint(this.x + (Globals.playerWidth*2)-hitSmall, this.y - (Globals.playerHeight*2)+hitSmall);
         
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


    public void paintSprite(){
        imageEntity.setX(scratchX - Globals.cameraX);
        imageEntity.setY(scratchY + Globals.cameraY);
        //System.out.println(playeraction);
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
            if (!Globals.editor){
                moveSpriteX();
                moveSpriteY();       
            } 
            

            Globals.playerX = this.x;
            Globals.playerY = this.y;

            scratchX = this.x + Globals.width/2 - size/2 ;
            scratchY = -this.y +  Globals.height/2 - size/2;

            paintSprite();
        }
    }
}

class Smoke extends Component {
    double x = 0.0;
    double y = 0.0;
    double frame = 0;
    Entity imageEntity;
    
    double scratchX = 0;
    double scratchY = 0;
    double size = 0.5;
    public void changeImage(String texture, double size){
        ImageView image = new ImageView();
        InputStream ris = getClass().getResourceAsStream("/assets/textures/"+texture);
        Image img = new Image(ris);
        image.setImage(img);
        image.setPreserveRatio(true);
        image.setSmooth(true);
        //image.setFitWidth(size*32);
        //image.setFitHeight(size*32);
        
        //image.setFitWidth(size);
        imageEntity.getViewComponent().clearChildren();
        imageEntity.getViewComponent().addChild(image);
    }
    public Smoke() {
        frame = 1;
        this.x = Globals.playerX;
        this.y = Globals.playerY - Globals.playerHeight*2;
        
    }
    
    @Override
    public void onAdded() {
        this.imageEntity = entity;
        changeImage("Smoke1.png", 1);
    };

    @Override
    public void onUpdate(double tpf) {
        if (frame == 0) {
            return;
        }

        if ((frame > 3)) {
            entity.removeFromWorld();
            return;
        }

        scratchX = this.x + Globals.width/2 ;
        scratchY = -this.y +  Globals.height/2 - (size*32)/2;

        imageEntity.setX(scratchX - Globals.cameraX);
        imageEntity.setY(scratchY + Globals.cameraY);

        changeImage("Smoke" +(int) Math.floor(frame)+".png", frame*-0.5 + 1);
            //make a function that is 1 at 0 and 0 at 2
        
        frame += 0.2;
    }
}


public class Main extends GameApplication {

    Entity player;
    
    Globals globals = new Globals();

    int keyRight = 0;
    int keyLeft = 0;
    int keyUp = 0;
    int keyDown = 0;
    int keyE = 0;

    int keyWalk = 0;

    String levelPath = "level.dat";


    double tileIndex;
    String underTile;
    

    String brush = "Air.png";
    
    public void callDoneLoadingOnAllBlocks() {
       for (Entity entity : FXGL.getGameWorld().getEntities()) {
        if (entity.hasComponent(Block.class)) {
            entity.getComponent(Block.class).doneLoading();
        // Do something with the entity or its component
            };
        };
    }
    
    
    public void addWall() {
        for (int i = 0; i < Globals.gridHeight; i++) {
            Globals.tileGrid.add("Bedrock.png");
        }
    }
    public void addBoxColumn() {
        String[] randomBlocks = {"dirt.png", "stone.png", "grass.png"};

        Globals.tileGrid.add("bedrock.png");
        for (int i = 0; i < Globals.gridHeight-2; i++) {
            
                Globals.tileGrid.add("Air.png");
            
        }
        Globals.tileGrid.add("grass.png");

    }
    public void generateNewLevel() {
        
        addWall();
        System.out.println("width:"+Globals.gridWidth);
        for (int i = 0; i < Globals.gridWidth-2; i++) {
            addBoxColumn();
        }
        addWall();

        
        
    }
    public void generateLevel() {
        Globals.editor = false;
        Globals.tileGrid.clear();
        decodeLevel(Globals.levelNumber);
        if (Globals.tileGrid.size() == 0) {
            Globals.gridWidth = 100;
            Globals.gridHeight = 40;
            generateNewLevel();
        }
    };

    

    


    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(Globals.width);
        settings.setHeight(Globals.height);
        settings.setTitle("Don't Jump in Lava");
    }

    
    UserAction rightPressed = new UserAction("Right") {
        @Override 
        protected void onAction() {keyRight = 1;}
        @Override
        protected void onActionEnd() {keyRight = 0;}
    };
    UserAction dPressed = new UserAction("D") {
        @Override 
        protected void onAction() {keyRight = 1;};
        @Override
        protected void onActionEnd() {keyRight = 0;};
    };
    UserAction leftPressed = new UserAction("Left") {
        @Override 
        protected void onAction() {keyLeft = 1;}
        @Override
        protected void onActionEnd() {keyLeft = 0;}
    };
    UserAction aPressed = new UserAction("A") {
        @Override 
        protected void onAction() {keyLeft = 1;};
        @Override
        protected void onActionEnd() {keyLeft = 0;};
    };
    UserAction upPressed = new UserAction("Up") {
        @Override 
        protected void onAction() {keyUp = 1;}
        @Override
        protected void onActionEnd() {keyUp = 0;}
    };
    UserAction wPressed = new UserAction("W") {
        @Override 
        protected void onAction() {keyUp = 1;}
        @Override
        protected void onActionEnd() {keyUp = 0;}
    };
    UserAction spacePressed = new UserAction("Space") {
        @Override 
        protected void onAction() {keyUp = 1;};
        @Override
        protected void onActionEnd() {keyUp = 0;};
    }; 
    UserAction downPressed = new UserAction("Down") {
        @Override 
        protected void onAction() {keyDown = 1;}
         @Override
        protected void onActionEnd() {keyDown = 0;}
    };
    UserAction sPressed = new UserAction("S") {
        @Override 
        protected void onAction() {keyDown = 1;}
        @Override
        protected void onActionEnd() {keyDown = 0;}
    };
    UserAction ePressed = new UserAction("E") {
        @Override 
        protected void onActionBegin() {
            if (Globals.editor) {
                Globals.levelStart = false;
                encodeLevel(Globals.levelNumber);
                Globals.levelStart = true;
            } else {
                Globals.levelStart = false;
                decodeLevel(Globals.levelNumber);
                Globals.levelStart = true;
            }
            Globals.editor = !Globals.editor;
        }
    };

    UserAction lPressed = new UserAction("L") {
        @Override 
        protected void onActionBegin() {
            int answer = 0;
            System.out.print("Enter Level Number: (Current: " + Globals.levelNumber + "): ");
            answer = Globals.Input.nextInt();
            if (answer < 1) {
                System.out.println("Please enter a number greater than 1.");
                return;
            }
            Globals.levelStart = false;
            encodeLevel(Globals.levelNumber);
            
            Globals.levelNumber = answer;
            decodeLevel(Globals.levelNumber);
            Globals.levelStart = true;
            
        }
    };

    UserAction rPressed = new UserAction("R") {
        @Override 
        protected void onActionBegin() {
            if (Globals.editor){
                System.out.println(Globals.tileGrid);
                String answer = "";
                System.out.print("Do you want to reset the level? Please exactly type: \"Yes\".");
                answer = Globals.Input.nextLine();
                if (answer.equals("Yes")) {
                    Globals.levelStart = false;


                    System.out.print("Enter Level Width (Current: " + Globals.gridWidth + ")");
                    try {
                        Globals.gridWidth = Globals.Input.nextInt();
                    } catch (Error e) {
                        Globals.gridWidth = 100;
                    }

                    System.out.print("Enter Level Height (Current: " + Globals.gridHeight + ")");
                    try {
                        Globals.gridHeight = Globals.Input.nextInt();
                    } catch (Error e) {
                        Globals.gridHeight = 40;
                    }

                    generateNewLevel();
                    callDoneLoadingOnAllBlocks();
                    Globals.levelStart = true;
                
                }
            }
            
        }
    };
    UserAction mouseClicked = new UserAction("Click") {
        @Override
        protected void onActionBegin() {Globals.mousePressed = true;}
        @Override 
        protected void onAction() {Globals.mouseDown = true;}
        @Override
        protected void onActionEnd() {Globals.mouseDown = false;}
    };

    String[] grassList = {"Grass.png", "Dirt.png", "CompactGrass.png", "CompactDirt.png"};
    String[] stoneList = {"Stone.png", "Bedrock.png", "Nonsolid.png"};
    String[] waterList = {"Water.png", "WaterTop1.png"};
    String[] misaList = {"Rail.png", "WaterGlass.png"};


    public void changeBrush(String strung) {  Globals.chosenBrush = strung; System.out.println("Set chosen brush to: " + strung);}

    public void nextBrush(int key) {
        int index = 0;
        String[] loadedList;
        switch(key) {
            case 1:
              loadedList = grassList;
              break;
            case 2: 
                loadedList = stoneList;
                break;
            case 3:
                loadedList = waterList;
                break;
            case 4:
                loadedList = misaList;
                break;
            default:
                loadedList = grassList;
                break;
        }
        try {
            index = Arrays.asList(loadedList).indexOf(Globals.chosenBrush);
        } catch(ArrayIndexOutOfBoundsException e) {
            index = 0;
        }

        try {
            changeBrush(loadedList[index+1]);
        } catch(ArrayIndexOutOfBoundsException e) {
            changeBrush(loadedList[0]);
        }
    }

    UserAction one = new UserAction("Grass") { @Override protected void onActionBegin() { nextBrush(1); } };
    UserAction two = new UserAction("Dirt") { @Override protected void onActionBegin() {nextBrush(2);}};
    UserAction three = new UserAction("Stone") { @Override protected void onActionBegin() {nextBrush(3); }};
    UserAction four  = new UserAction("Bedrock") { @Override protected void onActionBegin() {nextBrush(4);}};

    UserAction qPressed  = new UserAction("Eyedrop") { @Override protected void onActionBegin() {changeBrush(underTile);}};
    
    
    @Override
    protected void initInput() {
        Input input = FXGL.getInput();
        input.addAction(upPressed, KeyCode.UP);
        input.addAction(rightPressed, KeyCode.RIGHT);
        input.addAction(leftPressed, KeyCode.LEFT);
        input.addAction(downPressed, KeyCode.DOWN);
        
        input.addAction(wPressed, KeyCode.W);
        input.addAction(aPressed, KeyCode.A);
        input.addAction(sPressed, KeyCode.S);
        input.addAction(dPressed, KeyCode.D);

        input.addAction(ePressed, KeyCode.E);

        input.addAction(qPressed, KeyCode.Q);

        input.addAction(spacePressed, KeyCode.SPACE);

        input.addAction(mouseClicked, MouseButton.PRIMARY);

        input.addAction(one, KeyCode.DIGIT1);
        input.addAction(two, KeyCode.DIGIT2);
        input.addAction(three, KeyCode.DIGIT3);
        input.addAction(four, KeyCode.DIGIT4);

        input.addAction(lPressed, KeyCode.L);

        input.addAction(rPressed, KeyCode.R);

        
    }

    public void makeSkipSmoke() {
        player.getComponent(Player.class).playerframe += 1;
        if (player.getComponent(Player.class).playerframe %3 < 1) {
            FXGL.entityBuilder().at(
                (Globals.playerX + Globals.width/2)- Globals.cameraX, 
                (-(Globals.playerY - Globals.playerHeight*2 - (16*32)/2) + Globals.height/2)+ Globals.cameraY) // size is 16
            .view("Smoke1.png")
            .with(new Smoke())
            .buildAndAttach();
        }
    }
    public void handleGodMode() {
        player.getComponent(Player.class).changeVelX(3 * (keyRight - keyLeft));
        player.getComponent(Player.class).changeVelY(3 * (keyUp - keyDown));

        player.getComponent(Player.class).setVelX(0.7* player.getComponent(Player.class).dx);
        player.getComponent(Player.class).setVelY(0.7* player.getComponent(Player.class).dy);

        player.getComponent(Player.class).changeX(player.getComponent(Player.class).dx);
        player.getComponent(Player.class).changeY(player.getComponent(Player.class).dy);
    }

    public void handleKeysLeftRight() {
        player.getComponent(Player.class).playeraction = "walk"; 
        keyWalk = (keyRight - keyLeft);

        if (keyWalk == 0) {
            if (player.getComponent(Player.class).falling < 10) {
                            if (player.getComponent(Player.class).dx > 0.4) {
                player.getComponent(Player.class).changeVelX(-0.4);
            } else if (player.getComponent(Player.class).dx < -0.4) {
                player.getComponent(Player.class).changeVelX(0.4);
            } else {
                player.getComponent(Player.class).setVelX(
                        0
                );
                player.getComponent(Player.class).playerframe = 0;
            }
        }

        } else {
            if (keyWalk * player.getComponent(Player.class).dx < 10) {
                if (keyWalk * player.getComponent(Player.class).dx < 0) {
                player.getComponent(Player.class).changeVelX(
                        keyWalk * 0.8
                );

                if (player.getComponent(Player.class).falling < 10){
                    player.getComponent(Player.class).playeraction = "turn";
                    makeSkipSmoke();
                }

            } else {
                player.getComponent(Player.class).changeVelX(
                        keyWalk * 0.4
                );
            }

            }
        }

        player.getComponent(Player.class).temp = Math.abs(player.getComponent(Player.class).dx)/19;
        if (player.getComponent(Player.class).temp < 0.2) {
            player.getComponent(Player.class).temp = 0.2;
        }
        player.getComponent(Player.class).playerframe +=1;
    }

    public void handleKeysJump() {

        

        player.getComponent(Player.class).changeVelY(
                        -0.3
        ); 

        if (player.getComponent(Player.class).dy < -22) {
            player.getComponent(Player.class).setVelY(-22);
        }
        if (keyUp == 1) {
            if ((player.getComponent(Player.class).falling < 10)
             || (player.getComponent(Player.class).jumping > 0)) {
            
                player.getComponent(Player.class).changeJumping(1);
                if (player.getComponent(Player.class).jumping < 15) {
                    player.getComponent(Player.class).setVelY(6); 
                }
        }
        } else {
            player.getComponent(Player.class).setJumping(0);
        }

    };

    public String getTile(double x, double y) {
        Globals.tileGridX = Math.floor(x/32)+1;
        Globals.tileGridY = Math.ceil(y/32);


        tileIndex = ((Globals.tileGridX)*(Globals.gridHeight)) - Globals.tileGridY;
        // the y value is not flipped, so we get the next row, and subtract by tilegridy+1
        if (tileIndex >= 0 && tileIndex < Globals.tileGrid.size()){
            this.underTile = Globals.tileGrid.get((int) tileIndex);
        } else {
            this.underTile = "";
        }
        
        
        /*System.out.println(
            "X: " + x + " Y: " + y + 
            "\tMouse X: " + Globals.mouseX + "Mouse Y: " +Globals.mouseY+
            "\tCamera X:" + Globals.cameraX + " Camera Y: " + Globals.cameraY + 
            "\tTile Grid X: " + Globals.tileGridX + " Tile Grid Y:" + Globals.tileGridY +
            "\tUndertile: " + underTile);*/
        

        return underTile;
        //System.out.println("Tile Grid X: "+tileGridX + " Tile Grid Y"+ tileGridY + " Tile: " + underTile);
    }

    public void movePlayerEditor(){
        if (Globals.editor == false) {
            return;
        }
        
        Globals.mouseX = (double) FXGL.getInput().mouseXUIProperty().get();
        Globals.mouseY = (double) FXGL.getInput().mouseYUIProperty().get();
        getTile(Globals.mouseX + (Globals.cameraX - Globals.twoForty), Globals.height - (Globals.mouseY-(Globals.cameraY-Globals.oneEighty)));

        if (!Globals.mouseDown) {
            return;
        }
        if (Globals.mousePressed) {
            Globals.mousePressed = false;
            System.out.println("Undertile: " + underTile + "\tBrush: " + Globals.chosenBrush);
            if (underTile.equalsIgnoreCase(Globals.chosenBrush)) {
                brush = "Air.png";
            } else {
                brush = Globals.chosenBrush;
            }
        }
        try {

            Globals.tileGrid.set((int)tileIndex, brush);
            //writeLevelData();
           
        } catch (Error e) {
            e.printStackTrace();
        }
        

        



        //System.out.println("Mouse X: " + mouseX + " Mouse Y: "+ mouseY + 
        //"\tUndertile: " + getTile(mouseX+Globals.cameraX, mouseY-Globals.cameraY));
    }


    public void resetPlayer() {
        Globals.cameraX = Globals.twoForty;
        Globals.cameraY = Globals.oneEighty;
        Globals.playerHeight = 16;
        Globals.playerWidth = 16;

        player.getComponent(Player.class).x = 0;
        player.getComponent(Player.class).y = 32;
        player.getComponent(Player.class).dx = 0;
        player.getComponent(Player.class).dy = 0;
        player.getComponent(Player.class).jumping = 99;
        player.getComponent(Player.class).falling = 99;

         
        

    }
    public void checkAroundPlayer() {
        //System.out.println(player.getComponent(Player.class).y);
            if (player.getComponent(Player.class).y < 0) {
                resetPlayer();   
            }
        }
    public void movePlayer() {
        if (Globals.editor) {
            handleGodMode();
            movePlayerEditor();
        } else {
            handleKeysLeftRight();
            handleKeysJump();
            checkAroundPlayer(); 
        }
    
        moveCamera();

    }

    
    @Override
    protected void onUpdate(double tpf) {
        Globals.timer += tpf;
        if (Globals.levelStart) {
        movePlayer();
        }
        //System.out.println(1/tpf);

        //Globals.cameraX = Math.sin(memoryWaster)*100;
        //Globals.cameraY = Math.sin(memoryWaster)*100;
        //Globals.cameraY = memoryWaster*100;
    }

    protected void createBlock(double x, double y, double size, String image) {
        double scratchX = x+Globals.width/2;
        double scratchY = y+Globals.height/2;
        // Try to load the image from the FXGL assets folder: assets/textures/<image>
        try {
            String resourcePath = "/assets/textures/" + image;

            // Use getResourceAsStream so it works from both IDE and packaged jar
            java.io.InputStream is = getClass().getResourceAsStream(resourcePath);
            if (is == null) {
                throw new java.io.IOException("Resource not found: " + resourcePath);
            }

            Image playerImage = new Image(is);
            ImageView playerImageView = new ImageView(playerImage);

            // Set the desired width and height for the ImageView
            playerImageView.setFitWidth(size);
            playerImageView.setFitHeight(size);
            playerImageView.setPreserveRatio(true);

            FXGL.entityBuilder()
                .at(scratchX, scratchY)
                .view(playerImageView)
                .with(new Block(x, y, size, image))
                .buildAndAttach();

        } catch (Exception ex) {
            // Log a helpful message and fall back to a visible rectangle so the app still starts
            System.err.println("Failed to load image '" + image + "' from /assets/textures/: " + ex.getMessage());
            javafx.scene.shape.Rectangle fallback = new javafx.scene.shape.Rectangle(size, size, javafx.scene.paint.Color.BROWN);
            FXGL.entityBuilder()
                .at(scratchX, scratchY)
                .view(fallback)
                .with(new Block(x, y, size, image))
                .buildAndAttach();
        }
    }

    protected void createBlockBrush(double x, double y, double size, String image) {
        double scratchX = x+Globals.width/2;
        double scratchY = y+Globals.height/2;
        // Try to load the image from the FXGL assets folder: assets/textures/<image>
        try {
            String resourcePath = "/assets/textures/" + image;

            // Use getResourceAsStream so it works from both IDE and packaged jar
            java.io.InputStream is = getClass().getResourceAsStream(resourcePath);
            if (is == null) {
                throw new java.io.IOException("Resource not found: " + resourcePath);
            }

            Image playerImage = new Image(is);
            ImageView playerImageView = new ImageView(playerImage);

            // Set the desired width and height for the ImageView
            playerImageView.setFitWidth(size);
            playerImageView.setFitHeight(size);
            playerImageView.setPreserveRatio(true);

            FXGL.entityBuilder()
                .at(scratchX, scratchY)
                .view(playerImageView)
                .with(new BlockBrush(x, y, size))
                .buildAndAttach();

        } catch (Exception ex) {
            // Log a helpful message and fall back to a visible rectangle so the app still starts
            System.err.println("Failed to load image '" + image + "' from /assets/textures/: " + ex.getMessage());
            javafx.scene.shape.Rectangle fallback = new javafx.scene.shape.Rectangle(size, size, javafx.scene.paint.Color.BROWN);
            FXGL.entityBuilder()
                .at(scratchX, scratchY)
                .view(fallback)
                .with(new BlockBrush(x, y, size))
                .buildAndAttach();
        }
    }

    public void setup() {
        double tileX = 0.0;
        double tileY = 0.0;

        System.out.println(Globals.tileGrid.size());
        //System.out.println(Globals.tileGrid);

        tileX = -16*3;
        for (int i = 0; i < Globals.cloneCountX; i++) {
            tileY = 16*3;
            for (int j = 0; j < Globals.cloneCountY; j++) {
                createBlock(tileX, tileY, 32,
                        Globals.tileGrid.get(Globals.tileIndex) // move this to the block class
                    );
                
                
                tileY += 32;
                Globals.tileIndex += 1;
            } 
            tileX += 32;
            Globals.tileIndex += (Globals.gridHeight - Globals.cloneCountY);

        }

        createBlockBrush(tileX, tileY, 32, Globals.tileGrid.get(Globals.tileIndex)); // move this to the block class);

        


    }

    public void cloneLevelTiles() {
        setup();
    }

    public void moveCamera() {
        Globals.cameraX = Globals.playerX;
        Globals.cameraY += (Globals.playerY - Globals.cameraY)/4;

       
        if (Globals.cameraX < Globals.twoForty){ 
            Globals.cameraX = Globals.twoForty ;
        }
        if (Globals.cameraY < Globals.oneEighty){ 
            Globals.cameraY = Globals.oneEighty;
        }
        if (Globals.cameraX > ((Globals.gridWidth*(32))-Globals.width/2 - 32)) { 
            Globals.cameraX = ((Globals.gridWidth*(32))-Globals.width/2 - 32);
        }
        if (Globals.cameraY > (Globals.gridHeight*32) - (Globals.height/2) - 32) { 
            Globals.cameraY = (Globals.gridHeight*32) - (Globals.height/2) - 32 ;
        }
    }



    public void readLevelData() {
        try (FileInputStream fis = new FileInputStream(levelPath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            Globals.tileGrid = (ArrayList<String>) ois.readObject();
            ois.close();
            System.out.println("ArrayList loaded from " + levelPath);
            //System.out.println("Loaded strings: " + Globals.tileGrid);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writeLevelData() {
        ArrayList<String> existing = null;

        // Try to read existing file once
        try (FileInputStream fis = new FileInputStream(levelPath);
            ObjectInputStream ois = new ObjectInputStream(fis)) {

            Object obj = ois.readObject();
            if (obj instanceof ArrayList) {
                @SuppressWarnings("unchecked")
                ArrayList<String> list = (ArrayList<String>) obj;
                existing = list;
            } else {
                System.out.println("Unexpected object in " + levelPath);
            }
        } catch (IOException | ClassNotFoundException e) {
            // File missing/empty/corrupt -> we'll overwrite it below
            System.out.println("Read error (will overwrite): " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // Compare contents (use equals, not ==)
        if (existing != null && existing.equals(Globals.tileGrid)) {
            //System.out.println("nothing has changed, ending write process");
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(levelPath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(Globals.tileGrid);
            System.out.println("ArrayList saved to " + levelPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeValue(String value, char delimiter) {
        
        Globals.levelStore += value + delimiter;
       
    }

    public static void writeStringToFileLine(String filePath, int lineNumber, String content) throws IOException {
        if (lineNumber < 1) {
            throw new IllegalArgumentException("Line number must be 1 or greater.");
        }

        List<String> lines = new ArrayList<>();

        // Read all lines from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            // Handle file not found or other read errors (e.g., if the file doesn't exist yet)
            // For this scenario, we'll proceed with an empty list of lines
        }

        // Adjust the list to accommodate the target line number
        while (lines.size() < lineNumber) {
            lines.add(""); // Add empty lines until the target line is reached
        }

        // Replace or insert the content at the specified line number
        lines.set(lineNumber - 1, content);

        // Write all lines back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < lines.size(); i++) {
                writer.write(lines.get(i));
                if (i < lines.size() - 1) { // Add newline for all lines except the last
                    writer.newLine();
                }
            }
        }
    }

    public static String readLineFromFile(String filePath, int lineNumber) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentLineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (currentLineNumber == lineNumber) {
                    return line; // Return the desired line
                }
                currentLineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null if the line number is out of bounds or an error occurs
    }

    public String getIDfromBlock(String tile) {
        for (int i = 0; i < Globals.blockID.length; i++) {
            if (Globals.blockID[i].equalsIgnoreCase(tile)){
                return Integer.toString(i);
            }
        }

        return "@";
    }

    public String getBlockFromID(String id) {
        try {
            return Globals.blockID[Integer.parseInt(id)];
        } catch (ArrayIndexOutOfBoundsException e) { // out of bounds
            return "Air.png";
        }

        
    }


    public void encodeLevel(int levelNumber) {
        Globals.levelStore = "";

        writeValue("1", '_');
        writeValue(Integer.toString(Globals.gridWidth), '_');
        writeValue(Integer.toString(Globals.gridHeight), '_');
        
        
        int tileIndex = 0;
        int length = 0;
        String tile = Globals.tileGrid.get(tileIndex);
        String blockID = "0";
        
        for (int i =0; i< Globals.gridHeight; i++) {
            for (int j = 0; j < Globals.gridWidth; j++) {
                if (length < Globals.atoz.length() && tile.equalsIgnoreCase(Globals.tileGrid.get(tileIndex))) {
                    length++;
                } else {
                    blockID = getIDfromBlock(tile);
                    if (tile.equalsIgnoreCase("Air.png")) {
                        blockID = "";
                    }


                    writeValue(blockID, Globals.atoz.charAt(length-1));
                    //writeValue(tile, '_');
                    //writeValue(Integer.toString(length), '_');
                   
                    tile = Globals.tileGrid.get(tileIndex);
                    length = 1;
                }
                tileIndex += Globals.gridHeight;
            }
            tileIndex += 1 - (Globals.gridWidth * Globals.gridHeight);
        }
        writeValue(getIDfromBlock(tile), Globals.atoz.charAt(length-1));
        //writeValue(tile, '_');
        //writeValue(Integer.toString(length), '_');

        String savePath = "stupid.txt";

        /*try (FileOutputStream fos = new FileOutputStream(savePath)) {
            // Convert the string to bytes using UTF-8 encoding
            byte[] bytes = Globals.levelStore.getBytes(StandardCharsets.UTF_8);

            // Write the bytes to the FileOutputStream
            fos.write(bytes);

            System.out.println("String successfully written to " + savePath);

        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }*/
       try {writeStringToFileLine(savePath, levelNumber, Globals.levelStore);} catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }

        Globals.levelStore = "";




        
    }

    public char readLetter() {
        char character = ' ';
        try {
            character = Globals.levelStore.charAt(Globals.readIndex);
        } catch (StringIndexOutOfBoundsException e) {
            Globals.letter = ' ';
            e.printStackTrace();
        }

        Globals.letter = character;
        
        Globals.readIndex++;
        return character;
    }

    public String readValue() {
        String value = "";
        int ascii = (int) Globals.letter;
        readLetter();
        while (true){ // ascii value below 65 means its number
            
            ascii = (int) Globals.letter;
            
            if (ascii > 65) {
                break;
            }
            if(Globals.letter == ' ') {
                break;
            } 
            value += Globals.letter;
            readLetter();
        }

        

        return value;
    }

    public void decodeLevel(int levelNumber) {
        try {
            Globals.levelStore = readLineFromFile("stupid.txt", levelNumber);

            if (Globals.levelStore == null) {
                Globals.tileGrid.clear();
                generateNewLevel();
                return;
            }
            
        } catch (Error e) {
            e.printStackTrace();
        }
        Globals.readIndex = 0;
        
        readValue();
        /*if (!readValue().equalsIgnoreCase("1")) {
            return;
        }*/
        Globals.tileGrid.clear(); // scary!
        Globals.gridWidth = Integer.valueOf(readValue());
        Globals.gridHeight = Integer.valueOf(readValue());

        for (int i = 0; i < Globals.gridHeight * Globals.gridWidth; i++) {
            Globals.tileGrid.add("");
        };

        Globals.tileIndex = 0;

        String value;
        while (Globals.readIndex < Globals.levelStore.length()) {
          value = readValue();
          if (value.equalsIgnoreCase("")) {
            value = getIDfromBlock("Air.png");
          }
          for (int i = 0; i < Globals.atoz.indexOf(Globals.letter)+1; i++) {
              if (Globals.tileIndex > (Globals.gridHeight * Globals.gridWidth)-1) {
                  Globals.tileIndex += (1 - (Globals.gridWidth*Globals.gridHeight));
              }
              //System.out.println("TileIndex:" + Globals.tileIndex);
              Globals.tileGrid.set(Globals.tileIndex, getBlockFromID(value));
              Globals.tileIndex += Globals.gridHeight;
              
              
          }
        }

        Globals.tileIndex = 0;

        callDoneLoadingOnAllBlocks();


    }

    @Override
    protected void initGame() {
        FXGL.getGameScene().setBackgroundColor(javafx.scene.paint.Color.DARKGRAY); // or any color
        // Create an entity and add the ImageView as its view component
        
        int playerSize = 32;
        Image playerImage = new Image("assets/textures/player.png", 64, 64, true, true);
        ImageView playerImageView = new ImageView(playerImage);
        // Set the desired width and height for the ImageView
        playerImageView.setFitWidth(playerSize);
        playerImageView.setFitHeight(playerSize);
        playerImageView.setPreserveRatio(true);
        playerImageView.setSmooth(false);
        
        player = FXGL.entityBuilder()
        .at(0, 32)
        .view(playerImageView)
        .with(new Player(0, 32, 1))
        .buildAndAttach();
        
        
        /*readLevelData();
        if (Globals.tileGrid.size() == 0) {
            generateLevel();
            writeLevelData();
            System.err.println("Data Empty: regenerate level!");
        }*/

        generateLevel();



        cloneLevelTiles();
        resetPlayer();

        System.out.println("GridHeight: "+ Globals.gridHeight + "\tGridwidth: " + Globals.gridWidth);

        //encodeLevel(1);

        
        
        
    }



    

    
    public static void main(String[] args) {
        launch(args);
    }
    
}