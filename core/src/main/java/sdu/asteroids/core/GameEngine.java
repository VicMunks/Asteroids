package sdu.asteroids.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.FilledPolygonShape;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.IShape;
import sdu.asteroids.common.data.TextShape;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.IEntityProcessorService;
import sdu.asteroids.common.services.IPostEntityProcessorService;

import java.util.ArrayList;
import java.util.List;

public class GameEngine extends AnimationTimer {

    private final List<IEntityProcessorService> entityProcessors;
    private final List<IPostEntityProcessorService> postProcessors;
    private final GraphicsContext gc;
    private final GameData gameData;
    private final World world;
    private final Game game;
    private long lastTime;

    public GameEngine(List<IEntityProcessorService> entityProcessors,
                      List<IPostEntityProcessorService> postProcessors,
                      GraphicsContext gc,
                      GameData gameData,
                      World world,
                      Game game) {
        this.entityProcessors = entityProcessors;
        this.postProcessors = postProcessors;
        this.gc = gc;
        this.gameData = gameData;
        this.world = world;
        this.game = game;
    }

    @Override
    public void handle(long now) {
        if (gameData.getGameState() == GameState.RESTARTING) {
            game.restart();
            return;
        }

        if (lastTime > 0) {
            gameData.setDeltaTime((now - lastTime) / 1_000_000_000.0);
        }
        lastTime = now;

        for (IEntityProcessorService processor : entityProcessors) {
            processor.process(gameData, world);
        }
        for (IPostEntityProcessorService postProcessor : postProcessors) {
            postProcessor.process(gameData, world);
        }
        render(world.getEntities());
        world.cleanUp();
    }

    private void render(List<Entity> entities) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameData.getDisplayWidth(), gameData.getDisplayHeight());

        List<Entity> hudEntities = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.getType() == EntityType.HUD) {
                hudEntities.add(entity);
                continue;
            }
            renderEntity(entity);
        }
        for (Entity entity : hudEntities) {
            renderEntity(entity);
        }
    }

    private void renderEntity(Entity entity) {
        IShape shape = entity.getShape();
        if (shape == null) return;

        double[] color = shape.getColorRGBA();

        if (shape instanceof TextShape ts) {
            gc.save();
            gc.setFont(Font.font("monospace", ts.getFontSize()));
            gc.setFill(Color.color(color[0], color[1], color[2], color[3]));
            gc.fillText(ts.getText(), entity.getX(), entity.getY());
            gc.restore();
            return;
        }

        double[] points = shape.getPoints();
        int n = points.length / 2;
        double[] xPoints = new double[n];
        double[] yPoints = new double[n];
        for (int i = 0; i < n; i++) {
            xPoints[i] = points[i * 2];
            yPoints[i] = points[i * 2 + 1];
        }

        gc.save();
        gc.translate(entity.getX(), entity.getY());
        gc.rotate(Math.toDegrees(entity.getRotation()));

        if (shape instanceof FilledPolygonShape) {
            gc.setFill(Color.color(color[0], color[1], color[2], color[3]));
            gc.fillPolygon(xPoints, yPoints, n);
        } else {
            gc.setStroke(Color.color(color[0], color[1], color[2], color[3]));
            gc.strokePolygon(xPoints, yPoints, n);
        }

        gc.restore();
    }
}
