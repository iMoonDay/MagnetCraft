package com.imoonday.magnetcraft.common.items.armors;

import com.imoonday.magnetcraft.api.ImplementedInventory;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import com.imoonday.magnetcraft.screen.handler.MagneticShulkerBackpackScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MagneticShulkerBackpackItem extends BlockItem implements ImplementedInventory, Equipment {

    public static final String BLOCK_ENTITY_TAG_KEY = "BlockEntityTag";
    public static final String ITEMS = "Items";
    public static final String ID = "id";
    public static final String BACKPACK_ID = "magnetcraft:magnetic_shulker_backpack";
    public static final String SLOT = "Slot";
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);

    public MagneticShulkerBackpackItem(Settings settings) {
        super(BlockRegistries.MAGNETIC_SHULKER_BACKPACK_BLOCK, settings);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int emptySlot = 27 - getItemCount(stack);
        Formatting color;
        if (emptySlot >= 18) {
            color = Formatting.GREEN;
        } else if (emptySlot >= 9) {
            color = Formatting.GOLD;
        } else {
            color = Formatting.RED;
        }
        Text slot = Text.literal(String.valueOf(emptySlot)).formatted(Formatting.BOLD).formatted(color);
        tooltip.add(Text.translatable("block.magnetcraft.magnetic_shulker_backpack.tooltip").formatted(Formatting.BOLD).formatted(Formatting.GRAY).append(slot));
    }

    private Inventory getInventory(ItemStack stack) {
        this.inventory.clear();
        Inventories.readNbt(getBackpackNbt(stack), this.inventory);
        return this;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.isSneaking()) {
            int slot = hand == Hand.MAIN_HAND ? user.getInventory().selectedSlot : 40;
            openScreen(user, stack, slot);
        } else {
            equipAndSwap(this, world, user, hand);
        }
        user.getInventory().markDirty();
        return TypedActionResult.success(stack);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (checkValidClick(slot, clickType, player)) {
            if (otherStack.isEmpty()) {
                openScreen(player, stack, slot.getIndex());
                player.getInventory().markDirty();
                return true;
            } else {
                return insertStack(player, slot.getIndex(), stack, otherStack, getItems(stack));
            }
        }
        return false;
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        ItemStack stackOnSlot = player.getInventory().getStack(slot.getIndex());
        if (stackOnSlot.isEmpty()) {
            return false;
        }
        if (checkValidClick(slot, clickType, player)) {
            return insertStack(player, slot.getIndex(), stack, stackOnSlot, getItems(stack));
        }
        return false;
    }

    private static boolean checkValidClick(Slot slot, ClickType clickType, PlayerEntity player) {
        return clickType == ClickType.RIGHT && slot.inventory instanceof PlayerInventory && !player.getAbilities().creativeMode;
    }

    public static boolean insertStack(PlayerEntity player, int slot, ItemStack stack, ItemStack otherStack, DefaultedList<ItemStack> inventory) {
        if (!otherStack.getItem().canBeNested()) {
            return false;
        }
        if (inventory.stream().anyMatch(inventoryStack -> canCombine(otherStack, inventoryStack))) {
            for (ItemStack inventoryStack : inventory) {
                if (!canCombine(otherStack, inventoryStack)) {
                    continue;
                }
                if (otherStack.getCount() <= 0) {
                    Inventories.writeNbt(getBackpackNbt(stack), inventory);
                    updateIfOpening(player, slot);
                    return true;
                }
                int totalCount = otherStack.getCount() + inventoryStack.getCount();
                int maxCount = inventoryStack.getMaxCount();
                if (totalCount <= maxCount) {
                    inventoryStack.setCount(totalCount);
                    otherStack.setCount(0);
                    Inventories.writeNbt(getBackpackNbt(stack), inventory);
                    updateIfOpening(player, slot);
                    return true;
                } else {
                    int mergeCount = maxCount - inventoryStack.getCount();
                    otherStack.setCount(otherStack.getCount() - mergeCount);
                    inventoryStack.setCount(maxCount);
                }
            }
        }
        if (otherStack.getCount() > 0) {
            List<Integer> occupiedSlots = getItemNbts(stack).mapToInt(nbtCompound -> nbtCompound.getByte(SLOT)).boxed().toList();
            OptionalInt emptySlot = IntStream.range(0, inventory.size()).filter(value -> !occupiedSlots.contains(value)).sorted().findFirst();
            if (emptySlot.isPresent()) {
                inventory.set(emptySlot.getAsInt(), otherStack);
                Inventories.writeNbt(getBackpackNbt(stack), inventory);
                otherStack.setCount(0);
                updateIfOpening(player, slot);
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static void updateIfOpening(PlayerEntity player, int slot) {
        if (player.currentScreenHandler instanceof MagneticShulkerBackpackScreenHandler handler && handler.getSlot() == slot) {
            handler.updateInventory();
        }
    }

    private static int getItemCount(ItemStack stack) {
        return getBackpackNbt(stack).getList(ITEMS, NbtElement.COMPOUND_TYPE).size();
    }

    private static boolean canCombine(ItemStack otherStack, ItemStack inventoryStack) {
        return ItemStack.canCombine(otherStack, inventoryStack) && inventoryStack.getCount() < inventoryStack.getMaxCount();
    }

    public void openScreen(PlayerEntity player, ItemStack stack, int slot) {
        if (player.world != null && !player.world.isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {

                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeInt(slot);
                }

                @Override
                public Text getDisplayName() {
                    return stack.getName();
                }

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new MagneticShulkerBackpackScreenHandler(syncId, inv, getInventory(stack), slot);
                }

            });
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    public static NbtCompound getBackpackNbt(ItemStack stack) {
        return getOrCreateNbt(stack).getCompound(BLOCK_ENTITY_TAG_KEY);
    }

    public static DefaultedList<ItemStack> getItems(ItemStack stack) {
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
        getItemNbts(stack).forEach(nbtCompound -> {
            int slot = nbtCompound.getByte(SLOT);
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            stacks.set(slot, itemStack);
        });
        return stacks;
    }

    @NotNull
    private static Stream<NbtCompound> getItemNbts(ItemStack stack) {
        return getBackpackNbt(stack).getList(ITEMS, NbtElement.COMPOUND_TYPE).stream().map(nbtElement -> (NbtCompound) nbtElement);
    }

    @NotNull
    private static NbtCompound getOrCreateNbt(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains(BLOCK_ENTITY_TAG_KEY)) {
            NbtList list = new NbtList();
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.put(ITEMS, list);
            nbtCompound.putString(ID, BACKPACK_ID);
            nbt.put(BLOCK_ENTITY_TAG_KEY, nbtCompound);
        }
        return nbt;
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.CHEST;
    }

    @Override
    public SoundEvent getEquipSound() {
        return ItemRegistries.MAGNETIC_IRON_MATERIAL.getEquipSound();
    }

    @Override
    public boolean canBeNested() {
        return false;
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

}
