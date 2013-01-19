package halvors.mods.lightningrods.client;

import halvors.mods.lightningrods.ContainerLightningRodGenerator;
import halvors.mods.lightningrods.TileEntityLightningRodGenerator;
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
		fontRenderer.drawString(tileEntity.getInvName(), (xSize - fontRenderer.getStringWidth(tileEntity.getInvName())) / 2, 6, 0x404040);
        fontRenderer.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		int texture = mc.renderEngine.getTexture("/halvors/mods/lightningrods/client/sprites/GuiLightningRodGenerator.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(texture);
		int x = (width - xSize) / 2;
        int z = (height - ySize) / 2;
        drawTexturedModalRect(x, z, 0, 0, xSize, ySize);
        
        if (tileEntity.canLightningStrike()) {
            drawTexturedModalRect(x + 67, z + 36, 176, 0, 12, 14);
        }
        
        if (tileEntity.getStored() > 0) {
            int chargeLevel = 24 * tileEntity.getStored() / tileEntity.getCapacity();
            drawTexturedModalRect(x + 94, z + 35, 176, 14, chargeLevel + 1, 16);
        }
	}
}