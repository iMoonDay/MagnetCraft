package com.imoonday.magnetcraft.events;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.CustomStatRegistries;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class TeleportEvent {

    public static void teleportItems(World world, PlayerEntity entity, double dis, Hand hand) {

        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        boolean emptyDamage = NbtEvent.checkEmptyDamage(entity, hand);
        boolean mainhand = hand == Hand.MAIN_HAND;

        String feedback;

        if (emptyDamage) return;
        if (mainhand) dis += config.value.magnetHandSpacing;//主副手范围差距

        double finalDis = dis;

        int count = entity.getWorld().getOtherEntities(entity, new Box(
                                entity.getPos().getX() + dis,
                                entity.getPos().getY() + dis,
                                entity.getPos().getZ() + dis,
                                entity.getPos().getX() - dis,
                                entity.getPos().getY() - dis,
                                entity.getPos().getZ() - dis),
                        e -> (e instanceof ItemEntity || e instanceof ExperienceOrbEntity) && e.distanceTo(entity) <= finalDis)
                .size();

        if (count != 0) {
            feedback = "在 " + dis + " 格范围内捡起了 " + count + " 个物品/经验";
        } else {
            feedback = "在 " + dis + " 格范围内没有寻找到物品/经验";
        }

        entity.getWorld().getOtherEntities(
                        entity, new Box(
                                entity.getPos().getX() + dis,
                                entity.getPos().getY() + dis,
                                entity.getPos().getZ() + dis,
                                entity.getPos().getX() - dis,
                                entity.getPos().getY() - dis,
                                entity.getPos().getZ() - dis),
                        e -> (e instanceof ItemEntity || e instanceof ExperienceOrbEntity) && e.distanceTo(entity) <= finalDis)
                .forEach(e -> {
                    NbtEvent.addDamage(entity, hand, 1);

                    if (e instanceof ExperienceOrbEntity) {
                        e.teleport(
                                entity.getPos().getX(),
                                entity.getPos().getY() + 1,
                                entity.getPos().getZ());
                    } else {
                        if (entity.getInventory().getEmptySlot() != -1) {
                            entity.giveItemStack(((ItemEntity) e).getStack());
                            e.kill();
                        } else {
                            e.teleport(
                                    entity.getPos().getX(),
                                    entity.getPos().getY() + 1,
                                    entity.getPos().getZ());
                        }
                    }
                    entity.incrementStat(CustomStatRegistries.ITEMS_TELEPORTED_TO_PLAYER);
                    entity.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1, 1);
                });
        if (!world.isClient) entity.sendMessage(Text.literal(feedback));
    }
}