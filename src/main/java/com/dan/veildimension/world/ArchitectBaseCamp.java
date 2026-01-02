package com.dan.veildimension.world;

import com.dan.veildimension.ModBlocks;
import com.dan.veildimension.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class ArchitectBaseCamp {

    /**
     * Generate ruined Architect's base camp with collapsed walls
     */
    public static void generate(ServerWorld world, BlockPos pos) {
        Random random = world.getRandom();

        System.out.println("[VeilDimension] Generating ruined Architect base camp at " + pos);

        // Clear area first
        for (int x = -6; x <= 6; x++) {
            for (int z = -6; z <= 6; z++) {
                for (int y = 0; y <= 5; y++) {
                    BlockPos clearPos = pos.add(x, y, z);
                    if (world.getBlockState(clearPos).isAir() ||
                            world.getBlockState(clearPos).isReplaceable()) {
                        world.setBlockState(clearPos, Blocks.AIR.getDefaultState(), 3);
                    }
                }
            }
        }

        // 1. FLOOR OUTLINE - Cracked and incomplete
        generateBrokenFloor(world, pos, random);

        // 2. PARTIAL WALLS - Only a few sections remain standing
        generateCollapsedWalls(world, pos, random);

        // 3. RUBBLE PILES - Scattered debris
        generateRubble(world, pos, random);

        // 4. CENTRAL CAMPFIRE - Still burning
        world.setBlockState(pos.add(0, 1, 0), Blocks.SOUL_CAMPFIRE.getDefaultState(), 3);

        // 5. HIDDEN CHEST - Half-buried in rubble
        generateHiddenChest(world, pos.add(2, 1, -2), random);

        // 6. PURPLE LANTERNS - A few scattered around
        world.setBlockState(pos.add(-3, 1, 2), ModBlocks.VEIL_LANTERN.getDefaultState(), 3);
        world.setBlockState(pos.add(3, 1, -3), ModBlocks.VEIL_LANTERN.getDefaultState(), 3);

        // 7. OVERGROWTH - Vines and moss
        generateOvergrowth(world, pos, random);

        System.out.println("[VeilDimension] Ruined base camp generated!");
    }

    /**
     * Generate broken, incomplete floor
     */
    private static void generateBrokenFloor(ServerWorld world, BlockPos pos, Random random) {
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                // Only place floor blocks randomly (70% chance)
                if (random.nextFloat() < 0.7f) {
                    BlockPos floorPos = pos.add(x, 0, z);

                    // Mix of stone bricks, cracked, and mossy
                    float rand = random.nextFloat();
                    if (rand < 0.5f) {
                        world.setBlockState(floorPos, Blocks.STONE_BRICKS.getDefaultState(), 3);
                    } else if (rand < 0.75f) {
                        world.setBlockState(floorPos, Blocks.CRACKED_STONE_BRICKS.getDefaultState(), 3);
                    } else {
                        world.setBlockState(floorPos, Blocks.MOSSY_STONE_BRICKS.getDefaultState(), 3);
                    }
                }
            }
        }
    }

    /**
     * Generate partial walls - only some sections remain
     */
    private static void generateCollapsedWalls(ServerWorld world, BlockPos pos, Random random) {
        // NORTH WALL - Partially standing (1-3 blocks high, incomplete)
        for (int x = -3; x <= 3; x++) {
            if (random.nextFloat() < 0.6f) { // 60% chance each segment exists
                int height = random.nextInt(3) + 1; // 1-3 blocks high
                for (int y = 1; y <= height; y++) {
                    world.setBlockState(pos.add(x, y, -4), Blocks.STONE_BRICKS.getDefaultState(), 3);
                }
            }
        }

        // EAST WALL - Mostly collapsed (just a few blocks)
        for (int z = -3; z <= 3; z++) {
            if (random.nextFloat() < 0.3f) { // Only 30% standing
                world.setBlockState(pos.add(4, 1, z), Blocks.CRACKED_STONE_BRICKS.getDefaultState(), 3);
                if (random.nextFloat() < 0.5f) {
                    world.setBlockState(pos.add(4, 2, z), Blocks.STONE_BRICKS.getDefaultState(), 3);
                }
            }
        }

        // SOUTH WALL - Single pillar remains
        world.setBlockState(pos.add(-2, 1, 4), Blocks.STONE_BRICKS.getDefaultState(), 3);
        world.setBlockState(pos.add(-2, 2, 4), Blocks.STONE_BRICKS.getDefaultState(), 3);
        world.setBlockState(pos.add(-2, 3, 4), Blocks.CRACKED_STONE_BRICKS.getDefaultState(), 3);

        // WEST WALL - Completely collapsed (no wall)
    }

    /**
     * Generate rubble piles from collapsed walls
     */
    private static void generateRubble(ServerWorld world, BlockPos pos, Random random) {
        // Scattered rubble piles
        int[] rubbleX = {-3, 2, -1, 4, -4, 1};
        int[] rubbleZ = {2, -3, 4, 1, -2, -4};

        for (int i = 0; i < rubbleX.length; i++) {
            BlockPos rubblePos = pos.add(rubbleX[i], 1, rubbleZ[i]);

            // Pile of 1-3 blocks
            int height = random.nextInt(3) + 1;
            for (int y = 0; y < height; y++) {
                float rand = random.nextFloat();
                if (rand < 0.6f) {
                    world.setBlockState(rubblePos.up(y), Blocks.COBBLESTONE.getDefaultState(), 3);
                } else {
                    world.setBlockState(rubblePos.up(y), Blocks.STONE.getDefaultState(), 3);
                }
            }
        }
    }

    /**
     * Generate hidden chest with Journal #18
     */
    private static void generateHiddenChest(ServerWorld world, BlockPos pos, Random random) {
        // Place chest facing a direction
        world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH), 3);

        // Add rubble around it (half-buried)
        world.setBlockState(pos.add(-1, 0, 0), Blocks.COBBLESTONE.getDefaultState(), 3);
        world.setBlockState(pos.add(1, 0, 0), Blocks.STONE.getDefaultState(), 3);

        // Fill chest with loot
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ChestBlockEntity chest) {
            Inventory inventory = chest;

            // Add Journal #18
            chest.setStack(13, new ItemStack(ModItems.JOURNAL_ENTRY_18));

            // Some supplies
            inventory.setStack(10, new ItemStack(Items.BREAD, random.nextInt(3) + 2));
            inventory.setStack(11, new ItemStack(Items.TORCH, random.nextInt(8) + 4));
            inventory.setStack(15, new ItemStack(Items.IRON_INGOT, random.nextInt(3) + 1));

            // Purple dye (thematic)
            inventory.setStack(16, new ItemStack(Items.PURPLE_DYE, random.nextInt(4) + 2));
        }
    }

    /**
     * Add vines and moss for overgrown look
     */
    private static void generateOvergrowth(ServerWorld world, BlockPos pos, Random random) {
        // Vines hanging from remaining walls
        for (int i = 0; i < 8; i++) {
            int x = random.nextInt(9) - 4;
            int z = random.nextInt(9) - 4;
            int y = random.nextInt(3) + 1;

            BlockPos vinePos = pos.add(x, y, z);

            // Only place vines where there's a block to hang from
            if (world.getBlockState(vinePos).isSolidBlock(world, vinePos)) {
                BlockPos below = vinePos.down();
                if (world.getBlockState(below).isAir()) {
                    world.setBlockState(below, Blocks.VINE.getDefaultState(), 3);
                }
            }
        }

        // Moss carpet on some floor blocks
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (random.nextFloat() < 0.2f) { // 20% chance
                    BlockPos mossPos = pos.add(x, 1, z);
                    if (world.getBlockState(mossPos).isAir() &&
                            world.getBlockState(mossPos.down()).isSolidBlock(world, mossPos.down())) {
                        world.setBlockState(mossPos, Blocks.MOSS_CARPET.getDefaultState(), 3);
                    }
                }
            }
        }
    }
}