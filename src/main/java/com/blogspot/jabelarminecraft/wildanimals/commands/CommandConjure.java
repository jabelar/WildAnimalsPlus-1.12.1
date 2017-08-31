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

package com.blogspot.jabelarminecraft.wildanimals.commands;

import java.util.ArrayList;
import java.util.List;

import com.blogspot.jabelarminecraft.wildanimals.WildAnimals;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class CommandConjure extends CommandBase
{
	private final List<String> aliases;
	
	protected ResourceLocation fullEntityName;
	protected Entity conjuredEntity;
	
	public CommandConjure()
	{
		    aliases = new ArrayList<String>();
		    aliases.add("conjure");
		    aliases.add("conj");
	}

	@Override
	public String getName() 
	{
		return "conjure";
	}

	@Override
	public String getUsage(ICommandSender var1) 
	{
		return "conjure <text>";
	}

	@Override
	public List getAliases() 
	{
		return this.aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] argString) 
	{
		World world = sender.getEntityWorld();
		
		if (world.isRemote)
		{
			System.out.println("Not processing on Client side");
		}
		else
		{
			System.out.println("Processing on Server side");

			if(argString.length == 0)
		    {
		    	sender.sendMessage(new TextComponentString("Invalid argument"));
		    	return;
		    }
			
		    sender.sendMessage(new TextComponentString("Conjuring: [" + argString[0] + "]"));
		    
		    fullEntityName = new ResourceLocation(WildAnimals.MODID+"."+argString[0]);
		    if (EntityList.getClass(fullEntityName) != null)
		    {
	            conjuredEntity = EntityList.createEntityByIDFromName(fullEntityName, world);
	    		conjuredEntity.setPosition(sender.getCommandSenderEntity().posX, sender.getCommandSenderEntity().posY, 
	    				sender.getCommandSenderEntity().posZ);
	    		world.spawnEntity(conjuredEntity);
		    }
		    else
		    {
		    	sender.sendMessage(new TextComponentString("Entity not found"));
		    }  
		}
	}
}
