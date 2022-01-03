package net.lyragames.practice;

import lombok.Getter;
import net.lyragames.llib.LyraPlugin;
import net.lyragames.llib.utils.ConfigFile;

@Getter
public class PracticePlugin extends LyraPlugin {

    @Getter private static PracticePlugin instance;

    private ConfigFile kitsFile, arenasFile;

    @Override
    public void onEnable() {
        instance = this;

        kitsFile = new ConfigFile(this, "kits");
        arenasFile = new ConfigFile(this, "arenas");

    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
