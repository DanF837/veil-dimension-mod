package com.dan.veildimension.util;

import com.dan.veildimension.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SupplyCacheSpawner {

    /**
     * Spawn a supply cache near the player's spawn point in the Veil
     */
    public static void spawnSupplyCacheNearPlayer(ServerPlayerEntity player, World world) {
        BlockPos playerPos = player.getBlockPos();

        System.out.println("[VeilDimension] Attempting to spawn supply cache near " + playerPos);

        // Find a safe location 3-8 blocks away
        BlockPos cachePos = findSafeLocation(world, playerPos, 3, 8);

        if (cachePos != null) {
            System.out.println("[VeilDimension] Spawning supply cache at " + cachePos);
            // Place the supply cache
            world.setBlockState(cachePos, ModBlocks.SUPPLY_CACHE.getDefaultState());

            // Send message to player
            player.sendMessage(net.minecraft.text.Text.literal("§7§o[You notice a strange cache nearby...]§r"), false);
        } else {
            System.out.println("[VeilDimension] Failed to find safe location, using fallback");
            // Fallback: place it right next to player
            BlockPos fallback = playerPos.add(2, 0, 0);
            world.setBlockState(fallback, ModBlocks.SUPPLY_CACHE.getDefaultState());
            player.sendMessage(net.minecraft.text.Text.literal("§7§o[You notice a strange cache nearby...]§r"), false);
        }
    }

    /**
     * Find a safe solid ground location near the target position
     */
    private static BlockPos findSafeLocation(World world, BlockPos center, int minRadius, int maxRadius) {
        // Try to find a location in a spiral pattern
        for (int radius = minRadius; radius <= maxRadius; radius++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    // Check if this is roughly at the desired radius
                    if (Math.abs(x) == radius || Math.abs(z) == radius) {
                        BlockPos checkPos = center.add(x, 0, z);

                        // Find ground level
                        BlockPos groundPos = findGround(world, checkPos);

                        if (groundPos != null && isSafeLocation(world, groundPos)) {
                            return groundPos;
                        }
                    }
                }
            }
        }

        // Fallback: just place it next to the player
        return center.add(3, 0, 0);
    }

    /**
     * Find solid ground below a position
     */
    private static BlockPos findGround(World world, BlockPos start) {
        BlockPos.Mutable pos = start.mutableCopy();

        // Search down
        for (int i = 0; i < 10; i++) {
            if (world.getBlockState(pos).isOpaque() &&
                    world.getBlockState(pos.up()).isAir()) {
                return pos.up().toImmutable();
            }
            pos.move(0, -1, 0);
        }

        // Search up
        pos = start.mutableCopy();
        for (int i = 0; i < 10; i++) {
            if (world.getBlockState(pos).isOpaque() &&
                    world.getBlockState(pos.up()).isAir()) {
                return pos.up().toImmutable();
            }
            pos.move(0, 1, 0);
        }

        return null;
    }

    /**
     * Check if a location is safe to place the cache
     */
    private static boolean isSafeLocation(World world, BlockPos pos) {
        // Must have air above
        if (!world.getBlockState(pos).isAir()) {
            return false;
        }

        // Must have solid ground below
        if (!world.getBlockState(pos.down()).isOpaque()) {
            return false;
        }

        // Must have 2 blocks of air above for visibility
        if (!world.getBlockState(pos.up()).isAir()) {
            return false;
        }

        return true;
    }
}