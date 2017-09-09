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

import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityBigCat;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// TODO: Auto-generated Javadoc
@SuppressWarnings("rawtypes")
@SideOnly(Side.CLIENT)
public class RenderBigCat extends RenderLiving
{
    protected ResourceLocation normalTexture ;
    protected ResourceLocation tamedTexture ;
    protected ResourceLocation angryTexture ;

	/**
	 * Instantiates a new render big cat.
	 *
	 * @param parRenderManager the par render manager
	 * @param parModelBase1 the par model base 1
	 * @param parModelBase2 the par model base 2
	 * @param parShadowSize the par shadow size
	 * @param parNormalTexture the par normal texture
	 * @param parTamedTexture the par tamed texture
	 * @param parAngryTexture the par angry texture
	 * @param parCollarTexture the par collar texture
	 */
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
     * Defines what float the third param in setRotationAngles of ModelBase is.
     *
     * @param parEntityBigCat the par entity big cat
     * @param par2 the par 2
     * @return the float
     */
    protected float handleRotationFloat(EntityBigCat parEntityBigCat, float par2)
    {
        return parEntityBigCat.getTailRotation();
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is.
     *
     * @param par1EntityLivingBase the par 1 entity living base
     * @param par2 the par 2
     * @return the float
     */
    @Override
	protected float handleRotationFloat(EntityLivingBase par1EntityLivingBase, float par2)
    {
        return handleRotationFloat((EntityBigCat)par1EntityLivingBase, par2);
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
        return getEntityTexture((EntityBigCat)par1Entity);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     *
     * @param parEntityBigCat the par entity big cat
     * @return the entity texture
     */
    protected ResourceLocation getEntityTexture(EntityBigCat parEntityBigCat)
    {
        return parEntityBigCat.isTamed() ? tamedTexture : (parEntityBigCat.isAngry() ? angryTexture : normalTexture);
    }
    
    /**
     * Gets the render factory.
     *
     * @param parModelBase1 the par model base 1
     * @param parModelBase2 the par model base 2
     * @param parShadowSize the par shadow size
     * @param parNormalTexture the par normal texture
     * @param parTamedTexture the par tamed texture
     * @param parAngryTexture the par angry texture
     * @param parCollarTexture the par collar texture
     * @return the render factory
     */
    public static IRenderFactory getRenderFactory(
	        ModelBase parModelBase1, 
	        ModelBase parModelBase2, 
	        float parShadowSize, 
	        ResourceLocation parNormalTexture, 
	        ResourceLocation parTamedTexture, 
			ResourceLocation parAngryTexture, 
			ResourceLocation parCollarTexture
			)
    {
    	return new RenderFactory(
    	        parModelBase1, 
    	        parModelBase2, 
    	        parShadowSize, 
    	        parNormalTexture, 
    	        parTamedTexture, 
    			parAngryTexture, 
    			parCollarTexture
    			);
    }
    
	private static class RenderFactory implements IRenderFactory
	{
        ModelBase model1;
        ModelBase model2; 
        float shadowSize;
        ResourceLocation normalTexture; 
        ResourceLocation tamedTexture;
		ResourceLocation angryTexture; 
		ResourceLocation collarTexture;
		
		public RenderFactory(
	        ModelBase parModelBase1, 
	        ModelBase parModelBase2, 
	        float parShadowSize, 
	        ResourceLocation parNormalTexture, 
	        ResourceLocation parTamedTexture, 
			ResourceLocation parAngryTexture, 
			ResourceLocation parCollarTexture)
		{
			model1 = parModelBase1;
			model2 = parModelBase2;
			shadowSize = parShadowSize;
			normalTexture = parNormalTexture;
			tamedTexture = parTamedTexture;
			angryTexture = parAngryTexture;
			collarTexture = parCollarTexture;		
		}

		@Override
		public Render createRenderFor(RenderManager manager) 
		{
			return new RenderBigCat(
				manager,
				model1,
				model2,
				shadowSize,
				normalTexture,
				tamedTexture,
				angryTexture,
				collarTexture
			);
		}	
	}
}