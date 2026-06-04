module sdu.asteroids.core {
    requires sdu.asteroids.common;
    requires javafx.graphics;
    requires javafx.controls;
    requires spring.context;
    requires spring.beans;
    requires spring.core;
    opens sdu.asteroids.core to javafx.graphics, spring.core, spring.beans, spring.context;
}
