package com.dontjumpinlava;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

class Globals {
    public static double cameraX = 0;
    public static double cameraY = 0;

    public static int width = 960;
    public static int height = 540;
    public static int twoForty = (width/2)-32;
    public static int oneEighty = (height/2)-32;
    public static int cloneCountX = (int) Math.ceil(width/32)+1;

    public static int cloneCountY = (int) Math.ceil(height/32)+1;

    public static ArrayList<String> tileGrid = new ArrayList<>();
    public static int gridWidth = 90;
    public static int gridHeight = 25;

    public static int tileIndex = 0;

    public static double playerX = 0;
    public static double playerY = 0;

    public static double playerHeight = 16;

    

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
        String[] randomBlocks = {"dirt.png", "grass.png", "bedrock.png"};
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

    @Override
    public void onUpdate(double tpf) {
        //Globals.tileGrid.get(Globals.tileIndex)
        //this.tileIndex = Globals.tileIndex;
        updateTextureIfNeeded();
        
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

        scratchX = x + Globals.width/2 - size/2 ;
        scratchY = y + Globals.height/2 - size/2;

        imageEntity.setX(scratchX - Globals.cameraX);
        imageEntity.setY(scratchY + Globals.cameraY);
    }
}

class Player extends Component {
    double x; 
    double y;
    double size;
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

    public void getTile(double x, double y) {
        tileGridX = Math.floor(x/32)+1;
        tileGridY = Math.ceil(y/32);
        playerTileIndex = -(tileGridY+1) + ((tileGridX+1)*(Globals.gridHeight));
        // the y value is flipped, so we get the next row, and subtract by tilegridy+1
        if (playerTileIndex >= 0){
            underTile = Globals.tileGrid.get((int)playerTileIndex);
        } else {
            underTile = "";
        }
        //System.out.println("Tile Grid X: "+tileGridX + " Tile Grid Y"+ tileGridY + " Tile: " + underTile);
    }

    
    public void fixCollisionAtPoint(double x, double y) {
    
        getTile(x, y);
        if ( !underTile.equalsIgnoreCase("Air.png")) {
            solid = 10;
        } 
    }

    public void fixCollisionInDirection(double dx, double dy) {
        solid = 0;
        fixCollisionAtPoint(this.x+16, this.y);
        fixCollisionAtPoint(this.x+16, this.y - Globals.playerHeight);
        fixCollisionAtPoint(this.x+16, this.y - (Globals.playerHeight)-15);
        if ( solid > 0) {
            this.x -= dx;
            this.y -= dy;

            //this.dx = 0;
            //this.dy = 0;
        } 
    }



    @Override
    public void onUpdate(double tpf) {
        this.x += dx;
        fixCollisionInDirection(dx, 0);
        this.y += dy;
        fixCollisionInDirection(0, dy);
        

        Globals.playerX = this.x;
        Globals.playerY = this.y;

        scratchX = this.x + Globals.width/2 - size/2 ;
        scratchY = -this.y +  Globals.height/2 - size/2;

        imageEntity.setX(scratchX - Globals.cameraX);
        imageEntity.setY(scratchY + Globals.cameraY);
    }
}


public class Main extends GameApplication {

    Entity player;
    
    Globals globals = new Globals();

    int keyRight = 0;
    int keyLeft = 0;
    int keyUp = 0;
    int keyDown = 0;

    int keyWalk = 0;

    
    
    public void addWall() {
        for (int i = 0; i < Globals.gridHeight; i++) {
            Globals.tileGrid.add("Bedrock.png");
        }
    }
    public void addBoxColumn() {
        String[] randomBlocks = {"dirt.png", "grass.png"};

        Globals.tileGrid.add("Bedrock.png");
        for (int i = 0; i < Globals.gridHeight-2; i++) {
            if (Math.floor(Math.random()*10) == 0) {
                Globals.tileGrid.add("Grass.png");
            }else{
                Globals.tileGrid.add("Air.png");
            }

        }
        Globals.tileGrid.add("Bedrock.png");

    }

    public void generateLevel() {
        addWall();
        for (int i = 0; i < Globals.gridWidth-2; i++) {
            addBoxColumn();
        }
        addWall();
    };

    

    


    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(Globals.width);
        settings.setHeight(Globals.height);
        settings.setTitle("Don't Jump in Lava");
    }

    UserAction rightPressed = new UserAction("Right") {
        @Override 
        protected void onAction() {
            keyRight = 1;
        }
         @Override
        protected void onActionEnd() {
            keyRight = 0;
        }
    };

    UserAction leftPressed = new UserAction("Left") {
        @Override 
        protected void onAction() {
            keyLeft = 1;
        }
         @Override
        protected void onActionEnd() {
            keyLeft = 0;
        }
    };
    
    UserAction upPressed = new UserAction("Up") {
        @Override 
        protected void onAction() {
            keyUp = 1;
        }
         @Override
        protected void onActionEnd() {
            keyUp = 0;
        }
    };
    
    UserAction downPressed = new UserAction("Down") {
        @Override 
        protected void onAction() {
            keyDown = 1;
        }
         @Override
        protected void onActionEnd() {
            keyDown = 0;
        }
    };
    
    @Override
    protected void initInput() {
        Input input = FXGL.getInput();
        input.addAction(rightPressed, KeyCode.RIGHT);
        input.addAction(leftPressed, KeyCode.LEFT);
        input.addAction(upPressed, KeyCode.UP);
        input.addAction(downPressed, KeyCode.DOWN);
    }

    public void handleKeysLeftRight() {

        keyWalk = (keyRight - keyLeft);

        player.getComponent(Player.class).setVelX(
            (player.getComponent(Player.class).dx * 0.9) + (keyWalk )
        ); 
    }

    public void handleKeysJump() {
        System.out.println(player.getComponent(Player.class).dy);
        System.out.println(player.getComponent(Player.class).dx);
        if (keyUp == 1) {
            player.getComponent(Player.class).setVelY(
                        6
            ); 
        }

        player.getComponent(Player.class).changeVelY(
                        -0.3
        ); 

        if (player.getComponent(Player.class).dy < -22) {
            player.getComponent(Player.class).setVelY(-22);
        }

    };

    public void movePlayer() {

        handleKeysLeftRight();
        handleKeysJump();
        /*player.getComponent(Player.class).setVelX(
            6*(keyRight - keyLeft)
        ); 
        player.getComponent(Player.class).setVelY(
            6*(keyUp - keyDown)
        );*/ 
        moveCamera();

    }

    double memoryWaster = 0;
    @Override
    protected void onUpdate(double tpf) {
        movePlayer();
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

    public void setup() {
        double tileX = 0.0;
        double tileY = 0.0;

        System.out.println(Globals.tileGrid.size());
        System.out.println(Globals.tileGrid);

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
            Globals.cameraY = Globals.oneEighty ;
        }
        if (Globals.cameraX > ((Globals.gridWidth*(32))-Globals.width/2 - 32)) { 
            Globals.cameraX = ((Globals.gridWidth*(32))-Globals.width/2 - 32);
        }
        if (Globals.cameraY > (Globals.gridHeight*32) - (Globals.height/2) - 32) { 
            Globals.cameraY = (Globals.gridHeight*32) - (Globals.height/2) - 32 ;
        }
    }

    public void resetPlayer() {
        Globals.cameraX= Globals.twoForty;
        Globals.cameraY= Globals.oneEighty;
        Globals.playerHeight = 16;
        player.setX(0);
        player.setY(-32);

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
        
        generateLevel();
        cloneLevelTiles();
        resetPlayer();
        
        
    }



    

    
    public static void main(String[] args) {
        launch(args);
    }
    
}