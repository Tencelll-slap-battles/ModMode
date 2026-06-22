package dev.staffplugin.managers;

import dev.staffplugin.StaffPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StaffManager {

    // All available staff commands (without the slash)
    public static final List<String> ALL_COMMANDS = List.of(
            "to", "bring", "tp", "kill", "kick", "ban", "title", "freeze", "unfreeze", "gamemode", "modmode"
    );

    private final StaffPlugin plugin;
    private final File dataFile;
    private FileConfiguration data;

    // uuid -> set of permitted commands
    private final Map<UUID, Set<String>> staffPermissions = new HashMap<>();
    // uuid -> is modmode active
    private final Map<UUID, Boolean> modModeActive = new HashMap<>();
    // uuid -> is staff at all
    private final Set<UUID> staffMembers = new HashSet<>();
    // frozen players
    private final Set<UUID> frozenPlayers = new HashSet<>();

    public StaffManager(StaffPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "staffdata.yml");
        load();
    }

    // ── Staff management ──────────────────────────────────────────────────────

    public boolean isStaff(UUID uuid) {
        return staffMembers.contains(uuid);
    }

    /** Make a player staff with ALL commands granted. */
    public void addStaff(UUID uuid) {
        staffMembers.add(uuid);
        staffPermissions.put(uuid, new HashSet<>(ALL_COMMANDS));
        modModeActive.put(uuid, false);
        save();
    }

    /** Remove all staff privileges. */
    public void removeStaff(UUID uuid) {
        staffMembers.remove(uuid);
        staffPermissions.remove(uuid);
        modModeActive.remove(uuid);
        save();
    }

    // ── Per-command permissions ───────────────────────────────────────────────

    public boolean hasCommandPermission(UUID uuid, String command) {
        Set<String> perms = staffPermissions.get(uuid);
        return perms != null && perms.contains(command.toLowerCase());
    }

    public void grantCommand(UUID uuid, String command) {
        staffPermissions.computeIfAbsent(uuid, k -> new HashSet<>()).add(command.toLowerCase());
        save();
    }

    public void revokeCommand(UUID uuid, String command) {
        Set<String> perms = staffPermissions.get(uuid);
        if (perms != null) {
            perms.remove(command.toLowerCase());
            save();
        }
    }

    public Set<String> getGrantedCommands(UUID uuid) {
        return staffPermissions.getOrDefault(uuid, Collections.emptySet());
    }

    // ── Mod mode ─────────────────────────────────────────────────────────────

    public boolean isModModeActive(UUID uuid) {
        return modModeActive.getOrDefault(uuid, false);
    }

    public void setModMode(UUID uuid, boolean active) {
        modModeActive.put(uuid, active);
        save();
    }

    // ── Freeze ────────────────────────────────────────────────────────────────

    public boolean isFrozen(UUID uuid) {
        return frozenPlayers.contains(uuid);
    }

    public void freeze(UUID uuid) {
        frozenPlayers.add(uuid);
    }

    public void unfreeze(UUID uuid) {
        frozenPlayers.remove(uuid);
    }

    // ── Persistence ──────────────────────────────────────────────────────────

    public void load() {
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            return;
        }
        data = YamlConfiguration.loadConfiguration(dataFile);

        staffMembers.clear();
        staffPermissions.clear();
        modModeActive.clear();

        if (data.isConfigurationSection("staff")) {
            for (String key : data.getConfigurationSection("staff").getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                staffMembers.add(uuid);

                List<String> cmds = data.getStringList("staff." + key + ".commands");
                staffPermissions.put(uuid, new HashSet<>(cmds));

                boolean mm = data.getBoolean("staff." + key + ".modmode", false);
                modModeActive.put(uuid, mm);
            }
        }
    }

    public void save() {
        if (data == null) data = new YamlConfiguration();
        // clear old
        data.set("staff", null);

        for (UUID uuid : staffMembers) {
            String path = "staff." + uuid;
            Set<String> cmds = staffPermissions.getOrDefault(uuid, Collections.emptySet());
            data.set(path + ".commands", new ArrayList<>(cmds));
            data.set(path + ".modmode", modModeActive.getOrDefault(uuid, false));
        }

        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save staff data: " + e.getMessage());
        }
    }
}
