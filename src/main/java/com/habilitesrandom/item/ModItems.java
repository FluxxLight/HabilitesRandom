package com.habilitesrandom.item;
import com.habilitesrandom.HabilitesRandom;import net.minecraft.core.Registry;import net.minecraft.core.registries.*;import net.minecraft.resources.ResourceKey;import net.minecraft.world.item.*;
public final class ModItems {private ModItems(){} public static Item POWERFUL_FRUIT;
 public static void register(){ResourceKey<Item> key=ResourceKey.create(Registries.ITEM,HabilitesRandom.id("powerful_fruit"));POWERFUL_FRUIT=Registry.register(BuiltInRegistries.ITEM,key,new PowerfulFruitItem(new Item.Properties().setId(key).stacksTo(16).rarity(Rarity.EPIC)));}
}
