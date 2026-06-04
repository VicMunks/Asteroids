module sdu.asteroids.shop {
    requires sdu.asteroids.common;
    provides sdu.asteroids.common.services.IGamePluginService
        with sdu.asteroids.shop.ShopPlugin;
    provides sdu.asteroids.common.services.IEntityProcessorService
        with sdu.asteroids.shop.ShopPlugin;
}
