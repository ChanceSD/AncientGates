package org.mcteam.ancientgates.util.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum InvBoolean {
	// Allow inventory
	TRUE,
	
	// Clear inventory
	CLEAR,
	
	// Disallow invnetory
	FALSE;
	
    private static final Map<String, InvBoolean> nameToInvBool = new HashMap<String, InvBoolean>();
    static {
        for (InvBoolean value : EnumSet.allOf(InvBoolean.class)) {
        	nameToInvBool.put(value.name(), value);
        }
    }
    
	public static InvBoolean fromName(String name) {
		return nameToInvBool.get(name);
	}
	
    public static final String[] names=new String[values().length];
    static {
        InvBoolean[] values=values();
        for(int i=0;i<values.length;i++)
            names[i]=values[i].name();
    }

}