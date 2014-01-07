package org.mcteam.ancientgates.util.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public enum GateMaterial {
	// AIR
	AIR("psudeo air blocks", Material.PISTON_MOVING_PIECE),
	
	// LAVA
	LAVA("stationary lava blocks", Material.STATIONARY_LAVA),
	
	// NETHER/ENDER PORTAL
	PORTAL("nether/ender portal blocks", Material.PORTAL),
	
	// SUGARCANE
	SUGARCANE("sugarcane blocks", Material.SUGAR_CANE_BLOCK),
	
	// WATER
	WATER("stationary water blocks", Material.STATIONARY_WATER),
	
	// WEB
	WEB("spiders web blocks", Material.WEB);
	
    private static final Map<String, GateMaterial> nameToMaterial = new HashMap<String, GateMaterial>();
    static {
        for (GateMaterial value : EnumSet.allOf(GateMaterial.class)) {
            nameToMaterial.put(value.name(), value);
        }
    }
    
	public static GateMaterial fromName(String name) {
		return nameToMaterial.get(name);
	}
    
    public static final String[] names=new String[values().length];
    static {
        GateMaterial[] values=values();
        for(int i=0;i<values.length;i++)
            names[i]=values[i].name();
    }
    
	protected final String desc;
	public String getDesc() { return this.desc; }
    
	protected final Material material;
	public Material getMaterial() { return this.material; }
	
	private GateMaterial(final String desc, final Material material) {
		this.desc = desc;
		this.material = material;
	}

}