package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.common.items.armors.MagneticIronArmorItem;
import com.imoonday.magnetcraft.common.items.armors.NetheriteMagneticIronArmorItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.DoubleStream;

import static com.imoonday.magnetcraft.registries.common.EnchantmentRegistries.MAGNETIC_LEVITATION_ENCHANTMENT;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends EntityMixin {

    private static final String USED_TICK = "UsedTick";

    @Override
    public void tryLevitation() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack stack = player.getEquippedStack(EquipmentSlot.FEET);
        if (player.isFallFlying() || player.getAbilities().flying || !stack.hasEnchantment(MAGNETIC_LEVITATION_ENCHANTMENT) || !player.getMagneticLevitationMode()) {
            return;
        }
        boolean level1 = stack.isOf(ItemRegistries.MAGNETIC_IRON_BOOTS);
        boolean level2 = stack.isOf(ItemRegistries.NETHERITE_MAGNETIC_IRON_BOOTS);
        double multiplier = level2 ? 2.0 : level1 ? 1.0 : 0.5;
        double speed = 0.25 * multiplier;
        int lvl = player.getEnchantmentLvl(EquipmentSlot.FEET, MAGNETIC_LEVITATION_ENCHANTMENT);
        int tick = player.getLevitationTick();
        int maxTick = (int) ((10 + lvl * 10) * multiplier) * 20;
        boolean auto = player.getAutomaticLevitation();
        boolean isClient = player.world.isClient;
        boolean survival = !player.isSpectator() && !player.getAbilities().creativeMode;
        StringBuilder displayTime = new StringBuilder();
        if (!isClient) {
            int maxSlot = 20;
            double percent = ((double) tick / maxTick) * maxSlot;
            int i1 = Math.max(0, (int) Math.min(percent, maxSlot));
            int i2 = Math.max(0, (int) Math.max(maxSlot - percent + 1, 0));
            displayTime.append("▓".repeat(i1)).append("░".repeat(i1 + i2 > maxSlot ? --i2 : i2));
        }
        boolean jumping = player.jumping || auto;
        if (!player.isOnGround() && jumping) {
            double heightMultiplier = MagneticIronArmorItem.isInMagneticIronSuit(player) ? 1.5 : NetheriteMagneticIronArmorItem.isInNetheriteMagneticIronSuit(player) ? 2.0 : 1.0;
            double height = (1.5 + (double) lvl / 2.0) * heightMultiplier + 0.1;
            player.setNoGravity(true);
            boolean collide = DoubleStream.iterate(0, d -> d <= height, d -> d + 0.1).anyMatch(d -> (!player.doesNotCollide(0, -d, 0)));
            player.setVelocity(player.getVelocity().x, tick >= maxTick ? -0.1 : collide ? player.isSneaking() ? auto && !player.jumping ? -speed : 0 : speed : !player.doesNotCollide(0, -height - 0.1, 0) ? player.isSneaking() && auto ? -speed : 0 : -0.1, player.getVelocity().z);
            if (isClient) {
                player.world.addParticle(ParticleTypes.FIREWORK, player.getX(), player.getY(), player.getZ(), player.getRandom().nextGaussian() * 0.05, -player.getVelocity().y * 0.5, player.getRandom().nextGaussian() * 0.05);
            }
            player.velocityDirty = true;
            player.setIgnoreFallDamage(true);
            if (survival) {
                player.setLevitationTick(++tick);
                player.addExhaustion(player.isSprinting() ? 0.2f / 20 : 0.05f / 20);
                if (!isClient) {
                    int usedTick = stack.getNbt() != null && stack.getNbt().contains("UsedTick") ? stack.getOrCreateNbt().getInt("UsedTick") : 0;
                    stack.getOrCreateNbt().putInt("UsedTick", ++usedTick);
                    player.sendMessage(Text.literal(displayTime.toString()), true);
                }
            }
            player.getInventory().markDirty();
        } else {
            player.setNoGravity(false);
            if (survival && tick > 0) {
                player.setLevitationTick(++tick);
                if (!isClient) {
                    player.sendMessage(Text.literal(displayTime.toString()), true);
                }
            }
        }
    }

    @Override
    public void setCooldown(ItemStack stack, int cooldown) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        int percent = ModConfig.getValue().coolingPercentage;
        if (stack.hasEnchantment(EnchantmentRegistries.FASTER_COOLDOWN_ENCHANTMENT)) {
            int level = stack.getEnchantmentLvl(EnchantmentRegistries.FASTER_COOLDOWN_ENCHANTMENT);
            cooldown -= cooldown * level / 10;
        }
        player.getItemCooldownManager().set(stack.getItem(), cooldown * percent / 100);
    }

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow
    @Final
    private PlayerAbilities abilities;


    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player != null) {
            World world = player.world;
            if (world == null) {
                return;
            }
            player.tryLevitation();
            boolean shouldDecreaseTick = player.getLevitationTick() > 0 && (player.isOnGround() || player.getAbilities().flying);
            if (shouldDecreaseTick) {
                player.setLevitationTick(player.getLevitationTick() - 3);
            }
            if (!player.isSpectator() && !player.getAbilities().creativeMode && player.hasNoGravity()) {
                player.setNoGravity(false);
            }
            if (!world.isClient && !player.isSpectator()) {
                ItemStack stack = player.getEquippedStack(EquipmentSlot.FEET);
                boolean hasTick = stack.getNbt() != null && stack.getNbt().contains(USED_TICK);
                int tick = hasTick ? stack.getNbt().getInt(USED_TICK) : 0;
                if (stack.hasEnchantment(MAGNETIC_LEVITATION_ENCHANTMENT) && player.getMagneticLevitationMode() && !player.getAbilities().creativeMode) {
                    stack.getOrCreateNbt().putInt(USED_TICK, ++tick);
                }
                int maxTick = 60 * 20;
                while (hasTick && stack.getNbt().getInt(USED_TICK) >= maxTick) {
                    stack.getOrCreateNbt().putInt(USED_TICK, stack.getOrCreateNbt().getInt(USED_TICK) - maxTick);
                    stack.addDamage(player.getRandom(), 1, true);
                }
            }
            player.getInventory().markDirty();
        }
    }

}