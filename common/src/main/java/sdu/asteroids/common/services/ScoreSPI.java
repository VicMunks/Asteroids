package sdu.asteroids.common.services;

import sdu.asteroids.common.data.EntityType;

public interface ScoreSPI {
    void recordKill(EntityType destroyed, int points);
}
