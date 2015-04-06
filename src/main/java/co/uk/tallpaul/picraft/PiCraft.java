package co.uk.tallpaul.picraft;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;


@Mod(modid = PiCraft.MODID, version = PiCraft.VERSION)
public class PiCraft
{
    public static final String MODID = "examplemod";
    public static final String VERSION = "1.0";
    public static Block urlBlock;
    
    @SidedProxy(clientSide="co.uk.tallpaul.picraft.ClientProxy",
            serverSide="co.uk.tallpaul.picraft.CommonProxy")
    public static CommonProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) 
    {
             urlBlock = new UrlBlock(Material.ground)
            .setHardness(0.5F).setStepSound(Block.soundTypeMetal)
            .setUnlocalizedName("urlBlock").setCreativeTab(CreativeTabs.tabBlock);
            //MinecraftForge.setBlockHarvestLevel(urlBlock, "shovel", 0);
            GameRegistry.registerBlock(urlBlock, "urlBlock");
            // End Basic Blocks
            GameRegistry.registerTileEntity(PiTileEntity.class, "PiEntity");
            
            //proxy.registerRenderers();
    }
    
}
