package com.imoonday.magnetcraft.common.entities.bomb;

import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import com.imoonday.magnetcraft.registries.common.EntityRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class ElectromagneticPulseBombEntity extends ExplosiveProjectileEntity {

    public static final float POWER = 3.0f;
    public static final float MIN_DAMAGE = 10.0f;
    public static final float MAX_DAMAGE = 20.0f;
    public static final int DIS = 30;
    public static final int EXPLOSION_TICK = 60;
    private boolean damaged;
    private static final TrackedData<Boolean> EXPLOSIVE = DataTracker.registerData(ElectromagneticPulseBombEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FLASH = DataTracker.registerData(ElectromagneticPulseBombEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private ItemStack userStack;

    public ElectromagneticPulseBombEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public ElectromagneticPulseBombEntity(World world, LivingEntity user, float speed, boolean explosive, ItemStack userStack) {
        this(EntityRegistries.ELECTROMAGNETIC_PULSE_BOMB, world);
        this.refreshPositionAndAngles(user.getX(), user.getEyeY(), user.getZ(), user.getYaw(), user.getPitch());
        this.refreshPosition();
        this.setOwner(user);
        this.setNoGravity(true);
        this.noClip = !explosive;
        this.setVelocity(user, user.getPitch(), user.getYaw(), 0f, speed, 1.0f);
        this.setExplosive(explosive);
        this.userStack = userStack.copy();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(EXPLOSIVE, false);
        this.dataTracker.startTracking(FLASH, false);
    }

    public void setExplosive(boolean explosive) {
        this.dataTracker.set(EXPLOSIVE, explosive);
    }

    public void setFlash(boolean flash) {
        this.dataTracker.set(FLASH, flash);
    }

    public boolean isExplosive() {
        return this.dataTracker.get(EXPLOSIVE);
    }

    public boolean isFlash() {
        return this.dataTracker.get(FLASH);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("explosive", this.isExplosive());
        nbt.putBoolean("flash", this.isFlash());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("explosive")) {
            this.setExplosive(nbt.getBoolean("explosive"));
        }
        if (nbt.contains("flash")) {
            this.setFlash(nbt.getBoolean("flash"));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.damaged) {
            this.discard();
        }
        this.setFlash(this.age / 5 % 2 == 0);
        if (!this.isExplosive() && this.age >= EXPLOSION_TICK) {
            this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            this.world.getOtherEntities(null, this.getBoundingBox().expand(DIS), entity -> entity instanceof LivingEntity livingEntity && livingEntity.isInRange(this, DIS)).stream().map(entity -> (LivingEntity) entity).forEach(entity -> {
                float damage = MathHelper.clamp(entity.getHealth() / 2, MIN_DAMAGE, MAX_DAMAGE);
                if (entity.hasEnchantmentOnArmor(EnchantmentRegistries.ELECTROMAGNETIC_PROTECTION_ENCHANTMENT)) {
                    int lvl = entity.getEnchantmentLvlOnArmor(EnchantmentRegistries.ELECTROMAGNETIC_PROTECTION_ENCHANTMENT);
                    damage -= damage * lvl / 16;
                }
                if (!(entity instanceof PlayerEntity player) || !player.getAbilities().creativeMode && !player.isSpectator()) {
                    this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
                    if (this.isOnFire()) {
                        entity.setOnFireFor(3);
                    }
                    entity.damage(getDamageSources().explosion(this, this.getOwner()), Math.max(damage, 0));
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 5 * 20, 4, false, false, false), this.getOwner());
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 7 * 20, 4, false, false, false), this.getOwner());
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 3 * 20, 4, false, false, false), this.getOwner());
                }
            });
            this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 10.0f, 1.0f);
            this.damaged = true;
        }
        if (this.isExplosive() && this.age >= 10 * 20) {
            explode();
        }
    }

    protected void explode() {
        if (!this.world.isClient) {
            boolean bl = this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
            this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), POWER, bl, World.ExplosionSourceType.MOB);
            this.discard();
        }
    }

    public ItemStack getUserStack() {
        return this.userStack;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (!this.isExplosive()) {
            return;
        }
        super.onCollision(hitResult);
        explode();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (!this.isExplosive()) {
            return;
        }
        super.onEntityHit(entityHitResult);
        if (this.world.isClient) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        Entity owner = this.getOwner();
        entity.damage(this.getDamageSources().explosion(this, owner), MIN_DAMAGE);
        if (owner instanceof LivingEntity livingEntity) {
            this.applyDamageEffects(livingEntity, entity);
        }
    }

    @Override
    public boolean canHit() {
        return false;
    }

    @Override
    protected boolean isBurning() {
        return false;
    }

    @Override
    public boolean isTouchingWater() {
        return this.isExplosive() && super.isTouchingWater();
    }

    @Override
    public boolean isPushedByFluids() {
        return this.isExplosive();
    }

    @Override
    protected float getDrag() {
        return this.isExplosive() ? 1.0f : super.getDrag();
    }

    @Override
    public boolean canAvoidTraps() {
        return !this.isExplosive();
    }

}
