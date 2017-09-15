package com.blogspot.jabelarminecraft.wildanimals.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class PetListProvider implements ICapabilitySerializable<NBTTagCompound>
{
	@CapabilityInject(IPetList.class)
	public static final Capability<IPetList> PET_LIST = null;
	private IPetList instance = PET_LIST.getDefaultInstance();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) 
	{
		return capability == PET_LIST;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) 
	{
		return capability == PET_LIST ? PET_LIST.<T> cast(this.instance) : null;
	}

	@Override
	public NBTTagCompound serializeNBT() 
	{
		return (NBTTagCompound) PET_LIST.getStorage().writeNBT(PET_LIST, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) 
	{
		 PET_LIST.getStorage().readNBT(PET_LIST, this.instance, null, nbt);		
	}

}
