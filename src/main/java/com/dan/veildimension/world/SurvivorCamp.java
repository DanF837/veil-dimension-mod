package com.dan.veildimension.world;

import com.dan.veildimension.ModBlocks;
import com.dan.veildimension.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class SurvivorCamp {

    /**
     * Generate a random survivor camp
     */
    public static void generate(ServerWorld world, BlockPos pos, Random random) {
        int variant = random.nextInt(4); // 4 different camp types

        switch (variant) {
            case 0:
                generateAbandonedCamp(world, pos);
                break;
            case 1:
                generateActiveCamp(world, pos);
                break;
            case 2:
                generateRuinedCamp(world, pos);
                break;
            case 3:
                generateSmallCamp(world, pos);
                break;
        }

        System.out.println("[VeilDimension] Generated Survivor Camp (variant " + variant + ") at " + pos);
    }

    /**
     * Build a foundation platform for the camp
     */
    private static void buildFoundation(ServerWorld world, BlockPos pos) {
        // Build a 7x7 platform of dirt/stone
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                BlockPos foundationPos = pos.add(x, -1, z);

                // Place dirt as foundation
                world.setBlockState(foundationPos, Blocks.COARSE_DIRT.getDefaultState());

                // Fill any air gaps below (up to 3 blocks down)
                for (int y = -2; y >= -4; y--) {
                    BlockPos fillPos = pos.add(x, y, z);
                    if (world.getBlockState(fillPos).isAir()) {
                        world.setBlockState(fillPos, Blocks.DIRT.getDefaultState());
                    } else {
                        break; // Stop when we hit solid ground
                    }
                }
            }
        }
    }

    /**
     * Abandoned camp - has a sign with a message
     */
    private static void generateAbandonedCamp(ServerWorld world, BlockPos pos) {
        // Build foundation first
        buildFoundation(world, pos);

        // Campfire (extinguished)
        world.setBlockState(pos, Blocks.CAMPFIRE.getDefaultState()
                .with(net.minecraft.block.CampfireBlock.LIT, false));

        // A few blocks around it
        world.setBlockState(pos.add(1, 0, 1), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(pos.add(-1, 0, 1), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(pos.add(1, 0, -1), Blocks.COBBLESTONE.getDefaultState());

        // Chest with minimal supplies
        BlockPos chestPos = pos.add(2, 0, 0);
        world.setBlockState(chestPos, Blocks.CHEST.getDefaultState());

        BlockEntity be = world.getBlockEntity(chestPos);
        if (be instanceof ChestBlockEntity chest) {
            chest.setStack(13, new ItemStack(Items.BREAD, 2));
            chest.setStack(14, new ItemStack(Items.TORCH, 4));
            chest.markDirty();
        }

        // Sign with message
        BlockPos signPos = pos.add(-2, 0, 0);
        world.setBlockState(signPos, Blocks.OAK_SIGN.getDefaultState());

        BlockEntity signBe = world.getBlockEntity(signPos);
        if (signBe instanceof SignBlockEntity sign) {
            SignText frontText = new SignText()
                    .withMessage(0, Text.literal("Moved on."))
                    .withMessage(1, Text.literal("Heading to"))
                    .withMessage(2, Text.literal("main camp."))
                    .withMessage(3, Text.literal("- Survivor"));
            sign.setText(frontText, true);
            sign.markDirty();
        }
    }

    /**
     * Active camp - looks lived-in
     */
    private static void generateActiveCamp(ServerWorld world, BlockPos pos) {
        // Build foundation first
        buildFoundation(world, pos);

        // Lit campfire
        world.setBlockState(pos, Blocks.CAMPFIRE.getDefaultState()
                .with(net.minecraft.block.CampfireBlock.LIT, true));

        // Stone ring around campfire
        world.setBlockState(pos.add(1, 0, 0), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(pos.add(-1, 0, 0), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(pos.add(0, 0, 1), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(pos.add(0, 0, -1), Blocks.COBBLESTONE.getDefaultState());

        // Crafting table
        world.setBlockState(pos.add(2, 0, 2), Blocks.CRAFTING_TABLE.getDefaultState());

        // Chest with better supplies
        BlockPos chestPos = pos.add(-2, 0, -2);
        world.setBlockState(chestPos, Blocks.BARREL.getDefaultState());

        BlockEntity be = world.getBlockEntity(chestPos);
        if (be instanceof ChestBlockEntity chest) {
            chest.setStack(10, new ItemStack(Items.COOKED_BEEF, 8));
            chest.setStack(11, new ItemStack(Items.TORCH, 16));
            chest.setStack(13, new ItemStack(Items.IRON_INGOT, 4));
            chest.setStack(15, new ItemStack(Items.STICK, 8));
            chest.markDirty();
        }

        // Veil Lanterns
        world.setBlockState(pos.add(3, 0, 0), ModBlocks.VEIL_LANTERN.getDefaultState());
        world.setBlockState(pos.add(-3, 0, 0), ModBlocks.VEIL_LANTERN.getDefaultState());

        // Sign with message
        BlockPos signPos = pos.add(0, 0, 3);
        world.setBlockState(signPos, Blocks.OAK_SIGN.getDefaultState());

        BlockEntity signBe = world.getBlockEntity(signPos);
        if (signBe instanceof SignBlockEntity sign) {
            SignText frontText = new SignText()
                    .withMessage(0, Text.literal("Rest here."))
                    .withMessage(1, Text.literal("Stay safe."))
                    .withMessage(2, Text.literal("The Veil"))
                    .withMessage(3, Text.literal("watches."));
            sign.setText(frontText, true);
            sign.markDirty();
        }
    }

    /**
     * Ruined camp - attacked or abandoned in panic
     */
    private static void generateRuinedCamp(ServerWorld world, BlockPos pos) {
        // Build foundation first
        buildFoundation(world, pos);

        // Broken campfire
        world.setBlockState(pos, Blocks.SOUL_CAMPFIRE.getDefaultState()
                .with(net.minecraft.block.CampfireBlock.LIT, false));

        // Scattered blocks
        world.setBlockState(pos.add(2, 0, 1), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(pos.add(-1, 0, -2), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(pos.add(1, 0, 2), Blocks.ANDESITE.getDefaultState());

        // Broken chest (open, half-destroyed)
        BlockPos chestPos = pos.add(-2, 0, 0);
        world.setBlockState(chestPos, Blocks.CHEST.getDefaultState());

        BlockEntity be = world.getBlockEntity(chestPos);
        if (be instanceof ChestBlockEntity chest) {
            // Almost empty - just bones and a torch
            chest.setStack(4, new ItemStack(Items.BONE, 3));
            chest.setStack(22, new ItemStack(Items.TORCH, 1));
            chest.markDirty();
        }

        // Ominous sign
        BlockPos signPos = pos.add(2, 0, 0);
        world.setBlockState(signPos, Blocks.CRIMSON_SIGN.getDefaultState());

        BlockEntity signBe = world.getBlockEntity(signPos);
        if (signBe instanceof SignBlockEntity sign) {
            SignText frontText = new SignText()
                    .withMessage(0, Text.literal("They came"))
                    .withMessage(1, Text.literal("at night."))
                    .withMessage(2, Text.literal("Run."))
                    .withMessage(3, Text.literal(""));
            sign.setText(frontText, true);
            sign.markDirty();
        }
    }

    /**
     * Small minimal camp - just passing through
     */
    private static void generateSmallCamp(ServerWorld world, BlockPos pos) {
        // Build foundation first
        buildFoundation(world, pos);

        // Simple campfire
        world.setBlockState(pos, Blocks.CAMPFIRE.getDefaultState()
                .with(net.minecraft.block.CampfireBlock.LIT, true));

        // Log to sit on
        world.setBlockState(pos.add(0, 0, 2), Blocks.OAK_LOG.getDefaultState());

        // Single lantern
        world.setBlockState(pos.add(-2, 0, 0), ModBlocks.VEIL_LANTERN.getDefaultState());

        // Sign
        BlockPos signPos = pos.add(2, 0, 0);
        world.setBlockState(signPos, Blocks.BIRCH_SIGN.getDefaultState());

        BlockEntity signBe = world.getBlockEntity(signPos);
        if (signBe instanceof SignBlockEntity sign) {
            SignText frontText = new SignText()
                    .withMessage(0, Text.literal("Passing"))
                    .withMessage(1, Text.literal("through."))
                    .withMessage(2, Text.literal("Stay warm."))
                    .withMessage(3, Text.literal("- Traveler"));
            sign.setText(frontText, true);
            sign.markDirty();
        }
    }
}