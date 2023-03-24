package com.imoonday.magnetcraft.common.items.magnets;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.screen.handler.MineralMagnetScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.imoonday.magnetcraft.api.AbstractFilterableItem.shulkerBoxCheck;
import static com.imoonday.magnetcraft.api.AbstractFilterableItem.shulkerBoxSet;
import static com.imoonday.magnetcraft.common.items.magnets.ElectromagnetItem.tryInsertIntoShulkerBox;
import static com.imoonday.magnetcraft.common.tags.BlockTags.MAGNETITE_ORES;
import static com.imoonday.magnetcraft.registries.common.ItemRegistries.*;
import static net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags.ORES;
import static net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags.QUARTZ_ORES;
import static net.minecraft.item.Items.*;
import static net.minecraft.registry.tag.BlockTags.*;

/**
 * @author iMoonDay
 */
public class MineralMagnetItem extends Item {

    public static final String CORES = "Cores";
    public static final String FILTERABLE = "Filterable";
    public static final String ID = "id";
    public static final String ENABLE = "enable";

    public MineralMagnetItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(MINERAL_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> livingEntity instanceof PlayerEntity player && player.getItemCooldownManager().isCoolingDown(MINERAL_MAGNET_ITEM) ? 0.0F : itemStack.isBroken() ? 0.0F : 1.0F);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        ArrayList<String> list = IntStream.range(0, stack.getOrCreateNbt().getList(CORES, NbtElement.COMPOUND_TYPE).size()).filter(i -> stack.getOrCreateNbt().getList(CORES, NbtString.COMPOUND_TYPE).getCompound(i).getBoolean(ENABLE)).mapToObj(i -> stack.getOrCreateNbt().getList(CORES, NbtString.COMPOUND_TYPE).getCompound(i).getString(ID)).collect(Collectors.toCollection(ArrayList::new));
        if (list.isEmpty()) {
            tooltip.add(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.1").formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        } else {
            list.stream().map(Identifier::tryParse).filter(Objects::nonNull).map(identifier -> "item." + identifier.toTranslationKey()).map(stackName -> Text.translatable(stackName).formatted(Formatting.GRAY).formatted(Formatting.BOLD)).forEach(tooltip::add);
        }
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        stack.getOrCreateNbt().putBoolean(FILTERABLE, true);
        coresSet(stack);
        shulkerBoxSet(stack);
        return stack;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(EMERALD);
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return new ItemStack(MINERAL_MAGNET_CRAFTING_MODULE_ITEM);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        coresCheck(stack);
        shulkerBoxCheck(stack);
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
        Hand hand = user.getActiveHand();
        if (!user.isBroken(hand)) {
            int value = searchMineral(player, hand);
            boolean success = value > 0;
            user.setCooldown(stack, success && !player.isCreative() ? value * 20 : 20);
        }
        return stack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stackInHand = user.getStackInHand(hand);
        if (stackInHand.getOrCreateNbt().getBoolean(FILTERABLE) && user.isSneaky() && !user.getAbilities().flying) {
            if (!user.world.isClient) {
                int slot = hand == Hand.MAIN_HAND ? user.getInventory().selectedSlot : -1;
                user.openHandledScreen(new ExtendedScreenHandlerFactory() {
                    @Override
                    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf buf) {
                        buf.writeInt(slot);
                    }

                    @Override
                    public Text getDisplayName() {
                        return Text.translatable(stackInHand.getItem().getTranslationKey());
                    }

                    @Override
                    public @me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                        return new MineralMagnetScreenHandler(syncId, inv, slot);
                    }
                });
            }
        } else {
            if (user.getAbilities().creativeMode || !stackInHand.isBroken()) {
                user.setCurrentHand(hand);
            }
            return TypedActionResult.fail(stackInHand);
        }
        return TypedActionResult.success(stackInHand,!stackInHand.isBroken());
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        super.inventoryTick(stack, world, user, slot, selected);
        coresCheck(stack);
        shulkerBoxCheck(stack);
        if (user instanceof PlayerEntity player && user.isSpectator() && player.getItemCooldownManager().isCoolingDown(this)) {
            player.getItemCooldownManager().remove(this);
        }
    }

    public static int searchMineral(PlayerEntity player, Hand hand) {
        int requiredExperienceLevel = ModConfig.getValue().requiredExperienceLevel;
        int total = 0;
        int totalValue = 0;
        if (!player.world.isClient) {
            int coal = 0;
            int iron = 0;
            int gold = 0;
            int diamond = 0;
            int redstone = 0;
            int copper = 0;
            int emerald = 0;
            int lapis = 0;
            int quartz = 0;
            int magnetite = 0;
            int others = 0;
            int value;
            boolean isEmptyDamage = false;
            player.addDamage(hand, 1, false);
            if (player.experienceLevel < requiredExperienceLevel && !player.isCreative()) {
                player.sendMessage(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.2"), true);
                return 0;
            }
            for (int x = -10; x <= 10; x++) {
                if (player.isBroken(hand)) {
                    isEmptyDamage = true;
                    break;
                }
                for (int y = -10; y <= 10; y++) {
                    if (player.isBroken(hand)) {
                        isEmptyDamage = true;
                        break;
                    }
                    for (int z = -10; z <= 10; z++) {
                        if (player.isBroken(hand)) {
                            isEmptyDamage = true;
                            break;
                        }
                        BlockPos pos = player.getBlockPos().add(x, y, z);
                        BlockState state = player.world.getBlockState(pos);
                        ServerWorld world = (ServerWorld) player.world;
                        BlockEntity blockEntity = world.getBlockEntity(pos);
                        List<ItemStack> droppedStacks = Block.getDroppedStacks(state, world, pos, blockEntity, player, IRON_PICKAXE.getDefaultStack());
                        boolean nbtPass = droppedStacks.stream().anyMatch(stack -> (player.getStackInHand(hand).getOrCreateNbt().getList(CORES, NbtString.COMPOUND_TYPE).stream().anyMatch(nbtElement -> nbtElement instanceof NbtCompound && ((NbtCompound) nbtElement).getString(ID).equals(Registries.ITEM.getId(stack.getItem()).toString()) && ((NbtCompound) nbtElement).getBoolean(ENABLE))));
                        if (state.isIn(ORES) && nbtPass) {
                            droppedStacks.forEach(stack -> tryInsertIntoShulkerBox(player, hand, stack));
                            world.breakBlock(pos, false, player);
                            if (state.isIn(COAL_ORES)) {
                                coal++;
                                value = 1;
                            } else if (state.isIn(IRON_ORES)) {
                                iron++;
                                value = 2;
                            } else if (state.isIn(GOLD_ORES)) {
                                gold++;
                                if (state.isOf(Blocks.NETHER_GOLD_ORE)) {
                                    value = 1;
                                } else {
                                    value = 3;
                                }
                            } else if (state.isIn(DIAMOND_ORES)) {
                                diamond++;
                                value = 5;
                            } else if (state.isIn(REDSTONE_ORES)) {
                                redstone++;
                                value = 2;
                            } else if (state.isIn(COPPER_ORES)) {
                                copper++;
                                value = 2;
                            } else if (state.isIn(EMERALD_ORES)) {
                                emerald++;
                                value = 4;
                            } else if (state.isIn(LAPIS_ORES)) {
                                lapis++;
                                value = 3;
                            } else if (state.isIn(QUARTZ_ORES)) {
                                quartz++;
                                value = 2;
                            } else if (state.isIn(MAGNETITE_ORES)) {
                                magnetite++;
                                value = 2;
                            } else {
                                others++;
                                value = 1;
                            }
                            total++;
                            totalValue += value;
                            player.addDamage(hand, value, true);
                        }
                    }
                }
            }
            if (!player.isCreative() && totalValue > 0) {
                player.addExperienceLevels(-requiredExperienceLevel);
            }
            if (isEmptyDamage) {
                player.sendMessage(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.3", requiredExperienceLevel));
            }
            player.sendMessage(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.4", total));
            if (coal > 0) {
                player.sendMessage(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.6", coal));
            }
            if (iron > 0) {
                player.sendMessage(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.7", iron));
            }
            if (gold > 0) {
                player.sendMessage(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.8", gold));
            }
            if (diamond > 0) {
                player.sendMessage(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.9", diamond));
            }
            if (redstone > 0) {
                player.sendMessage(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.10", redstone));
            }
            if (copper > 0) {
                player.sendMessage(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.11", copper));
            }
            if (emerald > 0) {
                player.sendMessage(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.12", emerald));
            }
            if (lapis > 0) {
                player.sendMessage(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.13", lapis));
            }
            if (quartz > 0) {
                player.sendMessage(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.14", quartz));
            }
            if (magnetite > 0) {
                player.sendMessage(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.15", magnetite));
            }
            if (others > 0) {
                player.sendMessage(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.16", others));
            }
        }
        return totalValue;
    }

    public static void coresCheck(ItemStack stack) {
        if (stack.getNbt() == null || !stack.getNbt().contains(CORES, NbtElement.LIST_TYPE)) {
            coresSet(stack);
        }
        if (!stack.getOrCreateNbt().contains(FILTERABLE)) {
            stack.getOrCreateNbt().putBoolean(FILTERABLE, false);
        }
    }

    public static void coresSet(ItemStack stack) {
        Item[] items = new Item[]{};
        coresSet(stack, items);
    }

    public static void coresSet(ItemStack stack, Item[] items) {
        NbtList list = stack.getOrCreateNbt().getList(CORES, NbtElement.COMPOUND_TYPE);
        String[] names = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            names[i] = Registries.ITEM.getId(items[i]).toString();
            NbtCompound nbt = new NbtCompound();
            nbt.putString(ID, names[i]);
            nbt.putBoolean(ENABLE, true);
            boolean exist = false;
            for (int j = 0; j < list.size(); j++) {
                exist = list.getCompound(j).getString(ID).equals(names[i]);
                if (exist) {
                    break;
                }
            }
            if (!exist) {
                list.add(nbt);
            }
        }
        stack.getOrCreateNbt().put(CORES, list);
    }

    public static ItemStack getAllCoresStack() {
        ItemStack stack = new ItemStack(MINERAL_MAGNET_ITEM);
        Item[] items = new Item[]{COAL, RAW_IRON, RAW_GOLD, GOLD_NUGGET, DIAMOND, REDSTONE, RAW_COPPER, EMERALD, LAPIS_LAZULI, QUARTZ, RAW_MAGNET_ITEM};
        coresSet(stack, items);
        stack.getOrCreateNbt().putBoolean(FILTERABLE, true);
        return stack;
    }

    public static void changeCoreEnable(ItemStack stack, String id) {
        if (stack.getOrCreateNbt().getList(CORES, NbtElement.COMPOUND_TYPE).stream().anyMatch(nbtElement -> nbtElement instanceof NbtCompound && ((NbtCompound) nbtElement).getString(ID).equals(id))) {
            stack.getOrCreateNbt().getList(CORES, NbtElement.COMPOUND_TYPE).stream().filter(nbtElement -> nbtElement instanceof NbtCompound && ((NbtCompound) nbtElement).getString(ID).equals(id)).forEach(nbtElement -> ((NbtCompound) nbtElement).putBoolean(ENABLE, !((NbtCompound) nbtElement).getBoolean(ENABLE)));
        }
    }

    public static void changeAllCoreEnable(ItemStack stack, boolean enable) {
        stack.getOrCreateNbt().getList(CORES, NbtElement.COMPOUND_TYPE).forEach(nbtElement -> ((NbtCompound) nbtElement).putBoolean(ENABLE, enable));
    }

    @Override
    public int getEnchantability() {
        return 16;
    }

}
