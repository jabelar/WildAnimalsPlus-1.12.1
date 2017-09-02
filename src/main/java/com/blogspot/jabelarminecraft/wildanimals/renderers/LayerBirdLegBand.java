package com.blogspot.jabelarminecraft.wildanimals.renderers;


import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerBirdLegBand implements LayerRenderer<EntityBirdOfPrey>
{
    protected ResourceLocation legBandTexture;
    protected final RenderBirdOfPrey renderer;

    public LayerBirdLegBand(RenderBirdOfPrey parRenderer, ResourceLocation parTexture)
    {
        renderer = parRenderer;
        legBandTexture = parTexture;
    }

    @Override
	public void doRenderLayer(EntityBirdOfPrey entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible())
        {
            this.renderer.bindTexture(legBandTexture);
            float[] afloat = entitylivingbaseIn.getLegBandColor().getColorComponentValues();
            GlStateManager.color(afloat[0], afloat[1], afloat[2]);
            this.renderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
	public boolean shouldCombineTextures()
    {
        return true;
    }
}