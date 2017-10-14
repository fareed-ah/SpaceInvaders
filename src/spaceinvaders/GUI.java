package spaceinvaders;

import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GUI {

    Player player; //references player
    Enemy[][] enemies = new Enemy[5][11]; //holds all enemy objects
    ArrayList<Shield> shields = new ArrayList<>(); //array for the shields/barricades
    Bullet bullet; //player bullet
    int enemySpeed = 1; //enemy speed
    private Text score, lives, highscore; //display score and lives
    private int currScore = 0; // keep track of the current score
    Enemy bonusShip; //alien ship with bonus points  
    Button restart, quit; //buttons at the end of the game to resart or quit
    private TextField playerName; //get player name for highscores

    public GUI(Pane root, Player getPlayer) {
        reset(root, getPlayer);
        root.setStyle("-fx-background-color: #000000"); // set background black
    }

    public void reset(Pane root, Player getPlayer) {
        root.getChildren().clear(); 
        enemySpeed = 1; 
        currScore = 0;
        player = getPlayer;
        bullet = new Bullet((int) player.getX() + (int) (player.getWidth() / 2), (int) player.getY(), 3, 20);
        bullet.setVisible(false);

        bonusShip = new Enemy(0, 60, 40, 20, 300, "pics/alienShip.png");
        bonusShip.setVisible(false);

        createEnemies(root);
        createShields(root);

        score = new Text(30, 50, String.valueOf("SCORE<" + currScore + ">"));
        score.setFont(new Font("Arial", 24));
        score.setFill(Color.WHITE);

        lives = new Text(20, 630, String.valueOf(player.getLives()));
        lives.setFont(new Font("Arial", 24));
        lives.setFill(Color.WHITE);

        root.getChildren().add(bullet);
        root.getChildren().add(player);
        root.getChildren().add(score);
        root.getChildren().add(lives);
        root.getChildren().add(bonusShip);
        upd(root);
    }

    public void upd(Pane root) {
        score.setText("SCORE<" + currScore + ">");
        lives.setText(String.valueOf(player.getLives()));

        Enemy enemy;

        /*
         If the enemies in the far right column are visible and reach the
         end of the screen then enemies move down and start moving left.
         */
        Loop:
        for (int col = 10; col >= 0; col--) {
            for (int row = 0; row < enemies.length; row++) {
                enemy = enemies[row][col];

                if (enemy.isVisible() && enemy.getX() + enemy.getWidth() >= 600) {
                    moveDown(enemies);
                    enemySpeed = -1;
                    break Loop;
                }
            }
        }

        /*
         If the enemies in the left column are visible and reach the
         end of the screen then enemies move down and start moving right.
         */
        Loop:
        for (int col = 0; col < 11; col++) {
            for (int row = 0; row < enemies.length; row++) {
                enemy = enemies[row][col];
                if (enemy.isVisible() && enemy.getX() <= 0) {
                    moveDown(enemies);
                    enemySpeed = 1;
                    break Loop;
                }
            }
        }

        //move enemies if they are visible
        for (Enemy row[] : enemies) {
            for (Enemy item : row) {
                if (item.isVisible()) {
                    item.setX(item.getX() + enemySpeed);
                }
            }
        }

        //if the bonus ship is visible move it across the screen unitl it leaves the screen
        if (bonusShip.isVisible()) {
            bonusShip.setX(bonusShip.getX() + 2);
            if (bonusShip.getX() > 600) {
                bonusShip.setX(0);
                bonusShip.setVisible(false);
            }
        }
    }

    //move enemies down
    public void moveDown(Enemy[][] enemies) {
        for (Enemy row[] : enemies) {
            for (Enemy item : row) {
                if (item.isVisible()) {
                    item.setY(item.getY() + 7);
                }
            }
        }
    }

    //reset the shields and add new ones
    public void createShields(Pane root) {
        if (!shields.isEmpty()) {
            root.getChildren().removeAll(shields);
            shields.clear();
        }
        for (int i = 0; i < 4; i++) {
            shields.add(new Shield((i * 120) + 100, 500, 45, 35));
            root.getChildren().add(shields.get(shields.size() - 1));
        }
    }

    /*
     create various enemies based on row and set different images
     and points acquired for each enemy
     */
    public void createEnemies(Pane root) {
        for (int row = 0; row < enemies.length; row++) {
            for (int col = 0; col < enemies[row].length; col++) {
                switch (row) {
                    case 0:
                        enemies[row][col] = new Enemy((45 * col) + 70, (45 * row) + 100, 30, 30, 30, "pics/enemy5.png");
                        break;
                    case 1:
                    case 2:
                        enemies[row][col] = new Enemy((45 * col) + 70, (45 * row) + 100, 30, 30, 20, "pics/enemy3.png");
                        break;
                    case 3:
                    case 4:
                        enemies[row][col] = new Enemy((45 * col) + 70, (45 * row) + 100, 30, 30, 10, "pics/enemy.png");
                        break;
                }
                root.getChildren().add(enemies[row][col]);
            }
        }
    }

    public String getPlayerName() {
        return playerName.getText();
    }

    public void setScore(int addValue) {
        currScore += addValue;
    }

    public int getScore() {
        return currScore;
    }

    //Game Over screen
    public void gameOver(Pane root, EventHandler btnPressed, List<String> highscoresList) {
        Text message = new Text("GAME OVER");

        Text prompt = new Text("Enter Name: ");
        prompt.setFill(Color.WHITE);
        prompt.setLayoutX(180);
        prompt.setLayoutY(215);

        playerName = new TextField();
        playerName.setPromptText("Enter Name");
        playerName.relocate(250, 200);

        score.setText("SCORE<" + currScore + ">");
        score.setX(235);
        score.setY(100);

        highscore = new Text("Highscores:\n");
        highscore.setFont(new Font("Arial", 24));
        highscore.setFill(Color.WHITE);
        highscore.relocate(200, 270);
        for (String score : highscoresList) {
            highscore.setText(highscore.getText() + score + "\n");
        }

        restart = new Button("Play Again");
        restart.setLayoutX(200);
        restart.setLayoutY(150);
        restart.setOnAction(btnPressed);

        quit = new Button("Exit");
        quit.setLayoutX(350);
        quit.setLayoutY(150);
        quit.setOnAction(btnPressed);

        root.getChildren().clear();

        root.getChildren().add(prompt);
        root.getChildren().add(playerName);
        root.getChildren().add(highscore);
        root.getChildren().add(score);
        root.getChildren().add(restart);
        root.getChildren().add(quit);
    }
}
