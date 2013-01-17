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
		fontRenderer.drawString(tileEntity.getInvName(), (xSize - fontRenderer.getStringWidth(tileEntity.getInvName())) / 2, 6, 0x404040);
        fontRenderer.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
        
        int energy = tileEntity.getStored();

        if (energy > tileEntity.getCapacity()) {
        	energy = tileEntity.getCapacity();
        }
        
        fontRenderer.drawString(" " + energy, 103, 45, 0x404040);
        fontRenderer.drawString("/" + tileEntity.getCapacity(), 103, 55, 0x404040);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		int texture = mc.renderEngine.getTexture("/halvors/mods/lightningrod/client/sprites/GuiLightningRodGenerator.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(texture);
		int x = (width - xSize) / 2;
        int z = (height - ySize) / 2;
        drawTexturedModalRect(x, z, 0, 0, xSize, ySize);
        
        if (tileEntity.canLightningStrike) {
            drawTexturedModalRect(x + 57, z + 36, 176, 0, 12, 14);
        }
        
        if (tileEntity.getStored() > 0) {
            int chargeLevel = (int) 24.0F * tileEntity.getStored() / tileEntity.getCapacity();
            drawTexturedModalRect(x + 85, z + 34, 176, 14, chargeLevel + 1, 16);
        }
	}
}