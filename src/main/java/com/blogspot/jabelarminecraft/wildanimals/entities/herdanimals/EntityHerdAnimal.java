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

package com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals;

import com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.herdanimal.EntityAIPanicHerdAnimal;

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
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHerdAnimal extends EntityAnimal implements IModEntity
{
    protected NBTTagCompound syncDataCompound = new NBTTagCompound();
    protected static final DataParameter<NBTTagCompound> SYNC_COMPOUND = EntityDataManager.<NBTTagCompound>createKey(Entity.class, DataSerializers.COMPOUND_TAG);

    protected static final int REARING_TICKS_MAX = 20;
    
    protected boolean isHitWithoutResistance = false ;

    protected SoundEvent ambientSound = new SoundEvent(new ResourceLocation("mob.cow.say"));
    protected SoundEvent hurtSound = new SoundEvent(new ResourceLocation("mob.cow.say"));
    protected SoundEvent deathSound = new SoundEvent(new ResourceLocation("mob.cow.say"));

    
    public EntityHerdAnimal(World par1World)
    {
        super(par1World);
        
//        // DEBUG
//        System.out.println("EntityHerdAnimal constructor(), entity.worldObj.isRemote = "+this.worldObj.isRemote);

        setSize(0.9F, 1.3F);
        initSyncDataCompound();
        setupAI();        
     }
    
    @Override
    public void initSyncDataCompound()
    {
        syncDataCompound.setFloat("scaleFactor", 1.0F);
        syncDataCompound.setInteger("rearingCounter", 0);
        syncDataCompound.setBoolean("isRearing", false);
        dataManager.register(SYNC_COMPOUND, syncDataCompound);
    }
    
    // set up AI tasks
    @Override
    public void setupAI()
    {
    	setPathPriority(PathNodeType.WATER, 0.0F);
        clearAITasks(); // clear any tasks assigned in super classes
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIPanicHerdAnimal(this));
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
    
    // use clear tasks for subclasses then build up their ai task list specifically
    @Override
    public void clearAITasks()
    {
        tasks.taskEntries.clear();
        targetTasks.taskEntries.clear();
    }

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
     */
    @Override
    protected SoundEvent getAmbientSound()
    {
        return ambientSound;
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected SoundEvent getHurtSound(DamageSource parDamageSource)
    {
        return hurtSound;
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
    protected SoundEvent getDeathSound()
    {
        return deathSound;
    }

//    @Override
//    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
//    {
//        playSound("mob.cow.step", 0.15F, 1.0F);
//    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    @Override
    protected float getSoundVolume()
    {
        return 0.4F;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
    }
    
    @Override
    protected Item getDropItem()
    {
        return Items.LEATHER;
    }

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
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

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
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


    @Override
    public EntityHerdAnimal createChild(EntityAgeable par1EntityAgeable)
    {
        return new EntityHerdAnimal(world);
    }
    
    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float parDamageAmount)
    {
    	setRearing(true);
    	return super.attackEntityFrom(par1DamageSource, parDamageAmount);
    }
    
    @SideOnly(Side.CLIENT)
    protected <T> boolean canRenderName(T entity)
    {
    	return false;
    }

    // *****************************************************
    // ENCAPSULATION SETTER AND GETTER METHODS
    // Don't forget to send sync packets in setters
    // *****************************************************
        
    public void setRearing(Boolean parSetRearing)
    {
        if (parSetRearing && getAttackTarget()==null) // don't rear if already has target
        {
            setRearingCounter(REARING_TICKS_MAX);
            syncDataCompound.setBoolean("isRearing", true);
            // DEBUG
            System.out.println("Rearing instead of fleeing");
            System.out.println("rearingCounter = "+getRearingCounter());
          }
        else
        {
            setRearingCounter(0);
            syncDataCompound.setBoolean("isRearing", false);
            // DEBUG
            System.out.println("Finished Rearing");
            System.out.println("rearingCounter = "+getRearingCounter());
           }
        
         // don't forget to sync client and server
        sendEntitySyncPacket();
    }
    
    public boolean isRearing()
    {
        return dataManager.get(SYNC_COMPOUND).getBoolean("isRearing");
    }
    
    @Override
    public void setScaleFactor(float parScaleFactor)
    {
        syncDataCompound.setFloat("scaleFactor", Math.abs(parScaleFactor));
       
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }
    
    @Override
    public float getScaleFactor()
    {
        return dataManager.get(SYNC_COMPOUND).getFloat("scaleFactor");
    }
    
    public void setRearingCounter(int parTicks)
    {
        syncDataCompound.setInteger("rearingCounter", parTicks);
           
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }
    
    public void decrementRearingCounter()
    {
        syncDataCompound.setInteger("rearingCounter", getRearingCounter()-1);
           
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }
    
    public int getRearingCounter()
    {
        return dataManager.get(SYNC_COMPOUND).getInteger("rearingCounter");
    }

    public boolean isRearingFirstTick()
    {
        return (dataManager.get(SYNC_COMPOUND).getInteger("rearingCounter")==REARING_TICKS_MAX);
    }
    
    @Override
    public void sendEntitySyncPacket()
    {
        dataManager.set(SYNC_COMPOUND, syncDataCompound);
    }

    @Override
    public NBTTagCompound getSyncDataCompound()
    {
        return syncDataCompound;
    }
    
    @Override
    public void setSyncDataCompound(NBTTagCompound parCompound)
    {
        syncDataCompound = parCompound;
    }
}