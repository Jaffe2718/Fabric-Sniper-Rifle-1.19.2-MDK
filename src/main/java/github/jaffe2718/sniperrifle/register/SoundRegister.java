package github.jaffe2718.sniperrifle.register;

import github.jaffe2718.sniperrifle.SniperRifle;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SoundRegister {

    public static final SoundEvent BULLET_HIT = new SoundEvent(new Identifier(SniperRifle.ModID, "bullet_hit"));
    public static final SoundEvent SNIPER_RIFLE_FIRE = new SoundEvent(new Identifier(SniperRifle.ModID, "sniper_rifle_fire"));
    public static final SoundEvent SNIPER_RIFLE_LOADED = new SoundEvent(new Identifier(SniperRifle.ModID, "sniper_rifle_loaded"));
    public static final SoundEvent SNIPER_RIFLE_LOADING = new SoundEvent(new Identifier(SniperRifle.ModID, "sniper_rifle_loading"));

    public static void register() {
        Registry.register(Registry.SOUND_EVENT, "bullet_hit", BULLET_HIT);
        Registry.register(Registry.SOUND_EVENT, "sniper_rifle_fire", SNIPER_RIFLE_FIRE);
        Registry.register(Registry.SOUND_EVENT, "sniper_rifle_loaded", SNIPER_RIFLE_LOADED);
        Registry.register(Registry.SOUND_EVENT, "sniper_rifle_loading", SNIPER_RIFLE_LOADING);
    }
}
