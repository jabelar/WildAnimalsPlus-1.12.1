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

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// TODO: Auto-generated Javadoc
@SideOnly(Side.CLIENT)
public class LayerBigCatCollar implements LayerRenderer<EntityBigCat>
{
    protected ResourceLocation collarTexture ;
    protected final RenderBigCat renderer;

    /**
     * Instantiates a new layer big cat collar.
     *
     * @param parRenderer the par renderer
     * @param parTexture the par texture
     */
    public LayerBigCatCollar(RenderBigCat parRenderer, ResourceLocation parTexture)
    {
        renderer = parRenderer;
        collarTexture = parTexture;
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.renderer.entity.layers.LayerRenderer#doRenderLayer(net.minecraft.entity.EntityLivingBase, float, float, float, float, float, float, float)
     */
    @Override
	public void doRenderLayer(EntityBigCat entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible())
        {
            this.renderer.bindTexture(collarTexture);
            float[] afloat = entitylivingbaseIn.getCollarColor().getColorComponentValues();
            GlStateManager.color(afloat[0], afloat[1], afloat[2]);
            this.renderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.renderer.entity.layers.LayerRenderer#shouldCombineTextures()
     */
    @Override
	public boolean shouldCombineTextures()
    {
        return true;
    }
}