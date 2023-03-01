package com.imoonday.magnetcraft.screen;

import com.imoonday.magnetcraft.methods.FilterNbtMethods;
import com.imoonday.magnetcraft.screen.handler.FilterableMagnetScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashSet;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class FilterableMagnetScreen extends HandledScreen<FilterableMagnetScreenHandler> {

    private static final Identifier TEXTURE = id("textures/gui/magnet_filter.png");

    /**
     * x:目标x坐标 -1
     * y:目标y坐标 -1
     * u:替代x坐标 -1
     * v:替代y坐标 -1
     * w:替代的宽度
     * h:替代的高度
     */

    public FilterableMagnetScreen(FilterableMagnetScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        HashSet<ItemStack> stacks = new HashSet<>();
        for (int i = 0; i < this.handler.getInventory().size(); i++) {
            if (!this.handler.getInventory().getStack(i).isOf(Items.AIR)) {
                stacks.add(this.handler.getInventory().getStack(i));
            }
        }
        FilterNbtMethods.setFilterItems(this.handler.getStack(), stacks);
        PlayerEntity player = this.handler.getPlayer();
        int slot = this.handler.getSlot();
        if (slot != -1) {
            player.getInventory().getStack(slot).setNbt(this.handler.getStack().getOrCreateNbt());
        } else {
            player.getOffHandStack().setNbt(this.handler.getStack().getOrCreateNbt());
        }
        player.getInventory().markDirty();
    }

    @Override
    public void close() {
        super.close();
        this.handler.getInventory().clear();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
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

}
