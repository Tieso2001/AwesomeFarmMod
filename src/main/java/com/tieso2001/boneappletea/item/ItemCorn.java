package com.tieso2001.boneappletea.item;

import net.minecraft.item.ItemFood;

public class ItemCorn extends ItemFood{

    public ItemCorn(String name, int amount, boolean isWolfFood) {
        super(amount, isWolfFood);
        setUnlocalizedName(name);
        setRegistryName(name);
    }

}