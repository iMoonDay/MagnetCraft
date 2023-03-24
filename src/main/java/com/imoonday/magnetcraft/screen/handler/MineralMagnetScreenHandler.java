package com.imoonday.magnetcraft.screen.handler;

import com.imoonday.magnetcraft.api.AbstractFilterableItem;
import com.imoonday.magnetcraft.registries.special.ScreenRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;

import static com.imoonday.magnetcraft.common.items.magnets.MineralMagnetItem.changeAllCoreEnable;
import static com.imoonday.magnetcraft.common.items.magnets.MineralMagnetItem.changeCoreEnable;
import static com.imoonday.magnetcraft.registries.common.ItemRegistries.RAW_MAGNET_ITEM;
import static net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.SHULKER_BOXES;
import static net.minecraft.item.Items.*;

/**
 * @author iMoonDay
 */
@SuppressWarnings("ConstantValue")
public class MineralMagnetScreenHandler extends ScreenHandler {

    public static final String SHULKER_BOX = "ShulkerBox";
    private final int slot;
    private final PlayerInventory inventory;
    private final Inventory shulkerBoxSlots = new SimpleInventory(3);

    public MineralMagnetScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, buf.readInt());
    }

    public PlayerInventory getInventory() {
        return inventory;
    }

    public int getSlot() {
        return slot;
    }

    public MineralMagnetScreenHandler(int syncId, PlayerInventory inventory, int slot) {
        super(ScreenRegistries.MINERAL_MAGNET_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        this.slot = slot;
        ItemStack stack = getSlot() != -1 ? getInventory().getStack(getSlot()) : getInventory().player.getOffHandStack();
        NbtList list = stack.getOrCreateNbt().getList(SHULKER_BOX, NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            ItemStack itemstackFromNbt = ItemStack.fromNbt(list.getCompound(i));
            this.shulkerBoxSlots.setStack(i, itemstackFromNbt);
        }
        checkSize(this.shulkerBoxSlots, 3);
        int y;
        int x;
        for (y = 0; y < 3; ++y) {
            this.addSlot(new ShulkerBoxSlot(shulkerBoxSlots, y, 178, 17 + y * 18));
        }
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (y = 0; y < 9; ++y) {
            this.addSlot(new Slot(inventory, y, 8 + y * 18, 142));
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.shulkerBoxSlots.clear();
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        if (inventory == this.shulkerBoxSlots) {
            ArrayList<ItemStack> stacks = new ArrayList<>();
            for (int i = 0; i < inventory.size(); i++) {
                stacks.add(inventory.getStack(i));
            }
            ItemStack stack = getSlot() != -1 ? getInventory().getStack(getSlot()) : getInventory().player.getOffHandStack();
            AbstractFilterableItem.setShulkerBoxItems(stack, stacks);
        }
        this.sendContentUpdates();
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        this.onContentChanged(this.shulkerBoxSlots);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id < 0 || id > 12) {
            return false;
        }
        ItemStack stack = this.slot != -1 ? player.getInventory().getStack(this.slot) : player.getOffHandStack();
        if (id <= 10) {
            Item[] items = new Item[]{QUARTZ, RAW_MAGNET_ITEM, COAL, RAW_IRON, RAW_GOLD, GOLD_NUGGET, DIAMOND, REDSTONE, RAW_COPPER, EMERALD, LAPIS_LAZULI};
            String item = Registries.ITEM.getId(items[id]).toString();
            changeCoreEnable(stack, item);
        } else if (id == 11) {
            changeAllCoreEnable(stack, false);
        } else if (id == 12) {
            changeAllCoreEnable(stack, true);
        }

        this.onContentChanged(this.inventory);
        return super.onButtonClick(player, id);
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
                slot.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
                MineralMagnetScreenHandler.this.onContentChanged(this.shulkerBoxSlots);
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    private class ShulkerBoxSlot extends Slot {

        public ShulkerBoxSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.isIn(SHULKER_BOXES);
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            super.onTakeItem(player, stack);
            MineralMagnetScreenHandler.this.onContentChanged(this.inventory);
        }

    }

}
