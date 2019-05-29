package com.tieso2001.boneappletea.tile;

import com.tieso2001.boneappletea.recipe.RecipeStockPot;
import com.tieso2001.boneappletea.recipe.RecipeStockPotRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileStockPot extends TileEntity implements ITickable, IInventory
{
    public int SLOTS_INPUT = 2;
    public int SLOTS_OUTPUT = 1;
    public int SLOTS = SLOTS_INPUT + SLOTS_OUTPUT;
    public int boilTime = 0;
    public int maxBoilTime = 200;
    public boolean isActive = false;

    private ItemStackHandler inputItemStackHandler = new ItemStackHandler(SLOTS_INPUT)
    {
        @Override
        protected void onContentsChanged(int slot) { markDirty(); }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack)
        {
            for (RecipeStockPot recipe : RecipeStockPotRegistry.getRecipeMap().values())
            {
                if (stack.getItem() == recipe.getInputItemFirst().getItem() || stack.getItem() == recipe.getInputItemSecond().getItem() || stack.isEmpty()) return true;
            }
            return false;
        }
    };

    private ItemStackHandler outputItemStackHandler = new ItemStackHandler(SLOTS_OUTPUT)
    {
        @Override
        protected void onContentsChanged(int slot) { markDirty(); }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) { return false; }
    };

    private CombinedInvWrapper combinedHandler = new CombinedInvWrapper(inputItemStackHandler, outputItemStackHandler);

    private FluidTank fluidTank = new FluidTank(1000)
    {
        @Override
        protected void onContentsChanged() { markDirty(); }
    };

    public FluidTank getFluidTank()
    {
        return fluidTank;
    }

    @Override
    public void update()
    {
        FluidStack fluidStack = fluidTank.getFluid();
        ItemStack itemFirst = inputItemStackHandler.getStackInSlot(0);
        ItemStack itemSecond = inputItemStackHandler.getStackInSlot(1);
        ItemStack itemOutput = outputItemStackHandler.getStackInSlot(0);

        if (world.getBlockState(pos.down()).getBlock() != Blocks.FIRE)
        {
            boilTime = 0;
            return;
        }

        if (fluidStack == null)
        {
            boilTime = 0;
            return;
        }

        RecipeStockPot recipe = RecipeStockPotRegistry.getRecipe(fluidStack, itemFirst, itemSecond);

        if (recipe == null || fluidStack.amount < recipe.getInputFluid().amount || (!itemOutput.isEmpty() && !itemOutput.isItemEqual(recipe.getOutputItem())) || (itemOutput.getCount() + recipe.getOutputItem().getCount() > 64))
        {
            boilTime = 0;
            return;
        }

        if (boilTime == 0)
        {
            boilTime = recipe.getBoilTime();
            maxBoilTime = recipe.getBoilTime();
        }

        if (boilTime == 1)
        {
            boilTime--;

            if (recipe.getOutputFluid() == null) fluidTank.setFluid(null);
            else fluidTank.setFluid(new FluidStack(recipe.getOutputFluid().copy(), fluidStack.amount));

            if (itemFirst != ItemStack.EMPTY) inputItemStackHandler.getStackInSlot(0).shrink(1);
            if (itemSecond != ItemStack.EMPTY) inputItemStackHandler.getStackInSlot(1).shrink(1);

            if (!recipe.getOutputItem().isEmpty())
            {
                if (itemOutput.isEmpty()) outputItemStackHandler.setStackInSlot(0, recipe.getOutputItem().copy());
                else if (itemOutput.isItemEqual(recipe.getOutputItem())) outputItemStackHandler.getStackInSlot(0).setCount(itemOutput.getCount() + recipe.getOutputItem().getCount());
            }

            markDirty();
        }

        if (boilTime > 1)
        {
            boilTime--;
            isActive = true;
        }
        else isActive = false;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if (compound.hasKey("itemsInput")) inputItemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("itemsInput"));
        if (compound.hasKey("itemsOutput")) outputItemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("itemsOutput"));
        if (compound.hasKey("fluids")) fluidTank.readFromNBT(compound.getCompoundTag("fluids"));
        boilTime = compound.getInteger("boilTime");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag("itemsInput", inputItemStackHandler.serializeNBT());
        compound.setTag("itemsOutput", outputItemStackHandler.serializeNBT());
        NBTTagCompound fluidTankNBT = new NBTTagCompound();
        fluidTank.writeToNBT(fluidTankNBT);
        compound.setTag("fluids", fluidTankNBT);
        compound.setInteger("boilTime", boilTime);
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(combinedHandler);
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidTank);
        return super.getCapability(capability, facing);
    }

    // IInventory

    @Override
    public int getSizeInventory()
    {
        return SLOTS;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return combinedHandler.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        combinedHandler.getStackInSlot(index).shrink(count);
        markDirty();
        return combinedHandler.getStackInSlot(index);
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        combinedHandler.setStackInSlot(index, ItemStack.EMPTY);
        markDirty();
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        combinedHandler.setStackInSlot(index, stack);
        markDirty();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        if (this.world.getTileEntity(this.pos) != this)
        {
            return false;
        }
        else {
            return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
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
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return combinedHandler.isItemValid(index, stack);
    }

    @Override
    public void clear()
    {
        for (int i = 0; i < SLOTS; i++) {
            combinedHandler.setStackInSlot(i, ItemStack.EMPTY);
            markDirty();
        }
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {

    }

    @Override
    public String getName()
    {
        return "container.stock_pot";
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }
}