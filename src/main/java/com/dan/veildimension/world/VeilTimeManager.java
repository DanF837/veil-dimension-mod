package com.dan.veildimension.world;

import com.dan.veildimension.ModDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;

public class VeilTimeManager {

    // Darker twilight/night time (18000 = midnight)
    private static final long TWILIGHT_TIME = 18000;

    /**
     * Initialize the time manager
     */
    public static void initialize() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            // Only affect the Veil dimension
            if (world.getRegistryKey() == ModDimensions.VEIL_WORLD) {
                lockTimeToTwilight(world);
            }
        });
    }

    /**
     * Lock the time to perpetual twilight
     */
    private static void lockTimeToTwilight(ServerWorld world) {
        long currentTime = world.getTimeOfDay();
        long dayTime = currentTime % 24000;

        // If time drifts away from twilight, reset it
        if (dayTime != TWILIGHT_TIME) {
            world.setTimeOfDay((currentTime - dayTime) + TWILIGHT_TIME);
        }
    }
}