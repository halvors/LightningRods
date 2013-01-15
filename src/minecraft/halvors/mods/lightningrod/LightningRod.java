package halvors.mods.lightningrod;

import ic2.api.Items;

import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "LightningRod", name = "LightningRod", version = "0.0.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class LightningRod {
	// The instance of your mod that Forge uses.
	@Instance("LightningRod")
	public static LightningRod instance;

	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide = "halvors.mods.lightningrod.client.ClientProxy", serverSide = "halvors.mods.lightningrod.CommonProxy")
	public static CommonProxy proxy;

	public static BlockLightningRodGenerator blockLightningRodGenerator;
	public static int productionRate = 1;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());

		try {
			config.load();
			Property block = config.getBlock("lightningRodGenerator", 2679);
			block.comment = "The block id for the Lightning Rod Generator.";
			blockLightningRodGenerator = new BlockLightningRodGenerator(block.getInt(2679));
			Property scale = config.get(Configuration.CATEGORY_GENERAL, "scaleFactor", 1);
			scale.comment = "The EU generation scaling factor. "
					+ "The average number of ticks needed to generate one EU packet."
					+ "1 is every tick, 2 is every other tick etc. "
					+ "Will still generate a whole packet (512 EU).";
			productionRate = scale.getInt(1);
		} catch (Exception e) {
			FMLLog.log(Level.SEVERE, e, "LightningRod was unable to load it's configuration successfully.");
			throw new RuntimeException(e);
		} finally {
			config.save();
		}
	}

	@Init
	public void load(FMLInitializationEvent event) {
		// Block
		LanguageRegistry.addName(blockLightningRodGenerator, "Lightning Rod Generator");
		GameRegistry.registerBlock(blockLightningRodGenerator, "lightningRodGenerator");

		// TileEntity
		GameRegistry.registerTileEntity(TileEntityLightningRodGenerator.class, "LightningRodGenerator");
		
		proxy.registerRenderers();
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		// Recipe
		GameRegistry.addRecipe(new ItemStack(blockLightningRodGenerator), "rir", "ctc", "aga", 
				'r', Items.getItem("refinedIronIngot"), 
				'i', Items.getItem("ironFence"), 
				'c', Items.getItem("trippleInsulatedIronCableItem"), 
				't', Items.getItem("hvTransformer"), 
				'a', Items.getItem("advancedCircuit"), 
				'g', Items.getItem("generator"));
	}
}