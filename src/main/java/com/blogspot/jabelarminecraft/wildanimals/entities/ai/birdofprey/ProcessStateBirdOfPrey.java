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

import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.utilities.Utilities;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.Vec3d;

// TODO: Auto-generated Javadoc
/**
 * @author jabelar
 *
 */
public class ProcessStateBirdOfPrey
{
    EntityBirdOfPrey theBird;
    
    public float yawChangeRate = 1.5F;
    public float pitchChangeRate = 1.5F;
    public float targetPitch = 0.0F;
    
    /**
     * Instantiates a new process state bird of prey.
     *
     * @param parBirdOfPrey the par bird of prey
     */
    public ProcessStateBirdOfPrey(EntityBirdOfPrey parBirdOfPrey)
    {
        theBird = parBirdOfPrey;
    }

    /**
     * Update AI tick.
     */
    public void updateAITick()
    {
//        // DEBUG
//        System.out.println("State = "+getState());
 
        // regen
        if (theBird.isTamed())
        {
            if (theBird.ticksExisted%50 == 0 && theBird.isEntityAlive())
            {
                theBird.setHealth(theBird.getHealth()+1.0F);
            }
        }
        
        switch (theBird.getState())
        {
        case AIStates.STATE_PERCHED:
            processPerched();
            break;
        case AIStates.STATE_TAKING_OFF:
            processTakingOff();
            break;
        case AIStates.STATE_SOARING:
            processSoaring();
            break;
        case AIStates.STATE_DIVING:
            processDiving();
            break;
        case AIStates.STATE_LANDING:
            processLanding();
            break;
        case AIStates.STATE_TRAVELLING:
            processTravelling();
            break;
        case AIStates.STATE_ATTACKING:
            processAttacking();
            break;
        case AIStates.STATE_SOARING_TAMED:
            processSoaring();
            break;
        case AIStates.STATE_PERCHED_TAMED:
            processPerched();
            break;
        case AIStates.STATE_SEEKING:
            processSeeking();
            break;
        default:
            // DEBUG
            System.out.println("Unknown state");
            break;
                
        }
    }

    /**
     * 
     */
    private void processSeeking()
    {
        updatePitch(0.0F);
        if (theBird.getAttackTarget() != null)
        {
            updateYaw(Utilities.getYawFromVec(new Vec3d(
                    theBird.getAttackTarget().posX - theBird.posX, 
                    theBird.getAttackTarget().posY - theBird.posY, 
                    theBird.getAttackTarget().posZ - theBird.posZ)));
        }
        moveForward(1.0D);
    }

    /**
     * Process landing.
     */
    protected void processLanding() 
    {
        updatePitch(Math.max(0.0F, theBird.rotationPitch-pitchChangeRate*3));
        theBird.motionX = theBird.getAnchor().getX() - theBird.posX;
        theBird.motionZ = theBird.getAnchor().getZ() - theBird.posZ;
        theBird.motionY = Math.min(-0.2F, theBird.motionY+0.2);
    }

    /**
     * Process diving.
     */
    protected void processDiving() 
    {
        updatePitch(90.0F);
        theBird.motionX = theBird.getAnchor().getX() - theBird.posX;
        theBird.motionZ = theBird.getAnchor().getZ() - theBird.posZ;
        theBird.motionY = -1.0D;
    }

    /**
     * Process taking off.
     */
    protected void processTakingOff() 
    {
        updatePitch(0.0F);
        
        // climb to soaring height
        if (theBird.posY < theBird.getSoarHeight())
        {
        	// DEBUG
        	if (theBird.ticksExisted%20 == 0)
        	{
        		System.out.println("processTakingOff setting upwards motion because has not reached soar height for entity ID = "+theBird.getEntityId());
        	}

        	theBird.motionY = 0.1D;
        }

        moveForward(1.0D);

        // turn
        if (theBird.getSoarClockwise())
        {
            updateYaw(theBird.rotationYaw + yawChangeRate);
        }
        else
        {
            updateYaw(theBird.rotationYaw - yawChangeRate);
        }
    }

    /**
     * Process perched.
     */
    protected void processPerched() 
    {
        updatePitch(0.0F); // although the model is upright, want to make sure look vector and sense of motion preserved.
        processFrictionAndGravity();
//        stopMoving();
    }
    
    /**
     * Process soaring.
     */
    protected void processSoaring()
    {
        updatePitch(0.0F);
        
        // drift down slowly
        theBird.motionY = -0.01D;

        moveForward(1.0D);
        
        // turn
        if (theBird.isTamed())
        {
            // turn towards owner
            // got the dot product idea from https://github.com/chraft/c-raft/wiki/Vectors,-Location,-Yaw-and-Pitch-in-C%23raft
            Vec3d vecToOwner = new Vec3d(
                    theBird.getOwner().posX - theBird.posX, 
                    0, 
                    theBird.getOwner().posZ - theBird.posZ).normalize();
            if (theBird.getLookVec().dotProduct(vecToOwner) > 0.0D)
            {
                updateYaw(theBird.rotationYaw - yawChangeRate);
            }
            else
            {
                updateYaw(theBird.rotationYaw + yawChangeRate);
            }
        }
        else
        {
            if (theBird.getSoarClockwise())
            {
                updateYaw(theBird.rotationYaw + yawChangeRate);
            }
            else
            {
                updateYaw(theBird.rotationYaw - yawChangeRate);
            }
        }
    }
    
    /**
     * Process travelling.
     */
    protected void processTravelling()
    {
        updatePitch(0.0F);
        
        // climb to soaring height
        if (theBird.posY < theBird.getSoarHeight())
        {
            theBird.motionY = 0.1D;
        }

        moveForward(1.0D);
    }
    
    /**
     * Process attacking.
     */
    protected void processAttacking()
    {
        if (theBird.getAttackTarget() != null)
        {           
            theBird.motionY = -2.0D;
            double ticksToHitTarget = (theBird.posY - theBird.getAttackTarget().posY) / Math.abs(theBird.motionY);
            theBird.motionX = (theBird.getAttackTarget().posX - theBird.posX) / ticksToHitTarget;
            theBird.motionZ = (theBird.getAttackTarget().posZ - theBird.posZ) / ticksToHitTarget;
            updatePitch(Utilities.getPitchFromVec(new Vec3d(
                    theBird.motionX, 
                    theBird.motionY,
                    theBird.motionZ)));
        }        
    }
    
    
    /**
     * Move forward.
     *
     * @param parSpeedFactor the par speed factor
     */
    public void moveForward(double parSpeedFactor)
    {
        theBird.motionX = theBird.getLookVec().x * parSpeedFactor * theBird.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
        theBird.motionZ = theBird.getLookVec().z * parSpeedFactor * theBird.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
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
 
    /**
     * Process friction and gravity.
     */
    protected void processFrictionAndGravity()
    {
        theBird.motionX *= 0.93D;
        theBird.motionZ *= 0.93D;
        theBird.motionY -= 0.04D;
    }
    
    /**
     * Update yaw.
     *
     * @param parYaw the par yaw
     */
    protected void updateYaw(float parYaw)
    {
        float angleDiff = parYaw - theBird.rotationYaw;
        // handle possibility that shortest path is to rotate the other way
        if (angleDiff > 0.0001F)
        {
            if (angleDiff > 180.0F)
            {
                angleDiff -= 360.0F;
            }           
            
            theBird.rotationYaw += yawChangeRate;
        }
        else if (angleDiff < -0.0001F)
        {
            if (angleDiff < -180.0F)
            {
                angleDiff += 360.0F;
            }

            theBird.rotationYaw -= yawChangeRate;
        }
        
        // clamp to avoid oscillation around target
        if (Math.abs(angleDiff) < yawChangeRate)
        {
            theBird.rotationYaw = parYaw;
        }
    }
    
    /**
     * Update pitch.
     *
     * @param parPitch the par pitch
     */
    protected void updatePitch(float parPitch)
    {
        float angleDiff = parPitch - theBird.rotationPitch;
        // handle possibility that shortest path is to rotate the other way
        if (angleDiff > 0.0001F)
        {
            if (angleDiff > 180.0F)
            {
                angleDiff -= 360.0F;
            }           
            
            theBird.rotationPitch += pitchChangeRate;
        }
        else if (angleDiff < -0.0001F)
        {
            if (angleDiff < -180.0F)
            {
                angleDiff += 360.0F;
            }

            theBird.rotationPitch -= pitchChangeRate;
        }
        
        // clamp to avoid oscillation around target
        if (Math.abs(angleDiff) < pitchChangeRate)
        {
            theBird.rotationPitch = parPitch;
        }
    }

}
