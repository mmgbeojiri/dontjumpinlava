package com.dontjumpinlava;
import java.io.InputStream;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Smoke extends Component {
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