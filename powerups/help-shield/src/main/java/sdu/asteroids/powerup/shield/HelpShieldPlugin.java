package sdu.asteroids.powerup.shield;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.World;
import sdu.asteroids.powerup.common.AbstractPowerupPlugin;

public class HelpShieldPlugin extends AbstractPowerupPlugin {

    @Override
    public String getId() { return "help-shield"; }

    @Override
    public String getDisplayName() { return "Shield"; }

    @Override
    public String getCategory() { return "HELP"; }

    @Override
    public void onAcquire(GameData gameData, World world) {
        gameData.setShieldActiveSeconds(5.0);
    }
}
