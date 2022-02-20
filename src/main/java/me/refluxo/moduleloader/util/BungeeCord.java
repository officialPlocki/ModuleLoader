package me.refluxo.moduleloader.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.refluxo.moduleloader.ModuleLoader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

@SuppressWarnings("ALL")
public class BungeeCord {

    /**
     * This function sends a player to another server
     *
     * @param player The player to send to the server
     * @param server The server to connect to.
     */
    @Deprecated
    public void sendPlayer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(Objects.requireNonNull(player.getPlayer()).getName());
        out.writeUTF(server);
        Bukkit.getServer().sendPluginMessage(ModuleLoader.getPlugin(), "BungeeCord", out.toByteArray());
    }

    /**
     * This function sends a message to the BungeeCord server to connect to another server
     *
     * @param playerName The name of the player you want to connect to.
     * @param server The server to connect to.
     */
    @Deprecated
    public void sendPlayer(String playerName, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(playerName);
        out.writeUTF(server);
        Bukkit.getServer().sendPluginMessage(ModuleLoader.getPlugin(), "BungeeCord", out.toByteArray());
    }

    /**
     * This function kicks a player from the server
     *
     * @param player The player to kick
     * @param message The message to send to the player.
     */
    @Deprecated
    public void kickPlayer(Player player, String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("KickPlayer");
        out.writeUTF(Objects.requireNonNull(player.getPlayer()).getName());
        out.writeUTF(message);
        Bukkit.getServer().sendPluginMessage(ModuleLoader.getPlugin(), "BungeeCord", out.toByteArray());
    }

}
