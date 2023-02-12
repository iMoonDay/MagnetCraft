package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.events.AttractEvent;
import com.imoonday.magnetcraft.events.NbtEvent;
import com.imoonday.magnetcraft.registries.EffectRegistries;
import com.imoonday.magnetcraft.registries.ItemRegistries;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(at = @At(value = "HEAD"), method = "tick")
    public void checkEquipmentAttractEnchantments(CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity != null) {
            World world = ((LivingEntity) (Object) this).getWorld();
            if (world == null)
                return;

            //数值设置
            int[] minDis = new int[]{10, 30, 20};//电磁铁,永磁铁,无极磁铁最小范围
            int magnetHandSpacing = 5;//手持范围差距
            int enchDefaultDis = 10;//附魔初始范围
            int disPerLvl = 2;//附魔每级提升的范围

            boolean mainhandEnabled = entity.getMainHandStack().getOrCreateNbt().getBoolean("enabled");
            boolean offhandEnabled = entity.getOffHandStack().getOrCreateNbt().getBoolean("enabled");

            boolean mainhandElectromagnet = entity.getMainHandStack().getItem() == ItemRegistries.ELECTROMAGNET_ITEM && mainhandEnabled;
            boolean mainhandPermanent = entity.getMainHandStack().getItem() == ItemRegistries.PERMANENT_MAGNET_ITEM && mainhandEnabled;
            boolean mainhandPolar = entity.getMainHandStack().getItem() == ItemRegistries.POLAR_MAGNET_ITEM && mainhandEnabled;

            boolean mainhandMagnet = mainhandElectromagnet || mainhandPermanent || mainhandPolar;

            boolean offhandElectromagnet = entity.getOffHandStack().getItem() == ItemRegistries.ELECTROMAGNET_ITEM && offhandEnabled;
            boolean offhandPermanent = entity.getOffHandStack().getItem() == ItemRegistries.PERMANENT_MAGNET_ITEM && offhandEnabled;
            boolean offhandPolar = entity.getOffHandStack().getItem() == ItemRegistries.POLAR_MAGNET_ITEM && offhandEnabled;

            boolean offhandMagnet = offhandElectromagnet || offhandPermanent || offhandPolar;

            boolean handElectromagnet = mainhandElectromagnet || offhandElectromagnet;
            boolean handPermanent = mainhandPermanent || offhandPermanent;
            boolean handPolar = mainhandPolar || offhandPolar;

            boolean handMagnet = mainhandMagnet || offhandMagnet;

            boolean hasEnch = (NbtEvent.hasEnchantment(entity, null, "magnetcraft:attract"));
            boolean hasTag = entity.getScoreboardTags().contains("MagnetOFF");
            boolean hasEffect = entity.hasStatusEffect(EffectRegistries.ATTRACT_EFFECT);

            boolean mainhandHasEnch = NbtEvent.hasEnchantment(entity, EquipmentSlot.MAINHAND, "magnetcraft:attract");
            boolean offhandHasEnch = NbtEvent.hasEnchantment(entity, EquipmentSlot.OFFHAND, "magnetcraft:attract");

            boolean selected = mainhandMagnet || mainhandHasEnch || offhandMagnet || offhandHasEnch;

            boolean isAttracting = (hasEnch || handMagnet || hasEffect) && !hasTag;

            boolean hasMagneticIronHelmet = entity.getEquippedStack(EquipmentSlot.HEAD).isOf(ItemRegistries.MAGNETIC_IRON_HELMET);
            boolean hasMagneticIronChestcplate = entity.getEquippedStack(EquipmentSlot.CHEST).isOf(ItemRegistries.MAGNETIC_IRON_CHESTPLATE);
            boolean hasMagneticIronLeggings = entity.getEquippedStack(EquipmentSlot.LEGS).isOf(ItemRegistries.MAGNETIC_IRON_LEGGINGS);
            boolean hasMagneticIronBoots = entity.getEquippedStack(EquipmentSlot.FEET).isOf(ItemRegistries.MAGNETIC_IRON_BOOTS);

            boolean hasNetheriteMagneticIronHelmet = entity.getEquippedStack(EquipmentSlot.HEAD).isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_HELMET);
            boolean hasNetheriteMagneticIronChestcplate = entity.getEquippedStack(EquipmentSlot.CHEST).isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_CHESTPLATE);
            boolean hasNetheriteMagneticIronLeggings = entity.getEquippedStack(EquipmentSlot.LEGS).isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_LEGGINGS);
            boolean hasNetheriteMagneticIronBoots = entity.getEquippedStack(EquipmentSlot.FEET).isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_BOOTS);

            boolean hasMagneticIronSuit = hasMagneticIronHelmet && hasMagneticIronChestcplate && hasMagneticIronLeggings && hasMagneticIronBoots;
            boolean hasNetheriteMagneticIronSuit = hasNetheriteMagneticIronHelmet && hasNetheriteMagneticIronChestcplate && hasNetheriteMagneticIronLeggings && hasNetheriteMagneticIronBoots;

            boolean[] handItems = new boolean[]{handElectromagnet, handPermanent, handPolar};
            boolean[] mainhandItems = new boolean[]{mainhandElectromagnet, mainhandPermanent, mainhandPolar};
            boolean[] offhandItems = new boolean[]{offhandElectromagnet, offhandPermanent, offhandPolar};

            int enchLvl = NbtEvent.getEnchantmentLvl(entity, null, "magnetcraft:attract");
            int enchMinDis = enchDefaultDis + disPerLvl;
            double finalDis = hasEnch ? enchMinDis + (enchLvl - 1) * disPerLvl : 0;

            String playerHand = null;

            ItemStack mainhandStack = ItemStack.EMPTY;
            ItemStack offhandStack = ItemStack.EMPTY;

            if (mainhandMagnet || mainhandHasEnch) {
                mainhandStack = entity.getMainHandStack();
                playerHand = "mainhand";
            }

            if (offhandMagnet || offhandHasEnch) {
                offhandStack = entity.getOffHandStack();
                if (mainhandMagnet) playerHand = "hand";
                else playerHand = "offhand";
            }

            if (isAttracting) {
                entity.addScoreboardTag("isAttracting");
            } else {
                entity.removeScoreboardTag("isAttracting");
            }

            if ((hasEnch || handMagnet) && !hasTag) {
                for (int i = 0; i <= 2; i++) {
                    if (handItems[i]) {
                        int dis = minDis[i];
                        if (mainhandItems[i]) {
                            dis += magnetHandSpacing;
                            if (offhandItems[i]) {
                                dis += magnetHandSpacing;
                            }
                        }
                        if (dis > finalDis) {
                            finalDis = dis;
                        }
                    }
                }

                if (hasMagneticIronSuit) {
                    finalDis *= 1.5;
                }

                if (hasNetheriteMagneticIronSuit) {
                    finalDis *= 2;
                }

                AttractEvent.attractItems(mainhandStack, offhandStack, entity, selected, finalDis, playerHand);

            }
        }
    }
}
