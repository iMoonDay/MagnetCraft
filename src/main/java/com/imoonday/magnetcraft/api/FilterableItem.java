package com.imoonday.magnetcraft.api;

import com.imoonday.magnetcraft.methods.FilterNbtMethods;
import com.imoonday.magnetcraft.screen.handler.FilterableMagnetScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class FilterableItem extends SwitchableItem implements ImplementedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

    public FilterableItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        stack.getOrCreateNbt().putBoolean("Filterable", true);
        FilterNbtMethods.filterSet(stack);
        return stack;
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        FilterNbtMethods.filterSet(stack);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (itemStack.getNbt() != null && itemStack.getNbt().getBoolean("Filterable")) {
            if (itemStack.getOrCreateNbt().getBoolean("Whitelist")) {
                tooltip.add(Text.literal("[").append(Text.translatable("text.autoconfig.magnetcraft.option.whitelist")).append("]").formatted(Formatting.GRAY).formatted(Formatting.BOLD));
            } else {
                tooltip.add(Text.literal("[").append(Text.translatable("text.autoconfig.magnetcraft.option.blacklist")).append("]").formatted(Formatting.GRAY).formatted(Formatting.BOLD));
            }
            for (int i = 0; i < itemStack.getOrCreateNbt().getList("Filter", NbtElement.COMPOUND_TYPE).size(); i++) {
                NbtCompound filter = itemStack.getOrCreateNbt().getList("Filter", NbtElement.COMPOUND_TYPE).getCompound(i);
                ItemStack stack = ItemStack.fromNbt(filter);
                String stackName = stack.getTranslationKey();
                if (stackName.equals(Text.translatable(stackName).getString())) {
                    stackName = stackName.replace("item.", "block.");
                }
                tooltip.add(Text.literal("[" + i + "] ").append(Text.translatable(stackName)).formatted(Formatting.GRAY).formatted(Formatting.BOLD));
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        FilterNbtMethods.filterCheck(stack);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void openScreen(PlayerEntity player, Hand hand, FilterableItem filterableMagnetItem) {
        filterableMagnetItem.inventory.clear();
        player.getInventory().markDirty();
        ItemStack stack = player.getStackInHand(hand);
        int slot = hand == Hand.MAIN_HAND ? player.getInventory().selectedSlot : -1;
        NbtList list = stack.getOrCreateNbt().getList("Filter", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            ItemStack itemstackFromNbt = ItemStack.fromNbt(list.getCompound(i));
            filterableMagnetItem.inventory.set(i, itemstackFromNbt);
        }
        if (player.world != null && !player.world.isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf buf) {
                    buf.writeInt(slot);
                }

                @Override
                public Text getDisplayName() {
                    return Text.translatable(stack.getItem().getTranslationKey());
                }

                @Override
                public @me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new FilterableMagnetScreenHandler(syncId, inv, filterableMagnetItem, slot);
                }
            });
        }
    }
}
