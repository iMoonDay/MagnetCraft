package com.imoonday.magnetcraft.registries.special;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CallbackRegistries {

    public static final String OWNER = "iMoonDay";
    public static final String NAME = "MagnetCraft";

    public static void register() {
        LootTableEvents.MODIFY.register(CallbackRegistries::modifyLootTable);
    }

    public static void registerClient() {
        ClientPlayConnectionEvents.JOIN.register(CallbackRegistries::updateCheck);
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

    private static void modifyLootTable(ResourceManager resourceManager, LootManager lootManager, Identifier id, LootTable.Builder tableBuilder, LootTableSource source) {
        if (source.isBuiltin() && LootTables.BURIED_TREASURE_CHEST.equals(id)) {
            LootPool.Builder builder = LootPool.builder()
                    .with(ItemEntry.builder(ItemRegistries.MAGNETIC_BATTERY)
                            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f)))
                            .weight(5));
            tableBuilder.pool(builder);
        }
    }

    private static void updateCheck(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player != null) {
            float latestVersion = getLatestVersion();
            float currentVersion = Float.parseFloat(MagnetCraft.VERSION.substring(1));
            if (latestVersion != -1 && currentVersion < latestVersion) {
                MutableText github = getText("Github", "https://github.com/iMoonDay/MagnetCraft");
                MutableText curseforge = getText("Curseforge", "https://www.curseforge.com/minecraft/mc-mods/magnet-craft");
                MutableText modrinth = getText("Modrinth", "https://modrinth.com/mod/magnet-craft");
                player.sendMessage(Text.translatable("text.magnetcraft.message.update", latestVersion).formatted(Formatting.BOLD).append(" ").append(github).append(" ").append(curseforge).append(" ").append(modrinth));
            }
        }
    }

    private static MutableText getText(String content, String website) {
        MutableText text = Text.literal(content).formatted(Formatting.UNDERLINE).formatted(Formatting.GREEN).formatted(Formatting.BOLD);
        text.setStyle(text.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, website)));
        return text;
    }
}
