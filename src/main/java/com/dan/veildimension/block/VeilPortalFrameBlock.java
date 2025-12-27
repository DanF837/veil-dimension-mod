package com.dan.veildimension.block;

import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.sound.BlockSoundGroup;

public class VeilPortalFrameBlock extends Block
{
    public VeilPortalFrameBlock()
    {
        super(Settings.create().mapColor(MapColor.PURPLE).strength(50.0F, 1200.0F).requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK).luminance(state -> 7));
    }
}