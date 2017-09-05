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

import java.util.UUID;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.world.World;

public class EntityLynx extends EntityBigCat
{

	public EntityLynx(World par1World) 
	{
		super(par1World);
	    
		// DEBUG
        System.out.println("EntityLynx constructor()");
        
	}

    @Override
	public EntityLynx createChild(EntityAgeable par1EntityAgeable)
    {
        EntityLynx entityChild = new EntityLynx(world);
        UUID uuid = getOwnerId(); 

        if (uuid != null)
        {
        	entityChild.setOwnerId(uuid);
            entityChild.setTamed(true);
        }
    	
    	return entityChild;
    }

}