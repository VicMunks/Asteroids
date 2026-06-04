package sdu.asteroids.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.World;

public class App extends Application {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 800;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);

        GameData gameData = new GameData();
        gameData.setDisplayWidth(WIDTH);
        gameData.setDisplayHeight(HEIGHT);
        gameData.setGameState(GameState.STARTING);

        World world = new World();

        new PluginLoader().load(gameData, world);

        AnnotationConfigApplicationContext context = 
            new AnnotationConfigApplicationContext(GameConfig.class);
        Game game = context.getBean(Game.class);
        game.start(canvas.getGraphicsContext2D(), gameData, world);

        Scene scene = new Scene(new Pane(canvas), WIDTH, HEIGHT, Color.BLACK);
        scene.setOnKeyPressed(e -> gameData.addKey(e.getCode().toString()));
        scene.setOnKeyReleased(e -> gameData.removeKey(e.getCode().toString()));

        stage.setTitle("AsteroidsFX");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
