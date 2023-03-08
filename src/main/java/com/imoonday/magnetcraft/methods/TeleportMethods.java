package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.special.CustomStatRegistries;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
                giveItemStackToPlayer(player, ((ItemEntity) e).getStack());
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

    public static void giveItemStackToPlayer(PlayerEntity player, ItemStack stack) {
        player.getInventory().offerOrDrop(stack);
//        boolean message = ModConfig.getConfig().displayMessageFeedback;
//        if (!world.isClient && message && !player.isCreative()&&(player.getInventory().getEmptySlot()==-1||!player.getInventory().containsAny(stack1 -> ItemStack.canCombine(stack,stack1)))) {
//            player.sendMessage(Text.translatable("text.magnetcraft.message.inventory_full"));
//        }
//        PlayerInventory inventory = player.getInventory();
//        boolean hasSlot = inventory.getEmptySlot() != -1;
//        ItemEntity itemEntity = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), stack);
//        if (hasSlot) {
//            player.giveItemStack(stack);
//        } else {
//            for (int i = 0; i < inventory.size(); i++) {
//                ItemStack stack1 = inventory.getStack(i);
//                if (ItemStack.canCombine(stack1, stack)) {
//                    int stack1Count = stack1.getCount();
//                    int eCount = stack.getCount();
//                    int totalCount = stack1Count + eCount;
//                    if (totalCount <= stack1.getMaxCount()) {
//                        stack1.setCount(totalCount);
//                        return;
//                    }
//                }
//            }
    }
}