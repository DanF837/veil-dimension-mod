package com.dan.veildimension;

import com.dan.veildimension.item.VeilIgniterItem;
import com.dan.veildimension.item.VeilReturnScrollItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import com.dan.veildimension.item.ArchitectJournalItem;

public class ModItems
{
    public static final Item VEIL_IGNITER = registerItem("veil_igniter", new VeilIgniterItem(new Item.Settings().maxDamage(64)));

    public static final Item VEIL_RETURN_SCROLL = registerItem("veil_return_scroll", new VeilReturnScrollItem(new Item.Settings().maxCount(16)));

    // Journal Entries
    public static final Item JOURNAL_ENTRY_1 = registerItem("journal_entry_1",
            new ArchitectJournalItem(new Item.Settings().maxCount(1), 1,
                    "I've done it. After fifteen years of searching,",
                    "I've found the Lost City of the Veil Keepers.",
                    "Everything in the ancient texts is TRUE.",
                    "",
                    "They discovered a dimension between dimensions -",
                    "a space called 'the Veil' or 'the Between.'",
                    "They built portals. They entered it.",
                    "They learned its secrets.",
                    "",
                    "Then they vanished. Every record destroyed.",
                    "Every city abandoned. As if they were... erased.",
                    "",
                    "But this portal frame remains.",
                    "Their instructions are clear. I have the materials.",
                    "Tomorrow, I light it.",
                    "",
                    "If you find this and I haven't returned...",
                    "I'm sorry. And thank you for trying to find me.",
                    "",
                    "- The Architect"
            ));

    public static final Item JOURNAL_ENTRY_15 = registerItem("journal_entry_15",
            new ArchitectJournalItem(new Item.Settings().maxCount(1), 15,
                    "If you're reading this, you followed me through.",
                    "Welcome to the Veil.",
                    "",
                    "Your items are safe. They've returned to your",
                    "dimension automatically. Material things from",
                    "our world cannot exist in the Between.",
                    "",
                    "I've left you basic tools. They'll be enough",
                    "to survive your first night.",
                    "",
                    "Three rules:",
                    "1. The shadows hunt after dark. Keep light nearby.",
                    "2. There are others here. Survivors. Most are friendly.",
                    "3. Purple lanterns mark safe paths. Follow them.",
                    "",
                    "I've scattered my research throughout the Veil.",
                    "Find the Observatories in each region.",
                    "Understand what I learned.",
                    "",
                    "Then maybe you'll understand why I had to go north.",
                    "",
                    "If I'm not at my base camp... I've gone too far.",
                    "Someone needs to finish what I started.",
                    "",
                    "Good luck.",
                    "- The Architect"
            ));

    public static final Item JOURNAL_ENTRY_18 = registerItem("journal_entry_18",
            new ArchitectJournalItem(new Item.Settings().maxCount(1), 18,
                    "Day 3 in the Veil.",
                    "",
                    "I've been gathering Veil Essence from defeated Stalkers.",
                    "The material is incredible - it responds to intention.",
                    "",
                    "When I craft with it, the tools shape themselves",
                    "to my needs. It's as if the Veil WANTS to be useful.",
                    "",
                    "I've also noticed something strange:",
                    "The purple lanterns I've been placing...",
                    "Sometimes new ones appear that I didn't place.",
                    "",
                    "The Veil is watching. Helping? Or guiding me",
                    "toward something?",
                    "",
                    "I'm beginning to understand why the Veil Keepers",
                    "were so fascinated by this place.",
                    "",
                    "- The Architect"
            ));

    public static final Item JOURNAL_ENTRY_22 = registerItem("journal_entry_22",
            new ArchitectJournalItem(new Item.Settings().maxCount(1), 22,
                    "Week 1: I've encountered other people.",
                    "",
                    "They call themselves Survivors - people who",
                    "entered the Veil and chose to stay, or can't leave.",
                    "",
                    "Most are human, from different worlds and",
                    "different times. Time flows strangely here.",
                    "",
                    "One Survivor, Ezra, has been here for...",
                    "he won't say how long. His eyes glow faintly purple.",
                    "Mine are starting to do the same.",
                    "",
                    "He's taught me about the regions:",
                    "- Enchanted Forest: Dense, alive, watching",
                    "- Crystal Desert: Reality bends, shows visions",
                    "- Frozen Veil: Where time itself freezes",
                    "",
                    "Each has an Observatory. Ancient Veil Keeper ruins.",
                    "That's where I'll find the truth.",
                    "",
                    "- The Architect"
            ));

    public static final Item JOURNAL_ENTRY_47 = registerItem("journal_entry_47",
            new ArchitectJournalItem(new Item.Settings().maxCount(1), 47,
                    "Month 2: I've explored all three Observatories.",
                    "",
                    "The Forest Observatory revealed their first discoveries.",
                    "The Desert Observatory showed their ambitions.",
                    "The Frozen Observatory... that's where they failed.",
                    "",
                    "The Veil Keepers tried to merge dimensions.",
                    "They succeeded for seven seconds.",
                    "Then the Watchers erased them from existence.",
                    "",
                    "But I'm different. I've BECOME part of the Veil.",
                    "My body is changing. I exist in multiple states.",
                    "Maybe I can succeed where they failed.",
                    "",
                    "There's one more place. Beyond the frozen wastes.",
                    "The Rift. Where the Between is torn open.",
                    "Where the Watchers watch.",
                    "",
                    "Ezra begged me not to go. The other Survivors",
                    "think I've gone mad. Maybe I have.",
                    "",
                    "But I'm so close to understanding everything.",
                    "",
                    "- The Architect"
            ));

    private static Item registerItem(String name, Item item)
    {
        return Registry.register(Registries.ITEM, Identifier.of("veildimension", name), item);
    }

    public static void initialize()
    {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.add(VEIL_IGNITER);
            content.add(VEIL_RETURN_SCROLL);

            // Journal Entries
            content.add(JOURNAL_ENTRY_1);
            content.add(JOURNAL_ENTRY_15);
            content.add(JOURNAL_ENTRY_18);
            content.add(JOURNAL_ENTRY_22);
            content.add(JOURNAL_ENTRY_47);
        });
    }
}