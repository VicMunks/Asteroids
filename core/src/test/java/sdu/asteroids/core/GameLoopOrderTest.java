package sdu.asteroids.core;

import org.junit.jupiter.api.Test;
import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.IEntityProcessorService;
import sdu.asteroids.common.services.IGamePluginService;
import sdu.asteroids.common.services.IPostEntityProcessorService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameLoopOrderTest {

    @Test
    void test_iepRunsBeforeIpep() {
        GameData gameData = new GameData();
        gameData.setGameState(GameState.RUNNING);
        gameData.setDeltaTime(0.016);
        World world = new World();

        List<String> callOrder = new ArrayList<>();
        IEntityProcessorService iep = (gd, w) -> callOrder.add("IEP");
        IPostEntityProcessorService ipep = (gd, w) -> callOrder.add("IPEP");

        Game game = new Game(List.of(), List.of(iep), List.of(ipep));
        game.runOneTickForTesting(gameData, world);

        assertEquals(List.of("IEP", "IPEP"), callOrder);
    }

    @Test
    void test_pluginRemovedFromWorldOnStop() {
        GameData gameData = new GameData();
        World world = new World();

        Entity[] ref = new Entity[1];
        IGamePluginService plugin = new IGamePluginService() {
            @Override
            public void start(GameData gd, World w) {
                ref[0] = new Entity();
                ref[0].setType(EntityType.PLAYER);
                w.addEntity(ref[0]);
            }
            @Override
            public void stop(GameData gd, World w) {
                ref[0].setActive(false);
            }
        };

        plugin.start(gameData, world);
        assertFalse(world.getEntities(EntityType.PLAYER).isEmpty());

        plugin.stop(gameData, world);
        world.cleanUp();

        assertTrue(world.getEntities(EntityType.PLAYER).isEmpty());
    }
}
