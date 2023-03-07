package com.imoonday.magnetcraft.screen;

import com.imoonday.magnetcraft.screen.handler.MineralMagnetScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

import static com.imoonday.magnetcraft.registries.common.ItemRegistries.RAW_MAGNET_ITEM;
import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;
import static net.minecraft.item.Items.*;

public class MineralMagnetScreen extends HandledScreen<MineralMagnetScreenHandler> {

    private static final Identifier TEXTURE = id("textures/gui/mineral_magnet.png");

    public MineralMagnetScreen(MineralMagnetScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean onItem;
        boolean offButton = mouseX >= x + 134 && mouseX <= x + 134 + 18 && mouseY >= y + 35 && mouseY <= y + 35 + 16;
        boolean onButton = mouseX >= x + 152 && mouseX <= x + 152 + 18 && mouseY >= y + 35 && mouseY <= y + 35 + 16;
        for (int i = 0; i < 2; i++) {
            onItem = mouseX >= x + 8 + i * 18 && mouseX <= x + 8 + i * 18 + 16 && mouseY >= y + 35 && mouseY <= y + 35 + 16;
            if (onItem) {
                if (this.client != null) {
                    this.handler.onButtonClick(this.client.player, i);
                    if (this.client.interactionManager != null) {
                        this.client.interactionManager.clickButton(this.handler.syncId, i);
                    }
                    if (this.client.player != null) {
                        this.client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
                    }
                }
                return true;
            }
        }
        for (int i = 0; i < 9; i++) {
            onItem = mouseX >= x + 8 + i * 18 && mouseX <= x + 8 + i * 18 + 16 && mouseY >= y + 53 && mouseY <= y + 53 + 16;
            if (onItem) {
                if (this.client != null) {
                    this.handler.onButtonClick(this.client.player, 2 + i);
                    if (this.client.interactionManager != null) {
                        this.client.interactionManager.clickButton(this.handler.syncId, 2 + i);
                    }
                    if (this.client.player != null) {
                        this.client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
                    }
                }
                return true;
            }
        }
        if (offButton) {
            if (this.client != null) {
                this.handler.onButtonClick(this.client.player, 11);
                if (this.client.interactionManager != null) {
                    this.client.interactionManager.clickButton(this.handler.syncId, 11);
                }
                if (this.client.player != null) {
                    this.client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
                }
            }
            return true;
        }
        if (onButton) {
            if (this.client != null) {
                this.handler.onButtonClick(this.client.player, 12);
                if (this.client.interactionManager != null) {
                    this.client.interactionManager.clickButton(this.handler.syncId, 12);
                }
                if (this.client.player != null) {
                    this.client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
                }
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        Item[] items = new Item[]{QUARTZ, RAW_MAGNET_ITEM};
        for (int i = 0; i < 2; i++) {
            if (canDestory(items[i])) {
                drawTexture(matrices, x + 8 + i * 18, y + 35, 8 + i * 18, 177, 16, 16);
            }
        }
        items = new Item[]{COAL, RAW_IRON, RAW_GOLD, GOLD_NUGGET, DIAMOND, REDSTONE, RAW_COPPER, EMERALD, LAPIS_LAZULI};
        for (int i = 0; i < 9; i++) {
            if (canDestory(items[i])) {
                drawTexture(matrices, x + 8 + i * 18, y + 53, 8 + i * 18, 195, 16, 16);
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    public boolean canDestory(Item requiredItem) {
        ItemStack stack = this.handler.getSlot() != -1 ? this.handler.getInventory().getStack(this.handler.getSlot()) : this.handler.getInventory().player.getOffHandStack();
        if (stack != null && stack.getNbt() != null && stack.getNbt().getBoolean("Filterable")) {
            String item = Registries.ITEM.getId(requiredItem).toString();
            NbtList list = stack.getNbt().getList("Cores", NbtElement.COMPOUND_TYPE);
            return list.stream().anyMatch(nbtElement -> nbtElement instanceof NbtCompound && Objects.equals(((NbtCompound) nbtElement).getString("id"), item) && ((NbtCompound) nbtElement).getBoolean("enable"));
        }
        return false;
    }
}
