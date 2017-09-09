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
package com.blogspot.jabelarminecraft.wildanimals.entities.ai.bigcat;

import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityBigCat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

// TODO: Auto-generated Javadoc
public class EntityAISitBigCat extends EntityAIBase
{
    private final EntityBigCat entityBigCat;
    
    /**
     *  If the EntityTameable is sitting.
     *
     * @param entityIn the entity in
     */

    public EntityAISitBigCat(EntityBigCat entityIn)
    {
        this.entityBigCat = entityIn;
        this.setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     *
     * @return true, if successful
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
     * Execute a one shot task or start executing a continuous task.
     */
    @Override
	public void startExecuting()
    {
        this.entityBigCat.getNavigator().clearPathEntity();
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