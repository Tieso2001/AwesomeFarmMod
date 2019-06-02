package com.tieso2001.boneappletea.event;

import com.tieso2001.boneappletea.BoneAppleTea;
import com.tieso2001.boneappletea.block.*;
import com.tieso2001.boneappletea.block.fluids.BlockFluidHoppedWort;
import com.tieso2001.boneappletea.block.fluids.BlockFluidSweetWort;
import com.tieso2001.boneappletea.init.ModBlocks;
import com.tieso2001.boneappletea.init.ModFluids;
import com.tieso2001.boneappletea.item.ItemMortarAndPestle;
import com.tieso2001.boneappletea.tile.TileStockPot;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = BoneAppleTea.MODID)
public class EventBusSubscriber
{
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        FluidRegistry.registerFluid(ModFluids.HOPPED_WORT);
        FluidRegistry.registerFluid(ModFluids.SWEET_WORT);

        FluidRegistry.addBucketForFluid(ModFluids.HOPPED_WORT);
        FluidRegistry.addBucketForFluid(ModFluids.SWEET_WORT);

        final Block[] blocks = {
                new BlockBarley().setRegistryName("barley").setTranslationKey(BoneAppleTea.MODID + "." + "barley"),
                new BlockCorn().setRegistryName("corn").setTranslationKey(BoneAppleTea.MODID + "." + "corn"),
                new BlockHops().setRegistryName("hops").setTranslationKey(BoneAppleTea.MODID + "." + "hops"),
                new BlockStockPot().setRegistryName("stock_pot").setTranslationKey(BoneAppleTea.MODID + "." + "stock_pot").setCreativeTab(BoneAppleTea.TAB_BONE_APPLE_TEA),
                new BlockFluidHoppedWort().setRegistryName("hopped_wort").setTranslationKey(BoneAppleTea.MODID + "." + "hopped_wort"),
                new BlockFluidSweetWort().setRegistryName("sweet_wort").setTranslationKey(BoneAppleTea.MODID + "." + "sweet_wort")
        };

        event.getRegistry().registerAll(blocks);
        GameRegistry.registerTileEntity(TileStockPot.class, BoneAppleTea.MODID + "_stock_pot");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        final Item[] items = {
                new Item().setRegistryName("barley").setTranslationKey(BoneAppleTea.MODID + "." + "barley").setCreativeTab(BoneAppleTea.TAB_BONE_APPLE_TEA),
                new Item().setRegistryName("barley_grains").setTranslationKey(BoneAppleTea.MODID + "." + "barley_grains").setCreativeTab(BoneAppleTea.TAB_BONE_APPLE_TEA),
                new Item().setRegistryName("barley_malt").setTranslationKey(BoneAppleTea.MODID + "." + "barley_malt").setCreativeTab(BoneAppleTea.TAB_BONE_APPLE_TEA),
                new Item().setRegistryName("barley_malt_crushed").setTranslationKey(BoneAppleTea.MODID + "." + "barley_malt_crushed").setCreativeTab(BoneAppleTea.TAB_BONE_APPLE_TEA),
                new ItemSeeds(ModBlocks.BARLEY, Blocks.FARMLAND).setRegistryName("barley_seeds").setTranslationKey(BoneAppleTea.MODID + "." + "barley_seeds").setCreativeTab(BoneAppleTea.TAB_BONE_APPLE_TEA),
                new ItemFood(1, 0.6F,false).setRegistryName("corn").setTranslationKey(BoneAppleTea.MODID + "." + "corn").setCreativeTab(BoneAppleTea.TAB_BONE_APPLE_TEA),
                new ItemSeedFood(1, 0.3F, ModBlocks.CORN, Blocks.FARMLAND).setRegistryName("corn_kernels").setTranslationKey(BoneAppleTea.MODID + "." + "corn_kernels").setCreativeTab(BoneAppleTea.TAB_BONE_APPLE_TEA),
                new Item().setRegistryName("hops").setTranslationKey(BoneAppleTea.MODID + "." + "hops").setCreativeTab(BoneAppleTea.TAB_BONE_APPLE_TEA),
                new ItemSeeds(ModBlocks.HOPS, Blocks.FARMLAND).setRegistryName("hops_seeds").setTranslationKey(BoneAppleTea.MODID + "." + "hops_seeds").setCreativeTab(BoneAppleTea.TAB_BONE_APPLE_TEA),
                new ItemMortarAndPestle().setRegistryName("mortar_and_pestle").setTranslationKey(BoneAppleTea.MODID + "." + "mortar_and_pestle").setCreativeTab(BoneAppleTea.TAB_BONE_APPLE_TEA),
                new ItemFood(2, 0.6F, false).setRegistryName("popcorn").setTranslationKey(BoneAppleTea.MODID + "." + "popcorn").setCreativeTab(BoneAppleTea.TAB_BONE_APPLE_TEA),
                new ItemFood(4, 0.6F, false).setRegistryName("roasted_corn").setTranslationKey(BoneAppleTea.MODID + "." + "roasted_corn").setCreativeTab(BoneAppleTea.TAB_BONE_APPLE_TEA)
        };

        final Item[] itemBlocks = {
                new ItemBlock(ModBlocks.STOCK_POT).setRegistryName(ModBlocks.STOCK_POT.getRegistryName())
        };

        event.getRegistry().registerAll(items);
        event.getRegistry().registerAll(itemBlocks);
    }
}