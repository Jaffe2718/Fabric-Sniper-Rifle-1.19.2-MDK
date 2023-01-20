package github.jaffe2718.sniperrifle.item;


import github.jaffe2718.sniperrifle.entity.BulletEntity;
import github.jaffe2718.sniperrifle.register.SoundRegister;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;


public class SniperRifleItem extends SpyglassItem {

    public SniperRifleItem(Settings settings) {
        super(settings);
    }

    public static boolean isCharged(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && nbtCompound.getBoolean("Charged");
    }

    public static void setCharged(ItemStack stack, boolean charged) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putBoolean("Charged", charged);
    }

    public static boolean isLoading(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && nbtCompound.getBoolean("Loading");
    }

    public static void setLoading(ItemStack stack, boolean loading) {
        NbtCompound nbtCompound = stack.getNbt();
        nbtCompound.putBoolean("Loading", loading);
    }

    public static boolean isWatching(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && nbtCompound.getBoolean("Watching");
    }

    public static void setWatching(ItemStack stack, boolean watching) {
        NbtCompound nbtCompound = stack.getNbt();
        nbtCompound.putBoolean("Watching", watching);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        setLoading(user.getStackInHand(hand),
                !isCharged(user.getStackInHand(hand)) &&
                    (user.getInventory().contains(new ItemStack(Items.GUNPOWDER)) || user.isCreative()));
        setWatching(user.getStackInHand(hand), false);
        if (isCharged(user.getStackInHand(hand))) {
            // 开镜
            setWatching(user.getStackInHand(hand), true);
            user.playSound(SoundEvents.ITEM_SPYGLASS_USE, 1.0F, 1.0F);
            user.incrementStat(Stats.USED.getOrCreateStat(super.asItem()));
            return ItemUsage.consumeHeldItem(world, user, hand);
        } else if (!(user.getInventory().contains(new ItemStack(Items.GUNPOWDER)) || user.isCreative())) {
            // 没有子弹
            return TypedActionResult.fail(user.getStackInHand(hand));
        } else {
            // 开始装子弹
            user.playSound(SoundRegister.SNIPER_RIFLE_LOADING, 1.0F, 1.0F);
            user.setCurrentHand(hand);
            return TypedActionResult.consume(user.getStackInHand(hand)); //ItemUsage.consumeHeldItem(world, user, hand);
            //return TypedActionResult.consume(user.getStackInHand(hand));
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (isCharged(stack)) {                // 开火
            if (!world.isClient) {             // Server端生成实体
                if (user instanceof ServerPlayerEntity player && !player.isCreative()) stack.damage(1, Random.create(), player);
                BulletEntity bullet = new BulletEntity(world, user);
                Vec3d pos = user.getEyePos();
                bullet.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 8.5F, 0F);
                bullet.setPos(pos.x, pos.y, pos.z);
                world.spawnEntity(bullet);     // 生成实体
            } else {                           // Client端生成粒子效果
                Vec3d muzzle = user.getEyePos().add(user.getRotationVec(1.0F));
                world.addParticle(ParticleTypes.LARGE_SMOKE,
                        muzzle.x, muzzle.y, muzzle.z, 0.0D, 0.0D, 0.0D);
            }
            world.playSound(user.getX(), user.getY(), user.getZ(),  // 开火音效
                    SoundRegister.SNIPER_RIFLE_FIRE, SoundCategory.PLAYERS,
                    1.5F, 1.0F, true);
            user.setPitch(user.getPitch() - 6.0F);                                         // 垂直后坐力
            user.setHeadYaw(user.getYaw() + (new java.util.Random()).nextFloat() * 2 - 1.0F);  // 水平后坐力
            setCharged(stack, false);
            setLoading(stack, false);
            setWatching(stack, false);
            if (stack.getDamage() >= this.getMaxDamage() && user instanceof PlayerEntity player){    // 耐久用完物品损坏
                player.getInventory().remove(
                        (stack_) -> {return stack_.getItem()==this && stack_.getDamage() >= this.getMaxDamage(); },
                        1,
                        player.getInventory());
            }
        } else if (isLoading(stack) && remainingUseTicks <= 1175) {
            if (user instanceof PlayerEntity player && !player.isCreative()) {   // 非创造玩家
                player.getInventory().remove(
                        (stuck_) -> {return stuck_.getItem().equals(Items.GUNPOWDER); },
                        1,
                        player.getInventory()
                ); // 消耗火药
            }
            setCharged(stack,true);
            setLoading(stack, false);
            setWatching(stack, false);
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {  // 正在使用
        if (isLoading(stack) && remainingUseTicks == 1175) {  // 没有用
            user.playSound(SoundRegister.SNIPER_RIFLE_LOADED, 1.0F, 1.0F);
        }
        if (remainingUseTicks <=0) setWatching(stack, false);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return (isCharged(stack) && !isLoading(stack)) ? UseAction.SPYGLASS : UseAction.BOW;
    }
}
