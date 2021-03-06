package v0id.exp.entity;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import v0id.api.exp.gravity.IGravitySusceptible;
import v0id.api.exp.gravity.ISupport;

import java.util.List;

@SuppressWarnings("Guava")
public class EntityGravFallingBlock extends EntityFallingBlock
{
	public static final double COLLISION_Y_CONST = 0.009999999776482582D;
	public IBlockState blockState;
	public final List<Entity> collidedWith = Lists.newArrayList();
	public static final DataParameter<Optional<IBlockState>> FALLING_BLOCK_SYNC = EntityDataManager.createKey(EntityGravFallingBlock.class, DataSerializers.OPTIONAL_BLOCK_STATE);
	
	public EntityGravFallingBlock(World worldIn)
	{
		super(worldIn);
	}
	
	@Override
	protected void entityInit()
    {
		super.entityInit();
        this.dataManager.register(FALLING_BLOCK_SYNC, Optional.absent());
    }
	
	public void setFallingBlock(IBlockState state)
    {
        this.dataManager.set(FALLING_BLOCK_SYNC, state == null ? Optional.absent() : Optional.of(state));
    }

    @SideOnly(Side.CLIENT)
    public IBlockState getFallingBlock()
    {
        return this.dataManager.get(FALLING_BLOCK_SYNC).orNull();
    }
	
	public EntityGravFallingBlock(World worldIn, double x, double y, double z, IBlockState fallingBlockState)
	{
		super(worldIn, x, y, z, fallingBlockState);
		this.blockState = fallingBlockState;
	}
	
	@Override
	public IBlockState getBlock()
	{
		return this.blockState;
	}

	@SuppressWarnings("RedundantCast")
	@Override
	public void onUpdate()
    {
		if (!this.world.isRemote)
		{
			this.setFallingBlock(this.blockState);
		}
		
		if (this.world.isRemote)
		{
			this.blockState = this.getFallingBlock();
		}
		
		if (this.blockState != null)
		{
			Block block = this.blockState.getBlock();
			if (block.isAssociatedBlock(Blocks.AIR) || this.blockState.getMaterial() == Material.AIR)
			{
				this.setDead();
				return;
			}
			
			updateMovement();
			checkFall();
			if (!this.isDead)
			{
				List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.posX - 0.5D, this.posY - 0.5D, this.posZ - 0.5D, this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D), e -> !this.collidedWith.contains(e));
				entities.forEach(this::attackEntityByFall);
			}
		}
    }
	
	public void attackEntityByFall(EntityLivingBase e)
	{
		this.collidedWith.add(e);
		if (this.blockState.getBlock() instanceof IGravitySusceptible) //Should always be true!
		{
			IGravitySusceptible gravityManager = ((IGravitySusceptible)this.blockState.getBlock());
			gravityManager.onCollided(e, this);
		}
	}

	public void checkFall()
	{
		BlockPos pos = new BlockPos(this);
		IBlockState stateSelf = this.world.getBlockState(pos);
		if (!stateSelf.getBlock().isAir(stateSelf, this.world, pos) && stateSelf.isOpaqueCube())
		{
			this.onGround = false;
			this.noClip = true;
			this.move(MoverType.SELF, 0, 1, 0);
			this.noClip = false;
			return;
		}
		
		
		if (!this.world.isRemote)
        {
			if (this.onGround)
			{
				BlockPos on = new BlockPos(this.posX, this.posY - COLLISION_Y_CONST, this.posZ);
				IBlockState state = this.world.getBlockState(on);
				if (this.blockState.getBlock() instanceof IGravitySusceptible) //Should always be true!
				{
					IGravitySusceptible gravityManager = ((IGravitySusceptible)this.blockState.getBlock());
					if (gravityManager.destroysNonOpaqueBlocksOnImpact() && !state.isOpaqueCube() && !(state.getBlock() instanceof ISupport))
					{
						this.world.setBlockToAir(on);
						// TODO destruction effects
						this.onGround = false;
					}
					else
					{
						gravityManager.onFall(this.world, pos);
						if (!gravityManager.breaksOnFall())
						{
						    if (this.world.getBlockState(pos).getBlock() instanceof ISupport)
                            {
                                pos = pos.up();
                            }

							this.world.setBlockState(pos, this.blockState);
						}
						
						this.setDead();
					}
				}
			}
        }
	}

	public void updateMovement()
	{
		this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.03999999910593033D;
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;
        
        if (!this.onGround && this.world.isRemote)
        {
        	this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
            this.motionY *= -0.5D;
        }
	}

	@Override
	public void fall(float distance, float damageMultiplier)
	{
		
	}

	@Override
	public void setDead()
	{
		super.setDead();
		this.collidedWith.clear();
	}
}
