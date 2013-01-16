package halvors.mods.lightningrod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ContainerLightningRodGenerator extends Container {
	private TileEntityLightningRodGenerator tileEntity;
	private IInventory playerInventory;

	public ContainerLightningRodGenerator(IInventory playerInventory, TileEntityLightningRodGenerator tileEntity) {
		this.tileEntity = tileEntity;
		this.playerInventory = playerInventory;

		addSlotToContainer(new Slot(tileEntity, 0, 80, 53));

		for (int inventoryRow = 0; inventoryRow < 3; inventoryRow++) {
			for (int inventoryColumn = 0; inventoryColumn < 9; inventoryColumn++) {
				addSlotToContainer(new Slot(playerInventory, inventoryColumn + inventoryRow * 9 + 9, 8 + inventoryColumn * 18, 84 + inventoryRow * 18));
			}
		}

		for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
			addSlotToContainer(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityPlayer) {
		return tileEntity.isUseableByPlayer(entityPlayer);
	}
	
	public TileEntityLightningRodGenerator getTileEntity() {
		return tileEntity;
	}
}