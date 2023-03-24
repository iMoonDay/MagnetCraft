package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.api.MagnetCraftEntity;
import com.imoonday.magnetcraft.common.items.magnets.CreatureMagnetItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * @author iMoonDay
 */
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
    protected boolean isAdsorbedByEntity = false;
    protected boolean isAdsorbedByBlock = false;
    protected UUID adsorptionEntityId = CreatureMagnetItem.EMPTY_UUID;
    protected BlockPos adsorptionBlockPos = new BlockPos(0, 0, 0);

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
        this.isAdsorbedByEntity = this.isAdsorbedByEntity();
        this.isAdsorbedByBlock = this.isAdsorbedByBlock();
        this.adsorptionEntityId = this.getAdsorptionEntityId();
        this.adsorptionBlockPos = this.getAdsorptionBlockPos();
        if (entity.isAttracting() && entity.getEnable() && entity.isAlive()) {
            MagnetCraft.AttractMethods.tryAttract(entity, entity.getAttractDis());
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

    @Override
    public boolean isAdsorbedByEntity() {
        if (!this.attractData.contains("isAdsorbedByEntity")) {
            this.attractData.putBoolean("isAdsorbedByEntity", false);
        }
        return this.attractData.getBoolean("isAdsorbedByEntity") && !this.isAdsorbedByBlock();
    }

    @Override
    public void setAdsorbedByEntity(boolean adsorbed) {
        this.attractData.putBoolean("isAdsorbedByEntity", adsorbed);
        if (adsorbed) {
            this.setAdsorbedByBlock(false);
        } else {
            this.setAdsorptionEntityId(CreatureMagnetItem.EMPTY_UUID, true);
        }
    }

    @Override
    public boolean isAdsorbedByBlock() {
        if (!this.attractData.contains("isAdsorbedByBlock")) {
            this.attractData.putBoolean("isAdsorbedByBlock", false);
        }
        return this.attractData.getBoolean("isAdsorbedByBlock") && !this.isAdsorbedByEntity();
    }

    @Override
    public void setAdsorbedByBlock(boolean adsorbed) {
        this.attractData.putBoolean("isAdsorbedByBlock", adsorbed);
        if (adsorbed) {
            this.setAdsorbedByEntity(false);
        } else {
            this.setAdsorptionBlockPos(new BlockPos(0, 0, 0), true);
        }
    }

    @Override
    public UUID getAdsorptionEntityId() {
        if (!this.attractData.contains("AdsorptionEntityId")) {
            this.attractData.putUuid("AdsorptionEntityId", CreatureMagnetItem.EMPTY_UUID);
        }
        return this.attractData.getUuid("AdsorptionEntityId");
    }

    @Override
    public void setAdsorptionEntityId(UUID uuid, boolean clear) {
        if (!clear) {
            this.setAdsorbedByEntity(true);
            this.setFollowing(false);
        }
        this.attractData.putUuid("AdsorptionEntityId", uuid);
    }

    @Override
    public BlockPos getAdsorptionBlockPos() {
        if (!this.attractData.contains("AdsorptionBlockPos") || this.attractData.getIntArray("AdsorptionBlockPos").length != 3) {
            this.attractData.putIntArray("AdsorptionBlockPos", new int[]{0, 0, 0});
        }
        int[] pos = this.attractData.getIntArray("AdsorptionBlockPos");
        return new BlockPos(pos[0], pos[1], pos[2]);
    }

    @Override
    public void setAdsorptionBlockPos(BlockPos pos, boolean clear) {
        if (!clear) {
            this.setAdsorbedByBlock(true);
            this.setFollowing(false);
        }
        this.attractData.putIntArray("AdsorptionBlockPos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
    }

    @Inject(method = "writeNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", shift = At.Shift.AFTER))
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
        this.attractData.putBoolean("isAdsorbedByEntity", this.isAdsorbedByEntity());
        this.attractData.putBoolean("isAdsorbedByBlock", this.isAdsorbedByBlock());
        this.attractData.putUuid("AdsorptionEntityId", this.getAdsorptionEntityId());
        this.attractData.putIntArray("AdsorptionBlockPos", new int[]{this.getAdsorptionBlockPos().getX(), this.getAdsorptionBlockPos().getY(), this.getAdsorptionBlockPos().getZ()});
        nbt.put("AttractData", this.attractData);
    }

    @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V", shift = At.Shift.AFTER))
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
            if (data.contains("isAdsorbedByEntity")) {
                this.isAdsorbedByEntity = data.getBoolean("isAdsorbedByEntity");
            }
            if (data.contains("isAdsorbedByBlock")) {
                this.isAdsorbedByBlock = data.getBoolean("isAdsorbedByBlock");
            }
            if (data.contains("AdsorptionEntityId")) {
                this.adsorptionEntityId = data.getUuid("AdsorptionEntityId");
            }
            if (data.contains("AdsorptionBlockPos") && data.getIntArray("AdsorptionBlockPos").length == 3) {
                this.adsorptionBlockPos = new BlockPos(data.getIntArray("AdsorptionBlockPos")[0], data.getIntArray("AdsorptionBlockPos")[1], data.getIntArray("AdsorptionBlockPos")[2]);
            }
        }
    }

}
