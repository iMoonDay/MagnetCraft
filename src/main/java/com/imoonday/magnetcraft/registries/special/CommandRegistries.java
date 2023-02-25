package com.imoonday.magnetcraft.registries.special;

import com.imoonday.magnetcraft.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandRegistries {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("magnet")
                        .requires(ServerCommandSource::isExecutedByPlayer)
                        .requires(e -> e.hasPermissionLevel(4))
                        .then(literal("blacklist")
                                .then(literal("add")
                                        .executes(context -> {
                                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        ItemStack stack = player.getMainHandStack();
                                                        if (stack != null && !stack.isOf(Items.AIR)) {
                                                            String key = stack.getTranslationKey();
                                                            int returnInt = addBlacklistItem(stack.getItem());
                                                            if (returnInt == 1) {
                                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.add").append(Text.translatable(key)));
                                                                return 1;
                                                            } else if (returnInt == 0) {
                                                                context.getSource().sendMessage(Text.translatable(key).append(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.exist")));
                                                            } else {
                                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
                                                            }
                                                        }
                                                    }
                                                    return 0;
                                                }
                                        )
                                        .then(argument("item", ItemStackArgumentType.itemStack(registryAccess)).executes(context -> {
                                            String key = ItemStackArgumentType.getItemStackArgument(context, "item").getItem().getTranslationKey();
                                            Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
                                            int returnInt = addBlacklistItem(item);
                                            if (returnInt == 1) {
                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.add").append(Text.translatable(key)));
                                                return 1;
                                            } else if (returnInt == 0) {
                                                context.getSource().sendMessage(Text.translatable(key).append(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.exist")));
                                            } else {
                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
                                            }
                                            return 0;
                                        }))
                                )
                                .then(literal("remove")
                                        .executes(context -> {
                                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        ItemStack stack = player.getMainHandStack();
                                                        if (stack != null && !stack.isOf(Items.AIR)) {
                                                            String key = stack.getTranslationKey();
                                                            int returnInt = removeBlacklistItem(stack.getItem());
                                                            if (returnInt == 1) {
                                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.remove").append(Text.translatable(key)));
                                                                return 1;
                                                            } else if (returnInt == 0) {
                                                                context.getSource().sendMessage(Text.translatable(key).append(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.nonexist")));
                                                            } else {
                                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
                                                            }
                                                            return 0;
                                                        }
                                                    }
                                                    return 0;
                                                }
                                        )
                                        .then(argument("item", ItemStackArgumentType.itemStack(registryAccess)).executes(context -> {
                                            String key = ItemStackArgumentType.getItemStackArgument(context, "item").getItem().getTranslationKey();
                                            Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
                                            int returnInt = removeBlacklistItem(item);
                                            if (returnInt == 1) {
                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.remove").append(Text.translatable(key)));
                                                return 1;
                                            } else if (returnInt == 0) {
                                                context.getSource().sendMessage(Text.translatable(key).append(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.nonexist")));
                                            } else {
                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
                                            }
                                            return 0;
                                        }))
                                )
                                .then(literal("list")
                                        .executes(context -> {
                                                    if (Arrays.stream(ModConfig.getConfig().blacklist.list).toList().isEmpty()) {
                                                        context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.empty"));
                                                    } else {
                                                        context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.blacklist").append(Arrays.toString(ModConfig.getConfig().blacklist.list)));
                                                    }
                                                    return 1;
                                                }
                                        )
                                )
                                .then(literal("clear")
                                        .executes(context -> {
                                                    ModConfig.getConfig().blacklist.list = new String[]{};
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
                                                        ItemStack stack = player.getMainHandStack();
                                                        if (stack != null && !stack.isOf(Items.AIR)) {
                                                            String key = stack.getTranslationKey();
                                                            int returnInt = addWhitelistItem(stack.getItem());
                                                            if (returnInt == 1) {
                                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.add").append(Text.translatable(key)));
                                                                return 1;
                                                            } else if (returnInt == 0) {
                                                                context.getSource().sendMessage(Text.translatable(key).append(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.exist")));
                                                            } else {
                                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
                                                            }
                                                        }
                                                    }
                                                    return 0;
                                                }
                                        )
                                        .then(argument("item", ItemStackArgumentType.itemStack(registryAccess)).executes(context -> {
                                            String key = ItemStackArgumentType.getItemStackArgument(context, "item").getItem().getTranslationKey();
                                            Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
                                            int returnInt = addWhitelistItem(item);
                                            if (returnInt == 1) {
                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.add").append(Text.translatable(key)));
                                                return 1;
                                            } else if (returnInt == 0) {
                                                context.getSource().sendMessage(Text.translatable(key).append(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.exist")));
                                            } else {
                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
                                            }
                                            return 0;
                                        }))
                                )
                                .then(literal("remove")
                                        .executes(context -> {
                                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        ItemStack stack = player.getMainHandStack();
                                                        if (stack != null && !stack.isOf(Items.AIR)) {
                                                            String key = stack.getTranslationKey();
                                                            int returnInt = removeWhitelistItem(stack.getItem());
                                                            if (returnInt == 1) {
                                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.remove").append(Text.translatable(key)));
                                                                return 1;
                                                            } else if (returnInt == 0) {
                                                                context.getSource().sendMessage(Text.translatable(key).append(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.nonexist")));
                                                            } else {
                                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
                                                            }
                                                            return 0;
                                                        }
                                                    }
                                                    return 0;
                                                }
                                        )
                                        .then(argument("item", ItemStackArgumentType.itemStack(registryAccess)).executes(context -> {
                                            String key = ItemStackArgumentType.getItemStackArgument(context, "item").getItem().getTranslationKey();
                                            Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
                                            int returnInt = removeWhitelistItem(item);
                                            if (returnInt == 1) {
                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.remove").append(Text.translatable(key)));
                                                return 1;
                                            } else if (returnInt == 0) {
                                                context.getSource().sendMessage(Text.translatable(key).append(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.nonexist")));
                                            } else {
                                                context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
                                            }
                                            return 0;
                                        }))
                                )
                                .then(literal("list")
                                        .executes(context -> {
                                                    if (Arrays.stream(ModConfig.getConfig().whitelist.list).toList().isEmpty()) {
                                                        context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.empty"));
                                                    } else {
                                                        context.getSource().sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.whitelist").append(Arrays.toString(ModConfig.getConfig().whitelist.list)));
                                                    }
                                                    return 1;
                                                }
                                        )
                                )
                                .then(literal("clear")
                                        .executes(context -> {
                                                    ModConfig.getConfig().whitelist.list = new String[]{};
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

    public static int addWhitelistItem(Item item) {
        String name = Registries.ITEM.getId(item).toString();
        String[] oldList = ModConfig.getConfig().whitelist.list;
        int length = oldList.length;
        String[] newList = new String[length + 1];
        System.arraycopy(oldList, 0, newList, 0, length);
        newList[length] = name;
        boolean contains = Arrays.stream(ModConfig.getConfig().whitelist.list).toList().contains(name);
        if (contains) {
            return 0;
        } else {
            ModConfig.getConfig().whitelist.list = newList;
            AutoConfig.getConfigHolder(ModConfig.class).save();
            contains = Arrays.stream(ModConfig.getConfig().whitelist.list).toList().contains(name);
            if (contains) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public static int addBlacklistItem(Item item) {
        String name = Registries.ITEM.getId(item).toString();
        String[] oldList = ModConfig.getConfig().blacklist.list;
        int length = oldList.length;
        String[] newList = new String[length + 1];
        System.arraycopy(oldList, 0, newList, 0, length);
        newList[length] = name;
        boolean contains = Arrays.stream(ModConfig.getConfig().blacklist.list).toList().contains(name);
        if (contains) {
            return 0;
        } else {
            ModConfig.getConfig().blacklist.list = newList;
            AutoConfig.getConfigHolder(ModConfig.class).save();
            contains = Arrays.stream(ModConfig.getConfig().blacklist.list).toList().contains(name);
            if (contains) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public static int removeWhitelistItem(Item item) {
        String name = Registries.ITEM.getId(item).toString();
        String[] oldList = ModConfig.getConfig().whitelist.list;
        int length = oldList.length;
        if (length != 0) {
            String[] newList = new String[length - 1];
            boolean removed = false;
            boolean contains = Arrays.stream(ModConfig.getConfig().whitelist.list).toList().contains(name);
            if (!contains) {
                return 0;
            } else {
                for (int i = 0; i < length; i++) {
                    if (!Objects.equals(oldList[i], name) && !removed) {
                        newList[i] = oldList[i];
                    } else {
                        removed = true;
                        if (i + 1 < length) {
                            newList[i] = oldList[i + 1];
                        }
                    }
                }
                ModConfig.getConfig().whitelist.list = newList;
                AutoConfig.getConfigHolder(ModConfig.class).save();
                contains = Arrays.stream(ModConfig.getConfig().whitelist.list).toList().contains(name);
                if (!contains) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
        return 0;
    }

    public static int removeBlacklistItem(Item item) {
        String name = Registries.ITEM.getId(item).toString();
        String[] oldList = ModConfig.getConfig().blacklist.list;
        int length = oldList.length;
        if (length != 0) {
            String[] newList = new String[length - 1];
            boolean removed = false;
            boolean contains = Arrays.stream(ModConfig.getConfig().blacklist.list).toList().contains(name);
            if (!contains) {
                return 0;
            } else {
                for (int i = 0; i < length; i++) {
                    if (!Objects.equals(oldList[i], name) && !removed) {
                        newList[i] = oldList[i];
                    } else {
                        removed = true;
                        if (i + 1 < length) {
                            newList[i] = oldList[i + 1];
                        }
                    }
                }
                ModConfig.getConfig().blacklist.list = newList;
                AutoConfig.getConfigHolder(ModConfig.class).save();
                contains = Arrays.stream(ModConfig.getConfig().blacklist.list).toList().contains(name);
                if (!contains) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
        return 0;
    }

    public static void addOrRemoveWhitelistItem(PlayerEntity player) {
        Item item = player.getMainHandStack().getItem();
        if (item == Items.AIR) {
            return;
        }
        String key = player.getMainHandStack().getTranslationKey();
        String name = Registries.ITEM.getId(item).toString();
        boolean contains = Arrays.stream(ModConfig.getConfig().whitelist.list).toList().contains(name);
        if (contains) {
            int returnInt = removeWhitelistItem(item);
            if (returnInt == 1) {
                player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.remove").append(Text.translatable(key)));
            } else if (returnInt == 0) {
                player.sendMessage(Text.translatable(key).append(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.nonexist")));
            } else {
                player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
            }
        } else {
            int returnInt = addWhitelistItem(item);
            if (returnInt == 1) {
                player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.add").append(Text.translatable(key)));
            } else if (returnInt == 0) {
                player.sendMessage(Text.translatable(key).append(Text.translatable("text.autoconfig.magnetcraft.command.whitelist.exist")));
            } else {
                player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
            }
        }
    }

    public static void addOrRemoveBlacklistItem(PlayerEntity player) {
        Item item = player.getMainHandStack().getItem();
        if (item == Items.AIR) {
            return;
        }
        String key = player.getMainHandStack().getTranslationKey();
        String name = Registries.ITEM.getId(item).toString();
        boolean contains = Arrays.stream(ModConfig.getConfig().blacklist.list).toList().contains(name);
        if (contains) {
            int returnInt = removeBlacklistItem(item);
            if (returnInt == 1) {
                player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.remove").append(Text.translatable(key)));
            } else if (returnInt == 0) {
                player.sendMessage(Text.translatable(key).append(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.nonexist")));
            } else {
                player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
            }
        } else {
            int returnInt = addBlacklistItem(item);
            if (returnInt == 1) {
                player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.add").append(Text.translatable(key)));
            } else if (returnInt == 0) {
                player.sendMessage(Text.translatable(key).append(Text.translatable("text.autoconfig.magnetcraft.command.blacklist.exist")));
            } else {
                player.sendMessage(Text.translatable("text.autoconfig.magnetcraft.command.error"));
            }
        }
    }
}
