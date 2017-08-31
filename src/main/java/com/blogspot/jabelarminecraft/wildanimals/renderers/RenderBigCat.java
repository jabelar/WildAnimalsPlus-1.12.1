/**
    Copyright (C) 2014 by jabelar

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

package com.blogspot.jabelarminecraft.wildanimals.renderers;

import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityBigCat;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("rawtypes")
@SideOnly(Side.CLIENT)
public class RenderBigCat extends RenderLiving
{
    protected ResourceLocation normalTexture ;
    protected ResourceLocation tamedTexture ;
    protected ResourceLocation angryTexture ;

    @SuppressWarnings("unchecked")
	public RenderBigCat(
    		RenderManager parRenderManager,
            ModelBase parModelBase1, 
            ModelBase parModelBase2, 
            float parShadowSize, 
            ResourceLocation parNormalTexture, 
            ResourceLocation parTamedTexture, 
    		ResourceLocation parAngryTexture, 
    		ResourceLocation parCollarTexture)
    {
        super(parRenderManager, parModelBase1, parShadowSize);
        normalTexture = parNormalTexture ;
        tamedTexture = parTamedTexture ;
        angryTexture = parAngryTexture ;
        addLayer(new LayerBigCatCollar(this, parCollarTexture));
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    protected float handleRotationFloat(EntityBigCat parEntityBigCat, float par2)
    {
        return parEntityBigCat.getTailRotation();
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    @Override
	protected float handleRotationFloat(EntityLivingBase par1EntityLivingBase, float par2)
    {
        return handleRotationFloat((EntityBigCat)par1EntityLivingBase, par2);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return getEntityTexture((EntityBigCat)par1Entity);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityBigCat parEntityBigCat)
    {
        return parEntityBigCat.isTamed() ? tamedTexture : (parEntityBigCat.isAngry() ? angryTexture : normalTexture);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    @SuppressWarnings("unchecked")
	public void doRender(EntityWolf entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (entity.isWolfWet())
        {
            float f = entity.getBrightness() * entity.getShadingWhileWet(partialTicks);
            GlStateManager.color(f, f, f);
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
}