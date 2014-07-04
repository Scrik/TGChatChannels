/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package tgchatchannels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ChannelsStorage {

	private TGChatChannels plugin;

	public ChannelsStorage(TGChatChannels plugin) {
		this.plugin = plugin;
	}

	private HashMap<String, ChannelData> channels = new HashMap<String, ChannelData>();
	private HashMap<UUID, PlayerData> players = new HashMap<UUID, PlayerData>();
	private HashSet<String> defaultChannels = new HashSet<String>();

	public void loadDefaultChannels() {
		File configfile = new File(plugin.getDataFolder(), "config.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
		defaultChannels.addAll(config.getStringList("defaultchannels"));
		config = new YamlConfiguration();
		config.set("defaultchannels", new ArrayList<String>(defaultChannels));
		try {
			config.save(configfile);
		} catch (IOException e) {
		}
	}

	public void load() {
		File configfile = new File(plugin.getDataFolder(), "data.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);
		for (String defualtChannel : defaultChannels) {
			channels.put(defualtChannel, new ChannelData());
		}
		ConfigurationSection channelsData = config.getConfigurationSection("channels");
		if (channelsData != null) {
			for (String channelName : channelsData.getKeys(false)) {
				ChannelData data = null;
				String uuidstring = channelsData.getString(channelName+".owner", null);
				if (uuidstring == null) {
					data = new ChannelData();
				} else {
					data = new ChannelData(UUID.fromString(uuidstring)); 
				}
				boolean privateChannel = channelsData.getBoolean(channelName+".private", false);
				if (!privateChannel) {
					data.setPublic();
				}
				for (String playerUUIDString : channelsData.getStringList(channelName+".players")) {
					data.addPlayer(UUID.fromString(playerUUIDString));
				}
				channels.put(channelName, data);
			}
		}
		ConfigurationSection playersData = config.getConfigurationSection("players");
		if (playersData != null) {
			for (String playerUUIDString : playersData.getKeys(false)) {
				UUID uuid = UUID.fromString(playerUUIDString);
				PlayerData data = new PlayerData();
				data.setCurrentChannel(playersData.getString(playerUUIDString+".channel"));
				players.put(uuid, data);
			}
		}
	}

	public void save() {
		File configfile = new File(plugin.getDataFolder(), "data.yml");
		FileConfiguration config = new YamlConfiguration();
		ConfigurationSection channelsData = config.createSection("channels");
		for (Entry<String, ChannelData> entry : channels.entrySet()) {
			channelsData.set(entry.getKey()+".owner", entry.getValue().getOwner().toString());
			channelsData.set(entry.getKey()+".private", entry.getValue().isPrivate());
			List<String> cplayers = new ArrayList<String>();
			for (UUID player : entry.getValue().getPlayers()) {
				cplayers.add(player.toString());
			}
			channelsData.set(entry.getKey()+".players", cplayers);
		}
		ConfigurationSection playersData = config.createSection("players");
		for (Entry<UUID, PlayerData> entry : players.entrySet()) {
			playersData.set(entry.getKey()+".channel", entry.getValue().getCurrentChannel());
		}
		try {
			config.save(configfile);
		} catch (IOException e) {
		}
	}

	public boolean isPlayerDataExist(UUID playerUUID) {
		return players.containsKey(playerUUID);
	}

	public PlayerData getPlayerData(UUID playerUUID) {
		return players.get(playerUUID);
	}

	public void addToDefaultChannels(UUID uuid) {
		PlayerData data = new PlayerData();
		if (!defaultChannels.isEmpty()) {
			data.setCurrentChannel(defaultChannels.iterator().next());
		}
		players.put(uuid, data);
		for (String channelName : defaultChannels) {
			if (channels.containsKey(channelName)) {
				channels.get(channelName).addPlayer(uuid);
			}
		}
	}

	public boolean channelExists(String channelName) {
		return channels.containsKey(channelName);
	}

	public ChannelData getChannelData(String channelName) {
		return channels.get(channelName);
	}

	public void addChannel(UUID owner, String channelName) {
		channels.put(channelName, new ChannelData(owner));
	}

	public void removeChannel(String channelName) {
		channels.remove(channelName);
	}

}
