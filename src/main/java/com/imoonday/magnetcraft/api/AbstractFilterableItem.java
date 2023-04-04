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
import java.util.stream.IntStream;

import static net.minecraft.item.Items.AIR;

public abstract class AbstractFilterableItem extends AbstractSwitchableItem implements ImplementedInventory {

    public static final String FILTERABLE = "Filterable";
    public static final String WHITELIST = "Whitelist";
    public static final String FILTER = "Filter";
    public static final String COMPARE_DAMAGE = "CompareDamage";
    public static final String COMPARE_NBT = "CompareNbt";
    public static final String SHULKER_BOX = "ShulkerBox";
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

    public AbstractFilterableItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        stack.getOrCreateNbt().putBoolean(FILTERABLE, true);
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
        if (itemStack.getNbt() != null && itemStack.getNbt().getBoolean(FILTERABLE)) {
            tooltip.add(itemStack.getOrCreateNbt().getBoolean(WHITELIST) ? Text.literal("[").append(Text.translatable("text.autoconfig.magnetcraft.option.whitelist")).append("]").formatted(Formatting.GRAY).formatted(Formatting.BOLD) : Text.literal("[").append(Text.translatable("text.autoconfig.magnetcraft.option.blacklist")).append("]").formatted(Formatting.GRAY).formatted(Formatting.BOLD));
            IntStream.range(0, itemStack.getOrCreateNbt().getList(FILTER, NbtElement.COMPOUND_TYPE).size()).forEach(i -> {
                NbtCompound filter = itemStack.getOrCreateNbt().getList(FILTER, NbtElement.COMPOUND_TYPE).getCompound(i);
                ItemStack stack = ItemStack.fromNbt(filter);
                tooltip.add(Text.literal("[" + i + "] ").append(stack.getName()).formatted(Formatting.GRAY).formatted(Formatting.BOLD));
            });
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

    public static void initialize(ItemStack stack, AbstractFilterableItem item) {
        item.inventory.clear();
        NbtList list = stack.getOrCreateNbt().getList(FILTER, NbtElement.COMPOUND_TYPE);
        IntStream.range(0, list.size()).forEach(i -> item.inventory.set(i, ItemStack.fromNbt(list.getCompound(i))));
    }

    public void openScreen(PlayerEntity player, Hand hand, AbstractFilterableItem filterableMagnetItem) {
        ItemStack stack = player.getStackInHand(hand);
        int slot = hand == Hand.MAIN_HAND ? player.getInventory().selectedSlot : 40;
        initialize(stack, filterableMagnetItem);
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
        if (stack.getNbt() == null || !stack.getNbt().contains(FILTER) || !stack.getNbt().contains(WHITELIST) || !stack.getNbt().contains(COMPARE_DAMAGE) || !stack.getNbt().contains(COMPARE_NBT) || !stack.getNbt().contains(FILTERABLE)) {
            filterSet(stack);
        }
    }

    public static void filterSet(ItemStack stack) {
        if (!stack.getOrCreateNbt().contains(FILTERABLE)) {
            stack.getOrCreateNbt().putBoolean(FILTERABLE, false);
        }
        if (stack.getNbt() != null && stack.getNbt().getBoolean(FILTERABLE)) {
            if (!stack.getOrCreateNbt().contains(WHITELIST)) {
                stack.getOrCreateNbt().putBoolean(WHITELIST, false);
            }
            if (!stack.getOrCreateNbt().contains(FILTER)) {
                stack.getOrCreateNbt().put(FILTER, new NbtList());
            }
            if (!stack.getOrCreateNbt().contains(COMPARE_DAMAGE)) {
                stack.getOrCreateNbt().putBoolean(COMPARE_DAMAGE, false);
            }
            if (!stack.getOrCreateNbt().contains(COMPARE_NBT)) {
                stack.getOrCreateNbt().putBoolean(COMPARE_NBT, false);
            }
        }
    }

    public static void setFilterItems(ItemStack stack, ArrayList<ItemStack> stacks) {
        filterCheck(stack);
        NbtList list = new NbtList();
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
        stack.getOrCreateNbt().put(FILTER, list);
    }

    public static void setBoolean(ItemStack stack, String key, boolean b) {
        stack.getOrCreateNbt().putBoolean(key, b);
    }

    public static void setBoolean(ItemStack stack, String key) {
        stack.getOrCreateNbt().putBoolean(key, !stack.getOrCreateNbt().getBoolean(key));
    }

    public static void shulkerBoxCheck(ItemStack stack) {
        if (stack.getNbt() == null || !stack.getNbt().contains(SHULKER_BOX)) {
            shulkerBoxSet(stack);
        }
    }

    public static void shulkerBoxSet(ItemStack stack) {
        if (!stack.getOrCreateNbt().contains(SHULKER_BOX)) {
            stack.getOrCreateNbt().put(SHULKER_BOX, new NbtCompound());
        }
    }

    public static void setShulkerBoxItems(ItemStack stack, ArrayList<ItemStack> stacks) {
        shulkerBoxCheck(stack);
        NbtList list = stack.getOrCreateNbt().getList(SHULKER_BOX, NbtElement.COMPOUND_TYPE);
        list.clear();
        stacks.stream().map(otherStack -> otherStack.writeNbt(new NbtCompound())).forEach(list::add);
        stack.getOrCreateNbt().put(SHULKER_BOX, list);
    }

    public abstract boolean canTeleportItems();

}
