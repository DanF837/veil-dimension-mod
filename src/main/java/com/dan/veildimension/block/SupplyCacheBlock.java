package com.dan.veildimension.block;

import com.dan.veildimension.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SupplyCacheBlock extends Block {

    public static final BooleanProperty OPENED = BooleanProperty.of("opened");

    public SupplyCacheBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(OPENED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPENED);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && !state.get(OPENED)) {
            // Mark as opened
            world.setBlockState(pos, state.with(OPENED, true));

            // Play opening sound
            world.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 1.0F, 1.0F);

            // Give player supplies
            player.giveItemStack(new ItemStack(ModItems.JOURNAL_ENTRY_15));
            player.giveItemStack(new ItemStack(Items.COOKED_BEEF, 64));
            player.giveItemStack(new ItemStack(Items.TORCH, 64));

            // Send message
            player.sendMessage(Text.literal("You found the Architect's supply cache!").formatted(Formatting.LIGHT_PURPLE), false);
            player.sendMessage(Text.literal("Basic supplies have been added to your inventory.").formatted(Formatting.GRAY), false);

            // Remove the cache block after a moment
            world.scheduleBlockTick(pos, this, 20);

            return ActionResult.SUCCESS;
        }

        if (!world.isClient && state.get(OPENED)) {
            player.sendMessage(Text.literal("This cache has already been opened.").formatted(Formatting.GRAY), false);
        }

        return ActionResult.success(world.isClient);
    }
}