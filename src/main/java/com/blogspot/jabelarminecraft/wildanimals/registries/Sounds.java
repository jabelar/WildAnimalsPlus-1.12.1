package com.blogspot.jabelarminecraft.wildanimals.registries;

import com.blogspot.jabelarminecraft.wildanimals.MainMod;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder(MainMod.MODID)
public class Sounds 
{
    // instantiate SoundEvents
	
	// big cats
	@ObjectHolder("mob.bigCat.growl")
    public static final SoundEvent soundAmbientGrowlBigCat = null;
	@ObjectHolder("mob.bigCat.whine")
    public static final SoundEvent soundAmbientWhineBigCat = null;
	@ObjectHolder("mob.bigCat.panting")
    public static final SoundEvent soundAmbientPantingBigCat = null;
	@ObjectHolder("mob.bigCat.park")
    public static final SoundEvent soundAmbientBarkBigCat = null;
	@ObjectHolder("mob.bigCat.hurt")
    public static final SoundEvent soundHurtBigCat = null;
	@ObjectHolder("mob.bigCat.death")
    public static final SoundEvent soundDeathBigCat = null;
	@ObjectHolder("mob.bigCat.shake")
    public static final SoundEvent soundShakeBigCat = null;
    
    // elephant
	@ObjectHolder("mob.elephant.say")
    public static final SoundEvent soundAmbientElephant = null;
	@ObjectHolder("mob.elephant.hurt")
    public static final SoundEvent soundHurtElephant = null;
//	@ObjectHolder("mob.elephant.hurt")
//    public static final SoundEvent soundDeathElephant = new SoundEvent(new ResourceLocation("wildanimals:mob.elephant.hurt"));

    // birds of prey
//	@ObjectHolder("mob.birdsofprey.hurt")
//    public static final SoundEvent soundHurtBird = new SoundEvent(new ResourceLocation("wildanimals:mob.birdsofprey.death"));
	@ObjectHolder("mob.birdsofprey.hurt")
    public static final SoundEvent soundDeathBird = null;
	@ObjectHolder("mob.birdsofprey.call")
    public static final SoundEvent soundCallBird = null;
	@ObjectHolder("mob.birdsofprey.flapping")
    public static final SoundEvent soundFlappingBird = null;

    // snake
//	@ObjectHolder("mob.serpent.hurt")
//    public static final SoundEvent soundHurtSnake = new SoundEvent(new ResourceLocation("wildanimals:mob.serpent.death"));
	@ObjectHolder("mob.serpent.death")
    public static final SoundEvent soundDeathSerpent = null;
	@ObjectHolder("mob.serpent.hiss")
    public static final SoundEvent soundCallSerpent = null;
       
	/**
	 * Initialize this mod's {@link Block}s with any post-registration data.
	 */
	private static void initialize() 
	{
	}

	@Mod.EventBusSubscriber(modid = MainMod.MODID)
	public static class RegistrationHandler 
	{
		/**
		 * Register this mod's {@link SoundEvent}s.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void onEvent(final RegistryEvent.Register<SoundEvent> event) 
		{
			final String[] arraySoundEvents = {
					// big cats
					"mob.bigCat.growl",
					"mob.bigCat.whine",
					"mob.bigCat.panting",
					"mob.bigCat.park",
					"mob.bigCat.hurt",
					"mob.bigCat.death",
					"mob.bigCat.shake",
				    
				    // elephant
					"mob.elephant.say",
					"mob.elephant.hurt",

				    // birds of prey
					"mob.birdsofprey.hurt",
					"mob.birdsofprey.call",
					"mob.birdsofprey.flapping",

				    // serpent
					"mob.serpent.death",
					"mob.serpent.hiss"
			};

			final IForgeRegistry<SoundEvent> registry = event.getRegistry();

	        System.out.println("Registering sound events");

	        for (final String soundName : arraySoundEvents) 
	        {
				registry.register(new SoundEvent(new ResourceLocation(MainMod.MODID, soundName)).setRegistryName(soundName));
			}

			initialize();
		}
	}
}
