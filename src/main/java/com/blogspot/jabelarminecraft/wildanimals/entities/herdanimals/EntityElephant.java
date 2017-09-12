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

package com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

// TODO: Auto-generated Javadoc
public class EntityElephant extends EntityHerdAnimal
{	
	
	/**
	 * Instantiates a new entity elephant.
	 *
	 * @param par1World the par 1 world
	 */
	public EntityElephant(World par1World) 
	{
		super(par1World);
	    
//		// DEBUG
//        System.out.println("EntityElephant constructor()");

    	setScaleFactor(2.0F); // elephants are big
        setSize(width*getScaleFactor(), height*getScaleFactor());
//
//        soundAmbientElephant = new SoundEvent(new ResourceLocation("wildanimals:mob.elephant.say"));
//        soundHurtElephant = new SoundEvent(new ResourceLocation("wildanimals:mob.elephant.hurt"));
//        soundDeathElephant = new SoundEvent(new ResourceLocation("wildanimals:mob.elephant.hurt"));

	}

    /* (non-Javadoc)
     * @see com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals.EntityHerdAnimal#applyEntityAttributes()
     */
    @Override
	protected void applyEntityAttributes()
    {
    	// DEBUG
    	System.out.println("EntityElephant applyEnityAttributes()");
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D); // elephants are tough
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D); // can't knockback an elephant
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
    }

    /* (non-Javadoc)
     * @see com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals.EntityHerdAnimal#createChild(net.minecraft.entity.EntityAgeable)
     */
    @Override
    public EntityElephant createChild(EntityAgeable par1EntityAgeable)
    {
        return new EntityElephant(world);
    }

 }
