package v0id.exp.handler;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import v0id.api.exp.data.ExPRegistryNames;
import v0id.exp.block.IBlockRegistryEntry;
import v0id.exp.block.item.IItemRegistryEntry;
import v0id.exp.potion.IPotionRegistryEntry;
import v0id.exp.world.biome.IBiomeRegistryEntry;

@Mod.EventBusSubscriber(modid = ExPRegistryNames.modid)
public class ExPHandlerRegistry
{
	private static final List<IBlockRegistryEntry> blockEntries = Lists.newArrayList();
	private static final List<IItemRegistryEntry> itemEntries = Lists.newArrayList();
	private static final List<IBiomeRegistryEntry> biomeEntries = Lists.newArrayList();
	private static final List<IPotionRegistryEntry> potionEntries = Lists.newArrayList();
	
	public static void put(Object o)
	{
		if (o instanceof IBlockRegistryEntry)
		{
			blockEntries.add((IBlockRegistryEntry) o);
		}
		
		if (o instanceof IItemRegistryEntry)
		{
			itemEntries.add((IItemRegistryEntry) o);
		}
		
		if (o instanceof IBiomeRegistryEntry)
		{
			biomeEntries.add((IBiomeRegistryEntry) o);
		}
		
		if (o instanceof IPotionRegistryEntry)
		{
			potionEntries.add((IPotionRegistryEntry) o);
		}
	}
	
	@SubscribeEvent
	public static void onBlocksRegistry(RegistryEvent.Register<Block> event)
	{ 
		blockEntries.forEach(e -> e.registerBlock(event.getRegistry()));
	}
	
	@SubscribeEvent
	public static void onItemsRegistry(RegistryEvent.Register<Item> event)
	{
		itemEntries.forEach(e -> e.registerItem(event.getRegistry()));
	}
	
	@SubscribeEvent
	public static void onBiomesRegistry(RegistryEvent.Register<Biome> event)
	{
		biomeEntries.forEach(e -> e.registerBiome(event.getRegistry()));
	}
	
	@SubscribeEvent
	public static void onPotionsRegistry(RegistryEvent.Register<Potion> event)
	{
		potionEntries.forEach(e -> e.registerPotion(event.getRegistry()));
	}
}