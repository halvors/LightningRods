package halvors.mods.lightningrods;

import ic2.api.Direction;
import ic2.api.ElectricItem;
import ic2.api.IElectricItem;
import ic2.api.IEnergyStorage;
import ic2.api.IWrenchable;
import ic2.api.Items;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileSourceEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;
import ic2.api.network.NetworkHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;

import net.minecraft.block.Block;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityLightningRodGenerator extends TileEntity implements IInventory, IEnergySource, IEnergyStorage, INetworkDataProvider, INetworkUpdateListener, IWrenchable {
	private final Random random = new Random();
	private final int maxEnergyOutput = 2048;
	private final int production = LightningRods.lightningRodGeneratorProduction;
	private final int capacity = 1000000;
	private final int tier = 3;
	private final int minRodHeight = LightningRods.lightningRodGeneratorMinHeight;
	private final int maxRodHeight = LightningRods.lightningRodGeneratorMaxHeight;
	
	private boolean addedToEnergyNet;
	private boolean canLightningStrike;
	private int energy;
	private ItemStack[] inventory;
	private boolean rodDetected;
	private int rodHeight;
	private int ticker;
	
	public TileEntityLightningRodGenerator() {
		super();

		this.inventory = new ItemStack[1];
	}
	
	@Override
	public void updateEntity() {
		if (worldObj != null && !worldObj.isRemote) {
			if (!addedToEnergyNet) {
				EnergyTileLoadEvent loadEvent = new EnergyTileLoadEvent(this);
				MinecraftForge.EVENT_BUS.post(loadEvent);
				
				this.addedToEnergyNet = true;
			}
			
			if (worldObj.provider.hasNoSky || production > capacity) {
				return;
			}
			
			// Max energy output if not amount is lower.
			int output = Math.min(getMaxEnergyOutput(), getStored());
			
			if (getStored() > getCapacity()) {
				setStored(getCapacity());
			}
			
			if (getStored() > 0 && inventory[0] != null && (Item.itemsList[inventory[0].itemID] instanceof IElectricItem)) {
				int leftOvers = ElectricItem.charge(inventory[0], getStored(), getTier(), false, false); // TODO: Tier 4?
				this.energy -= leftOvers;
			}
	
		    if (output > 0) {
				EnergyTileSourceEvent sourceEvent = new EnergyTileSourceEvent(this, output);
				MinecraftForge.EVENT_BUS.post(sourceEvent);
				
				setStored(getStored() + sourceEvent.amount - output);
			}
			
		    if (ticker % 32 == 0 && worldObj.isThundering()) {
		    	updateRod();
				updateCanLightningStrike();
				
				FMLLog.info("canLightningStrike is " + canLightningStrike());
		    }
		    
			if (ticker % 64 == 0 && worldObj.isThundering()) {
				FMLLog.info("Lightning code.");
				
				if (isRodDetected() && canLightningStrike() && random.nextInt(4096 * worldObj.getHeight()) < getRodHeight() * yCoord + getRodHeight()) {
					FMLLog.info("Lightning stuck.");
					
					if (getStored() <= getCapacity() - production) {
						worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, xCoord, yCoord + getRodHeight() + 1, zCoord));
						addEnergy(production);
					} else {
						doExplosion(output); // TODO: Do the TileEntity gets removed on explosion.
					}
					
					// TODO: When lightning strikes, damage nearby Living Entities.
				}
			}
			
			this.ticker++;
		}
	}
	
	@Override
	public void invalidate() {
		EnergyTileUnloadEvent unloadEvent = new EnergyTileUnloadEvent(this);
		MinecraftForge.EVENT_BUS.post(unloadEvent);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {		
		super.readFromNBT(tagCompound);
		
		NBTTagList tagList = tagCompound.getTagList("Items");
		this.inventory = new ItemStack[getSizeInventory()];
		this.energy = tagCompound.getInteger("Energy");
		
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tagCompound1 = (NBTTagCompound) tagList.tagAt(i);
			int j = tagCompound1.getByte("Slot") & 0xff;
			
			if (j >= 0 && j < inventory.length) {
				inventory[j] = ItemStack.loadItemStackFromNBT(tagCompound1);
			}
		}
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
		tagCompound.setInteger("Energy", energy);
	}
	
	public void updateRod() {
		int height = 0;
		boolean detect = true;
		
		for (int i = yCoord + 1; i < worldObj.getHeight() - 1; i++) {
			if (detect && worldObj.getBlockId(xCoord, i, zCoord) == new ItemStack(LightningRods.blockLightningRod).itemID) {
				height++;
			} else {
				detect = false;
				
				if (worldObj.getBlockId(xCoord, i, zCoord) != 0) {
					height = 0;
					break;
				}
			}
		}
		
		setRodDetected(height >= minRodHeight && height <= maxRodHeight ? true : false);
		setRodHeight(height >= minRodHeight && height <= maxRodHeight ? height : 0);
	}
	
	public boolean isRodDetected() {
		return rodDetected;
	}
	
	public void setRodDetected(boolean rodDetected) {
		this.rodDetected = rodDetected;
	}
	
	public int getRodHeight() {
		return rodHeight;
	}
	
	public void setRodHeight(int rodHeight) {
		this.rodHeight = rodHeight;
	}
	
	public void updateCanLightningStrike() {
		setCanLightningStrike(isRodDetected() ? worldObj.canLightningStrikeAt(xCoord, yCoord + getRodHeight() + 1, zCoord) : false);
	}
	
	public boolean canLightningStrike() {
		return canLightningStrike;
	}
	
	public void setCanLightningStrike(boolean canLightningStrike) {
		this.canLightningStrike = canLightningStrike;
	}
	
	public void doExplosion(int output) {
        float strength = output < 32 ? 0.25F : (output < 128 ? 0.5F : (output < 512 ? 0.75F : (output < 2048 ? 1.0F : 1.25F)));
        
        worldObj.setBlock(xCoord, yCoord, zCoord, 0);
        worldObj.removeBlockTileEntity(xCoord, yCoord, zCoord);
        worldObj.createExplosion(null, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, strength, true);
    }
	
	public int getTier() {
		return tier;
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

		return entityPlayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
	}

	@Override
	public void openChest() {
		
	}

	@Override
	public void closeChest() {
		
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
		return maxEnergyOutput;
	}
	
	@Override
	public int getStored() {
		return energy;
	}

	@Override
	public void setStored(int energy) {
		this.energy = energy;
		
	}

	@Override
	public int addEnergy(int amount) {
		this.energy += amount;
		
		return amount;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}
	
	@Override
	public int getOutput() {
		return maxEnergyOutput;
	}

	@Override
	public boolean isTeleporterCompatible(Direction side) {
		return false;
	}
	
	@Override
	public void onNetworkUpdate(String field) {
		
	}

	@Override
	public List<String> getNetworkedFields() {
		return new ArrayList<String>();
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
		return new ItemStack(LightningRods.blockLightningRodGenerator, 1);
	}
}