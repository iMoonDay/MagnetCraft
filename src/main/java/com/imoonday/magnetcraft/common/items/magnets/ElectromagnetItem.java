package com.imoonday.magnetcraft.common.items.magnets;

import com.imoonday.magnetcraft.api.AbstractFilterableItem;
import com.imoonday.magnetcraft.common.entities.wrench.MagneticWrenchEntity;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import com.imoonday.magnetcraft.registries.special.CustomStatRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class ElectromagnetItem extends AbstractFilterableItem {

    public static final String ENABLE = "Enable";
    public static final String FILTERABLE = "Filterable";
    public static final String SHULKER_BOX = "ShulkerBox";
    public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    public static final String TAG = "tag";
    public static final String SLOT = "Slot";
    public static final String ITEMS = "Items";
    public static final String COUNT = "Count";
    public static final String ID = "id";
    public static final String SHULKER_BOX_ID = "minecraft:shulker_box";
    public static final String AIR_ID = "minecraft:air";

    public ElectromagnetItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.ELECTROMAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> itemStack.getNbt() == null || !itemStack.getNbt().contains(ENABLE) ? 0.0F : itemStack.getOrCreateNbt().getBoolean(ENABLE) ? 1.0F : 0.0F);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.ENDER_PEARL);
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return new ItemStack(ItemRegistries.ELECTROMAGNET_CRAFTING_MODULE_ITEM);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.magnetcraft.electromagnet.tooltip.1")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.magnetcraft.electromagnet.tooltip.2")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 20;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!(user instanceof PlayerEntity player)) {
            return stack;
        }
        double dis = ModConfig.getValue().electromagnetTeleportMinDis;
        if (!world.isClient && !player.isCreative()) {
            stack.addDamage(1);
        }
        Hand hand = user.getActiveHand();
        teleportItems(world, player, dis, hand);
        user.setCooldown(stack, 20);
        return stack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        boolean sneakToSwitch = ModConfig.getConfig().enableSneakToSwitch;
        boolean reversal = ModConfig.getConfig().rightClickReversal;
        boolean sneaking = user.isSneaking();
        if (sneaking && user.getAbilities().flying) {
            sneaking = false;
        }
        ItemStack stackInHand = user.getStackInHand(hand);
        if (sneaking != reversal) {
            if (stackInHand.getOrCreateNbt().getBoolean(FILTERABLE)) {
                if (!user.world.isClient) {
                    openScreen(user, hand, this);
                }
            } else {
                if (!sneakToSwitch) {
                    return super.use(world, user, hand);
                }
                enabledSwitch(world, user, hand);
            }
        } else if (!user.isBroken(hand)) {
            if (user.getAbilities().creativeMode || !stackInHand.isBroken()) {
                user.setCurrentHand(hand);
            }
            return TypedActionResult.fail(stackInHand);
        }
        user.setCooldown(stackInHand, 20);
        return TypedActionResult.success(stackInHand, !stackInHand.isBroken());
    }

    public static void teleportItems(World world, PlayerEntity player, double dis, Hand hand) {
        boolean message = ModConfig.getConfig().displayMessageFeedback;
        int magnetHandSpacing = ModConfig.getValue().magnetHandSpacing;
        if (player.isBroken(hand)) {
            return;
        }
        if (hand == Hand.MAIN_HAND) {
            dis += magnetHandSpacing;
        }
        double finalDis = dis;
        List<Entity> entities = player.world.getOtherEntities(player, player.getBoundingBox().expand(dis), targetEntity -> (targetEntity instanceof ItemEntity || targetEntity instanceof ExperienceOrbEntity) && targetEntity.getPos().isInRange(player.getPos(), finalDis));
        int count = entities.size();
        entities.forEach(targetEntity -> {
            player.addDamage(hand, 1, true);
            if (targetEntity instanceof ExperienceOrbEntity entity) {
                int amount = entity.getExperienceAmount();
                player.addExperience(amount);
            } else if (targetEntity instanceof ItemEntity itemEntity) {
                tryInsertIntoShulkerBox(player, hand, itemEntity.getStack());
            }
            targetEntity.kill();
            player.incrementStat(CustomStatRegistries.ITEMS_TELEPORTED_TO_PLAYER);
            player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1, 1);
        });
        List<Entity> wrenchEntities = player.world.getOtherEntities(null, player.getBoundingBox().expand(dis * 10), entity -> (entity instanceof MagneticWrenchEntity wrench && wrench.getOwner() == player));
        wrenchEntities.forEach(entity -> entity.setPosition(player.getPos()));
        String text = count > 0 ? "text.magnetcraft.message.teleport.tooltip.1" : "text.magnetcraft.message.teleport.tooltip.2";
        if (!world.isClient && message) {
            player.sendMessage(Text.translatable(text, dis, count));
        }
        if (!wrenchEntities.isEmpty() && !world.isClient) {
            player.sendMessage(Text.translatable("text.magnetcraft.message.teleport.tooltip.3", wrenchEntities.size()));
        }
    }

    @Override
    public int getEnchantability() {
        return 14;
    }

    @Override
    public boolean canTeleportItems() {
        return true;
    }

    public static void tryInsertIntoShulkerBox(PlayerEntity player, Hand hand, ItemStack insertStack) {
        if (Block.getBlockFromItem(insertStack.getItem()) instanceof ShulkerBoxBlock) {
            player.getInventory().offerOrDrop(insertStack);
            return;
        }
        ItemStack stackInHand = player.getStackInHand(hand);
        NbtCompound handStackNbt = stackInHand.getOrCreateNbt();
        List<NbtElement> shulkerBoxList = handStackNbt.getList(SHULKER_BOX, NbtElement.COMPOUND_TYPE).stream().filter(nbtElement -> nbtElement instanceof NbtCompound boxNbt && !AIR_ID.equals(boxNbt.getString(ID))).toList();
        if (shulkerBoxList.isEmpty()) {
            player.getInventory().offerOrDrop(insertStack);
            return;
        }
        int boxes = shulkerBoxList.size();
        int currentIndex = 1;
        for (NbtElement shulkerBox : shulkerBoxList) {
            if (insertStack.getCount() < 1) {
                break;
            }
            NbtCompound shulkerBoxNbt = (NbtCompound) shulkerBox;
            ItemStack shulkerBoxStack = ItemStack.fromNbt(shulkerBoxNbt);
            if (Block.getBlockFromItem(shulkerBoxStack.getItem()) instanceof ShulkerBoxBlock) {
                NbtList items = shulkerBoxNbt.getCompound(TAG).getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE).isEmpty() ? new NbtList() : shulkerBoxNbt.getCompound(TAG).getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE).copy();
                NbtCompound insertStackNbt = insertStack.writeNbt(new NbtCompound());
                ArrayList<NbtCompound> newOriginalItemNbt = new ArrayList<>();
                ArrayList<Integer> itemSlots = new ArrayList<>();
                int slot = -1;
                slot = getSlot(shulkerBoxNbt, insertStackNbt, newOriginalItemNbt, itemSlots, slot);
                items.add(insertStackNbt);
                NbtCompound blockEntityTag = new NbtCompound();
                blockEntityTag.put(ITEMS, items);
                blockEntityTag.putString(ID, SHULKER_BOX_ID);
                NbtCompound tag = new NbtCompound();
                tag.put(BLOCK_ENTITY_TAG, blockEntityTag);
                if (!shulkerBoxNbt.contains(TAG)) {
                    shulkerBoxNbt.put(TAG, tag);
                    break;
                } else if (!shulkerBoxNbt.getCompound(TAG).contains(BLOCK_ENTITY_TAG)) {
                    shulkerBoxNbt.getCompound(TAG).put(BLOCK_ENTITY_TAG, blockEntityTag);
                    break;
                } else if (!shulkerBoxNbt.getCompound(TAG).getCompound(BLOCK_ENTITY_TAG).contains(ITEMS) || shulkerBoxNbt.getCompound(TAG).getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE).isEmpty()) {
                    shulkerBoxNbt.getCompound(TAG).getCompound(BLOCK_ENTITY_TAG).put(ITEMS, items);
                    break;
                } else {
                    currentIndex = tryCombineAndStartNext(player, insertStack, boxes, currentIndex, shulkerBoxNbt, insertStackNbt, newOriginalItemNbt, slot);
                }
            } else {
                player.getInventory().offerOrDrop(insertStack);
            }
        }
    }

    private static int getSlot(NbtCompound shulkerBoxNbt, NbtCompound insertStackNbt, ArrayList<NbtCompound> newOriginalItemNbt, ArrayList<Integer> itemSlots, int slot) {
        if (shulkerBoxNbt.getCompound(TAG).getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE).isEmpty()) {
            insertStackNbt.putByte(SLOT, (byte) 0);
        } else {
            shulkerBoxNbt.getCompound(TAG).getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE).forEach(nbtElement1 -> {
                NbtCompound originaItemNbt = (NbtCompound) nbtElement1;
                NbtCompound originalItemNbtCopy = originaItemNbt.copy();
                newOriginalItemNbt.add(originalItemNbtCopy);
            });
            for (NbtCompound newOriginalItem : newOriginalItemNbt) {
                itemSlots.add(newOriginalItem.getInt(SLOT));
            }
            List<Integer> emptySlots = new ArrayList<>(IntStream.range(0, 27).boxed().toList());
            emptySlots.removeAll(itemSlots);
            if (!emptySlots.isEmpty()) {
                slot = Collections.min(emptySlots);
                insertStackNbt.putByte(SLOT, (byte) slot);
            }
        }
        return slot;
    }

    private static int tryCombineAndStartNext(PlayerEntity player, ItemStack insertStack, int boxes, int currentIndex, NbtCompound shulkerBoxNbt, NbtCompound insertStackNbt, ArrayList<NbtCompound> newOriginalItemNbt, int slot) {
        for (NbtCompound newOriginalItem : newOriginalItemNbt) {
            int newOriginalItemSlot = newOriginalItem.getInt(SLOT);
            int newOriginalItemCount = newOriginalItem.getInt(COUNT);
            NbtCompound newOriginalItemCopy = getNbtCopyWithoutSlotAndCount(newOriginalItem);
            NbtCompound insertStackNbtCopy = getNbtCopyWithoutSlotAndCount(insertStackNbt);
            if (newOriginalItemCopy.equals(insertStackNbtCopy) && insertStack.isStackable()) {
                if (insertStack.getCount() + newOriginalItemCount <= insertStack.getMaxCount()) {
                    setCount(player, insertStack, shulkerBoxNbt, newOriginalItemSlot);
                    break;
                } else {
                    decreaseCount(player, insertStack, shulkerBoxNbt, newOriginalItemSlot, newOriginalItemCount);
                }
            }
        }
        if (insertStack.getCount() > 0) {
            if (slot != -1) {
                insertStackNbt.putByte(COUNT, (byte) insertStack.getCount());
                shulkerBoxNbt.getCompound(TAG).getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE).add(insertStackNbt);
                insertStack.setCount(0);
            } else {
                if (++currentIndex > boxes) {
                    player.getInventory().offerOrDrop(insertStack);
                }
            }
        }
        return currentIndex;
    }

    private static void decreaseCount(PlayerEntity player, ItemStack insertStack, NbtCompound shulkerBoxNbt, int newOriginalItemSlot, int newOriginalItemCount) {
        shulkerBoxNbt.getCompound(TAG).getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE).stream().filter(nbtElement1 -> nbtElement1 instanceof NbtCompound nbtCompound1 && nbtCompound1.getInt(SLOT) == newOriginalItemSlot).findFirst().ifPresentOrElse(nbtElement1 -> {
            NbtCompound originalItemNbt = (NbtCompound) nbtElement1;
            originalItemNbt.putByte(COUNT, (byte) insertStack.getMaxCount());
            insertStack.setCount(insertStack.getCount() - (insertStack.getMaxCount() - newOriginalItemCount));
        }, () -> player.getInventory().offerOrDrop(insertStack));
    }

    private static void setCount(PlayerEntity player, ItemStack insertStack, NbtCompound shulkerBoxNbt, int slot) {
        shulkerBoxNbt.getCompound(TAG).getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE).stream().filter(nbtElement1 -> nbtElement1 instanceof NbtCompound nbtCompound1 && nbtCompound1.getInt(SLOT) == slot).findFirst().ifPresentOrElse(nbtElement1 -> {
            NbtCompound originalItemNbt = (NbtCompound) nbtElement1;
            originalItemNbt.putByte(COUNT, (byte) (originalItemNbt.getByte(COUNT) + insertStack.getCount()));
            insertStack.setCount(0);
        }, () -> player.getInventory().offerOrDrop(insertStack));
    }

    @NotNull
    private static NbtCompound getNbtCopyWithoutSlotAndCount(NbtCompound nbt) {
        NbtCompound copy = nbt.copy();
        copy.remove(SLOT);
        copy.remove(COUNT);
        return copy;
    }

}