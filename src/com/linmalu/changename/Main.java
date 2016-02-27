package com.linmalu.changename;

import com.linmalu.changename.data.GameData;
import com.linmalu.library.api.LinmaluMain;

public class Main extends LinmaluMain
{
	public static Main getMain()
	{
		return (Main) LinmaluMain.getMain();
	}

	private GameData gamedata;

	@Override
	public void onEnable()
	{
		gamedata = new GameData();
		registerCommand(new Main_Command());
		registerEvents(new Main_Event());
		new ProtocolLib_Event();
		getLogger().info("제작 : 린마루(Linmalu)");
	}
	@Override
	public void onDisable()
	{
		getLogger().info("제작 : 린마루(Linmalu)");
	}
	public GameData getGameData()
	{
		return gamedata;
	}
}
