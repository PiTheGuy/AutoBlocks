package pitheguy.autoblocks.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import pitheguy.autoblocks.AutoBlocks;
import pitheguy.autoblocks.util.SchematicHandler;

public class SchematicCreatorScreen extends Screen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(AutoBlocks.MODID, "textures/gui/schematic_creator.png");

    private int leftPos;
    private int topPos;
    private final int imageWidth;
    private final int imageHeight;
    private final BlockPos firstCorner;
    private final BlockPos secondCorner;
    private EditBox name;
    private SaveButton saveButton;

    public SchematicCreatorScreen(BlockPos firstCorner, BlockPos secondCorner) {
        super(Component.translatable("gui.schematic_creator.title"));
        this.firstCorner = firstCorner;
        this.secondCorner = secondCorner;
        this.imageWidth = 176;
        this.imageHeight = 41;
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        name = new EditBox(this.font, this.leftPos + 5, this.topPos + 17, 120, 16, Component.literal("Schematic name"));
        name.setTextColor(-1);
        name.setValue("");
        name.setCanLoseFocus(false);
        name.setResponder(input -> saveButton.active = !input.isEmpty());
        this.addRenderableWidget(name);
        saveButton = new SaveButton();
        saveButton.active = false;
        this.addRenderableWidget(saveButton);
        this.setInitialFocus(name);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.minecraft.getTextureManager().bindForSetup(TEXTURE);
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER && this.saveButton.active) this.saveButton.onPress();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    class SaveButton extends AbstractButton {

        public SaveButton() {
            super(SchematicCreatorScreen.this.leftPos + 128, SchematicCreatorScreen.this.topPos + 17, 40, 16, Component.translatable("gui.schematic_creator.save"));
        }

        @Override
        public void onPress() {
            SchematicCreatorScreen.this.minecraft.setScreen(null);
            SchematicHandler.saveSchematic(SchematicHandler.SCHEMATICS, SchematicCreatorScreen.this.name.getValue(), false, SchematicCreatorScreen.this.minecraft.level, SchematicCreatorScreen.this.firstCorner, SchematicCreatorScreen.this.secondCorner);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            defaultButtonNarrationText(narrationElementOutput);
        }
    }
}
