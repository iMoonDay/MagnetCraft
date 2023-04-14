package com.imoonday.magnetcraft.common.items;

import com.imoonday.magnetcraft.common.blocks.entities.ElectromagneticShuttleBaseEntity;
import com.imoonday.magnetcraft.common.entities.entrance.ShuttleEntranceEntity;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ElectromagneticRecorderItem extends Item {

    public static final String RECORDING = "Recording";
    public static final String POS = "Pos";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String SOURCE_POS = "SourcePos";

    public ElectromagneticRecorderItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.ELECTROMAGNETIC_RECORDER_ITEM, new Identifier("recording"), (itemStack, clientWorld, livingEntity, provider) -> itemStack.getOrCreateNbt().getBoolean(RECORDING) ? 1.0F : 0.0F);
        ModelPredicateProviderRegistry.register(ItemRegistries.ELECTROMAGNETIC_RECORDER_ITEM, new Identifier("tick1"), (itemStack, clientWorld, livingEntity, provider) -> itemStack.getDamage() / 20 % 4 == 1 ? 1.0F : 0.0F);
        ModelPredicateProviderRegistry.register(ItemRegistries.ELECTROMAGNETIC_RECORDER_ITEM, new Identifier("tick2"), (itemStack, clientWorld, livingEntity, provider) -> itemStack.getDamage() / 20 % 4 == 2 ? 1.0F : 0.0F);
        ModelPredicateProviderRegistry.register(ItemRegistries.ELECTROMAGNETIC_RECORDER_ITEM, new Identifier("tick3"), (itemStack, clientWorld, livingEntity, provider) -> itemStack.getDamage() / 20 % 4 == 3 ? 1.0F : 0.0F);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        initializeNbt(stack);
        return stack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!stack.getOrCreateNbt().contains(RECORDING)) {
            return TypedActionResult.pass(stack);
        }
        if (stack.getOrCreateNbt().getBoolean(RECORDING)) {
            if (user.isSneaking()) {
                initializeNbt(stack);
            } else {
                if (!world.isClient) {
                    finishRecording(stack, world, user);
                }
            }
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }

    private static void spawnEntrances(World world, BlockPos sourcePos, Vec3d endPos) {
        BlockEntity blockEntity = world.getBlockEntity(sourcePos);
        if (blockEntity instanceof ElectromagneticShuttleBaseEntity base) {
            Vec3d startPos = sourcePos.toCenterPos().add(0, 0.25, 0);
            float uniqueOffset = world.random.nextFloat() + 2.0f;
            ShuttleEntranceEntity sourceEntity = new ShuttleEntranceEntity(world, startPos, true, sourcePos, endPos, uniqueOffset);
            ShuttleEntranceEntity connectedEntity = new ShuttleEntranceEntity(world, endPos, false, sourcePos, startPos, uniqueOffset);
            sourceEntity.setConnectedEntity(connectedEntity);
            connectedEntity.setConnectedEntity(sourceEntity);
            base.setSourceEntity(sourceEntity);
            base.setConnectedEntity(connectedEntity);
            world.spawnEntity(sourceEntity);
            world.spawnEntity(connectedEntity);
            base.markDirty();
            world.playSound(null, sourceEntity.getBlockPos(), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.VOICE);
            world.playSound(null, connectedEntity.getBlockPos(), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.VOICE);
        }
    }

    private static void finishRecording(ItemStack stack, World world, Entity entity) {
        stack.getOrCreateNbt().putBoolean(RECORDING, false);
        int[] sourcePos = stack.getOrCreateNbt().getIntArray(SOURCE_POS);
        stack.getOrCreateNbt().remove(SOURCE_POS);
        BlockPos pos = new BlockPos(sourcePos[0], sourcePos[1], sourcePos[2]);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ElectromagneticShuttleBaseEntity base) {
            base.setConnecting(true);
            ArrayList<Vec3d> route = new ArrayList<>();
            NbtList list = stack.getOrCreateNbt().getList(POS, NbtElement.COMPOUND_TYPE);
            for (NbtElement element : list) {
                NbtCompound compound = (NbtCompound) element;
                double x = compound.getDouble(X);
                double y = compound.getDouble(Y);
                double z = compound.getDouble(Z);
                route.add(new Vec3d(x, y, z));
            }
            base.setRoute(route);
            entity.shuttleCooldown();
            stack.getOrCreateNbt().put(POS, new NbtList());
            NbtCompound compound = (NbtCompound) list.get(Math.max(list.size() - 1, 0));
            Vec3d end = new Vec3d(compound.getDouble(X), compound.getDouble(Y), compound.getDouble(Z));
            spawnEntrances(world, pos, end);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) {
            return;
        }
        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains(RECORDING)) {
            nbt.putBoolean(RECORDING, false);
        }
        if (!nbt.contains(POS)) {
            nbt.put(POS, new NbtList());
        }
        NbtList list = nbt.getList(POS, NbtElement.COMPOUND_TYPE);
        stack.setDamage(list.size());
        if (nbt.getBoolean(RECORDING)) {
            if (!selected) {
                initializeNbt(stack);
                return;
            }
            int[] sourcePos = stack.getOrCreateNbt().getIntArray(SOURCE_POS);
            BlockPos blockPos = new BlockPos(sourcePos[0], sourcePos[1], sourcePos[2]);
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (!(blockEntity instanceof ElectromagneticShuttleBaseEntity) || stack.isBroken()) {
                finishRecording(stack, world, entity);
                return;
            }
            boolean recorded = false;
            for (NbtElement element : list) {
                NbtCompound compound = (NbtCompound) element;
                double x = compound.getDouble(X);
                double y = compound.getDouble(Y);
                double z = compound.getDouble(Z);
                Vec3d pos = new Vec3d(x, y, z);
                if (pos.equals(entity.getPos())) {
                    recorded = true;
                    break;
                }
            }
            if (!recorded) {
                NbtCompound compound = new NbtCompound();
                compound.putDouble(X, entity.getX());
                compound.putDouble(Y, entity.getY());
                compound.putDouble(Z, entity.getZ());
                list.add(compound);
                nbt.put(POS, list);
                stack.addDamage(1);
            }
            if (entity instanceof PlayerEntity player) {
                player.getInventory().markDirty();
            }
        } else {
            initializeNbt(stack);
        }
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    public static void initializeNbt(ItemStack stack) {
        stack.getOrCreateNbt().putBoolean(RECORDING, false);
        stack.getOrCreateNbt().put(POS, new NbtList());
        stack.getOrCreateNbt().remove(SOURCE_POS);
        stack.setDamage(0);
    }

}
