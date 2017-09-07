package com.blogspot.jabelarminecraft.wildanimals.entities.ai.bigcat;

import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityBigCat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISitBigCat extends EntityAIBase
{
    private final EntityBigCat entityBigCat;
    /** If the EntityTameable is sitting. */

    public EntityAISitBigCat(EntityBigCat entityIn)
    {
        this.entityBigCat = entityIn;
        this.setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
	public boolean shouldExecute()
    {
        if (!this.entityBigCat.isTamed())
        {
            return false;
        }
        else if (this.entityBigCat.isInWater())
        {
            return false;
        }
        else if (!this.entityBigCat.onGround)
        {
            return false;
        }
        else
        {
            EntityLivingBase entitylivingbase = this.entityBigCat.getOwner();

            if (entitylivingbase == null)
            {
                return true;
            }
            else
            {
                return this.entityBigCat.getDistanceSqToEntity(entitylivingbase) < 144.0D && entitylivingbase.getRevengeTarget() != null ? false : entityBigCat.isSitting();
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
	public void startExecuting()
    {
        this.entityBigCat.getNavigator().clearPathEntity();
        this.entityBigCat.setSitting(true);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
	public void resetTask()
    {
        this.entityBigCat.setSitting(false);
    }
}