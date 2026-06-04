package sdu.asteroids.hud;

import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.TextShape;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.IEntityProcessorService;
import sdu.asteroids.common.services.IGamePluginService;

public class HudPlugin implements IGamePluginService, IEntityProcessorService {

    private String scoreEntityId;
    private String highScoreEntityId;

    @Override
    public void start(GameData gameData, World world) {
        Entity scoreEntity = new Entity();
        scoreEntity.setType(EntityType.HUD);
        scoreEntity.setX(20);
        scoreEntity.setY(30);
        scoreEntity.setShape(new TextShape("SCORE: 0", 20, new double[]{1, 1, 1, 1}));
        world.addEntity(scoreEntity);
        scoreEntityId = scoreEntity.getId();

        Entity highScoreEntity = new Entity();
        highScoreEntity.setType(EntityType.HUD);
        highScoreEntity.setX(20);
        highScoreEntity.setY(58);
        highScoreEntity.setShape(new TextShape("BEST: 0", 20, new double[]{0.6, 0.6, 0.6, 1}));
        world.addEntity(highScoreEntity);
        highScoreEntityId = highScoreEntity.getId();
    }

    @Override
    public void stop(GameData gameData, World world) {
        for (Entity e : world.getEntities(EntityType.HUD)) {
            if (e.getId().equals(scoreEntityId) || e.getId().equals(highScoreEntityId)) {
                e.setActive(false);
            }
        }
    }

    @Override
    public void process(GameData gameData, World world) {
        for (Entity e : world.getEntities(EntityType.HUD)) {
            if (e.getId().equals(scoreEntityId)) {
                ((TextShape) e.getShape()).setText("SCORE: " + gameData.getScore());
            } else if (e.getId().equals(highScoreEntityId)) {
                ((TextShape) e.getShape()).setText("BEST: " + gameData.getHighScore());
            }
        }
    }
}
