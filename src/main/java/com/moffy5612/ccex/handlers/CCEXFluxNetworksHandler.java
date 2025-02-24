package com.moffy5612.ccex.handlers;

import com.moffy5612.addonlib.api.ContentHandlerBase;
import com.moffy5612.ccex.api.CCEXUtilsApi;
import com.moffy5612.ccex.api.FluxControllerPeripheral;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CCEXFluxNetworksHandler extends ContentHandlerBase{

    @Override
    public void setup(FMLCommonSetupEvent event) {
        ComputerCraftAPI.registerAPIFactory(new CCEXUtilsApi.Factory());
        ComputerCraftAPI.registerPeripheralProvider(new FluxControllerPeripheral.Provider());
    }

    @Override
    public String[] getRequiredModIds() {
        return new String[]{"fluxnetworks"};
    }
}
