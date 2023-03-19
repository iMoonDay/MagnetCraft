package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.api.MagnetCraftEntity;
import com.imoonday.magnetcraft.common.items.magnets.CreatureMagnetItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.AttractMethods;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Entity.class)
public class EntityMixin implements MagnetCraftEntity {

    protected NbtCompound attractData = new NbtCompound();
    protected double attractDis = 0;
    protected boolean isAttracting = false;
    protected boolean enable = true;
    protected UUID attractOwner = CreatureMagnetItem.EMPTY_UUID;
    protected boolean isFollowing = false;
    protected boolean ignoreFallDamage = false;
    protected boolean magneticLevitationMode = true;
    protected int levitationTick = 0;
    protected boolean automaticLevitation = false;

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        this.attractDis = this.getAttractDis();
        this.isAttracting = this.isAttracting() && this.canAttract();
        this.enable = this.getEnable();
        this.attractOwner = this.getAttractOwner();
        this.isFollowing = this.isFollowing();
        this.ignoreFallDamage = this.ignoreFallDamage();
        this.magneticLevitationMode = this.getMagneticLevitationMode();
        this.automaticLevitation = this.getAutomaticLevitation();
        if (entity.isAttracting() && entity.getEnable() && entity.isAlive()) {
            AttractMethods.tryAttract(entity, entity.getAttractDis());
        }
    }

    @Override
    public NbtCompound getAttractData() {
        if (attractData == null) {
            attractData = new NbtCompound();
        }
        return this.attractData;
    }

    @Override
    public boolean clearAttractData() {
        this.attractData = new NbtCompound();
        return this.attractData.equals(new NbtCompound());
    }

    @Override
    public double getAttractDis() {
        if (!this.attractData.contains("AttractDis")) {
            this.attractData.putDouble("AttractDis", 0);
        }
        return this.attractData.getDouble("AttractDis");
    }

    @Override
    public void setAttractDis(double dis) {
        this.attractData.putDouble("AttractDis", dis);
    }

    @Override
    public boolean isAttracting() {
        if (!this.attractData.contains("isAttracting")) {
            this.attractData.putBoolean("isAttracting", false);
        }
        return this.attractData.getBoolean("isAttracting");
    }

    @Override
    public void setAttracting(boolean attracting) {
        this.attractData.putBoolean("isAttracting", attracting);
        if (!attracting) {
            this.attractData.putDouble("AttractDis", 0);
        }
    }

    @Override
    public void setAttracting(boolean attracting, double dis) {
        setAttracting(attracting);
        setAttractDis(dis);
    }

    @Override
    public boolean getEnable() {
        if (!this.attractData.contains("Enable")) {
            this.attractData.putBoolean("Enable", true);
        }
        return this.attractData.getBoolean("Enable");
    }

    @Override
    public void setEnable(boolean enable) {
        this.attractData.putBoolean("Enable", enable);
    }

    @Override
    public UUID getAttractOwner() {
        if (!this.attractData.contains("AttractOwner")) {
            this.attractData.putUuid("AttractOwner", CreatureMagnetItem.EMPTY_UUID);
        }
        return this.attractData.getUuid("AttractOwner");
    }

    @Override
    public void setAttractOwner(UUID uuid) {
        this.attractData.putUuid("AttractOwner", uuid);
    }

    @Override
    public boolean canAttract() {
        Entity entity = (Entity) (Object) this;
        if (!entity.getEnable()) {
            return false;
        }
        int degaussingDis = ModConfig.getValue().degaussingDis;
        if (!entity.world.getEntitiesByClass(LivingEntity.class, entity.getBoundingBox().expand(degaussingDis), otherEntity -> otherEntity.hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && entity.getPos().isInRange(otherEntity.getPos(), degaussingDis) && !otherEntity.isSpectator()).isEmpty()) {
            return false;
        }
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.hasStatusEffect(EffectRegistries.UNATTRACT_EFFECT)) {
                return false;
            }
            if (!(livingEntity instanceof PlayerEntity)) {
                return livingEntity.world.getEntitiesByClass(PlayerEntity.class, entity.getBoundingBox().expand(degaussingDis), player -> player.getInventory().containsAny(stack -> stack.isOf(ItemRegistries.PORTABLE_DEMAGNETIZER_ITEM) && stack.getNbt() != null && stack.getNbt().getBoolean("Enable"))).isEmpty();
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean isFollowing() {
        if (!this.attractData.contains("isFollowing")) {
            this.attractData.putBoolean("isFollowing", false);
        }
        return this.attractData.getBoolean("isFollowing");
    }

    @Override
    public void setFollowing(boolean following) {
        this.attractData.putBoolean("isFollowing", following);
    }

    @Override
    public boolean ignoreFallDamage() {
        if (!this.attractData.contains("IgnoreFallDamage")) {
            this.attractData.putBoolean("IgnoreFallDamage", false);
        }
        return this.attractData.getBoolean("IgnoreFallDamage");
    }

    @Override
    public void setIgnoreFallDamage(boolean ignoreFallDamage) {
        this.attractData.putBoolean("IgnoreFallDamage", ignoreFallDamage);
    }

    @Override
    public boolean getMagneticLevitationMode() {
        if (!this.attractData.contains("MagneticLevitationMode")) {
            this.attractData.putBoolean("MagneticLevitationMode", true);
        }
        return this.attractData.getBoolean("MagneticLevitationMode");
    }

    @Override
    public void setMagneticLevitationMode(boolean mode) {
        this.attractData.putBoolean("MagneticLevitationMode", mode);
    }

    @Override
    public int getLevitationTick() {
        if (!this.attractData.contains("LevitationTick") && this.attractData.getInt("LevitationTick") < 0) {
            this.attractData.putInt("LevitationTick", 0);
        }
        return this.attractData.getInt("LevitationTick");
    }

    @Override
    public void setLevitationTick(int tick) {
        this.attractData.putInt("LevitationTick", Math.max(tick, 0));
    }

    @Override
    public boolean getAutomaticLevitation() {
        if (!this.attractData.contains("AutomaticLevitation")) {
            this.attractData.putBoolean("AutomaticLevitation", false);
        }
        return this.attractData.getBoolean("AutomaticLevitation");
    }

    @Override
    public void setAutomaticLevitation(boolean enable) {
        this.attractData.putBoolean("AutomaticLevitation", enable);
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    public void writePocketsDataToNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        this.attractData.putDouble("AttractDis", this.getAttractDis());
        this.attractData.putBoolean("isAttracting", this.isAttracting() && this.canAttract());
        this.attractData.putBoolean("Enable", this.getEnable());
        this.attractData.putUuid("AttractOwner", this.getAttractOwner());
        this.attractData.putBoolean("isFollowing", this.isFollowing());
        this.attractData.putBoolean("IgnoreFallDamage", this.ignoreFallDamage());
        this.attractData.putBoolean("MagneticLevitationMode", this.getMagneticLevitationMode());
        this.attractData.putInt("LevitationTick", this.getLevitationTick());
        this.attractData.putBoolean("AutomaticLevitation", this.getAutomaticLevitation());
        nbt.put("AttractData", this.attractData);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    public void readPocketsDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("AttractData")) {
            NbtCompound data = nbt.getCompound("AttractData");
            this.attractData = data;
            if (data.contains("AttractDis")) {
                this.attractDis = data.getDouble("AttractDis");
            }
            if (data.contains("isAttracting")) {
                this.isAttracting = data.getBoolean("isAttracting");
            }
            if (data.contains("Enable")) {
                this.enable = data.getBoolean("Enable");
            }
            if (data.contains("AttractOwner")) {
                this.attractOwner = data.getUuid("AttractOwner");
            }
            if (data.contains("isFollowing")) {
                this.isFollowing = data.getBoolean("isFollowing");
            }
            if (data.contains("IgnoreFallDamage")) {
                this.ignoreFallDamage = data.getBoolean("IgnoreFallDamage");
            }
            if (data.contains("MagneticLevitationMode")) {
                this.magneticLevitationMode = data.getBoolean("MagneticLevitationMode");
            }
            if (data.contains("LevitationTick")) {
                this.levitationTick = data.getInt("LevitationTick");
            }
            if (data.contains("AutomaticLevitation")) {
                this.automaticLevitation = data.getBoolean("AutomaticLevitation");
            }
        }
    }

}
