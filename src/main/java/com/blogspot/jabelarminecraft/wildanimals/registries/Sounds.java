package com.blogspot.jabelarminecraft.wildanimals.registries;

import java.util.HashSet;
import java.util.Set;

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
    public static final SoundEvent soundAmbientGrowlBigCat = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.growl"));
    public static final SoundEvent soundAmbientWhineBigCat = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.whine"));
    public static final SoundEvent soundAmbientPantingBigCat = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.panting"));
    public static final SoundEvent soundAmbientBarkBigCat = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.bark"));
    public static final SoundEvent soundHurtBigCat = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.hurt"));
    public static final SoundEvent soundDeathBigCat = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.death"));
    public static final SoundEvent soundShakeBigCat = new SoundEvent(new ResourceLocation( "wildanimals:mob.bigCat.shake"));
    
    // elephant
    public static final SoundEvent soundAmbientElephant = new SoundEvent(new ResourceLocation("wildanimals:mob.elephant.say"));
    public static final SoundEvent soundHurtElephant = new SoundEvent(new ResourceLocation("wildanimals:mob.elephant.hurt"));
    public static final SoundEvent soundDeathElephant = new SoundEvent(new ResourceLocation("wildanimals:mob.elephant.hurt"));

    // birds of prey
    public static final SoundEvent soundHurtBird = new SoundEvent(new ResourceLocation("wildanimals:mob.birdsofprey.death"));
    public static final SoundEvent soundDeathBird = new SoundEvent(new ResourceLocation("wildanimals:mob.birdsofprey.death"));
    public static final SoundEvent soundCallBird = new SoundEvent(new ResourceLocation("wildanimals:mob.birdsofprey.cry"));
    public static final SoundEvent soundFlappingBird = new SoundEvent(new ResourceLocation("wildanimals:mob.birdsofprey.flapping"));

    // snake
    public static final SoundEvent soundHurtSnake = new SoundEvent(new ResourceLocation("wildanimals:mob.serpent.death"));
    public static final SoundEvent soundDeathSnake = new SoundEvent(new ResourceLocation("wildanimals:mob.serpent.death"));
    public static final SoundEvent soundCallSnake = new SoundEvent(new ResourceLocation("wildanimals:mob.serpent.hiss"));
       
	/**
	 * Initialize this mod's {@link Block}s with any post-registration data.
	 */
	private static void initialize() 
	{
	}

	@Mod.EventBusSubscriber(modid = MainMod.MODID)
	public static class RegistrationHandler 
	{
		public static final Set<SoundEvent> SET_SOUND_EVENTS = new HashSet<>();

		/**
		 * Register this mod's {@link SoundEvent}s.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void onEvent(final RegistryEvent.Register<SoundEvent> event) 
		{
			final SoundEvent[] arraySoundEvents = {
				     soundAmbientGrowlBigCat,
				     soundAmbientWhineBigCat,
				     soundAmbientPantingBigCat,
				     soundAmbientBarkBigCat,
				     soundHurtBigCat,
				     soundDeathBigCat,
				     soundShakeBigCat,
				     soundAmbientElephant,
				     soundHurtElephant,
				     soundDeathElephant,
				     soundHurtBird,
				     soundDeathBird,
				     soundCallBird,
				     soundFlappingBird,
				     soundHurtSnake,
				     soundDeathSnake,
				     soundCallSnake
			};

			final IForgeRegistry<SoundEvent> registry = event.getRegistry();

	        System.out.println("Registering sound events");

	        for (final SoundEvent SoundEvent : arraySoundEvents) {
				registry.register(SoundEvent);
				SET_SOUND_EVENTS.add(SoundEvent);
			}

			initialize();
		}
	}
}
