package v0id.exp.registry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import v0id.api.exp.data.ExPBlocks;
import v0id.api.exp.data.ExPCreativeTabs;
import v0id.api.exp.data.ExPItems;

public class ExPCreativeTabsRegistry extends AbstractRegistry
{
	static
	{
		ExPCreativeTabs.tabUnderground = new CreativeTabs(CreativeTabs.getNextID(), "exp.underground"){
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem()
			{
				return new ItemStack(ExPBlocks.rock);
			}
		};
		
		ExPCreativeTabs.tabOres = new CreativeTabs(CreativeTabs.getNextID(), "exp.ores"){
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem()
			{
				return new ItemStack(ExPBlocks.ore);
			}
		};
		
		ExPCreativeTabs.tabCommon = new CreativeTabs(CreativeTabs.getNextID(), "exp.commonBlocks"){
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem()
			{
				return new ItemStack(ExPBlocks.soil);
			}
		};
		
		ExPCreativeTabs.tabPlantlife = new CreativeTabs(CreativeTabs.getNextID(), "exp.plants"){
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem()
			{
				return new ItemStack(ExPBlocks.vegetation);
			}
		};
		
		ExPCreativeTabs.tabMiscBlocks = new CreativeTabs(CreativeTabs.getNextID(), "exp.miscBlocks"){
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem()
			{
				return new ItemStack(ExPBlocks.freshWater);
			}
		};
		
		ExPCreativeTabs.tabMetals = new CreativeTabs(CreativeTabs.getNextID(), "exp.metals"){
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem()
			{
				return new ItemStack(ExPItems.ingot);
			}
		};
		
		ExPCreativeTabs.tabTools = new CreativeTabs(CreativeTabs.getNextID(), "exp.tools"){
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem()
			{
				return new ItemStack(ExPItems.knife);
			}
		};
		
		ExPCreativeTabs.tabFood = new CreativeTabs(CreativeTabs.getNextID(), "exp.food"){
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem()
			{
				return new ItemStack(ExPItems.food);
			}
		};
	}
	
	public ExPCreativeTabsRegistry()
	{
		super();
	}
}