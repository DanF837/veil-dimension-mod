package com.dan.veildimension.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.IOException;

public class InventoryManager {

    /**
     * Get the save file for a player's Overworld inventory
     */
    private static File getOverworldSaveFile(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) return null;

        File worldDir = server.getSavePath(WorldSavePath.ROOT).toFile();
        File veilDir = new File(worldDir, "veil_data");
        if (!veilDir.exists()) {
            veilDir.mkdirs();
        }

        return new File(veilDir, player.getUuidAsString() + "_overworld.dat");
    }

    /**
     * Get the save file for a player's Veil inventory
     */
    private static File getVeilSaveFile(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) return null;

        File worldDir = server.getSavePath(WorldSavePath.ROOT).toFile();
        File veilDir = new File(worldDir, "veil_data");
        if (!veilDir.exists()) {
            veilDir.mkdirs();
        }

        return new File(veilDir, player.getUuidAsString() + "_veil.dat");
    }

    /**
     * Save inventory to NBT
     */
    private static NbtCompound saveInventoryToNbt(ServerPlayerEntity player) {
        NbtCompound data = new NbtCompound();

        // Save main inventory
        NbtList inventoryList = new NbtList();
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty()) {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putInt("Slot", i);
                itemTag.put("Item", stack.encode(player.getRegistryManager()));
                inventoryList.add(itemTag);
            }
        }
        data.put("Inventory", inventoryList);

        // Save armor
        NbtList armorList = new NbtList();
        for (int i = 0; i < player.getInventory().armor.size(); i++) {
            ItemStack stack = player.getInventory().armor.get(i);
            if (!stack.isEmpty()) {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putInt("Slot", i);
                itemTag.put("Item", stack.encode(player.getRegistryManager()));
                armorList.add(itemTag);
            }
        }
        data.put("Armor", armorList);

        // Save offhand
        NbtList offhandList = new NbtList();
        for (int i = 0; i < player.getInventory().offHand.size(); i++) {
            ItemStack stack = player.getInventory().offHand.get(i);
            if (!stack.isEmpty()) {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putInt("Slot", i);
                itemTag.put("Item", stack.encode(player.getRegistryManager()));
                offhandList.add(itemTag);
            }
        }
        data.put("Offhand", offhandList);

        // Save XP
        data.putInt("XP", player.totalExperience);
        data.putInt("Level", player.experienceLevel);

        return data;
    }

    /**
     * Load inventory from NBT
     */
    private static void loadInventoryFromNbt(ServerPlayerEntity player, NbtCompound data) {
        // Clear current inventory
        player.getInventory().clear();

        // Restore main inventory
        NbtList inventoryList = data.getList("Inventory", 10);
        for (int i = 0; i < inventoryList.size(); i++) {
            NbtCompound itemTag = inventoryList.getCompound(i);
            int slot = itemTag.getInt("Slot");
            ItemStack stack = ItemStack.fromNbt(player.getRegistryManager(), itemTag.getCompound("Item")).orElse(ItemStack.EMPTY);
            if (slot >= 0 && slot < player.getInventory().size()) {
                player.getInventory().setStack(slot, stack);
            }
        }

        // Restore armor
        NbtList armorList = data.getList("Armor", 10);
        for (int i = 0; i < armorList.size(); i++) {
            NbtCompound itemTag = armorList.getCompound(i);
            int slot = itemTag.getInt("Slot");
            ItemStack stack = ItemStack.fromNbt(player.getRegistryManager(), itemTag.getCompound("Item")).orElse(ItemStack.EMPTY);
            if (slot >= 0 && slot < player.getInventory().armor.size()) {
                player.getInventory().armor.set(slot, stack);
            }
        }

        // Restore offhand
        NbtList offhandList = data.getList("Offhand", 10);
        for (int i = 0; i < offhandList.size(); i++) {
            NbtCompound itemTag = offhandList.getCompound(i);
            int slot = itemTag.getInt("Slot");
            ItemStack stack = ItemStack.fromNbt(player.getRegistryManager(), itemTag.getCompound("Item")).orElse(ItemStack.EMPTY);
            if (slot >= 0 && slot < player.getInventory().offHand.size()) {
                player.getInventory().offHand.set(slot, stack);
            }
        }

        // Restore XP
        player.totalExperience = data.getInt("XP");
        player.experienceLevel = data.getInt("Level");
        player.experienceProgress = 0;
    }

    /**
     * Save Overworld inventory and load Veil inventory when entering Veil
     */
    public static void saveAndClearInventory(ServerPlayerEntity player) {
        // Save current Overworld inventory
        File overworldFile = getOverworldSaveFile(player);
        if (overworldFile != null) {
            NbtCompound overworldData = saveInventoryToNbt(player);
            try {
                NbtIo.writeCompressed(overworldData, overworldFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Clear inventory
        player.getInventory().clear();
        player.totalExperience = 0;
        player.experienceLevel = 0;
        player.experienceProgress = 0;

        // Load Veil inventory if it exists
        File veilFile = getVeilSaveFile(player);
        if (veilFile != null && veilFile.exists()) {
            try {
                NbtCompound veilData = NbtIo.readCompressed(veilFile.toPath(), NbtSizeTracker.ofUnlimitedBytes());
                loadInventoryFromNbt(player, veilData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Save Veil inventory and restore Overworld inventory when leaving Veil
     */
    public static void restoreInventory(ServerPlayerEntity player) {
        // Save current Veil inventory
        File veilFile = getVeilSaveFile(player);
        if (veilFile != null) {
            NbtCompound veilData = saveInventoryToNbt(player);
            try {
                NbtIo.writeCompressed(veilData, veilFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Clear inventory
        player.getInventory().clear();
        player.totalExperience = 0;
        player.experienceLevel = 0;
        player.experienceProgress = 0;

        // Restore Overworld inventory
        File overworldFile = getOverworldSaveFile(player);
        if (overworldFile != null && overworldFile.exists()) {
            try {
                NbtCompound overworldData = NbtIo.readCompressed(overworldFile.toPath(), NbtSizeTracker.ofUnlimitedBytes());
                loadInventoryFromNbt(player, overworldData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if player has saved Overworld inventory data
     */
    public static boolean hasSavedData(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            File saveFile = getOverworldSaveFile(serverPlayer);
            return saveFile != null && saveFile.exists();
        }
        return false;
    }

    public static boolean hasVeilInventory(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            File saveFile = getVeilSaveFile(serverPlayer);
            return saveFile != null && saveFile.exists();
        }
        return false;
    }
}