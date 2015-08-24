package org.mcteam.ancientgates.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.mcteam.ancientgates.Conf;

public class TextUtil {
	
    public static final int PAGEHEIGHT_PLAYER = 9;
    public static final int PAGEHEIGHT_CONSOLE = 50;
	
	public static Map<String, ChatColor> chatColors;
	static {
		chatColors = new HashMap<String, ChatColor>();
		  for (ChatColor c : ChatColor.values()) {
			  chatColors.put(c.name(), c);
		  }
	}
	
	public static String titleize(String str) {
		String line = Conf.colorChrome+repeat("_", 60);
		String center = ".[ " + Conf.colorSystem + str + Conf.colorChrome + " ].";
		int pivot = line.length() / 2;
		int eatLeft = center.length() / 2;
		int eatRight = center.length() - eatLeft;
		return line.substring(0, pivot - eatLeft) + center + line.substring(pivot + eatRight);
	}
	
	public static String repeat(String s, int times) {
	    if (times <= 0) return "";
	    else return s + repeat(s, times-1);
	}
	
	public static ArrayList<String> split(String str, String delim) {
		return new ArrayList<String>(Arrays.asList(str.split(delim)));
	}
	
	public static Boolean containsSubString(List<String> strList, String subStr) {
		for (String str : strList) {
			if (str.contains(subStr)) return true;
		}
		return false;
	}
	
	public static String implode(List<String> list, String glue) {
	    String ret = "";
	    for (int i=0; i<list.size(); i++) {
	        if (i!=0) {
	        	ret += glue;
	        }
	        ret += list.get(i);
	    }
	    return ret;
	}
	
	public static String implode(List<String> list) {
		return implode(list, " ");
	}
	
	public static ArrayList<String> concatenate(List<String> list1, List<String> list2, List<String> list3) {
		ArrayList<String> ret = new ArrayList<String>();
	    for (int i=0; i<list1.size(); i++) {
	        ret.add(list1.get(i)+" "+Conf.colorChrome+"("+list2.get(i)+" - "+list3.get(i)+Conf.colorChrome+")");
	    }
	    return ret;
	}
	
	public static ArrayList<String> concatenate(List<String> list1, List<String> list2) {
		ArrayList<String> ret = new ArrayList<String>();
	    for (int i=0; i<list1.size(); i++) {
	        ret.add(list1.get(i)+Conf.colorChrome+"("+list2.get(i)+Conf.colorChrome+")");
	    }
	    return ret;
	}
	
	public static String md5(String md5) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; i++) {
				sb.append(Integer.toHexString(array[i] & 0xFF | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// Ignore, will return null
		}
		return null;
	}
	
	public static boolean isBoolean(String s) {
	    return "true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s);
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
	
    public static ArrayList<String> getPage(List<String> lines, int pageHumanBased, String title, CommandSender sender) {
    	return getPage(lines, pageHumanBased, title, (sender instanceof Player) ? PAGEHEIGHT_PLAYER : PAGEHEIGHT_CONSOLE);
    }
    
    public static ArrayList<String> getPage(List<String> lines, int pageHumanBased, String title, int pageheight) {
    	ArrayList<String> ret = new ArrayList<String>();
    	int pageZeroBased = pageHumanBased - 1;
    	int pagecount = (int)Math.ceil(((double)lines.size()) / pageheight);
            
    	ret.add(titleize(title+" "+pageHumanBased+"/"+pagecount));
            
    	if (pagecount == 0) {
    		ret.add("Sorry. No Pages available.");
    		return ret;
    	} else if (pageZeroBased < 0 || pageHumanBased > pagecount) {
    		ret.add("Invalid page. Must be between 1 and "+pagecount);
    		return ret;
    	}
            
    	int from = pageZeroBased * pageheight;
    	int to = from+pageheight;
    	if (to > lines.size()) {
    		to = lines.size();
    	}
            
    	ret.addAll(lines.subList(from, to));
    	return ret;
    }
	
}


