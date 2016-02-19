package com.linmalu.changename;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.linmalu.LinmaluLibrary.API.LinmaluTellraw;
import com.linmalu.changename.data.GameData;
import com.linmalu.library.api.LinmaluVersion;

public class Main_Command implements CommandExecutor
{
	public Main_Command()
	{
		Main.getMain().getCommand(Main.getMain().getDescription().getName()).setTabCompleter(new TabCompleter()
		{			
			public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
			{
				ArrayList<String> list = new ArrayList<>();
				if(args.length == 1 && sender.isOp())
				{
					list.add("등록");
					list.add("변경");
					list.add("취소");
					list.add("삭제");
					list.add("확인");
					list.add("리로드");
					list.add("관리자");
					return list;
				}
				else if(args.length == 3 && args[0].equals("관리자") && sender.isOp())
				{
					list.add("등록");
					list.add("변경");
					list.add("취소");
					list.add("삭제");
					return list;
				}
				return null;
			}
		});
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(sender instanceof Player && sender.isOp())
		{
			Player player = (Player)sender;
			GameData data = Main.getMain().getGameData();
			if(args.length == 2 && args[0].equals("등록"))
			{
				if(args[1].length() > 16)
				{
					sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "이름 길이가 16자를 넘어갑니다.");
				}
				else if(data.isChangeName(args[1]))
				{
					sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "이미 존재하는 이름입니다.");
				}
				else
				{
					data.addName(player, args[1]);
					sender.sendMessage(Main.getMain().getTitle() + ChatColor.GOLD + args[1] + ChatColor.GREEN + "이름이 등록되었습니다.");
				}
				return true;
			}
			else if(args.length == 1)
			{
				if(args[0].equals("변경"))
				{
					if(!data.isName(player.getName()))
					{
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "이름이 등록되지 않았습니다.");
					}
					else if(data.isChange(player.getName()))
					{
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "이름을 변경한 상태입니다.");
					}
					else
					{
						data.changePlayer(player, true);
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.GOLD + data.getChangeName(player.getName()) + ChatColor.GREEN + "이름으로 변경되었습니다.");
					}
					return true;
				}
				else if(args[0].equals("취소"))
				{
					if(!data.isName(player.getName()))
					{
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "이름이 등록되지 않았습니다.");
					}
					else if(!data.isChange(player.getName()))
					{
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "이름을 변경하지 않았습니다.");
					}
					else
					{
						data.changePlayer(player, false);
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "이름 변경이 취소되었습니다.");
					}
					return true;
				}
				else if(args[0].equals("삭제"))
				{
					if(!data.isName(player.getName()))
					{
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "이름이 등록되지 않았습니다.");
					}
					else
					{
						data.removeName(player);
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "등록된 이름이 삭제되었습니다.");
					}
					return true;
				}
				else if(args[0].equals("확인"))
				{
					if(!data.isName(player.getName()))
					{
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "이름이 등록되지 않았습니다.");
					}
					else
					{
						sender.sendMessage(Main.getMain().getTitle() + ChatColor.GOLD + data.getChangeName(player.getName()) + ChatColor.GREEN + "이름이 등록되어있습니다.");
					}
					return true;
				}
				else if(args[0].equals("리로드"))
				{
					data.reloadConfig();
					sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "설정 파일을 다시 불러왔습니다.");
					return true;
				}
			}
			if(args.length >= 3 && args[0].equals("관리자"))
			{
				@SuppressWarnings("deprecation")
				Player p = Bukkit.getPlayer(args[1]);
				if(p != null)
				{
					if(args.length == 4 && args[2].equals("등록"))
					{
						if(args[3].length() > 16)
						{
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "이름 길이가 16자를 넘어갑니다.");
						}
						else if(data.isChangeName(args[3]))
						{
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "이미 존재하는 이름입니다.");
						}
						else
						{
							data.addName(p, args[3]);
							sender.sendMessage(Main.getMain().getTitle() + ChatColor.GOLD + args[3] + ChatColor.GREEN + "이름이 등록되었습니다.");
							p.sendMessage(Main.getMain().getTitle() + ChatColor.GOLD + args[3] + ChatColor.GREEN + "이름이 등록되었습니다.");
						}
						return true;
					}
					else if(args.length == 3)
					{
						if(args[2].equals("변경"))
						{
							if(!data.isName(p.getName()))
							{
								sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "이름이 등록되지 않았습니다.");
							}
							else if(data.isChange(p.getName()))
							{
								sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "이름을 변경한 상태입니다.");
							}
							else
							{
								data.changePlayer(p, true);
								sender.sendMessage(Main.getMain().getTitle() + ChatColor.GOLD + data.getChangeName(p.getName()) + ChatColor.GREEN + "이름으로 변경되었습니다.");
								p.sendMessage(Main.getMain().getTitle() + ChatColor.GOLD + data.getChangeName(p.getName()) + ChatColor.GREEN + "이름으로 변경되었습니다.");
							}
							return true;
						}
						else if(args[2].equals("취소"))
						{
							if(!data.isName(p.getName()))
							{
								sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "이름이 등록되지 않았습니다.");
							}
							else if(!data.isChange(p.getName()))
							{
								sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "이름을 변경하지 않았습니다.");
							}
							else
							{
								data.changePlayer(p, false);
								sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "이름 변경이 취소되었습니다.");
								p.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "이름 변경이 취소되었습니다.");
							}
							return true;
						}
						else if(args[2].equals("삭제"))
						{
							if(!data.isName(p.getName()))
							{
								sender.sendMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "이름이 등록되지 않았습니다.");
							}
							else
							{
								data.removeName(p);
								sender.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "등록된 이름이 삭제되었습니다.");
								p.sendMessage(Main.getMain().getTitle() + ChatColor.GREEN + "등록된 이름이 삭제되었습니다.");
							}
							return true;
						}
					}
				}
				else
				{
					sender.sendMessage(Main.getMain().getTitle() + ChatColor.GOLD + args[1] + ChatColor.YELLOW + "플레이어는 접속 중이 아닙니다.");
					return true;
				}
			}
			sender.sendMessage(ChatColor.GREEN + " = = = = = [ Linmalu Change Name ] = = = = =");
			LinmaluTellraw.sendCmdChat(player, "/" + label + " 등록 ", ChatColor.GOLD + "/" + label + " 등록 <이름>" + ChatColor.GRAY + " : 이름을 등록합니다.");
			LinmaluTellraw.sendCmdChat(player, "/" + label + " 변경", ChatColor.GOLD + "/" + label + " 변경" + ChatColor.GRAY + " : 이름을 변경합니다.");
			LinmaluTellraw.sendCmdChat(player, "/" + label + " 취소", ChatColor.GOLD + "/" + label + " 취소" + ChatColor.GRAY + " : 이름 변경을 취소합니다.");
			LinmaluTellraw.sendCmdChat(player, "/" + label + " 삭제", ChatColor.GOLD + "/" + label + " 삭제" + ChatColor.GRAY + " : 등록된 이름을 삭제합니다.");
			LinmaluTellraw.sendCmdChat(player, "/" + label + " 확인", ChatColor.GOLD + "/" + label + " 확인" + ChatColor.GRAY + " : 등록된 이름을 확인합니다.");
			LinmaluTellraw.sendCmdChat(player, "/" + label + " 리로드", ChatColor.GOLD + "/" + label + " 리로드" + ChatColor.GRAY + " : 설정 파일을 다시 불러옵니다.");
			LinmaluTellraw.sendCmdChat(player, "/" + label + " 관리자 ", ChatColor.GOLD + "/" + label + " 관리자 <플레이어> <등록/변경/취소/삭제> (이름)" + ChatColor.GRAY + " : 다른 플레이어를 설정합니다.");
			sender.sendMessage(ChatColor.YELLOW + "제작자 : " + ChatColor.AQUA + "린마루(Linmalu)" + ChatColor.WHITE + " - http://blog.linmalu.com");
			sender.sendMessage(ChatColor.YELLOW + "카페 : " + ChatColor.WHITE + "http://cafe.naver.com/craftproducer");
			LinmaluVersion.check(Main.getMain(), player);
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "권한이 없습니다.");
		}
		return true;
	}
}
