package com.tieso2001.boneappletea.block;

import com.tieso2001.boneappletea.BoneAppleTea;
import com.tieso2001.boneappletea.init.ModFluids;
import com.tieso2001.boneappletea.init.ModItems;
import com.tieso2001.boneappletea.tile.TileCauldron;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class BlockCauldron extends Block
{
    public BlockCauldron()
    {
        super(Material.IRON);
        this.setHardness(2.0F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.METAL);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote) return true;

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (!(tileEntity instanceof TileCauldron)) return false;

        if (playerIn.getHeldItem(hand).isItemEqual(new ItemStack(Items.GLASS_BOTTLE)) && ((TileCauldron) tileEntity).getFluidTank(1).getFluid() != null)
        {
            if (((TileCauldron) tileEntity).getFluidTank(1).getFluid().containsFluid(new FluidStack(ModFluids.BEER, 250)))
            {
                playerIn.getHeldItem(hand).shrink(1);
                playerIn.inventory.addItemStackToInventory(new ItemStack(ModItems.BOTTLE_BEER));
                ((TileCauldron) tileEntity).getFluidTank(1).drain(250, true);
                worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return true;
            }
        }
        if (playerIn.getHeldItem(hand).isItemEqual(new ItemStack(Items.GLASS_BOTTLE)) && ((TileCauldron) tileEntity).getFluidTank(0).getFluid() != null)
        {
            if (((TileCauldron) tileEntity).getFluidTank(0).getFluid().containsFluid(new FluidStack(ModFluids.BEER, 250)))
            {
                playerIn.getHeldItem(hand).shrink(1);
                playerIn.inventory.addItemStackToInventory(new ItemStack(ModItems.BOTTLE_BEER));
                ((TileCauldron) tileEntity).getFluidTank(0).drain(250, true);
                worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return true;
            }
        }
        if (FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, EnumFacing.DOWN)) return true;
        if (playerIn.getHeldItem(hand).isItemEqual(new ItemStack(ModItems.BOTTLE_BEER)))
        {
            if (((TileCauldron) tileEntity).getFluidTank(0).fillInternal(new FluidStack(ModFluids.BEER, 250), false) == 250)
            {
                playerIn.getHeldItem(hand).shrink(1);
                playerIn.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
                ((TileCauldron) tileEntity).getFluidTank(0).fillInternal(new FluidStack(ModFluids.BEER, 250), true);
                worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return true;
            }
        }
        if (FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, EnumFacing.UP)) return true;

        playerIn.openGui(BoneAppleTea.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileCauldron tileEntity = (TileCauldron) worldIn.getTileEntity(pos);
        if (tileEntity != null)
        {
            for (int i = 0; i < tileEntity.slots; i++)
            {
                if (tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(i).isEmpty()) return;
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(i));
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    // TileEntity

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileCauldron();
    }
}