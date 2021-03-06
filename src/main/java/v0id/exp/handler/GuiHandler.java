package v0id.exp.handler;

import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import v0id.api.exp.data.ExPItems;
import v0id.exp.client.gui.*;
import v0id.exp.container.*;
import v0id.exp.item.ItemBackpack;
import v0id.exp.tile.*;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler
{
    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID)
        {
            case -1:
            {
                return new GuiCrafting(player.inventory, world, new BlockPos(x, y, z));
            }

            case 0:
            {
                return new GuiCampfire(player.inventory, (TileCampfire) tile);
            }

            case 1:
            {
                return new GuiPotteryStation(player.inventory, (TilePotteryStation) tile);
            }

            case 2:
            {
                ItemStack is = player.getHeldItem(EnumHand.MAIN_HAND);
                if (is.getItem() != ExPItems.pottery || !is.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
                {
                    is = player.getHeldItem(EnumHand.OFF_HAND);
                }

                return new GuiPot(player.inventory, is);
            }

            case 3:
            {
                return new GuiPot(player.inventory, tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
            }

            case 4:
            {
                return new GuiCrate(player.inventory, (TileCrate) tile);
            }

            case 5:
            {
                return new GuiForge(player.inventory, (TileForge) tile);
            }

            case 6:
            {
                return new GuiQuern(player.inventory, (TileQuern) tile);
            }

            case 7:
            {
                return new GuiAnvil(player.inventory, (TileAnvil) tile);
            }

            case 8:
            {
                return new GuiCrucible(player.inventory, (TileCrucible) tile);
            }

            case 9:
            {
                return new GuiNestingBox(player.inventory, (TileNestingBox) tile);
            }

            case 10:
            {
                return new GuiBarrel(player.inventory, (TileBarrel) tile);
            }

            case 11:
            {
                return new GuiBloomery(player.inventory, (TileBloomery) tile);
            }

            case 12:
            {
                return new GuiPress(player.inventory, (TileFruitPress) tile);
            }

            case 13:
            {
                return new GuiChest(player.inventory, (TileChest) tile);
            }

            case 14:
            {
                return new GuiMechanicalQuern(player.inventory, (TileMechanicalQuern) tile);
            }

            case 15:
            {
                return new GuiMechanicalPotteryStation(player.inventory, (TileMechanicalPotteryStation) tile);
            }

            case 16:
            {
                return new GuiBlastFurnace(player.inventory, (TileBlastFurnace) tile);
            }

            case 17:
            {
                ItemStack is = player.getHeldItem(EnumHand.MAIN_HAND);
                if (!(is.getItem() instanceof ItemBackpack) || !is.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
                {
                    is = player.getHeldItem(EnumHand.OFF_HAND);
                }

                return new GuiBackpack(player.inventory, is);
            }

            case 18:
            {
                return new GuiOven(player.inventory, (TileOven) tile);
            }

            default:
            {
                return null;
            }
        }
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID)
        {
            case -1:
            {
                return new ContainerCraftingTable(player.inventory, world, new BlockPos(x, y, z));
            }

            case 0:
            {
                return new ContainerCampfire(player.inventory, (TileCampfire) tile);
            }

            case 1:
            {
                return new ContainerPotteryStation(player.inventory, (TilePotteryStation) tile);
            }

            case 2:
            {
                ItemStack is = player.getHeldItem(EnumHand.MAIN_HAND);
                if (is.getItem() != ExPItems.pottery || !is.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
                {
                    is = player.getHeldItem(EnumHand.OFF_HAND);
                }

                return new ContainerPot(player.inventory, is);
            }

            case 3:
            {
                return new ContainerPot(player.inventory, tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
            }

            case 4:
            {
                return new ContainerCrate(player.inventory, (TileCrate) tile);
            }

            case 5:
            {
                return new ContainerForge(player.inventory, (TileForge) tile);
            }

            case 6:
            {
                return new ContainerQuern(player.inventory, (TileQuern) tile);
            }

            case 7:
            {
                return new ContainerAnvil(player.inventory, (TileAnvil) tile);
            }

            case 8:
            {
                return new ContainerCrucible(player.inventory, (TileCrucible) tile);
            }

            case 9:
            {
                return new ContainerNestingBox(player.inventory, (TileNestingBox) tile);
            }

            case 10:
            {
                return new ContainerBarrel(player.inventory, (TileBarrel) tile);
            }

            case 11:
            {
                return new ContainerBloomery(player.inventory, (TileBloomery) tile);
            }

            case 12:
            {
                return new ContainerPress(player.inventory, (TileFruitPress) tile);
            }

            case 13:
            {
                return new ContainerChest(player.inventory, (TileChest) tile);
            }

            case 14:
            {
                return new ContainerMechanicalQuern(player.inventory, (TileMechanicalQuern) tile);
            }

            case 15:
            {
                return new ContainerMechanicalPotteryStation(player.inventory, (TileMechanicalPotteryStation) tile);
            }

            case 16:
            {
                return new ContainerBlastFurnace(player.inventory, (TileBlastFurnace) tile);
            }

            case 17:
            {
                ItemStack is = player.getHeldItem(EnumHand.MAIN_HAND);
                if (!(is.getItem() instanceof ItemBackpack) || !is.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
                {
                    is = player.getHeldItem(EnumHand.OFF_HAND);
                }

                return new ContainerBackpack(player.inventory, is);
            }

            case 18:
            {
                return new ContainerOven(player.inventory, (TileOven) tile);
            }

            default:
            {
                return null;
            }
        }
    }
}
