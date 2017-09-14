package com.blogspot.jabelarminecraft.wildanimals.capabilities;

import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class CapabilityPetListStorage implements IStorage<ICapabilityPetList>
{
	private String keyBase = "petUUID";
	
	@Override
	public NBTBase writeNBT(
			Capability<ICapabilityPetList> capability, 
			ICapabilityPetList instance, 
			EnumFacing side) 
	{
		List<UUID> petList = instance.getPetList();
		NBTTagCompound compound = new NBTTagCompound();
		for (int i = 0; i < petList.size(); i++)
		{
			compound.setUniqueId(keyBase+i, petList.get(i));
		}
		
		return compound;
	}

	@Override
	public void readNBT(
			Capability<ICapabilityPetList> capability, 
			ICapabilityPetList instance, 
			EnumFacing side,
			NBTBase nbt) 
	{
		NBTTagCompound compound = (NBTTagCompound)nbt;
		instance.getPetList().clear();
	
		for (int i=0; i < compound.getSize(); i++)
		{
			instance.getPetList().add(compound.getUniqueId(keyBase+i));
		}
	}

}
