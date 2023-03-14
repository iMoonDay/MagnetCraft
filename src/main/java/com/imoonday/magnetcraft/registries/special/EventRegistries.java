package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class EventRegistries {

    private static final Identifier IRON_GOLEM_LOOT_TABLE_ID = EntityType.IRON_GOLEM.getLootTableId();

    public static void register(){
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin() && IRON_GOLEM_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder builder = LootPool.builder()
                        .with(ItemEntry.builder(ItemRegistries.MAGNET_FRAGMENT_ITEM)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f))));
                tableBuilder.pool(builder);
            }
        });
    }
}