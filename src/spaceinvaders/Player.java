package spaceinvaders;

import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player extends ImageView {

    private int width, height, lives; //stores width, height, and player lives

    //set position, size, lives and image of the player
    public Player(int setX, int setY, int setWidth, int setHeight) {
        width = setWidth;
        height = setHeight;
        lives = 3;
        this.setX(setX);
        this.setY(setY);
        this.setImage(new Image(new File("pics/player.png").toURI().toString(), width, height, false, false));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int newValue) {
        lives = newValue;
    }
}
