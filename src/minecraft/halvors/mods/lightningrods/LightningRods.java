package halvors.mods.lightningrods;

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

@Mod(modid = "LightningRods", name = "Lightning Rods", version = "0.0.1", dependencies = "required-after:IC2;")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class LightningRods {
	// The instance of your mod that Forge uses.
	@Instance("LightningRods")
	public static LightningRods instance;

	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide = "halvors.mods.lightningrods.client.ClientProxy", serverSide = "halvors.mods.lightningrods.CommonProxy")
	public static CommonProxy proxy;

	public static BlockLightningRodGenerator blockLightningRodGenerator;
	
	public static int lightningRodGeneratorMinHeight;
	public static int lightningRodGeneratorMaxHeight;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());

		try {
			config.load();
			Property block = config.getBlock("lightningRodGenerator", 2679);
			block.comment = "The block id for the Lightning Rod Generator.";
			this.blockLightningRodGenerator = new BlockLightningRodGenerator(block.getInt(2679));
			
			Property minHeight = config.get(Configuration.CATEGORY_GENERAL, "lightningRodGeneratorMinHeight", 8);
			this.lightningRodGeneratorMinHeight = minHeight.getInt(8);
			Property maxHeight = config.get(Configuration.CATEGORY_GENERAL, "lightningRodGeneratorMaxHeight", 32);
			this.lightningRodGeneratorMaxHeight = maxHeight.getInt(32);
		} catch (Exception e) {
			FMLLog.log(Level.SEVERE, e, "LightningRods has a problem loading it's configuration.");
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
		GameRegistry.registerTileEntity(TileEntityLightningRodGenerator.class, "lightningRodGenerator");
		
		proxy.registerRenderers();
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		// Recipe
		GameRegistry.addRecipe(new ItemStack(blockLightningRodGenerator), "iti", "clc", "aga", 
				'i', Items.getItem("ironFence"), 
				't', Items.getItem("hvTransformer"), 
				'c', Items.getItem("trippleInsulatedIronCableItem"), 
				'l', Items.getItem("lapotronCrystal"), 
				'a', Items.getItem("advancedCircuit"), 
				'g', Items.getItem("generator"));
	}
}