package com.imoonday.magnetcraft.registries.special;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.imoonday.magnetcraft.MagnetCraft;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

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

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
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
        });
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

}
