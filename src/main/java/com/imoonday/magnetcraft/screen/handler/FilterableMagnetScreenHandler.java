package com.imoonday.magnetcraft.screen.handler;

import com.imoonday.magnetcraft.api.FilterableItem;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import com.imoonday.magnetcraft.registries.special.ScreenRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.Arrays;

import static com.imoonday.magnetcraft.common.items.MagnetControllerItem.changeMagnetEnable;
import static net.minecraft.item.Items.*;

@SuppressWarnings("ConstantValue")
public class FilterableMagnetScreenHandler extends ScreenHandler {

    private final int slot;
    private final Inventory inventory;
    private final PlayerEntity player;

    public FilterableMagnetScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, new SimpleInventory(9), buf.readInt());
    }

    public Inventory getInventory() {
        return inventory;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isCropMagnet() {
        return getStack().isOf(ItemRegistries.CROP_MAGNET_ITEM);
    }

    public ItemStack getStack() {
        return slot != -1 ? player.getInventory().getStack(slot) : player.getOffHandStack();
    }

    public FilterableMagnetScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, int slot) {
        super(ScreenRegistries.FILTERABLE_MAGNET_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        this.slot = slot;
        this.player = playerInventory.player;
        checkSize(inventory, 9);
        int y;
        int x;
        for (x = 0; x < 9; ++x) {
            if (isCropMagnet()) {
                this.addSlot(new CropSlot(inventory, x, 8 + x * 18, 35 + 18));
            } else {
                this.addSlot(new LockedSlot(inventory, x, 8 + x * 18, 35 + 18));
            }
        }
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                if (isCropMagnet()) {
                    this.addSlot(new CropSlot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
                } else {
                    this.addSlot(new LockedSlot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
                }

            }
        }
        for (y = 0; y < 9; ++y) {
            if (isCropMagnet()) {
                this.addSlot(new CropSlot(playerInventory, y, 8 + y * 18, 142));
            } else {
                this.addSlot(new LockedSlot(playerInventory, y, 8 + y * 18, 142));
            }
        }
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.clear();
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < this.inventory.size(); i++) {
            stacks.add(this.inventory.getStack(i));
        }
        FilterableItem.setFilterItems(getStack(), stacks);
        this.sendContentUpdates();
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        this.onContentChanged(this.inventory);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id < 0 || id > 4) {
            return false;
        }
        switch (id) {
            case 0 -> FilterableItem.setBoolean(getStack(), "Whitelist", true);
            case 1 -> FilterableItem.setBoolean(getStack(), "Whitelist", false);
            case 2 -> FilterableItem.setBoolean(getStack(), "CompareDamage");
            case 3 -> FilterableItem.setBoolean(getStack(), "CompareNbt");
            case 4 -> {
                if (getStack().isOf(ItemRegistries.MAGNET_CONTROLLER_ITEM)) {
                    changeMagnetEnable(player);
                } else {
                    FilterableItem.setBoolean(getStack(), "Enable");
                }
            }
        }
        this.onContentChanged(this.inventory);
        return super.onButtonClick(player, id);
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
                FilterableMagnetScreenHandler.this.onContentChanged(this.inventory);
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
                        FilterableMagnetScreenHandler.this.onContentChanged(this.inventory);
                        bl = true;
                    } else if (itemStack.getCount() < stack.getMaxCount()) {
                        stack.decrement(stack.getMaxCount() - itemStack.getCount());
                        itemStack.setCount(stack.getMaxCount());
                        slot.markDirty();
                        FilterableMagnetScreenHandler.this.onContentChanged(this.inventory);
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
                    FilterableMagnetScreenHandler.this.onContentChanged(this.inventory);
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
            return stackMovementIsAllowed(stack) && noSameItem(stack);
        }

        @Override
        public int getMaxItemCount() {
            return this.inventory instanceof PlayerInventory ? super.getMaxItemCount() : 1;
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            super.onTakeItem(player, stack);
            FilterableMagnetScreenHandler.this.onContentChanged(this.inventory);
        }

        private boolean stackMovementIsAllowed(ItemStack itemStack) {
            return itemStack != FilterableMagnetScreenHandler.this.getStack();
        }

        private boolean noSameItem(ItemStack itemStack) {
            return !getInventory().containsAny(itemStack::isItemEqual) || this.inventory instanceof PlayerInventory;
        }
    }

    private class CropSlot extends LockedSlot {

        public CropSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return super.stackMovementIsAllowed(stack) && super.noSameItem(stack) && isCropItem(stack);
        }

        private boolean isCropItem(ItemStack itemStack) {
            Item[] items = new Item[]{WHEAT, CARROT, POTATO, BEETROOT_SEEDS, MELON_SEEDS, PUMPKIN_SEEDS, GLOW_BERRIES, SUGAR_CANE, BAMBOO, CACTUS, KELP, SWEET_BERRIES, NETHER_WART, COCOA_BEANS};
            return Arrays.asList(items).contains(itemStack.getItem()) || this.inventory instanceof PlayerInventory;
        }
    }

}
