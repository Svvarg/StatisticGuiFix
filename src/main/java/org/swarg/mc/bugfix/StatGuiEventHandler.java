package org.swarg.mc.bugfix;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityList;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatList;
import net.minecraft.item.Item;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * 05-02-21
 * @author Swarg
 */
@SideOnly(Side.CLIENT)
public class StatGuiEventHandler {
    public static final Logger LOG = LogManager.getLogger("StatGuiFix");
    boolean fixed;
    /**
     * Statistics Gui fix portal 90 -1 if of
     * find the best way to fix
     * net.minecraft.network.play.server.S37PacketStatistics
     * @param event
     * MinecraftForge.EVENT_BUS.register
     */
    @SubscribeEvent
    public void onClientGUI(net.minecraftforge.client.event.GuiOpenEvent event) {
        if (event.gui != null) {
            if (event.gui.getClass() == net.minecraft.client.gui.achievement.GuiStats.class) {
                //net.minecraft.stats.StatCrafting

                int a = fixGuiStatListItemBased(net.minecraft.stats.StatList.objectMineStats);
                int b = fixGuiStatListItemBased(net.minecraft.stats.StatList.itemStats);
                //EntityList.EntityEggInfo
                int c = fixGuiStatEntityList();
                if (!fixed) {
                    //the counter displays the number of detected objects that crash the gui
                    /*DEBUG*/LOG.log(Level.INFO, "[##StatGuiFix##] ObjectMine:{} Item:{} Entity:{}", a, b, c);
                    fixed = true;
                }
            }
        }
    }
    //------------------------------------------------------------------------\\


    /**
     * objectMineStats
     * Prevent and Fix Crash on ClientSide then click to Statistics button
     *
     * java.lang.ArrayIndexOutOfBoundsException: -1
     *  at net.minecraft.client.gui.achievement.GuiStats$StatsBlock.[init](SourceFile:548)
     *  at net.minecraft.client.gui.achievement.GuiStats.func_146509_g(SourceFile:123)
     *  at net.minecraft.client.network.NetHandlerPlayClient.func_147293_a(NetHandlerPlayClient.java:1328)
     * net.minecraft.stats.StatCrafting
     */
    @SideOnly(Side.CLIENT)
    public static int fixGuiStatListItemBased(List stats) {
        int removed = 0;
        if (stats != null && stats.size() > 0 ) {
            final int max = StatList.objectUseStats.length;//32k
            final int was = stats.size();
            Iterator iter = stats.iterator();
            while (iter.hasNext()) {
                Object e = iter.next();
                if (e == null) {
                    iter.remove();
                }
                else if (e instanceof StatCrafting) {
                    Item item = ((StatCrafting)e).func_150959_a();//ClientSideOnly! //"field_150960_a"
                    final int id = Item.getIdFromItem(item);
                    if (id < 0 || id >= max ) {
                        iter.remove();//removed++;
                    }
                }
            }
            removed = was - stats.size();
            //"###[Fix] StatList Removed not Registered Items: " + removed;
        }
        return removed;
    }


    @SideOnly(Side.CLIENT)
    public static int fixGuiStatEntityList() {
        if (EntityList.entityEggs != null) {
            int was = EntityList.entityEggs.size();
            Iterator<Map.Entry> iter = EntityList.entityEggs.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry e = iter.next();
                if (e.getValue() == null) {
                    iter.remove();//(EntityList.EntityEggInfo)e.getValue();
                }
            }
            return was - EntityList.entityEggs.size();
        }
        return 0;
    }


    /*
    forge source file add check for outOfRange on
    src\main\java\net\minecraft\client\gui\achievement\GuiStats.java

    need fix snippets like:
    int k = Item.getIdFromItem(item); if item unreg then return -1;
    StatList.objectCraftStats[k], ... //Boom!


    java.lang.ArrayIndexOutOfBoundsException: -1
 	at net.minecraft.client.gui.achievement.GuiStats$StatsBlock.func_148126_a(SourceFile:628)  | drawSlot
 	at net.minecraft.client.gui.GuiSlot.func_148120_b(GuiSlot.java:433)                        | drawSelectionBox
 	at net.minecraft.client.gui.GuiSlot.func_148128_a(GuiSlot.java:306)                        | drawScreen
 	at net.minecraft.client.gui.achievement.GuiStats.func_73863_a(SourceFile:108)              | drawScreen
 	at net.minecraft.client.renderer.EntityRenderer.func_78480_b(EntityRenderer.java:1455)     | updateCameraAndRender
 	at net.minecraft.client.Minecraft.func_71411_J(Minecraft.java:1001)                        | runGameLoop
 	at net.minecraft.client.Minecraft.func_99999_d(Minecraft.java:898)                         | run
 	at net.minecraft.client.main.Main.main(SourceFile:148)
 	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
 	at sun.reflect.NativeMethodAccessorImpl.invoke(Unknown Source)
 	at sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)
 	at java.lang.reflect.Method.invoke(Unknown Source)
 	at net.minecraft.launchwrapper.Launch.launch(Launch.java:135)
 	at net.minecraft.launchwrapper.Launch.main(Launch.java:28)
    */
}
