package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.ImplementedInventory;
import com.imoonday.magnetcraft.common.blocks.LodestoneBlock;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.AttractMethods;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.screen.handler.LodestoneScreenHandler;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class LodestoneEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {

    private boolean redstone;
    private double dis2;
    private int direction;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(18, ItemStack.EMPTY);

    public LodestoneEntity(BlockPos pos, BlockState state) {
        super(BlockRegistries.LODESTONE_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        NbtCompound nbt = Objects.requireNonNull(world.getBlockEntity(pos)).createNbt();
        double dis = nbt.getDouble("dis") <= ModConfig.getConfig().value.lodestoneMaxDis ? nbt.getDouble("dis") + 1 : ModConfig.getConfig().value.lodestoneMaxDis + 1;
        boolean enable = nbt.getBoolean("enable");
        int direction1 = nbt.getInt("direction");
        Vec3d centerPos = pos.toCenterPos();
        centerPos = centerPos.add(0, 0.5, 0);
        if (enable) {
            centerPos = switch (direction1) {
                case 1 -> centerPos.add(0, 0, 1);
                case 2 -> centerPos.add(-1, 0, 0);
                case 3 -> centerPos.add(0, 0, -1);
                case 4 -> centerPos.add(1, 0, 0);
                case 5 -> centerPos.add(0, 1, 0);
                case 6 -> centerPos.add(0, -1, 0);
                default -> centerPos;
            };
            AttractMethods.attractItems(world, centerPos, dis);
            putItemEntityIn(world, pos);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        boolean hasPowered = world != null && world.isReceivingRedstonePower(pos);
        int disPerPower = ModConfig.getConfig().value.disPerPower;
        double dis = world.getReceivedRedstonePower(pos) * disPerPower;
        nbt.putBoolean("redstone", redstone);
        if (redstone) {
            nbt.putBoolean("enable", hasPowered);
            nbt.putDouble("dis", dis);
        } else {
            nbt.putBoolean("enable", dis2 > 1);
            nbt.putDouble("dis", dis2);
        }
        nbt.putInt("direction", direction);
        Inventories.writeNbt(nbt, this.inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("redstone")) {
            redstone = nbt.getBoolean("redstone");
        }
        if (nbt.contains("dis")) {
            dis2 = nbt.getDouble("dis");
        }
        if (nbt.contains("direction")) {
            direction = nbt.getInt("direction");
        }
        Inventories.readNbt(nbt, this.inventory);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        if (world != null) {
            return LodestoneBlock.showState(world, pos, null);
        } else {
            return Text.translatable(getCachedState().getBlock().getTranslationKey());
        }
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new LodestoneScreenHandler(syncId, inv, this);
    }

    public static void putItemEntityIn(World world, BlockPos blockPos) {
        world.getOtherEntities(null, Box.from(new BlockBox(blockPos.up())), entity -> entity instanceof ItemEntity).forEach(e -> {
            Inventory inventory = (Inventory) world.getBlockEntity(blockPos);
            ItemStack stack = ((ItemEntity) e).getStack();
            boolean hasEmptySlot = false;
            boolean hasSameStack = false;
            boolean countOverflow = false;
            int overflowCount = 0;
            int slot = -1;
            if (inventory != null) {
                for (int i = 0; i < inventory.size(); i++) {
                    ItemStack inventoryStack = inventory.getStack(i);
                    hasEmptySlot = inventoryStack.isEmpty();
                    hasSameStack = inventoryStack.isItemEqual(stack) && inventoryStack.getCount() < inventoryStack.getMaxCount();
                    countOverflow = inventoryStack.getCount() + stack.getCount() > stack.getMaxCount();
                    if (countOverflow) {
                        overflowCount = inventoryStack.getCount() + stack.getCount() - stack.getMaxCount();
                    }
                    if (hasEmptySlot || hasSameStack) {
                        slot = i;
                        break;
                    }
                }
            }
            if (hasEmptySlot) {
                inventory.setStack(slot, stack);
                e.kill();
            } else if (hasSameStack) {
                if (countOverflow) {
                    inventory.setStack(slot, stack.copy().copyWithCount(stack.getMaxCount()));
                    stack.setCount(overflowCount);
                } else {
                    stack.increment(inventory.getStack(slot).getCount());
                    inventory.setStack(slot, stack);
                    e.kill();
                }
            }
        });
    }
}
