package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.events.AttractEvent;
import com.imoonday.magnetcraft.events.NbtEvent;
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
            int electromagnetMinDis = 10;//电磁铁最小范围
            int permanentMagnetMinDis = 30;//永磁铁最小范围
            int polorMagnetMinDis = 20;//无极磁铁最小范围
            int magnetHandSpacing = 5;//手持范围差距
            int enchDefaultDis = 10;//附魔初始范围
            int disPerLvl = 2;//附魔每级提升的范围

            boolean mainhandEnabled = entity.getMainHandStack().getOrCreateNbt().getBoolean("enabled");
            boolean offhandEnabled = entity.getOffHandStack().getOrCreateNbt().getBoolean("enabled");

            boolean mainhandElectromagnet = entity.getMainHandStack().getItem() == MagnetCraft.ELECTROMAGNET_ITEM && mainhandEnabled;
            boolean mainhandPermanent = entity.getMainHandStack().getItem() == MagnetCraft.PERMANENT_MAGNET_ITEM && mainhandEnabled;
            boolean mainhandPolar = entity.getMainHandStack().getItem() == MagnetCraft.POLAR_MAGNET_ITEM && mainhandEnabled;

            boolean mainhandMagnet = mainhandElectromagnet || mainhandPermanent || mainhandPolar;

            boolean offhandElectromagnet = entity.getOffHandStack().getItem() == MagnetCraft.ELECTROMAGNET_ITEM && offhandEnabled;
            boolean offhandPermanent = entity.getOffHandStack().getItem() == MagnetCraft.PERMANENT_MAGNET_ITEM && offhandEnabled;
            boolean offhandPolar = entity.getOffHandStack().getItem() == MagnetCraft.POLAR_MAGNET_ITEM && offhandEnabled;

            boolean offhandMagnet = offhandElectromagnet || offhandPermanent || offhandPolar;

            boolean handElectromagnet = mainhandElectromagnet || offhandElectromagnet;
            boolean handPermanent = mainhandPermanent || offhandPermanent;
            boolean handPolar = mainhandPolar || offhandPolar;

            boolean handMagnet = mainhandMagnet || offhandMagnet;

            boolean hasEnch = (NbtEvent.hasEnchantment(entity, null, "magnetcraft:attract"));
            boolean hasTag = entity.getScoreboardTags().contains("MagnetOFF");

            boolean mainhandHasEnch = NbtEvent.hasEnchantment(entity, EquipmentSlot.MAINHAND, "magnetcraft:attract");
            boolean offhandHasEnch = NbtEvent.hasEnchantment(entity, EquipmentSlot.OFFHAND, "magnetcraft:attract");

            int dis;
            int enchLvl = NbtEvent.getEnchantmentLvl(entity, null, "magnetcraft:attract");
            int enchMinDis = enchDefaultDis + disPerLvl;
            int finalDis = hasEnch ? enchMinDis + (enchLvl - 1) * disPerLvl : 0;

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

            if ((hasEnch || handMagnet) && !hasTag) {
                if (handElectromagnet) {
                    dis = electromagnetMinDis;
                    if (mainhandElectromagnet) {
                        dis += magnetHandSpacing;
                        if (offhandElectromagnet) {
                            dis += magnetHandSpacing;
                        }
                    }
                    if (dis > finalDis) {
                        finalDis = dis;
                    }
                }

                if (handPermanent) {
                    dis = permanentMagnetMinDis;
                    if (mainhandPermanent) {
                        dis += magnetHandSpacing;
                        if (offhandPermanent) {
                            dis += magnetHandSpacing;
                        }
                    }
                    if (dis > finalDis) {
                        finalDis = dis;
                    }
                }

                if (handPolar) {
                    dis = polorMagnetMinDis;
                    if (mainhandPolar) {
                        dis += magnetHandSpacing;
                        if (offhandPolar) {
                            dis += magnetHandSpacing;
                        }
                    }
                    if (dis > finalDis) {
                        finalDis = dis;
                    }
                }

                boolean selected = mainhandMagnet || mainhandHasEnch || offhandMagnet || offhandHasEnch;

                AttractEvent.attractItems(mainhandStack, offhandStack, entity, selected, finalDis, playerHand);

            }
        }
    }
}
