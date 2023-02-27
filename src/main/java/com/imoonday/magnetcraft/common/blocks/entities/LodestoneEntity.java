package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.api.ImplementedInventory;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class LodestoneEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {

    private boolean redstone;
    private double dis;
    private int direction;
    private boolean enable;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(18, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return redstone ? 1 : 0;
        }

        @Override
        public void set(int index, int value) {

        }

        @Override
        public int size() {
            return 1;
        }
    };

    public LodestoneEntity(BlockPos pos, BlockState state) {
        super(BlockRegistries.LODESTONE_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, LodestoneEntity entity) {
        int maxDis = ModConfig.getConfig().value.lodestoneMaxDis;
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
            AttractMethods.attractItems(world, centerPos, dis);
            if (direction != null) {
                putItemEntityIn(world, pos, entity, direction);
            } else {
                for (Direction direction2 : new Direction[]{Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST, Direction.UP, Direction.DOWN}) {
                    putItemEntityIn(world, pos, entity, direction2);
                }
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        int disPerPower = ModConfig.getConfig().value.disPerPower;nbt.putBoolean("redstone", redstone);
        nbt.putDouble("dis", redstone && world != null ? world.getReceivedRedstonePower(pos) * disPerPower : this.dis);
        nbt.putBoolean("enable", enable);
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
            dis = nbt.getDouble("dis");
        }
        if (nbt.contains("direction")) {
            direction = nbt.getInt("direction");
        }
        enable = redstone ? world != null && world.isReceivingRedstonePower(pos) : dis > 1;
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
        return new LodestoneScreenHandler(syncId, inv, this, propertyDelegate);
    }

    public static void putItemEntityIn(World world, BlockPos blockPos, LodestoneEntity blockEntity, Direction direction) {
        world.getOtherEntities(null, Box.from(new BlockBox(blockPos.offset(direction))), entity -> entity instanceof ItemEntity).forEach(e -> {
            DefaultedList<ItemStack> inventory = blockEntity.inventory;
            ItemStack stack = ((ItemEntity) e).getStack();
            boolean hasEmptySlot = false;
            boolean hasSameStack = false;
            boolean countOverflow = false;
            int overflowCount = 0;
            int slot = -1;
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack inventoryStack = inventory.get(i);
                hasEmptySlot = inventoryStack.isEmpty();
                hasSameStack = inventoryStack.isItemEqual(stack) && inventoryStack.getCount() < inventoryStack.getMaxCount();
                countOverflow = inventoryStack.getCount() + stack.getCount() > stack.getMaxCount();
                if (hasEmptySlot || hasSameStack) {
                    slot = i;
                    if (countOverflow) {
                        overflowCount = inventoryStack.getCount() + stack.getCount() - stack.getMaxCount();
                    }
                    break;
                }
            }
            if (hasEmptySlot) {
                inventory.set(slot, stack);
                e.kill();
            } else if (hasSameStack) {
                if (countOverflow) {
                    inventory.set(slot, stack.copy().copyWithCount(stack.getMaxCount()));
                    stack.setCount(overflowCount);
                } else {
                    stack.increment(inventory.get(slot).getCount());
                    inventory.set(slot, stack);
                    e.kill();
                }
            }
        });
    }
}
