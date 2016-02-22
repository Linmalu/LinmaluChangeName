package com.linmalu.changename;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.collect.Multimap;
import com.linmalu.LinmaluLibrary.packetwrapper.WrapperPlayClientTabComplete;
import com.linmalu.LinmaluLibrary.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.linmalu.changename.data.GameData;
import com.linmalu.changename.data.PlayerData;
import com.linmalu.library.api.LinmaluPlayer;

public class ProtocolLib_Event extends PacketAdapter
{
	private GameData data = Main.getMain().getGameData();

	public ProtocolLib_Event()
	{
		super(Main.getMain(), ListenerPriority.NORMAL, PacketType.Play.Server.TAB_COMPLETE, PacketType.Play.Server.NAMED_ENTITY_SPAWN, PacketType.Play.Client.TAB_COMPLETE);
	}
	@Override
	@SuppressWarnings("deprecation")
	public void onPacketSending(PacketEvent event)
	{
		if(event.getPacketType() == PacketType.Play.Server.TAB_COMPLETE)
		{
			StructureModifier<String[]> sm = event.getPacket().getStringArrays();
			String[] tabs = (String[])sm.read(0);
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
		else if(event.getPacketType() == PacketType.Play.Server.NAMED_ENTITY_SPAWN)
		{
			WrapperPlayServerNamedEntitySpawn nes = new WrapperPlayServerNamedEntitySpawn(event.getPacket());
			Player player = Bukkit.getPlayer(UUID.fromString(nes.getPlayerUUID()));
			String name = data.getNowName(nes.getPlayerName());
			WrappedGameProfile wgp = new WrappedGameProfile(nes.getPlayerUUID(), name);
			PlayerData pd = data.getPlayer(player.getName());
			Multimap<String, WrappedSignedProperty> wsp = LinmaluPlayer.getProperties(pd == null ? player.getName() : pd.getNowSkin());
			wgp.getProperties().putAll(wsp == null ? WrappedGameProfile.fromPlayer(player).getProperties() : wsp);
			nes.setProfile(wgp);
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
