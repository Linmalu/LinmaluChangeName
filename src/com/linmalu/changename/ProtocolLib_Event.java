package com.linmalu.changename;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.linmalu.LinmaluLibrary.packetwrapper.WrapperPlayClientTabComplete;
import com.linmalu.LinmaluLibrary.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.linmalu.changename.data.GameData;

public class ProtocolLib_Event extends PacketAdapter
{
	private GameData data = Main.getMain().getGameData();

	public ProtocolLib_Event()
	{
		super(Main.getMain(), ListenerPriority.NORMAL, PacketType.Play.Server.TAB_COMPLETE, PacketType.Play.Server.NAMED_ENTITY_SPAWN, PacketType.Play.Client.TAB_COMPLETE);
	}
	@Override
	public void onPacketSending(PacketEvent event)
	{
		if(event.getPacketType() == PacketType.Play.Server.TAB_COMPLETE)
		{
			StructureModifier<String[]> sm = event.getPacket().getStringArrays();
			String[] tabs = (String[])sm.read(0);
			if(tabs.length == Bukkit.getOnlinePlayers().length)
			{
				ArrayList<String> list = new ArrayList<>();
				for(String tab : tabs)
				{
					if(data.isChange(tab))
					{
						list.add(data.getNowName(tab));
					}
					else
					{
						list.add(tab);
					}
				}
				sm.write(0, list.toArray(new String[list.size()]));
			}
		}
		else if(event.getPacketType() == PacketType.Play.Server.NAMED_ENTITY_SPAWN)
		{
			WrapperPlayServerNamedEntitySpawn nes = new WrapperPlayServerNamedEntitySpawn(event.getPacket());
			if(data.isChange(nes.getPlayerName()))
			{
				String name = data.getNowName(nes.getPlayerName());
				WrappedGameProfile gp = new WrappedGameProfile(nes.getPlayerUUID(), name);
				gp.getProperties().putAll(nes.getProfile().getProperties());
				nes.setProfile(gp);
			}
		}
	}
	@Override
	public void onPacketReceiving(PacketEvent event)
	{
		if(event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE)
		{
			WrapperPlayClientTabComplete tc = new WrapperPlayClientTabComplete(event.getPacket());
			String s = tc.getText();
			if(!s.endsWith(" "))
			{
				int i = s.lastIndexOf(" ");
				if(i != -1)
				{
					tc.setText(s.substring(0, i + 1));
				}
			}
		}
	}
}
