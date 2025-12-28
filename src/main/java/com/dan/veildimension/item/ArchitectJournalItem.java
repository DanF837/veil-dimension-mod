package com.dan.veildimension.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class ArchitectJournalItem extends Item {

    private final int entryNumber;
    private final String[] journalText;

    public ArchitectJournalItem(Settings settings, int entryNumber, String... journalText) {
        super(settings);
        this.entryNumber = entryNumber;
        this.journalText = journalText;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient) {
            // Display journal entry to player
            player.sendMessage(Text.literal("").formatted(Formatting.DARK_PURPLE)
                    .append(Text.literal("=".repeat(50)).formatted(Formatting.DARK_GRAY)), false);

            player.sendMessage(Text.literal("Journal Entry #" + entryNumber + " - The Architect")
                    .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);

            player.sendMessage(Text.literal(""), false);

            // Send each line of the journal
            for (String line : journalText) {
                player.sendMessage(Text.literal(line).formatted(Formatting.GRAY), false);
            }

            player.sendMessage(Text.literal(""), false);
            player.sendMessage(Text.literal("=".repeat(50)).formatted(Formatting.DARK_GRAY), false);
        }

        return TypedActionResult.success(player.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("Entry #" + entryNumber).formatted(Formatting.DARK_PURPLE));
        tooltip.add(Text.literal("Right-click to read").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}