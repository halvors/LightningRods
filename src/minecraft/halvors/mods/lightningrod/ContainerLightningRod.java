package halvors.mods.lightningrod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerLightningRod extends Container {
	private TileEntityLightningRod tileEntity;
	private EntityPlayer myPlayer;

	public ContainerLightningRod(PlayerInventory playerInventory, TileEntityCompactSolar tileEntity) {
		this.tileEntity = tileEntity;
		this.playerInventory = playerInventory;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityPlayer) {
		return tileEntity.isUsableByPlayer(entityPlayer);
	}
}
