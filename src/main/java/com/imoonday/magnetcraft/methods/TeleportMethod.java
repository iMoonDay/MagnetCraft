package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.special.CustomStatRegistries;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class TeleportMethod {

    public static void teleportItems(World world, PlayerEntity entity, double dis, Hand hand) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        boolean emptyDamage = NbtClassMethod.checkEmptyDamage(entity, hand);
        boolean mainhand = hand == Hand.MAIN_HAND;
        boolean message = config.displayQuantityFeedback;
        boolean hasSlot = entity.getInventory().getEmptySlot() != -1;
        boolean client = world.isClient;
        if (emptyDamage) return;
        if (mainhand) dis += config.value.magnetHandSpacing;
        double finalDis = dis;
        int count = entity.getWorld().getOtherEntities(entity, new Box(entity.getPos().getX() + dis, entity.getPos().getY() + dis, entity.getPos().getZ() + dis, entity.getPos().getX() - dis, entity.getPos().getY() - dis, entity.getPos().getZ() - dis), e -> (e instanceof ItemEntity || e instanceof ExperienceOrbEntity) && e.distanceTo(entity) <= finalDis).size();
        String text;
        entity.getWorld().getOtherEntities(entity, new Box(entity.getPos().getX() + dis, entity.getPos().getY() + dis, entity.getPos().getZ() + dis, entity.getPos().getX() - dis, entity.getPos().getY() - dis, entity.getPos().getZ() - dis), e -> (e instanceof ItemEntity || e instanceof ExperienceOrbEntity) && e.distanceTo(entity) <= finalDis).forEach(e -> {
            boolean isExp = e instanceof ExperienceOrbEntity;
            NbtClassMethod.addDamage(entity, hand, 1);
            if (hasSlot && !isExp) {
                entity.giveItemStack(((ItemEntity) e).getStack());
                e.kill();
            } else {
                e.teleport(entity.getPos().getX(), entity.getPos().getY() + 1, entity.getPos().getZ());
            }
            entity.incrementStat(CustomStatRegistries.ITEMS_TELEPORTED_TO_PLAYER);
            entity.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1, 1);
        });
        if (count > 0) {
            text = "[磁铁]: 在 " + dis + " 格范围内捡起了 " + count + " 个物品/经验";
        } else {
            text = "[磁铁]: 在 " + dis + " 格范围内没有寻找到物品/经验";
        }
        if (!client && message) entity.sendMessage(Text.literal(text));
    }
}