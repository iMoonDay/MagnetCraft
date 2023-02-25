package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.special.CustomStatRegistries;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class TeleportMethods {

    public static void teleportSurroundingItemEntitiesToPlayer(World world, PlayerEntity player, double dis, Hand hand) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        boolean emptyDamage = DamageMethods.isEmptyDamage(player, hand);
        boolean mainhand = hand == Hand.MAIN_HAND;
        boolean message = config.displayMessageFeedback;
        boolean client = world.isClient;
        if (emptyDamage) return;
        if (mainhand) dis += config.value.magnetHandSpacing;
        double finalDis = dis;
        int count = player.getWorld().getOtherEntities(player, player.getBoundingBox().expand(dis), e -> (e instanceof ItemEntity || e instanceof ExperienceOrbEntity) && e.distanceTo(player) <= finalDis).size();
        String text;
        player.getWorld().getOtherEntities(player, player.getBoundingBox().expand(dis), e -> (e instanceof ItemEntity || e instanceof ExperienceOrbEntity) && e.distanceTo(player) <= finalDis).forEach(e -> {
            boolean isExp = e instanceof ExperienceOrbEntity;
            DamageMethods.addDamage(player, hand, 1);
            if (isExp) {
                int amount = ((ExperienceOrbEntity) e).getExperienceAmount();
                player.addExperience(amount);
            } else {
                giveItemStackToPlayer(world, player, ((ItemEntity) e).getStack());
            }
            player.incrementStat(CustomStatRegistries.ITEMS_TELEPORTED_TO_PLAYER);
            player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1, 1);

        });
        text = count > 0 ? "text.magnetcraft.message.teleport.tooltip.1" : "text.magnetcraft.message.teleport.tooltip.2";
        if (!client && message) {
            player.sendMessage(Text.translatable(text,dis,count));
        }
    }

    public static void giveItemStackToPlayer(World world, PlayerEntity player, ItemStack stack) {
        PlayerInventory inventory = player.getInventory();
        boolean hasSlot = inventory.getEmptySlot() != -1;
        ItemEntity itemEntity = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), stack);
        if (hasSlot) {
            player.giveItemStack(stack);
        } else {
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack1 = inventory.getStack(i);
                if (ItemStack.canCombine(stack1, stack)) {
                    int stack1Count = stack1.getCount();
                    int eCount = stack.getCount();
                    int totalCount = stack1Count + eCount;
                    if (totalCount <= stack1.getMaxCount()) {
                        stack1.setCount(totalCount);
                        return;
                    }
                }
            }
            world.spawnEntity(itemEntity);
            boolean isClient = world.isClient;
            boolean displayMessageFeedback = ModConfig.getConfig().displayMessageFeedback;
            boolean creative = player.isCreative();
            if (!isClient && displayMessageFeedback && !creative) {
                player.sendMessage(Text.translatable("text.magnetcraft.message.inventory_full"));
            }
        }
    }
}