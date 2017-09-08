package com.blogspot.jabelarminecraft.wildanimals.events;
import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

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

    public BirdTameEvent(EntityBirdOfPrey parBird, EntityPlayer parTamer)
    {
        super(parBird);
        theBird = parBird;
        tamer = parTamer;
    }

    public EntityBirdOfPrey getAnimal()
    {
        return theBird;
    }

    public EntityPlayer getTamer()
    {
        return tamer;
    }
}