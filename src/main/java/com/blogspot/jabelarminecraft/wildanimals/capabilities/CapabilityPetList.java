package com.blogspot.jabelarminecraft.wildanimals.capabilities;

import java.util.List;
import java.util.UUID;

public class CapabilityPetList implements ICapabilityPetList
{
	private List<UUID> petList = null;

	@Override
	public void setPetList(List<UUID> parPetList) 
	{
		petList = parPetList;
	}

	@Override
	public List<UUID> getPetList() 
	{
		return petList;
	}

}
