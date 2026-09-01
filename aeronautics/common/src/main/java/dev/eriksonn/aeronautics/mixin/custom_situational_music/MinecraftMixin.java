package dev.eriksonn.aeronautics.mixin.custom_situational_music;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.eriksonn.aeronautics.api.CustomSituationalMusic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Shadow @Nullable public LocalPlayer player;

	@Shadow @Nullable public ClientLevel level;

	/// 26.2 pulls the ambient track from an environment attribute instead of
	/// reading Musics.GAME, so there is no field read left to wrap and the
	/// custom track is applied to the resolved music instead. That widens where
	/// this runs: the old field read only happened in world, while the return
	/// value is also produced on the menu screen, where there is no level or
	/// player to test a condition against.
	@ModifyReturnValue(method = "getSituationalMusic", at = @At("RETURN"))
	private Music aeronautics$getSituationalMusic(Music music) {
		if (this.level == null || this.player == null) {
			return music;
		}

		Music customMusic = CustomSituationalMusic.getSituationalMusic(this.level, this.player);
		return customMusic != null ? customMusic : music;
	}
}
