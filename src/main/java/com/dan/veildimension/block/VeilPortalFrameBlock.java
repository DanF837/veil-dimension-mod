package com.dan.veildimension.block;

import com.dan.veildimension.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class VeilPortalFrameBlock extends Block
{
    public VeilPortalFrameBlock()
    {
        super(Settings.create()
                .mapColor(MapColor.PURPLE)
                .strength(50.0F, 1200.0F)
                .requiresTool()
                .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                .luminance(state -> 7));
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type)
    {
        tooltip.add(Text.translatable("block.veildimension.veil_portal_frame.tooltip"));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if (!world.isClient)
        {
            // When a frame block is broken, destroy any adjacent portal blocks
            destroyAdjacentPortal(world, pos);
        }
        return super.onBreak(world, pos, state, player);
    }

    private void destroyAdjacentPortal(World world, BlockPos framePos)
    {
        // Check all 6 directions for portal blocks
        for (Direction dir : Direction.values())
        {
            BlockPos checkPos = framePos.offset(dir);
            if (world.getBlockState(checkPos).isOf(ModBlocks.VEIL_PORTAL))
            {
                // Found a portal block - destroy the entire portal
                destroyEntirePortal(world, checkPos);
                return; // Only need to find one to destroy the whole portal
            }
        }
    }

    private void destroyEntirePortal(World world, BlockPos startPos)
    {
        // Use flood fill to find and destroy all connected portal blocks
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toCheck = new LinkedList<>();
        toCheck.add(startPos);
        visited.add(startPos);

        while (!toCheck.isEmpty())
        {
            BlockPos current = toCheck.poll();

            // Destroy this portal block
            if (world.getBlockState(current).isOf(ModBlocks.VEIL_PORTAL))
            {
                world.setBlockState(current, Blocks.AIR.getDefaultState(), 3);
            }

            // Check all 6 directions for more portal blocks
            for (Direction dir : Direction.values())
            {
                BlockPos neighbor = current.offset(dir);

                if (!visited.contains(neighbor) && world.getBlockState(neighbor).isOf(ModBlocks.VEIL_PORTAL))
                {
                    visited.add(neighbor);
                    toCheck.add(neighbor);
                }
            }
        }
    }
}