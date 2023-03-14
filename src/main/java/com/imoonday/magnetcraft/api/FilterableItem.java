package com.imoonday.magnetcraft.api;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.item.Items.AIR;

public abstract class FilterableItem extends SwitchableItem implements ImplementedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

    public FilterableItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        stack.getOrCreateNbt().putBoolean("Filterable", true);
        filterSet(stack);
        if (canTeleportItems()) {
            shulkerBoxCheck(stack);
        }
        return stack;
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        filterSet(stack);
        if (canTeleportItems()) {
            shulkerBoxSet(stack);
        }
    }

    @Override
    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (itemStack.getNbt() != null && itemStack.getNbt().getBoolean("Filterable")) {
            tooltip.add(itemStack.getOrCreateNbt().getBoolean("Whitelist") ? Text.literal("[").append(Text.translatable("text.autoconfig.magnetcraft.option.whitelist")).append("]").formatted(Formatting.GRAY).formatted(Formatting.BOLD) : Text.literal("[").append(Text.translatable("text.autoconfig.magnetcraft.option.blacklist")).append("]").formatted(Formatting.GRAY).formatted(Formatting.BOLD));
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
        filterCheck(stack);
        if (canTeleportItems()) {
            shulkerBoxCheck(stack);
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void openScreen(PlayerEntity player, Hand hand, FilterableItem filterableMagnetItem) {
        filterableMagnetItem.inventory.clear();
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
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new FilterableMagnetScreenHandler(syncId, inv, filterableMagnetItem, slot);
                }
            });
        }
    }

    public static void filterCheck(ItemStack stack) {
        if (stack.getNbt() == null || !stack.getNbt().contains("Filter") || !stack.getNbt().contains("Whitelist") || !stack.getNbt().contains("CompareDamage") || !stack.getNbt().contains("CompareNbt") || !stack.getNbt().contains("Filterable")) {
            filterSet(stack);
        }
    }

    public static void filterSet(ItemStack stack) {
        if (!stack.getOrCreateNbt().contains("Filterable")) {
            stack.getOrCreateNbt().putBoolean("Filterable", false);
        }
        if (stack.getNbt() != null && stack.getNbt().getBoolean("Filterable")) {
            if (!stack.getOrCreateNbt().contains("Whitelist")) {
                stack.getOrCreateNbt().putBoolean("Whitelist", false);
            }
            if (!stack.getOrCreateNbt().contains("Filter")) {
                stack.getOrCreateNbt().put("Filter", new NbtList());
            }
            if (!stack.getOrCreateNbt().contains("CompareDamage")) {
                stack.getOrCreateNbt().putBoolean("CompareDamage", false);
            }
            if (!stack.getOrCreateNbt().contains("CompareNbt")) {
                stack.getOrCreateNbt().putBoolean("CompareNbt", false);
            }
        }
    }

    public static void setFilterItems(ItemStack stack, ArrayList<ItemStack> stacks) {
        filterCheck(stack);
        NbtList list = stack.getOrCreateNbt().getList("Filter", NbtElement.COMPOUND_TYPE);
        list.clear();
        for (ItemStack otherStack : stacks) {
            NbtCompound otherStackNbt = otherStack.writeNbt(new NbtCompound());
            if (otherStack.isOf(AIR)) {
                continue;
            }
            otherStackNbt.putInt("Count", 1);
            if (!list.contains(otherStackNbt)) {
                list.add(otherStackNbt);
            }
        }
        stack.getOrCreateNbt().put("Filter", list);
    }

    public static void setBoolean(ItemStack stack, String key, boolean b) {
        stack.getOrCreateNbt().putBoolean(key, b);
    }

    public static void setBoolean(ItemStack stack, String key) {
        stack.getOrCreateNbt().putBoolean(key, !stack.getOrCreateNbt().getBoolean(key));
    }

    public static void shulkerBoxCheck(ItemStack stack) {
        if (stack.getNbt() == null || !stack.getNbt().contains("ShulkerBox")) {
            shulkerBoxSet(stack);
        }
    }

    public static void shulkerBoxSet(ItemStack stack) {
        if (!stack.getOrCreateNbt().contains("ShulkerBox")) {
            stack.getOrCreateNbt().put("ShulkerBox", new NbtCompound());
        }
    }

    public static void setShulkerBoxItems(ItemStack stack, ArrayList<ItemStack> stacks) {
        shulkerBoxCheck(stack);
        NbtList list = stack.getOrCreateNbt().getList("ShulkerBox", NbtElement.COMPOUND_TYPE);
        list.clear();
        stacks.stream().map(otherStack -> otherStack.writeNbt(new NbtCompound())).forEach(list::add);
        stack.getOrCreateNbt().put("ShulkerBox", list);
    }

    public boolean canTeleportItems() {
        return false;
    }

}
