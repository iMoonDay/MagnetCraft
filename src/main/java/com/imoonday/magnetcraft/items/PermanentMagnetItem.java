package com.imoonday.magnetcraft.items;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.events.NbtEvent;
import com.imoonday.magnetcraft.events.TeleportEvent;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class PermanentMagnetItem extends Item {
    public PermanentMagnetItem(Settings settings) {
        super(settings);
    }

    public static void register() {
        ModelPredicateProviderRegistry.register(MagnetCraft.PERMANENT_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (livingEntity == null || !itemStack.hasNbt()) return 0.0F;
            return itemStack.getOrCreateNbt().getBoolean("enabled") ? 1.0F : 0.0F;
        });
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        NbtEvent.enabledSet(stack);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.magnetcraft.permanent_magnet.tooltip.1")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.magnetcraft.permanent_magnet.tooltip.2")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        boolean sneaking = user.isSneaking();

        int dis = 25;//副手传送距离 主手+5

        if (sneaking) NbtEvent.enabledSwitch(world, user, hand);//潜行
        else TeleportEvent.teleportItems(world, user, dis, hand);//站立
        user.getItemCooldownManager().set(this, 10);
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        super.inventoryTick(stack, world, user, slot, selected);

        NbtEvent.enabledCheck(world, (PlayerEntity) user, slot);

    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {

        if (!entity.addScoreboardTag(user.getEntityName())) entity.removeScoreboardTag(user.getEntityName());
        if (!user.isCreative()) user.getItemCooldownManager().set(this, 20);

        return ActionResult.PASS;
    }

}