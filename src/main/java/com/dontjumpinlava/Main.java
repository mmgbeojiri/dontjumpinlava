package com.dontjumpinlava;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

/*
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
 */
public class Main extends GameApplication {

    Entity player;
    
    Globals globals = new Globals();

    int keyRight = 0;
    int keyLeft = 0;
    int keyUp = 0;
    
    int keyE = 0;

    

    String levelPath = "level.dat";


    double tileIndex;
    String underTile;
    
    int foundIndex;
    int enemyFoundIndex;
    String brush = "Air.png";
    int spawnIndex = -1;

    //Image enemyImage = new Image("EnemyRun1.png", 64, 64, true, true);
    //ImageView enemyImageView = new ImageView(enemyImage);


    public void updateSpawnIndex() {
        spawnIndex = Globals.tileGrid.indexOf("PlayerSpawn.png"); 
    //System.out.println("SpawnIndex: " + spawnIndex);
    };
    void entityClear() {
        FXGL.getGameWorld().getEntities().stream()
        .filter(e -> e.hasComponent(Enemy.class))
        .collect(java.util.stream.Collectors.toList())
        .forEach(Entity::removeFromWorld);
    }
    void spawnLoop() {
        for (int i = 0; i < Globals.objectIndex.size(); i++) {
            enemySpawnIndex = Globals.objectIndex.get(i);
            spawnType(Globals.objectType.get(i));
            System.out.println("Creating an enemy: "+i + "at index: "+ Globals.objectIndex.get(i));
            
        }
    }
    void entitySetup() {
        spawnLoop();
    }


    public void callDoneLoadingOnAllBlocks() {
        updateSpawnIndex();

        for (Entity entity : FXGL.getGameWorld().getEntities()) {
            if (entity.hasComponent(Block.class)) {
                entity.getComponent(Block.class).doneLoading();
            // Do something with the entity or its component
                };

               
            };
            entityClear();
            entitySetup();


        
    };
    
    
    
    public void addWall() {
        for (int i = 0; i < Globals.gridHeight; i++) {
            Globals.tileGrid.add("Bedrock.png");
            
        }
        // System.out.println("Created Wall!");
    }

    public void addBoxColumn() {
        String[] randomBlocks = {"dirt.png", "stone.png", "grass.png"};

        Globals.tileGrid.add("bedrock.png");
        for (int i = 0; i < Globals.gridHeight - 2; i++) {
            
                Globals.tileGrid.add("Air.png");
            
        }
        Globals.tileGrid.add("CompactGrass.png");

        //System.out.println("Created Column!");

    }
    public void generateNewLevel() {
        Globals.tileGrid.clear();
        addWall();
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
    UserAction specialPressed = new UserAction("Debug") {
        @Override
        protected void onActionBegin() {
            System.out.println("SpawnIndex: " +  spawnIndex + "Tile Grid Y: "+Math.floor(player.getComponent(Player.class).y/32));
        }
    };
    
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
        protected void onAction() {Globals.keyDown = 1;}
         @Override
        protected void onActionEnd() {Globals.keyDown = 0;}
    };
    UserAction sPressed = new UserAction("S") {
        @Override 
        protected void onAction() {Globals.keyDown = 1;}
        @Override
        protected void onActionEnd() {Globals.keyDown = 0;}
    };

    double enemyx;
    double enemyy;
    double enemySpawnIndex;
    void spawnType(String tileType) {
        
        enemyx = (Math.floor((enemySpawnIndex-2)/Globals.gridHeight) * 32)-32;
        enemyy = ((Globals.gridHeight - 1) - (enemySpawnIndex % Globals.gridHeight)) * 32;
        if (tileType.equalsIgnoreCase("EnemyStand.png")||tileType.equalsIgnoreCase("Enemy")) {
            System.out.println("Enemy spawned at \tX:"+ enemyx+"\tY:"+enemyy);
            FXGL.entityBuilder().at(enemyx, enemyy)
            .view("EnemyStand.png")
            .with(new Enemy(enemyx, enemyy,1, "Op"))
            .buildAndAttach();
        }
    }
    UserAction gPressed = new UserAction("G") {
        @Override
        protected void onActionBegin(){
            spawnType("Enemy");
            
        }
    };

    UserAction ePressed = new UserAction("E") {
        @Override 
        protected void onActionBegin() {
            if (Globals.editor) {
                Globals.levelStart = false;
                encodeLevel(Globals.levelNumber);
                Globals.levelStart = true;
                callDoneLoadingOnAllBlocks();
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
                //System.out.println(Globals.tileGrid);
                String answer = "";
                System.out.print("Do you want to reset the level? Please exactly type: \"Yes\".");
                answer = Globals.Input.nextLine();
                if (answer.equals("Yes")) {
                    Globals.levelStart = false;


                    System.out.print("Enter Level Width (Current: " + Globals.gridWidth + ")");
                    int answerInt = Globals.Input.nextInt();
                    try {
                        
                        Globals.gridWidth = answerInt;
                        if (answerInt < 30) {
                            System.out.println("Grid Width should not be less than 30.");
                            Globals.gridWidth = 30;
                        }
                    } catch (Error e) {
                        Globals.gridWidth = 100;
                    } 

                    System.out.print("Enter Level Height (Current: " + Globals.gridHeight + ")");
                    answerInt = Globals.Input.nextInt();
                    try {
                        Globals.gridHeight = answerInt;
                        if (answerInt < 16) {
                            System.out.println("Grid height should not be less than 16.");
                            Globals.gridHeight = 16;
                        }
                    } catch (Error e) {
                        Globals.gridHeight = 40;
                    }

                    generateNewLevel();
                    //System.out.println("I WOKE UP IN A NEW BUGATII");
                    //System.out.println(Globals.tileGrid); // there is a row of bedrocks in the middle of the list that is the same length as Grid.height. why does this occur? the only fnction that couild do this could be addwall.
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
    String[] misaList = {"Rail.png", "WaterGlass.png", "PlayerSpawn.png"};
    String[] enemyList = {"EnemyStand.png"};

    public void changeBrush(String strung) {  Globals.chosenBrush = strung; System.out.println("Set chosen brush to: " + strung);}

    public void nextBrush(int key) {
        int index = 0;
        String[] loadedList;
        switch (key) {
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
            case 5: 
                loadedList = enemyList;
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
    UserAction five  = new UserAction("Enemy") { @Override protected void onActionBegin() {nextBrush(5);}};
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
        input.addAction(five, KeyCode.DIGIT5);

        input.addAction(lPressed, KeyCode.L);

        input.addAction(rPressed, KeyCode.R);

        input.addAction(gPressed, KeyCode.G);

        //input.addAction(specialPressed, KeyCode.BACK_SLASH);
        

        
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
        player.getComponent(Player.class).changeVelY(3 * (keyUp - Globals.keyDown));

        player.getComponent(Player.class).setVelX(0.7* player.getComponent(Player.class).dx);
        player.getComponent(Player.class).setVelY(0.7* player.getComponent(Player.class).dy);

        player.getComponent(Player.class).changeX(player.getComponent(Player.class).dx);
        player.getComponent(Player.class).changeY(player.getComponent(Player.class).dy);
    }

    public void handleKeysLeftRight() {
        player.getComponent(Player.class).playeraction = "walk"; 
        Globals.keyWalk = (keyRight - keyLeft);

        if (Globals.keyWalk == 0) { // player not pressing keys
            if (player.getComponent(Player.class).falling < 10) { // if the character isnt falling
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
            if (Globals.keyWalk * player.getComponent(Player.class).dx < 10) {
                if (Globals.keyWalk * player.getComponent(Player.class).dx < 0) {
                player.getComponent(Player.class).changeVelX(
                        Globals.keyWalk * 0.8
                );

                if (player.getComponent(Player.class).falling < 10){
                    player.getComponent(Player.class).playeraction = "turn";
                    makeSkipSmoke();
                }

            } else {
                player.getComponent(Player.class).changeVelX(
                        Globals.keyWalk * 0.4
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

        if (player.getComponent(Player.class).dy < -Globals.terminalVelocity) {
            player.getComponent(Player.class).setVelY(-Globals.terminalVelocity);
        }

        if (keyUp == 1) { // key space is pressed
            if ((player.getComponent(Player.class).ableToWallJump)) {
                player.getComponent(Player.class).changeJumping(1);
                if (player.getComponent(Player.class).jumping < 15) {
                    player.getComponent(Player.class).setVelY(6); 
                }
                player.getComponent(Player.class).dx = 10 * -player.getComponent(Player.class).direction;
            }
            if ((player.getComponent(Player.class).falling < 10) || (player.getComponent(Player.class).jumping > 0)) {
            
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

    void paintEntity() {
        if (!brush.equalsIgnoreCase("Air.png")) {
            return;
        }
        brush = Globals.chosenBrush;
        enemyFoundIndex = Globals.objectIndex.indexOf(tileIndex);
        if (enemyFoundIndex > -1) {
            Globals.objectIndex.remove(enemyFoundIndex);
            Globals.objectType.remove(enemyFoundIndex);
            System.out.println("Removed type: " + brush + " from index: "+enemyFoundIndex);

        } else {

            Globals.objectIndex.add((int)tileIndex);
            Globals.objectType.add(brush);
            System.out.println("Added type: " + brush + " to index: "+enemyFoundIndex);
        }
        entityClear();
        entitySetup();

        System.out.println("objectIndex: \t"+Globals.objectIndex);
        System.out.println("ObjectType: \t"+Globals.objectType);

    }

    public void movePlayerEditor(){
        if (Globals.editor == false) {
            return;
        }
        //System.out.println(player.getComponent(Player.class).y);
        
        Globals.mouseX = (double) FXGL.getInput().mouseXUIProperty().get();
        Globals.mouseY = (double) FXGL.getInput().mouseYUIProperty().get();
        getTile(Globals.mouseX + (Globals.cameraX - Globals.twoForty), Globals.height - (Globals.mouseY-(Globals.cameraY-Globals.oneEighty)));

        if (!Globals.mouseDown) {
            brush = "Air.png";
            return;
        }
         if (Arrays.asList(enemyList).contains(Globals.chosenBrush)){
            
            paintEntity();
            return;
            
        }


        if (brush.equalsIgnoreCase("Air.png")) {
            System.out.println("Undertile: " + underTile + "\tBrush: " + Globals.chosenBrush);
            if (underTile.equalsIgnoreCase(Globals.chosenBrush)) {
                brush = "Air.png";
            } else {
                brush = Globals.chosenBrush;
            }
        }
       
        if (brush.equalsIgnoreCase("PlayerSpawn.png")) {
            foundIndex = Globals.tileGrid.indexOf(brush);
            if (foundIndex > -1) {
                Globals.tileGrid.set(foundIndex, "Air.png");
            }

            
        }
        try {

            Globals.tileGrid.set((int)tileIndex, brush);
            //writeLevelData();
            if (brush.equalsIgnoreCase("PlayerSpawn.png")) {
                updateSpawnIndex();
            }
           
        } catch (Error e) {
            e.printStackTrace();
        }
        

        



        //System.out.println("Mouse X: " + mouseX + " Mouse Y: "+ mouseY + 
        //"\tUndertile: " + getTile(mouseX+Globals.cameraX, mouseY-Globals.cameraY));
    }


    public void resetPlayer() {
        Globals.playerHeight = 16;
        Globals.playerWidth = 16;
        if (spawnIndex > -1) {
            player.getComponent(Player.class).x = (Math.floor((spawnIndex-2)/Globals.gridHeight) * 32)-32;
            player.getComponent(Player.class).y = ((Globals.gridHeight - 1) - (spawnIndex % Globals.gridHeight)) * 32;
            //tileIndex = ((Globals.tileGridX)*(Globals.gridHeight)) - Globals.tileGridY;
            //
            System.out.println(player.getComponent(Player.class).y);
            //player.getComponent(Player.class).y = -(spawnIndex - 1) % Globals.gridHeight;
            
            //player.getComponent(Player.class).x = (player.getComponent(Player.class).x * 32) + 16;
            //player.getComponent(Player.class).y = (player.getComponent(Player.class).y * 32) + Globals.playerHeight;
        } else {
            player.getComponent(Player.class).x = 0;
            player.getComponent(Player.class).y = 32;
        }
        
        
        
        player.getComponent(Player.class).dx = 0;
        player.getComponent(Player.class).dy = 0;
        player.getComponent(Player.class).jumping = 99;
        player.getComponent(Player.class).falling = 99;
        
        
        //Globals.cameraX = Globals.twoForty;
        //Globals.cameraY = Globals.oneEighty;
        

        Globals.cameraX = player.getComponent(Player.class).x;
        Globals.cameraY = player.getComponent(Player.class).y;

    }
    
    public void checkAroundPlayer() {
        //System.out.println(player.getComponent(Player.class).y);
        if (player.getComponent(Player.class).y < 0) {
            resetPlayer();
        }
        getTile(player.getComponent(Player.class).x+48, player.getComponent(Player.class).y+32);
        
        //System.out.println(underTile);
        if (underTile.equalsIgnoreCase("WaterGlass.png")) {
            
            Globals.tileGrid.set((int) tileIndex, "Air.png"); // Scary!
            Globals.waterGlasses += 1;
            System.out.println("Water Glasses: "+ Globals.waterGlasses);
        }

    }

    public void movePlayer() {
        if (Globals.editor) {
            handleGodMode();
            movePlayerEditor();
        } else {
            if (player.getComponent(Player.class).falling > 2 && player.getComponent(Player.class).dy < -1) {
                Globals.bopY = player.getComponent(Player.class).y;
            } else {
                Globals.bopY = 0;
            }

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

    
    void encodeTileGrid() {
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
                    blockID = Globals.getIDfromBlock(tile);
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
        writeValue(Globals.getIDfromBlock(tile), Globals.atoz.charAt(length-1));
        //writeValue(tile, '_');
        //writeValue(Integer.toString(length), '_');

        



    }

    void encodeObjects() {
        writeValue(Integer.toString(Globals.objectIndex.size()), '_');
        for (int i = 0; i < Globals.objectIndex.size(); i++) {
            writeValue(Integer.toString(Globals.objectIndex.get(i)), '_');
            writeValue(Globals.objectType.get(i), '_');

        }
    }

    public void encodeLevel(int levelNumber) {
        Globals.levelStore = "";

        writeValue("1", '_');
        encodeTileGrid();
        encodeObjects();
        String savePath = "stupid.txt";

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
            if (Globals.letter == '.') {
                break;
            }
            value += Globals.letter;
            readLetter();
        }

        

        return value;
    }

    void decodeObjects() {
        int objectListLength = 0;
        try {
            objectListLength = Integer.parseInt(readValue());
        } catch (NumberFormatException e) {
            
            return;
        }
        Globals.objectIndex.clear();
        Globals.objectType.clear();
        for (int i = 0; i < objectListLength; i++) {
            Globals.objectIndex.add(Integer.valueOf(readValue()));
            Globals.objectType.add(readValue());
            
        }
        System.out.println("Object Index\t" + Globals.objectIndex);
        System.out.println("Object Type\t" + Globals.objectType);
    }

    void decodeTileGrid() {
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
            value = Globals.getIDfromBlock("Air.png");
          }
          for (int i = 0; i < Globals.atoz.indexOf(Globals.letter)+1; i++) {
              Globals.tileGrid.set(Globals.tileIndex, Globals.getBlockFromID(value));
              Globals.tileIndex += Globals.gridHeight;
              if (Globals.tileIndex > (Globals.gridHeight * Globals.gridWidth)-1) {
                  Globals.tileIndex += (1 - (Globals.gridWidth*Globals.gridHeight));
                    if (Globals.tileIndex > Globals.gridHeight) {
                        return;
                    }
                }
              //System.out.println("TileIndex:" + Globals.tileIndex);
              
              
          }
        }

        Globals.tileIndex = 0;

        callDoneLoadingOnAllBlocks();
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
        if (!readValue().equalsIgnoreCase("1")) {
            return;
        }
        
        decodeTileGrid();
        decodeObjects();


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