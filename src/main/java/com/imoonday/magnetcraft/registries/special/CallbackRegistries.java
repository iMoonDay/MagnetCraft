package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

public class CallbackRegistries {

    private static final Identifier IRON_GOLEM_LOOT_TABLE_ID = EntityType.IRON_GOLEM.getLootTableId();

    public static void register(){

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin() && IRON_GOLEM_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(ItemRegistries.MAGNETIC_IRON_INGOT));
                tableBuilder.pool(poolBuilder);
            }
        });

    }

}
