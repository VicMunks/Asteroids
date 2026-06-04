package sdu.asteroids.powerup.shotgun;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.ShotConfig;
import sdu.asteroids.common.data.World;
import sdu.asteroids.powerup.common.AbstractWeaponPlugin;

public class WeaponShotgunPlugin extends AbstractWeaponPlugin {

    @Override
    public String getId() { return "weapon-shotgun"; }

    @Override
    public String getDisplayName() { return "Shotgun"; }

    @Override
    public void modifyShot(ShotConfig config, GameData gameData) {
        int level = getLevel(gameData);
        if (level == 0) return;
        config.bulletCount += level;
        config.spreadAngle = Math.toRadians(15.0 + level * 5.0);
    }
}
