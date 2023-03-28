package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.common.entities.golem.MagneticIronGolemEntity;
import com.imoonday.magnetcraft.common.entities.wrench.MagneticWrenchEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.Heightmap;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class EntityRegistries {

    public static final EntityType<MagneticIronGolemEntity> MAGNETIC_IRON_GOLEM = register("magnetic_iron_golem", EntityType.Builder.create(MagneticIronGolemEntity::new, SpawnGroup.MISC).setDimensions(1.4f, 2.7f).maxTrackingRange(15));
    public static final Item MAGNETIC_IRON_GOLEM_SPAWN_EGG = ItemRegistries.register("magnetic_iron_golem_spawn_egg", (Item) new SpawnEggItem(MAGNETIC_IRON_GOLEM, 10063810, 7643954, new Item.Settings()));
    public static final EntityType<MagneticWrenchEntity> MAGNETIC_WRENCH = register("magnetic_wrench", FabricEntityTypeBuilder.<MagneticWrenchEntity>create(SpawnGroup.MISC, MagneticWrenchEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).trackRangeChunks(4).trackedUpdateRate(20).build());

    @SuppressWarnings("SameParameterValue")
    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, id(id), type.build(id));
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends Entity> EntityType<T> register(String id, EntityType<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, id(id), type);
    }

    static {
        SpawnRestriction.register(MAGNETIC_IRON_GOLEM, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
    }

    public static void register() {
        FabricDefaultAttributeRegistry.register(MAGNETIC_IRON_GOLEM, MagneticIronGolemEntity.createIronGolemAttributes());
    }

}
