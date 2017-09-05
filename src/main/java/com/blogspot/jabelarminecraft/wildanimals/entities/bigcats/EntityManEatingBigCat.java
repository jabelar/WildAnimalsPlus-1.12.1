/**
    Copyright (C) 2014 by jabelar

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

package com.blogspot.jabelarminecraft.wildanimals.entities.bigcats;

import com.blogspot.jabelarminecraft.wildanimals.entities.ai.bigcat.EntityAISeePlayerBigCat;
import com.google.common.base.Predicate;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityManEatingBigCat extends EntityBigCat
{
	protected final EntityAIBase aiSeePlayer = new EntityAISeePlayerBigCat(this, 32.0D);
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected final EntityAIBase aiTargetNonTamedAnimal = new EntityAITargetNonTamed(this, EntityAnimal.class, false, (Predicate)null);
	
    public EntityManEatingBigCat(World par1World)
    {
        super(par1World);
        
        // DEBUG
        System.out.println("EntityManEatingJaguar constructor()");
     
        setSize(0.6F, 0.8F);
                
        // rebuild AI task list specific to this sub-class
        clearAITasks();
        tasks.addTask(1, aiSwimming);
        tasks.addTask(2, aiLeapAtTarget);
        tasks.addTask(3, aiAttackOnCollide);
        tasks.addTask(4, aiSeePlayer);
        tasks.addTask(5, aiWander);
        tasks.addTask(6, aiWatchClosest);
        tasks.addTask(7, aiLookIdle);
        targetTasks.addTask(1, aiHurtByTarget);
        targetTasks.addTask(2, aiTargetNonTamedAnimal);
        
        setTamed(false);

    }
 
    @Override
	public void setTamed(boolean par1)
    {
    	// man-eating jaguar can't be tamed
    }

 
    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
    @Override
	public boolean isBreedingItem(ItemStack par1ItemStack)
    {
        return false; // can't breed man-eating jaguar
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    @Override
	public int getMaxSpawnedInChunk()
    {
        return 1;
    }
}