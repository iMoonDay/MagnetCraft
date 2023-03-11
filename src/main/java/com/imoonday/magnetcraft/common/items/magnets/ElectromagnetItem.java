package com.imoonday.magnetcraft.common.items.magnets;

import com.imoonday.magnetcraft.api.FilterableItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.CooldownMethods;
import com.imoonday.magnetcraft.methods.DamageMethods;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import com.imoonday.magnetcraft.registries.special.CustomStatRegistries;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

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
            } else if (targetEntity instanceof ItemEntity entity) {
                player.getInventory().offerOrDrop(entity.getStack());
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

}