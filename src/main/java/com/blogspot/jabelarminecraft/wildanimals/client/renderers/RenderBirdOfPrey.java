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

package com.blogspot.jabelarminecraft.wildanimals.client.renderers;

import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

// TODO: Auto-generated Javadoc
@SuppressWarnings("rawtypes")
public class RenderBirdOfPrey extends RenderLiving
{
    protected ResourceLocation birdOfPreyTexture;

    /**
     * Instantiates a new render bird of prey.
     *
     * @param parRenderManager the par render manager
     * @param parModelBase1 the par model base 1
     * @param parModelBase2 the par model base 2
     * @param parShadowSize the par shadow size
     * @param parNormalTexture the par normal texture
     * @param parLegBandTexture the par leg band texture
     */
    @SuppressWarnings("unchecked")
	public RenderBirdOfPrey(
    		RenderManager parRenderManager,
            ModelBase parModelBase1, 
            ModelBase parModelBase2, 
            float parShadowSize,
            ResourceLocation parNormalTexture, 
            ResourceLocation parLegBandTexture
            )
    {
        super(parRenderManager, parModelBase1, parShadowSize);
        birdOfPreyTexture = parNormalTexture;
        addLayer(new LayerBirdLegBand(this, parLegBandTexture));
    }
	
 
    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     *
     * @param par1Entity the par 1 entity
     * @return the entity texture
     */
    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return getEntityTexture((EntityBirdOfPrey)par1Entity);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     *
     * @param parEntityBirdOfPrey the par entity bird of prey
     * @return the entity texture
     */
    protected ResourceLocation getEntityTexture(EntityBirdOfPrey parEntityBirdOfPrey)
    {
        return birdOfPreyTexture;
    }  
    
    /**
     * Gets the render factory.
     *
     * @param parModelBase1 the par model base 1
     * @param parModelBase2 the par model base 2
     * @param parShadowSize the par shadow size
     * @param parNormalTexture the par normal texture
     * @param parLegBandTexture the par leg band texture
     * @return the render factory
     */
    public static IRenderFactory getRenderFactory(
	        ModelBase parModelBase1, 
	        ModelBase parModelBase2, 
	        float parShadowSize, 
	        ResourceLocation parNormalTexture, 
			ResourceLocation parLegBandTexture
			)
    {
    	return new RenderFactory(
    	        parModelBase1, 
    	        parModelBase2, 
    	        parShadowSize, 
    	        parNormalTexture, 
    			parLegBandTexture
    			);
    }
    
	private static class RenderFactory implements IRenderFactory
	{
        ModelBase model1;
        ModelBase model2; 
        float shadowSize;
        ResourceLocation normalTexture; 
		ResourceLocation legBandTexture;
		
		public RenderFactory(
	        ModelBase parModelBase1, 
	        ModelBase parModelBase2, 
	        float parShadowSize, 
	        ResourceLocation parNormalTexture, 
			ResourceLocation parLegBandTexture)
		{
			model1 = parModelBase1;
			model2 = parModelBase2;
			shadowSize = parShadowSize;
			normalTexture = parNormalTexture;
			legBandTexture = parLegBandTexture;		
		}

		@Override
		public Render createRenderFor(RenderManager manager) 
		{
			return new RenderBirdOfPrey(
				manager,
				model1,
				model2,
				shadowSize,
				normalTexture,
				legBandTexture
			);
		}	
	}
 }