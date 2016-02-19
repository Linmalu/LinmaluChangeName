package com.linmalu.changename;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.linmalu.changename.data.GameData;

public class Main extends JavaPlugin
{
	private static Main main;
	private GameData gamedata;

	public void onEnable()
	{
		main = this;
		gamedata = new GameData();
		getCommand(getDescription().getName()).setExecutor(new Main_Command());
		getServer().getPluginManager().registerEvents(new Main_Event(), this);
		ProtocolLibrary.getProtocolManager().addPacketListener(new ProtocolLib_Event());
		getLogger().info("제작 : 린마루(Linmalu)");
	}
	public void onDisable()
	{
		getLogger().info("제작 : 린마루(Linmalu)");
	}
	public static Main getMain()
	{
		return main;
	}
	public GameData getGameData()
	{
		return gamedata;
	}
	public String getTitle()
	{
		return ChatColor.AQUA + "[" + getDescription().getName() + "] ";
	}
}
