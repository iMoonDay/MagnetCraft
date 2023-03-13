package com.imoonday.magnetcraft.common.entities;

import com.imoonday.magnetcraft.common.blocks.LodestoneBlock;
import com.imoonday.magnetcraft.registries.common.EntityRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import com.imoonday.magnetcraft.screen.handler.MagneticIronGolemScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MagneticIronGolemEntity extends IronGolemEntity {

    private Inventory inventory = new SimpleInventory(27);
    private boolean hasLodestone = true;

    public MagneticIronGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createIronGolemAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 15.0);
    }

    @Override
    public boolean isAttracting() {
        return hasLodestone;
    }

    @Override
    public double getAttractDis() {
        return hasLodestone ? 15 : 0;
    }

    public void setHasLodestone(boolean hasLodestone) {
        this.hasLodestone = hasLodestone;
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        for (int i = 0; i < this.inventory.size(); i++) {
            dropStack(this.inventory.getStack(i));
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound newNbt = super.writeNbt(nbt);
        NbtList inventory = new NbtList();
        for (int i = 0; i < this.inventory.size(); i++) {
            inventory.add(this.inventory.getStack(i).writeNbt(new NbtCompound()));
        }
        newNbt.put("Inventory", inventory);
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Inventory")) {
            Inventory inventory = new SimpleInventory(27);
            nbt.getList("Inventory", NbtElement.COMPOUND_TYPE).forEach(nbtElement -> {
                NbtCompound nbtCompound = (NbtCompound) nbtElement;
                for (int i = 0; i < inventory.size(); i++) {
                    inventory.setStack(i, ItemStack.fromNbt(nbtCompound));
                }
            });
            this.inventory = inventory;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getHealth() > 0) {
            world.getEntitiesByClass(ItemEntity.class, this.getBoundingBox(), itemEntity -> true).forEach(this::insertItem);
        }
    }

    private void insertItem(ItemEntity entity) {
        ItemStack stack = entity.getStack();
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
            inventory.setStack(slot, stack);
            entity.kill();
        } else if (hasSameStack) {
            if (overflow) {
                inventory.setStack(slot, stack.copy().copyWithCount(stack.getMaxCount()));
                stack.setCount(overflowCount);
            } else {
                stack.increment(inventory.getStack(slot).getCount());
                inventory.setStack(slot, stack);
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
            this.hasLodestone = true;
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0f, g);
            if (!player.getAbilities().creativeMode) {
                player.getStackInHand(hand).decrement(1);
            }
            return ActionResult.success(this.world.isClient);
        } else if (player.world != null && !player.world.isClient && this.hasLodestone) {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, player1) -> new MagneticIronGolemScreenHandler(syncId, inv, this.inventory), MagneticIronGolemEntity.this.getDisplayName()));
        }
        return ActionResult.PASS;
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

}
