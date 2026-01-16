package com.dontjumpinlava;
import java.util.ArrayList;
import java.util.Scanner;
public class Globals {
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
    "Rail.png","Water.png","WaterTop1.png","WaterTop2.png","WaterGlass.png", "PlayerSpawn.png", "EnemyStand.png"};

    public static int readIndex = 0;
    public static char letter; 

    public static int levelNumber = 3;

    public static Scanner Input = new Scanner(System.in); 

    public static boolean levelStart = true;

    public static boolean doneLoading = false;

    public static String[] tileShape = {"#", "", "=", "", "#", "#","#","=", "#","","","","","",""};

    public static String getIDfromBlock(String tile) {
        for (int i = 0; i < Globals.blockID.length; i++) {
            if (Globals.blockID[i].equalsIgnoreCase(tile)){
                return Integer.toString(i);
            }
        }

        return "@";
    }

    public static String getBlockFromID(String id) {
        try {
            return Globals.blockID[Integer.parseInt(id)];
        } catch (ArrayIndexOutOfBoundsException e) { // out of bounds
            return "Air.png";
        }

        
    }

    public static int waterGlasses = 0; // IF YOU ARE GOING TO REFACTOR THIS CODE, THIS IS ONE OF THE ONLY NUMBERS THAT WILL NOT BE FINAL.

    public static int keyDown = 0; // same here
    public static int keyWalk = 0;

    public static double bopY = 0;
    public static double bouncePlayer = 0;
    public static int terminalVelocity = 22;

}