package com.blogspot.jabelarminecraft.wildanimals.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PetList implements IPetList
{
	private List<UUID> petList = new ArrayList<UUID>();

	@Override
	public void setPetList(List<UUID> parPetList) 
	{
		petList = parPetList;
	}

	@Override
	public ArrayList<UUID> getPetList() 
	{
		return (ArrayList<UUID>) petList;
	}

}
