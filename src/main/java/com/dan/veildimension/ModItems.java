package com.dan.veildimension;

import com.dan.veildimension.item.VeilIgniterItem;
import com.dan.veildimension.item.VeilReturnScrollItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems
{
    public static final Item VEIL_IGNITER = registerItem("veil_igniter", new VeilIgniterItem(new Item.Settings().maxDamage(64)));

    public static final Item VEIL_RETURN_SCROLL = registerItem("veil_return_scroll", new VeilReturnScrollItem(new Item.Settings().maxCount(16)));

    private static Item registerItem(String name, Item item)
    {
        return Registry.register(Registries.ITEM, Identifier.of("veildimension", name), item);
    }

    public static void initialize()
    {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.add(VEIL_IGNITER);
            content.add(VEIL_RETURN_SCROLL);
        });
    }
}