package dev.eriksonn.aeronautics.index;

import com.google.common.collect.Maps;
import dev.eriksonn.aeronautics.Aeronautics;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

/// 26.2 makes ArmorMaterial a plain record instead of a registry entry, and the
/// armor texture now comes from an equipment asset rather than a layer list.
public class AeroArmorMaterials {

	public static final ResourceKey<EquipmentAsset> AVIATORS_GOGGLES_ASSET =
			ResourceKey.create(EquipmentAssets.ROOT_ID, Aeronautics.path("aviators_goggles"));

	public static final ArmorMaterial AVIATORS_GOGGLES = new ArmorMaterial(
			5, defense(0, 0, 0, 1, 0), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F,
			ItemTags.REPAIRS_LEATHER_ARMOR, AVIATORS_GOGGLES_ASSET);

	private static Map<ArmorType, Integer> defense(final int boots, final int legs, final int chest,
	                                               final int helmet, final int body) {
		return Maps.newEnumMap(Map.of(ArmorType.BOOTS, boots, ArmorType.LEGGINGS, legs,
				ArmorType.CHESTPLATE, chest, ArmorType.HELMET, helmet, ArmorType.BODY, body));
	}

	public static void init() {}
}
