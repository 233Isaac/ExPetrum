package v0id.exp.crop;

import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import v0id.api.core.VoidApi;
import v0id.api.core.settings.VCSettings;
import v0id.api.exp.data.ExPItems;
import v0id.api.exp.event.crop.EventFarmlandUpdate;
import v0id.api.exp.tile.crop.EnumCrop;
import v0id.api.exp.tile.crop.EnumPlantNutrient;
import v0id.api.exp.tile.crop.ExPCropCapability;
import v0id.api.exp.tile.crop.ExPFarmlandCapability;
import v0id.api.exp.tile.crop.ExPSeedsCapability;
import v0id.api.exp.tile.crop.IExPSeed;
import v0id.api.exp.tile.crop.IFarmland;
import v0id.api.exp.world.Calendar;
import v0id.api.exp.world.IExPWorld;
import v0id.api.exp.world.ImmutableCalendar;
import v0id.api.exp.world.TemperatureRange;
import v0id.exp.block.plant.BlockVegetation;
import v0id.exp.util.Helpers;

public class CropLogic
{
	public static ItemStack createSeeds(ExPCrop crop, Random rand)
	{
		ItemStack seeds = new ItemStack(ExPItems.seeds, 1, crop.getType().ordinal() - 1);
		CropStats stats = new CropStats(crop.stats);
		++stats.generation;
		stats.growth = 0;
		stats.wild = false;
		float randomChanceMultipleSeeds = Math.min(6, (float)crop.getGeneration() * 0.1F);
		while (randomChanceMultipleSeeds > 0)
		{
			if (rand.nextDouble() * randomChanceMultipleSeeds < 0.5)
			{
				seeds.grow(1);
			}
			
			--randomChanceMultipleSeeds;
		}
		
		if (rand.nextInt(35) < crop.getGeneration())
		{
			TemperatureRange rangeMin = crop.getSurvivalTemperature();
			TemperatureRange rangeMed = crop.getOptimalTemperature();
			TemperatureRange rangeHig = crop.getSurvivalTemperature();
			if (rangeMin.min > -20 && rangeMin.max < 60)
			{
				stats.growthRanges[0] = new TemperatureRange(rangeMin.min * 1.05F, rangeMin.max * 1.05F);
				stats.growthRanges[1] = new TemperatureRange(rangeMed.min * 1.05F, rangeMed.max * 1.05F);
				stats.growthRanges[2] = new TemperatureRange(rangeHig.min * 1.05F, rangeHig.max * 1.05F);
			}
		}
		
		if (rand.nextInt(100) < crop.getGeneration())
		{
			stats.growthRate *= 1.1;
		}
		
		if (rand.nextInt(20) < crop.getGeneration())
		{
			stats.waterConsumption *= 0.94F;
		}
		
		IExPSeed cap = seeds.getCapability(ExPSeedsCapability.seedsCap, null);
		cap.setExtendedData(stats.createItemNBT(null));
		return seeds;
	}
	
	public static void onFarmlandUpdate(ExPFarmland farmland)
	{
		if (((VCSettings)(VoidApi.config.dataHolder)).recoveryMode)
		{
			return;
		}
		
		if (MinecraftForge.EVENT_BUS.post(new EventFarmlandUpdate.Pre(farmland, farmland.getContainer().getWorld(), farmland.getContainer().getPos())))
		{
			return;
		}
		
		Calendar prev = farmland.timeKeeper;
		if (prev.getTime() == 0)
		{
			prev.setTime(IExPWorld.of(farmland.getContainer().getWorld()).today().getTime());
		}
		else
		{
			Calendar current = IExPWorld.of(farmland.getContainer().getWorld()).today();
			long ticksDelta = current.getTime() - prev.getTime();
			farmland.timeKeeper = current;
			handleFarmlandTimePassed(farmland, ticksDelta, current);
		}
		
		MinecraftForge.EVENT_BUS.post(new EventFarmlandUpdate.Post(farmland, farmland.getContainer().getWorld(), farmland.getContainer().getPos()));
	}
	
	public static void handleFarmlandTimePassed(ExPFarmland farmland, long ticks, Calendar calendarReference)
	{
		World w = farmland.getContainer().getWorld();
		BlockPos pos = farmland.getContainer().getPos();
		ImmutableCalendar ref = new ImmutableCalendar(calendarReference);
		EventFarmlandUpdate.Logic.Pre pre = new EventFarmlandUpdate.Logic.Pre(farmland, w, pos, ref, ticks);
		if (MinecraftForge.EVENT_BUS.post(pre))
		{
			return;
		}
		
		ticks = pre.ticksHappened;
		IBlockState above = w.getBlockState(pos.up());
		boolean canSeeTheSky = w.canBlockSeeSky(pos.up());
		TileEntity tileAbove = w.getTileEntity(pos.up());
		if (tileAbove != null && tileAbove.hasCapability(ExPCropCapability.cropCap, EnumFacing.DOWN))
		{
			return;
		}
		
		EventFarmlandUpdate.Logic.WaterNutrientLogic wnLogic = new EventFarmlandUpdate.Logic.WaterNutrientLogic(farmland, w, pos, ref, ticks, (0.00001f * (above.getBlock() instanceof BlockVegetation ? 2 : 1)) * ticks, (1.39e-6F * (above.getBlock() instanceof BlockVegetation ? -10 : 1)) * ticks);
		MinecraftForge.EVENT_BUS.post(wnLogic);
		float waterLoss = wnLogic.waterLossBase;
		float nutrientGain = wnLogic.nutrientGainBase;
		if (w.isRaining() && canSeeTheSky && !MinecraftForge.EVENT_BUS.post(new EventFarmlandUpdate.Logic.Rain(farmland, w, pos, ref, ticks)))
		{
			farmland.setMoisture(1);
		}
		else
		{
			farmland.setMoisture(MathHelper.clamp(farmland.getMoisture() - waterLoss, 0, 1));
		}
		
		Stream.of(EnumPlantNutrient.values()).forEach(n -> farmland.setNutrient(n, MathHelper.clamp(farmland.getNutrient(n) + nutrientGain, 0, 1)));
		MinecraftForge.EVENT_BUS.post(new EventFarmlandUpdate.Logic.Post(farmland, w, pos, ref, ticks));
		farmland.setDirty();
	}
	
	public static void onWorldUpdate(ExPCrop crop)
	{
		if (((VCSettings)(VoidApi.config.dataHolder)).recoveryMode)
		{
			return;
		}
		
		Calendar prev = crop.timeKeeper;
		
		// Should not happen
		if (prev.getTime() == 0)
		{
			prev.setTime(IExPWorld.of(crop.getContainer().getWorld()).today().getTime());
		}
		else
		{
			Calendar current = IExPWorld.of(crop.getContainer().getWorld()).today();
			long ticksDelta = current.getTime() - prev.getTime();
			crop.timeKeeper = current;
			handleTimePassed(crop, ticksDelta, current);
		}
	}
	
	public static void handleTimePassed(ExPCrop crop, long ticks, Calendar calendarReference)
	{
		// Dead crops can't update
		if (crop.getType() == EnumCrop.DEAD)
		{
			return;
		}
		
		World w = crop.getContainer().getWorld();
		BlockPos pos = crop.getContainer().getPos();
		TileEntity tile = w.getTileEntity(pos.down());
		
		if (crop.isWild())
		{
			if (crop.getSurvivalTemperature().isWithinRange(Helpers.getTemperatureAt(w, pos)))
			{
				crop.setGrowth(Math.min(1, crop.getGrowth() + ticks * (crop.stats.growthRate / (90 * calendarReference.ticksPerDay))));
			}
			else
			{
				crop.causeDamage(ticks * 0.0001F);
			}
			
			return;
		}
		
		// Crop can't exist on a block that can't support it. Should not happen.
		if (tile == null || !tile.hasCapability(ExPFarmlandCapability.farmlandCap, EnumFacing.UP))
		{
			w.setBlockToAir(pos);
			return;
		}
		
		IFarmland farmland = IFarmland.of(tile, EnumFacing.UP);
		float waterConsumption = crop.getWaterConsumption() / calendarReference.ticksPerDay / 100;
		float waterConsumed = waterConsumption * ticks;
		float waterPresent = farmland.getMoisture();
		float waterActuallyConsumed = Math.min(waterConsumed, waterPresent);
		float waterLeft = waterPresent - waterActuallyConsumed;
		long ticksWaterConsumedFor = (long) (waterActuallyConsumed / waterConsumption);
		farmland.setMoisture(waterLeft);
		boolean allNutrientsFine = consumeNutrients(farmland, ticks, crop, calendarReference);
		boolean moistureInRange = waterPresent >= crop.getType().getData().humidityRange.min && waterPresent <= crop.getType().getData().humidityRange.max;
		float temperature = Helpers.getTemperatureAt(w, pos);
		boolean inBaseRange = crop.getSurvivalTemperature().isWithinRange(temperature);
		boolean canGrow = crop.canGrowOverride(Helpers.canPlantGrow(pos, w)) && inBaseRange; 
		long growthTicks = 0;
		if (canGrow)
		{
			float growthMultiplier = 0.25F;
			if (allNutrientsFine)
			{
				if (crop.getIdealTemperature().isWithinRange(temperature))
				{
					growthMultiplier = 1.1F;
				}
				else
				{
					if (crop.getOptimalTemperature().isWithinRange(growthMultiplier))
					{
						growthMultiplier = 1.0F;
					}
					else
					{
						growthMultiplier = 0.75F;
					}
				}
			}
			
			growthMultiplier *= farmland.getGrowthMultiplier();
			growthTicks = (long) (ticksWaterConsumedFor * growthMultiplier);
			float grownBy = growthTicks * (crop.stats.growthRate / (calendarReference.ticksPerDay * 30));
			crop.setGrowth(crop.getGrowth() == 1 ? 1 : Math.min(crop.isHarvestSeason(IExPWorld.of(w).getCurrentSeason()) ? 1 : 0.99F, crop.getGrowth() + grownBy));
			if (ticks > ticksWaterConsumedFor)
			{
				crop.causeDamage((ticksWaterConsumedFor - ticks) * 0.0005F);
			}
			else
			{
				if (!crop.isSick && moistureInRange)
				{
					crop.setHealth(Math.min(crop.getType().getData().baseHealth, crop.getHealth() + ticks * 0.0001F));
				}
			}
		}
		else
		{
			crop.causeDamage(ticks * 0.00138F);
		}
		
		if (crop.isSick)
		{
			crop.causeDamage(ticks * 0.0006F);
		}
		
		if (!moistureInRange)
		{
			crop.causeDamage(ticks * 0.0002F);
		}
		
		farmland.setDirty();
	}
	
	public static boolean consumeNutrients(IFarmland of, long ticks, ExPCrop crop, Calendar calendarReference)
	{
		boolean consumedAll = true;
		for (Entry<EnumPlantNutrient, Float> kv : crop.getNutrientConsumption().entrySet())
		{
			float nutrientConsumed = kv.getValue() / calendarReference.ticksPerDay;
			float nutrientCurrent = of.getNutrient(kv.getKey());
			float actuallyConsumed = Math.min(nutrientCurrent, nutrientConsumed);
			float left = nutrientCurrent - actuallyConsumed;
			of.setNutrient(kv.getKey(), left);
			consumedAll &= actuallyConsumed < nutrientCurrent;
		}
		
		return consumedAll;
	}
}