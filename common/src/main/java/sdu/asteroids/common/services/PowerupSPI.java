package sdu.asteroids.common.services;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.World;

public interface PowerupSPI {
    String getId();
    String getDisplayName();
    String getCategory();
    void onAcquire(GameData gameData, World world);
}
