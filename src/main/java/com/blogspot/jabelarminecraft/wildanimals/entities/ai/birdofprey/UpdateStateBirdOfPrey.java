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

package com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey;

import java.util.Iterator;
import java.util.List;

import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.utilities.Utilities;

import net.minecraft.block.BlockLeaves;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

// TODO: Auto-generated Javadoc
/**
 * @author jabelar
 *
 */
public class UpdateStateBirdOfPrey
{
    public EntityBirdOfPrey theBird;
    public World theWorld;
    
    // defines area on ground that bird looks for prey in
    // region is double this size as this gives dimensions in each direction
    public double attackRegionSize = 5.0D;
    
    // the "one in" chance per tick that it will decide to perch if over a perchable block
    public final int PERCH_CHANCE_BASE = 1;
    // the percent chance per tick that when perched it will decide to take off
    public final int TAKE_OFF_CHANCE_BASE = 2400;
    
    /**
     * Instantiates a new update state bird of prey.
     *
     * @param parBirdOfPrey the par bird of prey
     */
    public UpdateStateBirdOfPrey(EntityBirdOfPrey parBirdOfPrey)
    {
        theBird = parBirdOfPrey;
        theWorld = theBird.world;
    }
    
    /**
     * Update AI state.
     */
    public void updateAIState()
    {
//    	// DEBUG
//    	if (theBird.ticksExisted%20 == 0)
//    	{
//    		System.out.println("update ai state where state = "+theBird.getState()+" for entity id = "+theBird.getEntityId());
//    	}
    	
    	switch (theBird.getState())
        {
            case AIStates.STATE_PERCHED:
            {
                updateStatePerched();
                break;            
            }
            case AIStates.STATE_TAKING_OFF:
            {
                updateStateTakingOff();
                break;
            }
            case AIStates.STATE_SOARING:
            {
                updateStateSoaring();
                break;
            }
            case AIStates.STATE_DIVING:
            {
                updateStateDiving();
                break;
            }
            case AIStates.STATE_LANDING:
            {
                updateStateLanding();
                break;
            }
            case AIStates.STATE_TRAVELLING:
            {
                updateStateTravelling();
                break;
            }
            case AIStates.STATE_ATTACKING:
            {
                updateStateAttacking();
                break;
            }
            case AIStates.STATE_SOARING_TAMED:
            {
                updateStateSoaringTamed();
                break;
            }
            case AIStates.STATE_PERCHED_TAMED:
            {
                updateStatePerchedTamed();
                break;            
            }
            case AIStates.STATE_SEEKING:
            {
                updateStateSeeking();
                break;
            }
            default:
            {
                // DEBUG
                System.out.println("EntityBirdOfPrey OnLivingUpdate() **ERROR** unhandled state");
                break;
            }
        }
    }
    
    /**
     * 
     */
    private void updateStateSeeking()
    {
        if (theBird.isTamed())
        {
            processOwnerAttack();
     
            if (theBird.getAttackTarget() != null)
            {
                theBird.setState(AIStates.STATE_ATTACKING);
            }
            else
            {
                theBird.setState(AIStates.STATE_TRAVELLING);
            }
        }
        else
        {
            // DEBUG
            System.out.println("Seeking but isn't tamed");
        }
    }

    /**
     * 
     */
    private void updateStatePerchedTamed()
    {
        // check if block perched upon has disappeared
//            // DEBUG
//            System.out.println("Block underneath = "+worldObj.getBlock(MathHelper.floor_double(posX), (int)posY - 1, MathHelper.floor_double(posZ)).getUnlocalizedName());
        if (!hasLanded())
        {
            theBird.setState(AIStates.STATE_TAKING_OFF);
        }
        else // still solidly perched
        {
            // can occasionally adjust or flap, look around, or play sound to create variety
            if (theBird.getRNG().nextInt(getTakeOffChance()) == 0)
            {
                theBird.setState(AIStates.STATE_TAKING_OFF);
                // rotationYawHead = rand.nextInt(360);
            }
        }
    }

    /**
     * 
     */
    private void updateStateSoaringTamed()
    {
        // climb again if drifting too low
        if (theBird.posY < theBird.getSoarHeight()*0.9D)
        {
            // point towards owner
            theBird.rotationYaw = Utilities.getYawFromVec(new Vec3d(
                    theBird.getOwner().posX - theBird.posX,
                    theBird.getOwner().posY - theBird.posY,
                    theBird.getOwner().posZ - theBird.posZ));
            theBird.setState(AIStates.STATE_TRAVELLING);
        }
        
        considerAttacking();
        
        if (theBird.getAttackTarget() == null)
        {
            considerPerching();
        }
        else
        {
            theBird.setState(AIStates.STATE_SEEKING);
        }
    }

    /**
     * 
     */
    private void updateStateAttacking()
    {
        EntityLivingBase target = theBird.getAttackTarget();
        // check if target has been killed or despawned
        if (target == null)
        {
            theBird.setState(AIStates.STATE_TAKING_OFF);
        }
        else if (target.isDead)
        {
            theBird.setAttackTarget(null);
            theBird.setState(AIStates.STATE_TAKING_OFF);
        }
        else if (!Utilities.isCourseTraversable(theBird, target.posX, target.posY, target.posZ))
        {
            // theBird.setAttackTarget(null);
            theBird.setState(AIStates.STATE_TAKING_OFF);
        }
        // check for hitting target
        else if (theBird.getDistanceToEntity(theBird.getAttackTarget())<2.0F)
        {
            theBird.getAttackTarget().attackEntityFrom(
                    DamageSource.causeMobDamage(theBird), 
                    (float) theBird.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
            theBird.setState(AIStates.STATE_TAKING_OFF);
        }
    }

    /**
     * 
     */
    private void updateStateTravelling()
    {
        if (theBird.posY >= theBird.getSoarHeight())
        {
//            // DEBUG
//            System.out.println("State changed to soaring");
            if (theBird.isTamed())
            {
                theBird.setState(AIStates.STATE_SOARING_TAMED);
            }
            else
            {
                theBird.setState(AIStates.STATE_SOARING);
            }
        }
    }

    /**
     * 
     */
    private void updateStateLanding()
    {
        if (hasLanded())
        {
            // DEBUG
            System.out.println("Made it to perch"+" for entity id = "+theBird.getEntityId());
            
            if (theBird.isTamed())
            {
                theBird.setState(AIStates.STATE_PERCHED_TAMED);
            }
            else
            {
                theBird.setState(AIStates.STATE_PERCHED);
            }
        }
    }

    private void updateStateDiving()
    {
//            // DEBUG
//            System.out.println("Block underneath = "+worldObj.getBlock(MathHelper.floor_double(posX), (int)posY - 1, MathHelper.floor_double(posZ)).getUnlocalizedName());
        // handle case where perch target block might get destroyed before eagle gets to it
        // or might get obstructed.
//        if (theWorldObj.getTopBlock((int)theBird.posX, (int)theBird.posZ) instanceof BlockLeaves
//                && Utilities.isCourseTraversable(
//                            theBird,
//                            theBird.posX, 
//                            theWorldObj.getHeightValue(
//                                    (int)theBird.posX, 
//                                    (int)theBird.posZ), 
//                            theBird.posZ))
//        {
        // check if should start landing
    	BlockPos anchorPos = theBird.getAnchor();
    	
        if (theBird.getDistanceSq(
                anchorPos.getX(), 
                anchorPos.getY(),
                anchorPos.getZ())<25)
        {
            theBird.setState(AIStates.STATE_LANDING);
        }
            // see if made it to perch
        else if (hasLanded())
            {
                // DEBUG
                System.out.println("Made it to perch");
                if (theBird.isTamed())
                {
                    theBird.setState(AIStates.STATE_PERCHED_TAMED);
                    stopMoving();
                }
                else
                {
                    theBird.setState(AIStates.STATE_PERCHED);
                    stopMoving();
                }
            }
//        }
//        else
//        {
//            theBird.setState(AIStates.STATE_TAKING_OFF);
//        }
    }
    
    private boolean hasLanded()
    {
        AxisAlignedBB entityBoundingBox = (Utilities.copyBoundingBox(theBird.getEntityBoundingBox())).offset(0.0D, -0.5D, 0.0D);

        if (!theWorld.getCollisionBoxes(theBird, entityBoundingBox).isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Stop moving.
     */
    protected void stopMoving()
    {
        theBird.motionX = 0;
        theBird.motionY = 0;
        theBird.motionZ = 0;
    }

    private void updateStateSoaring()
    {
//    	// DEBUG
//    	if (theBird.ticksExisted%20==0)
//    	{
//    		System.out.println("update state soaring for entity id = "+theBird.getEntityId());
//    	}
    	
        if (theBird.isTamed())
        {
            theBird.setState(AIStates.STATE_SOARING_TAMED);
        }
        else
        {
            // climb again if drifting too low
            if (theBird.posY < theBird.getSoarHeight()*0.9D)
            {
                theBird.setState(AIStates.STATE_TRAVELLING);
            }
            
            considerAttacking();
            
            if (theBird.getAttackTarget() == null)
            {
                considerPerching();
            }
            else
            {
                theBird.setState(AIStates.STATE_ATTACKING);
            }
        }
    }

    /**
     * 
     */
    private void updateStateTakingOff()
    {
//    	// DEBUG
//    	if (theBird.ticksExisted%20 == 0)
//    	{
//    		System.out.println("update state taking off, the bird pos = "+theBird.posY+" and soaring height = "+theBird.getSoarHeight()+" for entity id = "+theBird.getEntityId());
//    	}
    	
    	if (theBird.posY >= theBird.getSoarHeight())
        {
            
            if (theBird.isTamed())
            {
                theBird.setState(AIStates.STATE_SOARING_TAMED);
            }
            else
            {
                theBird.setState(AIStates.STATE_SOARING);
            }
        }
    }

    /**
     * 
     */
    private void updateStatePerched()
    {
        // check if block perched upon has disappeared
//            // DEBUG
//            System.out.println("Block underneath = "+worldObj.getBlock(MathHelper.floor_double(posX), (int)posY - 1, MathHelper.floor_double(posZ)).getUnlocalizedName());

        // DEBUG
        System.out.println("Tamed = "+theBird.isTamed()+" for entity id = "+theBird.getEntityId());
        
        if (theBird.isTamed())
        {
            theBird.setState(AIStates.STATE_PERCHED_TAMED);
        }
        else if (!hasLanded())
        {
            theBird.setState(AIStates.STATE_TAKING_OFF);
        }
        else // still solidly perched
        {
            // can occasionally adjust or flap, look around, or play sound to create variety
            if (theBird.getRNG().nextInt(getTakeOffChance()) == 0)
            {
                theBird.setState(AIStates.STATE_TAKING_OFF);
                // rotationYawHead = rand.nextInt(360);
            }

            // entity can get scared if player gets too close
            EntityPlayer closestPlayer = theWorld.getClosestPlayerToEntity(theBird, 4.0D);
            if (closestPlayer != null)
            {
                ItemStack theHeldItemStack = closestPlayer.inventory.getCurrentItem();
                if (theHeldItemStack != null)
                {
                    // if not holding taming food, bird will get spooked
                    if (!theBird.isTamingFood(theHeldItemStack))
                    {
                        theBird.setState(AIStates.STATE_TAKING_OFF);
                    }
                }
            }
        }
    }

    /**
     * Consider perching.
     */
    public void considerPerching()
    {
        if (theBird.isTamed())
        {
            return;
        }
        else
        {
            // always try to perch starting at dusk
            if (theBird.getRNG().nextInt(getPerchChance()) == 0)
            {
                if (theWorld.getBlockState(theWorld.getTopSolidOrLiquidBlock(theBird.getPosition())) instanceof BlockLeaves)
                {
                    if (Utilities.isCourseTraversable(
                            theBird,
                            theBird.posX, 
                            theWorld.getHeight(
                                    (int)theBird.posX, 
                                    (int)theBird.posZ), 
                            theBird.posZ))
                    {
                        theBird.setState(AIStates.STATE_DIVING);
                        theBird.setAnchor(new BlockPos(
                                theBird.posX, 
                                theWorld.getHeight(
                                        (int)theBird.posX, 
                                        (int)theBird.posZ), 
                                theBird.posZ));
                    }
                }
            }
        }
    }
    
    /**
     * Gets the perch chance.
     *
     * @return the perch chance
     */
    public int getPerchChance()
    {
        if (theWorld.isRaining())
        {
            return 1;
        }
        
        if (theBird.isNocturnal())
        {
            if (theWorld.isDaytime())
            {
                return PERCH_CHANCE_BASE;
            }
            else
            {
                return PERCH_CHANCE_BASE * 10000;
            }
        }
        else
        {
            if (theWorld.isDaytime())
            {
                return PERCH_CHANCE_BASE * 10000;
            }
            else
            {
                return PERCH_CHANCE_BASE;
            }
        }
    }
    
    /**
     * Gets the take off chance.
     *
     * @return the take off chance
     */
    public int getTakeOffChance()
    {
        if (theWorld.isRaining())
        {
            return TAKE_OFF_CHANCE_BASE * 1000;
        }
        
        if (theBird.isNocturnal())
        {
            if (!theWorld.isDaytime())
            {
                return TAKE_OFF_CHANCE_BASE;
            }
            else
            {
                return TAKE_OFF_CHANCE_BASE * 100;
            }
        }
        else
        {
            if (!theWorld.isDaytime())
            {
                return TAKE_OFF_CHANCE_BASE * 100;
            }
            else
            {
                return TAKE_OFF_CHANCE_BASE;
            }
        }
    }
    
    /**
     * Consider attacking.
     */
    public void considerAttacking()
    {
        // DEBUG
        if (theBird.getAttackTarget() != null) System.out.println("Attack target = "+theBird.getAttackTarget());
        
        // handle case where previous target becomes unsuitable
        if (Utilities.isSuitableTarget(theBird, theBird.getAttackTarget(), false))
        {
            // DEBUG
            System.out.println(theBird.getAttackTarget()+" is no longer a suitable target");
            theBird.setAttackTarget(null);
        }
        
        if (theBird.isTamed())
        {
            processOwnerAttack();
        }
        else
        {
            processNaturalAttack(); // go for its natural prey
        }
        
        // check for revenge targets (the getAITarget() method really gives a revenge target)
        if (theBird.getAttackTarget() == null && theBird.getRevengeTarget() != null)
        {
            // DEBUG
            System.out.println("There is a revenge target");
            theBird.setAttackTarget(theBird.getRevengeTarget());
        }
    }

    /**
     * Process owner attack.
     */
    // detect if owner has attacked something, if so set attack target to owner's target
    public void processOwnerAttack()
    {
        EntityLivingBase theOwner = theBird.getOwner();

        if (theOwner == null)
        {
            return;
        }
        else
        {
            EntityLivingBase possibleTarget = theOwner.getLastAttackedEntity(); // note the get last attacker actually returns last attacked
            if (Utilities.isSuitableTarget(theOwner, possibleTarget, true) && 
                    Utilities.isCourseTraversable(theBird, possibleTarget.posX, possibleTarget.posY, possibleTarget.posZ))
            {
//                // attack region on ground
//                AxisAlignedBB attackRegion = AxisAlignedBB.getBoundingBox(
//                        theBird.posX - attackRegionSize, 
//                        theWorldObj.getHeightValue((int)theBird.posX, (int)theBird.posZ) - attackRegionSize, 
//                        theBird.posZ - attackRegionSize, 
//                        theBird.posX + attackRegionSize, 
//                        theWorldObj.getHeightValue((int)theBird.posX, (int)theBird.posZ) + attackRegionSize, 
//                        theBird.posZ + attackRegionSize);
//                if (attackRegion.isVecInside(Vec3d.createVectorHelper(
//                        possibleTarget.posX,
//                        possibleTarget.posY,
//                        possibleTarget.posZ)))
//                {
                    // DEBUG
                    System.out.println("Setting eagle target to owners target");
                    theBird.setAttackTarget(possibleTarget); 
//                }
            }
        }
    }

    /**
     * Process natural attack.
     */
    // detect if there is an attack target in region on ground directly below eagle
    public void processNaturalAttack()
    {
        // find target on ground
        AxisAlignedBB attackRegion = new AxisAlignedBB(
                theBird.posX - attackRegionSize, 
                theWorld.getHeight((int)theBird.posX, (int)theBird.posZ) - attackRegionSize, 
                theBird.posZ - attackRegionSize, 
                theBird.posX + attackRegionSize, 
                theWorld.getHeight((int)theBird.posX, (int)theBird.posZ) + attackRegionSize, 
                theBird.posZ + attackRegionSize);

        for (int i=0; i<theBird.getPreyArray().length; i++)
        {
            @SuppressWarnings({ "unchecked", "rawtypes" })
			List possibleTargetEntities = theWorld.getEntitiesWithinAABB(theBird.getPreyArray()[i], attackRegion);
            @SuppressWarnings("unchecked")
			Iterator<Object> targetIterator = possibleTargetEntities.iterator();
            while (targetIterator.hasNext())
            {
                EntityLivingBase possibleTarget = (EntityLivingBase)(targetIterator.next());
                if (Utilities.isCourseTraversable(theBird, possibleTarget.posX, possibleTarget.posY, possibleTarget.posZ))
                {
                    theBird.setAttackTarget(possibleTarget);
                }
            }
        }
    }
}
