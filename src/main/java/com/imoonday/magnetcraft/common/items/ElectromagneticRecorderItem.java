package com.imoonday.magnetcraft.common.items;

import com.imoonday.magnetcraft.common.blocks.entities.ElectromagneticShuttleBaseEntity;
import com.imoonday.magnetcraft.common.entities.entrance.ShuttleEntranceEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;

public class ElectromagneticRecorderItem extends Item {

    public ElectromagneticRecorderItem(Settings settings) {
        super(settings);
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
        if (!stack.getOrCreateNbt().contains("Recording")) {
            return TypedActionResult.pass(stack);
        }
        if (user.isSneaking()) {
            finishRecording(stack, world, user);
            if (!world.isClient) {
                user.sendMessage(Text.literal("已停止记录"));
            }
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) {
            return;
        }
        if (!stack.getOrCreateNbt().contains("Recording")) {
            stack.getOrCreateNbt().putBoolean("Recording", false);
        }
        if (!stack.getOrCreateNbt().contains("Pos")) {
            stack.getOrCreateNbt().put("Pos", new NbtList());
        }
        NbtList list = stack.getOrCreateNbt().getList("Pos", NbtElement.COMPOUND_TYPE);
        stack.setDamage(list.size());
        if (stack.getOrCreateNbt().getBoolean("Recording")) {
            if (!selected) {
                initializeNbt(stack);
                if (entity instanceof PlayerEntity player) {
                    player.sendMessage(Text.literal("请手持记录仪以进行记录"), true);
                    player.getInventory().markDirty();
                }
                return;
            }
            if (stack.isBroken()) {
                finishRecording(stack, world, entity);
                entity.sendMessage(Text.literal("记录结束"));
                return;
            }
            boolean recorded = false;
            for (NbtElement element : list) {
                NbtCompound compound = (NbtCompound) element;
                double x = compound.getDouble("x");
                double y = compound.getDouble("y");
                double z = compound.getDouble("z");
                Vec3d pos = new Vec3d(x, y, z);
                if (pos.equals(entity.getPos())) {
                    recorded = true;
                    break;
                }
            }
            if (!recorded) {
                NbtCompound compound = new NbtCompound();
                compound.putDouble("x", entity.getX());
                compound.putDouble("y", entity.getY());
                compound.putDouble("z", entity.getZ());
                list.add(compound);
                stack.getOrCreateNbt().put("Pos", list);
                stack.addDamage(null, 1, false);
            }
            if (entity instanceof PlayerEntity player) {
                player.getInventory().markDirty();
            }
        }
    }

    private static void finishRecording(ItemStack stack, World world, Entity entity) {
        stack.getOrCreateNbt().putBoolean("Recording", false);
        int[] sourcePos = stack.getOrCreateNbt().getIntArray("SourcePos");
        stack.getOrCreateNbt().remove("SourcePos");
        BlockPos pos = new BlockPos(sourcePos[0], sourcePos[1], sourcePos[2]);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ElectromagneticShuttleBaseEntity base) {
            base.setConnecting(true);
            ArrayList<Vec3d> route = new ArrayList<>();
            NbtList list = stack.getOrCreateNbt().getList("Pos", NbtElement.COMPOUND_TYPE);
            for (NbtElement element : list) {
                NbtCompound compound = (NbtCompound) element;
                double x = compound.getDouble("x");
                double y = compound.getDouble("y");
                double z = compound.getDouble("z");
                route.add(new Vec3d(x, y, z));
            }
            Collections.reverse(route);
            base.setRoute(route);
        }
        ShuttleEntranceEntity sourceEntity = new ShuttleEntranceEntity(world, true, pos);
        ShuttleEntranceEntity connetedEntity = new ShuttleEntranceEntity(world, false, pos);
        sourceEntity.setConnectedEntity(connetedEntity);
        connetedEntity.setConnectedEntity(sourceEntity);
        sourceEntity.refreshPositionAfterTeleport(pos.toCenterPos().add(0, 0.25, 0));
        connetedEntity.refreshPositionAfterTeleport(entity.getPos());
        world.spawnEntity(sourceEntity);
        world.spawnEntity(connetedEntity);
        stack.getOrCreateNbt().put("Pos", new NbtList());
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    private static void initializeNbt(ItemStack stack) {
        stack.getOrCreateNbt().putBoolean("Recording", false);
        stack.getOrCreateNbt().put("Pos", new NbtList());
    }

}
