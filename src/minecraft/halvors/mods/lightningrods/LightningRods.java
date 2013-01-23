package halvors.mods.lightningrods;

import ic2.api.Items;

import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
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
	
	public static BlockLightningRod blockLightningRod;
	public static BlockLightningRodGenerator blockLightningRodGenerator;
	public static int lightningRodGeneratorProduction;
	public static int lightningRodGeneratorMinHeight;
	public static int lightningRodGeneratorMaxHeight;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());

		try {
			config.load();
			Property blockLightningRod = config.getBlock("lightningRod", 2670);
			blockLightningRod.comment = "The block id for Lightning Rods.";
			this.blockLightningRod = new BlockLightningRod(blockLightningRod.getInt(2340), 0);
			
			Property blockLightningRodGenerator = config.getBlock("lightningRodGenerator", 2341);
			blockLightningRodGenerator.comment = "The block id for Lightning Rod Generators.";
			this.blockLightningRodGenerator = new BlockLightningRodGenerator(blockLightningRodGenerator.getInt(2341), 6);
			
			Property lightningRodGeneratorProduction = config.get(Configuration.CATEGORY_GENERAL, "lightningRodGeneratorProduction", 8);
			lightningRodGeneratorProduction.comment = "How much energy (In EU) to produce per lightning strike.";
			this.lightningRodGeneratorProduction = lightningRodGeneratorProduction.getInt(8);
			
			Property lightningRodGeneratorMinHeight = config.get(Configuration.CATEGORY_GENERAL, "lightningRodGeneratorMinHeight", 8);
			lightningRodGeneratorMinHeight.comment = "The minimum height the Lightning Rod over the Lightning Rod Generator can have in order to work.";
			this.lightningRodGeneratorMinHeight = lightningRodGeneratorMinHeight.getInt(8);
			
			Property lightningRodGeneratorMaxHeight = config.get(Configuration.CATEGORY_GENERAL, "lightningRodGeneratorMaxHeight", 32);
			lightningRodGeneratorMaxHeight.comment = "The maximum height the Lightning Rod over the Lightning Rod Generator can have in order to work.";
			this.lightningRodGeneratorMaxHeight = lightningRodGeneratorMaxHeight.getInt(32);
		} catch (Exception e) {
			FMLLog.log(Level.SEVERE, e, "LightningRods has a problem loading it's configuration.");
		} finally {
			config.save();
		}
	}

	@Init
	public void load(FMLInitializationEvent event) {
		// Block
		LanguageRegistry.addName(blockLightningRod, "Lightning Rod");
		GameRegistry.registerBlock(blockLightningRod, "lightningRod");
		
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
		GameRegistry.addRecipe(new ItemStack(blockLightningRod, 8), "igi", "igi", "igi", 
				'i', Items.getItem("copperIngot"), 
				'g', new ItemStack(Item.ingotIron));
		
		GameRegistry.addRecipe(new ItemStack(blockLightningRodGenerator, 1), "hrh", "tlt", "aga", 
				'r', new ItemStack(blockLightningRod), 
				'h', Items.getItem("hvTransformer"), 
				't', Items.getItem("trippleInsulatedIronCableItem"), 
				'l', Items.getItem("lapotronCrystal"), 
				'a', Items.getItem("advancedCircuit"), 
				'g', Items.getItem("generator"));
	}
}