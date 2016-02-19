package com.linmalu.changename.data;


public class PlayerData
{
	private String name;
	private String changeName;
	private boolean change;

	public PlayerData(String name, String changeName, boolean change)
	{
		this.name = name;
		this.changeName = changeName;
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
	public boolean isChange()
	{
		return change;
	}
	public void setChange(boolean change)
	{
		this.change = change;
	}
}
