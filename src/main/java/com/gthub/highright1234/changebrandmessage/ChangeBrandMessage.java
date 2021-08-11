package com.gthub.highright1234.changebrandmessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class ChangeBrandMessage extends Plugin implements Listener {

    private String brand;

    public String getBrand() {
        return brand;
    }

    @Override
    public void onEnable() {
        if (getConfig() == null) return;
        this.brand = getConfig().getString("brand");
        getProxy().getPluginManager().registerListener(this, this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void on(PluginMessageEvent e) {
        if (!(e.getReceiver() instanceof ProxiedPlayer || e.getSender() instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer player = e.getReceiver() != null ? (ProxiedPlayer) e.getReceiver() : (ProxiedPlayer) e.getSender();
        String channel = player.getPendingConnection().getVersion() >=
                ProtocolConstants.MINECRAFT_1_13 ? "minecraft:brand" : "MC|Brand";
        if (e.getTag().equalsIgnoreCase(channel)) {
            ByteBuf brand = ByteBufAllocator.DEFAULT.heapBuffer();
            DefinedPacket.writeString( this.brand, brand );
            player.sendData(channel, DefinedPacket.toArray(brand));
            brand.release();
            e.setCancelled(true);
        }
    }

    public Configuration getConfig() {
        if (!getDataFolder().exists()) {
            if (!getDataFolder().mkdir()) {
                return null;
            }
        }

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
