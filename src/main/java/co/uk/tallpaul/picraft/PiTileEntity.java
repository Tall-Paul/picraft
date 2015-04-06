package co.uk.tallpaul.picraft;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.command.CommandResultStats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class PiTileEntity extends TileEntity implements IUpdatePlayerListBox{
	public static final String publicName = "tileEntityPi";
	public static final PropertyInteger POWER = PropertyInteger.create("pipower", 0, 15);
    private String name = "tileEntityPi";

    
    int tick = 0;
    boolean lastWrite = false;
    boolean isSwitchedOn = false;
    
    private String url = ""; // gpio web interface url
    public String pin = "-1"; // gpio pin
    public String type = "-1"; //0 = digital in, 1 = digital out.
    
 
    public String getName() { 	
        return name;
    }
    
    public boolean getCurrentPower(){
    	System.out.println("getCurrentPower called");
    	if (worldObj.isRemote){
    	if (this.getConfig() == false)
    		return false;
    	String val = this.getValue();
    	if (val.equals("1")){
    		System.out.println("returning true");
    		return true;
    	}
    	else{
    		System.out.println("returning false ["+val+"]");
    		return false;
    	}
    	} else {
    		return false;
    	}
    }
    
    /*
     * read value from a pi input
     * 
     */
    private String getValue(){
    	if (this.pin.equals("-1"))
    	{
    		return "0";
    	}
    	URL oracle = null;
		try {
			oracle = new URL("http://"+this.url+":8000/GPIO/"+this.pin+"/value");
		} catch (MalformedURLException e) {
			System.out.println("Malformed URL");
			return "0";
		}
        BufferedReader in = null;
		try {
			in = new BufferedReader(
			new InputStreamReader(oracle.openStream()));
		} catch (IOException e1) {
			System.out.println("IO Exception");
			return "0";
		}

        String inputLine = null;
        try {
			while ((inputLine = in.readLine()) != null){
				in.close();
				//System.out.println("got "+inputLine);
				return inputLine;
			    
			}
		} catch (IOException e) {
			System.out.println("IO Exception 2");
			return "0";
		}
        try {
			in.close();
		} catch (IOException e) {
			System.out.println("IO Exception 3");
			return "0";
		}
        return "0";
    }
    
    /*
     * write value to a pi output
     * 
     * 
     */
    private void setValue(String val){
    	
    	String urlParameters  = "";
    	byte[] postData       = urlParameters.getBytes( Charset.forName( "UTF-8" ));
    	int    postDataLength = postData.length;
    	String request        = "http://"+this.url+":8000/GPIO/"+this.pin+"/value/"+val;
    	System.out.println(request);
    	URL url = null;
    	System.out.println("setValue called");
    	
		try {
			url = new URL( request );
		} catch (MalformedURLException e) {
			System.out.println("Malformed url");
		}
    	HttpURLConnection cox = null;
		try {
			cox = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			System.out.println("IO Exception");
		}           
    	cox.setDoOutput( true );
    	cox.setDoInput ( true );
    	cox.setInstanceFollowRedirects( false );
    	try {
			cox.setRequestMethod( "POST" );
		} catch (ProtocolException e) {
			System.out.println("Protocol exception");
		}
    	cox.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
    	cox.setRequestProperty( "charset", "utf-8");
    	cox.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
    	cox.setUseCaches( false );
    	try( DataOutputStream wr = new DataOutputStream( cox.getOutputStream())) {
    	   wr.write( postData );
    	} catch (IOException e) {
    		System.out.println("IO exception2");
		}
    	try {
			Object response = cox.getContent();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    
    /*
     * get url block associated with this tile entity
     * 
     */
    private UrlBlock getBlock(){
    	Block block = worldObj.getBlockState(this.getPos()).getBlock();
    	if (block != null && block.getClass() == UrlBlock.class){
    		return (UrlBlock) block;
    	}
    	else 
    		return null;
    }
   
    
    public void setDigitalIo(boolean val){	
    	if (val == this.lastWrite)
    			return;
    	if (this.getConfig() == false)
    		return;
    	if (this.type == "1"){
    		this.lastWrite = val;
    		if (val == true){
    			this.setValue("1");
    		} else {
    			this.setValue("0");
    		}
    	}
    }
    
    
    public boolean getDigitalIo(boolean updateBlock){
    	//TODO: poll raspberry pi here
    	World worldIn = this.getWorld();
    	BlockPos posin = this.getPos();
    	try {
    		String val = this.getValue();
    		//System.out.println("Got value of "+val);
    		boolean input = false;
    		if (val.equals("1")){
    			input = true;
    		}
    		else{
    			input = false;
    		}	
    		if (input != this.isSwitchedOn){
    			
    			if (input == true){
    				System.out.println("switching on");
    				this.isSwitchedOn = true;
    				if (updateBlock == true)
    					this.getBlock().updatePower(worldIn,posin,15);
    				return true;
    			}
    			else
    			{
    				System.out.println("switching off");
    				this.isSwitchedOn = false;
    				if (updateBlock == true)
    					this.getBlock().updatePower(worldIn, posin,0);
    				return false;
    			}
            	
    			//this.isSwitchedOn = input;
    		}
    		//this.isSwitchedOn = input;
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		System.out.println("error getting digital IO");
    		return false;
    	}
		return isSwitchedOn;
    	
    }
  
    /*
     * Reads configuration for block from a neighbouring sign
     * 
     */
    public boolean getConfig(){
    	BlockPos curpos = this.getPos();
    	int xPos = curpos.getX();
    	int yPos = curpos.getY();
    	int zPos = curpos.getZ();
    	
    	int xLoop = -1;
    	int yLoop = -1;
    	int zLoop = -1;
    	//System.out.println("Getting config");
    	for (xLoop = -1; xLoop <= 1; ++xLoop){
    		for (yLoop = -1; yLoop <= 1; ++yLoop){
    			for (zLoop = -1; zLoop <= 1; ++zLoop){
    				BlockPos tmp = new BlockPos(xPos + xLoop, yPos + yLoop, zPos + zLoop);
                    TileEntity tile = worldObj.getTileEntity(tmp);
                    if (tile != null){
                    	if (tile.getClass() == TileEntitySign.class){
                    		TileEntitySign configTileEntity = (TileEntitySign) tile;
                    		if (configTileEntity.signText[0].getUnformattedText().equals("pi")){                 			
                    			this.url = configTileEntity.signText[1].getUnformattedText();
                    			this.pin = configTileEntity.signText[2].getUnformattedText();
                    			this.type = configTileEntity.signText[3].getUnformattedText();
                    			if (this.type.equals("in"))
                    				this.type = "0";
                    			if (this.type.equals("out"))
                    				this.type = "1";              			
                    			//System.out.println(this.url+" "+this.pin+" "+this.type);
                    			return true;
                    		}
                    		
                    			
                    	}
                    }
                    
            	}
        	}
    	}
    	
    	return false;
    }
    
    
    @Override
    public void update()
    { 	
    	if(!worldObj.isRemote) { 		 
            tick++;
            if(tick == 10) {
            	int i = -1;        	
            	if (this.getConfig()){
            		if (this.type.equals("0")){
                		this.getDigitalIo(true);
                	}
            	}
                tick = 0;
            }
        }
    }


}
