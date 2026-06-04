package sdu.asteroids.powerup.bazooka;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.ShotConfig;
import sdu.asteroids.common.data.World;
import sdu.asteroids.powerup.common.AbstractWeaponPlugin;

public class WeaponBazookaPlugin extends AbstractWeaponPlugin {

    @Override
    public String getId() { return "weapon-bazooka"; }

    @Override
    public String getDisplayName() { return "Bazooka"; }

    @Override
    public void modifyShot(ShotConfig config, GameData gameData) {
        int level = getLevel(gameData);
        if (level == 0) return;
        config.explosive = true;
        config.explosionRadius = 50.0 + level * 30.0;
    }
}
