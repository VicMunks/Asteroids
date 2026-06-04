package sdu.asteroids.powerup.rapidfire;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.ShotConfig;
import sdu.asteroids.common.data.World;
import sdu.asteroids.powerup.common.AbstractWeaponPlugin;

public class WeaponRapidFirePlugin extends AbstractWeaponPlugin {

    @Override
    public String getId() { return "weapon-rapidfire"; }

    @Override
    public String getDisplayName() { return "Rapid Fire"; }

    @Override
    public void modifyShot(ShotConfig config, GameData gameData) {
        int level = getLevel(gameData);
        if (level == 0) return;
        config.volleys = 1 + level;
        config.volleyDelaySeconds = 0.08;
    }
}
