package spaceinvaders;

import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
/*
 This class handles enemies and their location
 */
public class Enemy extends ImageView {

    private int width, height, points; //variables to store width, height, and points awarded to the player.

    //set size, location, points, and an image path for the particular enemy image
    public Enemy(int setX, int setY, int setWidth, int setHeight, int setPoints, String imgPath) {
        width = setWidth;
        height = setHeight;
        points = setPoints;
        this.setX(setX);
        this.setY(setY);
        this.setImage(new Image(new File(imgPath).toURI().toString(), width, height, false, false));
    }
    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int value) {
        points = value;
    }

}
