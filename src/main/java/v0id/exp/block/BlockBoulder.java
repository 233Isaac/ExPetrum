package v0id.exp.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import v0id.api.exp.block.IChiselable;
import v0id.api.exp.block.IOreHintReplaceable;
import v0id.api.exp.block.property.EnumRockClass;
import v0id.api.exp.data.*;
import v0id.exp.block.item.ItemBlockWithMetadata;
import v0id.exp.item.ItemRock;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static v0id.api.exp.block.property.EnumRockClass.ANDESITE;
import static v0id.api.exp.data.ExPBlockProperties.ROCK_CLASS;

public class BlockBoulder extends Block implements IOreHintReplaceable, IOreDictEntry, IItemBlockProvider, IChiselable
{
	public static final PropertyInteger MODEL_INDEX = PropertyInteger.create("amdl", 0, 3);
	public static final AxisAlignedBB BOULDER_AABB = new AxisAlignedBB(0.1, 0, 0.1, 0.9, 0.6, 0.9);
	
	public BlockBoulder()
	{
		super(Material.ROCK);
		this.setHardness(3f);
		this.setResistance(0);
		this.setRegistryName(ExPRegistryNames.asLocation(ExPRegistryNames.blockBoulder));
		this.setSoundType(SoundType.STONE);
		this.setUnlocalizedName(this.getRegistryName().toString().replace(':', '.'));
		this.setDefaultState(this.blockState.getBaseState().withProperty(ROCK_CLASS, ANDESITE).withProperty(MODEL_INDEX, 0));
		this.setCreativeTab(ExPCreativeTabs.tabUnderground);
	}
		
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return ExPItems.rock;
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random random)
    {
        return 3 + random.nextInt(4);
    }

	@Override
	public int damageDropped(IBlockState state)
    {
        return state.getValue(ROCK_CLASS).ordinal();
    }
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,	EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (playerIn != null)
		{
			ItemStack held = playerIn.getHeldItem(hand);
			if (!held.isEmpty() && held.getItem() instanceof ItemRock || held.getItem() == Items.FLINT)
			{
				for (int i = 0; i < 16; ++i)
				{
					worldIn.spawnParticle(EnumParticleTypes.BLOCK_CRACK, pos.getX() + worldIn.rand.nextDouble(), pos.getY() + worldIn.rand.nextDouble(), pos.getZ() + worldIn.rand.nextDouble(), 0, 0, 0, Block.getStateId(state));
				}
				
				if (!worldIn.isRemote)
				{
					if (worldIn.rand.nextInt(8) == 0)
					{
						worldIn.playSound(null, pos, SoundEvents.BLOCK_ANVIL_FALL, SoundCategory.BLOCKS, 1, 0.1f);
						worldIn.setBlockState(pos, ExPBlocks.workedBoulder.getDefaultState().withProperty(ROCK_CLASS, state.getValue(ROCK_CLASS)));
						held.shrink(1);
					}
					else
					{
						worldIn.playSound(null, pos, SoundEvents.BLOCK_ANVIL_STEP, SoundCategory.BLOCKS, 1, 2f);
						if (worldIn.rand.nextBoolean() && worldIn.rand.nextBoolean())
						{
							if (held.getItem() == Items.FLINT)
							{
								return true;
							}

							held.shrink(1);
						}
					}
				}
				
				return true;
			}
		}
		
		return false;
	}

	@Override
	public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return PathNodeType.WALKABLE;
	}

	@SuppressWarnings("deprecation")
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(ROCK_CLASS, EnumRockClass.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(ROCK_CLASS).ordinal();
	}

	@SuppressWarnings("deprecation")
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		ExPMisc.modelVariantRandom.setSeed(MathHelper.getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ()));
		return state.withProperty(MODEL_INDEX, ExPMisc.modelVariantRandom.nextInt(4));
	}

	@SuppressWarnings("deprecation")
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return BOULDER_AABB;
	}

	@SuppressWarnings("deprecation")
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return this.getBoundingBox(blockState, worldIn, pos);
	}
	
    @SuppressWarnings("deprecation")
	@Override
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }

	@SuppressWarnings("deprecation")
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP))
		{
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
	{
		for (int i = 0; i < EnumRockClass.values().length; ++i)
		{
			list.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, ROCK_CLASS, MODEL_INDEX);
	}

	@Override
	public EnumOffsetType getOffsetType()
	{
		return EnumOffsetType.XZ;
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
	{
		super.onNeighborChange(world, pos, neighbor);
		if (world instanceof World)
		{
			this.neighborChanged(world.getBlockState(pos), (World) world, pos, this, neighbor);
		}
	}

	@Override
	public void registerOreDictNames()
	{
		Stream.of(ExPOreDict.blockBoulder).forEach(s -> { 
			OreDictionary.registerOre(s, new ItemStack(this, 1, OreDictionary.WILDCARD_VALUE)); 
			AtomicInteger i = new AtomicInteger(0);
			Stream.of(ExPOreDict.rockNames).forEach(ss -> OreDictionary.registerOre(s + Character.toUpperCase(ss.charAt(0)) + ss.substring(1), new ItemStack(this, 1, i.getAndIncrement())));
		});
	}

	@Override
	public void registerItem(IForgeRegistry<Item> registry)
	{
		registry.register(new ItemBlockWithMetadata(this));
	}

	@Override
	public IBlockState chisel(IBlockState original, World world, BlockPos pos)
	{
		return ExPBlocks.workedBoulder.getDefaultState().withProperty(ROCK_CLASS, original.getValue(ROCK_CLASS));
	}
}
