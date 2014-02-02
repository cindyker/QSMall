package com.minecats.qsmall.Listeners;

import com.minecats.qsmall.QSMall;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldedit.masks.Mask;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by cindy on 2/2/14.
 */
public class CommandListener implements CommandExecutor, Listener  {


    private QSMall plugin;

    public CommandListener(QSMall plugin) {
        this.plugin = plugin;


    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {


        plugin.getLogger().info("Got Command: " + e.getMessage());

        if(e.getMessage().startsWith("/qsmall rent"))
        {

            //TODO: Add Permissions

            if ( createShops(e.getPlayer()) )
            {
                //Created - Save Player Info in DataStore so
                // we can know when to EXPIRE them.
            }
            else
            {
                e.getPlayer().sendMessage("Shop Assignment cancelled");
            }

            e.setCancelled(true);


//            boolean modBuild = e.getPlayer().hasPermission("worldguard.region.bypass.*");




        }

        if(e.getMessage().startsWith("/qsmall remove"))
        {
            removeShop(e.getPlayer());
            e.setCancelled(true);
        }


        //remove player from region and clear region.




    }

    //****************************************
    // CreateShops
    //****************************************
    private boolean createShops(Player player)
    {
        //add player to region.
        RegionManager grm = plugin.getWorldGuard(plugin).getRegionManager(player.getWorld());

        if (grm == null)
        {
            player.sendMessage("You are not in a mall shop.");
            plugin.getLogger().info( " not a WG Region");

            return false;

        }



        Vector pt = BukkitUtil.toVector(player.getLocation());

        ApplicableRegionSet set = grm.getApplicableRegions(pt);
        if (set.size() == 0) // || (!set.canBuild(plugin.getWorldGuard(plugin).wrapPlayer(e.getPlayer())))) && (!modBuild))
        {

            //No region here...
            plugin.getLogger().info( " no WG Region here");
            return false;
        }

        for(ProtectedRegion pr: set)
        {
            plugin.getLogger().info( pr.getId() + " - Regions.");
            if(pr.getId().toLowerCase().startsWith("store"))
            {
                plugin.getLogger().info( "Region : " + pr.getId() + " found");
                if(pr.getMembers().size()>0)
                {
                    player.sendMessage(pr.getId() + " already has an Owner");

                    return false;

                }
                else
                {
                    DefaultDomain owners = new DefaultDomain();
                    owners.addPlayer(player.getName());
                    pr.setMembers(owners);
                    //region.setOwners(owners);
                    //pr.getMembers().addPlayer(e.getPlayer().getName());
                    player.getPlayer().sendMessage("You now own" + pr.getId() + ".");


                    try {
                        grm.save();
                    } catch (ProtectionDatabaseException ex) {
                        plugin.getLogger().info("ProtectionDatabaseException " + ex.getMessage());
                    }
                    // player.setCancelled(true);
                    return true;

                }
            }
            else
            {
                player.sendMessage("You are not in a mall shop.");
                plugin.getLogger().info( "Shop Regions need to start with Store - Store1, Store2, etc.");
            }
        }

        return false;
    }


    //**************************************
    //** Remove Shop
    //**************************************
    private boolean removeShop(Player player)
    {
        RegionManager grm = plugin.getWorldGuard(plugin).getRegionManager(player.getWorld());

        if (grm == null)
        {
            //     e.getPlayer().sendMessage("No WG Region Here");
            plugin.getLogger().info( " not a WG Region");
            return false;

        }



        Vector pt = BukkitUtil.toVector(player.getLocation());

        ApplicableRegionSet set = grm.getApplicableRegions(pt);

        if (set.size() == 0) // || (!set.canBuild(plugin.getWorldGuard(plugin).wrapPlayer(e.getPlayer())))) && (!modBuild))
        {

            //No region here...
            plugin.getLogger().info( " no WG Region here");
            return false;
        }

        for(ProtectedRegion pr: set)
        {
            plugin.getLogger().info( pr.getId() + " - Regions.");
            if(pr.getId().toLowerCase().startsWith("store"))
            {
                plugin.getLogger().info( "Region : " + pr.getId() + " found");
                if(pr.getMembers().size()>1)
                {

                    player.sendMessage(pr.getId() + " already has an Owner");

                    return false;

                }

                if(pr.getMembers().size()==1)
                {
                    if(pr.isMember(player.getName()))
                    {
                          /*  try
                            {
                             //   setPlayerSelection(e.getPlayer(),pr);

                            }
                            catch(CommandException ex)
                            {
                                plugin.getLogger().info(ex.getMessage());
                             //   log.log(Level.ALL,null,ex);
                            }*/
                        LocalSession sess = plugin.wep.getSession( player);
                        EditSession es =  plugin.wep.createEditSession(player);
                        LocalPlayer lp  =  plugin.wep.wrapPlayer( player);
                        //  Region region = sess.getSelection(player.getWorld());
                        Mask mask = sess.getMask();
                        sess.setMask(null);
                        Region region = null;
                        try
                        {


                            ProtectedCuboidRegion cuboid = (ProtectedCuboidRegion) pr;
                            Vector pt1 = cuboid.getMinimumPoint();
                            Vector pt2 = cuboid.getMaximumPoint();
                            CuboidSelection selection = new CuboidSelection(player.getWorld(), pt1, pt2);
                            plugin.wep.setSelection(player, selection);

                            region =  sess.getSelection(lp.getWorld()); //.getRegionSelector().getRegion();
                        }
                        catch(Exception ex) //    IncompleteRegionException ex)
                        {
                            plugin.getLogger().info(ex.getMessage());
                        }

                        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(lp.getWorld(), -1);


                        try
                        {
                            editSession.setBlocks(region,new BaseBlock(BlockID.AIR));
                        }
                        catch(MaxChangedBlocksException ex)
                        {
                            plugin.getLogger().info(  "MaxChangedBlocksException - Regions. " + ex.getMessage());
                        }

                        sess.setMask(mask);

                        DefaultDomain owners = new DefaultDomain();
                        owners.removePlayer(player.getName());
                        pr.setMembers(owners);

                        //Save Region Changes
                        try {
                            grm.save();
                        } catch (ProtectionDatabaseException ex) {
                            plugin.getLogger().info("ProtectionDatabaseException " + ex.getMessage());
                        }

                        player.sendMessage("Shop Cleared and Owner Removed");
                        return true;
                    }

                }
                else
                {

                    player.sendMessage("You don't own this shop");

                    return false;

                }
            }
        }

        return false;
    }


    private void setPlayerSelection(Player player, ProtectedRegion region)
            throws CommandException {
        WorldEditPlugin worldEdit = plugin.getWorldEdit(plugin);

        if(player == null)
            plugin.getLogger().info("Player Is NULL!!!");

        World world = player.getWorld();

        // Set selection
        if (region instanceof ProtectedCuboidRegion) {
            ProtectedCuboidRegion cuboid = (ProtectedCuboidRegion) region;
            Vector pt1 = cuboid.getMinimumPoint();
            Vector pt2 = cuboid.getMaximumPoint();

            CuboidSelection selection = new CuboidSelection(world, pt1, pt2);
            if(selection != null)
                worldEdit.setSelection(player, selection);
            else
                plugin.getLogger().info("Selection is NULL!!");

            player.sendMessage(ChatColor.YELLOW + "Region selected as a cuboid.");

        } else if (region instanceof ProtectedPolygonalRegion) {
            ProtectedPolygonalRegion poly2d = (ProtectedPolygonalRegion) region;
            Polygonal2DSelection selection = new Polygonal2DSelection(
                    world, poly2d.getPoints(),
                    poly2d.getMinimumPoint().getBlockY(),
                    poly2d.getMaximumPoint().getBlockY() );
            worldEdit.setSelection(player, selection);
            player.sendMessage(ChatColor.YELLOW + "Region selected as a polygon.");

        } else if (region instanceof GlobalProtectedRegion) {
            throw new CommandException(
                    "Can't select global regions! " +
                            "That would cover the entire world.");

        } else {
            throw new CommandException("Unknown region type: " +
                    region.getClass().getCanonicalName());
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        return false;

    }
}