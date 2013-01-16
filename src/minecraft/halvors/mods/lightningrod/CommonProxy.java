package halvors.mods.lightningrod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {
	public static String blockTexture = "/halvors/mods/lightningrod/client/block_textures.png";

	public void registerRenderers() {
		
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		
		if (tileEntity != null && tileEntity instanceof TileEntityLightningRodGenerator) {
			TileEntityLightningRodGenerator tileEntityLightningRod = (TileEntityLightningRodGenerator) tileEntity;
			
			return new ContainerLightningRodGenerator(player.inventory, tileEntityLightningRod);
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
}