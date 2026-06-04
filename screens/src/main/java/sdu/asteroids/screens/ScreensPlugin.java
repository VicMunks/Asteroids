package sdu.asteroids.screens;

import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.TextShape;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.IEntityProcessorService;
import sdu.asteroids.common.services.IGamePluginService;

import java.util.ArrayList;
import java.util.List;

public class ScreensPlugin implements IGamePluginService, IEntityProcessorService {

    private boolean startScreenShown = false;
    private boolean gameOverScreenShown = false;
    private final List<String> activeHudIds = new ArrayList<>();

    @Override
    public void start(GameData gameData, World world) {
        startScreenShown = false;
        gameOverScreenShown = false;
        activeHudIds.clear();
    }

    @Override
    public void stop(GameData gameData, World world) {
    }

    @Override
    public void process(GameData gameData, World world) {
        GameState state = gameData.getGameState();

        if (state == GameState.STARTING && !startScreenShown) {
            addHud(world, "ASTEROIDSFX", 48, new double[]{1, 1, 1, 1}, 540, 300);
            addHud(world, "Press SPACE to start", 24, new double[]{0.6, 0.6, 0.6, 1}, 540, 380);
            startScreenShown = true;
        }

        if (state == GameState.STARTING && gameData.isPressed("SPACE")) {
            clearHud(world);
            startScreenShown = false;
            gameOverScreenShown = false;
            gameData.setGameState(GameState.RUNNING);
        }

        if (state == GameState.GAME_OVER && !gameOverScreenShown) {
            clearHud(world);
            addHud(world, "GAME OVER", 48, new double[]{1, 0, 0, 1}, 540, 320);
            addHud(world, "Press SPACE to play again", 20, new double[]{0.6, 0.6, 0.6, 1}, 540, 390);
            gameOverScreenShown = true;
        }

        if (state == GameState.GAME_OVER && gameData.isPressed("SPACE")) {
            clearHud(world);
            gameOverScreenShown = false;
            startScreenShown = false;
            gameData.setGameState(GameState.RESTARTING);
        }
    }

    private void addHud(World world, String text, double fontSize, double[] color, double x, double y) {
        Entity e = new Entity();
        e.setType(EntityType.HUD);
        e.setX(x);
        e.setY(y);
        e.setShape(new TextShape(text, fontSize, color));
        world.addEntity(e);
        activeHudIds.add(e.getId());
    }

    private void clearHud(World world) {
        for (Entity e : world.getEntities()) {
            if (activeHudIds.contains(e.getId())) {
                e.setActive(false);
            }
        }
        activeHudIds.clear();
    }
}
