package dev.jack.autoominous;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class AutoOminousAddon extends MeteorAddon {
    @Override
    public void onInitialize() {
        Modules.get().add(new AutoOminous());
    }

    @Override
    public String getPackage() {
        return "dev.jack.autoominous";
    }
}