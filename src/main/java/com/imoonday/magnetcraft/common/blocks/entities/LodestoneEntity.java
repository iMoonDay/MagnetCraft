package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.api.ImplementedInventory;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.screen.handler.LodestoneScreenHandler;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author iMoonDay
 */
public class LodestoneEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {

    public static final String REDSTONE = "redstone";
    public static final String DIS = "dis";
    public static final String DIRECTION = "direction";
    public static final String FILTER = "filter";
    public static final String ENABLE = "enable";
    private boolean redstone;
    private double dis;
    private int direction;
    private boolean enable;
    private boolean filter;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(18, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> redstone ? 1 : 0;
                case 1 -> (int) dis;
                case 2 -> direction;
                case 3 -> filter ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int size() {
            return 4;
        }
    };

    public LodestoneEntity(BlockPos pos, BlockState state) {
        super(BlockRegistries.LODESTONE_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, LodestoneEntity entity) {
        if (world != null) {
            int maxDis = ModConfig.getValue().lodestoneMaxDis;
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            double dis = entity.dis <= maxDis ? entity.dis + 1 : maxDis + 1;
            int getDirection = entity.direction;
            Vec3d centerPos = pos.toCenterPos().add(0, 0.5, 0);
            if (entity.enable) {
                centerPos = switch (getDirection) {
                    case 1 -> centerPos.add(0, 0, 1);
                    case 2 -> centerPos.add(-1, 0, 0);
                    case 3 -> centerPos.add(0, 0, -1);
                    case 4 -> centerPos.add(1, 0, 0);
                    case 5 -> centerPos.add(0, 1, 0);
                    case 6 -> centerPos.add(0, -1, 0);
                    default -> centerPos;
                };
                Direction direction = switch (getDirection) {
                    case 1 -> Direction.SOUTH;
                    case 2 -> Direction.WEST;
                    case 3 -> Direction.NORTH;
                    case 4 -> Direction.EAST;
                    case 5 -> Direction.UP;
                    case 6 -> Direction.DOWN;
                    default -> null;
                };
                ArrayList<Item> allowedItems = entity.inventory.stream().map(ItemStack::getItem).collect(Collectors.toCollection(ArrayList::new));
                world.attractItems(centerPos, dis, entity.filter, allowedItems);
                if (direction != null) {
                    putItemEntityIn(world, pos, entity, direction);
                } else {
                    Arrays.stream(Direction.values()).forEach(direction2 -> putItemEntityIn(world, pos, entity, direction2));
                }
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        int disPerPower = ModConfig.getValue().disPerPower;
        nbt.putBoolean(REDSTONE, redstone);
        nbt.putDouble(DIS, redstone && world != null ? world.getReceivedRedstonePower(pos) * disPerPower : dis);
        dis = nbt.getDouble(DIS);
        enable = redstone ? world != null && world.isReceivingRedstonePower(pos) : dis > 1;
        nbt.putBoolean(ENABLE, enable);
        nbt.putInt(DIRECTION, direction);
        nbt.putBoolean(FILTER, filter);
        Inventories.writeNbt(nbt, inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains(REDSTONE)) {
            redstone = nbt.getBoolean(REDSTONE);
        }
        if (nbt.contains(DIS)) {
            dis = nbt.getDouble(DIS);
        }
        if (nbt.contains(DIRECTION)) {
            direction = nbt.getInt(DIRECTION);
        }
        if (nbt.contains(FILTER)) {
            filter = nbt.getBoolean(FILTER);
        }
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
        return Text.empty();
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new LodestoneScreenHandler(syncId, inv, this, propertyDelegate, ScreenHandlerContext.create(world, pos));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public static void putItemEntityIn(World world, BlockPos blockPos, LodestoneEntity blockEntity, Direction direction) {
        world.getOtherEntities(null, Box.from(new BlockBox(blockPos.offset(direction))), entity -> entity instanceof ItemEntity).stream().map(entity -> (ItemEntity) entity).forEach(entity -> {
            DefaultedList<ItemStack> inventory = blockEntity.inventory;
            ItemStack stack = entity.getStack();
            ArrayList<Item> allowedItems = blockEntity.inventory.stream().map(ItemStack::getItem).collect(Collectors.toCollection(ArrayList::new));
            if (!blockEntity.filter || allowedItems.contains(stack.getItem())) {
                boolean hasEmptySlot = false;
                boolean hasSameStack = false;
                boolean overflow = false;
                int overflowCount = 0;
                int slot = -1;
                for (int i = 0; i < inventory.size(); i++) {
                    ItemStack inventoryStack = inventory.get(i);
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
                    inventory.set(slot, stack);
                    entity.kill();
                } else if (hasSameStack) {
                    if (overflow) {
                        inventory.set(slot, stack.copy().copyWithCount(stack.getMaxCount()));
                        stack.setCount(overflowCount);
                    } else {
                        stack.increment(inventory.get(slot).getCount());
                        inventory.set(slot, stack);
                        entity.kill();
                    }
                }
            }
        });
    }
}
