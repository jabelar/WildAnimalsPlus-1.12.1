package com.blogspot.jabelarminecraft.wildanimals.capabilities;

import java.util.concurrent.Callable;

public class CapabilityFactoryPetList implements Callable<ICapabilityPetList>
{
	@Override
	public ICapabilityPetList call() throws Exception
	{
		return new CapabilityPetList();
	}
}
