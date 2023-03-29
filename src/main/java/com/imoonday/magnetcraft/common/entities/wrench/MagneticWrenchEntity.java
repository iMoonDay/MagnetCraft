package com.imoonday.magnetcraft.common.entities.wrench;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import com.imoonday.magnetcraft.registries.common.EntityRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MagneticWrenchEntity extends PersistentProjectileEntity {

    private static final TrackedData<Boolean> ENCHANTED = DataTracker.registerData(MagneticWrenchEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> CRITICAL_HIT = DataTracker.registerData(MagneticWrenchEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final String WRENCH = "Wrench";
    public static final String DEALT_DAMAGE = "DealtDamage";
    private ItemStack wrenchStack = new ItemStack(ItemRegistries.MAGNETIC_WRENCH_ITEM);
    private float damageMultiplier = 1.0f;
    private boolean dealtDamage;
    public int returnTimer;
    public boolean shouldReturn;

    public MagneticWrenchEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public MagneticWrenchEntity(World world, LivingEntity owner, ItemStack stack, boolean criticalHit) {
        super(EntityRegistries.MAGNETIC_WRENCH, owner, world);
        this.wrenchStack = stack.copy();
        this.dataTracker.set(ENCHANTED, stack.hasGlint());
        this.dataTracker.set(CRITICAL_HIT, criticalHit);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ENCHANTED, false);
        this.dataTracker.startTracking(CRITICAL_HIT, false);
    }

    @Override
    public void tick() {
        checkAttracting();
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }
        Entity owner = this.getOwner();
        if (owner != null) {
            if (!this.shouldReturn) {
                this.shouldReturn = owner.isAttracting() || this.isAttracting();
            }
            if (shouldReturn && (this.dealtDamage || this.isNoClip())) {
                if (!this.isOwnerAlive()) {
                    if (!this.world.isClient && this.pickupType == PickupPermission.ALLOWED) {
                        this.dropStack(this.asItemStack(), 0.1f);
                    }
                    this.discard();
                } else {
                    this.setNoClip(true);
                    Vec3d vec3d = owner.getEyePos().subtract(this.getPos());
                    int speedMultiplier;
                    double dis = Math.max(this.getAttractDis(), owner.getAttractDis());
                    if (dis > 25) {
                        speedMultiplier = 3;
                    } else if (dis > 15) {
                        speedMultiplier = 2;
                    } else {
                        speedMultiplier = 1;
                    }
                    this.setPos(this.getX(), this.getY() + vec3d.y * 0.015 * speedMultiplier, this.getZ());
                    if (this.world.isClient) {
                        this.lastRenderY = this.getY();
                    }
                    double d = 0.05 * speedMultiplier;
                    this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));
                    if (this.returnTimer == 0) {
                        this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0f, 1.0f);
                    }
                    ++this.returnTimer;
                }
            }
        }
        super.tick();
    }

    private void checkAttracting() {
        ModConfig config = ModConfig.getConfig();
        int enchLvl = this.wrenchStack.getEnchantmentLvl(EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean isAttracting = enchLvl > 0;
        double enchDefaultDis = config.value.enchDefaultDis;
        double disPerLvl = config.value.disPerLvl;
        double enchMinDis = enchDefaultDis + disPerLvl;
        double dis = enchMinDis + (enchLvl - 1) * disPerLvl;
        if (isAttracting && this.canAttract()) {
            this.setAttracting(true, dis);
        }
    }

    private boolean isOwnerAlive() {
        Entity entity = this.getOwner();
        if (entity == null || !entity.isAlive()) {
            return false;
        }
        return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
    }

    @Override
    public ItemStack asItemStack() {
        return this.wrenchStack.copy();
    }

    public boolean isEnchanted() {
        return this.dataTracker.get(ENCHANTED);
    }

    public boolean isCriticalHit() {
        return this.dataTracker.get(CRITICAL_HIT);
    }

    @Override
    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        if (this.dealtDamage) {
            return null;
        }
        return super.getEntityCollision(currentPosition, nextPosition);
    }

    public MagneticWrenchEntity withDamageMultiplier(float multiplier) {
        this.damageMultiplier = multiplier;
        return this;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();
        float damage = 4.0f;
        if (hitEntity instanceof LivingEntity livingEntity) {
            damage += EnchantmentHelper.getAttackDamage(this.wrenchStack, livingEntity.getGroup());
            damage *= this.damageMultiplier;
            if (this.isCriticalHit()) {
                damage *= 1.5f;
            }
        }
        if (this.isCriticalHit()) {
            this.setGlowing(true);
        }
        Entity owner = this.getOwner();
        DamageSource damageSource = this.getDamageSources().thrown(this, owner == null ? this : owner);
        this.dealtDamage = true;
        SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_HIT;
        if (hitEntity.damage(damageSource, damage)) {
            if (hitEntity.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (hitEntity instanceof LivingEntity livingEntity) {
                if (owner instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingEntity, owner);
                    EnchantmentHelper.onTargetDamaged((LivingEntity) owner, livingEntity);
                }
                this.onHit(livingEntity);
            }
        }
        this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
        this.playSound(soundEvent, 1.0f, 1.0f);
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        return super.tryPickup(player) || this.isNoClip() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack());
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.isOwner(player) || this.getOwner() == null) {
            super.onPlayerCollision(player);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains(WRENCH, NbtElement.COMPOUND_TYPE)) {
            this.wrenchStack = ItemStack.fromNbt(nbt.getCompound(WRENCH));
        }
        this.dealtDamage = nbt.getBoolean(DEALT_DAMAGE);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put(WRENCH, this.wrenchStack.writeNbt(new NbtCompound()));
        nbt.putBoolean(DEALT_DAMAGE, this.dealtDamage);
    }

    @Override
    public void age() {
        if (this.pickupType != PersistentProjectileEntity.PickupPermission.ALLOWED) {
            super.age();
        }
    }

    @Override
    protected float getDragInWater() {
        return 0.99f;
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

}
