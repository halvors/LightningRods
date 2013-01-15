package halvors.mods.lightningrod.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

public class GuiLightningRodGenerator extends GuiContainer {
	public GuiLightningRodGenerator(Container container) {
		super(container);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
        fontRenderer.drawString("Lightning Rod Generator", 8, 6, 0x404040);
        fontRenderer.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		int texture = mc.renderEngine.getTexture("/halvors/mods/lightningrod/client/GuiLightningRodGenerator.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(texture);
		int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}