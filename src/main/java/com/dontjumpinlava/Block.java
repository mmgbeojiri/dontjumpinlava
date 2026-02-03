package com.dontjumpinlava;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public class Block extends Component {
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

        doneLoading();
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
        if (texName.equalsIgnoreCase("CompactGrass.png")) {
            if (Globals.editor == false) {
                texName = "Grass.png";
            }
        }
        if (texName.equalsIgnoreCase("CompactDirt.png")) {
            if (Globals.editor == false) {
                texName = "Dirt.png";
            }
        }
        if (texName.equalsIgnoreCase("PlayerSpawn.png")) {
            if (Globals.editor == false) {
                texName = "Air.png";
            }
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
            imageview.toBack();
            imageview.setImage(img);
            imageview.setSmooth(true);

            if (texName.equalsIgnoreCase("Water.png") || texName.contains("WaterTop")){
                imageview.setOpacity(0.5f);
            } else{
                imageview.setOpacity(1);
            }
            
            //System.out.println(imageview);
            //imageview.toBack();

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