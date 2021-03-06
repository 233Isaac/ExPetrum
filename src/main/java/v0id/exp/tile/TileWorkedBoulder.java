package v0id.exp.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import v0id.api.exp.tile.ISyncableTile;

public class TileWorkedBoulder extends TileEntity implements ISyncableTile
{
	public byte workedIndex;
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.workedIndex = compound.getByte("workedIndex");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		NBTTagCompound ret = super.writeToNBT(compound);
		ret.setByte("workedIndex", this.workedIndex);
		return ret;
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(this.getPos(), 0, this.serializeNBT());
	}
	
	@Override
	public NBTTagCompound getUpdateTag()
	{
		return this.serializeNBT();
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		this.deserializeNBT(pkt.getNbtCompound());
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag)
	{
		this.deserializeNBT(tag);
	}

	@Override
	public NBTTagCompound serializeData()
	{
		NBTTagCompound ret = new NBTTagCompound();
		ret.setByte("workedIndex", this.workedIndex);
		return ret;
	}

	@Override
	public void readData(NBTTagCompound tag)
	{
		this.workedIndex = tag.getByte("workedIndex");
	}
}
