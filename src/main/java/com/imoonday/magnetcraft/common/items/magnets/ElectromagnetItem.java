package com.imoonday.magnetcraft.common.items.magnets;

import com.imoonday.magnetcraft.api.FilterableItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.CooldownMethods;
import com.imoonday.magnetcraft.methods.DamageMethods;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import com.imoonday.magnetcraft.registries.special.CustomStatRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class ElectromagnetItem extends FilterableItem {

    public ElectromagnetItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.ELECTROMAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (itemStack.getNbt() == null || !itemStack.getNbt().contains("Enable")) return 0.0F;
            return itemStack.getOrCreateNbt().getBoolean("Enable") ? 1.0F : 0.0F;
        });
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.ENDER_PEARL) || super.canRepair(stack, ingredient);
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
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        boolean sneakToSwitch = ModConfig.getConfig().enableSneakToSwitch;
        boolean reversal = ModConfig.getConfig().rightClickReversal;
        double dis = ModConfig.getConfig().value.electromagnetTeleportMinDis;
        boolean sneaking = user.isSneaking();
        if (sneaking && user.getAbilities().flying) {
            sneaking = false;
        }
        if ((sneaking && !reversal) || (!sneaking && reversal)) {
            if (user.getStackInHand(hand).getOrCreateNbt().getBoolean("Filterable")) {
                if (!user.world.isClient) {
                    openScreen(user, hand, this);
                }
            } else {
                if (!sneakToSwitch) {
                    return super.use(world, user, hand);
                }
                enabledSwitch(world, user, hand);
            }
        } else if (!DamageMethods.isEmptyDamage(user, hand)) {
            if (!world.isClient) {
                DamageMethods.addDamage(user, hand, 1, false);
            }
            teleportItems(world, user, dis, hand);
        }
        CooldownMethods.setCooldown(user, user.getStackInHand(hand), 20);
        return super.use(world, user, hand);
    }

    public static void teleportItems(World world, PlayerEntity player, double dis, Hand hand) {
        boolean message = ModConfig.getConfig().displayMessageFeedback;
        int magnetHandSpacing = ModConfig.getConfig().value.magnetHandSpacing;
        if (DamageMethods.isEmptyDamage(player, hand)) return;
        if (hand == Hand.MAIN_HAND) dis += magnetHandSpacing;
        double finalDis = dis;
        int count = player.world.getOtherEntities(player, player.getBoundingBox().expand(dis), targetEntity -> (targetEntity instanceof ItemEntity || targetEntity instanceof ExperienceOrbEntity) && targetEntity.getPos().isInRange(player.getPos(), finalDis)).size();
        player.world.getOtherEntities(player, player.getBoundingBox().expand(dis), targetEntity -> (targetEntity instanceof ItemEntity || targetEntity instanceof ExperienceOrbEntity) && targetEntity.getPos().isInRange(player.getPos(), finalDis)).forEach(targetEntity -> {
            DamageMethods.addDamage(player, hand, 1, true);
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
        String text = count > 0 ? "text.magnetcraft.message.teleport.tooltip.1" : "text.magnetcraft.message.teleport.tooltip.2";
        if (!world.isClient && message) {
            player.sendMessage(Text.translatable(text, dis, count));
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
        List<NbtElement> shulkerBoxList = handStackNbt.getList("ShulkerBox", NbtElement.COMPOUND_TYPE).stream().filter(nbtElement -> nbtElement instanceof NbtCompound boxNbt && !boxNbt.getString("id").equals("minecraft:air")).toList();
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
                NbtList items = shulkerBoxNbt.getCompound("tag").getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE).isEmpty() ? new NbtList() : shulkerBoxNbt.getCompound("tag").getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE).copy();
                NbtCompound insertStackNbt = insertStack.writeNbt(new NbtCompound());
                ArrayList<NbtCompound> newOriginalItemNbt = new ArrayList<>();
                ArrayList<Integer> itemSlots = new ArrayList<>();
                int slot = -1;
                if (shulkerBoxNbt.getCompound("tag").getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE).isEmpty()) {
                    insertStackNbt.putByte("Slot", (byte) 0);
                } else {
                    shulkerBoxNbt.getCompound("tag").getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE).forEach(nbtElement1 -> {
                        NbtCompound originaItemNbt = (NbtCompound) nbtElement1;
                        NbtCompound originalItemNbtCopy = originaItemNbt.copy();
                        newOriginalItemNbt.add(originalItemNbtCopy);
                    });
                    for (NbtCompound newOriginalItem : newOriginalItemNbt) {
                        itemSlots.add(newOriginalItem.getInt("Slot"));
                    }
                    List<Integer> emptySlots = new ArrayList<>(IntStream.range(0, 27).boxed().toList());
                    emptySlots.removeAll(itemSlots);
                    if (!emptySlots.isEmpty()) {
                        slot = Collections.min(emptySlots);
                        insertStackNbt.putByte("Slot", (byte) slot);
                    }
                }
                items.add(insertStackNbt);
                NbtCompound blockEntityTag = new NbtCompound();
                blockEntityTag.put("Items", items);
                blockEntityTag.putString("id", "minecraft:shulker_box");
                NbtCompound tag = new NbtCompound();
                tag.put("BlockEntityTag", blockEntityTag);
                if (!shulkerBoxNbt.contains("tag")) {
                    shulkerBoxNbt.put("tag", tag);
                    break;
                } else if (!shulkerBoxNbt.getCompound("tag").contains("BlockEntityTag")) {
                    shulkerBoxNbt.getCompound("tag").put("BlockEntityTag", blockEntityTag);
                    break;
                } else if (!shulkerBoxNbt.getCompound("tag").getCompound("BlockEntityTag").contains("Items") || shulkerBoxNbt.getCompound("tag").getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE).isEmpty()) {
                    shulkerBoxNbt.getCompound("tag").getCompound("BlockEntityTag").put("Items", items);
                    break;
                } else {
                    for (NbtCompound newOriginalItem : newOriginalItemNbt) {
                        int newOriginalItemSlot = newOriginalItem.getInt("Slot");
                        int newOriginalItemCount = newOriginalItem.getInt("Count");
                        NbtCompound newOriginalItemCopy = newOriginalItem.copy();
                        NbtCompound insertStackNbtCopy = insertStackNbt.copy();
                        newOriginalItemCopy.remove("Slot");
                        newOriginalItemCopy.remove("Count");
                        insertStackNbtCopy.remove("Slot");
                        insertStackNbtCopy.remove("Count");
                        if (newOriginalItemCopy.equals(insertStackNbtCopy) && insertStack.isStackable()) {
                            if (insertStack.getCount() + newOriginalItemCount <= insertStack.getMaxCount()) {
                                shulkerBoxNbt.getCompound("tag").getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE).stream().filter(nbtElement1 -> nbtElement1 instanceof NbtCompound nbtCompound1 && nbtCompound1.getInt("Slot") == newOriginalItemSlot).findFirst().ifPresentOrElse(nbtElement1 -> {
                                    NbtCompound originalItemNbt = (NbtCompound) nbtElement1;
                                    originalItemNbt.putByte("Count", (byte) (originalItemNbt.getByte("Count") + insertStack.getCount()));
                                    insertStack.setCount(0);
                                }, () -> player.getInventory().offerOrDrop(insertStack));
                                break;
                            } else {
                                shulkerBoxNbt.getCompound("tag").getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE).stream().filter(nbtElement1 -> nbtElement1 instanceof NbtCompound nbtCompound1 && nbtCompound1.getInt("Slot") == newOriginalItemSlot).findFirst().ifPresentOrElse(nbtElement1 -> {
                                    NbtCompound originalItemNbt = (NbtCompound) nbtElement1;
                                    originalItemNbt.putByte("Count", (byte) insertStack.getMaxCount());
                                    insertStack.setCount(insertStack.getCount() - (insertStack.getMaxCount() - newOriginalItemCount));
                                }, () -> player.getInventory().offerOrDrop(insertStack));
                            }
                        }
                    }
                    if (insertStack.getCount() > 0) {
                        if (slot != -1) {
                            insertStackNbt.putByte("Count", (byte) insertStack.getCount());
                            shulkerBoxNbt.getCompound("tag").getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE).add(insertStackNbt);
                            insertStack.setCount(0);
                        } else {
                            if (++currentIndex > boxes) {
                                player.getInventory().offerOrDrop(insertStack);
                            }
                        }
                    }
                }
            } else {
                player.getInventory().offerOrDrop(insertStack);
            }
        }
    }

}