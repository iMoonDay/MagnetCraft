package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.common.entities.MagneticIronGolemEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.Heightmap;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class EntityRegistries {

    public static final EntityType<MagneticIronGolemEntity> MAGNETIC_IRON_GOLEM = register("magnetic_iron_golem", EntityType.Builder.create(MagneticIronGolemEntity::new, SpawnGroup.MISC).setDimensions(1.4f, 2.7f).maxTrackingRange(15));
    public static final Item MAGNETIC_IRON_GOLEM_SPAWN_EGG = ItemRegistries.register("magnetic_iron_golem_spawn_egg", (Item)new SpawnEggItem(MAGNETIC_IRON_GOLEM, 10063810, 7643954, new Item.Settings()));


    @SuppressWarnings("SameParameterValue")
    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, id(id), type.build(id));
    }

    static {
        SpawnRestriction.register(MAGNETIC_IRON_GOLEM, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
    }

    public static void register(){
        FabricDefaultAttributeRegistry.register(MAGNETIC_IRON_GOLEM, MagneticIronGolemEntity.createIronGolemAttributes());
    }

}
