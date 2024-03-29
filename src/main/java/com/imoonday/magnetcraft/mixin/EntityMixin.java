package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.api.MagnetCraftEntity;
import com.imoonday.magnetcraft.common.blocks.entities.AttractSensorEntity;
import com.imoonday.magnetcraft.common.entities.entrance.ShuttleEntranceEntity;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.imoonday.magnetcraft.common.items.magnets.CreatureMagnetItem.EMPTY_UUID;
import static com.imoonday.magnetcraft.common.items.magnets.CreatureMagnetItem.followingCheck;

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
    private static final int SHUTTLE_COOLDOWN = 20;
    private static final String LAST_GAME_MODE = "LastGameMode";
    private static final String FLYING = "WasFlying";

    protected NbtCompound attractData = new NbtCompound();
    protected double attractDis = 0;
    protected boolean isAttracting = false;
    protected boolean enable = true;
    protected UUID attractOwner = EMPTY_UUID;
    protected boolean isFollowing = false;
    protected boolean ignoreFallDamage = false;
    protected boolean magneticLevitationMode = true;
    protected int levitationTick = 0;
    protected boolean automaticLevitation = false;
    protected boolean isAdsorbedByEntity = false;
    protected boolean isAdsorbedByBlock = false;
    protected UUID adsorptionEntityId = EMPTY_UUID;
    protected BlockPos adsorptionBlockPos = BlockPos.ORIGIN;

    protected NbtCompound shuttleData = new NbtCompound();
    protected boolean shuttling = false;
    protected ArrayList<Vec3d> route = new ArrayList<>();
    protected int currentRouteTick = 0;
    protected int shuttleCooldown = 0;
    protected boolean wasInvisible = false;
    protected boolean wasInvulnerable = false;
    protected boolean wasNoClip = false;
    protected boolean wasNoGravity = false;
    protected int lastGameMode = 0;
    protected boolean wasFlying = false;

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
        entity.setLastGameMode(entity instanceof ServerPlayerEntity player ? player.interactionManager.getGameMode().getId() : 0);
        entity.setWasFlying(entity instanceof ServerPlayerEntity player && player.getAbilities().flying);
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
            if (tick < maxTick) {
                if (entity instanceof ServerPlayerEntity player) {
                    if (!player.isSpectator()) {
                        player.changeGameMode(GameMode.SPECTATOR);
                    }
                } else {
                    if (!entity.isInvisible()) {
                        entity.setInvisible(true);
                    }
                    if (!entity.isInvulnerable()) {
                        entity.setInvulnerable(true);
                    }
                    if (!entity.noClip) {
                        entity.noClip = true;
                    }
                    if (!entity.hasNoGravity()) {
                        entity.setNoGravity(true);
                    }
                }
                entity.refreshPositionAfterTeleport(entity.getRoute().get(tick));
                entity.addCurrentRouteTick();
                times++;
            } else {
                entity.setShuttling(false);
                entity.setRoute(new ArrayList<>());
                entity.setCurrentRouteTick(0);
                entity.setIgnoreFallDamage(true);
                if (entity instanceof ServerPlayerEntity player) {
                    if (player.wasFlying() != player.getAbilities().flying) {
                        player.getAbilities().flying = player.wasFlying();
                    }
                    if (player.getLastGameMode() != player.interactionManager.getGameMode().getId()) {
                        player.changeGameMode(GameMode.byId(player.getLastGameMode()));
                    }
                } else {
                    if (entity.wasInvisible() != entity.isInvisible()) {
                        entity.setInvisible(entity.wasInvisible());
                    }
                    if (entity.wasInvulnerable() != entity.isInvulnerable()) {
                        entity.setInvulnerable(entity.wasInvulnerable());
                    }
                    if (entity.wasNoClip() != entity.noClip) {
                        entity.noClip = entity.wasNoClip();
                    }
                    if (entity.wasNoGravity() != entity.hasNoGravity()) {
                        entity.setNoGravity(entity.wasNoGravity());
                    }
                }
                entity.world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.VOICE, 1, 1);
                entity.shuttleCooldown();
                return;
            }
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
        return getBoolean(this.shuttleData, SHUTTLING, false);
    }

    @Override
    public void setShuttling(boolean shuttling) {
        this.shuttleData.putBoolean(SHUTTLING, shuttling);
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
        return getInt(this.shuttleData, TICK);
    }

    @Override
    public void setCurrentRouteTick(int currentRouteTick) {
        this.shuttleData.putInt(TICK, currentRouteTick);
    }

    @Override
    public void addCurrentRouteTick() {
        int value = ++this.currentRouteTick;
        this.shuttleData.putInt(TICK, value);
    }

    @Override
    public int getShuttleCooldown() {
        return getInt(this.shuttleData, COOLDOWN);
    }

    @Override
    public void setShuttleCooldown(int shuttleCooldown) {
        this.shuttleData.putInt(COOLDOWN, shuttleCooldown);
    }

    @Override
    public void minusShuttleCooldown() {
        this.shuttleData.putInt(COOLDOWN, --this.shuttleCooldown);
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
    public int getLastGameMode() {
        return getInt(this.shuttleData, LAST_GAME_MODE);
    }

    @Override
    public void setLastGameMode(int lastGameMode) {
        this.shuttleData.putInt(LAST_GAME_MODE, lastGameMode);
    }

    @Override
    public boolean wasFlying() {
        return getBoolean(this.shuttleData, FLYING, false);
    }

    @Override
    public void setWasFlying(boolean wasFlying) {
        this.shuttleData.putBoolean(FLYING, wasFlying);
    }

    @Override
    public boolean wasInvisible() {
        return getBoolean(this.shuttleData, INVISIBLE, false);
    }

    @Override
    public void setWasInvisible(boolean wasInvisible) {
        this.shuttleData.putBoolean(INVISIBLE, wasInvisible);
    }

    @Override
    public boolean wasInvulnerable() {
        return getBoolean(this.shuttleData, INVULNERABLE, false);
    }

    @Override
    public void setWasInvulnerable(boolean wasInvulnerable) {
        this.shuttleData.putBoolean(INVULNERABLE, wasInvulnerable);
    }

    @Override
    public boolean wasNoClip() {
        return getBoolean(this.shuttleData, NO_CLIP, false);
    }

    @Override
    public void setWasNoClip(boolean wasNoClip) {
        this.shuttleData.putBoolean(NO_CLIP, wasNoClip);
    }

    @Override
    public boolean wasNoGravity() {
        return getBoolean(this.shuttleData, NO_GRAVITY, false);
    }

    @Override
    public void setWasNoGravity(boolean wasNoGravity) {
        this.shuttleData.putBoolean(NO_GRAVITY, wasNoGravity);
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
        tryAttract();
        followingCheck(entity);
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
        return getBoolean(this.attractData, ATTRACTING, false);
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
        double value = attracting ? dis : 0;
        this.attractData.putDouble(ATTRACT_DIS, value);
    }

    @Override
    public boolean getEnable() {
        return getBoolean(this.attractData, ENABLE, true);
    }

    @Override
    public void setEnable(boolean enable) {
        this.attractData.putBoolean(ENABLE, enable);
    }

    @Override
    public UUID getAttractOwner() {
        return getUuid(this.attractData, ATTRACT_OWNER);
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
        return getBoolean(this.attractData, FOLLOWING, false);
    }

    @Override
    public void setFollowing(boolean following) {
        this.attractData.putBoolean(FOLLOWING, following);
    }

    @Override
    public boolean ignoreFallDamage() {
        return getBoolean(this.attractData, IGNORE_FALL_DAMAGE, false);
    }

    @Override
    public void setIgnoreFallDamage(boolean ignoreFallDamage) {
        this.attractData.putBoolean(IGNORE_FALL_DAMAGE, ignoreFallDamage);
    }

    @Override
    public boolean getMagneticLevitationMode() {
        return getBoolean(this.attractData, MAGNETIC_LEVITATION_MODE, true);
    }

    @Override
    public void setMagneticLevitationMode(boolean mode) {
        this.attractData.putBoolean(MAGNETIC_LEVITATION_MODE, mode);
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
        return getBoolean(this.attractData, AUTOMATIC_LEVITATION, true);
    }

    @Override
    public void setAutomaticLevitation(boolean enable) {
        this.attractData.putBoolean(AUTOMATIC_LEVITATION, enable);
    }

    @Override
    public boolean isAdsorbedByEntity() {
        return getBoolean(this.shuttleData, ADSORBED_BY_ENTITY, false) && !this.isAdsorbedByBlock();
    }

    @Override
    public void setAdsorbedByEntity(boolean adsorbed) {
        this.attractData.putBoolean(ADSORBED_BY_ENTITY, adsorbed);
        if (adsorbed) {
            this.setAdsorbedByBlock(false);
        } else {
            this.setAdsorptionEntityId(EMPTY_UUID, true);
        }
    }

    @Override
    public boolean isAdsorbedByBlock() {
        return getBoolean(this.shuttleData, ADSORBED_BY_BLOCK, false) && !this.isAdsorbedByEntity();
    }

    @Override
    public void setAdsorbedByBlock(boolean adsorbed) {
        this.attractData.putBoolean(ADSORBED_BY_BLOCK, adsorbed);
        if (adsorbed) {
            this.setAdsorbedByEntity(false);
        } else {
            this.setAdsorptionBlockPos(BlockPos.ORIGIN, true);
        }
    }

    @Override
    public UUID getAdsorptionEntityId() {
        return getUuid(this.attractData, ADSORPTION_ENTITY_ID);
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

    private static boolean getBoolean(NbtCompound data, String key, boolean defaultValue) {
        if (!data.contains(key)) {
            data.putBoolean(key, defaultValue);
        }
        return data.getBoolean(key);
    }

    private static int getInt(NbtCompound data, String key) {
        if (!data.contains(key)) {
            data.putInt(key, 0);
        }
        return data.getInt(key);
    }

    private static UUID getUuid(NbtCompound data, String key) {
        if (!data.contains(key)) {
            data.putUuid(key, EMPTY_UUID);
        }
        return data.getUuid(key);
    }

    @Inject(method = "writeNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", shift = At.Shift.AFTER))
    public void writePocketsDataToNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        writeAttractDataToNbt(nbt);
        writeShuttleDataToNbt(nbt);
    }

    private void writeShuttleDataToNbt(NbtCompound nbt) {
        NbtCompound data = this.shuttleData;
        data.putBoolean(SHUTTLING, this.isShuttling());
        data.put(ROUTE, asNbtList(this.getRoute()));
        data.putInt(TICK, this.getCurrentRouteTick());
        data.putInt(COOLDOWN, this.getShuttleCooldown());
        data.putBoolean(INVISIBLE, this.wasInvisible());
        data.putBoolean(INVULNERABLE, this.wasInvulnerable());
        data.putBoolean(NO_CLIP, this.wasNoClip());
        data.putBoolean(NO_GRAVITY, this.wasNoGravity());
        data.putInt(LAST_GAME_MODE, this.getLastGameMode());
        data.putBoolean(FLYING, this.wasFlying());
        nbt.put(SHUTTLE_DATA, data);
    }

    private void writeAttractDataToNbt(NbtCompound nbt) {
        NbtCompound data = this.attractData;
        data.putDouble(ATTRACT_DIS, this.getAttractDis());
        data.putBoolean(ATTRACTING, this.isAttracting() && this.canAttract());
        data.putBoolean(ENABLE, this.getEnable());
        data.putUuid(ATTRACT_OWNER, this.getAttractOwner());
        data.putBoolean(FOLLOWING, this.isFollowing());
        data.putBoolean(IGNORE_FALL_DAMAGE, this.ignoreFallDamage());
        data.putBoolean(MAGNETIC_LEVITATION_MODE, this.getMagneticLevitationMode());
        data.putInt(LEVITATION_TICK, this.getLevitationTick());
        data.putBoolean(AUTOMATIC_LEVITATION, this.getAutomaticLevitation());
        data.putBoolean(ADSORBED_BY_ENTITY, this.isAdsorbedByEntity());
        data.putBoolean(ADSORBED_BY_BLOCK, this.isAdsorbedByBlock());
        data.putUuid(ADSORPTION_ENTITY_ID, this.getAdsorptionEntityId());
        BlockPos pos = this.getAdsorptionBlockPos();
        data.putIntArray(ADSORPTION_BLOCK_POS, new int[]{pos.getX(), pos.getY(), pos.getZ()});
        nbt.put(ATTRACT_DATA, data);
    }

    @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V", shift = At.Shift.AFTER))
    public void readPocketsDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        readAttractDataFromNbt(nbt);
        readShuttleDataFromNbt(nbt);
    }

    private void readShuttleDataFromNbt(NbtCompound nbt) {
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
            if (data.contains(LAST_GAME_MODE)) {
                this.lastGameMode = data.getInt(LAST_GAME_MODE);
            }
            if (data.contains(FLYING)) {
                this.wasFlying = data.getBoolean(FLYING);
            }
        }
    }

    private void readAttractDataFromNbt(NbtCompound nbt) {
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
                int[] ints = data.getIntArray(ADSORPTION_BLOCK_POS);
                this.adsorptionBlockPos = new BlockPos(ints[0], ints[1], ints[2]);
            }
        }
    }

}