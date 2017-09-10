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
package com.blogspot.jabelarminecraft.wildanimals.advancements.criteria;

public class Triggers
{
    public static final CustomTrigger TAME_BIRD = new CustomTrigger("tame_bird");
    public static final CustomTrigger TAME_BIG_CAT = new CustomTrigger("tame_big_cat");
    
    public static final CustomTrigger[] TRIGGER_ARRAY = new CustomTrigger[] {
    	TAME_BIRD,
    	TAME_BIG_CAT
    };
}