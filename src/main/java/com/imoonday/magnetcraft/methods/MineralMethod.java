package com.imoonday.magnetcraft.methods;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static com.imoonday.magnetcraft.common.tags.BlockTags.MAGNETITE_ORES;
import static net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags.ORES;
import static net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags.QUARTZ_ORES;
import static net.minecraft.registry.tag.BlockTags.*;

public class MineralMethod {
    public static int SearchMineral(PlayerEntity player, Hand hand) {
        int total = 0;
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
            if (NbtClassMethod.isEmptyDamage(player, hand)) {
                return total;
            }
            if (player.experienceLevel < 5 && !player.isCreative()) {
                return total;
            }
            for (int x = -15; x <= 15; x++) {
                if (NbtClassMethod.isEmptyDamage(player, hand)) {
                    break;
                }
                for (int y = -15; y <= 15; y++) {
                    if (NbtClassMethod.isEmptyDamage(player, hand)) {
                        break;
                    }
                    for (int z = -15; z <= 15; z++) {
                        if (NbtClassMethod.isEmptyDamage(player, hand)) {
                            break;
                        }
                        BlockPos pos = player.getBlockPos().add(x, y, z);
                        BlockState state = player.world.getBlockState(pos);
                        ServerWorld world = (ServerWorld) player.world;
                        BlockEntity blockEntity = world.getBlockEntity(pos);
                        Direction direction = player.getMovementDirection();
                        if (state.isIn(ORES)) {
                            Block.getDroppedStacks(state, world, pos, blockEntity, player, Items.IRON_PICKAXE.getDefaultStack()).forEach(e -> {
                                TeleportMethod.giveItemStackToPlayer(player.world, player, e);
                            });
                            player.world.breakBlock(pos, false, player);
                            total++;
                            if (state.isIn(COAL_ORES)) {
                                coal++;
                            } else if (state.isIn(IRON_ORES)) {
                                iron++;
                            } else if (state.isIn(GOLD_ORES)) {
                                gold++;
                            } else if (state.isIn(DIAMOND_ORES)) {
                                diamond++;
                            } else if (state.isIn(REDSTONE_ORES)) {
                                redstone++;
                            } else if (state.isIn(COPPER_ORES)) {
                                copper++;
                            } else if (state.isIn(EMERALD_ORES)) {
                                emerald++;
                            } else if (state.isIn(LAPIS_ORES)) {
                                lapis++;
                            } else if (state.isIn(QUARTZ_ORES)) {
                                quartz++;
                            } else if (state.isIn(MAGNETITE_ORES)) {
                                magnetite++;
                            } else {
                                others++;
                            }
                            NbtClassMethod.addDamage(player, hand, 1);
                        }
                    }
                }
            }
            if (!player.isCreative()) {
                player.addExperienceLevels(-5);
            }
            String string = "一共发现了 " + total + " 个矿石";
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
                string = others + " 个未知矿石";
                player.sendMessage(Text.literal(string));
            }
        }
        return total;
    }
}
