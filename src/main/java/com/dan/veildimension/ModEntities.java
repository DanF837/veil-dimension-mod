package com.dan.veildimension;

import com.dan.veildimension.entity.ShadeStalkerEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public class ModEntities {

    public static final EntityType<ShadeStalkerEntity> SHADE_STALKER = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("veildimension", "shade_stalker"),
            EntityType.Builder.create(ShadeStalkerEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6f, 1.95f) // Similar to player size
                    .maxTrackingRange(8)
                    .build()
    );

    /**
     * Enable natural spawning of Shade Stalkers in the Veil dimension
     */
    public static void registerSpawns() {
        net.fabricmc.fabric.api.biome.v1.BiomeModifications.addSpawn(
                context -> context.getBiomeKey().getValue().getNamespace().equals("veildimension"),
                SpawnGroup.MONSTER,
                SHADE_STALKER,
                50, // Weight of spawning
                1,  // Min group size
                1   // Max group size (always spawn alone)
        );

        // Allow spawning at any light level in the Veil dimension
        SpawnRestriction.register(
                SHADE_STALKER,
                SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                (type, world, spawnReason, pos, random) -> {
                    // Check if block below is solid and allows spawning
                    return world.getBlockState(pos.down()).allowsSpawning(world, pos.down(), type);
                }
        );
    }

    public static void initialize() {
        // Register entity attributes
        FabricDefaultAttributeRegistry.register(SHADE_STALKER, ShadeStalkerEntity.createShadeStalkerAttributes());

        // Register spawning
        registerSpawns();

        System.out.println("[VeilDimension] Registered Shade Stalker entity");
    }
}