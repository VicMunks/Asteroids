module sdu.asteroids.common {
    exports sdu.asteroids.common.data;
    exports sdu.asteroids.common.services;
    exports sdu.asteroids.common.util;
    uses sdu.asteroids.common.services.IGamePluginService;
    uses sdu.asteroids.common.services.IEntityProcessorService;
    uses sdu.asteroids.common.services.IPostEntityProcessorService;
    uses sdu.asteroids.common.services.BulletSPI;
    uses sdu.asteroids.common.services.AsteroidSplittingSPI;
    uses sdu.asteroids.common.services.ScoreSPI;
    uses sdu.asteroids.common.services.EnemySpawnSPI;
    uses sdu.asteroids.common.services.AsteroidSpawnSPI;
    uses sdu.asteroids.common.services.PowerupSPI;
    uses sdu.asteroids.common.services.WeaponModifierSPI;
}
