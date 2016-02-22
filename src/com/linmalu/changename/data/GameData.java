package com.linmalu.changename.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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
	public boolean isChangeName(String name)
	{
		for(PlayerData pd : players.values())
		{
			if(name.equals(pd.getChangeName()))
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
			if(name.equals(pd.getChangeName()))
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
			if(name.equals(pd.getChangeName()) || name.equals(pd.getName()))
			{
				return pd.isChange() ? pd.getChangeName() : pd.getName();
			}
		}
		return name;
	}
	public void addPlayer(Player player, String name, String skin)
	{
		if(players.containsKey(player.getName()))
		{
			players.get(player.getName()).setData(name, skin);
		}
		else
		{
			players.put(player.getName(), new PlayerData(player.getName(), name, skin, false));
		}
		changePlayer(player, false);
	}
	public void removePlayer(Player player)
	{
		changePlayer(player, false);
		players.remove(player.getName());
	}
	public void changePlayer(Player player, boolean change)
	{
		PlayerData pd = players.get(player.getName());
		pd.setChange(change);
		LinmaluPlayer.change(player, pd.getNowName(), pd.getNowSkin());
		saveConfig();
	}
}
