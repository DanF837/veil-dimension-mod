package com.dan.veildimension.item;

import com.dan.veildimension.ModBlocks;
import com.dan.veildimension.block.VeilPortalBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.List;

public class VeilIgniterItem extends Item
{
    public VeilIgniterItem(Settings settings)
    {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
    {
        tooltip.add(Text.translatable("item.veildimension.veil_igniter.tooltip"));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        PlayerEntity player = context.getPlayer();

        // Check if clicking on a portal frame
        if (blockState.isOf(ModBlocks.VEIL_PORTAL_FRAME))
        {
            if (!world.isClient)
            {
                if (VeilPortalBlock.tryCreatePortal(world, blockPos))
                {
                    // Damage the item
                    ItemStack itemStack = context.getStack();
                    if (player != null && !player.getAbilities().creativeMode)
                    {
                        itemStack.damage(1, player, EquipmentSlot.MAINHAND);
                    }
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.CONSUME;
        }

        // If used on anything else, show purple flame block and particles
        if (!world.isClient)
        {
            ServerWorld serverWorld = (ServerWorld) world;
            BlockPos flamePos = blockPos.offset(context.getSide());

            // Check if we can place a flame there (must be air and on top of solid block)
            BlockPos belowFlame = flamePos.down();
            boolean canPlaceFire = world.getBlockState(flamePos).isAir() &&
                    world.getBlockState(belowFlame).isSolidBlock(world, belowFlame);

            if (canPlaceFire)
            {
                // Place soul fire (purple flame!) temporarily
                world.setBlockState(flamePos, Blocks.SOUL_FIRE.getDefaultState(), 3);

                // Schedule it to disappear after 2 seconds (40 ticks)
                world.scheduleBlockTick(flamePos, Blocks.SOUL_FIRE, 40);
            }

            // Spawn purple particles around it (whether fire placed or not)
            for (int i = 0; i < 20; i++)
            {
                double offsetX = world.random.nextDouble() * 0.6 - 0.3;
                double offsetY = world.random.nextDouble() * 0.5;
                double offsetZ = world.random.nextDouble() * 0.6 - 0.3;

                serverWorld.spawnParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        flamePos.getX() + 0.5 + offsetX,
                        flamePos.getY() + 0.3 + offsetY,
                        flamePos.getZ() + 0.5 + offsetZ,
                        1, 0, 0.05, 0, 0.01
                );

                serverWorld.spawnParticles(
                        ParticleTypes.PORTAL,
                        flamePos.getX() + 0.5 + offsetX,
                        flamePos.getY() + offsetY,
                        flamePos.getZ() + 0.5 + offsetZ,
                        1, 0, 0.1, 0, 0.05
                );
            }

            // Play sounds
            world.playSound(null, flamePos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                    SoundCategory.BLOCKS, 0.5F, 1.5F);
            world.playSound(null, flamePos, SoundEvents.ITEM_FIRECHARGE_USE,
                    SoundCategory.BLOCKS, 0.3F, 1.2F);

            // Send lore message to player
            if (player != null)
            {
                player.sendMessage(Text.translatable("chat.veildimension.igniter_failed"), true);
            }

            // Damage the item slightly
            ItemStack itemStack = context.getStack();
            if (player != null && !player.getAbilities().creativeMode)
            {
                itemStack.damage(1, player, EquipmentSlot.MAINHAND);
            }
        }
        return ActionResult.SUCCESS;
    }


}