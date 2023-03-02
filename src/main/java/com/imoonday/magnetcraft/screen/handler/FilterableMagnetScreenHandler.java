package com.imoonday.magnetcraft.screen.handler;

import com.imoonday.magnetcraft.api.FilterableMagnetItem;
import com.imoonday.magnetcraft.registries.special.ScreenRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

@SuppressWarnings("ConstantValue")
public class FilterableMagnetScreenHandler extends ScreenHandler {

    private final ItemStack stack;
    private final int slot;
    private final Inventory inventory;
    private final PlayerEntity player;

    public FilterableMagnetScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, new SimpleInventory(9), buf.readItemStack(), buf.readInt());
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack getStack() {
        return stack;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public int getSlot() {
        return slot;
    }

    public FilterableMagnetScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, ItemStack stack, int slot) {
        super(ScreenRegistries.FILTERABLE_MAGNET_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        this.stack = stack;
        this.slot = slot;
        this.player = playerInventory.player;
        checkSize(inventory, 9);
        int y;
        int x;
        for (x = 0; x < 9; ++x) {
            this.addSlot(new LockedSlot(inventory, x, 8 + x * 18, 35 + 18));
        }
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new LockedSlot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (y = 0; y < 9; ++y) {
            this.addSlot(new LockedSlot(playerInventory, y, 8 + y * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (this.inventory.containsAny(stack1 -> stack1.isOf(newStack.getItem()))) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        } else {
            newStack = ItemStack.EMPTY;
        }
        return newStack;
    }

    @Override
    protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        ItemStack itemStack;
        Slot slot;
        boolean bl = false;
        int i = startIndex;
        if (fromLast) {
            i = endIndex - 1;
        }
        if (stack.isStackable()) {
            while (!stack.isEmpty() && (fromLast ? i >= startIndex : i < endIndex)) {
                if (i < this.inventory.size()) {
                    break;
                }
                slot = this.slots.get(i);
                itemStack = slot.getStack();
                if (!itemStack.isEmpty() && ItemStack.canCombine(stack, itemStack)) {
                    int j = itemStack.getCount() + stack.getCount();
                    if (j <= stack.getMaxCount()) {
                        stack.setCount(0);
                        itemStack.setCount(j);
                        slot.markDirty();
                        bl = true;
                    } else if (itemStack.getCount() < stack.getMaxCount()) {
                        stack.decrement(stack.getMaxCount() - itemStack.getCount());
                        itemStack.setCount(stack.getMaxCount());
                        slot.markDirty();
                        bl = true;
                    }
                }
                if (fromLast) {
                    --i;
                    continue;
                }
                ++i;
            }
        }
        if (!stack.isEmpty()) {
            i = fromLast ? endIndex - 1 : startIndex;
            while (fromLast ? i >= startIndex : i < endIndex) {
                slot = this.slots.get(i);
                itemStack = slot.getStack();
                if (itemStack.isEmpty() && slot.canInsert(stack)) {
                    if (stack.getCount() > slot.getMaxItemCount()) {
                        slot.setStack(stack.split(slot.getMaxItemCount()));
                    } else {
                        slot.setStack(stack.split(stack.getCount()));
                    }
                    slot.markDirty();
                    bl = true;
                    break;
                }
                if (fromLast) {
                    --i;
                    continue;
                }
                ++i;
            }
        }
        return bl;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private class LockedSlot extends Slot {

        public LockedSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return stackMovementIsAllowed(getStack());
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stackMovementIsAllowed(stack) && !hasSameItem(stack);
        }

        @Override
        public int getMaxItemCount() {
            if (this.inventory == player.getInventory()) {
                return super.getMaxItemCount();
            }
            return 1;
        }

        private boolean stackMovementIsAllowed(ItemStack itemStack) {
            return !(itemStack.getItem() instanceof FilterableMagnetItem) && itemStack != stack;
        }

        private boolean hasSameItem(ItemStack itemStack) {
            if (this.inventory == player.getInventory()) {
                return false;
            }
            boolean hasSameItem = false;
            for (int i = 0; i < getInventory().size() && !hasSameItem; i++) {
                hasSameItem = itemStack.isItemEqual(getInventory().getStack(i));
            }
            return hasSameItem;
        }
    }

}