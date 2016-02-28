package com.linmalu.changename;

import org.bukkit.Bukkit;

import com.comphenix.packetwrapper.WrapperPlayClientTabComplete;
import com.comphenix.packetwrapper.WrapperPlayServerTabComplete;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.linmalu.changename.data.GameData;

public class ProtocolLib_Event extends PacketAdapter
{
	private GameData data = Main.getMain().getGameData();

	public ProtocolLib_Event()
	{
		super(Main.getMain(), ListenerPriority.NORMAL, PacketType.Play.Server.TAB_COMPLETE, PacketType.Play.Client.TAB_COMPLETE);
		ProtocolLibrary.getProtocolManager().addPacketListener(this);
	}
	@Override
	public void onPacketSending(PacketEvent event)
	{
		if(event.getPacketType() == PacketType.Play.Server.TAB_COMPLETE)
		{
			WrapperPlayServerTabComplete packet = new WrapperPlayServerTabComplete(event.getPacket());
			String[] tabs = packet.getMatches();
			for(String player : tabs)
			{
				if(Bukkit.getPlayer(player) == null)
				{
					return;
				}
			}
			for(int i = 0; i < tabs.length; i++)
			{
				tabs[i] = data.getNowName(tabs[i]);
			}
		}
	}
	@Override
	public void onPacketReceiving(PacketEvent event)
	{
		if(event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE)
		{
			WrapperPlayClientTabComplete packet = new WrapperPlayClientTabComplete(event.getPacket());
			String s = packet.getText();
			if(!s.endsWith(" "))
			{
				int i = s.lastIndexOf(" ");
				if(i != -1)
				{
					packet.setText(s.substring(0, i + 1));
				}
			}
		}
	}
}
