package org.swarg.mc.bugfix;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;

/**
 * 09-02-21
 * @author Swarg
 */
@Mod(modid = StatisticGuiFix.MODID, version = StatisticGuiFix.VERSION)
public class StatisticGuiFix {
    public static final String MODID = "StatisticGuiFix";
    public static final String VERSION = "0.3";
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            //for fix Crash on Statistics gui open
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new StatGuiEventHandler());
        }
    }
}
