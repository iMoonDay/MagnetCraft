package com.imoonday.magnetcraft.screen.handler;

import com.imoonday.magnetcraft.registries.special.ScreenRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import static com.imoonday.magnetcraft.common.items.magnets.MineralMagnetItem.changeAllCoreEnable;
import static com.imoonday.magnetcraft.common.items.magnets.MineralMagnetItem.changeCoreEnable;
import static com.imoonday.magnetcraft.registries.common.ItemRegistries.RAW_MAGNET_ITEM;
import static net.minecraft.item.Items.*;

@SuppressWarnings("ConstantValue")
public class MineralMagnetScreenHandler extends ScreenHandler {

    private final int slot;
    private final PlayerInventory inventory;

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
        int y;
        int x;
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
                slot.setStack(ItemStack.EMPTY);
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
}
