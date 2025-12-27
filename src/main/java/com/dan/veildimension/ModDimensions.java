package com.dan.veildimension;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ModDimensions
{

    public static final RegistryKey<World> VEIL_WORLD = RegistryKey.of(RegistryKeys.WORLD, Identifier.of("veildimension", "veil")
    );

    public static final RegistryKey<DimensionType> VEIL_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, Identifier.of("veildimension", "veil")
    );

    public static void initialize()
    {
        // Dimension is registered via data files
    }
}