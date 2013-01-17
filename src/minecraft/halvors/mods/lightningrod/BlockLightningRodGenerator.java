package halvors.mods.lightningrod;

import ic2.api.Items;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLightningRodGenerator extends BlockContainer {
	public BlockLightningRodGenerator(int blockId) {
		super(blockId, Material.iron);

		setBlockName("Lightning Rod");
		setHardness(3.0F);
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
			return 0;
			
		case 0: // Bottom
			return 1;

		default: // Sides
			return 2;
		}
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int metadata) {
		switch (side) {
		case 1: // Top
			return 0;
			
		case 0: // Bottom
			return 1;

		default: // Sides
			return 2;
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

			player.openGui(LightningRod.instance, 0, world, x, y, z);
		}

		return true;
	}
}