package sdu.asteroids.powerup.piercing;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.ShotConfig;
import sdu.asteroids.common.data.World;
import sdu.asteroids.powerup.common.AbstractWeaponPlugin;

public class WeaponPiercingPlugin extends AbstractWeaponPlugin {

    @Override
    public String getId() { return "weapon-piercing"; }

    @Override
    public String getDisplayName() { return "Piercing"; }

    @Override
    public void modifyShot(ShotConfig config, GameData gameData) {
        int level = getLevel(gameData);
        if (level == 0) return;
        config.pierceCount += level;
    }
}
