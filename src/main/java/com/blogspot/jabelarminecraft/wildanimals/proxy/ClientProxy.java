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

package com.blogspot.jabelarminecraft.wildanimals.proxy;

import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityJaguar;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityLion;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityLynx;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityManEatingTiger;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityTiger;
import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityEagle;
import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityHawk;
import com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals.EntityElephant;
import com.blogspot.jabelarminecraft.wildanimals.entities.serpents.EntitySerpent;
import com.blogspot.jabelarminecraft.wildanimals.models.ModelBigCat;
import com.blogspot.jabelarminecraft.wildanimals.models.ModelBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.models.ModelElephant;
import com.blogspot.jabelarminecraft.wildanimals.models.ModelSerpent;
import com.blogspot.jabelarminecraft.wildanimals.renderers.RenderBigCat;
import com.blogspot.jabelarminecraft.wildanimals.renderers.RenderBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.renderers.RenderHerdAnimal;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy 
{

	@Override
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event)
	{
		// DEBUG
        System.out.println("on Client side");
        
		// do common stuff
		super.fmlLifeCycleEvent(event);

        // do client-specific stuff
        registerRenderers();
	}
	@Override
	public void fmlLifeCycleEvent(FMLInitializationEvent event)
	{
		// DEBUG
        System.out.println("on Client side");

        // do common stuff
		super.fmlLifeCycleEvent(event);

		// do client-specific stuff
	}
	
	@Override
	public void fmlLifeCycleEvent(FMLPostInitializationEvent event)
	{
		// DEBUG
        System.out.println("on Client side");

        // do common stuff
		super.fmlLifeCycleEvent(event);

		// do client-specific stuff
	}

	@SuppressWarnings("unchecked")
	public void registerRenderers() 
    {
        // Big cats
	    RenderingRegistry.registerEntityRenderingHandler(
	    		EntityTiger.class, 
	    		RenderBigCat.getRenderFactory(
	            		new ModelBigCat(), 
	            		new ModelBigCat(), 
	                    0.5F,
	                    new ResourceLocation("wildanimals:textures/entity/tiger/tiger.png"), 
	                    new ResourceLocation("wildanimals:textures/entity/tiger/tiger_tame.png"),
	                    new ResourceLocation("wildanimals:textures/entity/tiger/tiger_angry.png"),
	                    new ResourceLocation("wildanimals:textures/entity/tiger/tiger_collar.png")
	    				)
	    		);	    
	    RenderingRegistry.registerEntityRenderingHandler(
	    		EntityManEatingTiger.class, 
	    		RenderBigCat.getRenderFactory(
	            		new ModelBigCat(), 
	            		new ModelBigCat(), 
	                    0.5F,
	                    new ResourceLocation("wildanimals:textures/entity/tiger/tiger.png"), 
	                    new ResourceLocation("wildanimals:textures/entity/tiger/tiger_tame.png"),
	                    new ResourceLocation("wildanimals:textures/entity/tiger/tiger_angry.png"),
	                    new ResourceLocation("wildanimals:textures/entity/tiger/tiger_collar.png")
	    				)
	    		);	    
	    RenderingRegistry.registerEntityRenderingHandler(
	    		EntityLion.class, 
	    		RenderBigCat.getRenderFactory(
	            		new ModelBigCat(), 
	            		new ModelBigCat(), 
	                    0.5F,
	    	    		new ResourceLocation("wildanimals:textures/entity/lion/lion.png"), 
	    	    		new ResourceLocation("wildanimals:textures/entity/lion/lion_tame.png"),
	    	    		new ResourceLocation("wildanimals:textures/entity/lion/lion_angry.png"),
	    	    		new ResourceLocation("wildanimals:textures/entity/tiger/tiger_collar.png")
	    				)
	    		);	    
	    RenderingRegistry.registerEntityRenderingHandler(
	    		EntityLynx.class, 
	    		RenderBigCat.getRenderFactory(
	            		new ModelBigCat(), 
	            		new ModelBigCat(), 
	                    0.5F,
	    	    		new ResourceLocation("wildanimals:textures/entity/lion/lion.png"), 
	    	    		new ResourceLocation("wildanimals:textures/entity/lion/lion_tame.png"),
	    	    		new ResourceLocation("wildanimals:textures/entity/lion/lion_angry.png"),
	    	    		new ResourceLocation("wildanimals:textures/entity/tiger/tiger_collar.png")
	    				)
	    		);	    
	    RenderingRegistry.registerEntityRenderingHandler(
	    		EntityJaguar.class, 
	    		RenderBigCat.getRenderFactory(
	            		new ModelBigCat(), 
	            		new ModelBigCat(), 
	                    0.5F,
	                    new ResourceLocation("wildanimals:textures/entity/tiger/tiger.png"), 
	                    new ResourceLocation("wildanimals:textures/entity/tiger/tiger_tame.png"),
	                    new ResourceLocation("wildanimals:textures/entity/tiger/tiger_angry.png"),
	                    new ResourceLocation("wildanimals:textures/entity/tiger/tiger_collar.png")
	    				)
	    		);
	    
	    /*
	     * Birds of Prey
	     */
	    RenderingRegistry.registerEntityRenderingHandler(
	    		EntityEagle.class, 
	    		RenderBirdOfPrey.getRenderFactory(
	                    new ModelBirdOfPrey(), 
	                    new ModelBirdOfPrey(), 
	                    0.5F,
	                    new ResourceLocation("wildanimals:textures/entity/birdsofprey/eagle.png"),
	                    new ResourceLocation("wildanimals:textures/entity/birdsofprey/bird_of_prey_legband.png")
	    				)
	    		);
	    RenderingRegistry.registerEntityRenderingHandler(
	    		EntityHawk.class, 
	    		RenderBirdOfPrey.getRenderFactory(
	                    new ModelBirdOfPrey(), 
	                    new ModelBirdOfPrey(), 
	                    0.5F,
	                    new ResourceLocation("wildanimals:textures/entity/birdsofprey/hawk.png"),
	                    new ResourceLocation("wildanimals:textures/entity/birdsofprey/bird_of_prey_legband.png")
	    				)
	    		);
	    RenderingRegistry.registerEntityRenderingHandler(
	    		EntityEagle.class, 
	    		RenderBirdOfPrey.getRenderFactory(
	                    new ModelBirdOfPrey(), 
	                    new ModelBirdOfPrey(), 
	                    0.5F,
	                    new ResourceLocation("wildanimals:textures/entity/birdsofprey/owl.png"),
	                    new ResourceLocation("wildanimals:textures/entity/birdsofprey/bird_of_prey_legband.png")
	    				)
	    		);
	    
	    /*
	     * Herd Animals
	     */
	    
	    RenderingRegistry.registerEntityRenderingHandler(
	    		EntityElephant.class, 
	    		RenderHerdAnimal.getRenderFactory(
	                    new ModelElephant(), 
	                    0.5F,
	                    new ResourceLocation("wildanimals:textures/entity/herdanimals/elephant.png")
	    				)
	    		);   
	    
	    /*
	     * Serpents
	     */
	    
	    RenderingRegistry.registerEntityRenderingHandler(
	    		EntitySerpent.class, 
	    		RenderHerdAnimal.getRenderFactory(
	                    new ModelSerpent(), 
	                    0.5F,
	                    new ResourceLocation("wildanimals:textures/entity/serpents/python.png")
	    				)
	    		);   
	    
	    
    }
	
    @Override
    public void sendMessageToPlayer(TextComponentString msg) 
    {
        Minecraft.getMinecraft().player.sendMessage(msg);
    }
    
	/*	 
	 * Thanks to CoolAlias for this tip!
	 */
	/**
	 * Returns a side-appropriate EntityPlayer for use during message handling
	 */
    @Override
    public EntityPlayer getPlayerEntityFromContext(MessageContext ctx) 
    {
        // Note that if you simply return 'Minecraft.getMinecraft().thePlayer',
        // your packets will not work because you will be getting a client
        // player even when you are on the server! Sounds absurd, but it's true.

        // Solution is to double-check side before returning the player:
        return (ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerEntityFromContext(ctx));
    }
    

//    
//    @Override
//	public void fmlLifeCycleEvent(FMLServerStartingEvent event) 
//    {
//		// DEBUG
//        System.out.println("on Client side");
//        
//	    event.registerServerCommand(new CommandConjure());
//	}
}