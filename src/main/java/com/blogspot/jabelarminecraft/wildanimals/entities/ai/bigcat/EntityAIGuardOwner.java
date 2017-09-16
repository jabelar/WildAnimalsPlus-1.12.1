package com.blogspot.jabelarminecraft.wildanimals.entities.ai.bigcat;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAIGuardOwner extends EntityAITarget
{
    EntityTameable tameable;
    EntityLivingBase attacker;

    public EntityAIGuardOwner(EntityTameable theDefendingTameableIn)
    {
        super(theDefendingTameableIn, true);
        tameable = theDefendingTameableIn;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
	public boolean shouldExecute()
    {
        if (!tameable.isTamed())
        {
            return false;
        }
        else
        {
            EntityLivingBase owner = tameable.getOwner();

            if (owner == null)
            {
                return false;
            }
            else
            {
            	List<Entity> entitiesNearby = owner.world.getEntitiesInAABBexcluding(owner, new AxisAlignedBB(owner.posX-10, owner.posY-10, owner.posZ-10, owner.posX+10, owner.posY+10, owner.posZ+10), null);
                Iterator<Entity> iterator = entitiesNearby.iterator();
                while (iterator.hasNext())
                {
                	Entity theEntity = iterator.next();
                	if (theEntity instanceof EntityLiving)
                	{
                		EntityLiving theEntityLiving = (EntityLiving) theEntity;
                		if (theEntityLiving.getAttackTarget() == owner)
                		{
                			attacker = theEntityLiving;
                            return isSuitableTarget(attacker, false) && tameable.shouldAttackEntity(attacker, owner);
                		}
                	}
                }

                return false;
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
	public void startExecuting()
    {
        taskOwner.setAttackTarget(attacker);
        EntityLivingBase entitylivingbase = tameable.getOwner();

        super.startExecuting();
    }
}