package sdu.asteroids.powerup.speed;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.World;
import sdu.asteroids.powerup.common.AbstractPowerupPlugin;

public class BoostSpeedPlugin extends AbstractPowerupPlugin {

    @Override
    public String getId() { return "boost-speed"; }

    @Override
    public String getDisplayName() { return "Speed Boost"; }

    @Override
    public String getCategory() { return "BOOST"; }

    @Override
    public void onAcquire(GameData gameData, World world) {
        gameData.setPlayerSpeedMultiplier(gameData.getPlayerSpeedMultiplier() + 0.3);
    }
}
