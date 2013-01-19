package halvors.mods.lightningrods.client;

import halvors.mods.lightningrods.CommonProxy;
import halvors.mods.lightningrods.ContainerLightningRodGenerator;
import halvors.mods.lightningrods.TileEntityLightningRodGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderers() {
		MinecraftForgeClient.preloadTexture(blockTexture);
	}
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		
		if (tileEntity != null && tileEntity instanceof TileEntityLightningRodGenerator) {
			TileEntityLightningRodGenerator tileEntityLightningRodGenerator = (TileEntityLightningRodGenerator) tileEntity;
			
			return new GuiLightningRodGenerator(new ContainerLightningRodGenerator(player.inventory, tileEntityLightningRodGenerator));
		}
		
		return null;
	}
}