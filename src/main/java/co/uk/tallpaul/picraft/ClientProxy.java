package co.uk.tallpaul.picraft;

import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.FMLClientHandler;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderInformation()
	{
		//MinecraftForgeClient.preloadTexture("/lightningcraft.png");
	}

	@Override
	public void registerTileEntitySpecialRenderer(/*PLACEHOLDER*/)
	{
		
	}

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}
}

