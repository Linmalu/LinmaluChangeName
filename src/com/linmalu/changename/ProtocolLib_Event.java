package com.linmalu.changename;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import com.comphenix.packetwrapper.WrapperPlayClientTabComplete;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.packetwrapper.WrapperPlayServerTabComplete;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.linmalu.changename.data.GameData;
import com.linmalu.changename.data.PlayerData;
import com.linmalu.library.api.LinmaluPlayer;

public class ProtocolLib_Event extends PacketAdapter
{
	private GameData data = Main.getMain().getGameData();

	public ProtocolLib_Event()
	{
		super(Main.getMain(), ListenerPriority.NORMAL, PacketType.Play.Server.TAB_COMPLETE, PacketType.Play.Server.PLAYER_INFO, PacketType.Play.Client.TAB_COMPLETE);
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
		else if(event.getPacketType() == PacketType.Play.Server.PLAYER_INFO)
		{
			WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(event.getPacket());
			if(packet.getAction() == PlayerInfoAction.ADD_PLAYER)
			{
				ArrayList<PlayerInfoData> list = new ArrayList<>();
				for(PlayerInfoData info : packet.getData())
				{
					PlayerData pd = data.getPlayer(data.getName(info.getProfile().getName()));
					if(pd != null && pd.isChange())
					{
						WrappedGameProfile gp = new WrappedGameProfile(info.getProfile().getUUID(), pd.getNowName());
						gp.getProperties().putAll(LinmaluPlayer.getWrappedSignedPropertys(pd.getNowSkin()));
						list.add(new PlayerInfoData(gp, info.getPing(), info.getGameMode(), WrappedChatComponent.fromText(gp.getName())));
					}
					else
					{
						list.add(info);
					}
				}
				packet.setData(list);
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
