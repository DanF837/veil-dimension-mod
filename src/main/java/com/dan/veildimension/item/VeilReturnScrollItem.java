package com.dan.veildimension.item;

import com.dan.veildimension.ModDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class VeilReturnScrollItem extends Item
{
    public VeilReturnScrollItem(Settings settings)
    {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getStackInHand(hand);

        // Only works in the Veil Dimension
        if (world.getRegistryKey() == ModDimensions.VEIL_WORLD)
        {
            if (!world.isClient)
            {
                ServerWorld serverWorld = (ServerWorld) world;
                ServerWorld overworld = serverWorld.getServer().getWorld(World.OVERWORLD);

                if (overworld != null)
                {
                    // Play sound and particles
                    world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

                    // Teleport to Overworld spawn
                    player.teleportTo(new TeleportTarget(overworld, player, TeleportTarget.NO_OP));

                    // Consume the item (remove one scroll)
                    if (!player.getAbilities().creativeMode)
                    {
                        stack.decrement(1);
                    }

                    player.sendMessage(Text.literal("§5§oYou return through the veil...§r"), true);
                }
            }
            return TypedActionResult.success(stack, world.isClient);
        }
        else
        {
            // Not in Veil Dimension
            player.sendMessage(Text.literal("§c§oThis can only be used within the Veil...§r"), true);
            return TypedActionResult.fail(stack);
        }
    }
}