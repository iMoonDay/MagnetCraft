package com.imoonday.magnetcraft.common.entities.golem;

import com.imoonday.magnetcraft.api.ImplementedInventory;
import com.imoonday.magnetcraft.common.blocks.LodestoneBlock;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.EntityRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("AlibabaUndefineMagicConstant")
public class MagneticIronGolemEntity extends IronGolemEntity implements ImplementedInventory {

    protected static final TrackedData<Byte> MAGNETIC_IRON_GOLEM_FLAGS = DataTracker.registerData(MagneticIronGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> HAS_LODESTONE = DataTracker.registerData(MagneticIronGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final String HAS_LODESTONE_TAG = "HasLodestone";
    public static final String ITEMS = "Items";
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);

    public MagneticIronGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createIronGolemAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 15.0);
    }

    @Override
    public boolean isAttracting() {
        return (this.isHasLodestone() || this.hasStatusEffect(EffectRegistries.ATTRACT_EFFECT)) && this.canAttract();
    }

    @Override
    public double getAttractDis() {
        return this.isHasLodestone() && this.canAttract() ? ModConfig.getGolemValue().attractDis : this.hasStatusEffect(EffectRegistries.ATTRACT_EFFECT) ? ModConfig.getConfig().value.attractDefaultDis + Objects.requireNonNull(this.getStatusEffect(EffectRegistries.ATTRACT_EFFECT)).getAmplifier() * ModConfig.getConfig().value.disPerAmplifier : 0;
    }

    public void setHasLodestone(boolean hasLodestone) {
        this.dataTracker.set(HAS_LODESTONE, hasLodestone);
    }

    public boolean isHasLodestone() {
        return this.dataTracker.get(HAS_LODESTONE);
    }

    public MagneticIronGolemEntity withLodestone() {
        this.setHasLodestone(true);
        return this;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        ItemScatterer.spawn(world, this, this);
        if (this.isHasLodestone()) {
            int percent = ModConfig.getGolemValue().lodestoneDropProbability;
            if (this.random.nextBetween(1, 100) <= percent) {
                dropStack(new ItemStack(BlockRegistries.LODESTONE_BLOCK));
            }
        }
    }

    @Override
    public Text getName() {
        return this.isHasLodestone() ? Text.translatable("entity.magnetcraft.magnetic_iron_golem.with_lodestone") : super.getName();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putBoolean(HAS_LODESTONE_TAG, this.isHasLodestone());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains(ITEMS)) {
            Inventories.readNbt(nbt, this.inventory);
        }
        if (nbt.contains(HAS_LODESTONE_TAG)) {
            this.setHasLodestone(nbt.getBoolean(HAS_LODESTONE_TAG));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getHealth() > 0 && this.isAttracting() && this.isHasLodestone()) {
            this.world.getEntitiesByClass(ItemEntity.class, this.getBoundingBox(), itemEntity -> true).forEach(this::insertItem);
        }
    }

    private void insertItem(@NotNull ItemEntity entity) {
        ItemStack stack = entity.getStack();
        if (stack.isOf(ItemRegistries.MAGNETIC_IRON_INGOT)) {
            while (this.getHealth() < this.getMaxHealth()) {
                this.heal(25.0f);
                float g = 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f;
                this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0f, g);
                stack.decrement(1);
            }
        }
        boolean hasEmptySlot = false;
        boolean hasSameStack = false;
        boolean overflow = false;
        int overflowCount = 0;
        int slot = -1;
        for (int i = 0; i < this.size(); i++) {
            ItemStack inventoryStack = this.getStack(i);
            hasEmptySlot = inventoryStack.isEmpty();
            hasSameStack = ItemStack.canCombine(stack, inventoryStack) && inventoryStack.getCount() < inventoryStack.getMaxCount();
            overflow = inventoryStack.getCount() + stack.getCount() > stack.getMaxCount();
            if (hasEmptySlot || hasSameStack) {
                slot = i;
                if (overflow) {
                    overflowCount = inventoryStack.getCount() + stack.getCount() - stack.getMaxCount();
                }
                break;
            }
        }
        if (hasEmptySlot) {
            this.setStack(slot, stack);
            entity.kill();
        } else if (hasSameStack) {
            if (overflow) {
                this.setStack(slot, stack.copy().copyWithCount(stack.getMaxCount()));
                stack.setCount(overflowCount);
            } else {
                stack.increment(this.getStack(slot).getCount());
                this.setStack(slot, stack);
                entity.kill();
            }
        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (hand != Hand.MAIN_HAND) {
            return ActionResult.PASS;
        }
        Block block = Block.getBlockFromItem(player.getStackInHand(hand).getItem());
        ItemStack itemStack = player.getStackInHand(hand);
        float g = 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f;
        if (itemStack.isOf(ItemRegistries.CREATURE_MAGNET_ITEM) || itemStack.isOf(ItemRegistries.ADSORPTION_MAGNET_ITEM)) {
            return ActionResult.PASS;
        } else if (itemStack.isOf(ItemRegistries.MAGNETIC_IRON_INGOT)) {
            float f = this.getHealth();
            this.heal(25.0f);
            if (this.getHealth() != f) {
                this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0f, g);
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
            } else {
                return ActionResult.PASS;
            }
        } else if (block instanceof LodestoneBlock && !this.dataTracker.get(HAS_LODESTONE)) {
            this.setHasLodestone(true);
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0f, g);
            if (!player.getAbilities().creativeMode) {
                player.getStackInHand(hand).decrement(1);
            }
            return ActionResult.success(this.world.isClient);
        } else if (player.world != null && !player.world.isClient && this.dataTracker.get(HAS_LODESTONE)) {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, player1) -> GenericContainerScreenHandler.createGeneric9x3(syncId, inv, this), MagneticIronGolemEntity.this.getDisplayName()));
        }
        return this.dataTracker.get(HAS_LODESTONE) ? ActionResult.SUCCESS : ActionResult.PASS;
    }

    @Override
    public boolean canSpawn(@NotNull WorldView world) {
        BlockPos blockPos = this.getBlockPos();
        BlockPos blockPos2 = blockPos.down();
        BlockState blockState = world.getBlockState(blockPos2);
        if (blockState.hasSolidTopSurface(world, blockPos2, this)) {
            for (int i = 1; i < 3; ++i) {
                BlockState blockState2;
                BlockPos blockPos3 = blockPos.up(i);
                if (SpawnHelper.isClearForSpawn(world, blockPos3, blockState2 = world.getBlockState(blockPos3), blockState2.getFluidState(), EntityRegistries.MAGNETIC_IRON_GOLEM)) {
                    continue;
                }
                return false;
            }
            return SpawnHelper.isClearForSpawn(world, blockPos, world.getBlockState(blockPos), Fluids.EMPTY.getDefaultState(), EntityRegistries.MAGNETIC_IRON_GOLEM) && world.doesNotIntersectEntities(this);
        }
        return false;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(MAGNETIC_IRON_GOLEM_FLAGS, (byte) 0);
        this.dataTracker.startTracking(HAS_LODESTONE, false);
    }

    @Override
    public boolean isPlayerCreated() {
        return (this.dataTracker.get(MAGNETIC_IRON_GOLEM_FLAGS) & 1) != 0;
    }

    @Override
    public void setPlayerCreated(boolean playerCreated) {
        byte b = this.dataTracker.get(MAGNETIC_IRON_GOLEM_FLAGS);
        if (playerCreated) {
            this.dataTracker.set(MAGNETIC_IRON_GOLEM_FLAGS, (byte) (b | 1));
        } else {
            this.dataTracker.set(MAGNETIC_IRON_GOLEM_FLAGS, (byte) (b & 0xFFFFFFFE));
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }
}