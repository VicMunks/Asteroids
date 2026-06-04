package sdu.asteroids.core;

import javafx.scene.canvas.GraphicsContext;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.IEntityProcessorService;
import sdu.asteroids.common.services.IGamePluginService;
import sdu.asteroids.common.services.IPostEntityProcessorService;

import java.util.List;

public class Game {

    private final List<IGamePluginService> gamePlugins;
    private final List<IEntityProcessorService> entityProcessors;
    private final List<IPostEntityProcessorService> postProcessors;
    private GameData gameData;
    private World world;

    public Game(List<IGamePluginService> gamePlugins,
                List<IEntityProcessorService> entityProcessors,
                List<IPostEntityProcessorService> postProcessors) {
        this.gamePlugins = gamePlugins;
        this.entityProcessors = entityProcessors;
        this.postProcessors = postProcessors;
    }

    public void start(GraphicsContext gc, GameData gameData, World world) {
        this.gameData = gameData;
        this.world = world;
        new GameEngine(entityProcessors, postProcessors, gc, gameData, world, this).start();
    }

    void runOneTickForTesting(GameData gameData, World world) {
        for (IEntityProcessorService ep : entityProcessors) {
            ep.process(gameData, world);
        }
        for (IPostEntityProcessorService pep : postProcessors) {
            pep.process(gameData, world);
        }
        world.cleanUp();
    }

    public void restart() {
        for (IGamePluginService plugin : gamePlugins) {
            plugin.stop(gameData, world);
        }
        world.getEntities().forEach(e -> e.setActive(false));
        world.cleanUp();
        gameData.setScore(0);
        gameData.setGameState(GameState.STARTING);
        for (IGamePluginService plugin : gamePlugins) {
            plugin.start(gameData, world);
        }
    }
}
