package com.blogspot.jabelarminecraft.wildanimals.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface IPetList
{
	void setPetList(List<UUID> parCapability);
	ArrayList<UUID> getPetList();
}
