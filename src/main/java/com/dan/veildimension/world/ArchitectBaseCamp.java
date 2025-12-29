package com.dan.veildimension.world;

import com.dan.veildimension.ModBlocks;
import com.dan.veildimension.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ArchitectBaseCamp {

    /**
     * Generate the Architect's base camp at a position
     */
    public static void generate(ServerWorld world, BlockPos pos) {
        System.out.println("[VeilDimension] Generating Architect's Base Camp at " + pos);

        // Clear the area first
        clearArea(world, pos, 7, 5, 7);

        // Build floor
        buildFloor(world, pos);

        // Build walls
        buildWalls(world, pos);

        // Build roof
        buildRoof(world, pos);

        // Add interior furnishings
        addFurnishings(world, pos);

        // Add supply chest with Journal #18
        addSupplyChest(world, pos);
    }

    /**
     * Clear area for building
     */
    private static void clearArea(ServerWorld world, BlockPos origin, int width, int height, int depth) {
        for (int x = -1; x < width + 1; x++) {
            for (int y = 0; y < height + 2; y++) {
                for (int z = -1; z < depth + 1; z++) {
                    BlockPos clearPos = origin.add(x, y, z);
                    if (!world.getBlockState(clearPos).isOf(Blocks.AIR)) {
                        world.setBlockState(clearPos, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }

    /**
     * Build the floor
     */
    private static void buildFloor(ServerWorld world, BlockPos origin) {
        for (int x = 0; x < 7; x++) {
            for (int z = 0; z < 7; z++) {
                BlockPos floorPos = origin.add(x, -1, z);
                world.setBlockState(floorPos, Blocks.DEEPSLATE_TILES.getDefaultState());
            }
        }
    }

    /**
     * Build the walls
     */
    private static void buildWalls(ServerWorld world, BlockPos origin) {
        // North wall (z=0)
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 4; y++) {
                if (x == 3 && y < 2) continue; // Leave door space
                world.setBlockState(origin.add(x, y, 0), Blocks.DEEPSLATE_BRICKS.getDefaultState());
            }
        }

        // South wall (z=6)
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 4; y++) {
                world.setBlockState(origin.add(x, y, 6), Blocks.DEEPSLATE_BRICKS.getDefaultState());
            }
        }

        // West wall (x=0)
        for (int z = 1; z < 6; z++) {
            for (int y = 0; y < 4; y++) {
                world.setBlockState(origin.add(0, y, z), Blocks.DEEPSLATE_BRICKS.getDefaultState());
            }
        }

        // East wall (x=6)
        for (int z = 1; z < 6; z++) {
            for (int y = 0; y < 4; y++) {
                world.setBlockState(origin.add(6, y, z), Blocks.DEEPSLATE_BRICKS.getDefaultState());
            }
        }
    }

    /**
     * Build the roof
     */
    private static void buildRoof(ServerWorld world, BlockPos origin) {
        for (int x = 0; x < 7; x++) {
            for (int z = 0; z < 7; z++) {
                world.setBlockState(origin.add(x, 4, z), Blocks.DEEPSLATE_TILE_SLAB.getDefaultState());
            }
        }
    }

    /**
     * Add interior furnishings
     */
    private static void addFurnishings(ServerWorld world, BlockPos origin) {
        // Crafting table (left side)
        world.setBlockState(origin.add(1, 0, 1), Blocks.CRAFTING_TABLE.getDefaultState());

        // Furnace (left side)
        world.setBlockState(origin.add(1, 0, 2), Blocks.FURNACE.getDefaultState());

        // Bed (back right corner)
        BlockPos bedPos = origin.add(4, 0, 5);
        world.setBlockState(bedPos, Blocks.PURPLE_BED.getDefaultState()
                .with(net.minecraft.block.BedBlock.PART, net.minecraft.block.enums.BedPart.FOOT)
                .with(net.minecraft.block.BedBlock.FACING, Direction.EAST));
        world.setBlockState(bedPos.east(), Blocks.PURPLE_BED.getDefaultState()
                .with(net.minecraft.block.BedBlock.PART, net.minecraft.block.enums.BedPart.HEAD)
                .with(net.minecraft.block.BedBlock.FACING, Direction.EAST));

    }

    /**
     * Add supply chest with Journal #18
     */
    private static void addSupplyChest(ServerWorld world, BlockPos origin) {
        BlockPos chestPos = origin.add(5, 0, 1);

        // Place chest
        world.setBlockState(chestPos, Blocks.CHEST.getDefaultState());

        // Add items to chest
        BlockEntity blockEntity = world.getBlockEntity(chestPos);
        if (blockEntity instanceof ChestBlockEntity chest) {
            // Add Journal #18
            chest.setStack(13, new ItemStack(ModItems.JOURNAL_ENTRY_18)); // Center slot

            // Add supplies
            chest.setStack(10, new ItemStack(Items.COOKED_BEEF, 16));
            chest.setStack(11, new ItemStack(Items.TORCH, 32));
            chest.setStack(12, new ItemStack(Items.IRON_PICKAXE));
            chest.setStack(14, new ItemStack(Items.IRON_SWORD));
            chest.setStack(15, new ItemStack(Items.IRON_AXE));
            chest.setStack(16, new ItemStack(Items.BOW));
            chest.setStack(17, new ItemStack(Items.ARROW, 64));

            chest.markDirty();
        }
    }
}