package v0id.exp.block.plant;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import v0id.api.core.util.ItemBlockWithMetadata;
import v0id.api.exp.data.ExPBlocks;
import v0id.api.exp.data.ExPCreativeTabs;
import v0id.api.exp.data.ExPMisc;
import v0id.api.exp.data.ExPRegistryNames;
import v0id.api.exp.inventory.IWeightProvider;

public class BlockCoralPlant extends Block implements IWeightProvider
{
	public static PropertyInteger TEXTURE_INDEX_ROCK = PropertyInteger.create("rtindex", 0, 5);
	public static PropertyInteger TEXTURE_INDEX_PLANT = PropertyInteger.create("ptindex", 0, 15);
	
	public BlockCoralPlant()
	{
		super(Material.ROCK);
		this.initBlock();
	}

	public void initBlock()
	{
		this.setHardness(1f);
		this.setRegistryName(ExPRegistryNames.blockCoralPlant);
		this.setResistance(0);
		this.setSoundType(SoundType.STONE);
		this.setUnlocalizedName(this.getRegistryName().toString().replace(':', '.'));
		this.setDefaultState(this.blockState.getBaseState().withProperty(TEXTURE_INDEX_ROCK, 0).withProperty(TEXTURE_INDEX_PLANT, 0));
		this.setCreativeTab(ExPCreativeTabs.tabCommon);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlockWithMetadata(this));
		this.setTickRandomly(true);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, TEXTURE_INDEX_ROCK, TEXTURE_INDEX_PLANT);
	}
	
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
	{
		super.onNeighborChange(world, pos, neighbor);
		this.doPlacementChecks(world, pos);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		this.onNeighborChange(worldIn, pos, fromPos);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		super.updateTick(worldIn, pos, state, rand);
		this.doPlacementChecks(worldIn, pos);
	}
	
	public void doPlacementChecks(IBlockAccess world, BlockPos pos)
	{
		if (world instanceof World)
		{
			World w = (World) world;
			if (w.isAirBlock(pos.down()) || !w.getBlockState(pos.down()).isSideSolid(w, pos.down(), EnumFacing.UP))
			{
				this.breakReplaceSelf(w, pos);
				return;
			}
		}
	}
	
	public void breakReplaceSelf(World w, BlockPos pos)
	{
		w.setBlockState(pos, ExPBlocks.coralRock.getDefaultState());
	}
	
	@Override
	public float provideWeight(ItemStack item)
	{
		return 0;
	}

	@Override
	public float provideVolume(ItemStack item)
	{
		return 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Items.AIR;
	}

	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
	{
		worldIn.setBlockState(pos, ExPBlocks.coralRock.getDefaultState());
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		ExPMisc.modelVariantRandom.setSeed(MathHelper.getPositionRandom(pos));
		return state.withProperty(TEXTURE_INDEX_ROCK, ExPMisc.modelVariantRandom.nextInt(6)).withProperty(TEXTURE_INDEX_PLANT, ExPMisc.modelVariantRandom.nextInt(16));
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		return this.getExtendedState(state, worldIn, pos);
	}
}
