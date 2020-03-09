package fr.opsycraft.opsyban;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import fr.opsycraft.opsyban.utils.FilesManager;

public class Main extends JavaPlugin implements Listener
{
	
	private Plugin plugin;
	private BanCommand bc;
	private FilesManager filesManager;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		
		filesManager = new FilesManager(this);
		filesManager.createConfigsFile();
		filesManager.createMessagesFiles();
		
		
		bc = new BanCommand(this);
		
		registerEvents(this, bc);
		getCommand("opsyban").setExecutor(bc);
		getCommand("opsytempban").setExecutor(bc);
		getCommand("opsyunban").setExecutor(bc);
		getCommand("opsykick").setExecutor(bc);
		
		bc.setBanMap(filesManager.loadDatabase());
	}
	
	@Override
	public void onDisable()
	{
		plugin = null;
	}
	
	private void registerEvents(Plugin plugin, Listener... listeners)
	{
		for(Listener listener : listeners)
		{
			Bukkit.getPluginManager().registerEvents(listener, plugin);
		}
	}
	
	public Plugin getPlugin()
	{
		return plugin;
	}
	
	public FilesManager getFilesManager()
	{
		return filesManager;
	}
	
	public BanCommand getBanCommand()
	{
		return bc;
	}
}
