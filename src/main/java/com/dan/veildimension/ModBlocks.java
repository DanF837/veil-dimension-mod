package com.dan.veildimension;

import com.dan.veildimension.block.VeilPortalFrameBlock;
import com.dan.veildimension.block.VeilPortalBlock;
import com.dan.veildimension.block.SupplyCacheBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks
{
    public static final Block VEIL_PORTAL_FRAME = registerBlock("veil_portal_frame", new VeilPortalFrameBlock());

    public static final Block VEIL_PORTAL = Registry.register(Registries.BLOCK, Identifier.of("veildimension", "veil_portal"), new VeilPortalBlock());

    public static final Block SUPPLY_CACHE = registerBlock("supply_cache", new SupplyCacheBlock(AbstractBlock.Settings.create().strength(1.5F).luminance(state -> 7)));

    private static Block registerBlock(String name, Block block)
    {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of("veildimension", name), block);
    }

    private static void registerBlockItem(String name, Block block)
    {
        Registry.register(Registries.ITEM, Identifier.of("veildimension", name), new BlockItem(block, new Item.Settings()));
    }

    public static void initialize()
    {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> {
            content.add(VEIL_PORTAL_FRAME);
            content.add(SUPPLY_CACHE);
        });
    }
}