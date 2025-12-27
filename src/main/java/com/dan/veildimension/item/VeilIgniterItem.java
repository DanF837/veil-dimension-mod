package com.dan.veildimension.item;

import com.dan.veildimension.ModBlocks;
import com.dan.veildimension.block.VeilPortalBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class VeilIgniterItem extends Item {

    public VeilIgniterItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(context.getBlockPos());

        // Check if clicking on a portal frame
        if (blockState.isOf(ModBlocks.VEIL_PORTAL_FRAME)) {
            if (!world.isClient) {
                if (VeilPortalBlock.tryCreatePortal(world, context.getBlockPos())) {
                    // Damage the item
                    ItemStack itemStack = context.getStack();
                    PlayerEntity player = context.getPlayer();
                    if (player != null && !player.getAbilities().creativeMode) {
                        itemStack.damage(1, player, EquipmentSlot.MAINHAND);
                    }
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }
}