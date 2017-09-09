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

package com.blogspot.jabelarminecraft.wildanimals.entities.serpents;

import com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

// TODO: Auto-generated Javadoc
public class EntitySerpent extends EntityAnimal implements IModEntity
{
    protected static final DataParameter<Float> SCALE_FACTOR = EntityDataManager.<Float>createKey(EntitySerpent.class, DataSerializers.FLOAT);

	// use fields for sounds to allow easy changes in child classes
	protected SoundEvent soundHurt = new SoundEvent(new ResourceLocation("wildanimals:mob.serpent.death"));
	protected SoundEvent soundDeath = new SoundEvent(new ResourceLocation("wildanimals:mob.serpent.death"));
	protected SoundEvent soundCall = new SoundEvent(new ResourceLocation("wildanimals:mob.serpent.hiss"));

	/**
	 * Instantiates a new entity serpent.
	 *
	 * @param par1World the par 1 world
	 */
	public EntitySerpent(World par1World)
	{
		super(par1World);
        
        // DEBUG
        System.out.println("EntitySerpent constructor(), "+"on Client="
        		+par1World.isRemote+", EntityID = "+getEntityId()+", ModEntityID = "+entityUniqueID);

        setSize(1.0F, 0.25F);
 	}
	
	/* (non-Javadoc)
	 * @see net.minecraft.entity.EntityAgeable#entityInit()
	 */
	@Override
	public void entityInit()
	{
		super.entityInit();
		dataManager.register(SCALE_FACTOR, 1.0F);
	}
    
    /* (non-Javadoc)
     * @see com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity#clearAITasks()
     */
    // use clear tasks for subclasses then build up their ai task list specifically
    @Override
	public void clearAITasks()
    {
        tasks.taskEntries.clear();
        targetTasks.taskEntries.clear();
    }

	/* (non-Javadoc)
	 * @see net.minecraft.entity.EntityLiving#initEntityAI()
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void initEntityAI() 
	{
		clearAITasks(); // clear any tasks assigned in super classes
        tasks.addTask(1, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAIPanic(this, 2.0D));
        tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, true));
        tasks.addTask(5, new EntityAIMate(this, 1.0D));
        tasks.addTask(6, new EntityAIWander(this, 1.0D));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(9, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(10, new EntityAINearestAttackableTarget(this, EntityChicken.class, true, true));
	}    

    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLiving#applyEntityAttributes()
     */
    // you don't have to call this as it is called automatically during entityLiving subclass creation
    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
    }

    /**
     * Returns the sound this mob makes while it's alive.
     *
     * @return the ambient sound
     */
    @Override
	protected SoundEvent getAmbientSound()
    {
        return soundCall;
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     *
     * @param parSource the par source
     * @return the hurt sound
     */
    @Override
	protected SoundEvent getHurtSound(DamageSource parSource)
    {
        return soundHurt; 
    }

    /**
     * Returns the sound this mob makes on death.
     *
     * @return the death sound
     */
    @Override
	protected SoundEvent getDeathSound()
    {
        return soundDeath;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     *
     * @return the sound volume
     */
    @Override
	protected float getSoundVolume()
    {
        return 0.3F;
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLiving#getDropItem()
     */
    @Override
	protected Item getDropItem()
    {
        return Item.getItemById(-1);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
	public void onLivingUpdate()
    {
        super.onLivingUpdate();
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLivingBase#attackEntityAsMob(net.minecraft.entity.Entity)
     */
    @Override
	public boolean attackEntityAsMob(Entity par1Entity)
    {
        setLastAttackedEntity(par1Entity);
        return false; // serpents don't currently attack par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue());
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityAgeable#createChild(net.minecraft.entity.EntityAgeable)
     */
    @Override
	public EntitySerpent createChild(EntityAgeable par1EntityAgeable)
    {
        
        // DEBUG
        System.out.println("EntitySerpent createChild()");
 
        EntitySerpent entitySerpent = new EntitySerpent(world);

        // transfer any attributes from parent to child here, if desired (like owner for tamed entities)

        return entitySerpent;
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.passive.EntityAnimal#writeEntityToNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
	{
	    super.writeEntityToNBT(compound);
	    compound.setFloat("scaleFactor", getScaleFactor());
 }

    /* (non-Javadoc)
     * @see net.minecraft.entity.passive.EntityAnimal#readEntityFromNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
 	public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        setScaleFactor(compound.getFloat("scaleFactor"));
    }
    
    // *****************************************************
    // ENCAPSULATION SETTER AND GETTER METHODS
    // Don't forget to send sync packets in setters
    // *****************************************************
    
    /* (non-Javadoc)
     * @see com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity#setScaleFactor(float)
     */
    @Override
	public void setScaleFactor(float parScaleFactor)
    {
    	dataManager.set(SCALE_FACTOR, Math.abs(parScaleFactor));
    }
    
    /* (non-Javadoc)
     * @see com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity#getScaleFactor()
     */
    @Override
	public float getScaleFactor()
    {
    	return dataManager.get(SCALE_FACTOR);
    }
}