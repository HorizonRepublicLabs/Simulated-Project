package dev.eriksonn.aeronautics.api.levitite_blend_crystallization;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class CrystallizationWorldSaveData extends SavedData {
	public static final String ID = "aeronautics_levitite_data";
	
	Level level;
	
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
		ListTag list = new ListTag();
		LevititeCrystallizerManager.saveData(list, level);
		tag.put("Levitite Manager Data", list);
		
		return tag;
	}

	public static CrystallizationWorldSaveData load(ServerLevel level, CompoundTag tag, final HolderLookup.Provider registries) {
		CrystallizationWorldSaveData data = new CrystallizationWorldSaveData();
		data.level = level;

		LevititeCrystallizerManager.loadData(tag, level);

		return data;
	}

	/// 26.2 describes saved data with a codec backed type rather than a factory.
	public static CrystallizationWorldSaveData get(ServerLevel level) {
		CrystallizationWorldSaveData data = level.getDataStorage().computeIfAbsent(new SavedDataType<>(
				Identifier.withDefaultNamespace(ID),
				CrystallizationWorldSaveData::new,
				CompoundTag.CODEC.xmap(
						tag -> load(level, tag, level.registryAccess()),
						d -> d.save(new CompoundTag(), level.registryAccess())
				),
				DataFixTypes.LEVEL
		));

		data.level = level;
		return data;
	}
}
