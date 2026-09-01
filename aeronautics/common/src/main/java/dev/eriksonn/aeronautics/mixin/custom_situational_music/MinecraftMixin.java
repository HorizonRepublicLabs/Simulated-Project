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
	/// custom track is applied to the resolved music instead.
	@ModifyReturnValue(method = "getSituationalMusic", at = @At("RETURN"))
	private Music aeronautics$getSituationalMusic(Music music) {
		Music customMusic = CustomSituationalMusic.getSituationalMusic(this.level, this.player);
		return customMusic != null ? customMusic : music;
	}
}
