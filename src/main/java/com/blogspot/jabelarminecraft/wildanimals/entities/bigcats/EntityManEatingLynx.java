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

package com.blogspot.jabelarminecraft.wildanimals.entities.bigcats;

import net.minecraft.world.World;

public class EntityManEatingLynx extends EntityManEatingBigCat
{
    
    /**
     * Instantiates a new entity man eating lynx.
     *
     * @param par1World the par 1 world
     */
    public EntityManEatingLynx(World par1World)
    {
        super(par1World);
        
        // DEBUG
        System.out.println("EntityManEatingLynx constructor()");
    }
}