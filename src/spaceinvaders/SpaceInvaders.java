package spaceinvaders;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
/*
Fareed Ahmad

Mr.Kuhn

June 22, 2016

A space invaders replica with various classses for gui, game logic, enemy, player, shield and bullet.
*/
public class SpaceInvaders extends Application {

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        GameLogic game = new GameLogic(root);

        Scene scene = new Scene(root, 600, 650);
        game.addHandlers(scene);
        primaryStage.setTitle("Space Invaders");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
