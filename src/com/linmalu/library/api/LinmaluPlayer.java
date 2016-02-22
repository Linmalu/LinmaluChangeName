package com.linmalu.library.api;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.linmalu.LinmaluLibrary.packetwrapper.WrapperPlayServerEntityEquipment;
import com.linmalu.LinmaluLibrary.packetwrapper.WrapperPlayServerEntityHeadRotation;
import com.linmalu.LinmaluLibrary.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.linmalu.changename.Main;

public class LinmaluPlayer implements Runnable
{
	private static HashMap<UUID, LinmaluProfile> profiles = new HashMap<>();

	public static void change(Player player, String name, String skin)
	{
		new LinmaluPlayer(skin, new LinmaluPacket(player, name));
	}
	@SuppressWarnings("deprecation")
	public static Multimap<String, WrappedSignedProperty> getProperties(String skin)
	{
		OfflinePlayer player = Bukkit.getOfflinePlayer(skin);
		if(!profiles.containsKey(player.getUniqueId()))
		{
			new LinmaluPlayer(skin, null);
		}
		return profiles.get(player.getUniqueId()).getWrappedSignedProperty();
	}

	private OfflinePlayer player;
	private Runnable runnable;

	@SuppressWarnings("deprecation")
	private LinmaluPlayer(String skin, Runnable runnable)
	{
		player = Bukkit.getOfflinePlayer(skin);
		this.runnable = runnable;
		if(!profiles.containsKey(player.getUniqueId()))
		{
			profiles.put(player.getUniqueId(), new LinmaluProfile());
		}
		new Thread(this).start();
	}
	@Override
	public void run()
	{
		if(profiles.get(player.getUniqueId()).isTimeOut())
		{
			try
			{
				URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + player.getUniqueId().toString().replace("-", "") + "?unsigned=false");
				HttpURLConnection huc = (HttpURLConnection)url.openConnection();
				if(huc.getResponseCode() == HttpURLConnection.HTTP_OK)
				{
					InputStreamReader isr = new InputStreamReader(huc.getInputStream(), Charsets.UTF_8);
					JSONObject json = (JSONObject)new JSONParser().parse(isr);
					JSONArray properties = (JSONArray)json.get("properties");
					for (int i = 0; i < properties.size(); i++)
					{
						JSONObject property = (JSONObject)properties.get(i);
						String name = (String)property.get("name");
						String value = (String)property.get("value");
						String signature = (String)property.get("signature");
						WrappedSignedProperty wsp = new WrappedSignedProperty(name, value, signature);
						profiles.get(player.getUniqueId()).setWrappedSignedProperty(wsp);
					}
				}
				else if(huc.getResponseCode() == 429)
				{
					profiles.get(player.getUniqueId()).time = System.currentTimeMillis() - 590000;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(runnable != null)
		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getMain(), runnable);
		}
	}
	private static class LinmaluPacket implements Runnable
	{
		private Player player;
		private String name;

		private LinmaluPacket(Player player, String name)
		{
			this.player = player;
			this.name = name;
		}
		@Override
		public void run()
		{
			WrapperPlayServerNamedEntitySpawn nes = new WrapperPlayServerNamedEntitySpawn(player);
			nes.setProfile(name != null ? new WrappedGameProfile(player.getUniqueId(), name) : WrappedGameProfile.fromPlayer(player));
			player.setPlayerListName(nes.getPlayerName());
			WrapperPlayServerEntityHeadRotation ehr = new WrapperPlayServerEntityHeadRotation();
			ehr.setEntityId(player.getEntityId());
			ehr.setHeadYaw(player.getLocation().getYaw());
			WrapperPlayServerEntityEquipment ee = new WrapperPlayServerEntityEquipment();
			ee.setEntityId(player.getEntityId());
			ee.setSlot((short)0);
			ee.setItem(player.getItemInHand());
			WrapperPlayServerEntityEquipment ee1 = new WrapperPlayServerEntityEquipment();
			ee1.setEntityId(player.getEntityId());
			ee1.setSlot((short)1);
			ee1.setItem(player.getInventory().getBoots());
			WrapperPlayServerEntityEquipment ee2 = new WrapperPlayServerEntityEquipment();
			ee2.setEntityId(player.getEntityId());
			ee2.setSlot((short)2);
			ee2.setItem(player.getInventory().getLeggings());
			WrapperPlayServerEntityEquipment ee3 = new WrapperPlayServerEntityEquipment();
			ee3.setEntityId(player.getEntityId());
			ee3.setSlot((short)3);
			ee3.setItem(player.getInventory().getChestplate());
			WrapperPlayServerEntityEquipment ee4 = new WrapperPlayServerEntityEquipment();
			ee4.setEntityId(player.getEntityId());
			ee4.setSlot((short)4);
			ee4.setItem(player.getInventory().getHelmet());
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(player != p)
				{
					nes.sendPacket(p);
					ehr.sendPacket(p);
					ee.sendPacket(p);
					ee1.sendPacket(p);
					ee2.sendPacket(p);
					ee3.sendPacket(p);
					ee4.sendPacket(p);
				}
			}
		}
	}
	private class LinmaluProfile
	{
		private long time = 0;
		private WrappedSignedProperty wsp;

		public boolean isTimeOut()
		{
			if((System.currentTimeMillis() - time) > 600000)
			{
				time = System.currentTimeMillis();
				return true;
			}
			else
			{
				return false;
			}
		}
		public Multimap<String, WrappedSignedProperty> getWrappedSignedProperty()
		{
			if(wsp == null)
			{
				return null;
			}
			else
			{
				Multimap<String, WrappedSignedProperty> map = HashMultimap.create();
				map.put("textures", wsp);
				return map;
			}
		}
		public void setWrappedSignedProperty(WrappedSignedProperty wsp)
		{
			this.wsp = wsp;
			time = System.currentTimeMillis();
		}
	}
}
