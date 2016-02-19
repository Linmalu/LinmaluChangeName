package com.linmalu.changename.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.linmalu.LinmaluLibrary.packetwrapper.WrapperPlayServerEntityEquipment;
import com.linmalu.LinmaluLibrary.packetwrapper.WrapperPlayServerEntityHeadRotation;
import com.linmalu.LinmaluLibrary.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.linmalu.changename.Main;
import com.linmalu.library.api.LinmaluYamlConfiguration;

public class GameData
{
	private LinmaluYamlConfiguration config;
	private HashMap<String, PlayerData> names = new HashMap<>();
	private final String changeName = "이름";
	private final String change = "상태";

	public GameData()
	{
		reloadConfig();
	}
	public void reloadConfig()
	{
		names.clear();
		config = LinmaluYamlConfiguration.loadConfiguration(new File(Main.getMain().getDataFolder(), "config.yml"));
		for(String name : config.getKeys(false))
		{
			String changeName = config.getString(name + "." + this.changeName);
			boolean change = config.getBoolean(name + "." + this.change);
			if(changeName != null)
			{
				names.put(name, new PlayerData(name, changeName, change));
			}
		}
		for(Player player : Bukkit.getOnlinePlayers())
		{
			refreshPlayer(player);
		}
	}
	public void saveConfig()
	{
		for(PlayerData pd : names.values())
		{
			config.set(pd.getName() + "." + changeName, pd.getChangeName());
			config.set(pd.getName() + "." + change, pd.isChange());
		}
		try
		{
			config.save(new File(Main.getMain().getDataFolder(), "config.yml"));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public boolean isName(String name)
	{
		return names.containsKey(name);
	}
	public boolean isChangeName(String name)
	{
		for(PlayerData pd : names.values())
		{
			if(name.equals(pd.getChangeName()))
			{
				return true;
			}
		}
		return false;
	}
	public boolean isChange(String name)
	{
		if(isName(name))
		{
			return getPlayerData(name).isChange();
		}
		return false;
	}
	public String getName(String name)
	{
		if(isChangeName(name))
		{
			for(PlayerData pd : names.values())
			{
				if(name.equals(pd.getChangeName()))
				{
					return pd.getName();
				}
			}
		}
		return name;
	}
	public String getChangeName(String name)
	{
		if(isName(name))
		{
			return getPlayerData(name).getChangeName();
		}
		return name;
	}
	public String getNowName(String name)
	{
		for(PlayerData pd : names.values())
		{
			if(name.equals(pd.getChangeName()) || name.equals(pd.getName()))
			{
				return pd.isChange() ? pd.getChangeName() : pd.getName();
			}
		}
		return name;
	}
	public PlayerData getPlayerData(String name)
	{
		return names.get(name);
	}
	public void addName(Player player, String name)
	{
		if(isChange(player.getName()))
		{
			changePlayer(player, false);
		}
		names.put(player.getName(), new PlayerData(player.getName(), name, false));
		saveConfig();
	}
	public void removeName(Player player)
	{
		if(isChange(player.getName()))
		{
			changePlayer(player, false);
		}
		names.remove(player.getName());
		saveConfig();
	}
	public void changePlayer(Player player, boolean change)
	{
		getPlayerData(player.getName()).setChange(change);
		saveConfig();
		refreshPlayer(player);
	}
	private void refreshPlayer(Player player)
	{
		player.setPlayerListName(getNowName(player.getName()));
		WrapperPlayServerNamedEntitySpawn nes = new WrapperPlayServerNamedEntitySpawn(player);
		WrapperPlayServerEntityEquipment e = new WrapperPlayServerEntityEquipment();
		e.setEntityId(player.getEntityId());
		e.setSlot((short)0);
		e.setItem(player.getItemInHand());
		WrapperPlayServerEntityEquipment e1 = new WrapperPlayServerEntityEquipment();
		e1.setEntityId(player.getEntityId());
		e1.setSlot((short)1);
		e1.setItem(player.getInventory().getBoots());
		WrapperPlayServerEntityEquipment e2 = new WrapperPlayServerEntityEquipment();
		e2.setEntityId(player.getEntityId());
		e2.setSlot((short)2);
		e2.setItem(player.getInventory().getLeggings());
		WrapperPlayServerEntityEquipment e3 = new WrapperPlayServerEntityEquipment();
		e3.setEntityId(player.getEntityId());
		e3.setSlot((short)3);
		e3.setItem(player.getInventory().getChestplate());
		WrapperPlayServerEntityEquipment e4 = new WrapperPlayServerEntityEquipment();
		e4.setEntityId(player.getEntityId());
		e4.setSlot((short)4);
		e4.setItem(player.getInventory().getHelmet());
		WrapperPlayServerEntityHeadRotation ehr = new WrapperPlayServerEntityHeadRotation();
		ehr.setEntityId(player.getEntityId());
		ehr.setHeadYaw(player.getLocation().getYaw());
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(player != p)
			{
				nes.sendPacket(p);
				e.sendPacket(p);
				e1.sendPacket(p);
				e2.sendPacket(p);
				e3.sendPacket(p);
				e4.sendPacket(p);
				ehr.sendPacket(p);
			}
		}
	}
}
