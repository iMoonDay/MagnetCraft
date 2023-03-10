package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.api.EntityAttractNbt;
import com.imoonday.magnetcraft.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandRegistries {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("magnet")
                        .then(literal("nbthelper")
                                .then(literal("self")
                                        .then(literal("get")
                                                .executes(context -> {
                                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                                            if (player != null) {
                                                                String text = "None";
                                                                NbtCompound nbt = ((EntityAttractNbt) player).getAttractData();
                                                                if (nbt != null) {
                                                                    text = nbt.toString();
                                                                }
                                                                player.sendMessage(Text.literal(text));
                                                            }
                                                            return 0;
                                                        }
                                                )
                                        )
                                        .then(literal("initialize")
                                                .executes(context -> {
                                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                                            if (player != null) {
                                                                if(((EntityAttractNbt) player).clearAttractData()){
                                                                    player.sendMessage(Text.literal("Success"));
                                                                }
                                                            }
                                                            return 0;
                                                        }
                                                )
                                        )
                                )
                                .then(literal("mainhand")
                                        .executes(context -> {
                                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        String text = "None";
                                                        NbtCompound nbt = player.getMainHandStack().getNbt();
                                                        if (nbt != null) {
                                                            text = nbt.toString();
                                                        }
                                                        player.sendMessage(Text.literal(text));
                                                    }
                                                    return 0;
                                                }
                                        )
                                )
                                .then(literal("offhand")
                                        .executes(context -> {
                                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        String text = "None";
                                                        NbtCompound nbt = player.getOffHandStack().getNbt();
                                                        if (nbt != null) {
                                                            text = nbt.toString();
                                                        }
                                                        player.sendMessage(Text.literal(text));
                                                    }
                                                    return 0;
                                                }
                                        )
                                )
                                .then(literal("slot")
                                        .then(argument("slot", ItemSlotArgumentType.itemSlot())
                                                .executes(context -> {
                                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                                            if (player != null) {
                                                                String text = "None";
                                                                NbtCompound nbt = player.getInventory().getStack(ItemSlotArgumentType.getItemSlot(context, "slot")).getNbt();
                                                                if (nbt != null) {
                                                                    text = nbt.toString();
                                                                }
                                                                player.sendMessage(Text.literal(text));
                                                            }
                                                            return 0;
                                                        }
                                                )
                                        )
                                )
                                .then(literal("block")
                                        .executes(context -> {
                                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        String text = "None";
                                                        MinecraftClient client = MinecraftClient.getInstance();
                                                        HitResult hit = client.crosshairTarget;
                                                        if (hit != null) {
                                                            if (Objects.requireNonNull(hit.getType()) == HitResult.Type.BLOCK) {
                                                                BlockHitResult blockHit = (BlockHitResult) hit;
                                                                BlockPos blockPos = blockHit.getBlockPos();
                                                                if (player.world.getBlockEntity(blockPos) != null) {
                                                                    NbtCompound nbt = Objects.requireNonNull(player.world.getBlockEntity(blockPos)).createNbt();
                                                                    text = nbt.toString();
                                                                }
                                                                player.sendMessage(Text.literal(text));
                                                            }
                                                        }
                                                    }
                                                    return 0;
                                                }
                                        )
                                )
                                .then(literal("entity")
                                        .executes(context -> {
                                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        String text = "None";
                                                        MinecraftClient client = MinecraftClient.getInstance();
                                                        HitResult hit = client.crosshairTarget;
                                                        if (hit != null) {
                                                            if (Objects.requireNonNull(hit.getType()) == HitResult.Type.ENTITY) {
                                                                EntityHitResult entityHit = (EntityHitResult) hit;
                                                                Entity entity = entityHit.getEntity();
                                                                NbtCompound nbt;
                                                                nbt = entity.writeNbt(new NbtCompound());
                                                                text = nbt.toString();
                                                            }
                                                        }
                                                        player.sendMessage(Text.literal(text));
                                                    }
                                                    return 0;
                                                }
                                        )
                                        .then(literal("mainhand")
                                                .executes(context -> {
                                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                                            if (player != null) {
                                                                String text = "None";
                                                                MinecraftClient client = MinecraftClient.getInstance();
                                                                HitResult hit = client.crosshairTarget;
                                                                if (hit != null) {
                                                                    if (Objects.requireNonNull(hit.getType()) == HitResult.Type.ENTITY) {
                                                                        EntityHitResult entityHit = (EntityHitResult) hit;
                                                                        Entity entity = entityHit.getEntity();
                                                                        if (entity instanceof LivingEntity) {
                                                                            NbtCompound nbt = ((LivingEntity) entity).getStackInHand(Hand.MAIN_HAND).getNbt();
                                                                            if (nbt != null) {
                                                                                text = nbt.toString();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                player.sendMessage(Text.literal(text));
                                                            }
                                                            return 0;
                                                        }
                                                )
                                        )
                                        .then(literal("offhand")
                                                .executes(context -> {
                                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                                            if (player != null) {
                                                                String text = "None";
                                                                MinecraftClient client = MinecraftClient.getInstance();
                                                                HitResult hit = client.crosshairTarget;
                                                                if (hit != null) {
                                                                    if (Objects.requireNonNull(hit.getType()) == HitResult.Type.ENTITY) {
                                                                        EntityHitResult entityHit = (EntityHitResult) hit;
                                                                        Entity entity = entityHit.getEntity();
                                                                        if (entity instanceof LivingEntity) {
                                                                            NbtCompound nbt = ((LivingEntity) entity).getStackInHand(Hand.OFF_HAND).getNbt();
                                                                            if (nbt != null) {
                                                                                text = nbt.toString();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                player.sendMessage(Text.literal(text));
                                                            }
                                                            return 0;
                                                        }
                                                )
                                        )
                                )
                        )
                        .requires(ServerCommandSource::isExecutedByPlayer)
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("blacklist")
                                .then(literal("add")
                                        .executes(context -> {
                                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        Item item = player.getMainHandStack().getItem();
                                                        itemListHandling(player, item, ListType.BLACKLIST, Action.ADD);
                                                    }
                                                    return 0;
                                                }
                                        )
                                        .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                                .executes(context -> {
                                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                                            if (player != null) {
                                                                Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
                                                                itemListHandling(player, item, ListType.BLACKLIST, Action.ADD);
                                                            }
                                                            return 0;
                                                        }
                                                )
                                        )
                                )
                                .then(literal("remove")
                                        .executes(context -> {
                                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        Item item = player.getMainHandStack().getItem();
                                                        itemListHandling(player, item, ListType.BLACKLIST, Action.REMOVE);
                                                    }
                                                    return 0;
                                                }
                                        )
                                        .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                                .executes(context -> {
                                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                                            if (player != null) {
                                                                Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
                                                                itemListHandling(player, item, ListType.BLACKLIST, Action.REMOVE);
                                                            }
                                                            return 0;
                                                        }
                                                )
                                        )
                                )
                                .then(literal("list")
                                        .executes(context -> {
                                                    if (ModConfig.getConfig().blacklist.list.isEmpty()) {
                                                        context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.empty"));
                                                    } else {
                                                        context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.blacklist", ModConfig.getConfig().blacklist.list.toString()));
                                                    }
                                                    return 1;
                                                }
                                        )
                                )
                                .then(literal("clear")
                                        .executes(context -> {
                                                    ModConfig.getConfig().blacklist.list.clear();
                                                    AutoConfig.getConfigHolder(ModConfig.class).save();
                                                    context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.cleared"));
                                                    return 1;
                                                }
                                        )
                                )
                                .then(literal("enable")
                                        .executes(context -> {
                                                    ModConfig.getConfig().blacklist.enable = true;
                                                    AutoConfig.getConfigHolder(ModConfig.class).save();
                                                    context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.enable"));
                                                    return 1;
                                                }
                                        )
                                )
                                .then(literal("disable")
                                        .executes(context -> {
                                                    ModConfig.getConfig().blacklist.enable = false;
                                                    AutoConfig.getConfigHolder(ModConfig.class).save();
                                                    context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.disabled"));
                                                    return 1;
                                                }
                                        )
                                )
                        )
                        .then(literal("whitelist")
                                .then(literal("add")
                                        .executes(context -> {
                                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        Item item = player.getMainHandStack().getItem();
                                                        itemListHandling(player, item, ListType.WHITELIST, Action.ADD);
                                                    }
                                                    return 0;
                                                }
                                        )
                                        .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                                .executes(context -> {
                                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                                            if (player != null) {
                                                                Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
                                                                itemListHandling(player, item, ListType.WHITELIST, Action.ADD);
                                                            }
                                                            return 0;
                                                        }
                                                )
                                        )
                                )
                                .then(literal("remove")
                                        .executes(context -> {
                                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        Item item = player.getMainHandStack().getItem();
                                                        itemListHandling(player, item, ListType.WHITELIST, Action.REMOVE);
                                                    }
                                                    return 0;
                                                }
                                        )
                                        .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                                .executes(context -> {
                                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                                            if (player != null) {
                                                                Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
                                                                itemListHandling(player, item, ListType.WHITELIST, Action.REMOVE);
                                                            }
                                                            return 0;
                                                        }
                                                )
                                        )
                                )
                                .then(literal("list")
                                        .executes(context -> {
                                                    if (ModConfig.getConfig().whitelist.list.isEmpty()) {
                                                        context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.empty"));
                                                    } else {
                                                        context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.whitelist", ModConfig.getConfig().whitelist.list.toString()));
                                                    }
                                                    return 1;
                                                }
                                        )
                                )
                                .then(literal("clear")
                                        .executes(context -> {
                                                    ModConfig.getConfig().whitelist.list.clear();
                                                    AutoConfig.getConfigHolder(ModConfig.class).save();
                                                    context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.cleared"));
                                                    return 1;
                                                }
                                        )
                                )
                                .then(literal("enable")
                                        .executes(context -> {
                                                    ModConfig.getConfig().whitelist.enable = true;
                                                    AutoConfig.getConfigHolder(ModConfig.class).save();
                                                    context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.enable"));
                                                    return 1;
                                                }
                                        )
                                )
                                .then(literal("disable")
                                        .executes(context -> {
                                                    ModConfig.getConfig().whitelist.enable = false;
                                                    AutoConfig.getConfigHolder(ModConfig.class).save();
                                                    context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.disabled"));
                                                    return 1;
                                                }
                                        )
                                )
                        )
                )
        );
    }

    public enum ListType {
        WHITELIST,
        BLACKLIST
    }

    public enum Action {
        ADD,
        REMOVE
    }

    public static int itemListHandling(Item item, ListType type, Action action) {
        String name = Registries.ITEM.getId(item).toString();
        ArrayList<String> list;
        switch (type) {
            case BLACKLIST -> list = ModConfig.getConfig().whitelist.list;
            case WHITELIST -> list = ModConfig.getConfig().blacklist.list;
            default -> {
                return -1;
            }
        }
        switch (action) {
            case ADD -> {
                if (list.contains(name)) {
                    return 0;
                } else {
                    list.add(name);
                    AutoConfig.getConfigHolder(ModConfig.class).save();
                    if (list.contains(name)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
            case REMOVE -> {
                if (!list.contains(name)) {
                    return 0;
                } else {
                    list.remove(name);
                    AutoConfig.getConfigHolder(ModConfig.class).save();
                    if (!list.contains(name)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
        }
        return -1;
    }

    public static void itemListHandling(PlayerEntity player, Item item, ListType type, @Nullable Action action) {
        if (item == null) {
            item = player.getMainHandStack().getItem();
        }
        if (item == Items.AIR) {
            return;
        }
        String key = item.getTranslationKey();
        String itemId = Registries.ITEM.getId(item).toString();
        ArrayList<String> list;
        switch (type) {
            case BLACKLIST -> list = ModConfig.getConfig().whitelist.list;
            case WHITELIST -> list = ModConfig.getConfig().blacklist.list;
            default -> {
                return;
            }
        }
        if (action == null) {
            action = list.contains(itemId) ? Action.REMOVE : Action.ADD;
        }
        int result = itemListHandling(item, type, action);
        sendMessage(player, type, action, result, key);
    }

    static void sendMessage(PlayerEntity player, ListType type, Action action, int result, String key) {
        key = Text.translatable(key).getString();
        if (type == ListType.WHITELIST) {
            if (action == Action.REMOVE) {
                switch (result) {
                    case 1 ->
                            player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.remove", key));
                    case 0 ->
                            player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.nonexist", key));
                    default -> player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
                }
            } else {
                switch (result) {
                    case 1 ->
                            player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.add", key));
                    case 0 ->
                            player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.exist", key));
                    default -> player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
                }
            }
        } else {
            if (action == Action.REMOVE) {
                switch (result) {
                    case 1 ->
                            player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.remove", key));
                    case 0 ->
                            player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.nonexist", key));
                    default -> player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
                }
            } else {
                switch (result) {
                    case 1 ->
                            player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.add", key));
                    case 0 ->
                            player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.exist", key));
                    default -> player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
                }
            }
        }
    }
}
