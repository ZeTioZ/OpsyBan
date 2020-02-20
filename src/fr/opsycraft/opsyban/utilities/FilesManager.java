package fr.opsycraft.opsyban.utilities;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import fr.opsycraft.opsyban.Ban;
import fr.opsycraft.opsyban.Main;

public class FilesManager {
	
	private Main main;
	private Plugin plugin;
	
	public FilesManager(Main main)
	{
		this.main = main;
		this.plugin = this.main.getPlugin();
	}
	
	//region Configs File (Creator/Getter)
    private YamlConfiguration configsFileConfig;
    
    public YamlConfiguration getConfigsFile()
    {
        return this.configsFileConfig;
    }

    public void createConfigsFile()
    {
    	File configsFile = new File(plugin.getDataFolder(), "configs.yml");
        if (!configsFile.exists())
        {
        	configsFile.getParentFile().mkdirs();
        	plugin.saveResource("configs.yml", false);
        }

        configsFileConfig = new YamlConfiguration();
        try
        {
        	configsFileConfig.load(configsFile);
        }
        catch (IOException | InvalidConfigurationException e)
        {
        	plugin.getLogger().severe("An error occured while loading the configs file!");
        }
    }
    //endregion
    
    //region Message File (Creator/Getter)
    private YamlConfiguration messagesFileConfig;

    public YamlConfiguration getMessagesFile()
    {
        return this.messagesFileConfig;
    }
    
	public void createMessagesFiles()
	{
		File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
		if(!messagesFile.exists())
		{
			messagesFile.getParentFile().mkdir();
			plugin.saveResource("messages.yml", false);
		}
		
		messagesFileConfig = new YamlConfiguration();
		try
	    {
			messagesFileConfig.load(messagesFile);
	    }
	    catch (IOException | InvalidConfigurationException e) {
	    	plugin.getLogger().severe("An error occured while loading the messages file!");
	    }
	}
	//endregion
	
	//region Database File (Creator/Getter)
    private YamlConfiguration databaseFileConfig;
    private File databaseFile;

    public YamlConfiguration getDatabaseFile()
    {
        return this.databaseFileConfig;
    }
    
	public void createDatabaseFiles()
	{
		databaseFile = new File(plugin.getDataFolder(), "database.yml");
		if(!databaseFile.exists())
		{
			try {
				databaseFile.createNewFile();
			} catch (IOException e) {
				plugin.getLogger().severe("An error occured while creating the database file!");
			}
		}
		
		databaseFileConfig = new YamlConfiguration();
		try
	    {
			databaseFileConfig.load(databaseFile);
	    }
	    catch (IOException | InvalidConfigurationException e) {
	    	plugin.getLogger().severe("An error occured while loading the database file!");
	    }
	}
	
	public Map<String, Ban> loadDatabase()
	{
		if(databaseFileConfig != null)
		{
			if(databaseFileConfig.isConfigurationSection("banned_players"))
			{
				Map<String, Ban> banMapLoad = new HashMap<>();
				ConfigurationSection databaseConfigSection = databaseFileConfig.getConfigurationSection("banned_players");
				for(String UUID : databaseConfigSection.getKeys(false))
				{
					ConfigurationSection playerSection = databaseConfigSection.getConfigurationSection(UUID);
					String playerName = playerSection.getString("player_name");
					String giverName = playerSection.getString("giver_name");
					String reason = playerSection.getString("ban_reason");
					Long banDate = playerSection.getLong("ban_date_millis");
					Long banTime = playerSection.getLong("ban_time_millis");
					Ban banLoad = new Ban(playerName, giverName, UUID, reason, banDate, banTime);
					banMapLoad.put(UUID, banLoad);
				}
				plugin.getLogger().info("Database loaded successfully!");
				return banMapLoad;
			}
			return new HashMap<>();
		}
		createDatabaseFiles();
		return loadDatabase();
	}
	
	public void saveDatabase()
	{
		if(databaseFileConfig != null)
		{		
			Map<String, Ban> banMapSave = main.getBanCommand().getBanMap();
			for(Entry<String, Ban> banSave : banMapSave.entrySet())
			{
				if(banSave.getValue() != null)
				{					
					databaseFileConfig.set("banned_players." + banSave.getKey() + ".player_name", banSave.getValue().getPlayerName());
					databaseFileConfig.set("banned_players." + banSave.getKey() + ".giver_name", banSave.getValue().getGiverName());
					databaseFileConfig.set("banned_players." + banSave.getKey() + ".ban_reason", banSave.getValue().getReason());
					databaseFileConfig.set("banned_players." + banSave.getKey() + ".ban_date_millis", banSave.getValue().getTime());
					databaseFileConfig.set("banned_players." + banSave.getKey() + ".ban_time_millis", banSave.getValue().getBanTime());
				}
				else
				{
					databaseFileConfig.set("banned_players." + banSave.getKey(), null);
				}
			}
			try {
				databaseFileConfig.save(databaseFile);
			} catch (IOException e) {
				plugin.getLogger().severe("An error occured while saving the database file!");
			}
		}
		else
		{
			createDatabaseFiles();
			saveDatabase();
		}
	}
	//endregion
}
