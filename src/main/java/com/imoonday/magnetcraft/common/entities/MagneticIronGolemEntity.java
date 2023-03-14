package com.imoonday.magnetcraft.common.entities;

import com.imoonday.magnetcraft.api.EntityAttractNbt;
import com.imoonday.magnetcraft.common.blocks.LodestoneBlock;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.EntityRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import com.imoonday.magnetcraft.screen.handler.MagneticIronGolemScreenHandler;
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
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.stream.IntStream;

@SuppressWarnings("RedundantCast")
public class MagneticIronGolemEntity extends IronGolemEntity {

    protected static final TrackedData<Byte> MAGNETIC_IRON_GOLEM_FLAGS = DataTracker.registerData(MagneticIronGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
    private final Inventory inventory = new SimpleInventory(27);
    private boolean hasLodestone = false;

    public MagneticIronGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createIronGolemAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 15.0);
    }

    @Override
    public boolean isAttracting() {
        return this.hasLodestone && ((EntityAttractNbt) this).canAttract();
    }

    @Override
    public double getAttractDis() {
        return this.hasLodestone && ((EntityAttractNbt) this).canAttract() ? ModConfig.getGolemValue().attractDis : 0;
    }

    public void setHasLodestone(boolean hasLodestone) {
        this.hasLodestone = hasLodestone;
    }

    public boolean isHasLodestone() {
        return this.hasLodestone;
    }

    public MagneticIronGolemEntity withLodestone() {
        this.hasLodestone = true;
        return this;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        IntStream.range(0, this.inventory.size()).mapToObj(this.inventory::getStack).forEach(this::dropStack);
        if (this.hasLodestone) {
            Random random = this.random;
            int percent = ModConfig.getGolemValue().lodestoneDropProbability;
            if (random.nextBetween(1, 100) <= percent) {
                dropStack(new ItemStack(BlockRegistries.LODESTONE_BLOCK_ITEM));
            }
        }
    }

    @Override
    public Text getName() {
        return this.hasLodestone ? Text.translatable("entity.magnetcraft.magnetic_iron_golem.with_lodestone") : super.getName();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        NbtList inventory = new NbtList();
        IntStream.range(0, this.inventory.size()).mapToObj(i -> this.inventory.getStack(i).writeNbt(new NbtCompound())).forEach(inventory::add);
        nbt.put("Inventory", inventory);
        nbt.putBoolean("HasLodestone", this.hasLodestone);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Inventory")) {
            IntStream.range(0, nbt.getList("Inventory", NbtElement.COMPOUND_TYPE).size()).forEach(i -> this.inventory.setStack(i, ItemStack.fromNbt((NbtCompound) nbt.getList("Inventory", NbtElement.COMPOUND_TYPE).get(i))));
        }
        if (nbt.contains("HasLodestone")) {
            this.hasLodestone = nbt.getBoolean("HasLodestone");
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getHealth() > 0 && this.isAttracting()) {
            this.world.getEntitiesByClass(ItemEntity.class, this.getBoundingBox(), itemEntity -> true).forEach(this::insertItem);
        }
    }

    private void insertItem(ItemEntity entity) {
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
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack inventoryStack = inventory.getStack(i);
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
            this.inventory.setStack(slot, stack);
            entity.kill();
        } else if (hasSameStack) {
            if (overflow) {
                this.inventory.setStack(slot, stack.copy().copyWithCount(stack.getMaxCount()));
                stack.setCount(overflowCount);
            } else {
                stack.increment(this.inventory.getStack(slot).getCount());
                this.inventory.setStack(slot, stack);
                entity.kill();
            }
        }
        this.inventory.markDirty();
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (hand != Hand.MAIN_HAND) {
            return ActionResult.PASS;
        }
        Block block = Block.getBlockFromItem(player.getStackInHand(hand).getItem());
        ItemStack itemStack = player.getStackInHand(hand);
        float g = 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f;
        if (itemStack.isOf(ItemRegistries.CREATURE_MAGNET_ITEM)) {
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
        } else if (block instanceof LodestoneBlock && !this.hasLodestone) {
            this.setHasLodestone(true);
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0f, g);
            if (!player.getAbilities().creativeMode) {
                player.getStackInHand(hand).decrement(1);
            }
            return ActionResult.success(this.world.isClient);
        } else if (player.world != null && !player.world.isClient && this.hasLodestone) {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, player1) -> new MagneticIronGolemScreenHandler(syncId, inv, this.inventory), MagneticIronGolemEntity.this.getDisplayName()));
        }
        this.inventory.markDirty();
        return ActionResult.success(this.world.isClient && this.hasLodestone);
    }

    @Override
    public boolean canSpawn(WorldView world) {
        BlockPos blockPos = this.getBlockPos();
        BlockPos blockPos2 = blockPos.down();
        BlockState blockState = world.getBlockState(blockPos2);
        if (blockState.hasSolidTopSurface(world, blockPos2, this)) {
            for (int i = 1; i < 3; ++i) {
                BlockState blockState2;
                BlockPos blockPos3 = blockPos.up(i);
                if (SpawnHelper.isClearForSpawn(world, blockPos3, blockState2 = world.getBlockState(blockPos3), blockState2.getFluidState(), EntityRegistries.MAGNETIC_IRON_GOLEM))
                    continue;
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

}
///summon magnetcraft:magnetic_iron_golem 28.16 -60.00 -48.16 {Brain: {memories: {}}, HurtByTimestamp: 0, Attributes: [{Base: 16.0d, Modifiers: [{Amount: -0.09630816611952596d, Operation: 1, UUID: [I; -30930734, -1459927240, -1906151979, -933374918], Name: "Random spawn bonus"}], Name: "minecraft:generic.follow_range"}, {Base: 0.25d, Name: "minecraft:generic.movement_speed"}], Invulnerable: 0b, FallFlying: 0b, PortalCooldown: 0, AbsorptionAmount: 0.0f, FallDistance: 0.0f, DeathTime: 0s, HandDropChances: [0.085f, 0.085f], PersistenceRequired: 0b, AngerTime: 0, Motion: [0.0d, -0.0784000015258789d, 0.0d], HasLodestone: 0b, Health: 100.0f, PlayerCreated: 0b, LeftHanded: 0b, Air: 300s, OnGround: 1b, Rotation: [45.0f, 0.0f], HandItems: [{}, {}], ArmorDropChances: [0.085f, 0.085f, 0.085f, 0.085f], Fire: -1s, ArmorItems: [{}, {}, {}, {}], CanPickUpLoot: 0b, AttractData: {isAttracting: 0b, AttractOwner: [I; 0, 0, 0, 0], AttractDis: 0.0d, Enable: 1b}, HurtTime: 0s, Inventory: [{id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}]}
///summon magnetcraft:magnetic_iron_golem 28.16 -60.00 -48.16 {Brain: {memories: {}}, HurtByTimestamp: 0, Attributes: [{Base: 16.0d, Modifiers: [{Amount: -0.09630816611952596d, Operation: 1, UUID: [I; -30930734, -1459927240, -1906151979, -933374918], Name: "Random spawn bonus"}], Name: "minecraft:generic.follow_range"}, {Base: 0.25d, Name: "minecraft:generic.movement_speed"}], Invulnerable: 0b, FallFlying: 0b, PortalCooldown: 0, AbsorptionAmount: 0.0f, FallDistance: 0.0f, DeathTime: 0s, HandDropChances: [0.085f, 0.085f], PersistenceRequired: 0b, AngerTime: 0, Motion: [0.0d, -0.0784000015258789d, 0.0d], HasLodestone: 0b, Health: 100.0f, PlayerCreated: 0b, LeftHanded: 0b, Air: 300s, OnGround: 1b, Rotation: [45.0f, 0.0f], HandItems: [{}, {}], ArmorDropChances: [0.085f, 0.085f, 0.085f, 0.085f], Fire: -1s, ArmorItems: [{}, {}, {}, {}], CanPickUpLoot: 0b, AttractData: {isAttracting: 0b, AttractOwner: [I; 0, 0, 0, 0], AttractDis: 0.0d, Enable: 1b}, HurtTime: 0s, Inventory: [{id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}]}

///summon magnetcraft:magnetic_iron_golem 25.16 -60.00 -47.84 {Brain: {memories: {}}, HurtByTimestamp: 0, Attributes: [{Base: 16.0d, Modifiers: [{Amount: 0.009176273725878611d, Operation: 1, UUID: [I; -2074426243, -960937013, -1277063034, -1422711650], Name: "Random spawn bonus"}], Name: "minecraft:generic.follow_range"}, {Base: 0.25d, Name: "minecraft:generic.movement_speed"}], Invulnerable: 0b, FallFlying: 0b, PortalCooldown: 0, AbsorptionAmount: 0.0f, FallDistance: 0.0f, DeathTime: 0s, HandDropChances: [0.085f, 0.085f], PersistenceRequired: 0b, AngerTime: 0, Motion: [0.0d, -0.0784000015258789d, 0.0d], HasLodestone: 1b, Health: 100.0f, PlayerCreated: 0b, LeftHanded: 0b, Air: 300s, OnGround: 1b, Rotation: [135.0f, 0.31677803f], HandItems: [{}, {}], ArmorDropChances: [0.085f, 0.085f, 0.085f, 0.085f], Fire: -1s, ArmorItems: [{}, {}, {}, {}], CanPickUpLoot: 0b, AttractData: {isAttracting: 1b, AttractOwner: [I; 0, 0, 0, 0], AttractDis: 15.0d, Enable: 1b}, HurtTime: 0s, Inventory: [{id: "magnetcraft:magnet_fragment", Count: 17b}, {id: "magnetcraft:magnetic_iron_ingot", Count: 15b}, {id: "magnetcraft:magnetic_iron_golem_spawn_egg", Count: 1b}, {id: "minecraft:oak_fence", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}]}
///summon magnetcraft:magnetic_iron_golem 25.16 -60.00 -47.84 {Brain: {memories: {}}, HurtByTimestamp: 0, Attributes: [{Base: 16.0d, Modifiers: [{Amount: 0.009176273725878611d, Operation: 1, UUID: [I; -2074426243, -960937013, -1277063034, -1422711650], Name: "Random spawn bonus"}], Name: "minecraft:generic.follow_range"}, {Base: 0.25d, Name: "minecraft:generic.movement_speed"}], Invulnerable: 0b, FallFlying: 0b, PortalCooldown: 0, AbsorptionAmount: 0.0f, FallDistance: 0.0f, DeathTime: 0s, HandDropChances: [0.085f, 0.085f], PersistenceRequired: 0b, AngerTime: 0, Motion: [0.0d, -0.0784000015258789d, 0.0d], HasLodestone: 1b, Health: 100.0f, PlayerCreated: 0b, LeftHanded: 0b, Air: 300s, OnGround: 1b, Rotation: [135.0f, 17.163456f], HandItems: [{}, {}], ArmorDropChances: [0.085f, 0.085f, 0.085f, 0.085f], Fire: -1s, ArmorItems: [{}, {}, {}, {}], CanPickUpLoot: 0b, AttractData: {isAttracting: 1b, AttractOwner: [I; 0, 0, 0, 0], AttractDis: 15.0d, Enable: 1b}, HurtTime: 0s, Inventory: [{id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}, {id: "minecraft:air", Count: 1b}]}