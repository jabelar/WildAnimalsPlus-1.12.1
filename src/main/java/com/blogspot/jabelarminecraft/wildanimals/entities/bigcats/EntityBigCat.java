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

import com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.bigcat.EntityAIBegBigCat;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.bigcat.EntityAISitBigCat;
import com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals.EntityHerdAnimal;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
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
    protected static final DataParameter<Float> SCALE_FACTOR = EntityDataManager.<Float>createKey(EntityBigCat.class, DataSerializers.FLOAT);
    protected static final DataParameter<Boolean> IS_INTERESTED = EntityDataManager.<Boolean>createKey(EntityBigCat.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean>  IS_ANGRY = EntityDataManager.<Boolean>createKey(EntityBigCat.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.<Integer>createKey(EntityBigCat.class, DataSerializers.VARINT);
    
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
 
	
    public EntityBigCat(World parWorld)
    {
        super(parWorld);
        
        // DEBUG
        System.out.println("EntityBigCat constructor(), "+"on Client="+parWorld.isRemote);

        setSize(1.0F, 1.33F);
 	}
    
    @Override
	public void entityInit()
    {
    	super.entityInit();
    	
    	// DEBUG
    	System.out.println("Entity Big Cat entityInit");
    	
    	dataManager.register(SCALE_FACTOR, 1.2F);
    	dataManager.register(IS_INTERESTED, false);
    	dataManager.register(IS_ANGRY, false);
    	dataManager.register(COLLAR_COLOR, Integer.valueOf(EnumDyeColor.RED.getDyeDamage()));
	}
	
	@Override
	public void initEntityAI() 
	{
		// DEBUG
		System.out.println("initEntityAI");
		
        clearAITasks(); // clear any tasks assigned in super classes
        tasks.addTask(1, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAISitBigCat(this));
        tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, true));
        tasks.addTask(5, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        tasks.addTask(6, new EntityAIMate(this, 1.0D));
        tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
        tasks.addTask(8, new EntityAIBegBigCat(this, 8.0F)); 
        tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(10, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(4, new EntityAITargetNonTamed<EntitySheep>(this, EntitySheep.class, true, (Predicate<? super EntitySheep>)null));
        targetTasks.addTask(4, new EntityAITargetNonTamed<EntityCow>(this, EntityCow.class, true, (Predicate<? super EntityCow>)null));
        targetTasks.addTask(4, new EntityAITargetNonTamed<EntityPig>(this, EntityPig.class, true, (Predicate<? super EntityPig>)null));
        targetTasks.addTask(4, new EntityAITargetNonTamed<EntityChicken>(this, EntityChicken.class, true, (Predicate<? super EntityChicken>)null));
        targetTasks.addTask(4, new EntityAITargetNonTamed<EntityHerdAnimal>(this, EntityHerdAnimal.class, true, (Predicate<? super EntityHerdAnimal>)null));
        targetTasks.addTask(4, new EntityAITargetNonTamed<EntityRabbit>(this, EntityRabbit.class, true, (Predicate<EntityRabbit>)null));
        
        // DEBUG
        System.out.println("Finished EntityBigCat initEntityAI");
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

        if (isInterested())
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
	public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else
        {
            Entity entity = source.getTrueSource();

            if (this.aiSit != null)
            {
                this.aiSit.setSitting(false);
            }

            if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
            {
                amount = (amount + 1.0F) / 2.0F;
            }

            return super.attackEntityFrom(source, amount);
        }
    }


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

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    @Override
	public boolean processInteract(EntityPlayer parPlayer, EnumHand parHand)
    {
        // DEBUG
        System.out.println("EntityBigCat interact() for hand = "+parHand);
        
        if (parHand == EnumHand.OFF_HAND)
        {
        	return super.processInteract(parPlayer, parHand);
        }
 
        ItemStack itemStackInHand = parPlayer.getHeldItem(parHand);

        // heal tamed with food
        if (isTamed())
        {
        	// DEBUG
        	System.out.println("Interacting with tamed big cat for hand = "+parHand);
        	
            if (!itemStackInHand.isEmpty())
            {
            	// DEBUG
            	System.out.println("Interacting with something in hand = "+itemStackInHand+" item = "+itemStackInHand.getItem());
            	
                if (itemStackInHand.getItem() instanceof ItemFood)
                {                	
                	// DEBUG
                	System.out.println("Interacting with food");         	

                    ItemFood itemfood = (ItemFood)itemStackInHand.getItem();

                    if (itemfood.isWolfsFavoriteMeat() && getHealth() < TAMED_HEALTH)
                    {
                        if (!parPlayer.capabilities.isCreativeMode)
                        {
                            itemStackInHand.shrink(1);
                        }

                        if (itemStackInHand.getCount() <= 0)
                        {
                            parPlayer.inventory.setInventorySlotContents(parPlayer.inventory.currentItem, ItemStack.EMPTY);
                        }

                        heal(itemfood.getHealAmount(itemStackInHand));
                        return true;
                    }
                }
                    

            	if (itemStackInHand.getItem() == Items.DYE)
                {
            		// DEBUG
            		System.out.println("Interacting with dye");
            		
                    EnumDyeColor dyeColor = EnumDyeColor.byDyeDamage(itemStackInHand.getMetadata());

                    if (dyeColor != getCollarColor())
                    {
                        setCollarColor(dyeColor);
                        
                        if (!parPlayer.capabilities.isCreativeMode)
                        {
	                        itemStackInHand.shrink(1);
	                        if (!parPlayer.capabilities.isCreativeMode && itemStackInHand.getCount() <= 0)
	                        {
	                            parPlayer.inventory.setInventorySlotContents(parPlayer.inventory.currentItem, ItemStack.EMPTY);
	                        }
                        }
                        
                        // DEBUG
                        System.out.println("EntityBigCat collar color now "+getCollarColor());
                        
                        return true;
                    }
                }
                    
                    
                // grow with meat
                if (itemStackInHand.getItem() == Items.BEEF && !isAngry())
                {
                	// DEBUG
                	System.out.println("Interacting with beef in hand, should grow");
                	
                    if (!parPlayer.capabilities.isCreativeMode)
                    {
                        itemStackInHand.shrink(1);
                    }

                    if (itemStackInHand.getCount() <= 0)
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
            }
            else
            {
            	// DEBUG
            	System.out.println("Interacting with nothing in hand");
               	System.out.println("Owner is player "+isOwner(parPlayer));
               	
	            // toggle sitting
	            if (isOwner(parPlayer) && !world.isRemote && !isBreedingItem(itemStackInHand))
	            {
	            	// DEBUG
	            	System.out.println("EntityBigCat toggle sitting");
	            	
	                setSitting(!isSitting());
	                isJumping = false;
	                navigator.clearPathEntity();
	                setAttackTarget((EntityLivingBase)null);
	            }
	            else
	            {
	            	// breeding items are handled in the super method
	            }
            }
        }
        else
        {
        	// DEBUG
        	System.out.println("Interacting with untamed big cat");

	        // tame with bone
	        if (!itemStackInHand.isEmpty() && itemStackInHand.getItem() == Items.BONE && !isAngry())
	        {
	            if (!parPlayer.capabilities.isCreativeMode)
	            {
	                itemStackInHand.shrink(1);
	            }
	
	            if (itemStackInHand.getCount() <= 0)
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
	                    getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
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
        }      
        return super.processInteract(parPlayer, parHand);
    }
    

    @Override
    public void setTamed(boolean parTamed)
    {
        super.setTamed(parTamed);

        if (parTamed)
        {
            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(TAMED_HEALTH);
        }
        else
        {
            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        }
        
        
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
        return dataManager.get(IS_ANGRY);
    }

    /**
     * Sets whether this bigCat is angry or not.
     */
    public void setAngry(boolean parIsAngry)
    {
        dataManager.set(IS_ANGRY, parIsAngry);
    }

    /**
     * Return this bigCat's collar color.
     */
    public EnumDyeColor getCollarColor()
    {
        return EnumDyeColor.byMetadata(dataManager.get(COLLAR_COLOR));
    }

    /**
     * Set this bigCat's collar color.
     */
    public void setCollarColor(EnumDyeColor parCollarColor)
    {
        dataManager.set(COLLAR_COLOR, parCollarColor.getMetadata());
    }

    @Override
	public EntityBigCat createChild(EntityAgeable par1EntityAgeable)
    {
        /*
         * Must @Override in classes extending this using code similar
         * to commented code below
         */
    	
//        // DEBUG
//        System.out.println("EntityBigCat createChild()");
// 
//        EntityBigCat entityBigCat = new EntityBigCat(world);
//        UUID uuid = getOwnerId(); 
//
//        if (uuid != null)
//        {
//        	entityBigCat.setOwnerId(uuid);
//            entityBigCat.setTamed(true);
//        }
//    	
//    	  return entityBigCat;

        return null;
    }

    public void setInterested(boolean parIsInterested)
    {
        dataManager.set(IS_INTERESTED, parIsInterested);
    }

    public boolean isInterested()
    {
        return dataManager.get(IS_INTERESTED);
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
 
    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
    	super.writeEntityToNBT(compound);
        compound.setFloat("scaleFactor", getScaleFactor());
        compound.setBoolean("isInterested", isInterested());
        compound.setBoolean("isAngry", isAngry());
        compound.setBoolean("isSitting", isSitting());
        compound.setInteger("collarColor", getCollarColor().getColorValue());
    }

    @Override
 	public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        setScaleFactor(compound.getFloat("scaleFactor"));
        setInterested(compound.getBoolean("isInterested"));
        setAngry(compound.getBoolean("isAngry"));
        setSitting(compound.getBoolean("isSitting"));
        setCollarColor(EnumDyeColor.byMetadata(compound.getInteger("collarColor")));
    }


    // *****************************************************
    // ENCAPSULATION SETTER AND GETTER METHODS
    // Don't forget to send sync packets in setters
    // *****************************************************
        
    @Override
	public void setScaleFactor(float parScaleFactor)
    {
    	dataManager.set(SCALE_FACTOR, Math.abs(parScaleFactor));
    }
    
    @Override
	public float getScaleFactor()
    {
    	return dataManager.get(SCALE_FACTOR);
    }
}