package sdu.asteroids.powerup.heal;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.World;
import sdu.asteroids.powerup.common.AbstractPowerupPlugin;

public class HelpHealPlugin extends AbstractPowerupPlugin {

    @Override
    public String getId() { return "help-heal"; }

    @Override
    public String getDisplayName() { return "Heal"; }

    @Override
    public String getCategory() { return "HELP"; }

    @Override
    public void onAcquire(GameData gameData, World world) {
        gameData.setPlayerHealth(gameData.getPlayerMaxHealth());
    }
}
