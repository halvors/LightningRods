package halvors.mods.lightningrods;

import ic2.api.Items;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLightningRodGenerator extends BlockContainer {
	private static final Random random = new Random();
	
	public BlockLightningRodGenerator(int blockId, int textureId) {
		super(blockId, textureId, Material.iron);
		
		setHardness(3.0F);
		setBlockName("blockLightningRodGenerator");
		setRequiresSelfNotify();
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityLightningRodGenerator();
	}

	@Override
	public String getTextureFile() {
		return CommonProxy.blockTexture;
	}

	@Override
	public int getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
		switch (side) {
		case 1: // Top
			return 6;
			
		case 0: // Bottom
			return 7;

		default: // Sides
			return 8;
		}
	}
	
	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int metadata) {
		switch (side) {
		case 1: // Top
			return 6;
			
		case 0: // Bottom
			return 7;
			
		default: // Sides
			return 8;
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int s, float f1, float f2, float f3) {
		ItemStack itemStack = player.getHeldItem();
		
		if (player.isSneaking() || itemStack != null && itemStack.itemID == Items.getItem("ironFence").itemID) {
			return false;
		}

		if (world.isRemote) {
			return true;
		}

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		
		if (tileEntity != null && tileEntity instanceof TileEntityLightningRodGenerator) {
			TileEntityLightningRodGenerator tileEntityLightningRod = (TileEntityLightningRodGenerator) tileEntity;

			player.openGui(LightningRods.instance, 0, world, x, y, z);
		}

		return true;
	}
	
	@Override
	public int idDropped(int i, Random random, int j) {
		return 0;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int i, int j) {
		TileEntityLightningRodGenerator tileEntity = (TileEntityLightningRodGenerator) world.getBlockTileEntity(x, y, z);
		
		if (tileEntity != null) {
			dropContent(0, tileEntity, world);
		}
		
		super.breakBlock(world, x, y, z, i, j);
	}

	public void dropContent(int newSize, TileEntityLightningRodGenerator tileEntity, World world) {
		for (int i = newSize; i < tileEntity.getSizeInventory(); i++) {
		    ItemStack itemStack = tileEntity.getStackInSlot(i);
		    
		    float f = random.nextFloat() * 0.8F + 0.1F;
		    float f1 = random.nextFloat() * 0.8F + 0.1F;
		    float f2 = random.nextFloat() * 0.8F + 0.1F;
		    
		    if (itemStack == null) {
		    	continue;
		    }
		    
		    while (itemStack.stackSize > 0) {
		        int j = random.nextInt(21) + 10;
		        
		        if (j > itemStack.stackSize){
		            j = itemStack.stackSize;
		        }
		        
		        itemStack.stackSize -= j;
		        float f3 = 0.05F;
		        
		        EntityItem entityItem = new EntityItem(world, tileEntity.xCoord + f, tileEntity.yCoord + (newSize > 0 ? 1 : 0) + f1, tileEntity.zCoord + f2, new ItemStack(itemStack.itemID, j, itemStack.getItemDamage()));
		        entityItem.motionX = random.nextGaussian() * f3;
		        entityItem.motionY = random.nextGaussian() * f3 + 0.2F;
		        entityItem.motionZ = random.nextGaussian() * f3;
		        
		        if (itemStack.hasTagCompound()) {
		        	entityItem.func_92014_d().setTagCompound((NBTTagCompound) itemStack.getTagCompound().copy());
		        }
		        
		        world.spawnEntityInWorld(entityItem);
		    }
		}
	}
	
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		super.randomDisplayTick(world, x, y, z, random);
		
		world.spawnParticle("depthsuspend", (float) x + random.nextFloat(), (float) y + 1.1F, (float) z + random.nextFloat(), 20.0D, 20.0D, 20.0D);
	}
}