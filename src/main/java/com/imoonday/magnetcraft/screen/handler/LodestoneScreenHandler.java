package com.imoonday.magnetcraft.screen.handler;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.special.ScreenRegistries;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

/**
 * @author iMoonDay
 */
@SuppressWarnings({"ConstantValue", "AlibabaUndefineMagicConstant"})
public class LodestoneScreenHandler extends ScreenHandler {

    public static final String REDSTONE = "redstone";
    public static final String DIS = "dis";
    private BlockPos pos;
    private final Inventory inventory;
    private final PlayerEntity player;
    private final PropertyDelegate propertyDelegate;
    private final ScreenHandlerContext context;
    private int lastDis = 0;

    public LodestoneScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, new SimpleInventory(18), new ArrayPropertyDelegate(4), ScreenHandlerContext.EMPTY);
        this.pos = buf.readBlockPos();
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

    public boolean isFilter() {
        return propertyDelegate.get(3) == 1;
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

    public LodestoneScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate, final ScreenHandlerContext context) {
        super(ScreenRegistries.LODESTONE_SCREEN_HANDLER, syncId);
        this.player = playerInventory.player;
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);
        this.context = context;
        this.pos = BlockPos.ORIGIN;
        checkSize(inventory, 18);
        checkDataCount(propertyDelegate, 4);
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

    @SuppressWarnings("AlibabaSwitchStatement")
    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id < 0 || id > 3) {
            return false;
        }
        this.context.run((world, pos) -> {
            BlockEntity entity = world.getBlockEntity(pos);
            if (world.getBlockState(pos).isOf(BlockRegistries.LODESTONE_BLOCK) && entity != null) {
                NbtCompound nbt = entity.createNbt();
                final int disEachClick = ModConfig.getValue().disEachClick;
                int maxDis = ModConfig.getValue().lodestoneMaxDis;
                switch (id) {
                    case 0 -> {
                        nbt.putBoolean(REDSTONE, !entity.createNbt().getBoolean(REDSTONE));
                        if (entity.createNbt().getBoolean(REDSTONE)) {
                            nbt.putDouble(DIS, lastDis);
                        } else {
                            this.lastDis = (int) nbt.getDouble(DIS);
                        }
                    }
                    case 1 -> {
                        if (!entity.createNbt().getBoolean(REDSTONE)) {
                            nbt.putDouble(DIS, entity.createNbt().getDouble(DIS) - disEachClick >= 0 ? entity.createNbt().getDouble(DIS) - disEachClick : maxDis);
                        }
                    }
                    case 2 -> {
                        if (!entity.createNbt().getBoolean(REDSTONE)) {
                            nbt.putDouble(DIS, entity.createNbt().getDouble(DIS) + disEachClick <= maxDis ? entity.createNbt().getDouble(DIS) + disEachClick : 0);
                        }
                    }
                    case 3 -> nbt.putBoolean("filter", !isFilter());
                }
                entity.readNbt(nbt);
                entity.markDirty();
            }
        });
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
