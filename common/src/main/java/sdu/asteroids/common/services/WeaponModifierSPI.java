package sdu.asteroids.common.services;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.ShotConfig;

public interface WeaponModifierSPI {
    void modifyShot(ShotConfig config, GameData gameData);
}
