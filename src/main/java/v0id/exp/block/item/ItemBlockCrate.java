package v0id.exp.block.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import v0id.api.exp.block.EnumTreeType;
import v0id.exp.tile.TileCrate;

public class ItemBlockCrate extends ItemBlockWithMetadata
{
    public ItemBlockCrate(Block block)
    {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        boolean ret = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        TileCrate tile = (TileCrate) world.getTileEntity(pos);
        tile.type = EnumTreeType.values()[stack.getMetadata()];
        EnumFacing facing = EnumFacing.fromAngle(player.rotationYaw);
        tile.direction = facing;
        return ret;
    }
}
