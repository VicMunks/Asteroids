package sdu.asteroids.powerup.common;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.ShotConfig;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.WeaponModifierSPI;

public abstract class AbstractWeaponPlugin extends AbstractPowerupPlugin implements WeaponModifierSPI {

    @Override
    public String getCategory() { return "WEAPON"; }

    @Override
    public void start(GameData gameData, World world) {
        gameData.getActiveWeapons().remove(getId());
    }

    @Override
    public void onAcquire(GameData gameData, World world) {
        gameData.getActiveWeapons().merge(getId(), 1, Integer::sum);
    }

    protected int getLevel(GameData gameData) {
        return gameData.getActiveWeapons().getOrDefault(getId(), 0);
    }
}
