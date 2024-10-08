package net.tracen.epicumapyoi;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EpicUmapyoi.MODID)
public class EpicUmapyoi
{
    // Define mod id in a common place for everything to reference
	public static final String MODID = "epicumapyoi";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    
	public static Logger getLogger() {
		return LOGGER;
	}
    
}
