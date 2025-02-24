package com.moffy5612.ccex;

import org.slf4j.Logger;

import com.moffy5612.ccex.handlers.CCEXFluxNetworksHandler;
import com.mojang.logging.LogUtils;

import net.minecraftforge.fml.common.Mod;

@Mod(CCEXRefs.MOD_ID)
public class CCEX {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CCEXFluxNetworksHandler FLUX_NETWORKS_HANDLER = new CCEXFluxNetworksHandler();

    public CCEX(){
        FLUX_NETWORKS_HANDLER.Handle();
    }
}
