/**
    Copyright (C) 2017 by jabelar

    This file is part of jabelar's Minecraft Forge modding examples; as such,
    you can redistribute it and/or modify it under the terms of the GNU
    General Public License as published by the Free Software Foundation,
    either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    For a copy of the GNU General Public License see <http://www.gnu.org/licenses/>.
*/

package com.blogspot.jabelarminecraft.wildanimals.utilities;

import com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

// TODO: Auto-generated Javadoc
/**
 * @author jabelar
 *
 */
public class Utilities 
{
	/*
	 * Block and BlockPos Utilities
	 */
    public static boolean isTeleportFriendlyBlock(Entity parEntity, int x, int p_192381_2_, int y, int p_192381_4_, int p_192381_5_)
    {
        BlockPos blockpos = new BlockPos(x + p_192381_4_, y - 1, p_192381_2_ + p_192381_5_);
        IBlockState iblockstate = parEntity.world.getBlockState(blockpos);
        return iblockstate.getBlockFaceShape(parEntity.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(parEntity) && parEntity.world.isAirBlock(blockpos.up()) && parEntity.world.isAirBlock(blockpos.up(2));
    }
    
	/*
	 * Text Utilities
	 */
	
	/**
	 * String to rainbow.
	 *
	 * @param parString the par string
	 * @param parReturnToBlack the par return to black
	 * @return the string
	 */
	public static String stringToRainbow(String parString, boolean parReturnToBlack)
	{
		int stringLength = parString.length();
		if (stringLength < 1)
		{
			return "";
		}
		String outputString = "";
		TextFormatting[] colorChar = 
			{
			TextFormatting.RED,
			TextFormatting.GOLD,
			TextFormatting.YELLOW,
			TextFormatting.GREEN,
			TextFormatting.AQUA,
			TextFormatting.BLUE,
			TextFormatting.LIGHT_PURPLE,
			TextFormatting.DARK_PURPLE
			};
		for (int i = 0; i < stringLength; i++)
		{
			outputString = outputString+colorChar[i%8]+parString.substring(i, i+1);
		}
		// return color to a common one after (most chat is white, but for other GUI might want black)
		if (parReturnToBlack)
		{
			return outputString+TextFormatting.BLACK;
		}
		return outputString+TextFormatting.WHITE;
	}

	/**
	 * String to rainbow.
	 *
	 * @param parString the par string
	 * @return the string
	 */
	// by default return to white (for chat formatting).
	public static String stringToRainbow(String parString)
	{
		return stringToRainbow(parString, false);
	}
	
	/**
	 * String to golden.
	 *
	 * @param parString the par string
	 * @param parShineLocation the par shine location
	 * @param parReturnToBlack the par return to black
	 * @return the string
	 */
	public static String stringToGolden(String parString, int parShineLocation, boolean parReturnToBlack)
	{
		int stringLength = parString.length();
		if (stringLength < 1)
		{
			return "";
		}
		String outputString = "";
		for (int i = 0; i < stringLength; i++)
		{
			if ((i+parShineLocation+Minecraft.getSystemTime()/20)%88==0)
			{
				outputString = outputString+TextFormatting.WHITE+parString.substring(i, i+1);				
			}
			else if ((i+parShineLocation+Minecraft.getSystemTime()/20)%88==1)
			{
				outputString = outputString+TextFormatting.YELLOW+parString.substring(i, i+1);				
			}
			else if ((i+parShineLocation+Minecraft.getSystemTime()/20)%88==87)
			{
				outputString = outputString+TextFormatting.YELLOW+parString.substring(i, i+1);				
			}
			else
			{
				outputString = outputString+TextFormatting.GOLD+parString.substring(i, i+1);								
			}
		}
		// return color to a common one after (most chat is white, but for other GUI might want black)
		if (parReturnToBlack)
		{
			return outputString+TextFormatting.BLACK;
		}
		return outputString+TextFormatting.WHITE;
	}

	/**
	 * String to golden.
	 *
	 * @param parString the par string
	 * @param parShineLocation the par shine location
	 * @return the string
	 */
	// by default return to white (for chat formatting).
	public static String stringToGolden(String parString, int parShineLocation)
	{
		return stringToGolden(parString, parShineLocation, false);
	}

	/**
	 * Gets the entity by ID.
	 *
	 * @param entityID the entity ID
	 * @param world the world
	 * @return the entity by ID
	 */
	public static Entity getEntityByID(int entityID, World world)        
	{         
	    for(Object o: world.getLoadedEntityList())                
	    {                        
	        if(((Entity)o).getEntityId() == entityID)                        
	        {   
	        	// DEBUG
	            // System.out.println("Found the entity");                                
	            return ((Entity)o);                        
	        }                
	    }                
	    return null;        
	} 

	/**
	* Based on code from http://pages.cs.wisc.edu/~ltorrey/cs302/examples/PigLatinTranslator.java
	* Method to translate a sentence word by word.
	* @param s The sentence in English
	* @return The pig latin version
	*/
	public static String toPigLatin(String s) 
	{
		String latin = "";
	    int i = 0;
	    while (i<s.length()) 
	    {
	    	// Take care of punctuation and spaces
	    	while (i<s.length() && !isLetter(s.charAt(i))) 
	    	{
	    		latin = latin + s.charAt(i);
	    		i++;
	    	}

	    	// If there aren't any words left, stop.
	    	if (i>=s.length()) break;

	    	// Otherwise we're at the beginning of a word.
	    	int begin = i;
	    	while (i<s.length() && isLetter(s.charAt(i))) 
	    	{
	    		i++;
	    	}

	    	// Now we're at the end of a word, so translate it.
	    	int end = i;
	    	latin = latin + pigWord(s.substring(begin, end));
	    }
	    return latin;
	}

	/**
	* Method to test whether a character is a letter or not.
	* @param c The character to test
	* @return True if it's a letter
	*/
	private static boolean isLetter(char c) 
	{
		return ( (c >='A' && c <='Z') || (c >='a' && c <='z') );
	}

	/**
	* Method to translate one word into pig latin.
	* @param word The word in english
	* @return The pig latin version
	*/
	private static String pigWord(String word) 
	{
		int split = firstVowel(word);
		return word.substring(split)+"-"+word.substring(0, split)+"ay";
	}

	/**
	* Method to find the index of the first vowel in a word.
	* @param word The word to search
	* @return The index of the first vowel
	*/
	private static int firstVowel(String word) 
	{
		word = word.toLowerCase();
	    for (int i=0; i<word.length(); i++)
	    {
	    	if (word.charAt(i)=='a' || word.charAt(i)=='e' ||
	    	      word.charAt(i)=='i' || word.charAt(i)=='o' ||
	              word.charAt(i)=='u')
	    	{
	    		return i;
	    	}
	    }
	    	return 0;
	}
	  
	/*
	 * Networking packet utilities
	 */
	
    /**
	 * Send entity sync packet to client.
	 *
	 * @param parEntity the par entity
	 */
	public static void sendEntitySyncPacketToClient(IModEntity parEntity) 
    {
    	Entity theEntity = (Entity)parEntity;
        if (!theEntity.world.isRemote)
        {
//        	// DEBUG
//        	System.out.println("sendEntitySyncPacket from server");
//            MainMod.network.sendToAll(new MessageSyncEntityToClient(theEntity.getEntityId(), parEntity.getSyncDataCompound()));           
        }
    }

    /**
     * Send entity sync packet to server.
     *
     * @param parEntity the par entity
     */
    public static void sendEntitySyncPacketToServer(IModEntity parEntity) 
    {
    	Entity theEntity = (Entity)parEntity;
        if (theEntity.world.isRemote)
        {
        	// DEBUG
        	System.out.println("sendEntitySyncPacket from client");
//            MainMod.network.sendToServer(new MessageSyncEntityToServer(theEntity.getEntityId(), parEntity.getSyncDataCompound()));           
        }
    }
    
//    /**
//     * Sets the block ID and metadata at a given location. Args: X, Y, Z, new block ID, new metadata, flags. Flag 1 will
//     * cause a block update. Flag 2 will send the change to clients (you almost always want parChunk). Flag 4 prevents the
//     * block from being re-rendered, if parChunk is a client world. Flags can be added together.
//     */
//    public static boolean setBlockFast(World parWorld, int parX, int parY, int parZ, IBlockState parBlock, int parMetaData, int parFlag)
//    {
//    	BlockPos blockPos = new BlockPos(parX, parY, parZ);
//    	
//        // Make sure position is within valid range
//        if (parX >= -30000000 && parZ >= -30000000 && parX < 30000000 && parZ < 30000000)
//        {
//            if (parY < 0)
//            {
//                return false;
//            }
//            else if (parY >= 256)
//            {
//                return false;
//            }
//            else
//            {
//                Chunk chunk = parWorld.getChunkFromChunkCoords(parX >> 4, parZ >> 4);
//                IBlockState existingBlock = null;
//                BlockSnapshot blockSnapshot = null;
//
//                if ((parFlag & 1) != 0)
//                {
//                    existingBlock = chunk.getBlockState(parX & 15, parY, parZ & 15);
//                }
//
//                if (parWorld.captureBlockSnapshots && !parWorld.isRemote)
//                {
//                    blockSnapshot = BlockSnapshot.getBlockSnapshot(parWorld, blockPos, parFlag);
//                    parWorld.capturedBlockSnapshots.add(blockSnapshot);
//                }
//
//                boolean setBlockSuceeded = setBlockInChunkFast(chunk, parX & 15, parY, parZ & 15, parBlock, parMetaData);
//
//                if (!setBlockSuceeded && blockSnapshot != null)
//                {
//                    parWorld.capturedBlockSnapshots.remove(blockSnapshot);
//                    blockSnapshot = null;
//                }
//
//                if (setBlockSuceeded && blockSnapshot == null) // Don't notify clients or update physics while capturing blockstates
//                {
//                    // Modularize client and physic updates
//                    parWorld.markAndNotifyBlock(blockPos, chunk, existingBlock, parBlock, parFlag);
//                }
//
//                return setBlockSuceeded;
//            }
//        }
//        else
//        {
//            return false;
//        }
//    }
//
//    public static boolean setBlockInChunkFast(Chunk parChunk, int parX, int parY, int parZ, IBlockState parBlock, int parMetaData)
//    {
//    	BlockPos blockPos = new BlockPos(parX, parY, parZ);
//    	
//        int mapKey = parZ << 4 | parX;
//
////        if (parY >= parChunk.getPrecipitationHeight(blockPos) - 1)
////        {
////            parChunk.setPecipitationHeightMap[mapKey] = -999;
////        }
//
//        IBlockState existingBlock = parChunk.getBlockState(blockPos);
//
//        if (existingBlock == parBlock)
//        {
//            return false;
//        }
//        else
//        {
//            ExtendedBlockStorage extendedblockstorage = parChunk.getBlockStorageArray()[parY >> 4];
//
//            if (extendedblockstorage == null)
//            {
//                if (parBlock == Blocks.AIR)
//                {
//                    return false;
//                }
//
//                extendedblockstorage = parChunk.getBlockStorageArray()[parY >> 4] = new ExtendedBlockStorage(parY >> 4 << 4, !parChunk.getWorld().provider..hasNoSky);
//            }
//
//            int worldPosX = parChunk.x * 16 + parX;
//            int worldPosZ = parChunk.z * 16 + parZ;
//
////            if (!parChunk.getWorld().isRemote)
////            {
////                existingBlock.onBlockPreDestroy(parChunk.getWorld(), worldPosX, parY, worldPosZ);
////            }
//
//            extendedblockstorage.setExtBlockID(parX, parY & 15, parZ, parBlock);
//            extendedblockstorage.setExtBlockMetadata(parX, parY & 15, parZ, parMetaData); // This line duplicates the one below, so breakBlock fires with valid worldstate
//
//            if (!parChunk.getWorld().isRemote)
//            {
//                existingBlock.breakBlock(parChunk.world, worldPosX, parY, worldPosZ, existingBlock, existingMetaData);
//                // After breakBlock a phantom TE might have been created with incorrect meta. This attempts to kill that phantom TE so the normal one can be create properly later
//                BlockPos tilePos = new BlockPos(parX & 0x0F, parY, parZ & 0x0F);
//                TileEntity te = parChunk.getTileEntity(tilePos, null);
//                if (te != null && te.shouldRefresh(existingBlock, parChunk.getBlockState(parX & 0x0F, parY, parZ & 0x0F), existingMetaData, parChunk.getBlockState(parX & 0x0F, parY, parZ & 0x0F), parChunk.getWorld(), worldPosX, parY, worldPosZ))
//                {
//                    parChunk.removeTileEntity(new BlockPos(parX & 0x0F, parY, parZ & 0x0F));
//                }
//            }
//            else if (existingBlock.hasTileEntity(existingMetaData))
//            {
//                TileEntity te = parChunk.getTileEntityUnsafe(parX & 0x0F, parY, parZ & 0x0F);
//                if (te != null && te.shouldRefresh(existingBlock, parBlock, existingMetaData, parMetaData, parChunk.world, worldPosX, parY, worldPosZ))
//                {
//                    parChunk.world.removeTileEntity(worldPosX, parY, worldPosZ);
//                }
//            }
//
//            if (extendedblockstorage.getBlockByExtId(parX, parY & 15, parZ) != parBlock)
//            {
//                return false;
//            }
//            else
//            {
//                extendedblockstorage.setExtBlockMetadata(parX, parY & 15, parZ, parMetaData);
//
//                TileEntity tileentity;
//
//                if (!parChunk.world.isRemote)
//                {
//                    parBlock.onBlockAdded(parChunk.world, worldPosX, parY, worldPosZ);
//                }
//
//                if (parBlock.hasTileEntity(parMetaData))
//                {
//                    tileentity = parChunk.getBlockTileEntityInChunk(parX, parY, parZ);
//
//                    if (tileentity != null)
//                    {
//                        tileentity.updateContainingBlockInfo();
//                        tileentity.blockMetadata = parMetaData;
//                    }
//                }
//
//                parChunk.isModified = true;
//                return true;
//            }
//        }
//    }
    
//    // this will work across all dimensions
//    // thanks to diesieben07 for this tip on http://www.minecraftforge.net/forum/index.php?topic=27715.0
//    public static EntityPlayer getPlayerOnServerFromUUID(World parWorld, UUID parUUID) 
//    {
//        if (parUUID == null) 
//        {
//            return null;
//        }
//        PlayerList allPlayers = parWorld.getMinecraftServer().getPlayerList();
//        for (EntityPlayerMP player : allPlayers) 
//        {
//            if (player.getUniqueID().equals(parUUID)) 
//            {
//                return player;
//            }
//        }
//        return null;
//    }

    
    /**
 * A method used to see if an entity is a suitable target through a number of checks.
 *
 * @param theAttackerEntity the the attacker entity
 * @param parPossibleTargetEntity the par possible target entity
 * @param parShouldCheckSight the par should check sight
 * @return true, if is suitable target
 */
    public static boolean isSuitableTarget(EntityLivingBase theAttackerEntity, 
            EntityLivingBase parPossibleTargetEntity,
            boolean parShouldCheckSight)
    {
        if (parPossibleTargetEntity == null)
        {
//            // DEBUG
//            System.out.println("Target isn't suitable because it is null");
            return false;
        }
        else if (parPossibleTargetEntity == theAttackerEntity)
        {
            // DEBUG
            System.out.println("Target isn't suitable because it is itself");
            return false;
        }
        else if (!parPossibleTargetEntity.isEntityAlive())
        {
            // DEBUG
            System.out.println("Target isn't suitable because it is dead");
            return false;
        }
        else if (theAttackerEntity.isOnSameTeam(parPossibleTargetEntity))
        {
            // DEBUG
            System.out.println("Target isn't suitable because it is on same team");
            return false;
        }
//        else if (parPossibleTargetEntity instanceof EntityPlayer && ((EntityPlayer)parPossibleTargetEntity).capabilities.disableDamage)
//        {
//            // DEBUG
//            System.out.println("Target isn't suitable because player can't take damage");
//            return false;
//        }
        else if (theAttackerEntity instanceof EntityLiving && parShouldCheckSight)
        {
            // DEBUG
            System.out.println("The attacker can see target = "+((EntityLiving)theAttackerEntity).getEntitySenses().canSee(parPossibleTargetEntity));
            return ((EntityLiving)theAttackerEntity).getEntitySenses().canSee(parPossibleTargetEntity);
        }
        else
        {
            return true;
        }
    }

    
//    // This is mostly copied from the EntityRenderer#getMouseOver() method
//    public static MovingObjectPosition getMouseOverExtended(float parDist)
//    {
//        double dist = parDist;
//        Minecraft mc = FMLClientHandler.instance().getClient();
//        EntityLivingBase theRenderViewEntity = mc.renderViewEntity;
//        AxisAlignedBB theViewBoundingBox = AxisAlignedBB.getBoundingBox(
//                theRenderViewEntity.posX-0.5D,
//                theRenderViewEntity.posY-0.0D,
//                theRenderViewEntity.posZ-0.5D,
//                theRenderViewEntity.posX+0.5D,
//                theRenderViewEntity.posY+1.5D,
//                theRenderViewEntity.posZ+0.5D
//                );
//        MovingObjectPosition returnMOP = null;
//        if (mc.theWorld != null)
//        {
//            returnMOP = theRenderViewEntity.rayTrace(parDist, 0);
//            Vec3 pos = theRenderViewEntity.getPosition(0).addVector(0.0D, theRenderViewEntity.getEyeHeight(), 0.0D);
//            if (returnMOP != null)
//            {
//                dist = returnMOP.hitVec.distanceTo(pos);
//            }
//            
//            Vec3 lookvec = theRenderViewEntity.getLook(0);
//            Vec3 var8 = pos.addVector(lookvec.xCoord * dist, lookvec.yCoord * dist, lookvec.zCoord * dist);
//            Entity pointedEntity = null;
//            float var9 = 1.0F;
//            @SuppressWarnings("unchecked")
//            List<Entity> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(theRenderViewEntity, theViewBoundingBox.addCoord(lookvec.xCoord * dist, lookvec.yCoord * dist, lookvec.zCoord * dist).expand(var9, var9, var9));
//            
//            for (Entity entity : list)
//            {
//                if (entity.canBeCollidedWith())
//                {
//                    float bordersize = entity.getCollisionBorderSize();
//                    AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(entity.posX-entity.width/2, entity.posY, entity.posZ-entity.width/2, entity.posX+entity.width/2, entity.posY+entity.height, entity.posZ+entity.width/2);
//                    aabb.expand(bordersize, bordersize, bordersize);
//                    MovingObjectPosition mop0 = aabb.calculateIntercept(pos, var8);
//                    
//                    if (aabb.isVecInside(pos))
//                    {
//                        if (0.0D < dist || dist == 0.0D)
//                        {
//                            pointedEntity = entity;
//                            dist = 0.0D;
//                        }
//                    } else if (mop0 != null)
//                    {
//                        double d1 = pos.distanceTo(mop0.hitVec);
//                        
//                        if (d1 < d || d == 0.0D)
//                        {
//                            pointedEntity = entity;
//                            d = d1;
//                        }
//                    }
//                }
//            }
//            
//            if (pointedEntity != null && (d < calcdist || returnMOP == null))
//            {
//                returnMOP = new MovingObjectPosition(pointedEntity);
//            }
//        
//        }
//        return returnMOP;
//    }

    /**
 * Gets the yaw from vec.
 *
 * @param parVec the par vec
 * @return the yaw from vec
 */
public static float getYawFromVec(Vec3d parVec)
    {
        // The coordinate system for Minecraft is a bit backwards as explained 
        // at https://github.com/chraft/c-raft/wiki/Vectors,-Location,-Yaw-and-Pitch-in-C%23raft
        return (float) -Math.toDegrees(Math.atan2(parVec.x, parVec.z));
    }
    
    /**
     * Gets the pitch from vec.
     *
     * @param parVec the par vec
     * @return the pitch from vec
     */
    public static float getPitchFromVec(Vec3d parVec)
    {
        // The coordinate system for Minecraft is a bit backwards as explained 
        // at https://github.com/chraft/c-raft/wiki/Vectors,-Location,-Yaw-and-Pitch-in-C%23raft
        Vec3d theVec = parVec.normalize();
        return (float) Math.toDegrees(Math.asin(theVec.y));
    }
    
    /**
     * Copy bounding box.
     *
     * @param aabbIn the aabb in
     * @return the axis aligned BB
     */
    public static AxisAlignedBB copyBoundingBox(AxisAlignedBB aabbIn)
    {
    	return new AxisAlignedBB(aabbIn.minX, aabbIn.minY, aabbIn.minZ, aabbIn.maxX, aabbIn.maxY, aabbIn.maxZ);
    }
    
    /**
     * True if the entity has an unobstructed line of travel to the waypoint.
     *
     * @param parEntity the par entity
     * @param parX the par X
     * @param parY the par Y
     * @param parZ the par Z
     * @return true, if is course traversable
     */
    public static boolean isCourseTraversable(Entity parEntity, double parX, double parY, double parZ)
    {
        double theDistance = MathHelper.sqrt(parX * parX + parY * parY + parZ * parZ);

        double incrementX = (parX - parEntity.posX) / theDistance;
        double incrementY = (parY - parEntity.posY) / theDistance;
        double incrementZ = (parZ - parEntity.posZ) / theDistance;
        AxisAlignedBB entityBoundingBox = copyBoundingBox(parEntity.getEntityBoundingBox());

        for (int i = 1; i < theDistance; ++i)
        {
            entityBoundingBox.offset(incrementX, incrementY, incrementZ);

            if (!parEntity.world.getCollisionBoxes(parEntity, entityBoundingBox).isEmpty())
            {
                return false;
            }
        }

        return true;
    }
}
