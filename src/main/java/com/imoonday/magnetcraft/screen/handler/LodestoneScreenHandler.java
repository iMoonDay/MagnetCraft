package com.imoonday.magnetcraft.screen.handler;

import com.imoonday.magnetcraft.registries.special.ScreenRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("ConstantValue")
public class LodestoneScreenHandler extends ScreenHandler {

    private BlockPos pos;
    private final Inventory inventory;
    private final PlayerEntity player;
    private final PropertyDelegate propertyDelegate;

    public LodestoneScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, new SimpleInventory(18), new ArrayPropertyDelegate(3));
        pos = buf.readBlockPos();
    }

    public int getRedstone() {
        return propertyDelegate.get(0);
    }

    public int getDis() {
        return propertyDelegate.get(1);
    }

    public int getDirection() {
        return propertyDelegate.get(2);
    }

    public BlockPos getPos() {
        return pos;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public LodestoneScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ScreenRegistries.LODESTONE_SCREEN_HANDLER, syncId);
        this.player = playerInventory.player;
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);
        this.pos = BlockPos.ORIGIN;
        checkSize(inventory, 18);
        checkDataCount(propertyDelegate, 3);
        int y;
        int x;
        for (y = 0; y < 2; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new Slot(inventory, x + y * 9, 8 + x * 18, 35 + y * 18));
            }
        }
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (y = 0; y < 9; ++y) {
            this.addSlot(new Slot(playerInventory, y, 8 + y * 18, 142));
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

}
