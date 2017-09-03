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

package com.blogspot.jabelarminecraft.wildanimals.proxy;

import com.blogspot.jabelarminecraft.wildanimals.EventHandler;
import com.blogspot.jabelarminecraft.wildanimals.MainMod;
import com.blogspot.jabelarminecraft.wildanimals.OreGenEventHandler;
import com.blogspot.jabelarminecraft.wildanimals.TerrainGenEventHandler;
import com.blogspot.jabelarminecraft.wildanimals.commands.CommandConjure;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityJaguar;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityLion;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityLynx;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityManEatingJaguar;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityManEatingLion;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityManEatingLynx;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityManEatingTiger;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityTiger;
import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityEagle;
import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityHawk;
import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityOwl;
import com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals.EntityElephant;
import com.blogspot.jabelarminecraft.wildanimals.entities.serpents.EntitySerpent;
import com.blogspot.jabelarminecraft.wildanimals.networking.MessageSyncEntityToClient;
import com.blogspot.jabelarminecraft.wildanimals.networking.MessageToClient;
import com.blogspot.jabelarminecraft.wildanimals.networking.MessageToServer;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy 
{
    
    protected int modEntityID = -1;
    
    // fluids
    public Fluid testFluid;
     
    public void fmlLifeCycleEvent(FMLPreInitializationEvent event)
    { 
        // load configuration before doing anything else
        // got config tutorial from http://www.minecraftforge.net/wiki/How_to_make_an_advanced_configuration_file
        initConfig(event);

        // register stuff
        registerBlocks();
        registerItems();
        registerTileEntities();
        registerModEntities();
        registerEntitySpawns();
        registerFuelHandlers();
        registerSimpleNetworking();
//      VillagerRegistry.instance().registerVillagerId(10);
//      VillagerRegistry.instance().registerVillageTradeHandler(10, new VillageTradeHandlerMagicBeans());
//      VillagerRegistry.getRegisteredVillagers();
    }
    
    public void fmlLifeCycleEvent(FMLInitializationEvent event)
    {
        // register custom event listeners
        registerEventListeners();
        
        // register recipes here to allow use of items from other mods
        registerRecipes();
    }
    
    public void fmlLifeCycleEvent(FMLPostInitializationEvent event)
    {
        // can do some inter-mod stuff here
    }
    
    protected void initConfig(FMLPreInitializationEvent event)
    {
        MainMod.configFile = event.getSuggestedConfigurationFile();
        // DEBUG
        System.out.println(MainMod.MODNAME+" config path = "+MainMod.configFile.getAbsolutePath());
        System.out.println("Config file exists = "+MainMod.configFile.canRead());
  
        MainMod.config = new Configuration(MainMod.configFile);
      	syncConfig();
    }
    
    public static void syncConfig() 
    {
    	MainMod.configBigCatsAreManEaters = MainMod.config.getBoolean("BigCatsAreManEaters", Configuration.CATEGORY_GENERAL, MainMod.configBigCatsAreManEaters, "A Boolean!");
    	MainMod.configIncludeSnakes = MainMod.config.getBoolean("IncludeSnakes", Configuration.CATEGORY_GENERAL, MainMod.configIncludeSnakes, "A Boolean!");
    	MainMod.configIncludeBigCats = MainMod.config.getBoolean("IncludeBigCats", Configuration.CATEGORY_GENERAL, MainMod.configIncludeBigCats, "A Boolean!");
    	MainMod.configIncludeHerdAnimals = MainMod.config.getBoolean("IncludeHerdAnimals", Configuration.CATEGORY_GENERAL, MainMod.configIncludeHerdAnimals, "A Boolean!");
        MainMod.configIncludeBirdsOfPrey = MainMod.config.getBoolean("IncludeBirdsOfPrey", Configuration.CATEGORY_GENERAL, MainMod.configIncludeBirdsOfPrey, "A Boolean!");
 
        if(MainMod.config.hasChanged())
        {
        	MainMod.config.save();
        }
    }

    // register blocks
    public void registerBlocks()
    {
        //example: GameRegistry.registerBlock(blockTomato, "tomatoes");
     }

    // register fluids
    public void registerFluids()
    {
//        // see tutorial at http://www.minecraftforge.net/wiki/Create_a_Fluid
//        Fluid testFluid = new Fluid("testfluid", new ResourceLocation("testfluid"));
//        FluidRegistry.registerFluid(testFluid);
//        testFluid.setLuminosity(0).setDensity(1000).setViscosity(1000).setGaseous(false) ;
     }
    
    // register items
    private void registerItems()
    {
        // DEBUG
        System.out.println("Registering items");

        // spawn eggs are registered during entity registration
        
        // example: GameRegistry.registerCustomItemStack(name, itemStack);
    }
    
    // register tileentities
    public void registerTileEntities()
    {
        // DEBUG
        System.out.println("Registering tile entities");
               
        // example: GameRegistry.registerTileEntity(TileEntityStove.class, "stove_tile_entity");
    }

    // register recipes
    public void registerRecipes()
    {
        // DEBUG
        System.out.println("Registering recipes");
               
        // examples:
        //        GameRegistry.addRecipe(recipe);
        //        GameRegistry.addShapedRecipe(output, params);
        //        GameRegistry.addShapelessRecipe(output, params);
        //        GameRegistry.addSmelting(input, output, xp);
    }

    // register entities
    // lots of conflicting tutorials on this, currently following: nly register mod id http://www.minecraftforum.net/topic/1417041-mod-entity-problem/page__st__140#entry18822284
    // another tut says to only register global id like http://www.minecraftforge.net/wiki/How_to_register_a_mob_entity#Registering_an_Entity
    // another tut says to use both: http://www.minecraftforum.net/topic/2389683-172-forge-add-new-block-item-entity-ai-creative-tab-language-localization-block-textures-side-textures/
    public void registerModEntities()
    {    
         // DEBUG
        System.out.println("Registering entities");
        
        // uses configuration file to control whether each entity type is registered, to allow user customization
        
        // Big cats
        if (MainMod.configIncludeBigCats)
        {
            if (MainMod.configBigCatsAreManEaters)
            {
                registerModEntityWithEgg(EntityManEatingTiger.class, "tiger", 0xE18519, 0x000000);
                registerModEntityWithEgg(EntityManEatingLion.class, "lion", 0xD9C292, 0xFFFFFF);
                registerModEntityWithEgg(EntityManEatingLynx.class, "lynx", 0xD9C292, 0xFFFFFF);
                registerModEntityWithEgg(EntityManEatingJaguar.class, "jaguar", 0xF4E003, 0x000000);
            }
            else
            {
                registerModEntityWithEgg(EntityTiger.class, "tiger", 0xE18519, 0x000000);
                registerModEntityWithEgg(EntityLion.class, "lion", 0xD9C292, 0xFFFFFF);
                registerModEntityWithEgg(EntityLynx.class, "lynx", 0xD9C292, 0xFFFFFF);
                registerModEntityWithEgg(EntityJaguar.class, "jaguar", 0xF4E003, 0x000000);
            }
        }

        // Herd animals
        if (MainMod.configIncludeHerdAnimals)
        {
            registerModEntityWithEgg(EntityElephant.class, "elephant", 0x888888, 0xAAAAAA);
        }
        
        // Serpents
        if (MainMod.configIncludeSnakes)
        {
            registerModEntityWithEgg(EntitySerpent.class, "python", 0x3F5505, 0x4E6414);
        }
        
        // Birds of Prey
        if (MainMod.configIncludeBirdsOfPrey)
        {
            registerModEntityWithEggLongTracking(EntityEagle.class, "eagle", 0xFFF2E3, 0x7D6C57);
            registerModEntityWithEggLongTracking(EntityHawk.class, "hawk", 0x7D6C57, 0xFFF2E3);
            registerModEntityWithEggLongTracking(EntityOwl.class, "owl", 0xFFF2E3, 0xFFFFFF);
        }
    }
     
     public void registerModEntity(Class parClass, String parName)
     {
         EntityRegistry.registerModEntity(new ResourceLocation(MainMod.MODID+":"+parName), parClass, parName, ++modEntityID, MainMod.instance, 80, 3, false);
       	 // DEBUG
       	 System.out.println("Registering mod entity "+parName+" with ID ="+modEntityID);
     }
     
     public void registerModEntityLongTracking(Class parClass, String parName)
     {
            EntityRegistry.registerModEntity(new ResourceLocation(MainMod.MODID, parName), parClass, parName, ++modEntityID, MainMod.instance, 80000, 3, false);
         // DEBUG
         System.out.println("Registering mod entity with long tracking "+parName+" with ID ="+modEntityID);
     }

     public void registerModEntityWithEgg(Class parClass, String parName, int parEggColor, int parEggSpotsColor)
     {
         EntityRegistry.registerModEntity(new ResourceLocation(MainMod.MODID, parName), parClass, parName, ++modEntityID, MainMod.instance, 80, 3, false, parEggColor, parEggSpotsColor);
       	 // DEBUG
       	 System.out.println("Registering mod entity "+parName+" with ID ="+modEntityID);
     }

     public void registerModEntityWithEggLongTracking(Class parClass, String parName, int parEggColor, int parEggSpotsColor)
     {
         EntityRegistry.registerModEntity(new ResourceLocation(MainMod.MODID, parName), parClass, parName, ++modEntityID, MainMod.instance, 80000, 3, false, parEggColor, parEggSpotsColor);
       	 // DEBUG
       	 System.out.println("Registering mod entity with long tracking "+parName+" with ID ="+modEntityID);
     }

     
     // for fast moving entities and projectiles need registration with tracking flag set true
     public void registerModEntityFastTracking(Class parClass, String parName)
     {
            EntityRegistry.registerModEntity(new ResourceLocation(MainMod.MODID, parName), parClass, parName, ++modEntityID, MainMod.instance, 80, 10, true);
          	 // DEBUG
          	 System.out.println("Registering fast tracking mod entity "+parName+" with ID ="+modEntityID);
     }
          
    public void registerEntitySpawns()
    {
        // register natural spawns for entities
        // EntityRegistry.addSpawn(MyEntity.class, spawnProbability, minSpawn, maxSpawn, enumCreatureType, [spawnBiome]);
        // See the constructor in Biomes.java to see the rarity of vanilla mobs; Sheep are probability 10 while Endermen are probability 1
        // minSpawn and maxSpawn are about how groups of the entity spawn
        // enumCreatureType represents the "rules" Minecraft uses to determine spawning, based on creature type. By default, you have three choices:
        //    EnumCreatureType.creature uses rules for animals: spawn everywhere it is light out.
        //    EnumCreatureType.monster uses rules for monsters: spawn everywhere it is dark out.
        //    EnumCreatureType.waterCreature uses rules for water creatures: spawn only in water.
        // [spawnBiome] is an optional parameter of type Biomes that limits the creature spawn to a single biome type. Without this parameter, it will spawn everywhere. 

         // DEBUG
        System.out.println("Registering natural spawns");
        
        // For the biome type you can use an list, but unfortunately the built-in biomeList contains
        // null entries and will crash, so you need to clean up that list.
        // Diesieben07 suggested the following code to remove the nulls and create list of all biomes
        // Biomes[] allBiomes = Iterators.toArray(Iterators.filter(Iterators.forArray(Biomes.getBiomeGenArray()), Predicates.notNull()), Biomes.class);

        // SAVANNA
        EntityRegistry.addSpawn(EntityLion.class, 6, 1, 5, EnumCreatureType.CREATURE, Biomes.SAVANNA); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityElephant.class, 10, 1, 5, EnumCreatureType.CREATURE, Biomes.SAVANNA); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 6, 1, 1, EnumCreatureType.CREATURE, Biomes.SAVANNA); //change the values to vary the spawn rarity, biome, etc.              

        // savann_PLATEAU
        EntityRegistry.addSpawn(EntityLion.class, 6, 1, 5, EnumCreatureType.CREATURE, Biomes.SAVANNA_PLATEAU); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityElephant.class, 10, 1, 5, EnumCreatureType.CREATURE, Biomes.SAVANNA_PLATEAU); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 6, 1, 1, EnumCreatureType.CREATURE, Biomes.SAVANNA_PLATEAU); //change the values to vary the spawn rarity, biome, etc.              

        // birch FOREST
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.BIRCH_FOREST); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.BIRCH_FOREST); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityEagle.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.BIRCH_FOREST); //change the values to vary the spawn rarity, biome, etc.              

        // birch FOREST hills
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.BIRCH_FOREST_HILLS); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityEagle.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.BIRCH_FOREST_HILLS); //change the values to vary the spawn rarity, biome, etc.              

        // cold taiga
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.COLD_TAIGA); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 6, 1, 1, EnumCreatureType.CREATURE, Biomes.COLD_TAIGA); //change the values to vary the spawn rarity, biome, etc.              
        
        // cold taiga hills
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.COLD_TAIGA_HILLS); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 6, 1, 1, EnumCreatureType.CREATURE, Biomes.COLD_TAIGA_HILLS); //change the values to vary the spawn rarity, biome, etc.              

        // DESERT
        EntityRegistry.addSpawn(EntityLion.class, 6, 1, 5, EnumCreatureType.CREATURE, Biomes.DESERT); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityElephant.class, 10, 1, 5, EnumCreatureType.CREATURE, Biomes.DESERT); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.DESERT); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.DESERT); //change the values to vary the spawn rarity, biome, etc.              

        // DESERT hills
        EntityRegistry.addSpawn(EntityLion.class, 6, 1, 5, EnumCreatureType.CREATURE, Biomes.DESERT_HILLS); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityElephant.class, 10, 1, 5, EnumCreatureType.CREATURE, Biomes.DESERT_HILLS); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.DESERT_HILLS); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.DESERT_HILLS); //change the values to vary the spawn rarity, biome, etc.              

        // extreme hills
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.EXTREME_HILLS); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityEagle.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.EXTREME_HILLS); //change the values to vary the spawn rarity, biome, etc.              

        // extreme hills edge
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.EXTREME_HILLS_EDGE); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityEagle.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.EXTREME_HILLS_EDGE); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityEagle.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.EXTREME_HILLS_EDGE); //change the values to vary the spawn rarity, biome, etc.              
        
        // extreme hills plus
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.EXTREME_HILLS_WITH_TREES); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityEagle.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.EXTREME_HILLS_WITH_TREES); //change the values to vary the spawn rarity, biome, etc.              

        // FOREST
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.FOREST); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.FOREST); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityEagle.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.FOREST); //change the values to vary the spawn rarity, biome, etc.              
        
        // FOREST hills
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.FOREST_HILLS); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityEagle.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.FOREST_HILLS); //change the values to vary the spawn rarity, biome, etc.              
        
        // ice mountains
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.ICE_MOUNTAINS); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 6, 1, 1, EnumCreatureType.CREATURE, Biomes.ICE_MOUNTAINS); //change the values to vary the spawn rarity, biome, etc.              

        // mega taiga
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.REDWOOD_TAIGA); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 6, 1, 1, EnumCreatureType.CREATURE, Biomes.REDWOOD_TAIGA); //change the values to vary the spawn rarity, biome, etc.              

        // mega taiga hills
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.REDWOOD_TAIGA_HILLS); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 6, 1, 1, EnumCreatureType.CREATURE, Biomes.REDWOOD_TAIGA_HILLS); //change the values to vary the spawn rarity, biome, etc.              
       
        // MESA
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.MESA); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.MESA); //change the values to vary the spawn rarity, biome, etc.              
        
        // MESA _ROCK
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.MESA_ROCK); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.MESA_ROCK); //change the values to vary the spawn rarity, biome, etc.              
        
        // MESA _CLEAR_ROCK
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.MESA_CLEAR_ROCK); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.MESA_CLEAR_ROCK); //change the values to vary the spawn rarity, biome, etc.              
        
        // swamp
        EntityRegistry.addSpawn(EntityJaguar.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.SWAMPLAND); //change the values to vary the spawn rarity, biome, etc.                  
        EntityRegistry.addSpawn(EntitySerpent.class, 10, 1, 4, EnumCreatureType.CREATURE, Biomes.SWAMPLAND); //change the values to vary the spawn rarity, biome, etc.              
        
        // JUNGLE
        EntityRegistry.addSpawn(EntityJaguar.class, 6, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityTiger.class, 1, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE); //change the values to vary the spawn rarity, biome, etc.              
        if (MainMod.configBigCatsAreManEaters)
        {
            EntityRegistry.addSpawn(EntityManEatingTiger.class, 1, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE); //change the values to vary the spawn rarity, biome, etc.            
        }
        EntityRegistry.addSpawn(EntitySerpent.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 6, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE); //change the values to vary the spawn rarity, biome, etc.              
        
        // JUNGLE hills
        EntityRegistry.addSpawn(EntityJaguar.class, 3, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE_HILLS); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityTiger.class, 1, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE_HILLS); //change the values to vary the spawn rarity, biome, etc.              
           if (MainMod.configBigCatsAreManEaters)
        {
            EntityRegistry.addSpawn(EntityManEatingTiger.class, 1, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE_HILLS); //change the values to vary the spawn rarity, biome, etc.            
        }
        EntityRegistry.addSpawn(EntitySerpent.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE_HILLS); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 6, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE_HILLS); //change the values to vary the spawn rarity, biome, etc.              
        
        // JUNGLE edge
        EntityRegistry.addSpawn(EntityJaguar.class, 1, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE_EDGE); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityTiger.class, 1, 0, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE_EDGE); //change the values to vary the spawn rarity, biome, etc.              
           if (MainMod.configBigCatsAreManEaters)
        {
            EntityRegistry.addSpawn(EntityManEatingTiger.class, 1, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE_EDGE); //change the values to vary the spawn rarity, biome, etc.            
        }
        EntityRegistry.addSpawn(EntitySerpent.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE_EDGE); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 6, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE_EDGE); //change the values to vary the spawn rarity, biome, etc.              

        // PLAINS
        EntityRegistry.addSpawn(EntityLion.class, 10, 1, 5, EnumCreatureType.CREATURE, Biomes.PLAINS); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityElephant.class, 10, 1, 5, EnumCreatureType.CREATURE, Biomes.PLAINS); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityHawk.class, 10, 1, 1, EnumCreatureType.CREATURE, Biomes.PLAINS); //change the values to vary the spawn rarity, biome, etc.              

        // BEACH
        EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.BEACH); //change the values to vary the spawn rarity, biome, etc.              

        // cold BEACH
        EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.COLD_BEACH); //change the values to vary the spawn rarity, biome, etc.              
        
        // ice PLAINS
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.ICE_PLAINS); //change the values to vary the spawn rarity, biome, etc.              

        // roofed FOREST
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.ROOFED_FOREST); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.ROOFED_FOREST); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityEagle.class, 6, 1, 1, EnumCreatureType.CREATURE, Biomes.ROOFED_FOREST); //change the values to vary the spawn rarity, biome, etc.              

        // stone BEACH
        EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.MONSTER, Biomes.STONE_BEACH); //change the values to vary the spawn rarity, biome, etc.              
    }
    
    public void registerFuelHandlers()
    {
         // DEBUG
        System.out.println("Registering fuel handlers");
        
        // example: GameRegistry.registerFuelHandler(handler);
    }
    
    public void registerEventListeners() 
    {
         // DEBUG
        System.out.println("Registering event listeners");

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.TERRAIN_GEN_BUS.register(new TerrainGenEventHandler());
        MinecraftForge.ORE_GEN_BUS.register(new OreGenEventHandler());        
    }
    
    /*
     * Thanks to diesieben07 tutorial for this code
     */
    /**
     * Registers the simple networking channel and messages for both sides
     */
    protected void registerSimpleNetworking() 
    {
        // DEBUG
        System.out.println("registering simple networking");
        MainMod.network = NetworkRegistry.INSTANCE.newSimpleChannel(MainMod.NETWORK_CHANNEL_NAME);

        int packetId = 0;
        // register messages from client to server
        MainMod.network.registerMessage(MessageToServer.Handler.class, MessageToServer.class, packetId++, Side.SERVER);
        // register messages from server to client
        MainMod.network.registerMessage(MessageToClient.Handler.class, MessageToClient.class, packetId++, Side.CLIENT);
        MainMod.network.registerMessage(MessageSyncEntityToClient.Handler.class, MessageSyncEntityToClient.class, packetId++, Side.CLIENT);
    }

	public void fmlLifeCycleEvent(FMLServerStartingEvent event) 
	{
        event.registerServerCommand(new CommandConjure());
	}

	public void fmlLifeCycleEvent(FMLServerAboutToStartEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void fmlLifeCycleEvent(FMLServerStartedEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void fmlLifeCycleEvent(FMLServerStoppingEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void fmlLifeCycleEvent(FMLServerStoppedEvent event) {
		// TODO Auto-generated method stub
		
	}
	
    /*     
     * Thanks to CoolAlias for this tip!
     */
    /**
     * Returns a side-appropriate EntityPlayer for use during message handling
     */
    public EntityPlayer getPlayerEntityFromContext(MessageContext ctx) 
    {
        return ctx.getServerHandler().player;
    }

    /**
     * @param msg
     */
    public void sendMessageToPlayer(TextComponentString msg)
    {
        // TODO Auto-generated method stub
        
    }
    

}