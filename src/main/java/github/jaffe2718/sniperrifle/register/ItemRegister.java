package github.jaffe2718.sniperrifle.register;

import github.jaffe2718.sniperrifle.SniperRifle;
import github.jaffe2718.sniperrifle.item.BulletItem;
import github.jaffe2718.sniperrifle.item.SniperRifleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class ItemRegister {

    public static final Item BULLET = new BulletItem(new Item.Settings().maxCount(64).group(ItemGroup.COMBAT));
    public static final Item SNIPERRIFLR = new SniperRifleItem(new Item.Settings().maxDamage(100).group(ItemGroup.COMBAT));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(SniperRifle.ModID, "bullet"), BULLET);
        Registry.register(Registry.ITEM, new Identifier(SniperRifle.ModID, "sniper_rifle"), SNIPERRIFLR);
    }
}
