package dev.staffplugin;

import dev.staffplugin.commands.*;
import dev.staffplugin.listeners.FreezeListener;
import dev.staffplugin.managers.StaffManager;
import org.bukkit.plugin.java.JavaPlugin;

public class StaffPlugin extends JavaPlugin {

    private StaffManager staffManager;

    @Override
    public void onEnable() {
        staffManager = new StaffManager(this);

        // Admin commands
        getCommand("staff").setExecutor(new StaffCommand(this));
        getCommand("unstaff").setExecutor(new UnstaffCommand(this));

        // Staff commands
        getCommand("modmode").setExecutor(new ModModeCommand(this));
        getCommand("to").setExecutor(new ToCommand(this));
        getCommand("bring").setExecutor(new BringCommand(this));
        getCommand("tp").setExecutor(new TpCommand(this));
        getCommand("kill").setExecutor(new KillCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("title").setExecutor(new TitleCommand(this));
        getCommand("freeze").setExecutor(new FreezeCommand(this));
        getCommand("unfreeze").setExecutor(new UnfreezeCommand(this));
        getCommand("gamemode").setExecutor(new GamemodeCommand(this));

        // Tab completers
        StaffTabCompleter staffTab = new StaffTabCompleter(this);
        getCommand("staff").setTabCompleter(staffTab);
        getCommand("unstaff").setTabCompleter(staffTab);
        getCommand("to").setTabCompleter(staffTab);
        getCommand("bring").setTabCompleter(staffTab);
        getCommand("kick").setTabCompleter(staffTab);
        getCommand("ban").setTabCompleter(staffTab);
        getCommand("title").setTabCompleter(staffTab);
        getCommand("freeze").setTabCompleter(staffTab);
        getCommand("unfreeze").setTabCompleter(staffTab);
        getCommand("gamemode").setTabCompleter(staffTab);

        // Listeners
        getServer().getPluginManager().registerEvents(new FreezeListener(staffManager), this);

        getLogger().info("StaffPlugin enabled!");
    }

    @Override
    public void onDisable() {
        staffManager.save();
    }

    public StaffManager getStaffManager() {
        return staffManager;
    }
}
