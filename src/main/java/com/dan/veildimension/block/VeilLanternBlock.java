package com.dan.veildimension.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LanternBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class VeilLanternBlock extends LanternBlock {

    public VeilLanternBlock() {
        super(Settings.copy(Blocks.SOUL_LANTERN)
                .sounds(BlockSoundGroup.LANTERN)
                .luminance(state -> 15));
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        // Purple particles floating upward
        if (random.nextInt(3) == 0) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.3;

            world.addParticle(ParticleTypes.PORTAL, x, y, z, 0, 0.05, 0);
        }

        // Occasional soul flame
        if (random.nextInt(10) == 0) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;

            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, 0.03, 0);
        }
    }
}