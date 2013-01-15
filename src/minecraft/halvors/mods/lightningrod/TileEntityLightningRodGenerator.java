package halvors.mods.lightningrod;

import java.util.List;

import ic2.api.Direction;
import ic2.api.IWrenchable;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileSourceEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;
import ic2.api.network.NetworkHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityLightningRodGenerator extends TileEntity implements IInventory, IEnergySource, IWrenchable {
	private ItemStack[] inventory;
	
	private boolean addedToEnergyNet = false;
	private int production;
	private boolean canRain;

	public TileEntityLightningRodGenerator() {
		super();

		this.inventory = new ItemStack[1];
	}
	
	@Override
	public void updateEntity() {
		if (worldObj != null && !addedToEnergyNet) {
			if (!worldObj.isRemote) {
				EnergyTileLoadEvent loadEvent = new EnergyTileLoadEvent(this);
				MinecraftForge.EVENT_BUS.post(loadEvent);
			}
			
//			canRain = worldObj.getWorldChunkManager().getBiomeGenAt(xCoord, zCoord).getIntRainfall() > 0;
//			canRain = worldObj.getWorldChunkManager().getBiomeGenAt(xCoord, zCoord).canSpawnLightningBolt();
			
			addedToEnergyNet = true;
		}
		
		int energyProduction = 0;
		
		if (energyProduction > 0) {
			EnergyTileSourceEvent sourceEvent = new EnergyTileSourceEvent(this, energyProduction);
			MinecraftForge.EVENT_BUS.post(sourceEvent);
		}
	}
	
	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (inventory[i] != null) {
			if (inventory[i].stackSize <= j) {
				ItemStack itemStack = inventory[i];
				inventory[i] = null;
				onInventoryChanged();
				
				return itemStack;
			}
			
			ItemStack itemStack1 = inventory[i].splitStack(j);
			
			if (inventory[i].stackSize == 0) {
				inventory[i] = null;
			}
			
			onInventoryChanged();
			
			return itemStack1;
		}
		
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (inventory[i] != null) {
			ItemStack itemStack = inventory[i];
			inventory[i] = null;

			return itemStack;
		}

		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemStack) {
		inventory[i] = itemStack;
		
		if (itemStack != null && itemStack.stackSize > getInventoryStackLimit()) {
			itemStack.stackSize = getInventoryStackLimit();
		}

		onInventoryChanged();
	}

	@Override
	public String getInvName() {
		return "Lightning Rod Generator";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
		if (worldObj == null) {
			return true;
		}

		if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		}

		return entityPlayer.getDistanceSq((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D) <= 64D;
	}

	@Override
	public void openChest() {
		
	}

	@Override
	public void closeChest() {
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		
		NBTTagList tagList = new NBTTagList();
		
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] != null) {
				NBTTagCompound tagCompound1 = new NBTTagCompound();
				tagCompound1.setByte("Slot", (byte) i);
				inventory[i].writeToNBT(tagCompound1);
				tagList.appendTag(tagCompound1);
			}
		}
	
		tagCompound.setTag("Items", tagList);
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {		
		super.readFromNBT(tagCompound);
		
		NBTTagList tagList = tagCompound.getTagList("Items");
		inventory = new ItemStack[getSizeInventory()];
		
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tagCompound1 = (NBTTagCompound) tagList.tagAt(i);
			int j = tagCompound1.getByte("Slot") & 0xff;
			
			if (j >= 0 && j < inventory.length) {
				inventory[j] = ItemStack.loadItemStackFromNBT(tagCompound1);
			}
		}
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
		return true;
	}

	@Override
	public boolean isAddedToEnergyNet() {
		return addedToEnergyNet;
	}

	@Override
	public int getMaxEnergyOutput() {
		return 512;
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}

	@Override
	public short getFacing() {
		return 0;
	}

	@Override
	public void setFacing(short facing) {
		
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public float getWrenchDropRate() {
		return 1.0F;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(LightningRod.blockLightningRodGenerator, 1);
	}
}