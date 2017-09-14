package com.blogspot.jabelarminecraft.wildanimals.capabilities;

import java.util.List;
import java.util.UUID;

public interface ICapabilityPetList
{
	void setPetList(List<UUID> parCapability);
	List<UUID> getPetList();
}
