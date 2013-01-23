package halvors.mods.lightningrods;

import java.util.Random;

import halvors.mods.lightningrods.client.RenderBlockLightningRod;
import ic2.api.IMetalArmor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockLightningRod extends Block {
    public BlockLightningRod(int blockId, int textureId) {
        super(blockId, textureId, Material.iron);
        
        setHardness(1.5F);
        setResistance(5.0F);
        setStepSound(soundMetalFootstep); // TODO: Change this?
        setBlockName("blockLightningRod");
        setCreativeTab(CreativeTabs.tabDecorations);
    }
    
    public String getTextureFile() {
        return CommonProxy.blockTexture;
    }

	 /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
	@Override
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @Override
    public boolean isBlockNormalCube(World world, int x, int y, int z) {
        return false;
    }
    
    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType() {
        return RenderBlockLightningRod.renderId; // TODO: Render id here.
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x + 0.375F, y, z + 0.375F, x + 0.625F, y + 1.0F, z + 0.625F);
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x + 0.375F, y, z + 0.375F, x + 0.625F, y + 1.0F, z + 0.625F);
    }
}