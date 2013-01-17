package halvors.mods.lightningrod;

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

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityLightningRodGenerator extends TileEntity implements IInventory, IEnergySource, INetworkDataProvider, INetworkUpdateListener, IWrenchable {
	private final Random random = new Random();
	private final int maxEnergyOutput = 512;
	private final int minRodHeight = 8;
	private final int maxRodHeight = 32;
	private final int production = 10000;
	private final int capacity = 100000;
	
	private boolean addedToEnergyNet;
	public boolean canLightningStrike;
	private int energy = 0;
	private ItemStack[] inventory;
	private boolean rodDetected;
	private int rodHeight;
	private int tick;
	
	
	public TileEntityLightningRodGenerator() {
		super();

		this.inventory = new ItemStack[1];
		this.tick = random.nextInt(64);
	}
	
	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			if (worldObj != null && !addedToEnergyNet) {
				if (worldObj.isRemote) {
					NetworkHelper.requestInitialData(this);
				} else {
					EnergyTileLoadEvent loadEvent = new EnergyTileLoadEvent(this);
					MinecraftForge.EVENT_BUS.post(loadEvent);
				}
				
				this.addedToEnergyNet = true;
			}
			
			if (energy > capacity) {
				energy = capacity;
			}
			
			if (tick-- == 0) {
				detectRod();
				this.tick = 64;
				
				if (rodDetected) {
					this.canLightningStrike = worldObj.canLightningStrikeAt(xCoord, yCoord + getRodHeight() + 1, zCoord);
					
					if (getRodHeight() != 0 && getRodHeight() >= minRodHeight && getRodHeight() <= maxRodHeight && canLightningStrike) { // && random.nextInt(4096 * this.worldObj.getHeight()) < getRodHeight() * yCoord + getRodHeight()) {
						worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, xCoord, yCoord + getRodHeight() + 1, zCoord));
						addEnergy(production);
					}
				}
			}
			
			if (energy > 0 && inventory[0] != null && (Item.itemsList[inventory[0].itemID] instanceof IElectricItem)) {
				int leftOvers = ElectricItem.charge(inventory[0], energy, 3, false, false);
				this.energy -= leftOvers;
			}
			
			int output = Math.min(maxEnergyOutput, energy);
	
		    if (output > 0) {
				EnergyTileSourceEvent sourceEvent = new EnergyTileSourceEvent(this, output);
				MinecraftForge.EVENT_BUS.post(sourceEvent);
				
				setStored(energy + sourceEvent.amount - output);
			}
		}
	}
	
	@Override
	public void invalidate() {
		EnergyTileUnloadEvent unloadEvent = new EnergyTileUnloadEvent(this);
		MinecraftForge.EVENT_BUS.post(unloadEvent);
	}
	
	public void detectRod() {
		int height = 0;
		boolean detect = true;
		
		for (int i = yCoord + 1; i < worldObj.getHeight() - 1; i++) {
			if (detect && worldObj.getBlockId(xCoord, i, zCoord) == Items.getItem("ironFence").itemID) {
				height++;
			} else {
				detect = false;
				
				if (worldObj.getBlockId(xCoord, i, zCoord) != 0) {
					height = 0;
					break;
				}
			}
		}
		
		if (!detect && height != 0) {
			this.rodDetected = true;
			this.rodHeight = height;
		}
	}
	
	public boolean isRodDetected() {
		return rodDetected;
	}
	
	public int getRodHeight() {
		return rodHeight;
	}
	
	public boolean getCanLightningStrike() {
		return canLightningStrike;
	}
	
	public void setCanLightningStrike(boolean canLightningStrike) {
		this.canLightningStrike = canLightningStrike;
	}
	
	public int getStored() {
		return energy;
	}

	public void setStored(int energy) {
		this.energy = energy;
	}
	
	public int addEnergy(int amount) {
		this.energy += amount;
		
		return amount;
	}

	public int getCapacity() {
		return capacity;
	}
	
//	public void onPostTickUpdate() {
//		if (worldObj.isRaining() || worldObj.isThundering()) {
//			
//		}
//		
//        if (!worldObj.isRemote && this.mTickTimer % 256L == 0L && (this.worldObj.isThundering() || this.worldObj.isRaining() && GT_Mod.Randomizer.nextInt(10) == 0)) {
//            int var1 = 0;
//            boolean var2 = true;
//            
//            for (int i = yCoord + 1; i < this.worldObj.getHeight() - 1; i++) {
//                if (var2 && worldObj.getBlockId(xCoord, i, zCoord) == Items.getItem("ironFence").itemID) {
//                    var1++;
//                } else {
//                    var2 = false;
//
//                    if (worldObj.getBlockId(xCoord, i, zCoord) != 0) {
//                        var1 = 0;
//                        break;
//                    }
//                }
//            }
//
//            if (!this.worldObj.isThundering() && thi.yCoord + svar1 < 128) {
//                var1 = 0;
//            }
//
//            if (GT_Mod.Randomizer.nextInt(4096 * this.worldObj.getHeight()) < var1 * (this.yCoord + var1)) {
//                setStoredEnergy(25000000);
//                worldObj.addWeatherEffect(newt EntityLightningBol(worldObj, (double) xCoord, (double) (yCoord + var1), (double) zCoord));
//            }
//        }
//    }
	
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
		return new ItemStack(LightningRod.blockLightningRodGenerator, 1);
	}
}