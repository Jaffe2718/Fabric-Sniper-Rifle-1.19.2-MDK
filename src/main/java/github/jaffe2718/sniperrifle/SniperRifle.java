package github.jaffe2718.sniperrifle;

import github.jaffe2718.sniperrifle.entity.BulletEntity;
import github.jaffe2718.sniperrifle.register.ItemRegister;
import github.jaffe2718.sniperrifle.register.SoundRegister;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SniperRifle implements ModInitializer {

    public static String ModID = "sniperrifle";

    public static Logger LOGGER = LoggerFactory.getLogger(ModID);

    /**
     * 注册子弹实体TYPE
     */
    public static final EntityType<BulletEntity> BULLET = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(SniperRifle.ModID, "bullet"),
            FabricEntityTypeBuilder.<BulletEntity>create(SpawnGroup.MISC, BulletEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // dimensions in Minecraft units of the projectile
                    .disableSaving()
                    .trackRangeBlocks(32)
                    .trackedUpdateRate(8) // necessary for all thrown projectiles (as it prevents it from breaking, lol)
                    .build() // VERY IMPORTANT DONT DELETE FOR THE LOVE OF GOD PSLSSSSSS
    );

    /**
     * 调用声音事件和物品的注册
     */
    @Override
    public void onInitialize() {
        SoundRegister.register();
        ItemRegister.register();

        LOGGER.info("Hello Fabric! SniperRifle Mod Loading......");
    }
}
