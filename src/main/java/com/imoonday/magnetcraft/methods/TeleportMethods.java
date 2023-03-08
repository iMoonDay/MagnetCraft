package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.special.CustomStatRegistries;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class TeleportMethods {


    public static void teleportSurroundingItemEntitiesToPlayer(World world, PlayerEntity player, double dis, Hand hand) {
        boolean message = ModConfig.getConfig().displayMessageFeedback;
        int magnetHandSpacing = ModConfig.getConfig().value.magnetHandSpacing;
        boolean emptyDamage = DamageMethods.isEmptyDamage(player, hand);
        boolean mainhand = hand == Hand.MAIN_HAND;
        if (emptyDamage) return;
        if (mainhand) dis += magnetHandSpacing;
        double finalDis = dis;
        int count = player.world.getOtherEntities(player, player.getBoundingBox().expand(dis), e -> (e instanceof ItemEntity || e instanceof ExperienceOrbEntity) && e.distanceTo(player) <= finalDis).size();
        player.world.getOtherEntities(player, player.getBoundingBox().expand(dis), e -> (e instanceof ItemEntity || e instanceof ExperienceOrbEntity) && e.distanceTo(player) <= finalDis).forEach(e -> {
            boolean isExp = e instanceof ExperienceOrbEntity;
            DamageMethods.addDamage(player, hand, 1, true);
            if (isExp) {
                int amount = ((ExperienceOrbEntity) e).getExperienceAmount();
                player.addExperience(amount);
            } else {
                player.getInventory().offerOrDrop(((ItemEntity) e).getStack());
                e.kill();
            }
            player.incrementStat(CustomStatRegistries.ITEMS_TELEPORTED_TO_PLAYER);
            player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1, 1);

        });
        String text = count > 0 ? "text.magnetcraft.message.teleport.tooltip.1" : "text.magnetcraft.message.teleport.tooltip.2";
        if (!world.isClient && message) {
            player.sendMessage(Text.translatable(text, dis, count));
        }
    }
}