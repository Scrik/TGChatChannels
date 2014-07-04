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
	private HashSet<String> defaultChannels = new HashSet<String>();
	private HashMap<UUID, PlayerData> playersData = new HashMap<UUID, PlayerData>();

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
		ConfigurationSection channels = config.getConfigurationSection("channels");
		if (channels != null) {
			for (String channelName : channels.getKeys(false)) {
				ChannelData data = null;
				String uuidstring = channels.getString(channelName+".owner", null);
				if (uuidstring == null) {
					data = new ChannelData();
				} else {
					data = new ChannelData(UUID.fromString(uuidstring)); 
				}
				boolean privateChannel = channels.getBoolean(channelName+".private", false);
				if (!privateChannel) {
					data.setPublic();
				}
				for (String playerUUIDString : channels.getStringList(channelName+".players")) {
					data.addPlayer(UUID.fromString(playerUUIDString));
				}
			}
		}
		ConfigurationSection players = config.getConfigurationSection("players");
		if (players != null) {
			for (String playerUUIDString : players.getKeys(false)) {
				UUID uuid = UUID.fromString(playerUUIDString);
				PlayerData data = new PlayerData();
				data.setCurrentChannel(players.getString(playerUUIDString+".channel"));
				playersData.put(uuid, data);
			}
		}
	}

	public void save() {
		
	}

	public PlayerData getPlayerData(UUID playerUUID) {
		return playersData.get(playerUUID);
	}

	public void addToDefaultChannels(UUID uuid) {
		PlayerData data = new PlayerData();
		if (!defaultChannels.isEmpty()) {
			data.setCurrentChannel(defaultChannels.iterator().next());
		}
		playersData.put(uuid, data);
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
