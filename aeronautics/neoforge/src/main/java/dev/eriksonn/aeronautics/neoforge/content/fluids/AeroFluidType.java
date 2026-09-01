package dev.eriksonn.aeronautics.neoforge.content.fluids;

import com.tterrag.registrate.builders.FluidBuilder;
import net.createmod.catnip.api.theme.Color;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.Supplier;

/// 26.2 drives fluid textures from data and no longer lets a fluid type
/// implement the client extensions itself, so the type carries only its
/// properties and hands the fog hooks out through clientExtensions().
public abstract class AeroFluidType extends FluidType {
	private Vector3f fogColor;
	private Supplier<Float> fogDistance;

	public AeroFluidType(Properties properties) {
		super(properties);
	}

	public static FluidBuilder.FluidTypeFactory create(int fogColor, Supplier<Float> fogDistance, Factory factory) {
		return p -> {
			AeroFluidType fluidType = factory.create(p);
			fluidType.fogColor = new Color(fogColor, false).asVectorF();
			fluidType.fogDistance = fogDistance;
			return fluidType;
		};
	}

	public interface Factory {
		AeroFluidType create(Properties properties);
	}

	/// Registered through RegisterClientExtensionsEvent rather than implemented here.
	public IClientFluidTypeExtensions clientExtensions() {
		return new IClientFluidTypeExtensions() {

			@Override
			public void modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance,
			                           float darkenWorldAmount, Vector4f fluidFogColor) {
				Vector3f customFogColor = AeroFluidType.this.getCustomFogColor();
				if (customFogColor != null) {
					fluidFogColor.set(customFogColor.x, customFogColor.y, customFogColor.z, fluidFogColor.w);
				}
			}

			@Override
			public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance,
			                            float partialTick, FogData fogData) {
				float modifier = AeroFluidType.this.getFogDistanceModifier();
				if (modifier == 1.0f) {
					return;
				}
				fogData.environmentalStart = -8;
				fogData.environmentalEnd = 96.0f * modifier;
			}

		};
	}

	public Vector3f getCustomFogColor() {
		return this.fogColor;
	}

	public float getFogDistanceModifier() {
		return this.fogDistance.get();
	}
}
