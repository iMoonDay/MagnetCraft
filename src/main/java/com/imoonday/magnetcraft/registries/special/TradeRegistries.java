package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;

import java.util.Arrays;

public class TradeRegistries {

    public static void register() {
        registerVillagers();
        registerWanderingTrader();
    }

    private static void registerWanderingTrader() {
        TradeOfferHelper.registerWanderingTraderOffers(1, factories -> {
            factories.add(TradeRegistries::sellTransmitterItem);
            factories.add(TradeRegistries::sellBatteryItem);
        });
    }

    private static void registerVillagers() {
        Arrays.asList(VillagerProfession.ARMORER, VillagerProfession.WEAPONSMITH, VillagerProfession.TOOLSMITH).forEach(villagerProfession -> TradeOfferHelper.registerVillagerOffers(villagerProfession, 2, factories -> factories.add(TradeRegistries::buyIngotItem)));
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 5, factories -> factories.add(TradeRegistries::sellTransmitterItem));
    }

    private static TradeOffer sellTransmitterItem(Entity entity, Random random) {
        ItemStack stack = new ItemStack(ItemRegistries.ELECTROMAGNETIC_TRANSMITTER_ITEM);
        stack.setDamage(random.nextBetween(0, 100));
        return new TradeOffer(new ItemStack(ItemRegistries.MAGNETIC_BATTERY, random.nextBetween(1, 3)), new ItemStack(Items.EMERALD, random.nextBetween(16, 48)), stack, 2, 30, 0.5f);
    }

    private static TradeOffer sellBatteryItem(Entity entity, Random random) {
        return new TradeOffer(new ItemStack(ItemRegistries.MAGNETIC_IRON_INGOT, random.nextBetween(4, 8)), new ItemStack(Items.EMERALD, random.nextBetween(4, 8)), new ItemStack(ItemRegistries.MAGNETIC_BATTERY, random.nextBetween(1, 3)), 3, 10, 0.5f);
    }

    private static TradeOffer buyIngotItem(Entity entity, Random random) {
        return new TradeOffer(new ItemStack(ItemRegistries.MAGNETIC_IRON_INGOT, 4), new ItemStack(Items.EMERALD), 12, 10, 0.05f);
    }

}
