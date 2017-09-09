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

import com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals.EntityHerdAnimal;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

// TODO: Auto-generated Javadoc
@SuppressWarnings("rawtypes")
public class RenderHerdAnimal extends RenderLiving
{
    protected ResourceLocation herdAnimalTexture;

    /**
     * Instantiates a new render herd animal.
     *
     * @param parRenderManager the par render manager
     * @param par1ModelBase the par 1 model base
     * @param parShadowSize the par shadow size
     * @param parNormalTexture the par normal texture
     */
    public RenderHerdAnimal(
    		RenderManager parRenderManager, 
    		ModelBase par1ModelBase, 
    		float parShadowSize,
    		ResourceLocation parNormalTexture
    		)
    {
        super(parRenderManager, par1ModelBase, parShadowSize);
        herdAnimalTexture = parNormalTexture;       
    }
	
    /**
     * Pre render callback.
     *
     * @param entity the entity
     * @param f the f
     */
    @Override
	protected void preRenderCallback(EntityLivingBase entity, float f)
    {
    	preRenderCallbackHerdAnimal((EntityHerdAnimal) entity, f);
    }

    
	/**
	 * Pre render callback herd animal.
	 *
	 * @param entity the entity
	 * @param f the f
	 */
	protected void preRenderCallbackHerdAnimal(EntityHerdAnimal entity, float f)
	{
    }
    
    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     *
     * @param par1EntityHerdAnimal the par 1 entity herd animal
     * @return the entity texture
     */
    protected ResourceLocation getEntityTexture(EntityHerdAnimal par1EntityHerdAnimal)
    {
        return herdAnimalTexture;
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
        return this.getEntityTexture((EntityHerdAnimal)par1Entity);
    }
    
    /**
     * Gets the render factory.
     *
     * @param parModelBase1 the par model base 1
     * @param parShadowSize the par shadow size
     * @param parNormalTexture the par normal texture
     * @return the render factory
     */
    public static IRenderFactory getRenderFactory(
	        ModelBase parModelBase1, 
	        float parShadowSize, 
	        ResourceLocation parNormalTexture 
			)
    {
    	return new RenderFactory(
    	        parModelBase1, 
    	        parShadowSize, 
    	        parNormalTexture 
    			);
    }
    
	private static class RenderFactory implements IRenderFactory
	{
        ModelBase model1;
        float shadowSize;
        ResourceLocation normalTexture; 
		
		public RenderFactory(
	        ModelBase parModelBase1, 
	        float parShadowSize, 
	        ResourceLocation parNormalTexture) 
		{
			model1 = parModelBase1;
			shadowSize = parShadowSize;
			normalTexture = parNormalTexture;
		}

		@Override
		public Render createRenderFor(RenderManager manager) 
		{
			return new RenderHerdAnimal(
				manager,
				model1,
				shadowSize,
				normalTexture
			);
		}	
	}
}