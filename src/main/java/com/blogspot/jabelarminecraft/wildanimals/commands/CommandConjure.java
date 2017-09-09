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

package com.blogspot.jabelarminecraft.wildanimals.commands;

import java.util.ArrayList;
import java.util.List;

import com.blogspot.jabelarminecraft.wildanimals.MainMod;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

// TODO: Auto-generated Javadoc
public class CommandConjure extends CommandBase
{
	private final List<String> aliases;
	
	protected ResourceLocation fullEntityName;
	protected Entity conjuredEntity;
	
	/**
	 * Instantiates a new command conjure.
	 */
	public CommandConjure()
	{
		    aliases = new ArrayList<String>();
		    aliases.add("conjure");
		    aliases.add("conj");
	}

	/* (non-Javadoc)
	 * @see net.minecraft.command.ICommand#getName()
	 */
	@Override
	public String getName() 
	{
		return "conjure";
	}

	/* (non-Javadoc)
	 * @see net.minecraft.command.ICommand#getUsage(net.minecraft.command.ICommandSender)
	 */
	@Override
	public String getUsage(ICommandSender var1) 
	{
		return "conjure <text>";
	}

	/* (non-Javadoc)
	 * @see net.minecraft.command.CommandBase#getAliases()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List getAliases() 
	{
		return this.aliases;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.command.ICommand#execute(net.minecraft.server.MinecraftServer, net.minecraft.command.ICommandSender, java.lang.String[])
	 */
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
		    
		    fullEntityName = new ResourceLocation(MainMod.MODID, argString[0].toLowerCase());
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
