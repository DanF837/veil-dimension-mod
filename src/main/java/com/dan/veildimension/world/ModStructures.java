package com.dan.veildimension.world;

import com.dan.veildimension.ModBlocks;
import com.dan.veildimension.ModDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

public class ModStructures {

    /**
     * Initialize structure spawning
     */
    public static void initialize() {
        // Register world load event to spawn structures
        ServerWorldEvents.LOAD.register((server, world) -> {
            // Only spawn in Veil dimension
            if (world.getRegistryKey() == ModDimensions.VEIL_WORLD) {
                System.out.println("[VeilDimension] Veil world loaded, checking for structures...");

                // Check if structures have been generated
                VeilStructureData data = getOrCreateStructureData(world);

                if (!data.hasGeneratedStructures()) {
                    System.out.println("[VeilDimension] Generating structures for the first time...");
                    generateInitialStructures(world, data);

                }
            }
        });
    }

    /**
     * Get or create persistent structure data
     */
    private static VeilStructureData getOrCreateStructureData(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        return manager.getOrCreate(
                VeilStructureData.getPersistentStateType(),
                "veil_structures"
        );
    }

    /**
     * Generate initial structures in the world
     */
    private static void generateInitialStructures(ServerWorld world, VeilStructureData data) {
        // Only generate survivor camps at world load
        // Lanterns and base camp are generated when first player enters

        Random random = world.getRandom();

        System.out.println("[VeilDimension] Generating survivor camps...");
        int campsSpawned = 0;
        int attempts = 0;
        int maxAttempts = 100;

        while (campsSpawned < 30 && attempts < maxAttempts) {
            attempts++;

            // Random position within 1000 blocks
            int x = random.nextBetween(-1000, 1000);
            int z = random.nextBetween(-1000, 1000);
            BlockPos campPos = new BlockPos(x, 64, z);

            BlockPos groundPos = findGroundLevelFromTop(world, campPos);

            if (groundPos != null) {
                BlockState groundBlock = world.getBlockState(groundPos.down());

                if (!groundBlock.isLiquid()) {
                    SurvivorCamp.generate(world, groundPos, random);
                    campsSpawned++;
                } else {
                    System.out.println("[VeilDimension] Skipped camp spawn on water at " + groundPos);
                }
            }
        }

        System.out.println("[VeilDimension] Generated " + campsSpawned + " survivor camps across the dimension (attempted " + attempts + " locations)");

        // DON'T mark as generated here - wait for player to enter
    }

    /**
     * Generate lantern trail from player position (called on first entry)
     * Structures are now generated at world load, this is just for compatibility
     */
    public static void generateLanternTrailFromPlayer(ServerPlayerEntity player, ServerWorld world) {
        // Check if structures have already been generated for this world
        VeilStructureData data = getOrCreateStructureData(world);

        if (data.hasGeneratedStructures()) {
            System.out.println("[VeilDimension] Lanterns already generated, skipping");
            return;
        }

        BlockPos playerPos = player.getBlockPos();

        System.out.println("[VeilDimension] Generating lantern trail from player at " + playerPos);

        // Generate trail heading east from player position
        generateLanternTrail(world, playerPos, Direction.EAST, 30);

        // Mark as generated so it doesn't happen again
        data.markGenerated();
        data.markDirty();

        System.out.println("[VeilDimension] Lantern trail and base camp generated!");
    }
    /**
     * Generate a trail of lanterns in a direction
     */
    private static void generateLanternTrail(ServerWorld world, BlockPos start, Direction direction, int length) {
        BlockPos.Mutable pos = start.mutableCopy();
        BlockPos lastLanternPos = start;

        for (int i = 0; i < length; i++) {
            // Move in direction
            pos.move(direction, 10);

            // Make sure chunk is loaded
            if (!world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                world.getChunk(pos);
            }

            // Find ground level (search from high to low)
            BlockPos groundPos = findGroundLevelFromTop(world, pos);

            if (groundPos != null) {
                // Place lantern on top of ground
                world.setBlockState(groundPos, ModBlocks.VEIL_LANTERN.getDefaultState());
                System.out.println("[VeilDimension] Placed lantern at " + groundPos);
                lastLanternPos = groundPos;
            } else {
                System.out.println("[VeilDimension] Could not find ground at " + pos);
            }
        }

        // Generate base camp at the end of the trail
        if (lastLanternPos != null) {
            BlockPos baseCampPos = lastLanternPos.offset(direction, 20);
            BlockPos groundPos = findGroundLevelFromTop(world, baseCampPos);
            if (groundPos != null) {
                ArchitectBaseCamp.generate(world, groundPos);
            }
        }
    }

    /**
     * Find the ground level by searching from top down
     * Handles water - places on lake/ocean floor instead of surface
     */
    private static BlockPos findGroundLevelFromTop(ServerWorld world, BlockPos pos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(pos.getX(), world.getTopY(), pos.getZ());

        // Search down from top
        for (int y = world.getTopY(); y > world.getBottomY(); y--) {
            mutable.setY(y);
            BlockState currentState = world.getBlockState(mutable);
            BlockState belowState = world.getBlockState(mutable.down());

            // Found solid ground with air or water above
            if (belowState.isOpaque() && !belowState.isLiquid()) {
                // Check if we're underwater
                if (currentState.isLiquid()) {
                    // Place on ocean/lake floor - return current position (on the solid block)
                    return mutable.toImmutable();
                } else if (currentState.isAir()) {
                    // Normal ground - place one block above
                    return mutable.toImmutable();
                }
            }
        }

        // Fallback: place at original Y level if nothing found
        return pos.withY(64);
    }

    /**
     * Persistent state to track generated structures
     */
    public static class VeilStructureData extends PersistentState {
        private boolean generated = false;

        public VeilStructureData() {
        }

        public boolean hasGeneratedStructures() {
            return generated;
        }

        public void markGenerated() {
            this.generated = true;
        }

        public static Type<VeilStructureData> getPersistentStateType() {
            return new Type<>(
                    VeilStructureData::new,
                    (nbt, registryLookup) -> {
                        VeilStructureData data = new VeilStructureData();
                        data.generated = nbt.getBoolean("generated");
                        return data;
                    },
                    null
            );
        }

        @Override
        public net.minecraft.nbt.NbtCompound writeNbt(net.minecraft.nbt.NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registryLookup) {
            nbt.putBoolean("generated", generated);
            return nbt;
        }
    }
}