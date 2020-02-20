package fr.opsycraft.opsyban;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import fr.opsycraft.opsyban.utilities.TimeConverter;

public class BanCommand implements CommandExecutor, Listener{

	private Main main;
	private Map<String, Ban> banMap = new HashMap<>();
	private YamlConfiguration messagesFile;
	private String prefix;
	
	public BanCommand(Main main)
	{
		this.main = main;
		this.messagesFile = main.getFilesManager().getMessagesFile();
		this.prefix = ChatColor.translateAlternateColorCodes('&', this.messagesFile.getString("prefix"));
	}
	
	public void setBanMap(Map<String, Ban> banMap)
	{
		this.banMap = banMap;
	}
	
	public Map<String, Ban> getBanMap()
	{
		return this.banMap;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		//region Ban player
		if(cmd.getName().equalsIgnoreCase("opsyban"))
		{
			if(args.length == 0)
			{
				sendHelpPage(sender);
			}
			else if(args.length >= 1)
			{
				if(args[0].equalsIgnoreCase("help"))
				{
					sendHelpPage(sender);
				}
				else if(args[0].equalsIgnoreCase("reload"))
				{
					if(sender.hasPermission("opsyban.reload"))
					{
						Bukkit.getPluginManager().disablePlugin(main);
						Bukkit.getPluginManager().enablePlugin(main);
						for(String line : messagesFile.getStringList("plugin-reloaded"))
						{
							line = ChatColor.translateAlternateColorCodes('&', line);
							sender.sendMessage(prefix + line);
						}
					}
					else
					{
						for(String line : messagesFile.getStringList("errors.not-enough-permissions"))
						{
							line = ChatColor.translateAlternateColorCodes('&', line);
							sender.sendMessage(prefix + line);
						}
					}
				}
				else if(args[0].equalsIgnoreCase("list"))
				{
					if(sender.hasPermission("opsyban.list"))
					{
						StringBuilder banList = new StringBuilder();
						boolean i = true;
						if(!banMap.isEmpty())
						{							
							for(Entry<String, Ban> bannedPlayer : banMap.entrySet())
							{
								if(i)
								{				
									banList.append(bannedPlayer.getValue().getPlayerName());
									i = false;
								}
								else
								{
									banList.append(", " + bannedPlayer.getValue().getPlayerName());
								}
							}
							String banListToSend = banList.toString();
							sender.sendMessage(prefix + org.bukkit.ChatColor.translateAlternateColorCodes('&', messagesFile.getString("banlist-prefix")) + banListToSend);
						}
						else
						{
							for(String line : messagesFile.getStringList("errors.empty-ban-list"))
							{
								line = ChatColor.translateAlternateColorCodes('&', line);
								sender.sendMessage(prefix + line);
							}
						}
					}
					else
					{
						for(String line : messagesFile.getStringList("errors.not-enough-permissions"))
						{
							line = ChatColor.translateAlternateColorCodes('&', line);
							sender.sendMessage(prefix + line);
						}
					}
				}
				else
				{
					if(sender.hasPermission("opsyban.ban"))
					{
						OfflinePlayer playerToBan = Bukkit.getOfflinePlayer(args[0].toLowerCase());
						String ptbName = playerToBan.getName();
						String ptbGiver = sender.getName();
						String ptbUUID = playerToBan.getUniqueId().toString();
						String reason = "";
						if(args.length >= 2)
						{
							StringBuilder reasonBuilder = new StringBuilder();
							String[] argsToKeep = Arrays.copyOfRange(args, 1, args.length);
							boolean i = true;
							for(String arg : argsToKeep)
							{
								if(i)
								{
									reasonBuilder.append(arg);
									i = false;
								}
								else
								{
									reasonBuilder.append(" " + arg);
								}
							}
							reason = reasonBuilder.toString();
						}
						else
						{
							reason = messagesFile.getString("empty-reason");
						}
						Ban pBan = new Ban(ptbName, ptbGiver, ptbUUID, reason);
						banMap.put(pBan.getPlayerUUID(), pBan);
						main.getFilesManager().saveDatabase();
						StringBuilder chatMessage = new StringBuilder();
						boolean i = true;
						for(String line : messagesFile.getStringList("chat.permanent-ban-message"))
						{
							line = line.replace("{player}", ptbName);
							line = line.replace("{giver}", ptbGiver);
							line = line.replace("{reason}", reason);
							line = line.replace("{date}", TimeConverter.millisToDate(pBan.getTime()));
							line = ChatColor.translateAlternateColorCodes('&', line);
							if(i)
							{
								chatMessage.append(line);
								i = false;
							}
							else
							{
								chatMessage.append("\n" + line);
							}
						}
						String chatMessageToSend = chatMessage.toString();
						sender.sendMessage(chatMessageToSend);
						i = true;
						if(playerToBan.isOnline())
						{
							StringBuilder kickMessage = new StringBuilder();
							for(String line : messagesFile.getStringList("kick.permanent-ban-message"))
							{
								line = line.replace("{giver}", ptbGiver);
								line = line.replace("{reason}", reason);
								line = line.replace("{date}", TimeConverter.millisToDate(pBan.getTime()));
								line = ChatColor.translateAlternateColorCodes('&', line);
								if(i)
								{
									kickMessage.append(line);
									i = false;
								}
								else
								{
									kickMessage.append("\n" + line);
								}
							}
							String kickMessageToSend = kickMessage.toString();
							playerToBan.getPlayer().kickPlayer(kickMessageToSend);
						}
					}
					else
					{
						for(String line : messagesFile.getStringList("errors.not-enough-permissions"))
						{
							line = ChatColor.translateAlternateColorCodes('&', line);
							sender.sendMessage(prefix + line);
						}
					}
				}
			}
		}
		//endregion
		//region Temporary ban player
		else if(cmd.getName().equalsIgnoreCase("opsytempban"))
		{
			if(args.length == 0)
			{
				sendHelpPage(sender);
			}
			else if(args.length == 1)
			{
				if(args[0].equalsIgnoreCase("help"))
				{
					sendHelpPage(sender);
				}
				else
				{
					for(String line : messagesFile.getStringList("errors.no-ban-time"))
					{
						line = ChatColor.translateAlternateColorCodes('&', line);
						sender.sendMessage(prefix + line);
					}
				}
			}
			else if(args.length >= 2)
			{
				if(sender.hasPermission("opsyban.tempban"))
				{
					OfflinePlayer playerToBan = Bukkit.getOfflinePlayer(args[0].toLowerCase());
					String ptbName = playerToBan.getName();
					String ptbUUID = playerToBan.getUniqueId().toString();
					String banGiver = sender.getName();
					long banTime = TimeConverter.stringTimeToMillis(args[1]);
					if(banTime == 0)
					{
						for(String line : messagesFile.getStringList("errors.no-ban-time"))
						{
							line = ChatColor.translateAlternateColorCodes('&', line);
							sender.sendMessage(prefix + line);
						}
						return false;
					}
					StringBuilder reasonBuilder = new StringBuilder();
					String reason;
					if(args.length >= 3)
					{						
						String[] argsToKeep = Arrays.copyOfRange(args, 2, args.length);
						boolean i = true;
						for(String arg : argsToKeep)
						{
							if(i)
							{								
								reasonBuilder.append(arg);
								i = false;
							}
							else
							{
								reasonBuilder.append(" " + arg);
							}
						}
						reason = reasonBuilder.toString();
					}
					else
					{
						reason = messagesFile.getString("empty-reason");
					}
					Ban pBan = new Ban(ptbName, banGiver, ptbUUID, reason, banTime);
					banMap.put(pBan.getPlayerUUID(), pBan);
					main.getFilesManager().saveDatabase();
					StringBuilder chatMessage = new StringBuilder();
					boolean i = true;
					for(String line : messagesFile.getStringList("chat.temporary-ban-message"))
					{
						line = line.replace("{player}", ptbName);
						line = line.replace("{reason}", reason);
						line = line.replace("{date}", TimeConverter.millisToDate(pBan.getTime()));
						line = line.replace("{time}", TimeConverter.millisToTime(banTime));
						line = ChatColor.translateAlternateColorCodes('&', line);
						if(i)
						{
							chatMessage.append(line);
							i = false;
						}
						else
						{							
							chatMessage.append("\n" + line);
						}
					}
					String chatMessageToSend = chatMessage.toString();
					sender.sendMessage(chatMessageToSend);
					i = true;
					if(playerToBan.isOnline())
					{
						StringBuilder kickMessage = new StringBuilder();
						for(String line : messagesFile.getStringList("kick.temporary-ban-message"))
						{
							line = line.replace("{giver}", banGiver);
							line = line.replace("{reason}", reason);
							line = line.replace("{time}", TimeConverter.millisToTime(banTime));
							line = line.replace("{left}", TimeConverter.millisToTime(banTime));
							line = line.replace("{date}", TimeConverter.millisToDate(pBan.getTime()));
							line = ChatColor.translateAlternateColorCodes('&', line);
							if(i)
							{
								kickMessage.append(line);
								i = false;
							}
							else
							{
								kickMessage.append("\n" + line);
							}
						}
						String kickMessageToSend = kickMessage.toString();
						playerToBan.getPlayer().kickPlayer(kickMessageToSend);
					}
				}
				else
				{
					for(String line : messagesFile.getStringList("errors.not-enough-permissions"))
					{
						line = ChatColor.translateAlternateColorCodes('&', line);
						sender.sendMessage(prefix + line);
					}
				}
			}
		}
		//endregion
		//region Unban player
		else if(cmd.getName().equalsIgnoreCase("opsyunban"))
		{
			if(args.length == 0)
			{
				sendHelpPage(sender);
			}
			else if(args.length == 1)
			{
				if(args[0].equalsIgnoreCase("help"))
				{
					sendHelpPage(sender);
				}
				else
				{
					if(sender.hasPermission("opsyban.unban"))
					{
						OfflinePlayer playerToUnban = Bukkit.getOfflinePlayer(args[0].toLowerCase());
						String ptubUUID = playerToUnban.getUniqueId().toString();
						if(banMap.containsKey(ptubUUID))
						{
							banMap.put(ptubUUID, null);
							main.getFilesManager().saveDatabase();
							banMap.remove(ptubUUID);
							for(String line : messagesFile.getStringList("chat.unban-message"))
							{
								line = line.replace("{player}", args[0]);
								line = ChatColor.translateAlternateColorCodes('&', line);
								sender.sendMessage(prefix + line);
							}
						}
						else
						{
							for(Entry<String, Ban> banCheck : banMap.entrySet())
							{
								if(banCheck.getValue().getPlayerName().equalsIgnoreCase(args[0]))
								{
									banMap.put(banCheck.getKey(), null);
									main.getFilesManager().saveDatabase();
									banMap.remove(banCheck.getKey());
									for(String line : messagesFile.getStringList("chat.unban-message"))
									{
										line = line.replace("{player}", args[0]);
										line = ChatColor.translateAlternateColorCodes('&', line);
										sender.sendMessage(prefix + line);
									}
									return true;
								}
							}
							for(String line : messagesFile.getStringList("errors.not-banned"))
							{
								line = line.replace("{player}", args[0]);
								line = ChatColor.translateAlternateColorCodes('&', line);
								sender.sendMessage(prefix + line);
							}
						}
					}
					else
					{
						for(String line : messagesFile.getStringList("errors.not-enough-permissions"))
						{
							line = ChatColor.translateAlternateColorCodes('&', line);
							sender.sendMessage(prefix + line);
						}
					}
				}
			}
			else
			{
				sendHelpPage(sender);
			}
		}
		//endregion
		//region Kick player
		else if(cmd.getName().equalsIgnoreCase("opsykick"))
		{
			if(args.length == 0)
			{
				sendHelpPage(sender);
			}
			else if(args.length >= 1)
			{
				if(sender.hasPermission("opsyban.kick"))
				{
					OfflinePlayer kickPlayer = Bukkit.getOfflinePlayer(args[0].toLowerCase());
					String reason;
					if(kickPlayer.isOnline())
					{
						StringBuilder reasonBuilder = new StringBuilder();
						String[] argsToKeep = Arrays.copyOfRange(args, 1, args.length);
						boolean i = true;
						for(String arg : argsToKeep)
						{
							if(i)
							{
								reasonBuilder.append(arg);
								i = false;
							}
							else
							{
								reasonBuilder.append(" " + arg);
							}
						}
						reason = reasonBuilder.toString().isEmpty() ? ChatColor.translateAlternateColorCodes('&', messagesFile.getString("empty-reason")) : reasonBuilder.toString();
						StringBuilder chatMessage = new StringBuilder();
						i = true;
						for(String line : messagesFile.getStringList("chat.kick-message"))
						{
							line = line.replace("{player}", args[0]);
							line = line.replace("{reason}", reason);
							line = line.replace("{date}", TimeConverter.millisToDate(System.currentTimeMillis()));
							line = ChatColor.translateAlternateColorCodes('&', line);
							if(i)
							{
								chatMessage.append(line);
								i = false;
							}
							else
							{
								chatMessage.append("\n" + line);
							}
						}
						String chatMessageToSend = chatMessage.toString();
						sender.sendMessage(chatMessageToSend);
						StringBuilder kickMessage = new StringBuilder();
						i = true;
						for(String line : messagesFile.getStringList("kick.kick-message"))
						{
							line = line.replace("{giver}", sender.getName());
							line = line.replace("{reason}", reason);
							line = line.replace("{date}", TimeConverter.millisToDate(System.currentTimeMillis()));
							line = ChatColor.translateAlternateColorCodes('&', line);
							if(i)
							{
								kickMessage.append(line);
								i = false;
							}
							else
							{
								kickMessage.append("\n" + line);
							}
						}
						String kickMessageToSend = kickMessage.toString();
						kickPlayer.getPlayer().kickPlayer(kickMessageToSend);
					}
					else
					{
						for(String line : messagesFile.getStringList("errors.kick-player-offline"))
						{
							line = line.replace("{player}", args[0]);
							line = ChatColor.translateAlternateColorCodes('&', line);
							sender.sendMessage(prefix + line);
						}
					}
				}
				else
				{
					for(String line : messagesFile.getStringList("errors.not-enough-permissions"))
					{
						line = ChatColor.translateAlternateColorCodes('&', line);
						sender.sendMessage(prefix + line);
					}
				}
			}
		}
		//endregion
		return false;
	}
	
	public void sendHelpPage(CommandSender sender)
	{
		for(String line : messagesFile.getStringList("help-page"))
		{
			line = ChatColor.translateAlternateColorCodes('&', line);
			sender.sendMessage(prefix + line);
		}
	}
	
	@EventHandler
	public void onPlayerConnect(AsyncPlayerPreLoginEvent e)
	{
		if(banMap.containsKey(e.getUniqueId().toString()))
		{
			Ban ptbBan = banMap.get(e.getUniqueId().toString());
			String banGiver = ptbBan.getGiverName();
			String reason = ptbBan.getReason() == null ? messagesFile.getString("empty-reason") : ptbBan.getReason();
			String time = TimeConverter.millisToTime(ptbBan.getBanTime());
			String timeLeft = ptbBan.getBanTime() > 0 ? TimeConverter.millisToTime((ptbBan.getBanTime() + ptbBan.getTime()) - System.currentTimeMillis()) : "";
			String date = TimeConverter.millisToDate(ptbBan.getTime());
			List<String> messageList = ptbBan.getBanTime() > 0 ? messagesFile.getStringList("kick.temporary-ban-message") : messagesFile.getStringList("kick.permanent-ban-message");
			if(ptbBan.getBanTime() > 0 && ptbBan.getBanTime() < System.currentTimeMillis())
			{
				banMap.remove(e.getUniqueId().toString());
				main.getFilesManager().saveDatabase();
				return;
			}
			StringBuilder kickMessage = new StringBuilder();
			boolean i = true;
			for(String line : messageList)
			{
				line = line.replace("{giver}", banGiver);
				line = line.replace("{reason}", reason);
				line = line.replace("{time}", time);
				line = line.replace("{left}", timeLeft);
				line = line.replace("{date}", date);
				line = ChatColor.translateAlternateColorCodes('&', line);
				if(i)
				{
					kickMessage.append(line);
					i = false;
				}
				else
				{
					kickMessage.append("\n" + line);
				}
			}
			String kickMessageToSend = kickMessage.toString();			
			e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMessageToSend);
		}
	}
}
