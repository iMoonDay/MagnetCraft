package com.imoonday.magnetcraft.common.items;

import com.imoonday.magnetcraft.methods.DamageMethods;
import com.imoonday.magnetcraft.methods.TeleportMethods;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.imoonday.magnetcraft.common.tags.BlockTags.MAGNETITE_ORES;
import static net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags.ORES;
import static net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags.QUARTZ_ORES;
import static net.minecraft.registry.tag.BlockTags.*;

public class MineralMagnetItem extends Item {
    public MineralMagnetItem(Settings settings) {
        super(settings);
    }

    public static void register() {
        ModelPredicateProviderRegistry.register(ItemRegistries.MINERAL_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (livingEntity instanceof PlayerEntity && ((PlayerEntity) livingEntity).getItemCooldownManager().isCoolingDown(ItemRegistries.MINERAL_MAGNET_ITEM)) {
                return 0.0F;
            }
            return DamageMethods.isEmptyDamage(itemStack) ? 0.0F : 1.0F;
        });
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        List<NbtElement> list = stack.getOrCreateNbt().getList("Cores", NbtString.STRING_TYPE).stream().toList();
        if (list.isEmpty()) {
            tooltip.add(Text.literal("空").formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        } else {
            for (NbtElement nbtElement : list) {
                String name = nbtElement.toString().replace("'", "").replace("\"", "");
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
        coresSet(stack);
        return stack;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.EMERALD) || super.canRepair(stack, ingredient);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        coresCheck(stack);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!DamageMethods.isEmptyDamage(user, hand)) {
            int value = searchMineral(user, hand);
            boolean success = value > 0;
            if (success && !user.isCreative()) {
                user.getItemCooldownManager().set(this, value * 20);
            } else {
                user.getItemCooldownManager().set(this, 20);
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        super.inventoryTick(stack, world, user, slot, selected);
        coresCheck(stack);
    }

    public static int searchMineral(PlayerEntity player, Hand hand) {
        String string;
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
            int value = 1;
            boolean isEmptyDamage = false;
            DamageMethods.addDamage(player, hand, 1);
            if (player.experienceLevel < 5 && !player.isCreative()) {
                string = "等级不足(至少5级)";
                player.sendMessage(Text.literal(string), true);
                return 0;
            }
            for (int x = -15; x <= 15; x++) {
                if (DamageMethods.isEmptyDamage(player, hand)) {
                    isEmptyDamage = true;
                    break;
                }
                for (int y = -15; y <= 15; y++) {
                    if (DamageMethods.isEmptyDamage(player, hand)) {
                        isEmptyDamage = true;
                        break;
                    }
                    for (int z = -15; z <= 15; z++) {
                        if (DamageMethods.isEmptyDamage(player, hand)) {
                            isEmptyDamage = true;
                            break;
                        }
                        BlockPos pos = player.getBlockPos().add(x, y, z);
                        BlockState state = player.world.getBlockState(pos);
                        ServerWorld world = (ServerWorld) player.world;
                        BlockEntity blockEntity = world.getBlockEntity(pos);
                        List<ItemStack> droppedStacks = Block.getDroppedStacks(state, world, pos, blockEntity, player, Items.IRON_PICKAXE.getDefaultStack());
                        final boolean[] nbtPass = {false};
                        droppedStacks.forEach(e -> {
                            String stackName = Registries.ITEM.getId(e.getItem()).toString();
                            NbtString nbtString = NbtString.of(NbtString.escape(stackName));
                            if (!nbtPass[0]) {
                                nbtPass[0] = player.getStackInHand(hand).getOrCreateNbt().getList("Cores", NbtString.STRING_TYPE).contains(nbtString);
                            }
                        });
                        if (state.isIn(ORES) && nbtPass[0]) {
                            droppedStacks.forEach(e -> TeleportMethods.giveItemStackToPlayer(player.world, player, e));
                            player.world.breakBlock(pos, false, player);
                            if (state.isIn(COAL_ORES)) {
                                coal++;
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
                            }
                            total++;
                            totalValue += value;
                            DamageMethods.addDamage(player, hand, value);
                        }
                    }
                }
            }
            if (!player.isCreative() && totalValue > 0) {
                player.addExperienceLevels(-5);
            }
            if (isEmptyDamage) {
                string = "耐久不足,搜寻矿物被迫终止";
                player.sendMessage(Text.literal(string));
            }
            string = "一共发现了 " + total + " 个矿石";
            player.sendMessage(Text.literal(string));
            if (coal > 0) {
                string = coal + " 个煤矿石";
                player.sendMessage(Text.literal(string));
            }
            if (iron > 0) {
                string = iron + " 个铁矿石";
                player.sendMessage(Text.literal(string));
            }
            if (gold > 0) {
                string = gold + " 个金矿石";
                player.sendMessage(Text.literal(string));
            }
            if (diamond > 0) {
                string = diamond + " 个钻石矿石";
                player.sendMessage(Text.literal(string));
            }
            if (redstone > 0) {
                string = redstone + " 个红石矿石";
                player.sendMessage(Text.literal(string));
            }
            if (copper > 0) {
                string = copper + " 个铜矿石";
                player.sendMessage(Text.literal(string));
            }
            if (emerald > 0) {
                string = emerald + " 个绿宝石矿石";
                player.sendMessage(Text.literal(string));
            }
            if (lapis > 0) {
                string = lapis + " 个青金石矿石";
                player.sendMessage(Text.literal(string));
            }
            if (quartz > 0) {
                string = quartz + " 个石英矿石";
                player.sendMessage(Text.literal(string));
            }
            if (magnetite > 0) {
                string = magnetite + " 个磁铁矿石";
                player.sendMessage(Text.literal(string));
            }
            if (others > 0) {
                string = others + " 个非原版矿石";
                player.sendMessage(Text.literal(string));
            }
        }
        return totalValue;
    }

    public static void coresCheck(ItemStack stack) {
        if (stack.getNbt() == null || !stack.getNbt().contains("Cores")) {
            coresSet(stack);
        }
    }

    public static void coresSet(ItemStack stack) {
        Item[] items = new Item[]{};
        coresSet(stack, items);
    }

    public static void coresSet(ItemStack stack, Item[] items) {
        NbtList list = stack.getOrCreateNbt().getList("Cores",NbtElement.STRING_TYPE);
        String[] names = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            names[i] = Registries.ITEM.getId(items[i]).toString();
            if (!list.contains(NbtString.of(NbtString.escape(names[i])))) {
                list.add(NbtString.of(NbtString.escape(names[i])));
            }
        }
        stack.getOrCreateNbt().put("Cores", list);
    }

    public static ItemStack getAllCoresStack() {
        ItemStack stack = new ItemStack(ItemRegistries.MINERAL_MAGNET_ITEM);
        Item[] items = new Item[]{Items.COAL, Items.RAW_IRON, Items.RAW_GOLD, Items.GOLD_NUGGET, Items.DIAMOND, Items.REDSTONE, Items.RAW_COPPER, Items.EMERALD, Items.LAPIS_LAZULI, Items.QUARTZ, ItemRegistries.RAW_MAGNET_ITEM};
        coresSet(stack, items);
        return stack;
    }

}
