package spaceinvaders;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
/*
 This class keepes track of all player and enemy bullets
 */
public class Bullet extends Rectangle {

    //Set location and size in constructor
    public Bullet(int setX, int setY, int setWidth, int setHeight) {
        this.setFill(Color.WHITE);
        this.setX(setX);
        this.setY(setY);
        this.setHeight(setHeight);
        this.setWidth(setWidth);
    }

    //Check if the bullet collides with a certain target object
    public boolean checkCollision(ImageView target, Pane root) {
        if (this.getBoundsInParent().intersects(target.getBoundsInParent()) && target.isVisible()) {
            this.setVisible(false);
            return true;
        }
        return false;
    }
}
