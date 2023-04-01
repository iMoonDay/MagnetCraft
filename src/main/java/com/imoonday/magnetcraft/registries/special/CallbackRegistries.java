package com.imoonday.magnetcraft.registries.special;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CallbackRegistries {

    public static final String OWNER = "iMoonDay";
    public static final String NAME = "MagnetCraft";

    private volatile static boolean updated = false;

    public static void register() {
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> updated = false);
        ServerPlayConnectionEvents.JOIN.register(CallbackRegistries::updateCheck);
        LootTableEvents.MODIFY.register(CallbackRegistries::modifyLootTable);
    }

    private static float getLatestVersion() {
        try {
            byte[] buf;
            try (InputStream i = new URL(" https://api.github.com/repos/" + OWNER + "/" + NAME + "/tags").openConnection().getInputStream()) {
                try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                    int i2 = i.read();
                    while (i2 != -1) {
                        stream.write(i2);
                        i2 = i.read();
                    }
                    buf = stream.toByteArray();
                }
            }
            JsonArray arr = new Gson().fromJson(new String(buf, StandardCharsets.UTF_8), JsonArray.class);
            String tag = arr.get(0).getAsJsonObject().get("name").getAsString();
            return Float.parseFloat(tag.substring(1));
        } catch (Throwable t) {
            return -1;
        }
    }

    private static void updateCheck(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.player;
        if (player.hasPermissionLevel(3) && !updated) {
            updated = true;
            float latestVersion = getLatestVersion();
            float currentVersion = Float.parseFloat(MagnetCraft.VERSION.substring(1));
            if (latestVersion != -1 && currentVersion < latestVersion) {
                player.sendMessage(Text.translatable("text.magnetcraft.message.update", latestVersion));
                String http = "https://www.curseforge.com/minecraft/mc-mods/magnet-craft/files";
                MutableText text = Text.translatable("text.magnetcraft.message.http");
                text.setStyle(text.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, http)));
                player.sendMessage(text);
            }
        }
    }

    private static void modifyLootTable(ResourceManager resourceManager, LootManager lootManager, Identifier id, LootTable.Builder tableBuilder, LootTableSource source) {
        if (source.isBuiltin() && LootTables.BURIED_TREASURE_CHEST.equals(id)) {
            LootPool.Builder builder = LootPool.builder()
                    .with(ItemEntry.builder(ItemRegistries.MAGNETIC_BATTERY)
                            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f)))
                            .weight(5));
            tableBuilder.pool(builder);
        }
    }
}
