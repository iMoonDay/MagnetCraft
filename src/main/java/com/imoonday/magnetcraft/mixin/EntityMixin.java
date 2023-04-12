package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.api.MagnetCraftEntity;
import com.imoonday.magnetcraft.common.blocks.entities.AttractSensorEntity;
import com.imoonday.magnetcraft.common.entities.entrance.ShuttleEntranceEntity;
import com.imoonday.magnetcraft.common.items.magnets.CreatureMagnetItem;
import com.imoonday.magnetcraft.common.tags.BlockTags;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(Entity.class)
public class EntityMixin implements MagnetCraftEntity {

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
    private static final String SHUTTLE_DATA = "ShuttleData";
    private static final String SHUTTLING = "Shuttling";
    private static final String ROUTE = "Route";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";
    private static final String TICK = "Tick";
    private static final String COOLDOWN = "Cooldown";
    private static final String INVISIBLE = "Invisible";
    private static final String INVULNERABLE = "Invulnerable";
    private static final String NO_CLIP = "NoClip";
    private static final String NO_GRAVITY = "NoGravity";
    private static final String FLYING = "Flying";
    private static final String ALLOW_MODIFY_WORLD = "AllowModifyWorld";
    private static final int SHUTTLE_COOLDOWN = 20;

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

    protected NbtCompound shuttleData = new NbtCompound();
    protected boolean shuttling = false;
    protected ArrayList<Vec3d> route = new ArrayList<>();
    protected int currentRouteTick = 0;
    protected int shuttleCooldown = 0;
    protected boolean wasInvisible = false;
    protected boolean wasInvulnerable = false;
    protected boolean wasNoClip = false;
    protected boolean wasNoGravity = false;
    protected boolean wasFlying = false;
    protected boolean wasAllowModifyWorld = false;

    @Override
    public void shuttle(ArrayList<Vec3d> route) {
        Entity entity = (Entity) (Object) this;
        if (entity.hasShuttleCooldown()) {
            return;
        }
        entity.setShuttling(true);
        entity.setRoute(route);
        entity.setCurrentRouteTick(0);
        entity.setWasInvisible(entity.isInvisible());
        entity.setWasInvulnerable(entity.isInvulnerable());
        entity.setWasNoClip(entity.noClip);
        entity.setWasNoGravity(entity.hasNoGravity());
        entity.setWasFlying(entity instanceof PlayerEntity player && player.getAbilities().flying);
        entity.setWasAllowModifyWorld(entity instanceof PlayerEntity player && player.getAbilities().allowModifyWorld);
        entity.world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.VOICE, 1, 1);
    }

    private void shuttleTick() {
        Entity entity = (Entity) (Object) this;
        if (entity.hasShuttleCooldown() && (entity.world.getOtherEntities(entity, entity.getBoundingBox(), ShuttleEntranceEntity.class::isInstance).isEmpty() || entity.getShuttleCooldown() != SHUTTLE_COOLDOWN)) {
            entity.minusShuttleCooldown();
        }
        if (!entity.isShuttling()) {
            return;
        }
        int times = 1;
        while (times <= 3) {
            int tick = entity.getCurrentRouteTick();
            int maxTick = entity.getRoute().size();
            if (tick >= maxTick) {
                entity.setShuttling(false);
                entity.setRoute(new ArrayList<>());
                entity.setCurrentRouteTick(0);
                entity.setInvisible(entity.wasInvisible());
                entity.setInvulnerable(entity.wasInvulnerable());
                entity.noClip = entity.wasNoClip();
                entity.setNoGravity(entity.wasNoGravity());
                if (entity instanceof LivingEntity livingEntity) {
                    Map<StatusEffect, StatusEffectInstance> statusEffects = livingEntity.getActiveStatusEffects();
                    for (StatusEffect statusEffect : Arrays.asList(StatusEffects.BLINDNESS, StatusEffects.DARKNESS)) {
                        StatusEffectInstance statusEffectInstance = statusEffects.get(statusEffect);
                        if (statusEffectInstance != null) {
                            StatusEffect type = statusEffectInstance.getEffectType();
                            System.out.println(statusEffectInstance.getDuration());
                            if (statusEffectInstance.getDuration() <= 21) {
                                livingEntity.removeStatusEffect(type);
                            }
                        }
                    }
                    if (livingEntity instanceof PlayerEntity player) {
                        player.getAbilities().flying = player.wasFlying();
                        player.getAbilities().allowModifyWorld = player.wasAllowModifyWorld();
                    }
                }
                entity.world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.VOICE, 1, 1);
                entity.shuttleCooldown();
                return;
            }
            entity.setInvisible(true);
            entity.setInvulnerable(true);
            entity.noClip = true;
            entity.setNoGravity(true);
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 21, 0, false, false, false));
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 21, 0, false, false, false));
                if (livingEntity instanceof PlayerEntity player) {
                    player.getAbilities().flying = true;
                    player.getAbilities().allowModifyWorld = false;
                }
            }
            entity.refreshPositionAfterTeleport(entity.getRoute().get(tick));
            entity.addCurrentRouteTick();
            times++;
        }
    }

    @Override
    public void shuttleCooldown() {
        Entity entity = (Entity) (Object) this;
        entity.setShuttleCooldown(SHUTTLE_COOLDOWN);
    }

    @Override
    public boolean hasShuttleCooldown() {
        Entity entity = (Entity) (Object) this;
        return entity.getShuttleCooldown() > 0;
    }

    @Override
    public boolean isShuttling() {
        return getShuttleDataBooleanKey(SHUTTLING);
    }

    @Override
    public void setShuttling(boolean shuttling) {
        setShuttleDataBooleanValue(SHUTTLING, shuttling);
    }

    @Override
    public ArrayList<Vec3d> getRoute() {
        if (!this.shuttleData.contains(ROUTE)) {
            this.shuttleData.put(ROUTE, new NbtList());
        }
        return asArrayList(this.shuttleData);
    }

    @NotNull
    private ArrayList<Vec3d> asArrayList(NbtCompound shuttleData) {
        return shuttleData.getList(ROUTE, NbtElement.COMPOUND_TYPE).stream().map(nbtElement -> new Vec3d(((NbtCompound) nbtElement).getDouble(X), ((NbtCompound) nbtElement).getDouble(Y), ((NbtCompound) nbtElement).getDouble(Z))).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void setRoute(ArrayList<Vec3d> route) {
        this.shuttleData.put(ROUTE, asNbtList(route));
    }

    @NotNull
    private static NbtList asNbtList(ArrayList<Vec3d> route) {
        NbtList list = new NbtList();
        for (Vec3d pos : route) {
            NbtCompound nbt = new NbtCompound();
            nbt.putDouble(X, pos.x);
            nbt.putDouble(Y, pos.y);
            nbt.putDouble(Z, pos.z);
            list.add(nbt);
        }
        return list;
    }

    @Override
    public int getCurrentRouteTick() {
        return getShuttleDataIntKey(TICK);
    }

    @Override
    public void setCurrentRouteTick(int currentRouteTick) {
        setShuttleDataIntValue(TICK, currentRouteTick);
    }

    @Override
    public void addCurrentRouteTick() {
        setShuttleDataIntValue(TICK, ++this.currentRouteTick);
    }

    @Override
    public int getShuttleCooldown() {
        return getShuttleDataIntKey(COOLDOWN);
    }

    @Override
    public void setShuttleCooldown(int shuttleCooldown) {
        setShuttleDataIntValue(COOLDOWN, shuttleCooldown);
    }

    @Override
    public void minusShuttleCooldown() {
        setShuttleDataIntValue(COOLDOWN, --this.shuttleCooldown);
    }

    @Override
    public NbtCompound getShuttleData() {
        if (this.shuttleData == null) {
            this.shuttleData = new NbtCompound();
        }
        return this.shuttleData;
    }

    @Override
    public void setShuttleData(NbtCompound shuttleData) {
        this.shuttleData = shuttleData;
    }

    @Override
    public boolean wasInvisible() {
        return getShuttleDataBooleanKey(INVISIBLE);
    }

    @Override
    public void setWasInvisible(boolean wasInvisible) {
        setShuttleDataBooleanValue(INVISIBLE, wasInvisible);
    }

    @Override
    public boolean wasInvulnerable() {
        return getShuttleDataBooleanKey(INVULNERABLE);
    }

    @Override
    public void setWasInvulnerable(boolean wasInvulnerable) {
        setShuttleDataBooleanValue(INVULNERABLE, wasInvulnerable);
    }

    @Override
    public boolean wasNoClip() {
        return getShuttleDataBooleanKey(NO_CLIP);
    }

    @Override
    public void setWasNoClip(boolean wasNoClip) {
        setShuttleDataBooleanValue(NO_CLIP, wasNoClip);
    }

    @Override
    public boolean wasNoGravity() {
        return getShuttleDataBooleanKey(NO_GRAVITY);
    }

    @Override
    public void setWasNoGravity(boolean wasNoGravity) {
        setShuttleDataBooleanValue(NO_GRAVITY, wasNoGravity);
    }

    @Override
    public boolean wasFlying() {
        return getShuttleDataBooleanKey(FLYING);
    }

    @Override
    public void setWasFlying(boolean wasFlying) {
        setShuttleDataBooleanValue(FLYING, wasFlying);
    }

    @Override
    public boolean wasAllowModifyWorld() {
        return getShuttleDataBooleanKey(ALLOW_MODIFY_WORLD);
    }

    @Override
    public void setWasAllowModifyWorld(boolean wasAllowModifyWorld) {
        setShuttleDataBooleanValue(ALLOW_MODIFY_WORLD, wasAllowModifyWorld);
    }

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
            if (pass && targetEntity.canReachTo(entity.getEyePos())) {
                boolean hasNearerPlayer;
                boolean hasNearerEntity = false;
                if (entity instanceof PlayerEntity) {
                    hasNearerPlayer = targetEntity.world.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), dis, entity1 -> entity1.isAttracting() && targetEntity.canReachTo(entity1.getEyePos())) != entity;
                } else {
                    hasNearerPlayer = targetEntity.world.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), dis, entity1 -> entity1.isAttracting() && targetEntity.canReachTo(entity1.getEyePos())) != null;
                    hasNearerEntity = !targetEntity.world.getOtherEntities(targetEntity, entity.getBoundingBox().expand(dis), otherEntity -> (!(otherEntity instanceof PlayerEntity) && otherEntity.distanceTo(targetEntity) < entity.distanceTo(targetEntity) && otherEntity.isAttracting() && otherEntity.getEnable() && otherEntity.isAlive() && targetEntity.canReachTo(otherEntity.getEyePos()))).isEmpty();
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
    public boolean canReachTo(Vec3d pos) {
        Entity entity = (Entity) (Object) this;
        for (double d = 0; d <= 1; d += 0.01) {
            Vec3d entityPos = entity.getEyePos();
            Vec3d newPos = entityPos.add(pos.subtract(entityPos).multiply(d));
            Vec3d offset = newPos.subtract(entityPos);
            Box newBox = entity.getBoundingBox().offset(offset);
            List<BlockPos> posList = BlockPos.stream(newBox).toList();
            for (BlockPos blockPos : posList) {
                BlockState blockState = entity.world.getBlockState(blockPos);
                if (!blockState.isIn(BlockTags.BLOCK_ATTRACT_BLOCKS)) {
                    VoxelShape voxelShape2 = VoxelShapes.empty();
                    for (Direction direction : Direction.values()) {
                        BlockPos neighborBlockPos = blockPos.offset(direction);
                        BlockState neighborBlockState = entity.world.getBlockState(neighborBlockPos);
                        if (neighborBlockState.isIn(BlockTags.BLOCK_ATTRACT_BLOCKS)) {
                            VoxelShape voxelShape = blockState.getOutlineShape(entity.world, neighborBlockPos, AttractSensorEntity.createHoldingShapeContext());
                            voxelShape2 = VoxelShapes.union(voxelShape2, voxelShape.offset(neighborBlockPos.getX(), neighborBlockPos.getY(), neighborBlockPos.getZ()));
                        }
                    }
                    boolean collide = VoxelShapes.matchesAnywhere(voxelShape2, VoxelShapes.cuboid(newBox), BooleanBiFunction.AND);
                    if (collide) {
                        return false;
                    }
                    continue;
                }
                VoxelShape voxelShape = blockState.getOutlineShape(entity.world, blockPos, AttractSensorEntity.createHoldingShapeContext());
                VoxelShape voxelShape2 = voxelShape.offset(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                boolean collide = VoxelShapes.matchesAnywhere(voxelShape2, VoxelShapes.cuboid(newBox), BooleanBiFunction.AND);
                if (collide) {
                    return false;
                }
            }
        }
        return true;
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
        this.shuttleData = this.getShuttleData();
        this.shuttling = this.isShuttling();
        this.route = this.getRoute();
        this.currentRouteTick = this.getCurrentRouteTick();
        this.shuttleCooldown = this.getShuttleCooldown();
        this.wasInvisible = this.wasInvisible();
        this.wasInvulnerable = this.wasInvulnerable();
        this.wasNoClip = this.wasNoClip();
        this.wasNoGravity = this.wasNoGravity();
        entity.tryAttract();
        CreatureMagnetItem.followingCheck(entity);
        shuttleTick();
    }

    @Override
    public NbtCompound getAttractData() {
        if (this.attractData == null) {
            this.attractData = new NbtCompound();
        }
        return this.attractData;
    }

    @Override
    public boolean clearAttractData() {
        this.attractData = new NbtCompound();
        return this.attractData.isEmpty();
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
        return getAttractDataBooleanKey(ATTRACTING);
    }

    @Override
    public void setAttracting(boolean attracting) {
        setValue(DataType.ATTRACT_DATA, ATTRACTING, attracting);
//        setAttractDataBooleanValue(ATTRACTING, attracting);
        if (!attracting) {
            setValue(DataType.ATTRACT_DATA, ATTRACT_DIS, (double) 0);
//            this.attractData.putDouble(ATTRACT_DIS, 0);
        }
    }

    @Override
    public void setAttracting(boolean attracting, double dis) {
        setAttracting(attracting);
        this.attractData.putDouble(ATTRACT_DIS, attracting ? dis : 0);
    }

    @Override
    public boolean getEnable() {
        return getAttractDataBooleanKeyWithTrue(ENABLE);
    }

    @Override
    public void setEnable(boolean enable) {
        setAttractDataBooleanValue(ENABLE, enable);
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
        return getAttractDataBooleanKey(FOLLOWING);
    }

    @Override
    public void setFollowing(boolean following) {
        setAttractDataBooleanValue(FOLLOWING, following);
    }

    @Override
    public boolean ignoreFallDamage() {
        return getAttractDataBooleanKey(IGNORE_FALL_DAMAGE);
    }

    @Override
    public void setIgnoreFallDamage(boolean ignoreFallDamage) {
        setAttractDataBooleanValue(IGNORE_FALL_DAMAGE, ignoreFallDamage);
    }

    @Override
    public boolean getMagneticLevitationMode() {
        return getAttractDataBooleanKeyWithTrue(MAGNETIC_LEVITATION_MODE);
    }

    @Override
    public void setMagneticLevitationMode(boolean mode) {
        setAttractDataBooleanValue(MAGNETIC_LEVITATION_MODE, mode);
    }

    @Override
    public int getLevitationTick() {
        if (!this.attractData.contains(LEVITATION_TICK) || this.attractData.getInt(LEVITATION_TICK) < 0) {
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
        return getAttractDataBooleanKeyWithTrue(AUTOMATIC_LEVITATION);
    }

    @Override
    public void setAutomaticLevitation(boolean enable) {
        setAttractDataBooleanValue(AUTOMATIC_LEVITATION, enable);
    }

    @Override
    public boolean isAdsorbedByEntity() {
        return getAttractDataBooleanKey(ADSORBED_BY_ENTITY) && !this.isAdsorbedByBlock();
    }

    @Override
    public void setAdsorbedByEntity(boolean adsorbed) {
        setAttractDataBooleanValue(ADSORBED_BY_ENTITY, adsorbed);
        if (adsorbed) {
            this.setAdsorbedByBlock(false);
        } else {
            this.setAdsorptionEntityId(CreatureMagnetItem.EMPTY_UUID, true);
        }
    }

    @Override
    public boolean isAdsorbedByBlock() {
        return getAttractDataBooleanKey(ADSORBED_BY_BLOCK) && !this.isAdsorbedByEntity();
    }

    @Override
    public void setAdsorbedByBlock(boolean adsorbed) {
        setAttractDataBooleanValue(ADSORBED_BY_BLOCK, adsorbed);
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

    private boolean getShuttleDataBooleanKey(String key) {
        if (!this.shuttleData.contains(key)) {
            setShuttleDataBooleanValue(key, false);
        }
        return this.shuttleData.getBoolean(key);
    }

    private void setShuttleDataBooleanValue(String key, boolean value) {
        this.shuttleData.putBoolean(key, value);
    }

    private int getShuttleDataIntKey(String key) {
        if (!this.shuttleData.contains(key)) {
            setShuttleDataIntValue(key, 0);
        }
        return this.shuttleData.getInt(key);
    }

    private void setShuttleDataIntValue(String key, int value) {
        this.shuttleData.putInt(key, value);
    }

    private boolean getAttractDataBooleanKey(String key) {
        if (!this.attractData.contains(key)) {
            setShuttleDataBooleanValue(key, false);
        }
        return this.attractData.getBoolean(key);
    }

    private boolean getAttractDataBooleanKeyWithTrue(String key) {
        if (!this.attractData.contains(key)) {
            setShuttleDataBooleanValue(key, true);
        }
        return this.attractData.getBoolean(key);
    }

    private void setAttractDataBooleanValue(String key, boolean value) {
        this.attractData.putBoolean(key, value);
    }

    private int getAttractDataIntKey(String key) {
        if (!this.attractData.contains(key)) {
            setShuttleDataIntValue(key, 0);
        }
        return this.attractData.getInt(key);
    }

    private void setAttractDataIntValue(String key, int value) {
        this.attractData.putInt(key, value);
    }

    private <T> void setValue(DataType dataType, String key, T value) {
        NbtCompound data = switch (dataType) {
            case ATTRACT_DATA -> this.attractData;
            case SHUTTLE_DATA -> this.shuttleData;
        };
        if (value instanceof Boolean booleanValue) {
            data.putBoolean(key, booleanValue);
        } else if (value instanceof Integer intValue) {
            data.putInt(key, intValue);
        } else if (value instanceof Double doubleValue) {
            data.putDouble(key, doubleValue);
        } else if (value instanceof UUID uuidValue) {
            data.putUuid(key, uuidValue);
        } else if (value instanceof int[] ints) {
            data.putIntArray(key, ints);
        } else if (value instanceof NbtElement element) {
            data.put(key, element);
        }
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
        setShuttleDataBooleanValue(SHUTTLING, this.isShuttling());
        this.shuttleData.put(ROUTE, asNbtList(this.getRoute()));
        setShuttleDataIntValue(TICK, this.getCurrentRouteTick());
        setShuttleDataIntValue(COOLDOWN, this.getShuttleCooldown());
        setShuttleDataBooleanValue(INVISIBLE, this.wasInvisible());
        setShuttleDataBooleanValue(INVULNERABLE, this.wasInvulnerable());
        setShuttleDataBooleanValue(NO_CLIP, this.wasNoClip());
        setShuttleDataBooleanValue(NO_GRAVITY, this.wasNoGravity());
        setShuttleDataBooleanValue(FLYING, this.wasFlying());
        setShuttleDataBooleanValue(ALLOW_MODIFY_WORLD, this.wasAllowModifyWorld());
        nbt.put(SHUTTLE_DATA, this.shuttleData);
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
        if (nbt.contains(SHUTTLE_DATA)) {
            NbtCompound data = nbt.getCompound(SHUTTLE_DATA);
            this.shuttleData = data;
            if (data.contains(SHUTTLING)) {
                this.shuttling = data.getBoolean(SHUTTLING);
            }
            if (data.contains(ROUTE)) {
                this.route = asArrayList(data);
            }
            if (data.contains(TICK)) {
                this.currentRouteTick = data.getInt(TICK);
            }
            if (data.contains(COOLDOWN)) {
                this.shuttleCooldown = data.getInt(COOLDOWN);
            }
            if (data.contains(INVISIBLE)) {
                this.wasInvisible = data.getBoolean(INVISIBLE);
            }
            if (data.contains(INVULNERABLE)) {
                this.wasInvulnerable = data.getBoolean(INVULNERABLE);
            }
            if (data.contains(NO_CLIP)) {
                this.wasNoClip = data.getBoolean(NO_CLIP);
            }
            if (data.contains(NO_GRAVITY)) {
                this.wasNoGravity = data.getBoolean(NO_GRAVITY);
            }
            if (data.contains(FLYING)) {
                this.wasFlying = data.getBoolean(FLYING);
            }
            if (data.contains(ALLOW_MODIFY_WORLD)) {
                this.wasAllowModifyWorld = data.getBoolean(ALLOW_MODIFY_WORLD);
            }
        }
    }

}