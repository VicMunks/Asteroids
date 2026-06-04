package sdu.asteroids.common.services;

import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.ShotConfig;

public interface BulletSPI {
    void spawnBullet(double x, double y, double rotation, EntityType owner);
    void spawnShot(ShotConfig config, double x, double y, double rotation, EntityType owner);
}
