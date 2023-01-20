package github.jaffe2718.sniperrifle.entity;

import github.jaffe2718.sniperrifle.SniperRifle;
import github.jaffe2718.sniperrifle.register.SoundRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BulletEntity extends ThrownItemEntity {
    public BulletEntity(EntityType<? extends BulletEntity> entityType, World world) {
        super(entityType, world);
    }

    public BulletEntity(World world, LivingEntity owner) {
        super(SniperRifle.BULLET, owner, world);
    }

    public BulletEntity(World world, double x, double y, double z) {
        super(SniperRifle.BULLET, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.POLISHED_BLACKSTONE_BUTTON.asItem();
    }

    @Override
    protected void onEntityHit(EntityHitResult result) {
        super.onEntityHit(result);
        Entity target = result.getEntity();
        target.damage(DamageSource.thrownProjectile(this, this.getOwner()), 20.0F);
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {
        super.onBlockHit(result);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),  // 击中障碍物音效
                SoundRegister.BULLET_HIT, SoundCategory.VOICE, 1.0F, 1.0F);

    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        Vec3d hitPos = hitResult.getPos();
        if (this.getWorld() instanceof ServerWorld serverWorld) {                              // 击中成粒子
            Random rd = new Random();
            serverWorld.spawnParticles(ParticleTypes.CRIT,
                    hitPos.x, hitPos.y, hitPos.z,
                    15,
                    rd.nextDouble()-0.5D, rd.nextDouble()-0.5D, rd.nextDouble()-0.5D,
                    0.25D
            );
            this.world.sendEntityStatus(this, (byte) 3);
            this.discard();
        }
    }
}
