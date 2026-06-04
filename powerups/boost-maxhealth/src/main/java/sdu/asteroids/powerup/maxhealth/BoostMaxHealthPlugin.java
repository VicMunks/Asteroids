package sdu.asteroids.powerup.maxhealth;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.World;
import sdu.asteroids.powerup.common.AbstractPowerupPlugin;

public class BoostMaxHealthPlugin extends AbstractPowerupPlugin {

    @Override
    public String getId() { return "boost-maxhealth"; }

    @Override
    public String getDisplayName() { return "Max Health Up"; }

    @Override
    public String getCategory() { return "BOOST"; }

    @Override
    public void onAcquire(GameData gameData, World world) {
        int newMax = gameData.getPlayerMaxHealth() + 1;
        gameData.setPlayerMaxHealth(newMax);
        gameData.setPlayerHealth(newMax);
    }
}
