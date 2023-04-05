package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.api.MagnetCraftEntity;
import com.imoonday.magnetcraft.common.blocks.MagneticFilterLayerBlock;
import com.imoonday.magnetcraft.common.items.magnets.CreatureMagnetItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin implements MagnetCraftEntity {

    private static final String WHITELIST = "Whitelist";
    private static final String TAG = "tag";
    private static final String COMPARE_NBT = "CompareNbt";
    private static final String DAMAGE = "Damage";
    private static final String COMPARE_DAMAGE = "CompareDamage";
    private static final String FILTER = "Filter";
    private static final String FILTERABLE = "Filterable";
    private static final String ATTRACT_DIS = "AttractDis";
    private static final String ATTRACTING = "isAttracting";
    private static final String ENABLE = "Enable";
    private static final String ATTRACT_OWNER = "AttractOwner";
    private static final String FOLLOWING = "isFollowing";
    private static final String IGNORE_FALL_DAMAGE = "IgnoreFallDamage";
    private static final String MAGNETIC_LEVITATION_MODE = "MagneticLevitationMode";
    private static final String LEVITATION_TICK = "LevitationTick";
    private static final String AUTOMATIC_LEVITATION = "AutomaticLevitation";
    private static final String ADSORBED_BY_ENTITY = "isAdsorbedByEntity";
    private static final String ADSORBED_BY_BLOCK = "isAdsorbedByBlock";
    private static final String ADSORPTION_ENTITY_ID = "AdsorptionEntityId";
    private static final String ADSORPTION_BLOCK_POS = "AdsorptionBlockPos";
    private static final String ATTRACT_DATA = "AttractData";
    private static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    private static final String ITEMS = "Items";
    private static final String ID = "id";
    private static final String SLOT = "Slot";

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

    @Override
    public void tryAttract() {
        Entity entity = (Entity) (Object) this;
        double dis = entity.getAttractDis();
        if (entity.world.isClient) {
            return;
        }
        if (!entity.isAttracting() || !entity.getEnable() || !entity.isAlive()) {
            return;
        }
        int degaussingDis = ModConfig.getValue().degaussingDis;
        boolean whitelistEnable = ModConfig.getConfig().whitelist.enable;
        boolean blacklistEnable = ModConfig.getConfig().blacklist.enable;
        ArrayList<String> whitelist = ModConfig.getConfig().whitelist.list;
        ArrayList<String> blacklist = ModConfig.getConfig().blacklist.list;
        entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(dis), targetEntity -> ((targetEntity instanceof ItemEntity || targetEntity instanceof ExperienceOrbEntity) && targetEntity.getPos().isInRange(entity.getPos(), dis) && !targetEntity.getPos().isInRange(entity.getPos(), 0.5))).forEach(targetEntity -> {
            boolean pass = true;
            if (targetEntity instanceof ItemEntity itemEntity) {
                String item = Registries.ITEM.getId(itemEntity.getStack().getItem()).toString();
                boolean mainhandStackListPass = true;
                boolean offhandStackListPass = true;
                boolean controllerListPass = true;
                if (entity instanceof LivingEntity livingEntity) {
                    if (livingEntity.getMainHandStack().getNbt() != null && livingEntity.getMainHandStack().getNbt().contains(FILTERABLE)) {
                        mainhandStackListPass = isSameStack(livingEntity.getMainHandStack(), itemEntity);
                    }
                    if (livingEntity.getOffHandStack().getNbt() != null && livingEntity.getOffHandStack().getNbt().contains(FILTERABLE)) {
                        offhandStackListPass = isSameStack(livingEntity.getOffHandStack(), itemEntity);
                    }
                    if (entity instanceof PlayerEntity player && player.getInventory().containsAny(stack -> ((stack.isOf(ItemRegistries.MAGNET_CONTROLLER_ITEM) && stack.getNbt() != null && stack.getNbt().contains(FILTERABLE) && !isSameStack(stack, itemEntity)) || ((Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock) && stack.getNbt() != null && stack.getNbt().getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE).stream().map(nbtElement -> (NbtCompound) nbtElement).filter(nbtCompound -> nbtCompound.getString(ID).equals(Registries.ITEM.getId(ItemRegistries.MAGNET_CONTROLLER_ITEM).toString())).peek(nbtCompound -> nbtCompound.remove(SLOT)).map(ItemStack::fromNbt).anyMatch(stack1 -> stack1.getNbt() != null && stack1.getNbt().contains(FILTERABLE) && !isSameStack(stack1, itemEntity)))))) {
                        controllerListPass = false;
                    }
                }
                boolean StackListPass = mainhandStackListPass && offhandStackListPass && controllerListPass;
                boolean whitelistPass = whitelist.contains(item);
                boolean blacklistPass = !blacklist.contains(item);
                boolean hasDegaussingPlayer = !targetEntity.world.getOtherEntities(targetEntity, targetEntity.getBoundingBox().expand(degaussingDis), otherEntity -> (otherEntity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && targetEntity.getPos().isInRange(otherEntity.getPos(), degaussingDis))).isEmpty();
                pass = (!whitelistEnable || whitelistPass) && (!blacklistEnable || blacklistPass) && StackListPass && !hasDegaussingPlayer;
            }
            if (pass && targetEntity.canBeAttractedTo(entity.getEyePos())) {
                boolean hasNearerPlayer;
                boolean hasNearerEntity = false;
                if (entity instanceof PlayerEntity) {
                    hasNearerPlayer = targetEntity.world.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), dis, entity1 -> entity1.isAttracting() && targetEntity.canBeAttractedTo(entity1.getEyePos())) != entity;
                } else {
                    hasNearerPlayer = targetEntity.world.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), dis, entity1 -> entity1.isAttracting() && targetEntity.canBeAttractedTo(entity1.getEyePos())) != null;
                    hasNearerEntity = !targetEntity.world.getOtherEntities(targetEntity, entity.getBoundingBox().expand(dis), otherEntity -> (!(otherEntity instanceof PlayerEntity) && otherEntity.distanceTo(targetEntity) < entity.distanceTo(targetEntity) && otherEntity.isAttracting() && otherEntity.getEnable() && otherEntity.isAlive() && targetEntity.canBeAttractedTo(otherEntity.getEyePos()))).isEmpty();
                }
                if (!hasNearerPlayer && !hasNearerEntity) {
                    Vec3d vec = entity.getEyePos().subtract(targetEntity.getPos()).multiply(0.05);
                    targetEntity.setVelocity(targetEntity.horizontalCollision ? vec.multiply(1, 0, 1).add(0, 0.25, 0) : vec);
                    PlayerLookup.tracking(targetEntity).forEach(serverPlayer -> serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(targetEntity)));
                }
            }
        });
    }

    private static boolean isSameStack(ItemStack stack, ItemEntity entity) {
        if (stack == null || stack.getNbt() == null || !stack.getNbt().getBoolean(FILTERABLE)) {
            return true;
        }
        boolean stackDamagePass = true;
        boolean stackNbtPass = true;
        NbtList list = stack.getNbt().getList(FILTER, NbtElement.COMPOUND_TYPE);
        String item = Registries.ITEM.getId(entity.getStack().getItem()).toString();
        boolean inList = list.stream().anyMatch(nbtElement -> nbtElement instanceof NbtCompound nbtCompound && nbtCompound.getString(ID).equals(item));
        if (stack.getNbt().getBoolean(COMPARE_DAMAGE) && inList) {
            stackDamagePass = list.stream().filter(nbtElement -> nbtElement instanceof NbtCompound nbtCompound && nbtCompound.getString(ID).equals(item)).map(nbtElement -> (NbtCompound) nbtElement).anyMatch(NbtCompound -> NbtCompound.getInt(DAMAGE) == entity.getStack().getDamage());
        }
        if (stack.getNbt().getBoolean(COMPARE_NBT) && inList) {
            NbtCompound nbt = entity.getStack().getNbt();
            NbtCompound nbtWithoutDamage = new NbtCompound();
            if (nbt != null) {
                nbtWithoutDamage = nbt.copy();
                nbtWithoutDamage.remove(DAMAGE);
            }
            NbtCompound finalNbt = nbtWithoutDamage;
            stackNbtPass = list.stream().filter(nbtElement -> nbtElement instanceof NbtCompound nbtCompound && nbtCompound.getString(ID).equals(item)).map(nbtElement -> (NbtCompound) nbtElement).peek(NbtCompound -> NbtCompound.getCompound(TAG).remove(DAMAGE)).anyMatch(NbtCompound -> NbtCompound.getCompound(TAG).equals(finalNbt));
        }
        boolean isWhitelist = stack.getNbt().getBoolean(WHITELIST);
        return (!isWhitelist || inList && stackDamagePass && stackNbtPass) && (isWhitelist || !inList || !stackDamagePass || !stackNbtPass);
    }

    @Override
    public boolean canBeAttractedTo(Vec3d pos) {
        Entity entity = (Entity) (Object) this;
        return MagneticFilterLayerBlock.canBeAttractedTo(entity, pos);
    }

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
        entity.tryAttract();
        CreatureMagnetItem.followingCheck(entity);
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
        if (!this.attractData.contains(ATTRACT_DIS)) {
            this.attractData.putDouble(ATTRACT_DIS, 0);
        }
        return this.attractData.getDouble(ATTRACT_DIS);
    }

    @Override
    public void setAttractDis(double dis) {
        this.attractData.putDouble(ATTRACT_DIS, dis);
    }

    @Override
    public boolean isAttracting() {
        if (!this.attractData.contains(ATTRACTING)) {
            this.attractData.putBoolean(ATTRACTING, false);
        }
        return this.attractData.getBoolean(ATTRACTING);
    }

    @Override
    public void setAttracting(boolean attracting) {
        this.attractData.putBoolean(ATTRACTING, attracting);
        if (!attracting) {
            this.attractData.putDouble(ATTRACT_DIS, 0);
        }
    }

    @Override
    public void setAttracting(boolean attracting, double dis) {
        setAttracting(attracting);
        this.attractData.putDouble(ATTRACT_DIS, attracting ? dis : 0);
    }

    @Override
    public boolean getEnable() {
        if (!this.attractData.contains(ENABLE)) {
            this.attractData.putBoolean(ENABLE, true);
        }
        return this.attractData.getBoolean(ENABLE);
    }

    @Override
    public void setEnable(boolean enable) {
        this.attractData.putBoolean(ENABLE, enable);
    }

    @Override
    public UUID getAttractOwner() {
        if (!this.attractData.contains(ATTRACT_OWNER)) {
            this.attractData.putUuid(ATTRACT_OWNER, CreatureMagnetItem.EMPTY_UUID);
        }
        return this.attractData.getUuid(ATTRACT_OWNER);
    }

    @Override
    public void setAttractOwner(UUID uuid) {
        this.attractData.putUuid(ATTRACT_OWNER, uuid);
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
                return livingEntity.world.getEntitiesByClass(PlayerEntity.class, entity.getBoundingBox().expand(degaussingDis), player -> player.getInventory().containsAny(stack -> stack.isOf(ItemRegistries.PORTABLE_DEMAGNETIZER_ITEM) && stack.getNbt() != null && stack.getNbt().getBoolean(ENABLE))).isEmpty();
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean isFollowing() {
        if (!this.attractData.contains(FOLLOWING)) {
            this.attractData.putBoolean(FOLLOWING, false);
        }
        return this.attractData.getBoolean(FOLLOWING);
    }

    @Override
    public void setFollowing(boolean following) {
        this.attractData.putBoolean(FOLLOWING, following);
    }

    @Override
    public boolean ignoreFallDamage() {
        if (!this.attractData.contains(IGNORE_FALL_DAMAGE)) {
            this.attractData.putBoolean(IGNORE_FALL_DAMAGE, false);
        }
        return this.attractData.getBoolean(IGNORE_FALL_DAMAGE);
    }

    @Override
    public void setIgnoreFallDamage(boolean ignoreFallDamage) {
        this.attractData.putBoolean(IGNORE_FALL_DAMAGE, ignoreFallDamage);
    }

    @Override
    public boolean getMagneticLevitationMode() {
        if (!this.attractData.contains(MAGNETIC_LEVITATION_MODE)) {
            this.attractData.putBoolean(MAGNETIC_LEVITATION_MODE, true);
        }
        return this.attractData.getBoolean(MAGNETIC_LEVITATION_MODE);
    }

    @Override
    public void setMagneticLevitationMode(boolean mode) {
        this.attractData.putBoolean(MAGNETIC_LEVITATION_MODE, mode);
    }

    @Override
    public int getLevitationTick() {
        if (!this.attractData.contains(LEVITATION_TICK) && this.attractData.getInt(LEVITATION_TICK) < 0) {
            this.attractData.putInt(LEVITATION_TICK, 0);
        }
        return this.attractData.getInt(LEVITATION_TICK);
    }

    @Override
    public void setLevitationTick(int tick) {
        this.attractData.putInt(LEVITATION_TICK, Math.max(tick, 0));
    }

    @Override
    public boolean getAutomaticLevitation() {
        if (!this.attractData.contains(AUTOMATIC_LEVITATION)) {
            this.attractData.putBoolean(AUTOMATIC_LEVITATION, false);
        }
        return this.attractData.getBoolean(AUTOMATIC_LEVITATION);
    }

    @Override
    public void setAutomaticLevitation(boolean enable) {
        this.attractData.putBoolean(AUTOMATIC_LEVITATION, enable);
    }

    @Override
    public boolean isAdsorbedByEntity() {
        if (!this.attractData.contains(ADSORBED_BY_ENTITY)) {
            this.attractData.putBoolean(ADSORBED_BY_ENTITY, false);
        }
        return this.attractData.getBoolean(ADSORBED_BY_ENTITY) && !this.isAdsorbedByBlock();
    }

    @Override
    public void setAdsorbedByEntity(boolean adsorbed) {
        this.attractData.putBoolean(ADSORBED_BY_ENTITY, adsorbed);
        if (adsorbed) {
            this.setAdsorbedByBlock(false);
        } else {
            this.setAdsorptionEntityId(CreatureMagnetItem.EMPTY_UUID, true);
        }
    }

    @Override
    public boolean isAdsorbedByBlock() {
        if (!this.attractData.contains(ADSORBED_BY_BLOCK)) {
            this.attractData.putBoolean(ADSORBED_BY_BLOCK, false);
        }
        return this.attractData.getBoolean(ADSORBED_BY_BLOCK) && !this.isAdsorbedByEntity();
    }

    @Override
    public void setAdsorbedByBlock(boolean adsorbed) {
        this.attractData.putBoolean(ADSORBED_BY_BLOCK, adsorbed);
        if (adsorbed) {
            this.setAdsorbedByEntity(false);
        } else {
            this.setAdsorptionBlockPos(new BlockPos(0, 0, 0), true);
        }
    }

    @Override
    public UUID getAdsorptionEntityId() {
        if (!this.attractData.contains(ADSORPTION_ENTITY_ID)) {
            this.attractData.putUuid(ADSORPTION_ENTITY_ID, CreatureMagnetItem.EMPTY_UUID);
        }
        return this.attractData.getUuid(ADSORPTION_ENTITY_ID);
    }

    @Override
    public void setAdsorptionEntityId(UUID uuid, boolean clear) {
        if (!clear) {
            this.setAdsorbedByEntity(true);
            this.setFollowing(false);
        }
        this.attractData.putUuid(ADSORPTION_ENTITY_ID, uuid);
    }

    @Override
    public BlockPos getAdsorptionBlockPos() {
        if (!this.attractData.contains(ADSORPTION_BLOCK_POS) || this.attractData.getIntArray(ADSORPTION_BLOCK_POS).length != 3) {
            this.attractData.putIntArray(ADSORPTION_BLOCK_POS, new int[]{0, 0, 0});
        }
        int[] pos = this.attractData.getIntArray(ADSORPTION_BLOCK_POS);
        return new BlockPos(pos[0], pos[1], pos[2]);
    }

    @Override
    public void setAdsorptionBlockPos(BlockPos pos, boolean clear) {
        if (!clear) {
            this.setAdsorbedByBlock(true);
            this.setFollowing(false);
        }
        this.attractData.putIntArray(ADSORPTION_BLOCK_POS, new int[]{pos.getX(), pos.getY(), pos.getZ()});
    }

    @Inject(method = "writeNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", shift = At.Shift.AFTER))
    public void writePocketsDataToNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        this.attractData.putDouble(ATTRACT_DIS, this.getAttractDis());
        this.attractData.putBoolean(ATTRACTING, this.isAttracting() && this.canAttract());
        this.attractData.putBoolean(ENABLE, this.getEnable());
        this.attractData.putUuid(ATTRACT_OWNER, this.getAttractOwner());
        this.attractData.putBoolean(FOLLOWING, this.isFollowing());
        this.attractData.putBoolean(IGNORE_FALL_DAMAGE, this.ignoreFallDamage());
        this.attractData.putBoolean(MAGNETIC_LEVITATION_MODE, this.getMagneticLevitationMode());
        this.attractData.putInt(LEVITATION_TICK, this.getLevitationTick());
        this.attractData.putBoolean(AUTOMATIC_LEVITATION, this.getAutomaticLevitation());
        this.attractData.putBoolean(ADSORBED_BY_ENTITY, this.isAdsorbedByEntity());
        this.attractData.putBoolean(ADSORBED_BY_BLOCK, this.isAdsorbedByBlock());
        this.attractData.putUuid(ADSORPTION_ENTITY_ID, this.getAdsorptionEntityId());
        this.attractData.putIntArray(ADSORPTION_BLOCK_POS, new int[]{this.getAdsorptionBlockPos().getX(), this.getAdsorptionBlockPos().getY(), this.getAdsorptionBlockPos().getZ()});
        nbt.put(ATTRACT_DATA, this.attractData);
    }

    @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V", shift = At.Shift.AFTER))
    public void readPocketsDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(ATTRACT_DATA)) {
            NbtCompound data = nbt.getCompound(ATTRACT_DATA);
            this.attractData = data;
            if (data.contains(ATTRACT_DIS)) {
                this.attractDis = data.getDouble(ATTRACT_DIS);
            }
            if (data.contains(ATTRACTING)) {
                this.isAttracting = data.getBoolean(ATTRACTING);
            }
            if (data.contains(ENABLE)) {
                this.enable = data.getBoolean(ENABLE);
            }
            if (data.contains(ATTRACT_OWNER)) {
                this.attractOwner = data.getUuid(ATTRACT_OWNER);
            }
            if (data.contains(FOLLOWING)) {
                this.isFollowing = data.getBoolean(FOLLOWING);
            }
            if (data.contains(IGNORE_FALL_DAMAGE)) {
                this.ignoreFallDamage = data.getBoolean(IGNORE_FALL_DAMAGE);
            }
            if (data.contains(MAGNETIC_LEVITATION_MODE)) {
                this.magneticLevitationMode = data.getBoolean(MAGNETIC_LEVITATION_MODE);
            }
            if (data.contains(LEVITATION_TICK)) {
                this.levitationTick = data.getInt(LEVITATION_TICK);
            }
            if (data.contains(AUTOMATIC_LEVITATION)) {
                this.automaticLevitation = data.getBoolean(AUTOMATIC_LEVITATION);
            }
            if (data.contains(ADSORBED_BY_ENTITY)) {
                this.isAdsorbedByEntity = data.getBoolean(ADSORBED_BY_ENTITY);
            }
            if (data.contains(ADSORBED_BY_BLOCK)) {
                this.isAdsorbedByBlock = data.getBoolean(ADSORBED_BY_BLOCK);
            }
            if (data.contains(ADSORPTION_ENTITY_ID)) {
                this.adsorptionEntityId = data.getUuid(ADSORPTION_ENTITY_ID);
            }
            if (data.contains(ADSORPTION_BLOCK_POS) && data.getIntArray(ADSORPTION_BLOCK_POS).length == 3) {
                this.adsorptionBlockPos = new BlockPos(data.getIntArray(ADSORPTION_BLOCK_POS)[0], data.getIntArray(ADSORPTION_BLOCK_POS)[1], data.getIntArray(ADSORPTION_BLOCK_POS)[2]);
            }
        }
    }

}
