package v0id.exp.item;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.oredict.OreDictionary;
import v0id.api.exp.data.ExPCreativeTabs;
import v0id.api.exp.data.ExPItems;
import v0id.api.exp.data.ExPOreDict;
import v0id.api.exp.data.ExPRegistryNames;
import v0id.api.exp.data.IOreDictEntry;
import v0id.api.exp.inventory.IWeightProvider;
import v0id.api.exp.tile.crop.EnumCrop;
import v0id.api.exp.tile.crop.ExPSeedsCapability;
import v0id.api.exp.tile.crop.IExPSeed;
import v0id.exp.block.item.IItemRegistryEntry;
import v0id.exp.crop.CropStats;
import v0id.exp.handler.ExPHandlerRegistry;

public class ItemSeeds extends Item implements IInitializableItem, IItemRegistryEntry, IOreDictEntry, IWeightProvider
{
	public ItemSeeds()
	{
		super();
		this.initItem();
	}
	
	@Override
	public void registerOreDictNames()
	{
		Stream.of(ExPOreDict.itemSeeds).forEach(s -> { 
			OreDictionary.registerOre(s, new ItemStack(this, 1, OreDictionary.WILDCARD_VALUE)); 
			AtomicInteger i = new AtomicInteger(0);
			Stream.of(ExPOreDict.cropNames).forEach(ss -> OreDictionary.registerOre(s + Character.toUpperCase(ss.charAt(0)) + ss.substring(1), new ItemStack(this, 1, i.getAndIncrement())));
		});
	}

	@Override
	public void registerItem(IForgeRegistry<Item> registry)
	{
		registry.register(this);
	}

	@Override
	public void initItem()
	{
		this.setRegistryName(ExPRegistryNames.itemSeeds);
		this.setUnlocalizedName(this.getRegistryName().toString().replace(':', '.'));
		this.setCreativeTab(ExPCreativeTabs.tabPlantlife);
		this.setHasSubtypes(true);
		ExPHandlerRegistry.put(this);
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		for (int i = 0; i < EnumCrop.values().length - 1; ++i)
		{
			subItems.add(new ItemStack(itemIn, 1, i));
		}
	}

	@Override
	public float provideWeight(ItemStack item)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Pair<Byte, Byte> provideVolume(ItemStack item)
	{
		return Pair.of((byte)1, (byte)1);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
	{
		return new CapabilityExPSeeds(stack);
	}

	public static class CapabilityExPSeeds implements IExPSeed, ICapabilityProvider
	{
		public ItemStack container;
		public CropStats data;
		
		public CapabilityExPSeeds()
		{
			this(new ItemStack(ExPItems.seeds));
		}
		
		public CapabilityExPSeeds(ItemStack is)
		{
			this.container = is;
			this.data = this.getOrCreateStats();
		}
		
		@Override
		public NBTTagCompound serializeNBT()
		{
			return this.container.getOrCreateSubCompound("exp.seedData");
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt)
		{
			this.container.getTagCompound().setTag("exp.seedData", nbt);
		}

		@Override
		public ItemStack getContainer()
		{
			return this.container;
		}

		@Override
		public EnumCrop getCrop()
		{
			return this.data.type;
		}

		@Override
		public void setCrop(EnumCrop crop)
		{
			this.data.type = crop;
			this.container.getTagCompound().setTag("exp.seedData", this.data.createItemNBT(null));
		}

		@Override
		public int getGeneration()
		{
			return this.data.generation;
		}

		@Override
		public void setGeneration(int newVal)
		{
			this.data.generation = newVal;
			this.container.getTagCompound().setTag("exp.seedData", this.data.createItemNBT(null));
		}

		@Override
		public NBTTagCompound getExtendedData()
		{
			return this.seedTag();
		}

		@Override
		public void setExtendedData(NBTTagCompound tag)
		{
			this.container.getTagCompound().setTag("exp.seedData", tag);
			this.data = this.getOrCreateStats();
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing)
		{
			return capability == ExPSeedsCapability.seedsCap ? true : false;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing)
		{
			return capability == ExPSeedsCapability.seedsCap ? ExPSeedsCapability.seedsCap.cast(this) : null;
		}
		
		public NBTTagCompound seedTag()
		{
			return this.container.getOrCreateSubCompound("exp.seedData");
		}
		
		public CropStats getOrCreateStats()
		{
			if (!this.container.hasTagCompound() || !this.container.getTagCompound().hasKey("exp.seedData"))
			{
				EnumCrop crop = EnumCrop.values()[Math.min(this.container.getMetadata() + 1, EnumCrop.values().length - 1)];
			}
			
			CropStats ret = new CropStats();
			ret.createFromItemNBT(this.seedTag());
			this.container.getTagCompound().setTag("exp.seedData", ret.createItemNBT(null));
			return ret;
		}
	}
}