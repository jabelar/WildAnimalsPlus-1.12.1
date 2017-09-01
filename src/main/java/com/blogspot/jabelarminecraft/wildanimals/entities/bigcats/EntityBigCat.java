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

package com.blogspot.jabelarminecraft.wildanimals.entities.bigcats;

import java.util.UUID;

import com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.bigcat.EntityAIBegBigCat;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.bigcat.EntityAIFollowBigCat;
import com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals.EntityHerdAnimal;
import com.blogspot.jabelarminecraft.wildanimals.utilities.Utilities;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBigCat extends EntityTameable implements IModEntity
{
    protected NBTTagCompound syncDataCompound = new NBTTagCompound();
    
    protected SoundEvent soundAmbientGrowl = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.growl"));
    protected SoundEvent soundAmbientWhine = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.whine"));
    protected SoundEvent soundAmbientPanting = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.panting"));
    protected SoundEvent soundAmbientBark = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.bark"));
    protected SoundEvent soundHurt = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.hurt"));
    protected SoundEvent soundDeath = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.death"));
    protected SoundEvent soundShake = new SoundEvent(new ResourceLocation( "wildanimals:mob.bigCat.shake"));

    /**
     * fields for controlling head tilt, like when interested or shaking
     */
    protected float targetHeadAngle;
    protected float prevHeadAngle;
    
    /**
     * true if the bigCat is wet else false
     */
    protected boolean isShaking;
    protected boolean startedShaking;
    
    protected final float TAMED_HEALTH = 20.0F;
    
    /**
     * This time increases while bigCat is shaking and emitting water particles.
     */
    protected float timeBigCatIsShaking;
    protected float prevTimeBigCatIsShaking;
 
    // good to have instances of AI so task list can be modified, including in sub-classes
    protected EntityAIBase aiSwimming = new EntityAISwimming(this);
    protected EntityAIBase aiLeapAtTarget = new EntityAILeapAtTarget(this, 0.4F);
    protected EntityAIBase aiAttackOnCollide = new EntityAIAttackMelee(this, 1.0D, true);
    protected EntityAIBase aiFollowOwner = new EntityAIFollowBigCat(this, 1.0D, 10.0F, 2.0F);
    protected EntityAIBase aiMate = new EntityAIMate(this, 1.0D);
    protected EntityAIBase aiWander = new EntityAIWander(this, 1.0D);
    protected EntityAIBase aiBeg = new EntityAIBegBigCat(this, 8.0F); // in vanilla begging is only for wolf
    protected EntityAIBase aiWatchClosest = new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F);
    protected EntityAIBase aiLookIdle = new EntityAILookIdle(this);
    protected EntityAIBase aiOwnerHurtByTarget = new EntityAIOwnerHurtByTarget(this);
    protected EntityAIBase aiOwnerHurtTarget = new EntityAIOwnerHurtTarget(this);
    protected EntityAIBase aiHurtByTarget = new EntityAIHurtByTarget(this, true);
	@SuppressWarnings("unchecked")
    protected EntityAIBase aiTargetNonTamedSheep = new EntityAITargetNonTamed(this, EntitySheep.class, false, (Predicate)null);
	@SuppressWarnings({ "unchecked", "rawtypes" })
    protected EntityAIBase aiTargetNonTamedCow = new EntityAITargetNonTamed(this, EntityCow.class, false, (Predicate)null);
	@SuppressWarnings({ "unchecked", "rawtypes" })
    protected EntityAIBase aiTargetNonTamedPig = new EntityAITargetNonTamed(this, EntityPig.class, false, (Predicate)null);
	@SuppressWarnings({ "unchecked", "rawtypes" })
    protected EntityAIBase aiTargetNonTamedChicken = new EntityAITargetNonTamed(this, EntityChicken.class, false, (Predicate)null);
	@SuppressWarnings({ "unchecked", "rawtypes" })
    protected EntityAIBase aiTargetNonTamedHerdAnimal = new EntityAITargetNonTamed(this, EntityHerdAnimal.class, false, (Predicate)null);

	
    public EntityBigCat(World parWorld)
    {
        super(parWorld);
        
//        // DEBUG
//        System.out.println("EntityBigCat constructor(), "+"on Client="+par1World.isRemote);

        setSize(1.0F, 1.33F);
        initSyncDataCompound();
        setupAI();		
 	}
	
	@Override
	public void setupAI() 
	{
        setPathPriority(PathNodeType.WATER, 0.0F);
        clearAITasks(); // clear any tasks assigned in super classes
        tasks.addTask(1, aiSwimming);
        tasks.addTask(2, aiSit);
        tasks.addTask(3, aiLeapAtTarget);
        tasks.addTask(4, aiAttackOnCollide);
        tasks.addTask(5, aiFollowOwner);
        tasks.addTask(6, aiMate);
        tasks.addTask(7, aiWander);
        tasks.addTask(8, aiBeg); // in vanilla begging is only for wolf
        tasks.addTask(9, aiWatchClosest);
        tasks.addTask(10, aiLookIdle);
        targetTasks.addTask(1, aiOwnerHurtByTarget);
        targetTasks.addTask(2, aiOwnerHurtTarget);
        targetTasks.addTask(3, aiHurtByTarget);
        targetTasks.addTask(4, aiTargetNonTamedSheep);
        targetTasks.addTask(4, aiTargetNonTamedCow);
        targetTasks.addTask(4, aiTargetNonTamedPig);
        targetTasks.addTask(4, aiTargetNonTamedChicken);
        targetTasks.addTask(4, aiTargetNonTamedHerdAnimal);
    }

	@Override
	public void initSyncDataCompound() 
	{
//	    // DEBUG
//	    System.out.println("Initializing sync data compound");
	    
    	syncDataCompound.setFloat("scaleFactor", 1.2F);
    	syncDataCompound.setBoolean("isInterested", false);
    	syncDataCompound.setBoolean("isTamed", false);
    	syncDataCompound.setBoolean("isAngry", false);
    	syncDataCompound.setBoolean("isSitting", false);
    	syncDataCompound.setByte("collarColor", (byte) 0);
    	syncDataCompound.setString("ownerName", "");
    	syncDataCompound.setLong("ownerUUIDMSB", 0);
    	syncDataCompound.setLong("ownerUUIDLSB", 0);
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
        super.applyEntityAttributes(); // registers the common attributes
        
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);

        // need to register any additional attributes
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }
    
    public void adjustEntityAttributes()
    {
        if (isTamed())
        {
            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(TAMED_HEALTH);
        }
    }

 
    /**
     * Sets the active target the Task system uses for tracking
     */
    @Override
	public void setAttackTarget(EntityLivingBase parEntityLivingBase)
    {
        super.setAttackTarget(parEntityLivingBase);

        if (parEntityLivingBase == null)
        {
            setAngry(false);
        }
        else if (!isTamed())
        {
            setAngry(true);
        }
    }

 	protected void updateAITick()
    {
        // Need to adjust attributes after the save data regarding
        // whether it is tamed is loaded and synced.
        if (ticksExisted == 10)
        {
            // note that the setTamed also forces a full NBT sync to client
            if (syncDataCompound.getBoolean("isTamed"))
            {
                setTamed(true);
            }
            else
            {
                setTamed(false);
            }
            adjustEntityAttributes();
        }
    }
 
    @Override
	protected void entityInit()
    {
        super.entityInit();
    }

//    @Override
//	protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
//    {
//        playSound("wildanimals:mob.bigCat.step", 0.15F, 1.0F); // this is randomized from 1 to 5
//    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound parCompound)
    {
//        // DEBUG
//        System.out.println("Writing NBT");
        super.writeToNBT(parCompound);
        parCompound.setTag("extendedPropsJabelar", syncDataCompound);
        return parCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound parCompound)
    {
//        // DEBUG
//        System.out.println("Reading NBT");
        super.readFromNBT(parCompound);
        syncDataCompound = (NBTTagCompound) parCompound.getTag("extendedPropsJabelar");
        sendEntitySyncPacket();
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    @Override
	protected SoundEvent getAmbientSound()
    {
        return isAngry() ? soundAmbientGrowl : (rand.nextInt(3) == 0 ? (isTamed() && getHealth() < 10.0F ? soundAmbientWhine : soundAmbientPanting) : soundAmbientBark);
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected SoundEvent getHurtSound(DamageSource parSource)
    {
        return soundHurt; // It uses sounds.json file to randomize and adds 1, 2 or 3 and .ogg
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected SoundEvent getDeathSound()
    {
        return soundDeath;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    @Override
	protected float getSoundVolume()
    {
        return 0.4F;
    }

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
        
        updateAITick();

        if (!world.isRemote && isShaking && !startedShaking && !hasPath() && onGround)
        {
            startedShaking = true;
            timeBigCatIsShaking = 0.0F;
            prevTimeBigCatIsShaking = 0.0F;
            world.setEntityState(this, (byte)8);
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
	public void onUpdate()
    {
        super.onUpdate();
        prevHeadAngle = targetHeadAngle;

        if (getInterested())
        {
            targetHeadAngle += (1.0F - targetHeadAngle) * 0.4F;
        }
        else
        {
            targetHeadAngle += (0.0F - targetHeadAngle) * 0.4F;
        }

        if (isWet())
        {
            isShaking = true;
            startedShaking = false;
            timeBigCatIsShaking = 0.0F;
            prevTimeBigCatIsShaking = 0.0F;
        }
        else if ((isShaking || startedShaking) && startedShaking)
        {
            if (timeBigCatIsShaking == 0.0F)
            {
                playSound(soundShake, getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
            }

            prevTimeBigCatIsShaking = timeBigCatIsShaking;
            timeBigCatIsShaking += 0.05F;

            if (prevTimeBigCatIsShaking >= 2.0F)
            {
                isShaking = false;
                startedShaking = false;
                prevTimeBigCatIsShaking = 0.0F;
                timeBigCatIsShaking = 0.0F;
            }

            if (timeBigCatIsShaking > 0.4F)
            {
                float f = (float)getEntityBoundingBox().minY;
                int i = (int)(MathHelper.sin((timeBigCatIsShaking - 0.4F) * (float)Math.PI) * 7.0F);

                for (int j = 0; j < i; ++j)
                {
                    float f1 = (rand.nextFloat() * 2.0F - 1.0F) * width * 0.5F;
                    float f2 = (rand.nextFloat() * 2.0F - 1.0F) * width * 0.5F;
                    world.spawnParticle(EnumParticleTypes.WATER_SPLASH, posX + f1, f + 0.8F, posZ + f2, motionX, motionY, motionZ);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean getBigCatShaking()
    {
        return isShaking;
    }

    /**
     * Used when calculating the amount of shading to apply while the bigCat is shaking.
     */
    @SideOnly(Side.CLIENT)
    public float getShadingWhileShaking(float parShakeRate)
    {
        return 0.75F + (prevTimeBigCatIsShaking + (timeBigCatIsShaking - prevTimeBigCatIsShaking) * parShakeRate) / 2.0F * 0.25F;
    }

    @SideOnly(Side.CLIENT)
    public float getShakeAngle(float parShakeRate, float par2)
    {
        float f2 = (prevTimeBigCatIsShaking + (timeBigCatIsShaking - prevTimeBigCatIsShaking) * parShakeRate + par2) / 1.8F;

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }
        else if (f2 > 1.0F)
        {
            f2 = 1.0F;
        }

        return MathHelper.sin(f2 * (float)Math.PI) * MathHelper.sin(f2 * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
    }

    @Override
	public float getEyeHeight()
    {
        return height * 0.8F;
    }

    @SideOnly(Side.CLIENT)
    public float getInterestedAngle(float parRateOfAngleChange)
    {
        return (prevHeadAngle + (targetHeadAngle - prevHeadAngle) * parRateOfAngleChange) * 0.15F * (float)Math.PI;
    }

    /**
     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
     * use in wolves.
     */
    @Override
	public int getVerticalFaceSpeed()
    {
        return isSitting() ? 20 : super.getVerticalFaceSpeed();
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        if (getIsInvulnerable())
        {
            return false;
        }
        else
        {
            aiSit.setSitting(false);

            return super.attackEntityFrom(par1DamageSource, par2);
        }
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.entity.EntityLivingBase#attackEntityAsMob(net.minecraft.entity.Entity)
     */
    @Override
	public boolean attackEntityAsMob(Entity par1Entity)
    {
        return par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    @Override
	public boolean processInteract(EntityPlayer parPlayer, EnumHand parHand)
    {
        // DEBUG
        System.out.println("EntityBigCat interact()");
 
        ItemStack itemInHand = parPlayer.getHeldItem(parHand);

        // heal tamed with food
        if (isTamed())
        {
            if (!itemInHand.isEmpty())
            {
                if (itemInHand.getItem() instanceof ItemFood)
                {
                    ItemFood itemfood = (ItemFood)itemInHand.getItem();

                    if (itemfood.isWolfsFavoriteMeat() && getHealth() < TAMED_HEALTH)
                    {
                        if (!parPlayer.capabilities.isCreativeMode)
                        {
                            itemInHand.shrink(1);
                        }

                        if (itemInHand.getCount() <= 0)
                        {
                            parPlayer.inventory.setInventorySlotContents(parPlayer.inventory.currentItem, ItemStack.EMPTY);
                        }

                        heal(itemfood.getHealAmount(itemInHand));
                        return true;
                    }
                }
                else if (itemInHand.getItem() == Items.DYE)
                {
                    EnumDyeColor dyeColor = EnumDyeColor.byMetadata(itemInHand.getMetadata());

                    if (dyeColor != getCollarColor())
                    {
                        setCollarColor(dyeColor);
                        
                        if (!parPlayer.capabilities.isCreativeMode)
                        {
	                        itemInHand.shrink(1);
	                        if (!parPlayer.capabilities.isCreativeMode && itemInHand.getCount() <= 0)
	                        {
	                            parPlayer.inventory.setInventorySlotContents(parPlayer.inventory.currentItem, ItemStack.EMPTY);
	                        }
                        }
                        
                        return true;
                    }
                }
            }

            // toggle sitting
            if (isOwner(parPlayer) && !world.isRemote && !isBreedingItem(itemInHand))
            {
                setSitting(!isSitting());
                aiSit.setSitting(isSitting());
                isJumping = false;
                navigator.clearPathEntity();
                setAttackTarget((EntityLivingBase)null);
            }
        }
        
        // tame with bone
        else if (!itemInHand.isEmpty() && itemInHand.getItem() == Items.BONE && !isAngry())
        {
            if (!parPlayer.capabilities.isCreativeMode)
            {
                itemInHand.shrink(1);
            }

            if (itemInHand.getCount() <= 0)
            {
                parPlayer.inventory.setInventorySlotContents(parPlayer.inventory.currentItem, ItemStack.EMPTY);
            }

            // Try taming
            if (!world.isRemote)
            {
                if (rand.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, parPlayer))
                {
                    setTamedBy(parPlayer);
                    navigator.clearPathEntity();
                    setAttackTarget((EntityLivingBase)null);
                    aiSit.setSitting(true);
                    setHealth(TAMED_HEALTH);
                    playTameEffect(true);
                    world.setEntityState(this, (byte)7);
                    // DEBUG
                    System.out.println("Taming successful for owner = "+parPlayer.getCommandSenderEntity());
                }
                else
                {
                    playTameEffect(false);
                    world.setEntityState(this, (byte)6);
                }
            }
        }
        
        // grow with meat
        else if (!itemInHand.isEmpty() && itemInHand.getItem() == Items.BEEF && !isAngry())
        {
            if (!parPlayer.capabilities.isCreativeMode)
            {
                itemInHand.shrink(1);
            }

            if (itemInHand.getCount() <= 0)
            {
                parPlayer.inventory.setInventorySlotContents(parPlayer.inventory.currentItem, ItemStack.EMPTY);
            }

            if (!world.isRemote)
            {
                if (rand.nextInt(3) == 0)
                {
                    setGrowingAge(getGrowingAge()+500);
                    world.setEntityState(this, (byte)7);
                }
                else
                {
                    playTameEffect(false);
                    world.setEntityState(this, (byte)6);
                }
            }

        return true;
        }

        return super.processInteract(parPlayer, parHand);
    }


    @Override
    public boolean isTamed()
    {
        boolean isTamed = syncDataCompound.getBoolean("isTamed");
        return isTamed;
    }

    @Override
    public void setTamed(boolean parTamed)
    {
        syncDataCompound.setBoolean("isTamed", parTamed);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();

        if (parTamed)
        {
            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(TAMED_HEALTH);
        }
        else
        {
            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        }
    }

    @Override
    public boolean isSitting()
    {
        return syncDataCompound.getBoolean("isSitting");
    }

    @Override
    public void setSitting(boolean parIsSitting)
    {
        syncDataCompound.setBoolean("isSitting", parIsSitting);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

    public String getOwnerName()
    {
        return getOwner().getCommandSenderEntity().getName();
    }

    public void setOwner(UUID parOwnerUUID)
    {
        syncDataCompound.setLong("ownerUUIDMSB", parOwnerUUID.getMostSignificantBits());
        syncDataCompound.setLong("ownerUUIDLSB", parOwnerUUID.getLeastSignificantBits());
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }
    
    @Override
    public EntityLivingBase getOwner()
    {
        UUID uuid = new UUID(syncDataCompound.getLong("ownerUUIDMSB"), syncDataCompound.getLong("ownerUUIDLSB"));
        return world.getPlayerEntityByUUID(uuid); 
    }

//    @Override
//	@SideOnly(Side.CLIENT)
//    public void handleHealthUpdate(byte par1)
//    {
//        if (par1 == 8)
//        {
//            startedShaking = true;
//            timeBigCatIsShaking = 0.0F;
//            prevTimeBigCatIsShaking = 0.0F;
//        }
//        else
//        {
//            super.handleHealthUpdate(par1);
//        }
//    }

    @SideOnly(Side.CLIENT)
    public float getTailRotation()
    {
        return isAngry() ? 1.5393804F : (isTamed() ? (0.55F - (TAMED_HEALTH - getHealth()) * 0.02F) * (float)Math.PI : ((float)Math.PI / 5F));
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
    @Override
	public boolean isBreedingItem(ItemStack par1ItemStack)
    {
        return par1ItemStack == ItemStack.EMPTY ? false : (!(par1ItemStack.getItem() instanceof ItemFood) ? false : ((ItemFood)par1ItemStack.getItem()).isWolfsFavoriteMeat());
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    @Override
	public int getMaxSpawnedInChunk()
    {
        return 8;
    }

    /**
     * Determines whether this bigCat is angry or not.
     */
    public boolean isAngry()
    {
        return syncDataCompound.getBoolean("isAngry");
    }

    /**
     * Sets whether this bigCat is angry or not.
     */
    public void setAngry(boolean parIsAngry)
    {
        syncDataCompound.setBoolean("isAngry", parIsAngry);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

    /**
     * Return this bigCat's collar color.
     */
    public EnumDyeColor getCollarColor()
    {
        return EnumDyeColor.byMetadata(syncDataCompound.getByte("collarColor"));
    }

    /**
     * Set this bigCat's collar color.
     */
    public void setCollarColor(EnumDyeColor parCollarColor)
    {
        syncDataCompound.setByte("collarColor", (byte) parCollarColor.getMetadata());
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

    @Override
	public EntityBigCat createChild(EntityAgeable par1EntityAgeable)
    {
        
        // DEBUG
        System.out.println("EntityBigCat createChild()");
 
        EntityBigCat entityBigCat = new EntityBigCat(world);
        UUID s = entityBigCat.getOwnerId(); // used to be getOwnerName();

        if (s != null)
        {
            entityBigCat.setTamed(true);
        }

        return entityBigCat;
    }

    public void setInterested(boolean parIsInterested)
    {
        syncDataCompound.setBoolean("isInterested", parIsInterested);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

    public boolean getInterested()
    {
        return syncDataCompound.getBoolean("isInterested");
    }
    
    /**
     * Returns true if the mob is currently able to mate with the specified mob.
     */
    @Override
	public boolean canMateWith(EntityAnimal parEntityAnimal)
    {
        if (parEntityAnimal == this)
        {
            return false;
        }
        else if (!isTamed())
        {
            return false;
        }
        else if (!(parEntityAnimal instanceof EntityBigCat))
        {
            return false;
        }
        else
        {
            EntityBigCat entitybigCat = (EntityBigCat)parEntityAnimal;
            // DEBUG
            System.out.println("Found mate = "+entitybigCat);
            return !entitybigCat.isTamed() ? false : (entitybigCat.isSitting() ? false : isInLove() && entitybigCat.isInLove());
        }
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    @Override
	protected boolean canDespawn()
    {
        return !isTamed() && ticksExisted > 2400;
    }

//    /*
//     * Used by the EntityAIOwnerHurt target and EntityAIOwnerHurtByTarget classes to identity 
//     * suitable attack targets
//     */
//    @Override
//	public boolean func_142018_a(EntityLivingBase par1EntityLivingBase, EntityLivingBase par2EntityLivingBase)
//    {
//        if (!(par1EntityLivingBase instanceof EntityCreeper) && !(par1EntityLivingBase instanceof EntityGhast))
//        {
//            if (par1EntityLivingBase instanceof EntityBigCat)
//            {
//                EntityBigCat entitybigCat = (EntityBigCat)par1EntityLivingBase;
//
//                if (entitybigCat.isTamed() && entitybigCat.getOwner() == par2EntityLivingBase)
//                {
//                    return false;
//                }
//            }
//
//            return par1EntityLivingBase instanceof EntityPlayer && par2EntityLivingBase instanceof EntityPlayer && !((EntityPlayer)par2EntityLivingBase).canAttackPlayer((EntityPlayer)par1EntityLivingBase) ? false : !(par1EntityLivingBase instanceof EntityHorse) || !((EntityHorse)par1EntityLivingBase).isTame();
//        }
//        else
//        {
//            return false;
//        }
//    }

    // *****************************************************
    // ENCAPSULATION SETTER AND GETTER METHODS
    // Don't forget to send sync packets in setters
    // *****************************************************
        
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
    	return syncDataCompound.getFloat("scaleFactor");
    }
    
    @Override
    public void sendEntitySyncPacket()
    {
        Utilities.sendEntitySyncPacketToClient(this);
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