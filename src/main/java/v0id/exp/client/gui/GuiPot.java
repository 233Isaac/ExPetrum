package v0id.exp.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import v0id.api.exp.data.ExPTextures;
import v0id.exp.container.ContainerPot;

public class GuiPot extends GuiContainer
{
    public ItemStackHandler cap;
    public ItemStack pot;

    public GuiPot(InventoryPlayer playerInv, IItemHandler cap)
    {
        super(new ContainerPot(playerInv, cap));
    }

    public GuiPot(InventoryPlayer playerInv, ItemStack pot)
    {
        super(new ContainerPot(playerInv, pot));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(((ContainerPot)this.inventorySlots).slots == 4 ? ExPTextures.guiInv4 : ExPTextures.guiInv1);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
