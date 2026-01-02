package com.dan.veildimension.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class ShadeStalkerEntity extends net.minecraft.entity.mob.ZombieEntity {

    public ShadeStalkerEntity(EntityType<? extends net.minecraft.entity.mob.ZombieEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 10;
    }

    /**
     * Set up AI goals
     */
    @Override
    protected void initGoals() {
        // Attack goals
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(5, new LookAroundGoal(this));

        // Target goals
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    /**
     * Set entity attributes (health, speed, damage, etc.)
     */
    public static DefaultAttributeContainer.Builder createShadeStalkerAttributes() {
        return net.minecraft.entity.mob.ZombieEntity.createZombieAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0) // 10 hearts
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25) // Slightly slower than player
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0) // 2 hearts damage
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0) // Can detect players from far
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2); // Slight resistance
    }

    /**
     * Ambient sound (breathing, idle sounds)
     */
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDERMAN_AMBIENT; // Eerie sound
    }

    /**
     * Hurt sound
     */
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ENDERMAN_HURT;
    }

    /**
     * Death sound
     */
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ENDERMAN_DEATH;
    }

    /**
     * Only spawn in darkness
     */
    @Override
    public boolean canSpawn(net.minecraft.world.WorldView world) {
        return super.canSpawn(world) && this.getWorld().isNight();
    }

    /**
     * Shade Stalkers are always adults - no baby variants
     */
    @Override
    public boolean isBaby() {
        return false; // Never spawn as baby
    }

    @Override
    public void setBaby(boolean baby) {
        // Prevent baby state from being set
    }

    @Override
    protected void dropLoot(DamageSource damageSource, boolean causedByPlayer) {
        super.dropLoot(damageSource, causedByPlayer);

        // Drop 1-3 Veil Essence
        int amount = this.random.nextInt(3) + 1;
        this.dropStack(new net.minecraft.item.ItemStack(com.dan.veildimension.ModItems.VEIL_ESSENCE, amount));
    }
}