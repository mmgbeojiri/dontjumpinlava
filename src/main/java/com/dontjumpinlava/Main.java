package com.dontjumpinlava;
import java.util.ArrayList;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    public static int gridHeight = 20;

    public static int tileIndex = 0;

    public static double playerX = 0;
    public static double playerY = 0;

    

}

class Block extends Component {
    double x; 
    double y;
    double size;
    String image = "";
    Entity imageEntity;

    double scratchX =  0.0;
    double scratchY = 0.0;
    public Block(double x, double y, double size, String image) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.image = image;       

    }

    @Override
    public void onAdded() {
        this.imageEntity = entity;
    }

    public void loopTileX(int tileSkip) {
        x += tileSkip*32;
        Globals.tileIndex += tileSkip * Globals.gridHeight;
    }

    public void loopTileY(int tileSkip) {
        y += tileSkip*32;
        Globals.tileIndex += tileSkip;
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
      


    @Override
    public void onUpdate(double tpf) {
        this.x += dx;
        this.y += dy;

        dx *= 0.9;
        dy *= 0.9;

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

    
    
    public void addWall() {
        for (int i = 0; i < Globals.gridHeight; i++) {
            Globals.tileGrid.add("Bedrock.png");
        }
    }
    public void addBoxColumn() {
        String[] randomBlocks = {"dirt.png", "grass.png"};

        Globals.tileGrid.add("Bedrock.png");
        for (int i = 1; i < Globals.gridHeight-1; i++) {
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
        for (int i = 1; i < Globals.width - 1; i++) {
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

    double memoryWaster = 0;
    @Override
    protected void onUpdate(double tpf) {
        memoryWaster += tpf;
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
        tileX = -16;
        for (int i = 0; i < Globals.cloneCountX; i++) {
            tileY = 16;
            for (int j = 0; j < Globals.cloneCountY; j++) {
                if (Globals.tileIndex< Globals.tileGrid.size()){
                    System.out.println(Globals.tileGrid.get(Globals.tileIndex) + " and tile index is" + Globals.tileIndex);
                    createBlock(tileX, tileY, 32, 
                        Globals.tileGrid.get(Globals.tileIndex) // move this to the block class
                    );
                }
                
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
        
        if (Globals.cameraX < Globals.twoForty){ 
            Globals.cameraX = Globals.twoForty ;
        }
        if (Globals.cameraY < Globals.oneEighty){ 
            Globals.cameraY= Globals.oneEighty ;
        }
        if (Globals.cameraX > (Globals.gridWidth*32)-Globals.twoForty) { 
            Globals.cameraX= (Globals.gridWidth*32)-Globals.twoForty ;
        }
        if (Globals.cameraY > (Globals.gridHeight*32)-Globals.oneEighty) { 
            Globals.cameraY = (Globals.gridHeight*32)-Globals.oneEighty ;
        }
    }

    public void resetPlayer() {
        Globals.cameraX= Globals.twoForty;
        Globals.cameraY= Globals.oneEighty;
        player.setX(0);
        player.setY(-32);

    }

    @Override
    protected void initGame() {
        FXGL.getGameScene().setBackgroundColor(javafx.scene.paint.Color.DARKGRAY); // or any color
        // Create an entity and add the ImageView as its view component
        
        int playerSize = 32;
        Image playerImage = new Image("assets/textures/player.png");
        ImageView playerImageView = new ImageView(playerImage);
        // Set the desired width and height for the ImageView
        playerImageView.setFitWidth(playerSize);
        playerImageView.setFitHeight(playerSize);
        playerImageView.setPreserveRatio(true);
        
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