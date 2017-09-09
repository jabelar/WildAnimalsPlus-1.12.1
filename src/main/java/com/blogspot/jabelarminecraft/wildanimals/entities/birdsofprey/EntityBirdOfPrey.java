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

package com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey;

import java.util.UUID;

import com.blogspot.jabelarminecraft.wildanimals.advancements.criteria.Triggers;
import com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey.AIStates;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey.ProcessStateBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey.UpdateStateBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.entities.serpents.EntitySerpent;
import com.blogspot.jabelarminecraft.wildanimals.events.BirdTameEvent;
import com.google.common.base.Optional;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityRabbit;
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
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// TODO: Auto-generated Javadoc
public class EntityBirdOfPrey extends EntityFlying implements IModEntity
{
    protected static final DataParameter<Float> SCALE_FACTOR = EntityDataManager.<Float>createKey(EntityBirdOfPrey.class, DataSerializers.FLOAT);
    protected static final DataParameter<Integer> STATE = EntityDataManager.<Integer>createKey(EntityBirdOfPrey.class, DataSerializers.VARINT);
    protected static final DataParameter<Boolean> SOAR_CLOCKWISE = EntityDataManager.<Boolean>createKey(EntityBirdOfPrey.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Float> SOAR_HEIGHT = EntityDataManager.<Float>createKey(EntityBirdOfPrey.class, DataSerializers.FLOAT);
    protected static final DataParameter<BlockPos> ANCHOR_POS = EntityDataManager.<BlockPos>createKey(EntityBirdOfPrey.class, DataSerializers.BLOCK_POS);
    protected static final DataParameter<Optional<UUID>> OWNER_UUID = EntityDataManager.<Optional<UUID>>createKey(EntityBirdOfPrey.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    protected static final DataParameter<Integer> LEG_BAND_COLOR = EntityDataManager.<Integer>createKey(EntityBirdOfPrey.class, DataSerializers.VARINT);

    public ProcessStateBirdOfPrey aiProcessState;
    public UpdateStateBirdOfPrey aiUpdateState;
    
    // use fields for sounds to allow easy changes in child classes
    protected SoundEvent soundHurt = new SoundEvent(new ResourceLocation("wildanimals:mob.birdsofprey.death"));
    protected SoundEvent soundDeath = new SoundEvent(new ResourceLocation("wildanimals:mob.birdsofprey.death"));
    protected SoundEvent soundCall = new SoundEvent(new ResourceLocation("wildanimals:mob.birdsofprey.cry"));
    protected SoundEvent soundFlapping = new SoundEvent(new ResourceLocation("wildanimals:mob.birdsofprey.flapping"));
    
    // to ensure that multiple entities don't get synced
    // create a random factor per entity
    protected int randFactor;
    
    @SuppressWarnings("rawtypes")
	private Class[] preyArray = new Class[] {EntityChicken.class, EntityBat.class, EntitySerpent.class, EntityRabbit.class, EntityParrot.class};

    private final double TAMED_HEALTH = 30.0D;
    
    /**
     * Instantiates a new entity bird of prey.
     *
     * @param parWorld the par world
     */
    public EntityBirdOfPrey(World parWorld)
    {
        super(parWorld);
        
        // DEBUG
        System.out.println("EntityBirdOfPrey constructor(), "+"on Client="
                +parWorld.isRemote+", EntityID = "+getEntityId()+", ModEntityID = "+entityUniqueID);

        randFactor = rand.nextInt(10);
        // DEBUG
        System.out.println("randFactor = "+randFactor);

        setSize(1.0F, 1.0F);
    }
        
    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLiving#entityInit()
     */
    @Override
    public void entityInit()
    {
    	super.entityInit();
        dataManager.register(SCALE_FACTOR, 1.0F);
        dataManager.register(STATE, AIStates.STATE_TAKING_OFF);
        dataManager.register(SOAR_CLOCKWISE, world.rand.nextBoolean());
        dataManager.register(SOAR_HEIGHT, (float)(126-randFactor));
        dataManager.register(ANCHOR_POS, new BlockPos(posX, posY, posZ));
        dataManager.register(OWNER_UUID, Optional.absent());
        dataManager.register(LEG_BAND_COLOR, Integer.valueOf(EnumDyeColor.RED.getDyeDamage()));       
    }
    
    /* (non-Javadoc)
     * @see com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity#clearAITasks()
     */
    @Override
    public void clearAITasks()
    {
        tasks.taskEntries.clear();
        targetTasks.taskEntries.clear();
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLiving#initEntityAI()
     */
    @Override
    public void initEntityAI() 
    {
    	clearAITasks(); // clear any tasks assigned in super classes
        aiProcessState = new ProcessStateBirdOfPrey(this);
        aiUpdateState = new UpdateStateBirdOfPrey(this);
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLiving#applyEntityAttributes()
     */
    // you don't have to call this as it is called automatically during entityLiving subclass creation
    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        // need to register any additional attributes
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
    }

	/**
	 * This checks whether state should change.
	 */
    @Override
    protected void updateAITasks()
    {
        super.updateAITasks();
        
        aiProcessState.updateAITick();
        aiUpdateState.updateAIState();
        
    }
    
    /* (non-Javadoc)
     * @see net.minecraft.entity.Entity#isInRangeToRenderDist(double)
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double parDistance)
    {
        return true; // need to see them even when far away or high above
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     *
     * @return true, if successful
     */
    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     *
     * @param parDistance the par distance
     * @param parDamageMultiplier the par damage multiplier
     */
    @Override
	public void fall(float parDistance, float parDamageMultiplier) 
    {
        // do nothing since bird cannot fall
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on the ground to update the fall distance
     * and deal fall damage if landing on the ground.  Args: distanceFallenThisTick, onGround
     *
     * @param y the y
     * @param onGroundIn the on ground in
     * @param state the state
     * @param pos the pos
     */
    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
    {
    	// do nothing since birds cannot fall
    }

    /**
     * Return whether this entity should NOT trigger a pressure plate or a tripwire.
     *
     * @return true, if successful
     */
    @Override
    public boolean doesEntityNotTriggerPressurePlate()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLiving#canBeLeashedTo(net.minecraft.entity.player.EntityPlayer)
     */
    @Override
    public boolean canBeLeashedTo(EntityPlayer parPlayer)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLiving#canAttackClass(java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
	@Override
    public boolean canAttackClass(Class parClass)
    {
        return true;
    }
    
    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLiving#getDropItem()
     */
    @Override
    public Item getDropItem()
    {
        return Items.FEATHER;
    }
    
    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLiving#dropFewItems(boolean, int)
     */
    @Override
    protected void dropFewItems(boolean parRecentlyHitByPlayer, int parLootLevel)
    {
        dropItem(Items.FEATHER, parLootLevel+1);
        return;
    }
    
    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLiving#isNoDespawnRequired()
     */
    @Override
    public boolean isNoDespawnRequired()
    {
        return isTamed();
    }
    
    /**
     * Sets the active target the Task system uses for tracking.
     *
     * @param theTargetEntity the new attack target
     */
    @Override
    public void setAttackTarget(EntityLivingBase theTargetEntity)
    {
        // DEBUG
        System.out.println("Setting attack target to = "+theTargetEntity);
        super.setAttackTarget(theTargetEntity);
    }


    /**
     * Returns the sound this mob makes while it's alive.
     *
     * @return the ambient sound
     */
    @Override
    protected SoundEvent getAmbientSound()
    {
        if (getState() == AIStates.STATE_TAKING_OFF || getState() == AIStates.STATE_TRAVELLING)
        {
            return soundFlapping;
        }
        else
        {
            return soundCall;
        }
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

            if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
            {
                amount = (amount + 1.0F) / 2.0F;
            }
            
            // DEBUG
            System.out.println("Bird of prey has been attacked by "+source.getImmediateSource()+" with source = "+source.getTrueSource()+" and revenge target set to "+getRevengeTarget()+" for entity id = "+getEntityId());

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
        System.out.println("EntityBirdOfPrey interact()");
        
        if (parHand == EnumHand.OFF_HAND)
        {
        	return super.processInteract(parPlayer, parHand);
        }
 
        ItemStack itemStackInHand = parPlayer.getHeldItem(parHand);
        
        if (!itemStackInHand.isEmpty()) // something in hand
        {
            // DEBUG
            System.out.println("Interacting with something in hand = "+itemStackInHand);

            if (isTamed())
            {
                // DEBUG
                System.out.println("Intereacting with already tamed");
                
            	
                if (itemStackInHand.getItem() instanceof ItemFood)
                {                	
                	// DEBUG
                	System.out.println("Interacting with food");         	

                    ItemFood itemfood = (ItemFood)itemStackInHand.getItem();

                    if (itemfood == Items.FISH && getHealth() < getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue());
                    {
        	            if (!parPlayer.capabilities.isCreativeMode)
        	            {
        	            	decrementStackInHand(parPlayer, itemStackInHand);
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

                    if (dyeColor != getLegBandColor())
                    {
                        setLegBandColor(dyeColor);
                        
        	            if (!parPlayer.capabilities.isCreativeMode)
        	            {
        	            	decrementStackInHand(parPlayer, itemStackInHand);
        	            }
 
                        return true;
                    }
                }
            }
            else // not tamed
            {
                // DEBUG
                System.out.println("Interacting with untamed");

                // check if raw salmon
                // DEBUG
                System.out.println("Is taming food = "+isTamingFood(itemStackInHand));
                
                if (isTamingFood(itemStackInHand))
                {
                    // DEBUG
                    System.out.println("Trying taming food");
                    
    	            if (!parPlayer.capabilities.isCreativeMode)
    	            {
    	            	decrementStackInHand(parPlayer, itemStackInHand);
    	            }
    	
    	            // Try taming
                    if (!world.isRemote)
                    {
	                    if (rand.nextInt(3) == 0) // && onAnimalTame(this, parPlayer))
	                    {                
	                    	// DEBUG
	                        System.out.println("Taming successful");

	                        setTamedBy(parPlayer);
		                    navigator.clearPathEntity();
	                        setAttackTarget((EntityLivingBase)null);
		                    getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(TAMED_HEALTH);
		                    playTameEffect(true);
		                    world.setEntityState(this, (byte)7);
     
	                        // DEBUG
	                        System.out.println("It likes the raw salmon");
	                    }
	                    else
	                    {
	                    	// DEBUG
	                        System.out.println("Taming failed");

	                        playTameEffect(false);
		                    world.setEntityState(this, (byte)6);
	                    }
                    }                   
                }
            }
        }
        else // nothing in hand
        {
        	// do nothing
        	// DEBUG
            System.out.println("Interacting with nothing in hand");


        }

        return super.processInteract(parPlayer, parHand);
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

    /**
     * On animal tame.
     *
     * @param animal the animal
     * @param tamer the tamer
     * @return true, if successful
     */
    public static boolean onAnimalTame(EntityBirdOfPrey animal, EntityPlayer tamer)
    {
    	boolean success = MinecraftForge.EVENT_BUS.post(new BirdTameEvent(animal, tamer));
    	
    	// DEBUG
    	System.out.println("Posting tame event to bus success ="+success);
    	
        return success;
    }
    
    /**
     * Checks if is taming food.
     *
     * @param parItemStack the par item stack
     * @return true, if is taming food
     */
    public boolean isTamingFood(ItemStack parItemStack)
    {
        // check for raw salmon
        return (parItemStack.getItem() == Items.FISH && parItemStack.getMetadata() == 1);
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

    /**
     * Determines if an entity can be despawned, used on idle far away entities.
     *
     * @return true, if successful
     */
    @Override
    protected boolean canDespawn()
    {
        return false;
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
     * @see net.minecraft.entity.Entity#setDead()
     */
    @Override
    public void setDead()
    {
        // DEBUG
        System.out.println("Setting dead");
        
        super.setDead();
    }
    
    /* (non-Javadoc)
     * @see net.minecraft.entity.Entity#getTeam()
     */
    @Override
    public Team getTeam()
    {
        if (getOwner() != null)
        {
            EntityLivingBase entityLivingBase = getOwner();

            if (entityLivingBase != null)
            {
                return entityLivingBase.getTeam();
            }
        }

        return super.getTeam();
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.Entity#isOnSameTeam(net.minecraft.entity.Entity)
     */
    @Override
    public boolean isOnSameTeam(Entity parEntity)
    {
        if (getOwner() != null)
        {
            EntityLivingBase entityOwner = getOwner();

            if (parEntity == entityOwner)
            {
                return true;
            }

            if (entityOwner != null)
            {
                return entityOwner.isOnSameTeam(parEntity);
            }
        }

        return super.isOnSameTeam(parEntity);
    }
    
    /**
     * Play the taming effect, will either be hearts or smoke depending on status.
     *
     * @param shouldSpawnHearts the should spawn hearts
     */
    protected void playTameEffect(boolean shouldSpawnHearts)
    {
        if (world.isRemote)
        {
            return;
        }
        
        EnumParticleTypes particleType = EnumParticleTypes.HEART;

        if (!shouldSpawnHearts)
        {
            particleType = EnumParticleTypes.SMOKE_NORMAL;
        }

        for (int i = 0; i < 7; ++i)
        {
            double d0 = rand.nextGaussian() * 0.02D;
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;
            world.spawnParticle(particleType, posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, d0, d1, d2);
        }
    }
    
    /**
     * Checks if is nocturnal.
     *
     * @return true, if is nocturnal
     */
    /*
     * Indicates whether the bird is more active during night time.
     * Affects tendency to perch and take off from perch
     */
    public boolean isNocturnal()
    {
        return false;
    }
    
    /**
     * Checks if is owl type.
     *
     * @return true, if is owl type
     */
    /*
     * Indicates proportions of the head
     */
    public boolean isOwlType()
    {
        return false;
    }
    
    /**
     * Checks if is tamed.
     *
     * @return true, if is tamed
     */
    public boolean isTamed()
    {
        return (getOwner() != null);
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
     * Sets the owner id.
     *
     * @param parUUID the new owner id
     */
    public void setOwnerId(UUID parUUID)
    {
        // DEBUG
        System.out.println("Setting new owner with UUID ="+parUUID);
        dataManager.set(OWNER_UUID, Optional.fromNullable(parUUID));
    }
    
    /**
     * Gets the owner id.
     *
     * @return the owner id
     */
    public UUID getOwnerId()
    {
        return dataManager.get(OWNER_UUID).orNull();
    }

    /**
     * Sets the state.
     *
     * @param parState the new state
     */
    public void setState(int parState)
    {
//        // DEBUG
//        System.out.println("EntityBirdOfPrey setState() state changed to "+parState);

        dataManager.set(STATE, parState);
    }

    /**
     * Gets the state.
     *
     * @return the state
     */
    public int getState() 
    {
        return dataManager.get(STATE);
    } 

    /**
     * Sets the anchor.
     *
     * @param parPos the new anchor
     */
    public void setAnchor(BlockPos parPos)
    {
        dataManager.set(ANCHOR_POS, parPos);
    }

    /**
     * Gets the anchor.
     *
     * @return the anchor
     */
    public BlockPos getAnchor() 
    {
        return dataManager.get(ANCHOR_POS);
    } 

    /**
     * Gets the owner.
     *
     * @return the owner
     */
    public EntityPlayer getOwner()
    {
        try
        {
            UUID uuid = getOwnerId();
            return uuid == null ? null : world.getPlayerEntityByUUID(uuid);
        }
        catch (IllegalArgumentException parIAE)
        {
            return null;
        }
    }
    
    /**
     * Sets the tamed by.
     *
     * @param parNewOwner the par new owner
     * @return true, if successful
     */
    public boolean setTamedBy(EntityLivingBase parNewOwner)
    {
        if (getOwner() != null)
        {
            // DEBUG
            System.out.println("There is already an owner");
            
            return false;
        }
        else if (parNewOwner == null)
        {
            // DEBUG
            System.out.println("Setting owner to null");
            setOwnerId(null);
            return false;
        }
        else
        {
            setAttackTarget(null);
            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(TAMED_HEALTH);
            setOwnerId(parNewOwner.getUniqueID());

            if (parNewOwner instanceof EntityPlayerMP)
            {
                Triggers.TAME_BIRD.trigger((EntityPlayerMP)parNewOwner, this);
            }
            return true;
        }
    }
    
    /**
     * Checks if is owner.
     *
     * @param parEntity the par entity
     * @return true, if is owner
     */
    public boolean isOwner(EntityLivingBase parEntity)
    {
        return parEntity == getOwner();
    }
    
    /**
     * Sets the soar clockwise.
     *
     * @param parClockwise the new soar clockwise
     */
    public void setSoarClockwise(boolean parClockwise)
    {
        dataManager.set(SOAR_CLOCKWISE, parClockwise);
    }
    
    /**
     * Gets the soar clockwise.
     *
     * @return the soar clockwise
     */
    public boolean getSoarClockwise()
    {
        return dataManager.get(SOAR_CLOCKWISE);
    }
    
    /**
     * Sets the soar height.
     *
     * @param parHeight the new soar height
     */
    public void setSoarHeight(float parHeight)
    {
        dataManager.set(SOAR_HEIGHT, parHeight);
    }
    
    /**
     * Gets the soar height.
     *
     * @return the soar height
     */
    public float getSoarHeight()
    {
        return dataManager.get(SOAR_HEIGHT);
    }
    
    /**
     * Gets the rand factor.
     *
     * @return the rand factor
     */
    public int getRandFactor()
    {
        return randFactor;
    }

    /**
     * Gets the prey array.
     *
     * @return the prey array
     */
    @SuppressWarnings("rawtypes")
	public Class[] getPreyArray()
    {
        return preyArray;
    }

    /**
     * Sets the prey array.
     *
     * @param parPreyArray the new prey array
     */
    @SuppressWarnings("rawtypes")
	public void setPreyArray(Class[] parPreyArray)
    {
        preyArray = parPreyArray;
    }

    /**
     * Gets the leg band color.
     *
     * @return the leg band color
     */
    public EnumDyeColor getLegBandColor()
    {
        return EnumDyeColor.byMetadata(dataManager.get(LEG_BAND_COLOR));
    }

    /**
     * Sets the leg band color.
     *
     * @param parLegBandColor the new leg band color
     */
    public void setLegBandColor(EnumDyeColor parLegBandColor)
    {
        dataManager.set(LEG_BAND_COLOR, parLegBandColor.getMetadata());
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLiving#writeEntityToNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
    	super.writeEntityToNBT(compound);
        compound.setFloat("scaleFactor", getScaleFactor());
        compound.setInteger("state", getState());
        compound.setBoolean("soaringClockwise", getSoarClockwise());
        compound.setFloat("soarHeight", getSoarHeight());
        compound.setDouble("anchorX", getAnchor().getX());
        compound.setDouble("anchorY", getAnchor().getY());
        compound.setDouble("anchorZ", getAnchor().getZ());
        if (this.getOwnerId() == null)
        {
            compound.setString("OwnerUUID", "");
        }
        else
        {
            compound.setString("OwnerUUID", this.getOwnerId().toString());
        }
        compound.setInteger("legBandColor", getLegBandColor().getColorValue());
    }

    /* (non-Javadoc)
     * @see net.minecraft.entity.EntityLiving#readEntityFromNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
 	public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        setScaleFactor(compound.getFloat("scaleFactor"));
        setState(compound.getInteger("state"));
        setSoarClockwise(compound.getBoolean("soaringClockwise"));
        setSoarHeight(compound.getFloat("soarHeight"));
        setAnchor(new BlockPos(
        		compound.getDouble("anchorX"), 
        		compound.getDouble("anchorY"), 
        		compound.getDouble("anchorZ")
        		));
        String s = compound.getString("OwnerUUID");
        if (!s.isEmpty())
        {
            try
            {
                setOwnerId(UUID.fromString(s));
            }
            catch (Throwable var4)
            {
                setOwnerId(null);
            }
        }
        else
        {
        	setOwnerId(null);
        }
        setLegBandColor(EnumDyeColor.byMetadata(compound.getInteger("legBandColor")));
    }
}