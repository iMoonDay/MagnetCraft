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
        ItemStack newStack = ItemStack.EMPTY;
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

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
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
