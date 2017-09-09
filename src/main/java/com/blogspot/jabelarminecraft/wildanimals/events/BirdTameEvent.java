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
package com.blogspot.jabelarminecraft.wildanimals.events;
import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

// TODO: Auto-generated Javadoc
/**
 * This event is fired when an {@link EntityAnimal} is tamed. <br>
 * It is fired via {@link ForgeEventFactory#onAnimalTame(EntityAnimal, EntityPlayer)}.
 * Forge fires this event for applicable vanilla animals, mods need to fire it themselves.
 * This event is {@link Cancelable}. If canceled, taming the animal will fail.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class BirdTameEvent extends LivingEvent
{
    private final EntityBirdOfPrey theBird;
    private final EntityPlayer tamer;

    /**
     * Instantiates a new bird tame event.
     *
     * @param parBird the par bird
     * @param parTamer the par tamer
     */
    public BirdTameEvent(EntityBirdOfPrey parBird, EntityPlayer parTamer)
    {
        super(parBird);
        theBird = parBird;
        tamer = parTamer;
    }

    /**
     * Gets the animal.
     *
     * @return the animal
     */
    public EntityBirdOfPrey getAnimal()
    {
        return theBird;
    }

    /**
     * Gets the tamer.
     *
     * @return the tamer
     */
    public EntityPlayer getTamer()
    {
        return tamer;
    }
}