package co.uk.tallpaul.picraft;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;


public class UrlBlock extends Block implements ITileEntityProvider {

	 
	
	public static final PropertyInteger POWER = PropertyInteger.create("pipower", 0, 15);
	private Side side = FMLCommonHandler.instance().getEffectiveSide();
	//private int current_state; //used to see if this block is RECEIVING power
	//private boolean powered_state; //is this block giving off power
	
	
	public UrlBlock (Material material) 
    {
		super(material);
		this.setDefaultState(this.blockState.getBaseState().withProperty(POWER, Integer.valueOf(0)));
            
    }
	
	
	
	private PiTileEntity getTileEntity(IBlockAccess worldObj,BlockPos pos){
		 TileEntity tile = worldObj.getTileEntity(pos);
	    	if (tile != null && tile.getClass() == PiTileEntity.class)
	    		return (PiTileEntity) tile;
	    	else 
	    		return null;
	 }

	  @Override
	  public boolean canProvidePower(){
		  return true;
	  }
	  
	  @Override
	  public int isProvidingWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side){
		  return ((Integer)state.getValue(POWER)).intValue();
	  }
	  
	  @Override
	  public int isProvidingStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side){
		  return this.isProvidingWeakPower(worldIn, pos, state, side);
	  }
	
	    
	    @Override
		public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
	    	PiTileEntity til = this.getTileEntity(worldIn, pos);  	
	    	if (neighborBlock.canProvidePower()){
	    		if (worldIn.isBlockIndirectlyGettingPowered(pos) > 0){
	    			til.setDigitalIo(true);
	    		} else {
	    			til.setDigitalIo(false);
	    		}
	    	}
			
		}
	    
	    @Override
	    public boolean canConnectRedstone(IBlockAccess world, BlockPos pos, EnumFacing side){
	    	return true;
	    }

		@Override
		public TileEntity createNewTileEntity(World worldIn, int meta) {
			// TODO Auto-generated method stub
			return new PiTileEntity();
		}
		
		@Override
	    public boolean hasTileEntity() { 
	        return true;
	    }
		
		
		 public void updatePower(World worldIn, BlockPos pos, int powerin)
		 {
			 if (!worldIn.isRemote){
				 try {
					 	IBlockState iblockstate = worldIn.getBlockState(pos);
					 	System.out.println("pre state: "+((Integer)iblockstate.getValue(POWER)).intValue());
					 	worldIn.setBlockState(pos, iblockstate.withProperty(POWER, Integer.valueOf(powerin)), 4);
					 	iblockstate = worldIn.getBlockState(pos);
					 	System.out.println("post state: "+((Integer)iblockstate.getValue(POWER)).intValue());
					 	worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
					 	worldIn.notifyNeighborsOfStateChange(pos, this);
				 }
				catch (Exception e){
					System.out.println(e.getMessage());
				}
			 }
		 }
		 
		 protected BlockState createBlockState()
		    {
		        return new BlockState(this, new IProperty[] {POWER});
		    }
		 
		 public IBlockState getStateFromMeta(int meta)
		    {
		        return this.getDefaultState().withProperty(POWER, Integer.valueOf(meta));
		    }

		    public int getMetaFromState(IBlockState state)
		    {
		        return ((Integer)state.getValue(POWER)).intValue();
		    }
		
		/*
		@Override
		public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
	    {
	        if (worldIn.isRemote)
	        {
	            return true;
	        }
	        else
	        {
	        	System.out.println("here2");
	            TileEntity tileentity = worldIn.getTileEntity(pos);
	            return tileentity instanceof PiTileEntity ? ((PiTileEntity)tileentity).func_174882_b(playerIn) : false;
	        }
	    }
	    */
	    
	    
	   
}
