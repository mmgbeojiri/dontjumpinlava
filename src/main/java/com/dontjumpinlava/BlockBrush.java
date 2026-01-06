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
public class BlockBrush extends Block {




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
