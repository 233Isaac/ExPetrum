package v0id.exp.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.Pair;
import v0id.api.exp.data.ExPCreativeTabs;
import v0id.api.exp.data.ExPRegistryNames;
import v0id.api.exp.inventory.IWeightProvider;
import v0id.exp.block.item.ItemBlockWithMetadata;
import v0id.exp.tile.TileSpinningWheel;

import javax.annotation.Nullable;

public class BlockSpinningWheel extends Block implements IWeightProvider, IItemBlockProvider
{
    public BlockSpinningWheel()
    {
        super(Material.WOOD);
        this.setHardness(1.0F);
        this.setResistance(3);
        this.setRegistryName(ExPRegistryNames.blockSpinningWheel);
        this.setSoundType(SoundType.WOOD);
        this.setUnlocalizedName(this.getRegistryName().toString().replace(':', '.'));
        this.setCreativeTab(ExPCreativeTabs.tabMiscBlocks);
        this.setLightOpacity(0);
    }

    @Override
    public float provideWeight(ItemStack item)
    {
        return 2F;
    }

    @Override
    public Pair<Byte, Byte> provideVolume(ItemStack item)
    {
        return Pair.of((byte)2, (byte)2);
    }

    @Override
    public void registerItem(IForgeRegistry<Item> registry)
    {
        registry.register(new ItemBlockWithMetadata(this));
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }

        ((TileSpinningWheel)worldIn.getTileEntity(pos)).click(playerIn);
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileSpinningWheel();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
}
