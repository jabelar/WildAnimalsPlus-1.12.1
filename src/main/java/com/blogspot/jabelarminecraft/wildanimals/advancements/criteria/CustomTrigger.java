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

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

// TODO: Auto-generated Javadoc
public class CustomTrigger implements ICriterionTrigger<CustomTrigger.Instance>
{
    private final ResourceLocation ID;
    private final Map<PlayerAdvancements, CustomTrigger.Listeners> listeners = Maps.<PlayerAdvancements, CustomTrigger.Listeners>newHashMap();

    public CustomTrigger(String parString)
    {
		super();
		ID = new ResourceLocation(parString);
    }
    
    public CustomTrigger(ResourceLocation parRL)
    {
    	super();
    	ID = parRL;
    }
    
    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#getId()
     */
    @Override
	public ResourceLocation getId()
    {
        return ID;
    }

    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#addListener(net.minecraft.advancements.PlayerAdvancements, net.minecraft.advancements.ICriterionTrigger.Listener)
     */
    @Override
	public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<CustomTrigger.Instance> listener)
    {
        CustomTrigger.Listeners tameanimaltrigger$listeners = this.listeners.get(playerAdvancementsIn);

        if (tameanimaltrigger$listeners == null)
        {
            tameanimaltrigger$listeners = new CustomTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, tameanimaltrigger$listeners);
        }

        tameanimaltrigger$listeners.add(listener);
    }

    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#removeListener(net.minecraft.advancements.PlayerAdvancements, net.minecraft.advancements.ICriterionTrigger.Listener)
     */
    @Override
	public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<CustomTrigger.Instance> listener)
    {
        CustomTrigger.Listeners tameanimaltrigger$listeners = this.listeners.get(playerAdvancementsIn);

        if (tameanimaltrigger$listeners != null)
        {
            tameanimaltrigger$listeners.remove(listener);

            if (tameanimaltrigger$listeners.isEmpty())
            {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#removeAllListeners(net.minecraft.advancements.PlayerAdvancements)
     */
    @Override
	public void removeAllListeners(PlayerAdvancements playerAdvancementsIn)
    {
        this.listeners.remove(playerAdvancementsIn);
    }

    /**
     * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
     *
     * @param json the json
     * @param context the context
     * @return the tame bird trigger. instance
     */
    @Override
	public CustomTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context)
    {
        return new CustomTrigger.Instance(this.getId());
    }

    /**
     * Trigger.
     *
     * @param parPlayer the player
     */
    public void trigger(EntityPlayerMP parPlayer)
    {
        CustomTrigger.Listeners tameanimaltrigger$listeners = this.listeners.get(parPlayer.getAdvancements());

        if (tameanimaltrigger$listeners != null)
        {
            tameanimaltrigger$listeners.trigger(parPlayer);
        }
    }

    public static class Instance extends AbstractCriterionInstance
    {
        
        /**
         * Instantiates a new instance.
         */
        public Instance(ResourceLocation parID)
        {
            super(parID);
        }

        /**
         * Test.
         *
         * @return true, if successful
         */
        public boolean test()
        {
            return true;
        }
    }

    static class Listeners
    {
        private final PlayerAdvancements playerAdvancements;
        private final Set<ICriterionTrigger.Listener<CustomTrigger.Instance>> listeners = Sets.<ICriterionTrigger.Listener<CustomTrigger.Instance>>newHashSet();

        /**
         * Instantiates a new listeners.
         *
         * @param playerAdvancementsIn the player advancements in
         */
        public Listeners(PlayerAdvancements playerAdvancementsIn)
        {
            this.playerAdvancements = playerAdvancementsIn;
        }

        /**
         * Checks if is empty.
         *
         * @return true, if is empty
         */
        public boolean isEmpty()
        {
            return this.listeners.isEmpty();
        }

        /**
         * Adds the.
         *
         * @param listener the listener
         */
        public void add(ICriterionTrigger.Listener<CustomTrigger.Instance> listener)
        {
            this.listeners.add(listener);
        }

        /**
         * Removes the.
         *
         * @param listener the listener
         */
        public void remove(ICriterionTrigger.Listener<CustomTrigger.Instance> listener)
        {
            this.listeners.remove(listener);
        }

        /**
         * Trigger.
         *
         * @param player the player
         */
        public void trigger(EntityPlayerMP player)
        {
            List<ICriterionTrigger.Listener<CustomTrigger.Instance>> list = null;

            for (ICriterionTrigger.Listener<CustomTrigger.Instance> listener : this.listeners)
            {
                if (listener.getCriterionInstance().test())
                {
                    if (list == null)
                    {
                        list = Lists.<ICriterionTrigger.Listener<CustomTrigger.Instance>>newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null)
            {
                for (ICriterionTrigger.Listener<CustomTrigger.Instance> listener1 : list)
                {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}