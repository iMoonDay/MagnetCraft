package com.imoonday.magnetcraft.mixin;

//import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.events.AttractEvent;
import com.imoonday.magnetcraft.events.NbtEvent;
import com.imoonday.magnetcraft.registries.EffectRegistries;
import com.imoonday.magnetcraft.registries.ItemRegistries;
import me.shedaniel.autoconfig.AutoConfig;
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
    public void checkAttract(CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity != null) {
            World world = ((LivingEntity) (Object) this).getWorld();
            if (world == null)
                return;

            //数值设置
            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            int[] minDis = new int[]{config.value.electromagnetAttractMinDis,config.value.permanentMagnetAttractMinDis,config.value.polarMagnetAttractMinDis};//电磁铁,永磁铁,无极磁铁最小范围
            int magnetHandSpacing = config.value.magnetHandSpacing;//手持范围差距
            int enchDefaultDis = config.value.enchDefaultDis;//附魔初始范围
            int disPerLvl = config.value.disPerLvl;//附魔每级提升的范围

            ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
            ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);
            ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
            ItemStack mainhand = entity.getEquippedStack(EquipmentSlot.MAINHAND);
            ItemStack offhand = entity.getEquippedStack(EquipmentSlot.OFFHAND);

            boolean mainhandEnabled = mainhand.getOrCreateNbt().getBoolean("enabled");
            boolean offhandEnabled = offhand.getOrCreateNbt().getBoolean("enabled");

            boolean mainhandElectromagnet = mainhand.isOf(ItemRegistries.ELECTROMAGNET_ITEM) && mainhandEnabled;
            boolean mainhandPermanent = mainhand.isOf(ItemRegistries.PERMANENT_MAGNET_ITEM) && mainhandEnabled;
            boolean mainhandPolar = mainhand.isOf(ItemRegistries.POLAR_MAGNET_ITEM) && mainhandEnabled;

            boolean mainhandMagnet = mainhandElectromagnet || mainhandPermanent || mainhandPolar;

            boolean offhandElectromagnet = offhand.isOf(ItemRegistries.ELECTROMAGNET_ITEM) && offhandEnabled;
            boolean offhandPermanent = offhand.isOf(ItemRegistries.PERMANENT_MAGNET_ITEM) && offhandEnabled;
            boolean offhandPolar = offhand.isOf(ItemRegistries.POLAR_MAGNET_ITEM) && offhandEnabled;

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

            boolean hasMagneticIronHelmet = head.isOf(ItemRegistries.MAGNETIC_IRON_HELMET);
            boolean hasMagneticIronChestcplate = chest.isOf(ItemRegistries.MAGNETIC_IRON_CHESTPLATE);
            boolean hasMagneticIronLeggings = feet.isOf(ItemRegistries.MAGNETIC_IRON_LEGGINGS);
            boolean hasMagneticIronBoots = legs.isOf(ItemRegistries.MAGNETIC_IRON_BOOTS);

            boolean hasNetheriteMagneticIronHelmet = head.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_HELMET);
            boolean hasNetheriteMagneticIronChestcplate = chest.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_CHESTPLATE);
            boolean hasNetheriteMagneticIronLeggings = feet.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_LEGGINGS);
            boolean hasNetheriteMagneticIronBoots = legs.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_BOOTS);

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
