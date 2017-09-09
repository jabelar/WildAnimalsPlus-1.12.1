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

import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

// TODO: Auto-generated Javadoc
public class TameBirdTrigger implements ICriterionTrigger<TameBirdTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("tame_bird");
    private final Map<PlayerAdvancements, TameBirdTrigger.Listeners> listeners = Maps.<PlayerAdvancements, TameBirdTrigger.Listeners>newHashMap();

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
	public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<TameBirdTrigger.Instance> listener)
    {
        TameBirdTrigger.Listeners tameanimaltrigger$listeners = this.listeners.get(playerAdvancementsIn);

        if (tameanimaltrigger$listeners == null)
        {
            tameanimaltrigger$listeners = new TameBirdTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, tameanimaltrigger$listeners);
        }

        tameanimaltrigger$listeners.add(listener);
    }

    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#removeListener(net.minecraft.advancements.PlayerAdvancements, net.minecraft.advancements.ICriterionTrigger.Listener)
     */
    @Override
	public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<TameBirdTrigger.Instance> listener)
    {
        TameBirdTrigger.Listeners tameanimaltrigger$listeners = this.listeners.get(playerAdvancementsIn);

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
	public TameBirdTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context)
    {
        EntityPredicate entitypredicate = EntityPredicate.deserialize(json.get("entity"));
        return new TameBirdTrigger.Instance(entitypredicate);
    }

    /**
     * Trigger.
     *
     * @param parPlayer the player
     * @param parBird the entity
     */
    public void trigger(EntityPlayerMP parPlayer, EntityBirdOfPrey parBird)
    {
        TameBirdTrigger.Listeners tameanimaltrigger$listeners = this.listeners.get(parPlayer.getAdvancements());

        if (tameanimaltrigger$listeners != null)
        {
            tameanimaltrigger$listeners.trigger(parPlayer, parBird);
        }
    }

    public static class Instance extends AbstractCriterionInstance
    {
        private final EntityPredicate entity;

        /**
         * Instantiates a new instance.
         *
         * @param entity the entity
         */
        public Instance(EntityPredicate entity)
        {
            super(TameBirdTrigger.ID);
            this.entity = entity;
        }

        /**
         * Test.
         *
         * @param player the player
         * @param entity the entity
         * @return true, if successful
         */
        public boolean test(EntityPlayerMP player, EntityBirdOfPrey entity)
        {
            return this.entity.test(player, entity);
        }
    }

    static class Listeners
    {
        private final PlayerAdvancements playerAdvancements;
        private final Set<ICriterionTrigger.Listener<TameBirdTrigger.Instance>> listeners = Sets.<ICriterionTrigger.Listener<TameBirdTrigger.Instance>>newHashSet();

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
        public void add(ICriterionTrigger.Listener<TameBirdTrigger.Instance> listener)
        {
            this.listeners.add(listener);
        }

        /**
         * Removes the.
         *
         * @param listener the listener
         */
        public void remove(ICriterionTrigger.Listener<TameBirdTrigger.Instance> listener)
        {
            this.listeners.remove(listener);
        }

        /**
         * Trigger.
         *
         * @param player the player
         * @param entity the entity
         */
        public void trigger(EntityPlayerMP player, EntityBirdOfPrey entity)
        {
            List<ICriterionTrigger.Listener<TameBirdTrigger.Instance>> list = null;

            for (ICriterionTrigger.Listener<TameBirdTrigger.Instance> listener : this.listeners)
            {
                if (listener.getCriterionInstance().test(player, entity))
                {
                    if (list == null)
                    {
                        list = Lists.<ICriterionTrigger.Listener<TameBirdTrigger.Instance>>newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null)
            {
                for (ICriterionTrigger.Listener<TameBirdTrigger.Instance> listener1 : list)
                {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}