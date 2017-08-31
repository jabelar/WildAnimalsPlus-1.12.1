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

import org.lwjgl.opengl.GL11;

import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderBirdOfPrey extends RenderLiving
{
    protected ResourceLocation birdOfPreyTexture;
    protected ResourceLocation legBandTexture;

    public RenderBirdOfPrey(
            ModelBase parModelBase1, 
            ModelBase parModelBase2, 
            float parShadowSize,
            ResourceLocation parNormalTexture, 
            ResourceLocation parLegBandTexture
            )
    {
        super(parModelBase1, parShadowSize);
        setRenderPassModel(parModelBase2); // I believe this is used for the leg band
        birdOfPreyTexture = parNormalTexture;
        legBandTexture = parLegBandTexture;        
    }
	
    @Override
	protected void preRenderCallback(EntityLivingBase entity, float f)
    {
    	preRenderCallbackBirdOfPrey((EntityBirdOfPrey) entity, f);
    }

    
	protected void preRenderCallbackBirdOfPrey(EntityBirdOfPrey entity, float f)
	{
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return getEntityTexture((EntityBirdOfPrey)par1Entity);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityBirdOfPrey parEntityBirdOfPrey)
    {
        return birdOfPreyTexture;
    }
    
    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityBirdOfPrey parEntityBirdOfPrey, int parRenderPass, float parShakeShadingFactor)
    {
        if (parRenderPass == 1 && parEntityBirdOfPrey.isTamed())
        {
            bindTexture(legBandTexture);
            float[] rgb = parEntityBirdOfPrey.getLegBandColor().getColorComponentValues();
            GL11.glColor3f(rgb[0], rgb[1], rgb[2]);
            return 1;
        }
        else
        {
            return -1;
        }
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    @Override
    protected int shouldRenderPass(EntityLivingBase parEntityLivingBase, int parRenderPass, float par3)
    {
        return shouldRenderPass((EntityBirdOfPrey)parEntityLivingBase, parRenderPass, par3);
    }

}