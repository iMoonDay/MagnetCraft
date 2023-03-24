package com.imoonday.magnetcraft.screen.handler;

import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import com.imoonday.magnetcraft.registries.special.ScreenRegistries;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.HashMap;
import java.util.Map;

/**
 * @author iMoonDay
 */
@SuppressWarnings({"ConstantValue", "AlibabaUndefineMagicConstant", "AlibabaAvoidComplexCondition"})
public class AdvancedGrindstoneScreenHandler extends ScreenHandler {

    private final Inventory result = new CraftingResultInventory();
    private final Inventory input = new SimpleInventory(2) {
        @Override
        public void markDirty() {
            super.markDirty();
            AdvancedGrindstoneScreenHandler.this.onContentChanged(this);
        }
    };
    private final ScreenHandlerContext context;
    private int maxIndex;
    private int index;
    private final PlayerEntity player;

    public AdvancedGrindstoneScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, ScreenHandlerContext.EMPTY);
    }

    public Inventory getResult() {
        return result;
    }

    public AdvancedGrindstoneScreenHandler(int syncId, PlayerInventory playerInventory, final ScreenHandlerContext context) {
        super(ScreenRegistries.ADVANCED_GRINDSTONE_SCREEN_HANDLER, syncId);
        this.context = context;
        this.index = 0;
        this.maxIndex = 0;
        this.player = playerInventory.player;
        checkSize(input, 2);
        int y;
        int x;
        this.addSlot(new Slot(this.input, 0, 49, 19) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.hasEnchantments() && !stack.isOf(Items.ENCHANTED_BOOK);
            }
        });
        this.addSlot(new Slot(this.input, 1, 49, 40) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ItemRegistries.EXTRACTION_MODULE_ITEM);
            }
        });
        this.addSlot(new Slot(this.result, 2, 129, 34) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                context.run((world, pos) -> world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.VOICE));
                if (AdvancedGrindstoneScreenHandler.this.input.getStack(0).isDamaged()) {
                    AdvancedGrindstoneScreenHandler.this.input.setStack(0, ItemStack.EMPTY);
                } else {
                    ItemStack damagedStack = AdvancedGrindstoneScreenHandler.this.input.getStack(0).copy();
                    damagedStack.setDamage(damagedStack.getMaxDamage() - 1);
                    damagedStack.getEnchantments().clear();
                    AdvancedGrindstoneScreenHandler.this.input.setStack(0, damagedStack);
                }
                AdvancedGrindstoneScreenHandler.this.input.setStack(1, AdvancedGrindstoneScreenHandler.this.input.getStack(1).copyWithCount(AdvancedGrindstoneScreenHandler.this.input.getStack(1).getCount() - 1));
                AdvancedGrindstoneScreenHandler.this.input.markDirty();
            }
        });
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
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        if (inventory == this.input) {
            this.updateResult();
        }
        if (!this.input.getStack(0).isOf(Items.AIR) && !this.input.getStack(0).hasEnchantments()) {
            player.getInventory().offerOrDrop(AdvancedGrindstoneScreenHandler.this.input.getStack(0));
            this.input.setStack(0, ItemStack.EMPTY);
            this.input.markDirty();
        }
    }

    private void updateResult() {
        ItemStack enchantedStack = this.input.getStack(0);
        ItemStack moduleStack = this.input.getStack(1);
        boolean stackIn = !enchantedStack.isEmpty() && !moduleStack.isEmpty();
        if (stackIn) {
            ItemStack enchantedBookStack;
            enchantedBookStack = new ItemStack(Items.ENCHANTED_BOOK);
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(enchantedStack);
            this.maxIndex = enchantments.size() - 1;
            if (enchantments.size() == 0 || this.index > this.maxIndex || this.index < 0) {
                return;
            }
            Enchantment enchantment = enchantments.keySet().stream().toList().get(index);
            int level = enchantments.get(enchantment);
            Map<Enchantment, Integer> newEnchantment = new HashMap<>(20);
            newEnchantment.put(enchantment, level);
            EnchantmentHelper.set(newEnchantment, enchantedBookStack);
            this.result.setStack(0, enchantedBookStack);
        } else {
            this.index = 0;
            this.result.setStack(0, ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.input));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return AdvancedGrindstoneScreenHandler.canUse(this.context, player, BlockRegistries.ADVANCED_GRINDSTONE_BLOCK);
    }

    @SuppressWarnings("AlibabaSwitchStatement")
    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id < 0 || id > 1) {
            return false;
        }
        switch (id) {
            case 0 -> {
                if (index - 1 < 0) {
                    this.index = maxIndex;
                } else {
                    this.index--;
                }
            }
            case 1 -> {
                if (index + 1 > maxIndex) {
                    this.index = 0;
                } else {
                    this.index++;
                }
            }
        }
        this.input.markDirty();
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            ItemStack itemStack3 = this.input.getStack(0);
            ItemStack itemStack4 = this.input.getStack(1);
            if (slot == 2) {
                if (!this.insertItem(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot2.onQuickTransfer(itemStack2, itemStack);
            } else if (slot == 0 || slot == 1 ? !this.insertItem(itemStack2, 3, 39, false) : (itemStack3.isEmpty() || itemStack4.isEmpty() ? !this.insertItem(itemStack2, 0, 2, false) : (slot >= 3 && slot < 30 ? !this.insertItem(itemStack2, 30, 39, false) : slot >= 30 && slot < 39 && !this.insertItem(itemStack2, 3, 30, false)))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot2.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }
}
