package micdoodle8.mods.galacticraft.core.inventory;

import micdoodle8.mods.galacticraft.api.inventory.IInventoryGC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;

public class InventoryExtended implements IInventoryGC
{
    public ItemStack[] inventoryStacks = new ItemStack[11];

    @Override
    public int getSizeInventory()
    {
        return this.inventoryStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int i)
    {
        return this.inventoryStacks[i];
    }

    @Override
    public ItemStack decrStackSize(int i, int j)
    {
        if (this.inventoryStacks[i] != null)
        {
            ItemStack var3;

            if (this.inventoryStacks[i].getCount() <= j)
            {
                var3 = this.inventoryStacks[i];
                this.inventoryStacks[i] = null;
                return var3;
            }
            else
            {
                var3 = this.inventoryStacks[i].splitStack(j);

                if (this.inventoryStacks[i].getCount() == 0)
                {
                    this.inventoryStacks[i] = null;
                }

                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int i)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack)
    {
        this.inventoryStacks[i] = itemstack;

        if (itemstack != null && itemstack.getCount() > this.getInventoryStackLimit())
        {
            itemstack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public String getName()
    {
        return "Galacticraft Player Inventory";
    }

    @Override
    public boolean hasCustomName()
    {
        return true;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void markDirty()
    {

    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer entityplayer)
    {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player)
    {

    }

    @Override
    public void closeInventory(EntityPlayer player)
    {

    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return false;
    }

    @Override
    public void dropExtendedItems(EntityPlayer player)
    {
        for (int i = 0; i < this.inventoryStacks.length; i++)
        {
            ItemStack stack = this.inventoryStacks[i];

            if (stack != null)
            {
                player.dropItem(stack, true);
            }

            this.inventoryStacks[i] = null;
        }
    }

    // Backwards compatibility for old inventory
    public void readFromNBTOld(NBTTagList par1NBTTagList)
    {
        this.inventoryStacks = new ItemStack[11];

        for (int i = 0; i < par1NBTTagList.tagCount(); ++i)
        {
            final NBTTagCompound nbttagcompound = par1NBTTagList.getCompoundTagAt(i);
            final int j = nbttagcompound.getByte("Slot") & 255;
            final ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

            if (itemstack != null)
            {
                if (j >= 200 && j < this.inventoryStacks.length + 200 - 1)
                {
                    this.inventoryStacks[j - 200] = itemstack;
                }
            }
        }
    }

    public void readFromNBT(NBTTagList tagList)
    {
        this.inventoryStacks = new ItemStack[11];

        for (int i = 0; i < tagList.tagCount(); ++i)
        {
            final NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            final int j = nbttagcompound.getByte("Slot") & 255;
            final ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

            if (itemstack != null)
            {
                this.inventoryStacks[j] = itemstack;
            }
        }
    }

    public NBTTagList writeToNBT(NBTTagList tagList)
    {
        NBTTagCompound nbttagcompound;

        for (int i = 0; i < this.inventoryStacks.length; ++i)
        {
            if (this.inventoryStacks[i] != null)
            {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                this.inventoryStacks[i].writeToNBT(nbttagcompound);
                tagList.appendTag(nbttagcompound);
            }
        }

        return tagList;
    }

    @Override
    public void copyInventory(IInventoryGC par1InventoryPlayer)
    {
        InventoryExtended toCopy = (InventoryExtended) par1InventoryPlayer;
        for (int i = 0; i < this.inventoryStacks.length; ++i)
        {
            this.inventoryStacks[i] = ItemStack.copyItemStack(toCopy.inventoryStacks[i]);
        }
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {

    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear()
    {

    }

    @Override
    public ITextComponent getDisplayName()
    {
        return null;
    }
}
