package sdu.asteroids.powerup.damage;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.World;
import sdu.asteroids.powerup.common.AbstractPowerupPlugin;

public class BoostDamagePlugin extends AbstractPowerupPlugin {

    @Override
    public String getId() { return "boost-damage"; }

    @Override
    public String getDisplayName() { return "Damage Boost"; }

    @Override
    public String getCategory() { return "BOOST"; }

    @Override
    public void onAcquire(GameData gameData, World world) {
        gameData.setPlayerDamageMultiplier(gameData.getPlayerDamageMultiplier() + 1.0);
    }
}
