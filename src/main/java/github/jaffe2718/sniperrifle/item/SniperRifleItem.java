package github.jaffe2718.sniperrifle.item;


import github.jaffe2718.sniperrifle.entity.BulletEntity;
import github.jaffe2718.sniperrifle.register.ItemRegister;
import github.jaffe2718.sniperrifle.register.SoundRegister;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.SpyglassItem;
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


/**
 * 栓动狙击步枪类 SniperRifleItem，基础伤害20，耐久100
 * 继承望远镜类 SpyglassItem 以方便调用
 * 未装填时右键长按装填
 * 已装填时右键长按开镜，左键点击开火
 * 开火分为腰射和开镜，开镜可以精准射击，腰射会有一定的误差*/
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
                    (user.getInventory().contains(new ItemStack(ItemRegister.BULLET)) || user.isCreative()));
        setWatching(user.getStackInHand(hand), false);
        if (isCharged(user.getStackInHand(hand))) {
            // 开镜
            setWatching(user.getStackInHand(hand), true);
            user.playSound(SoundEvents.ITEM_SPYGLASS_USE, 1.0F, 1.0F);
            user.incrementStat(Stats.USED.getOrCreateStat(super.asItem()));
            return ItemUsage.consumeHeldItem(world, user, hand);
        } else if (!(user.getInventory().contains(new ItemStack(ItemRegister.BULLET)) || user.isCreative())) {
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
//        if (isCharged(stack)) {                // 开火
//            shoot(stack, world, user);
//        } else
        if (!isCharged(stack) && isLoading(stack) && remainingUseTicks <= 1175) {    // 释放以装填子弹
            if (user instanceof PlayerEntity player && !player.isCreative()) {       // 非创造玩家
                player.getInventory().remove(
                        (stuck_) -> {return stuck_.getItem().equals(ItemRegister.BULLET); },
                        1,
                        player.getInventory()
                ); // 消耗火药
            }
            setCharged(stack,true);
            setLoading(stack, false);
        } else {
            user.playSound(SoundEvents.ITEM_SPYGLASS_STOP_USING, 1.0F, 1.0F);  // 关镜音效
        }
        setWatching(stack, false);                                                 // 关上倍镜

    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {  // 正在使用(装填/开镜)
        if (isLoading(stack) && remainingUseTicks == 1175) {  // 可以装填了
            user.playSound(SoundRegister.SNIPER_RIFLE_LOADED, 1.0F, 1.0F);
        }
        if (remainingUseTicks <=0) setWatching(stack, false);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        /*
        * stuck
        * world
        * entity
        */
        super.inventoryTick(stack, world, entity, slot, selected);
        if (MinecraftClient.getInstance().mouse.wasLeftButtonClicked() && selected &&
                isCharged(stack) && entity instanceof PlayerEntity user) {     // 左键开火
            shoot(stack, world, user);
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return (isCharged(stack) && !isLoading(stack)) ? UseAction.SPYGLASS : UseAction.BOW;
    }

    private void shoot(ItemStack stack, World world, LivingEntity user) {      // 射击函数
        if (!world.isClient) {                 // Server端生成实体
            if (user instanceof ServerPlayerEntity player && !player.isCreative()) stack.damage(1, Random.create(), player);
            BulletEntity bullet = new BulletEntity(world, user);
            Vec3d pos = user.getEyePos();
            // SniperRifle.LOGGER.info(isWatching(stack)?"开镜":"腰射");
            if (isWatching(stack)) {           // 开镜以精准射击
                bullet.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 8.5F, 0F);
            } else {                           // 腰射则子弹会有在视野的水平和垂直方向各有有+-1.0°的误差
                java.util.Random rd = new java.util.Random();
                bullet.setVelocity(user, user.getPitch() + rd.nextFloat() * 2.0F - 1.0F,
                        user.getYaw() + rd.nextFloat() * 2.0F - 1.0F,
                        0.0F, 8.5F, 0F);
            }
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
        user.setPitch(user.getPitch() - 6.0F);                                             // 垂直后坐力
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
        user.stopUsingItem();
    }

}
