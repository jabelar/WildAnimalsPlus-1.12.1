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

package com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals;

import com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity;
import com.blogspot.jabelarminecraft.wildanimals.registries.Sounds;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// TODO: Auto-generated Javadoc
public class EntityHerdAnimal extends EntityAnimal implements IModEntity
{
    protected static final DataParameter<Float> SCALE_FACTOR = EntityDataManager.<Float>createKey(EntityHerdAnimal.class, DataSerializers.FLOAT);
    protected static final DataParameter<Integer> REARING_COUNTER = EntityDataManager.<Integer>createKey(EntityHerdAnimal.class, DataSerializers.VARINT);
    protected static final DataParameter<Boolean> IS_REARING = EntityDataManager.<Boolean>createKey(EntityHerdAnimal.class, DataSerializers.BOOLEAN);

    protected static final int REARING_TICKS_MAX = 20;
    
    protected boolean isHitWithoutResistance = false ;

//    protected SoundEvent soundAmbientElephant = new SoundEvent(new ResourceLocation("mob.cow.say"));
//    protected SoundEvent soundHurtElephant = new SoundEvent(new ResourceLocation("mob.cow.say"));
//    protected SoundEvent soundDeathElephant = new SoundEvent(new ResourceLocation("mob.cow.say"));
  
    /**
     * Instantiates a new entity herd animal.
     *
     * @param par1World the par 1 world
     */
    public EntityHerdAnimal(World par1World)
    {
        super(par1World);
        
//        // DEBUG
//        System.out.println("EntityHerdAnimal constructor(), entity.worldObj.isRemote = "+this.worldObj.isRemote);

        setSize(0.9F, 1.3F);
     }
    
    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityAgeable#entityInit()
     */
    @Override
	public void entityInit()
    {
    	super.entityInit();
    	dataManager.register(SCALE_FACTOR, 1.0F);
    	dataManager.register(REARING_COUNTER, 0);
    	dataManager.register(IS_REARING, false);
    }
 
    
    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLiving#initEntityAI()
     */
    // set up AI tasks
    @Override
    public void initEntityAI()
    {
        clearAITasks(); // clear any tasks assigned in super classes
        tasks.addTask(0, new EntityAISwimming(this));
//        tasks.addTask(1, new EntityAIPanicHerdAnimal(this));
        // the leap and the collide together form an actual attack
        tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4F));
        tasks.addTask(3, new EntityAIAttackMelee(this, 1.0D, true));
        tasks.addTask(5, new EntityAIMate(this, 1.0D));
        tasks.addTask(6, new EntityAITempt(this, 1.25D, Items.WHEAT, false));
        tasks.addTask(7, new EntityAIFollowParent(this, 1.25D));
        tasks.addTask(8, new EntityAIWander(this, 1.0D));
        tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(10, new EntityAILookIdle(this));
        targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));         
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
     * @see net.minecraft.entity.EntityLiving#applyEntityAttributes()
     */
    // you don't have to call this as it is called automatically during entityLiving subclass creation
    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        // standard attributes registered to EntityLivingBase
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.8D); // hard to knock back
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
        // need to register any additional attributes
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    /**
     * Returns the sound this mob makes while it's alive.
     *
     * @return the ambient sound
     */
    @Override
    protected SoundEvent getAmbientSound()
    {
        return Sounds.soundAmbientElephant;
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     *
     * @param parDamageSource the par damage source
     * @return the hurt sound
     */
    @Override
    protected SoundEvent getHurtSound(DamageSource parDamageSource)
    {
        return Sounds.soundHurtElephant;
    }

    /**
     * Returns the sound this mob makes on death.
     *
     * @return the death sound
     */
    @Override
    protected SoundEvent getDeathSound()
    {
        return Sounds.soundDeathElephant;
    }

//    @Override
//    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
//    {
//        playSound("mob.cow.step", 0.15F, 1.0F);
//    }

    /**
 * Returns the volume for the sounds this mob makes.
 *
 * @return the sound volume
 */
    @Override
    protected float getSoundVolume()
    {
        return 0.4F;
    }
    
    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLiving#getDropItem()
     */
    @Override
    protected Item getDropItem()
    {
        return Items.LEATHER;
    }

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     *
     * @param par1 the par 1
     * @param par2 the par 2
     */
    @Override
    protected void dropFewItems(boolean par1, int par2)
    {
        int j = rand.nextInt(3) + rand.nextInt(1 + par2);
        int k;

        for (k = 0; k < j; ++k)
        {
            dropItem(Items.LEATHER, 1);
        }

        j = rand.nextInt(3) + 1 + rand.nextInt(1 + par2);

        for (k = 0; k < j; ++k)
        {
            if (isBurning())
            {
                dropItem(Items.COOKED_BEEF, 1);
            }
            else
            {
                dropItem(Items.BEEF, 1);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see net.minecraft.entity.passive.EntityAnimal#onLivingUpdate()
     */
    @Override
	public void onLivingUpdate()
    {
    	super.onLivingUpdate();
    	if (isRearing())
    	{
    		decrementRearingCounter();
    	}
        
//        // DEBUG
//        if (ticksExisted%20 == 0)
//        {
//        	if (!world.isRemote)
//        	{
//        		setHealth(getHealth()-1);
//        		setRearingCounter(getRearingCounter()+1);
//        	}
//        	List<DataEntry<?>> entryList = dataManager.getAll();
//        	System.out.println("On client = "+world.isRemote+" the entity ID "+getEntityId()+" health = "+entryList.get(7).getValue()+" rearing counter = "+entryList.get(14).getValue());
////        	entryList.forEach(entry -> System.out.println("key = "+entry.getKey()+" value = "+entry.getValue()+" isDirty = "+entry.isDirty()));
//        }
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     *
     * @param player the player
     * @param hand the hand
     * @return true, if successful
     */
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);

        if (itemstack.getItem() == Items.BUCKET && !player.capabilities.isCreativeMode && !this.isChild())
        {
            player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
            itemstack.shrink(1);

            if (itemstack.isEmpty())
            {
                player.setHeldItem(hand, new ItemStack(Items.MILK_BUCKET));
            }
            else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.MILK_BUCKET)))
            {
                player.dropItem(new ItemStack(Items.MILK_BUCKET), false);
            }

            return true;
        }
        else
        {
            return super.processInteract(player, hand);
        }
    }


    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityAgeable#createChild(net.minecraft.entity.EntityAgeable)
     */
    @Override
    public EntityHerdAnimal createChild(EntityAgeable par1EntityAgeable)
    {
        return null;
    }
    
    /**
     * Called when the entity is attacked.
     *
     * @param par1DamageSource the par 1 damage source
     * @param parDamageAmount the par damage amount
     * @return true, if successful
     */
    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float parDamageAmount)
    {
    	// DEBUG
    	System.out.println("EntityHerdAnimal attackEntityFrom on client = "+world.isRemote);
    	
    	if (!world.isRemote)
    	{
    		setRearing(true);
    	}
    	return super.attackEntityFrom(par1DamageSource, parDamageAmount);
    }
    
    /**
     * Can render name.
     *
     * @param <T> the generic type
     * @param entity the entity
     * @return true, if successful
     */
    @SideOnly(Side.CLIENT)
    protected <T> boolean canRenderName(T entity)
    {
    	return false;
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLivingBase#attackEntityAsMob(net.minecraft.entity.Entity)
     */
    @Override
	public boolean attackEntityAsMob(Entity entityIn)
    {
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), ((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

        if (flag)
        {
            this.applyEnchantments(this, entityIn);
        }

        return flag;
    }

    // *****************************************************
    // ENCAPSULATION SETTER AND GETTER METHODS
    // Don't forget to send sync packets in setters
    // *****************************************************
        
    /**
     * Sets the rearing.
     *
     * @param parSetRearing the new rearing
     */
    public void setRearing(Boolean parSetRearing)
    {
        if (parSetRearing && getAttackTarget()==null) // don't rear if already has target
        {
            setRearingCounter(REARING_TICKS_MAX);
            dataManager.set(IS_REARING, true);
            // DEBUG
            System.out.println("Rearing instead of fleeing");
            System.out.println("rearingCounter = "+getRearingCounter());
          }
        else
        {
            setRearingCounter(0);
            dataManager.set(IS_REARING, false);
            // DEBUG
            System.out.println("Finished Rearing");
            System.out.println("rearingCounter = "+getRearingCounter());
           }
    }
    
    /**
     * Checks if is rearing.
     *
     * @return true, if is rearing
     */
    public boolean isRearing()
    {
        return dataManager.get(IS_REARING);
    }
    
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
    
    /**
     * Sets the rearing counter.
     *
     * @param parTicks the new rearing counter
     */
    public void setRearingCounter(int parTicks)
    {
    	if (parTicks < 0)
    	{
    		dataManager.set(REARING_COUNTER, 0);
    		dataManager.set(IS_REARING, false);
    	}
    	else
    	{
    		dataManager.set(REARING_COUNTER, parTicks);
    	}
     }
    
    /**
     * Decrement rearing counter.
     */
    public void decrementRearingCounter()
    {
        setRearingCounter(getRearingCounter()-1);
    }
    
    /**
     * Gets the rearing counter.
     *
     * @return the rearing counter
     */
    public int getRearingCounter()
    {
        return dataManager.get(REARING_COUNTER);
    }

    /**
     * Checks if is rearing first tick.
     *
     * @return true, if is rearing first tick
     */
    public boolean isRearingFirstTick()
    {
        return (getRearingCounter()==REARING_TICKS_MAX);
    }

   /* (non-Javadoc)
    * @see net.minecraft.entity.passive.EntityAnimal#writeEntityToNBT(net.minecraft.nbt.NBTTagCompound)
    */
   @Override
   public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setFloat("scaleFactor", getScaleFactor());
        compound.setInteger("rearingCounter", getRearingCounter());
        compound.setBoolean("isRearing", isRearing());
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.passive.EntityAnimal#readEntityFromNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
	public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        setScaleFactor(compound.getFloat("scaleFactor"));
        setRearingCounter(compound.getInteger("rearingCounter"));
        setRearing(compound.getBoolean("isRearing"));
    }

}