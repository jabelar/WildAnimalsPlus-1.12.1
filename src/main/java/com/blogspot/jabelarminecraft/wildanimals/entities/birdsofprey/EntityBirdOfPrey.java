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

package com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey;

import java.util.UUID;

import com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey.AIStates;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey.ProcessStateBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey.UpdateStateBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.entities.serpents.EntitySerpent;

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
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBirdOfPrey extends EntityFlying implements IModEntity
{
    protected static final DataParameter<Float> SCALE_FACTOR = EntityDataManager.<Float>createKey(Entity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Integer> STATE = EntityDataManager.<Integer>createKey(Entity.class, DataSerializers.VARINT);
    protected static final DataParameter<Boolean> SOAR_CLOCKWISE = EntityDataManager.<Boolean>createKey(Entity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Float> SOAR_HEIGHT = EntityDataManager.<Float>createKey(Entity.class, DataSerializers.FLOAT);
    protected static final DataParameter<BlockPos> ANCHOR_POS = EntityDataManager.<BlockPos>createKey(Entity.class, DataSerializers.BLOCK_POS);
    protected static final DataParameter<String> OWNER_UUID = EntityDataManager.<String>createKey(Entity.class, DataSerializers.STRING);
    protected static final DataParameter<Integer> LEG_BAND_COLOR = EntityDataManager.<Integer>createKey(Entity.class, DataSerializers.VARINT);

    public ProcessStateBirdOfPrey aiHelper;
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
    
    public EntityBirdOfPrey(World parWorld)
    {
        super(parWorld);
        
        // DEBUG
        System.out.println("EntityBirdOfPrey constructor(), "+"on Client="
                +parWorld.isRemote+", EntityID = "+getEntityId()+", ModEntityID = "+entityUniqueID);

        randFactor = rand.nextInt(10);
        // DEBUG
        System.out.println("randFactor = "+randFactor);

        dataManager.register(SCALE_FACTOR, 1.0F);
        dataManager.register(STATE, AIStates.STATE_TAKING_OFF);
        dataManager.register(SOAR_CLOCKWISE, world.rand.nextBoolean());
        dataManager.register(SOAR_HEIGHT, (float)(126-randFactor));
        dataManager.register(ANCHOR_POS, new BlockPos(posX, posY, posZ));
        dataManager.register(OWNER_UUID, "");
        dataManager.register(LEG_BAND_COLOR, 0);
        
        setSize(1.0F, 1.0F);
        setupAI();
    }
        
    @Override
    public void entityInit()
    {
    }
    
    // use clear tasks then build up their custom ai task list specifically
    @Override
    public void clearAITasks()
    {
        tasks.taskEntries.clear();
        targetTasks.taskEntries.clear();
    }

    @Override
    public void setupAI() 
    {
    	clearAITasks(); // clear any tasks assigned in super classes
        aiHelper = new ProcessStateBirdOfPrey(this);
        aiUpdateState = new UpdateStateBirdOfPrey(this);
    }

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
     * This process the current state.
     */
    protected void syncOwner()
    { 
        if (ticksExisted == 10)
        {
            // note that the setTamed also forces a full NBT sync to client
            String ownerUUIDString = dataManager.get(OWNER_UUID);
            if (ownerUUIDString != "")
            {
                setOwnerUUIDString(ownerUUIDString);
            }
            else
            {
                setOwnerUUIDString("");
            }
        }
        
        aiHelper.updateAITick();
    }

	/**
     * This checks whether state should change
     */
    @Override
    protected void updateAITasks()
    {
        super.updateAITasks();
        
        aiUpdateState.updateAIState();
        
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double parDistance)
    {
        return true; // need to see them even when far away or high above
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    @Override
	public void fall(float parDistance, float parDamageMultiplier) 
    {
        // do nothing since bird cannot fall
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on the ground to update the fall distance
     * and deal fall damage if landing on the ground.  Args: distanceFallenThisTick, onGround
     */
    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
    {
    	// do nothing since birds cannot fall
    }

    /**
     * Return whether this entity should NOT trigger a pressure plate or a tripwire.
     */
    @Override
    public boolean doesEntityNotTriggerPressurePlate()
    {
        return true;
    }

    @Override
    public boolean canBeLeashedTo(EntityPlayer parPlayer)
    {
        return false;
    }

    @SuppressWarnings("rawtypes")
	@Override
    public boolean canAttackClass(Class parClass)
    {
        return true;
    }
    
    @Override
    public Item getDropItem()
    {
        return Items.FEATHER;
    }
    
    @Override
    protected void dropFewItems(boolean parRecentlyHitByPlayer, int parLootLevel)
    {
        dropItem(Items.FEATHER, parLootLevel+1);
        return;
    }
    
    @Override
    public boolean isNoDespawnRequired()
    {
        return isTamed();
    }
    
    /**
     * Sets the active target the Task system uses for tracking
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
     * Returns the volume for the sounds this mob makes.
     */
    @Override
    protected float getSoundVolume()
    {
        return 0.3F;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        
        // DEBUG
        if (ticksExisted%20 == 0)
        {
        	System.out.println("On client = "+world.isRemote+" the entity ID "+getEntityId()+" the state is "+dataManager.get(STATE)+" and soaring height = "+dataManager.get(SOAR_HEIGHT));
        }
        // syncOwner();
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

            if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
            {
                amount = (amount + 1.0F) / 2.0F;
            }
            
            // DEBUG
            System.out.println("Bird of prey has been attacked by "+source.getImmediateSource()+" with source = "+source.getTrueSource()+" and revenge target set to "+getRevengeTarget()+" for entity id = "+getEntityId());

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
        System.out.println("EntityBirdOfPrey interact()");
        
        ItemStack itemInHand = parPlayer.inventory.getCurrentItem();
        if (isTamed())
        {
            if (itemInHand != null)
            {
                if (itemInHand.getItem() == Items.DYE)
                {
                    EnumDyeColor i = EnumDyeColor.byMetadata(itemInHand.getMetadata());

                    if (i != getLegBandColor())
                    {
                        setLegBandColor(i);

                        if (!parPlayer.capabilities.isCreativeMode)
                        {
                            itemInHand.setCount(itemInHand.getCount()-1);
                        }

                        return true;
                    }
                }
            }
        }
        else if (itemInHand != null)
        {
            // check if raw salmon
            if (isTamingFood(itemInHand))
            {
                // DEBUG
                System.out.println("Trying taming food");
                
                if (rand.nextInt(3) == 0)
                {
                    setTamed(parPlayer);
    
                    // DEBUG
                    System.out.println("It likes the raw salmon");
                    if (!parPlayer.capabilities.isCreativeMode)
                    {
                        itemInHand.setCount(itemInHand.getCount()-1);
                    }
                }
                else
                {
                    spawnTamingParticles(false);
                }
            }
        }

        return super.processInteract(parPlayer, parHand);
    }
    
    public boolean isTamingFood(ItemStack parItemStack)
    {
        // check for raw salmon
        return (parItemStack.getItem() == Items.FISH && parItemStack.getMetadata() == 1);
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
     * Determines if an entity can be despawned, used on idle far away entities
     */
    @Override
    protected boolean canDespawn()
    {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    protected <T> boolean canRenderName(T entity)
    {
    	return false;
    }

    @Override
    public void setDead()
    {
        // DEBUG
        System.out.println("Setting dead");
        
        super.setDead();
    }
    
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
     * Play the taming effect, will either be hearts or smoke depending on status
     */
    protected void spawnTamingParticles(boolean shouldSpawnHearts)
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
    
    /*
     * Indicates whether the bird is more active during night time.
     * Affects tendency to perch and take off from perch
     */
    public boolean isNocturnal()
    {
        return false;
    }
    
    /*
     * Indicates proportions of the head
     */
    public boolean isOwlType()
    {
        return false;
    }
    
    public boolean isTamed()
    {
        return (getOwner() != null);
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
    
    public void setOwnerUUIDString(String parOwnerUUIDString)
    {
        // DEBUG
        System.out.println("Setting new owner with UUID ="+parOwnerUUIDString);
        dataManager.set(OWNER_UUID, parOwnerUUIDString);
    }
    
    public String getOwnerUUIDString()
    {
        return dataManager.get(OWNER_UUID);
    }

    public void setState(int parState)
    {
        // DEBUG
        System.out.println("EntityBirdOfPrey setState() state changed to "+parState);

        dataManager.set(STATE, parState);
    }

    public int getState() 
    {
        return dataManager.get(STATE);
    } 

    public void setAnchor(BlockPos parPos)
    {
        dataManager.set(ANCHOR_POS, parPos);
    }

    public BlockPos getAnchor() 
    {
        return dataManager.get(ANCHOR_POS);
    } 

    public EntityPlayer getOwner()
    {
        try
        {
            UUID uuid = UUID.fromString(getOwnerUUIDString());
            return uuid == null ? null : world.getPlayerEntityByUUID(uuid);
        }
        catch (IllegalArgumentException illegalargumentexception)
        {
            return null;
        }
    }
    
    public boolean setTamed(EntityLivingBase parNewOwner)
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
            System.out.println("Trying to assign a null owner");
            return false;
        }
        else
        {
            spawnTamingParticles(true);
            setAttackTarget(null);
            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(TAMED_HEALTH);
            setOwnerUUIDString(parNewOwner.getUniqueID().toString());
            return true;
        }
    }
    
    public boolean isOwner(EntityLivingBase parEntity)
    {
        return parEntity == getOwner();
    }
    
    public void setSoarClockwise(boolean parClockwise)
    {
        dataManager.set(SOAR_CLOCKWISE, parClockwise);
    }
    
    public boolean getSoarClockwise()
    {
        return dataManager.get(SOAR_CLOCKWISE);
    }
    
    public void setSoarHeight(float parHeight)
    {
        dataManager.set(SOAR_HEIGHT, parHeight);
    }
    
    public float getSoarHeight()
    {
        return dataManager.get(SOAR_HEIGHT);
    }
    
    public int getRandFactor()
    {
        return randFactor;
    }

    @SuppressWarnings("rawtypes")
	public Class[] getPreyArray()
    {
        return preyArray;
    }

    @SuppressWarnings("rawtypes")
	public void setPreyArray(Class[] parPreyArray)
    {
        preyArray = parPreyArray;
    }

    public EnumDyeColor getLegBandColor()
    {
        return EnumDyeColor.byMetadata(dataManager.get(LEG_BAND_COLOR));
    }

    public void setLegBandColor(EnumDyeColor parLegBandColor)
    {
        dataManager.set(LEG_BAND_COLOR, parLegBandColor.getMetadata());
    }

	@Override
	public void initSyncDataCompound() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEntitySyncPacket() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NBTTagCompound getSyncDataCompound() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSyncDataCompound(NBTTagCompound parCompound) {
		// TODO Auto-generated method stub
		
	}

}