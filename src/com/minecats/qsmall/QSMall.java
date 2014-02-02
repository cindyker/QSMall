package com.minecats.qsmall;

import com.minecats.qsmall.Listeners.CommandListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.maxgamer.QuickShop.Shop.ShopCreateEvent;
import org.maxgamer.QuickShop.Shop.ShopPreCreateEvent;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
/**
 * Created by cindy on 2/2/14.
 * For the play.minecats.com server
 */
public class QSMall  extends JavaPlugin implements Listener {

    public WorldGuardPlugin wgp;
    public WorldEditPlugin wep;
    public RegionManager wgp_rm;
    public CommandListener cmdListener;


    public void onEnable()
    {




        Bukkit.getPluginManager().registerEvents(this,  Bukkit.getPluginManager().getPlugin("QuickShop"));


        //Added so we can check WorldGuard Regions - cindy_k
        Plugin plug;

        //WorldGuard
        //	if(getConfig().getBoolean("plugins.worldguard")){
        plug = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if(plug != null){
            wgp = (WorldGuardPlugin) plug;
            if(wgp!=null)
                getLogger().info(ChatColor.GREEN + "WorldGuard hooked!");
            else
                getLogger().info(ChatColor.GREEN + "WorldGuard Problem!");
            //wgp_rm = wgp.getRegionManager(this.getServer().get world);
        }

        plug = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if(plug != null){
            wep = (WorldEditPlugin) plug;
            if(wep!=null)
                getLogger().info(ChatColor.GREEN + "WorldEdit hooked!");
            else
                getLogger().info(ChatColor.GREEN + "WorldEdit Problem!");
            //wgp_rm = wgp.getRegionManager(this.getServer().get world);
        }

        //	}

        cmdListener = new CommandListener(this);
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(cmdListener, this);


        getLogger().info("Your plugin has been enabled!");

    }

    public void onDisable()
    {
        //getLogger().info("Your plugin has been disabled!");
        //login.SaveItToDisk();
        //ystem.out.println("Disabled");
        getLogger().info("Your plugin has been Disabled!");
    }

    public static WorldGuardPlugin getWorldGuard(JavaPlugin plugin)
    {
        Plugin wPlugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");

        if ((wPlugin == null) || (!(wPlugin instanceof WorldGuardPlugin)))
        {
            return null;
        }

        return (WorldGuardPlugin) wPlugin;
    }

    public static WorldEditPlugin getWorldEdit(JavaPlugin plugin)
    {
        Plugin wPlugin = plugin.getServer().getPluginManager().getPlugin("WorldEdit");

        if ((wPlugin == null) || (!(wPlugin instanceof WorldGuardPlugin)))
        {
            return null;
        }

        return (WorldEditPlugin) wPlugin;
    }


    @EventHandler
    public void onShopCreate(ShopCreateEvent e){
        //Check that e.getPlayer() is allowed in the WorldGuard region
        //If they're not, e.setCancelled(true);

        //	 getLogger().info(e.getPlayer().getName() + " On Shop Create!");
        if (e.isCancelled()) return;


        boolean NotAllowedSell = false;
        RegionManager grm = getWorldGuard(this).getRegionManager(e.getPlayer().getWorld());
        if (grm == null)
        {
            //     e.getPlayer().sendMessage("No WG Region Here");
            //     getLogger().info(e.getPlayer().getName() + " is at a spot with no WG Region");
            NotAllowedSell = true;
        }

        Vector pt = BukkitUtil.toVector(e.getPlayer().getLocation());

        ApplicableRegionSet set = grm.getApplicableRegions(pt);

        boolean modBuild = e.getPlayer().hasPermission("worldguard.region.bypass.*");

        if (((set.size() == 0) || (!set.canBuild(getWorldGuard(this).wrapPlayer(e.getPlayer())))) && (!modBuild))
        {
            //	    	e.getPlayer().sendMessage("No Permissions on WG Region Here");
            //	    	getLogger().info(e.getPlayer().getName() + " has no WG Region permissions");
            NotAllowedSell = true;
        }

        if (NotAllowedSell)
        {
            //    	getLogger().info(e.getPlayer().getName() + " cancelling");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onShopPreCreate(ShopPreCreateEvent  e )
    {
        // getLogger().info(e.getPlayer().getName() + " On Shop Create!");
        if (e.isCancelled()) return;


        boolean NotAllowedSell = false;
        RegionManager grm = getWorldGuard(this).getRegionManager(e.getPlayer().getWorld());
        if (grm == null)
        {
            // e.getPlayer().sendMessage("No WG Region Here");
            //  getLogger().info(e.getPlayer().getName() + " is at a spot with no WG Region");
            NotAllowedSell = true;
        }

        Vector pt = BukkitUtil.toVector(e.getPlayer().getLocation());

        ApplicableRegionSet set = grm.getApplicableRegions(pt);

        boolean modBuild = e.getPlayer().hasPermission("worldguard.region.bypass.*");

        if (((set.size() == 0) || (!set.canBuild(getWorldGuard(this).wrapPlayer(e.getPlayer())))) && (!modBuild))
        {
            // 	e.getPlayer().sendMessage("No Permissions on WG Region Here");
            //	getLogger().info(e.getPlayer().getName() + " has no WG Region permissions");
            NotAllowedSell = true;
        }

        if (NotAllowedSell)
        {
            // 	getLogger().info(e.getPlayer().getName() + " cancelling");
            e.setCancelled(true);
        }

    }


}
