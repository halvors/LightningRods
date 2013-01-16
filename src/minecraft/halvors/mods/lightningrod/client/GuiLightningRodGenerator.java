package halvors.mods.lightningrod.client;

import halvors.mods.lightningrod.ContainerLightningRodGenerator;
import halvors.mods.lightningrod.TileEntityLightningRodGenerator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

public class GuiLightningRodGenerator extends GuiContainer {
	private ContainerLightningRodGenerator container;
	private TileEntityLightningRodGenerator tileEntity;
	
	public GuiLightningRodGenerator(ContainerLightningRodGenerator container) {
		super(container);
		
		this.container = container;
		this.tileEntity = container.getTileEntity();
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
        int z = (height - ySize) / 2;
        drawTexturedModalRect(x, z, 0, 0, xSize, ySize);

        if (tileEntity.getStorage() > 0) {
            int var7 = (int) 24.0F * tileEntity.getStorage() / tileEntity.getMaxStorage();
            this.drawTexturedModalRect(x + 79, z + 34, 176, 14, var7 + 1, 16);
        }
	}
}