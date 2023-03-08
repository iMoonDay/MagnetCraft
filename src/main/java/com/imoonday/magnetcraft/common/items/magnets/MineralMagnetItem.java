package com.imoonday.magnetcraft.common.items.magnets;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.DamageMethods;
import com.imoonday.magnetcraft.screen.handler.MineralMagnetScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.imoonday.magnetcraft.common.tags.BlockTags.MAGNETITE_ORES;
import static com.imoonday.magnetcraft.registries.common.ItemRegistries.*;
import static net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags.ORES;
import static net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags.QUARTZ_ORES;
import static net.minecraft.item.Items.*;
import static net.minecraft.registry.tag.BlockTags.*;

public class MineralMagnetItem extends Item {

    public MineralMagnetItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(MINERAL_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (livingEntity instanceof PlayerEntity && ((PlayerEntity) livingEntity).getItemCooldownManager().isCoolingDown(MINERAL_MAGNET_ITEM)) {
                return 0.0F;
            }
            return DamageMethods.isEmptyDamage(itemStack) ? 0.0F : 1.0F;
        });
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < stack.getOrCreateNbt().getList("Cores", NbtElement.COMPOUND_TYPE).size(); i++) {
            if (stack.getOrCreateNbt().getList("Cores", NbtString.COMPOUND_TYPE).getCompound(i).getBoolean("enable")) {
                list.add(stack.getOrCreateNbt().getList("Cores", NbtString.COMPOUND_TYPE).getCompound(i).getString("id"));
            }
        }
        if (list.isEmpty()) {
            tooltip.add(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.1").formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        } else {
            for (String name : list) {
                Identifier identifier = Identifier.tryParse(name);
                if (identifier != null) {
                    String stackName = "item." + identifier.toTranslationKey();
                    tooltip.add(Text.translatable(stackName).formatted(Formatting.GRAY).formatted(Formatting.BOLD));
                }
            }
        }
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        stack.getOrCreateNbt().putBoolean("Filterable", true);
        coresSet(stack);
        return stack;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(EMERALD) || super.canRepair(stack, ingredient);
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
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.getStackInHand(hand).getOrCreateNbt().getBoolean("Filterable") && user.isSneaky() && !user.getAbilities().flying) {
            if (!user.world.isClient) {
                int slot = hand == Hand.MAIN_HAND ? user.getInventory().selectedSlot : -1;
                user.openHandledScreen(new ExtendedScreenHandlerFactory() {
                    @Override
                    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf buf) {
                        buf.writeInt(slot);
                    }

                    @Override
                    public Text getDisplayName() {
                        return Text.translatable(user.getStackInHand(hand).getItem().getTranslationKey());
                    }

                    @Override
                    public @me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                        return new MineralMagnetScreenHandler(syncId, inv, slot);
                    }
                });
            }
        } else {
            if (!DamageMethods.isEmptyDamage(user, hand)) {
                int value = searchMineral(user, hand);
                boolean success = value > 0;
                if (success && !user.isCreative()) {
                    user.getItemCooldownManager().set(this, value * 20);
                } else {
                    user.getItemCooldownManager().set(this, 20);
                }
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        super.inventoryTick(stack, world, user, slot, selected);
        coresCheck(stack);
        if (user instanceof PlayerEntity && user.isSpectator() && ((PlayerEntity) user).getItemCooldownManager().isCoolingDown(this)) {
            ((PlayerEntity) user).getItemCooldownManager().remove(this);
        }
    }

    public static int searchMineral(PlayerEntity player, Hand hand) {
        int requiredExperienceLevel = ModConfig.getConfig().value.requiredExperienceLevel;
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
            DamageMethods.addDamage(player, hand, 1,false);
            if (player.experienceLevel < requiredExperienceLevel && !player.isCreative()) {
                player.sendMessage(Text.translatable("item.magnetcraft.mineral_magnet.tooltip.2"), true);
                return 0;
            }
            for (int x = -10; x <= 10; x++) {
                if (DamageMethods.isEmptyDamage(player, hand)) {
                    isEmptyDamage = true;
                    break;
                }
                for (int y = -10; y <= 10; y++) {
                    if (DamageMethods.isEmptyDamage(player, hand)) {
                        isEmptyDamage = true;
                        break;
                    }
                    for (int z = -10; z <= 10; z++) {
                        if (DamageMethods.isEmptyDamage(player, hand)) {
                            isEmptyDamage = true;
                            break;
                        }
                        BlockPos pos = player.getBlockPos().add(x, y, z);
                        BlockState state = player.world.getBlockState(pos);
                        ServerWorld world = (ServerWorld) player.world;
                        BlockEntity blockEntity = world.getBlockEntity(pos);
                        List<ItemStack> droppedStacks = Block.getDroppedStacks(state, world, pos, blockEntity, player, IRON_PICKAXE.getDefaultStack());
                        boolean nbtPass = droppedStacks.stream().anyMatch(e -> (player.getStackInHand(hand).getOrCreateNbt().getList("Cores", NbtString.COMPOUND_TYPE).stream().anyMatch(nbtElement -> nbtElement instanceof NbtCompound && ((NbtCompound) nbtElement).getString("id").equals(Registries.ITEM.getId(e.getItem()).toString()) && ((NbtCompound) nbtElement).getBoolean("enable"))));
                        if (state.isIn(ORES) && nbtPass) {
                            droppedStacks.forEach(e -> player.getInventory().offerOrDrop(e));
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
                            DamageMethods.addDamage(player, hand, value,true);
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
        if (stack.getNbt() == null || !stack.getNbt().contains("Cores", NbtElement.LIST_TYPE)) {
            coresSet(stack);
        }
        if (!stack.getOrCreateNbt().contains("Filterable")) {
            stack.getOrCreateNbt().putBoolean("Filterable", false);
        }
    }

    public static void coresSet(ItemStack stack) {
        Item[] items = new Item[]{};
        coresSet(stack, items);
    }

    public static void coresSet(ItemStack stack, Item[] items) {
        NbtList list = stack.getOrCreateNbt().getList("Cores", NbtElement.COMPOUND_TYPE);
        String[] names = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            names[i] = Registries.ITEM.getId(items[i]).toString();
            NbtCompound nbt = new NbtCompound();
            nbt.putString("id", names[i]);
            nbt.putBoolean("enable", true);
            boolean exist = false;
            for (int j = 0; j < list.size(); j++) {
                exist = list.getCompound(j).getString("id").equals(names[i]);
                if (exist) {
                    break;
                }
            }
            if (!exist) {
                list.add(nbt);
            }
        }
        stack.getOrCreateNbt().put("Cores", list);
    }

    public static ItemStack getAllCoresStack() {
        ItemStack stack = new ItemStack(MINERAL_MAGNET_ITEM);
        Item[] items = new Item[]{COAL, RAW_IRON, RAW_GOLD, GOLD_NUGGET, DIAMOND, REDSTONE, RAW_COPPER, EMERALD, LAPIS_LAZULI, QUARTZ, RAW_MAGNET_ITEM};
        coresSet(stack, items);
        stack.getOrCreateNbt().putBoolean("Filterable", true);
        return stack;
    }

    public static void changeCoreEnable(ItemStack stack, String id) {
        if (stack.getOrCreateNbt().getList("Cores", NbtElement.COMPOUND_TYPE).stream().anyMatch(nbtElement -> nbtElement instanceof NbtCompound && ((NbtCompound) nbtElement).getString("id").equals(id))) {
            stack.getOrCreateNbt().getList("Cores", NbtElement.COMPOUND_TYPE).stream().filter(nbtElement -> nbtElement instanceof NbtCompound && ((NbtCompound) nbtElement).getString("id").equals(id)).forEach(nbtElement -> ((NbtCompound) nbtElement).putBoolean("enable", !((NbtCompound) nbtElement).getBoolean("enable")));
        }
    }

    public static void changeAllCoreEnable(ItemStack stack,boolean enable){
        stack.getOrCreateNbt().getList("Cores", NbtElement.COMPOUND_TYPE).forEach(nbtElement -> ((NbtCompound) nbtElement).putBoolean("enable", enable));
    }

}
