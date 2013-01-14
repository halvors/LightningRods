package halvors.mods.lightningrod;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockLightningRod extends Block {
	public BlockLightningRod(int blockId) {
		super(blockId, Material.iron);
		
		setBlockName("CompactSolar");
		setHardness(3.0F);
		//setRequiresSelfNotify();
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	// @Override
	// public String getTextureFile () {
	// return CommonProxy.BLOCK_PNG;
	// }
}
