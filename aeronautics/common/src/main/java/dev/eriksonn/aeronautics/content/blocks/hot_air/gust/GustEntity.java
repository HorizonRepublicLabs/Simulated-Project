package dev.eriksonn.aeronautics.content.blocks.hot_air.gust;

import dev.eriksonn.aeronautics.index.AeroEntityTypes;
import dev.eriksonn.aeronautics.index.AeroSoundEvents;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.util.SableBufferUtils;
import dev.ryanhcode.sable.util.SableNBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class GustEntity extends Entity implements IEntityWithComplexSpawn {
    private final Quaterniond orientation = new Quaterniond();
    private boolean spawnedInitialBurst = false;

    public static void addGust(final Level level, final BlockPos pos, final Direction direction) {
        final Quaterniond orientation = new Quaterniond(direction.getRotation());

        final GustEntity gust = new GustEntity(AeroEntityTypes.GUST.get(), level, orientation);
        gust.setPos(Vec3.atCenterOf(pos));

        level.addFreshEntity(gust);
    }

    public GustEntity(final EntityType<?> entityType, final Level level) {
        this(entityType, level, JOMLConversion.QUAT_IDENTITY);
    }

    public GustEntity(final EntityType<?> entityType, final Level level, final Quaterniondc orientation) {
        super(entityType, level);
        this.orientation.set(orientation);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.spawnClientEffects();
        } else {
            final SubLevel subLevel = Sable.HELPER.getContaining(this);

            // FIXME: this should be moved to something physics-tick dependant
            //  but we don't have a great way to do that for entities when this is
            //  being written

            if (subLevel instanceof final ServerSubLevel serverSubLevel) {
                final Vector3d forceDir = this.orientation.transform(new Vector3d(0.0, 1.0, 0.0))
                        .mul(-3.0);

                RigidBodyHandle.of(serverSubLevel)
                        .applyImpulseAtPoint(this.position(), JOMLConversion.toMojang(forceDir));
            }

            if (this.tickCount > 5)
                this.remove(RemovalReason.DISCARDED);
        }
    }

    private void spawnClientEffects() {
        final Level level = this.level();

        if (!this.spawnedInitialBurst) {
            final Vec3 soundPos = this.position();
            level.playLocalSound(soundPos.x, soundPos.y, soundPos.z, AeroSoundEvents.GUST.event(), SoundSource.BLOCKS, 0.65f, 0.35f, false);

            // The gust and air poof particles belong to the staged-out particle layer.

            this.spawnedInitialBurst = true;
        }
    }

    /// 26.2 makes Entity#hurtServer abstract; a gust is not damageable.
    @Override
    public boolean hurtServer(final @NotNull ServerLevel level, final @NotNull DamageSource source, final float amount) {
        return false;
    }

    @Override
    public @NotNull PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE_ENTITY;
    }

    /// 26.2 sealed the no argument makeBoundingBox and passes the position in.
    @Override
    protected @NotNull AABB makeBoundingBox(final @NotNull Vec3 pos) {
        final AABB boundingBox = this.getDimensions(this.getPose()).makeBoundingBox(pos);
        return boundingBox.move(0, -boundingBox.getYsize() / 2.0, 0);
    }

    @Override
    protected void defineSynchedData(final @NotNull SynchedEntityData.Builder builder) {

    }

    // 26.2 persists entities through ValueInput / ValueOutput. The 1.21 code had
    // the read and write bodies the wrong way round, so they are also swapped back.
    @Override
    protected void readAdditionalSaveData(final @NotNull ValueInput input) {
        input.read("GustOrientation", CompoundTag.CODEC)
                .ifPresent(tag -> this.orientation.set(SableNBTUtils.readQuaternion(tag)));
    }

    @Override
    protected void addAdditionalSaveData(final @NotNull ValueOutput output) {
        output.store("GustOrientation", CompoundTag.CODEC, SableNBTUtils.writeQuaternion(this.orientation));
    }

    @Override
    public void writeSpawnData(final @NotNull RegistryFriendlyByteBuf buffer) {
        SableBufferUtils.write(buffer, this.orientation);
    }

    @Override
    public void readSpawnData(final @NotNull RegistryFriendlyByteBuf buffer) {
        SableBufferUtils.read(buffer, this.orientation);
    }
}
