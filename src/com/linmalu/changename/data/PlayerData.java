package com.linmalu.changename.data;


public class PlayerData
{
	private String name;
	private String changeName;
	private String skin;
	private boolean change;

	public PlayerData(String name, String changeName, String skin, boolean change)
	{
		this.name = name;
		this.changeName = changeName;
		this.skin = skin == null ? name : skin;
		this.change = change;
	}
	public String getName()
	{
		return name;
	}
	public String getChangeName()
	{
		return changeName;
	}
	public String getSkin()
	{
		return skin;
	}
	public void setData(String changeName, String skin)
	{
		this.changeName = changeName;
		this.skin = skin;
		change = false;
	}
	public boolean isChange()
	{
		return change;
	}
	public void setChange(boolean change)
	{
		this.change = change;
	}
	public String getNowName()
	{
		return change ? changeName : name;
	}
	public String getNowSkin()
	{
		return change ? skin : name;
	}
}
