package com.linmalu.changename;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.linmalu.changename.data.GameData;
import com.linmalu.library.api.LinmaluVersion;

public class Main_Event implements Listener
{
	private GameData data = Main.getMain().getGameData();

	@EventHandler
	public void Event(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if(player.isOp())
		{
			LinmaluVersion.check(Main.getMain(), player);
		}
		if(data.isChange(player.getName()))
		{
			player.setPlayerListName(data.getNowName(player.getName()));
		}
	}
	@EventHandler
	public void Event(AsyncPlayerChatEvent event)
	{
		event.setFormat(event.getFormat().replace("%1$s", data.getNowName(event.getPlayer().getName())));
	}
	//	@EventHandler
	//	public void Event(PlayerCommandPreprocessEvent event)
	//	{
	//		String msg = "";
	//		boolean first = true;
	//		for(String name : event.getMessage().split(" "))
	//		{
	//			if(first)
	//			{
	//				first = false;
	//			}
	//			else
	//			{
	//				msg += " ";
	//			}
	//			msg += data.getName(name);
	//		}
	//		event.setMessage(msg);
	//		Bukkit.broadcastMessage("작동");
	//	}
}
