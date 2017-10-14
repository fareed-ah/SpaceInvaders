package spaceinvaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

public class GameLogic {

    Pane root;
    GUI gui;
    Player player = new Player(50, 560, 40, 20);
    Enemy enemies[][] = new Enemy[5][11];
    ArrayList<Enemy> shootingEnemies = new ArrayList<>();
    ArrayList<Bullet> enemyBullets = new ArrayList<>();
    ArrayList<String> highscores = new ArrayList<>();

    boolean moveLeft = false, moveRight = true, shoot = false;
    Random rnd = new Random();

    public GameLogic(Pane getRoot) {
        root = getRoot;
        gui = new GUI(root, player);
        enemies = gui.enemies;
        loadScores();
        timer.start();
    }

    AnimationTimer timer = new AnimationTimer() {
        //set a wait timer for enemy shots
        long shootWait = (int) (800000000 * (rnd.nextInt(2) + 0.5));
        long shootTimer = 0;

        //set a wait timer for bonus enemy ship
        long bonusWait = (int) (2000000000 * (rnd.nextInt(2) + 0.5));
        long bonusTimer = 0;

        @Override
        public void handle(long now) {
            playerAction();
            getShootingEnemies();

            //if enough time has passed the enemy shoots
            if (now - shootTimer >= shootWait) {
                enemyShoot();
                shootTimer = now;
            }

            //if enough time has passed the bonus ship is visible
            if (now - bonusTimer >= (bonusWait) * 10 && !gui.bonusShip.isVisible()) {
                gui.bonusShip.setVisible(true);
                gui.bonusShip.setPoints(rnd.nextInt(500 - 100 + 1) + 100);
                bonusTimer = now;
                gui.bonusShip.setX(0);
            }

            enemyBulletCollision();
            playerBulletCollision();

            if (shootingEnemies.isEmpty()) {
                gui.createEnemies(root);
                gui.createShields(root);
                gui.enemies = enemies;
                player.setLives(player.getLives() + 1);
            }
            if (player.getLives() == 0) {
                timer.stop();
                int high = 10;
                if (highscores.size() < high) {
                    high = highscores.size();
                }
                gui.gameOver(root, new ButtonClick(), highscores.subList(0, high));
            }
            gui.upd(root);
        }
    };

    public void playerAction() {
        //move right or left depending on which movement is activated        
        if (moveRight && player.getX() + player.getWidth() < 600) {
            player.setX(player.getX() + 3);
        } else if (moveLeft && player.getX() > 0) {
            player.setX(player.getX() - 3);
        }

        //if you are going to shoot then reset the bullet position
        if (shoot) {
            gui.bullet.setX(player.getX() + (player.getWidth() / 2));
            gui.bullet.setY(player.getY() - player.getHeight());
            gui.bullet.setVisible(true);
            shoot = false;
        }
    }

    //Get the lowest enemies in each column
    public void getShootingEnemies() {
        //Remove shooting enemies from the array that are not visible anymore
        for (int i = 0; i < shootingEnemies.size(); i++) {
            if (!shootingEnemies.get(i).isVisible()) {
                shootingEnemies.remove(shootingEnemies.get(i));
                i--;
            }
        }

        //add the lowest enemies in each column to the shooting enemies array
        for (int col = 0; col < 11; col++) {
            for (int row = enemies.length - 1; row >= 0; row--) {
                if (enemies[row][col].isVisible()) {
                    shootingEnemies.add(enemies[row][col]);
                    break;
                }
            }
        }
    }

    public void enemyShoot() {
        //get a random enemy from the possible enemies that can shoot make a bullet for them to shoot
        Enemy enemy = shootingEnemies.get(rnd.nextInt(shootingEnemies.size()));
        enemyBullets.add(new Bullet((int) enemy.getX() + (enemy.getWidth() / 2), (int) enemy.getY() + enemy.getHeight(), 5, 30));
        root.getChildren().add(enemyBullets.get(enemyBullets.size() - 1));
    }

    public void enemyBulletCollision() {
        Loop:
        for (int i = 0; i < enemyBullets.size(); i++) {

            enemyBullets.get(i).setY(enemyBullets.get(i).getY() + 10);//move bullets down to shoot

            //check shield collision
            for (Shield shield : gui.shields) {
                if (enemyBullets.get(i).checkCollision(shield, root)) {
                    root.getChildren().remove(enemyBullets.get(i));
                    enemyBullets.remove(enemyBullets.get(i));

                    //if the shield is fully damged then remove it 
                    if (shield.getDamage() == 5) {
                        gui.shields.remove(shield);
                        root.getChildren().remove(shield);
                    } else {
                        shield.setDamage(shield.getDamage() + 1);
                    }
                    i--;//subtract i to adjust for removing the bullet
                    continue Loop;//don't loop through all the other shields
                }
            }

            //check if the bullet hit the player
            if (enemyBullets.get(i).checkCollision(player, root)) {
                root.getChildren().remove(enemyBullets.get(i));
                enemyBullets.remove(enemyBullets.get(i));
                i--;
                player.setLives(player.getLives() - 1);

                //check if the bullet just missed and went off screen
            } else if (enemyBullets.get(i).getY() > 650) {
                root.getChildren().remove(enemyBullets.get(i));
                enemyBullets.remove(enemyBullets.get(i));
                i--;
            }
        }
    }

    public void playerBulletCollision() {

        if (gui.bullet.isVisible()) {
            gui.bullet.setY(gui.bullet.getY() - 10);
        }

        //check if bullet hit an enemy
        for (Enemy[] enemie : enemies) {
            for (Enemy enemy1 : enemie) {
                if (gui.bullet.checkCollision(enemy1, root)) {
                    enemy1.setVisible(false);
                    gui.setScore(enemy1.getPoints());
                    gui.bullet.setY(player.getY());
                }
            }
        }

        //check if the bullet hit the bonus ship
        if (gui.bullet.checkCollision(gui.bonusShip, root)) {
            gui.bonusShip.setVisible(false);
            gui.bonusShip.setX(0);
            gui.setScore(gui.bonusShip.getPoints());
        }

        //if it hits a shield then reset the bullet
        for (Shield shield : gui.shields) {
            if (gui.bullet.checkCollision(shield, root)) {
                gui.bullet.setY(player.getY());
            }
        }
        //check if the bullet goes off the top of the screen
        if (gui.bullet.getY() < 0) {
            gui.bullet.setVisible(false);
            gui.bullet.setY(player.getY());
        }
    }

    //load highscores and sort them from highest to lowest
    public void loadScores() {
        try {
            Scanner sc = new Scanner(new File("highscores.txt"));
            while (sc.hasNextLine()) {
                highscores.add(sc.nextLine());
            }
            quicksort(highscores, 0, highscores.size());
            System.out.println(highscores);
            sc.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //save the players score to the highscore text file
    public void saveScore() {
        try {
            PrintStream fout = new PrintStream(new FileOutputStream(new File("highscores.txt"), false));
            if (gui.getPlayerName().isEmpty()) {
                highscores.add("Unnamed " + gui.getScore());
            } else {
                highscores.add(gui.getPlayerName() + " " + gui.getScore());
            }

            quicksort(highscores, 0, highscores.size());
            for (int i = 0; i < highscores.size(); i++) {
                fout.println(highscores.get(i));
            }
            fout.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void quicksort(ArrayList<String> scores, int low, int high) {
        int indexRange = high - low;

        if (indexRange <= 1) {//the array is sorted
            return;
        } else {

            //random pivotindex to categorize numbers as higher or lower than the pivot vlue
            int pivotIndex = low + (int) (Math.random() * indexRange);
            String pivotValue = scores.get(pivotIndex);

            String lowValues[] = new String[indexRange]; //holds low values
            int lowCtr = 0;

            String highValues[] = new String[indexRange];//holds high values
            int highCtr = 0;

            for (int i = low; i < high; i++) {
                if (i != pivotIndex) {
                    /*
                     if the score is less than the pivotvalue score than 
                     add it to low values else add the score to highvalues
                     */
                    if (Integer.valueOf(scores.get(i).substring(scores.get(i).indexOf(" ")).trim()) < Integer.valueOf(pivotValue.substring(pivotValue.indexOf(" ")).trim())) {
                        lowValues[lowCtr] = scores.get(i);
                        lowCtr++;
                    } else {
                        highValues[highCtr] = scores.get(i);
                        highCtr++;
                    }
                }
            }

            //add high values first to the top of the list
            for (int i = 0; i < highCtr; i++) {
                scores.set(low + i, highValues[i]);
            }

            scores.set(low + highCtr, pivotValue); //add the pivot value 

            //add low values after the pivot value
            for (int i = 0; i < lowCtr; i++) {
                scores.set(low + highCtr + 1 + i, lowValues[i]);
            }

            //sort both halves until the array is fully sorted
            quicksort(scores, low, low + highCtr);
            quicksort(scores, low + highCtr + 1, high);
        }
    }

    public void addHandlers(Scene scene) {
        scene.setOnKeyPressed(new KeyPressed());
        scene.setOnKeyReleased(new KeyReleased());
    }

    //handle user inputs for player movements 
    private class KeyPressed implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent ke) {
            switch (ke.getCode()) {
                case LEFT:
                    moveLeft = true;
                    moveRight = false;
                    break;
                case RIGHT:
                    moveRight = true;
                    moveLeft = false;
                    break;
                case SPACE:
                    shoot = !gui.bullet.isVisible();
                    break;
            }
        }
    }

    //stop movement when key is released
    private class KeyReleased implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent ke) {
            switch (ke.getCode()) {
                case LEFT:
                    moveLeft = false;
                    break;
                case RIGHT:
                    moveRight = false;
                    break;
            }
        }
    }

    private class ButtonClick implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {

            //restart the game 
            if (e.getSource().equals(gui.restart)) {
                saveScore();
                shootingEnemies.clear();
                gui.reset(root, new Player(50, 560, 40, 20));
                player = gui.player;
                timer.start();
                //exit the application
            } else if (e.getSource().equals(gui.quit)) {
                saveScore();
                System.exit(0);
            }

        }
    }
}
