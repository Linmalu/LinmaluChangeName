package com.linmalu.changename.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.linmalu.changename.Main;
import com.linmalu.library.api.LinmaluPlayer;
import com.linmalu.library.api.LinmaluYamlConfiguration;

public class GameData
{
	private HashMap<String, PlayerData> players = new HashMap<>();
	private final String NAME = "이름";
	private final String CHANGE = "상태";
	private final String SKIN = "스킨";
	private final File file = new File(Main.getMain().getDataFolder(), "config.yml");

	public GameData()
	{
		reloadConfig();
	}
	public void reloadConfig()
	{
		players.clear();
		LinmaluYamlConfiguration config = LinmaluYamlConfiguration.loadConfiguration(file);
		for(String name : config.getKeys(false))
		{
			String changeName = config.getString(name + "." + NAME);
			if(changeName.length() > 16)
			{
				changeName = changeName.substring(0, 16);
			}
			String skin = config.getString(name + "." + SKIN);
			boolean change = config.getBoolean(name + "." + CHANGE);
			if(changeName != null)
			{
				players.put(name, new PlayerData(name, changeName, skin, change));
			}
		}
	}
	public void saveConfig()
	{
		LinmaluYamlConfiguration config = new LinmaluYamlConfiguration();
		for(PlayerData pd : players.values())
		{
			config.set(pd.getName() + "." + NAME, pd.getChangeName());
			config.set(pd.getName() + "." + SKIN, pd.getSkin());
			config.set(pd.getName() + "." + CHANGE, pd.isChange());
		}
		try
		{
			config.save(file);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public void joinEvent(Player player)
	{
		for(String name : players.keySet())
		{
			if(players.get(name).getChangeName().equalsIgnoreCase(player.getName()))
			{
				Player p = Bukkit.getPlayer(name);
				if(p != null)
				{
					p.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "같은 이름의 플레이어가 접속하여 이름이 삭제됩니다.");
				}
				removePlayer(name);
				return;
			}
		}
	}
	public boolean isName(String name)
	{
		for(String key : players.keySet())
		{
			if(name.equalsIgnoreCase(key))
			{
				return true;
			}
		}
		for(PlayerData pd : players.values())
		{
			if(name.equalsIgnoreCase(pd.getChangeName()))
			{
				return true;
			}
		}
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(name.equalsIgnoreCase(player.getName()))
			{
				return true;
			}
		}
		return false;
	}
	public PlayerData getPlayer(String name)
	{
		return players.get(name);
	}
	public String getName(String name)
	{
		for(PlayerData pd : players.values())
		{
			if(name.equalsIgnoreCase(pd.getChangeName()))
			{
				return pd.getName();
			}
		}
		return name;
	}
	public String getNowName(String name)
	{
		for(PlayerData pd : players.values())
		{
			if(name.equalsIgnoreCase(pd.getChangeName()) || name.equalsIgnoreCase(pd.getName()))
			{
				return pd.isChange() ? pd.getChangeName() : pd.getName();
			}
		}
		return name;
	}
	public void addPlayer(String name, String changeName, String skin)
	{
		if(players.containsKey(name))
		{
			players.get(name).setData(changeName, skin);
		}
		else
		{
			players.put(name, new PlayerData(name, changeName, skin, false));
		}
		changePlayer(name, false);
	}
	public void removePlayer(String name)
	{
		changePlayer(name, false);
		players.remove(name);
	}
	public void changePlayer(String name, boolean change)
	{
		PlayerData pd = players.get(name);
		pd.setChange(change);
		Player player = Bukkit.getPlayer(name);
		if(player != null)
		{
			LinmaluPlayer.changePlayer(player, pd.getNowName(), pd.getNowSkin());
		}
		saveConfig();
	}
}
