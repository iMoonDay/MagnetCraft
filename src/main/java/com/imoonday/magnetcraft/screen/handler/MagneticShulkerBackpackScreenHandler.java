package com.imoonday.magnetcraft.screen.handler;

import com.imoonday.magnetcraft.common.items.armors.MagneticShulkerBackpackItem;
import com.imoonday.magnetcraft.registries.special.ScreenRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

public class MagneticShulkerBackpackScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final int slot;
    private final PlayerEntity player;

    public MagneticShulkerBackpackScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, new SimpleInventory(27), buf.readInt());
    }

    public MagneticShulkerBackpackScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, int slot) {
        super(ScreenRegistries.MAGNETIC_SHULKER_BACKPACK_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        this.player = playerInventory.player;
        this.slot = slot;
        checkSize(this.inventory, 27);
        int y;
        int x;
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new BackpackSlot(inventory, x + y * 9, 8 + x * 18, 18 + y * 18));
            }
        }
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new BackpackSlot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (y = 0; y < 9; ++y) {
            this.addSlot(new BackpackSlot(playerInventory, y, 8 + y * 18, 142));
        }
    }

    public ItemStack getStack() {
        return this.player.getInventory().getStack(this.slot);
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        if (inventory == this.inventory) {
            DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
            for (int i = 0; i < inventory.size(); i++) {
                stacks.set(i, inventory.getStack(i));
            }
            Inventories.writeNbt(MagneticShulkerBackpackItem.getBackpackNbt(getStack()), stacks);
        }
        this.sendContentUpdates();
    }

    public int getSlot() {
        return this.slot;
    }

    public void updateInventory() {
        DefaultedList<ItemStack> stacks = MagneticShulkerBackpackItem.getItems(this.player.getInventory().getStack(this.slot));
        if (stacks.size() != this.inventory.size()) {
            return;
        }
        for (int i = 0; i < stacks.size(); i++) {
            this.inventory.setStack(i, stacks.get(i));
        }
    }

    @SuppressWarnings("ConstantValue")
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
                slot.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    private class BackpackSlot extends Slot {

        public BackpackSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return !getStack().equals(MagneticShulkerBackpackScreenHandler.this.getStack());
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem().canBeNested() || this.inventory instanceof PlayerInventory;
        }

        @Override
        public void markDirty() {
            super.markDirty();
            MagneticShulkerBackpackScreenHandler.this.onContentChanged(this.inventory);
        }
    }

}
