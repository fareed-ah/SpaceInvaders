package spaceinvaders;

import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Shield extends ImageView {

    private int width, height, damage;//store width, height, and shield damage

    //Set default size, location and image
    public Shield(int setX, int setY, int setWidth, int setHeight) {
        width = setWidth;
        height = setHeight;
        damage = 0;
        this.setX(setX);
        this.setY(setY);
        this.setImage(new Image(new File("pics/Shield.png").toURI().toString(), width, height, false, false));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDamage() {
        return damage;
    }

    /*
    The damage will increase by one every time the shield is hit.
    with each damage level the image is changed untill the shield is destroyed
    */
    public void setDamage(int value) {
        damage = value;
        String imgPath = null;
        switch (damage) {
            case 0:
                imgPath = new File("pics/Shield.png").toURI().toString();
                break;
            case 1:
                imgPath = new File("pics/ShieldDamage1.png").toURI().toString();
                break;
            case 2:
                imgPath = new File("pics/ShieldDamage2.png").toURI().toString();
                break;
            case 3:
                imgPath = new File("pics/ShieldDamage3.png").toURI().toString();
                break;
            case 4:
                imgPath = new File("pics/ShieldDamage4.png").toURI().toString();
                break;
            case 5:
                imgPath = new File("pics/ShieldDamage5.png").toURI().toString();
                break;
        }
        this.setImage(new Image(imgPath, width, height, false, false));
    }
}
