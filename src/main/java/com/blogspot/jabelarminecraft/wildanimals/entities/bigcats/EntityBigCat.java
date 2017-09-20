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

package com.blogspot.jabelarminecraft.wildanimals.entities.bigcats;

import java.util.UUID;

import com.blogspot.jabelarminecraft.wildanimals.advancements.criteria.Triggers;
import com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.bigcat.EntityAIBegBigCat;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.bigcat.EntityAIGuardOwner;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.bigcat.EntityAISitBigCat;
import com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals.EntityHerdAnimal;
import com.blogspot.jabelarminecraft.wildanimals.registries.ModSounds;
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
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// TODO: Auto-generated Javadoc
public class EntityBigCat extends EntityTameable implements IModEntity
{
    protected static final DataParameter<Float> SCALE_FACTOR = EntityDataManager.<Float>createKey(EntityBigCat.class, DataSerializers.FLOAT);
    protected static final DataParameter<Boolean> IS_INTERESTED = EntityDataManager.<Boolean>createKey(EntityBigCat.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean>  IS_ANGRY = EntityDataManager.<Boolean>createKey(EntityBigCat.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.<Integer>createKey(EntityBigCat.class, DataSerializers.VARINT);
    
//    protected static SoundEvent soundBigCatAmbientGrowl = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.growl"));
//    protected static SoundEvent soundBigCatAmbientWhine = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.whine"));
//    protected static SoundEvent soundBigCatAmbientPanting = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.panting"));
//    protected static SoundEvent soundBigCatAmbientBark = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.bark"));
//    protected static SoundEvent soundBigCatHurt = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.hurt"));
//    protected static SoundEvent soundBigCatDeath = new SoundEvent(new ResourceLocation("wildanimals:mob.bigCat.death"));
//    protected static SoundEvent soundBigCatShake = new SoundEvent(new ResourceLocation( "wildanimals:mob.bigCat.shake"));

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
    
    protected final AttributeModifier TAMED_MODIFIER = new AttributeModifier("Tamed health and attack boost", 2.0D, 0);
    
    /**
     * This time increases while bigCat is shaking and emitting water particles.
     */
    protected float timeBigCatIsShaking;
    protected float prevTimeBigCatIsShaking;
 
    /**
     * Need a toggle field to control sitting toggling because otherwise
     * both hands may be processed on a right-click
     */
    protected boolean preventSitToggling = false;
	
    /**
     * Instantiates a new entity big cat.
     *
     * @param parWorld the par world
     */
    public EntityBigCat(World parWorld)
    {
        super(parWorld);
        
        // DEBUG
        System.out.println("EntityBigCat constructor(), "+"on Client="+parWorld.isRemote);

        setSize(1.0F, 1.33F);
 	}
    
    /* (non-Javadoc)
     * @see net.minecraft.entity.passive.EntityTameable#entityInit()
     */
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

    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLiving#applyEntityAttributes()
     */
    // you don't have to call this as it is called automatically during entityLiving subclass creation
    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes(); // registers the common attributes
        
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);

        // need to register any additional attributes
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }
	
	/* (non-Javadoc)
	 * @see net.minecraft.entity.EntityLiving#initEntityAI()
	 */
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
        tasks.addTask(5, new EntityAIFollowOwner(this, this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue(), 6.0F, 3.0F));
        tasks.addTask(6, new EntityAIMate(this, 1.0D));
        tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
        tasks.addTask(8, new EntityAIBegBigCat(this, 8.0F)); 
        tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(10, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIGuardOwner(this));
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
 
    /**
     * Sets the active target the Task system uses for tracking.
     *
     * @param parEntityLivingBase the new attack target
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
     *
     * @return the ambient sound
     */
    @Override
	protected SoundEvent getAmbientSound()
    {
        return isAngry() ? ModSounds.soundAmbientGrowlBigCat : (rand.nextInt(3) == 0 ? (isTamed() && getHealth() < 10.0F ? ModSounds.soundAmbientWhineBigCat : ModSounds.soundAmbientPantingBigCat) : ModSounds.soundAmbientBarkBigCat);
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
        return ModSounds.soundHurtBigCat; // It uses sounds.json file to randomize and adds 1, 2 or 3 and .ogg
    }

    /**
     * Returns the sound this mob makes on death.
     *
     * @return the death sound
     */
    @Override
	protected SoundEvent getDeathSound()
    {
        return ModSounds.soundDeathBigCat;
    }

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
        
        preventSitToggling = false;
 
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
                playSound(ModSounds.soundShakeBigCat, getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
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

    /**
     * Gets the big cat shaking.
     *
     * @return the big cat shaking
     */
    @SideOnly(Side.CLIENT)
    public boolean getBigCatShaking()
    {
        return isShaking;
    }

    /**
     * Used when calculating the amount of shading to apply while the bigCat is shaking.
     *
     * @param parShakeRate the par shake rate
     * @return the shading while shaking
     */
    @SideOnly(Side.CLIENT)
    public float getShadingWhileShaking(float parShakeRate)
    {
        return 0.75F + (prevTimeBigCatIsShaking + (timeBigCatIsShaking - prevTimeBigCatIsShaking) * parShakeRate) / 2.0F * 0.25F;
    }

    /**
     * Gets the shake angle.
     *
     * @param parShakeRate the par shake rate
     * @param par2 the par 2
     * @return the shake angle
     */
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

    /* (non-Javadoc)
     * @see net.minecraft.entity.Entity#getEyeHeight()
     */
    @Override
	public float getEyeHeight()
    {
        return height * 0.8F;
    }

    /**
     * Gets the interested angle.
     *
     * @param parRateOfAngleChange the par rate of angle change
     * @return the interested angle
     */
    @SideOnly(Side.CLIENT)
    public float getInterestedAngle(float parRateOfAngleChange)
    {
        return (prevHeadAngle + (targetHeadAngle - prevHeadAngle) * parRateOfAngleChange) * 0.15F * (float)Math.PI;
    }

    /**
     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
     * use in wolves.
     *
     * @return the vertical face speed
     */
    @Override
	public int getVerticalFaceSpeed()
    {
        return isSitting() ? 20 : super.getVerticalFaceSpeed();
    }

    /**
     * Called when the entity is attacked.
     *
     * @param source the source
     * @param amount the amount
     * @return true, if successful
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

            setSitting(false);
            
            if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
            {
                amount = (amount + 1.0F) / 2.0F;
            }

            return super.attackEntityFrom(source, amount);
        }
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

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     *
     * @param parPlayer the par player
     * @param parHand the par hand
     * @return true, if successful
     */
    @Override
	public boolean processInteract(EntityPlayer parPlayer, EnumHand parHand)
    {
        // DEBUG
        System.out.println("EntityBigCat interact() for hand = "+parHand);
         
    	boolean foundUsefulItem = false;
        ItemStack theStack = parPlayer.getHeldItem(parHand);
        
        foundUsefulItem = isTamed() ? proceesInteractTamed(parPlayer, theStack) : processInteractUntamed(parPlayer, theStack);
        
        if (foundUsefulItem)
        {
        	// DEBUG
        	System.out.println("Found useful item");
        	
        	consumeItemFromStack(parPlayer, theStack);
        	return true;
        }
        else
        {
        	// May want to do something even if nothing "useful" found
        	// such as toggling sitting on tamed animal
        	
        	// consider not useful item only if not holding items known useful in super methods
        	if (!(theStack.getItem() == Items.SPAWN_EGG) && !(isBreedingItem(theStack)))
        	{
            	// DEBUG
            	System.out.println("Did not find useful item so continuing with super method");
            	
            	return isTamed() ? processTamedNoUsefulItemFound(parPlayer) : processUntamedNoUsefulItemFound(parPlayer);
        	}
        	else
        	{
            	
            	// DEBUG
            	System.out.println("Item is useful in super method");
            	
                preventSitToggling = true;
        		return super.processInteract(parPlayer, parHand);
        	}
        }
    }
    
    /**
     * Procees interact tamed.
     *
     * @param parPlayer the par player
     * @param parStack the par stack
     * @return true, if successful
     */
    protected boolean proceesInteractTamed(EntityPlayer parPlayer, ItemStack parStack)
    {
    	// DEBUG
    	System.out.println("Interacting with tamed big cat");

    	return parStack.isEmpty() ? processInteractTamedHandEmpty(parPlayer) : processInteractTamedHandFull(parPlayer, parStack);
    }
    
    /**
     * Process interact tamed hand full.
     *
     * @param parPlayer the par player
     * @param parStack the par stack
     * @return true, if successful
     */
    protected boolean processInteractTamedHandFull(EntityPlayer parPlayer, ItemStack parStack)
    {
    	// DEBUG
    	System.out.println("Interacting with something in hand = "+parStack+" item = "+parStack.getItem());
    	
        if (parStack.getItem() instanceof ItemFood)
        {      
        	return processInteractFood(parPlayer, parStack);
        }
            

    	if (parStack.getItem() == Items.DYE)
        {
    		return processInteractDye(parPlayer, parStack);
        }
        
        return false;
    }
    
    /**
     * Process interact food.
     *
     * @param parPlayer the par player
     * @param parStack the par stack
     * @return true, if successful
     */
    protected boolean processInteractFood(EntityPlayer parPlayer, ItemStack parStack)
    {
    	// DEBUG
    	System.out.println("Interacting with food, entity health = "+getHealth()+" max health = "+getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());         	

        ItemFood itemFood = (ItemFood)parStack.getItem();

        if (isHealingFood(itemFood) && getHealth() < getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue())
        {
        	return processInteractHealItem(parPlayer, parStack);
        }
        else
        {
        	// DEBUG
        	System.out.println("Not healing");
        	
        	return false;
        }
    }
    
    /**
     * Process interact dye.
     *
     * @param parPlayer the par player
     * @param parStack the par stack
     * @return true, if successful
     */
    protected boolean processInteractDye(EntityPlayer parPlayer, ItemStack parStack)
    {
		// DEBUG
		System.out.println("Interacting with dye");
		
        preventSitToggling = true;
        EnumDyeColor dyeColor = EnumDyeColor.byDyeDamage(parStack.getMetadata());

        return (dyeColor != getCollarColor()) ? setCollarColor(dyeColor) : false;
    }
    
    /**
     * Process interact heal item.
     *
     * @param parPlayer the par player
     * @param parStack the par stack
     * @return true, if successful
     */
    protected boolean processInteractHealItem(EntityPlayer parPlayer, ItemStack parStack)
    {
    	// DEBUG
    	System.out.println("Healing");

        preventSitToggling = true;
        heal(((ItemFood)(parStack.getItem())).getHealAmount(parStack));
        
        return true;
    }
        
    /**
     * Process interact tamed hand empty.
     *
     * @param parPlayer the par player
     * @return true, if successful
     */
    protected boolean processInteractTamedHandEmpty(EntityPlayer parPlayer)
    {
    	// DEBUG
    	System.out.println("Interacting with nothing in hand");
       	System.out.println("Owner is player "+isOwner(parPlayer));
       	
       	return false;
    }
    
    /**
     * Process tamed no useful item found.
     *
     * @param parPlayer the par player
     * @return true, if successful
     */
    protected boolean processTamedNoUsefulItemFound(EntityPlayer parPlayer)
    {    	
    	// DEBUG
    	System.out.println("Interacting with tamed without useful object, sitAlreadyToggledThisTick ="+preventSitToggling);
    	
        // toggle sitting
        if (!preventSitToggling && isOwner(parPlayer) && !world.isRemote)
        {
        	// DEBUG
        	System.out.println("EntityBigCat toggle sitting");
        	
            setSitting(!isSitting());
            isJumping = false;
            navigator.clearPathEntity();
            setAttackTarget((EntityLivingBase)null);
            preventSitToggling = true;
            return true;
        }
        else
        {
        	// DEBUG
        	System.out.println("EntityBigCat toggle sitting didn't work, either on client or wrong owner");
        	return false;
        }
    }
    
    /**
     * Process untamed no useful item found.
     *
     * @param parPlayer the par player
     * @return true, if successful
     */
    protected boolean processUntamedNoUsefulItemFound(EntityPlayer parPlayer)
    {
    	return false;
    }
    
    /**
     * Process interact untamed.
     *
     * @param parPlayer the par player
     * @param parStack the par stack
     * @return true, if successful
     */
    protected boolean processInteractUntamed(EntityPlayer parPlayer, ItemStack parStack)
    {
    	// DEBUG
    	System.out.println("Interacting with untamed big cat");
    	
    	return parStack.isEmpty() ? processInteractUntamedHandEmpty(parPlayer) : processInteractUntamedHandFull(parPlayer, parStack);
    }
    
    /**
     * Process interact untamed hand full.
     *
     * @param parPlayer the par player
     * @param parStack the par stack
     * @return true, if successful
     */
    protected boolean processInteractUntamedHandFull(EntityPlayer parPlayer, ItemStack parStack)
    {
    	// DEBUG
    	System.out.println("Interacting with untamed big cat with something in hand");

        return (isTamingItem(parStack.getItem()) && !isAngry()) ? processInteractTamingItem(parPlayer) : false;
    }
    
    /**
     * Process interact taming item.
     *
     * @param parPlayer the par player
     * @return true, if successful
     */
    protected boolean processInteractTamingItem(EntityPlayer parPlayer)
    {
    	// DEBUG
    	System.out.println("Interacting with taming item");
    	
        preventSitToggling = true;
    	if (!world.isRemote)
        {
            if (rand.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, parPlayer))
            {
                setTamedBy(parPlayer);
                navigator.clearPathEntity();
                setAttackTarget((EntityLivingBase)null);
                setSitting(true);
                playTameEffect(true);
                world.setEntityState(this, (byte)7);
                
                // DEBUG
                System.out.println("Taming successful for owner = "+parPlayer.getCommandSenderEntity());
            }
            else
            {
            	// DEBUG
            	System.out.println("Taming failed");
            	
                playTameEffect(false);
                world.setEntityState(this, (byte)6);
            }
        }
        return true; // return value indicates item used, not success
    }
    
    /**
     * Process interact untamed hand empty.
     *
     * @param parPlayer the par player
     * @return true, if successful
     */
    protected boolean processInteractUntamedHandEmpty(EntityPlayer parPlayer)
    {
    	// DEBUG
    	System.out.println("Interacting with untamed and empty hand");
    	
    	return false;
    }
    
    /**
     * Checks if is healing food.
     *
     * @param parFood the par food
     * @return true, if is healing food
     */
    protected boolean isHealingFood(ItemFood parFood)
    {
    	return parFood == Items.BEEF;
    }
    
    /**
     * Checks if is taming item.
     *
     * @param parItem the par item
     * @return true, if is taming item
     */
    protected boolean isTamingItem(Item parItem)
    {
    	return parItem == Items.BONE;
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type).
     *
     * @param parStack the par stack
     * @return true, if is breeding item
     */
    @Override
	public boolean isBreedingItem(ItemStack parStack)
    {
        return parStack.getItem() == Items.CHICKEN;
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.passive.EntityTameable#setTamedBy(net.minecraft.entity.player.EntityPlayer)
     */
    @Override
	public void setTamedBy(EntityPlayer player)
    {
        this.setTamed(true);
        this.setOwnerId(player.getUniqueID());

        if (player instanceof EntityPlayerMP)
        {
            Triggers.TAME_BIG_CAT.trigger((EntityPlayerMP)player);
        }
    }
    
    /**
     * Sets the tamed.
     *
     * @param parTamed the new tamed
     */
    public void setTamed(Boolean parTamed)
    {
    	super.setTamed(parTamed);
    	
    	if (parTamed)
    	{
    		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(TAMED_MODIFIER);
    		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(TAMED_MODIFIER);	
    	}
    	else
    	{
    		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).removeModifier(TAMED_MODIFIER);
    		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(TAMED_MODIFIER);	
    	}
    	
		setHealth((float) getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
    }
    
    /**
     * Decrement stack in hand.
     *
     * @param parPlayer the par player
     * @param itemStackInHand the item stack in hand
     */
    protected void decrementStackInHand(EntityPlayer parPlayer, ItemStack itemStackInHand)
    {
        itemStackInHand.shrink(1);
        if (!parPlayer.capabilities.isCreativeMode && itemStackInHand.getCount() <= 0)
        {
            parPlayer.inventory.setInventorySlotContents(parPlayer.inventory.currentItem, ItemStack.EMPTY);
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

    /**
	 * Gets the tail rotation.
	 *
	 * @return the tail rotation
	 */
	@SideOnly(Side.CLIENT)
    public float getTailRotation()
    {
        return (float) (isAngry() ? 1.5393804F : (isTamed() ? (0.55F - (getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue() - getHealth()) * 0.02F) * (float)Math.PI : ((float)Math.PI / 5F)));
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     *
     * @return the max spawned in chunk
     */
    @Override
	public int getMaxSpawnedInChunk()
    {
        return 8;
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityAgeable#createChild(net.minecraft.entity.EntityAgeable)
     */
    @Override
	public EntityBigCat createChild(EntityAgeable par1EntityAgeable)
    {
        /*
         * Must @Override in classes extending this using code similar
         * to commented code below
         */
    	
//        // DEBUG
//        System.out.println("EntityBigCat createChild()");
 
        EntityBigCat entityBigCat = new EntityBigCat(world);
        UUID uuid = getOwnerId(); 

        if (uuid != null)
        {
        	entityBigCat.setOwnerId(uuid);
            entityBigCat.setTamed(true);
        }
    	
    	  return entityBigCat;
    }
    
    /**
     * Returns true if the mob is currently able to mate with the specified mob.
     *
     * @param parEntityAnimal the par entity animal
     * @return true, if successful
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
            System.out.println(this.toString()+" is considering mate = "+entitybigCat);
            return (entitybigCat.isSitting() ? false : isInLove() && entitybigCat.isInLove());
        }
    }
    
    /* (non-Javadoc)
     * @see net.minecraft.entity.passive.EntityAnimal#setInLove(net.minecraft.entity.player.EntityPlayer)
     */
    @Override
	public void setInLove(EntityPlayer parPlayer)
    {
    	// DEBUG
    	System.out.println("Setting in love with growing age = "+getGrowingAge()+" and already in love = "+isInLove());
    	
    	super.setInLove(parPlayer);
    }
    
    /* (non-Javadoc)
     * @see net.minecraft.entity.passive.EntityAnimal#resetInLove()
     */
    @Override
	public void resetInLove()
    {
    	// DEBUG
    	System.out.println("Resetting in love");
    	
    	super.resetInLove();
    }
 
    /* (non-Javadoc)
     * @see net.minecraft.entity.passive.EntityTameable#writeEntityToNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
    	super.writeEntityToNBT(compound);
        compound.setFloat("scaleFactor", getScaleFactor());
        compound.setBoolean("isInterested", isInterested());
        compound.setBoolean("isAngry", isAngry());
        compound.setBoolean("isSitting", isSitting());
        compound.setInteger("collarColor", getCollarColor().getDyeDamage());
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.passive.EntityTameable#readEntityFromNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
 	public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        setScaleFactor(compound.getFloat("scaleFactor"));
        setInterested(compound.getBoolean("isInterested"));
        setAngry(compound.getBoolean("isAngry"));
        setSitting(compound.getBoolean("isSitting"));
        setCollarColor(EnumDyeColor.byDyeDamage(compound.getInteger("collarColor")));
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

    /**
     * Determines whether this bigCat is angry or not.
     *
     * @return true, if is angry
     */
    public boolean isAngry()
    {
        return dataManager.get(IS_ANGRY);
    }

    /**
     * Sets whether this bigCat is angry or not.
     *
     * @param parIsAngry the new angry
     */
    public void setAngry(boolean parIsAngry)
    {
        dataManager.set(IS_ANGRY, parIsAngry);
    }

    /**
     * Return this bigCat's collar color.
     *
     * @return the collar color
     */
    public EnumDyeColor getCollarColor()
    {	
    	if (dataManager.get(COLLAR_COLOR) == null)
    	{
    		// DEBUG
    		System.out.println("getCollorColor got null from dataManager");
    		return EnumDyeColor.RED;
    	}
    	else
    	{
    		return EnumDyeColor.byMetadata(dataManager.get(COLLAR_COLOR));
    	}
    }

    /**
     * Set this bigCat's collar color.
     *
     * @param parCollarColor the new collar color
     * @return true, if successful
     */
    public boolean setCollarColor(EnumDyeColor parCollarColor)
    {
        dataManager.set(COLLAR_COLOR, parCollarColor.getMetadata());
        return true;
    }

    /**
     * Sets the interested.
     *
     * @param parIsInterested the new interested
     */
    public void setInterested(boolean parIsInterested)
    {
        dataManager.set(IS_INTERESTED, parIsInterested);
    }

    /**
     * Checks if is interested.
     *
     * @return true, if is interested
     */
    public boolean isInterested()
    {
        return dataManager.get(IS_INTERESTED);
    }
}