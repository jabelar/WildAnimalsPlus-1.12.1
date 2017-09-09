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

package com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey;

import net.minecraft.world.World;

// TODO: Auto-generated Javadoc
/**
 * @author jabelar
 * 
 */
public class EntityOwl extends EntityBirdOfPrey
{    
    
    /**
     * Instantiates a new entity owl.
     *
     * @param parWorld the par world
     */
    public EntityOwl(World parWorld)
    {
        super(parWorld);
    }
    
    /* (non-Javadoc)
     * @see com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey#entityInit()
     */
    @Override
	public void entityInit()
    {
    	super.entityInit();
    	float scaleFactor = 0.7F;
        setScaleFactor(scaleFactor);
        setSize(width*scaleFactor, height*scaleFactor);
    }
    
    /* (non-Javadoc)
     * @see com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey#isNocturnal()
     */
    @Override
    public boolean isNocturnal()
    {
        return true;
    }
}
