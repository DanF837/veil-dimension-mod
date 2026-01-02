package com.dan.veildimension.item;

import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.List;

public class VeilEssenceItem extends Item {

    public VeilEssenceItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(net.minecraft.item.ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("A strange essence from the Veil").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("It pulses with otherworldly energy").formatted(Formatting.DARK_PURPLE));
    }

}