package com.moffy5612.ccex.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.network.NetworkMember;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkData;
import sonar.fluxnetworks.common.connection.NetworkStatistics;
import sonar.fluxnetworks.common.device.TileFluxController;

public class FluxControllerPeripheral implements IPeripheral{

    private final BlockEntity tile;

    public FluxControllerPeripheral(BlockEntity tile){
        this.tile = tile;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof FluxControllerPeripheral && ((FluxControllerPeripheral) other).tile == this.tile;
    }

    @LuaFunction
    public Map<String, Object> getNetworkInfo(IComputerAccess computer, IArguments arguments) throws LuaException{
        Map<String, Object> infos = new HashMap<>();
        
        FluxNetwork network = getFluxNetwork();

        if(network != null){
            Level level = tile.getLevel();
            Player owner = level == null ? null : level.getPlayerByUUID(network.getOwnerUUID());

            infos.put("name", network.getNetworkName());
            infos.put("securityLevel", network.getSecurityLevel().getName());
            infos.put("owner", owner == null ? null : owner.getDisplayName());

            List<String> memberNames = new ArrayList<>();
            for(NetworkMember networkMember : network.getAllMembers()){
                memberNames.add(networkMember.getCachedName());
            }
            infos.put("members", memberNames);
        }
        return infos;
    }

    @LuaFunction
    public Map<String, Object> getConnections(IComputerAccess computer, IArguments arguments) throws LuaException{
        Map<String, Object> connections = new HashMap<>();
        
        FluxNetwork network = getFluxNetwork();

        if(network != null){
            for (IFluxDevice device : network.getAllConnections()){
                Map<String, Object> connectionInfo = new HashMap<>();
                Map<String, Object> connectionPos = new HashMap<>();
                
                connectionInfo.put("id", device.getCustomName());

                connectionInfo.put("name", device.getDisplayName().getContents());
                
                connectionPos.put("x", device.getGlobalPos().pos().getX());
                connectionPos.put("y", device.getGlobalPos().pos().getY());
                connectionPos.put("z", device.getGlobalPos().pos().getZ());
                connectionInfo.put("pos", connectionPos);

                connectionInfo.put("type", device.getDeviceType().name());
            }
        }

        return connections;
    }

    @LuaFunction
    public Map<String, Object> getStatistics(IComputerAccess computer, IArguments arguments) throws LuaException{
        Map<String, Object> statisticsInfo = new HashMap<>();
        
        FluxNetwork network = getFluxNetwork();

        if(network != null){
            NetworkStatistics statistics = network.getStatistics();
            statisticsInfo.put("totalEnergy", statistics.totalEnergy);
            statisticsInfo.put("totalBuffer", statistics.totalBuffer);
            statisticsInfo.put("fluxStorageCount", statistics.fluxStorageCount);
            statisticsInfo.put("fluxPlugCount", statistics.fluxPlugCount);
            statisticsInfo.put("fluxPointCount", statistics.fluxPointCount);
            statisticsInfo.put("fluxControllerCount", statistics.fluxControllerCount);
            statisticsInfo.put("energyInput", statistics.energyInput);
            statisticsInfo.put("energyOutput", statistics.energyOutput);
            statisticsInfo.put("averageTickMicro", statistics.averageTickMicro);
            statisticsInfo.put("energyChange", statistics.energyChange.toArray());
        }

        return statisticsInfo;
    }

    @Nullable
    private FluxNetwork getFluxNetwork() throws LuaException{
        if(!(tile instanceof TileFluxController))return null;

        TileFluxController fluxController = (TileFluxController)tile;
        int networkId = fluxController.getNetworkID();

        if(networkId == FluxConstants.INVALID_NETWORK_ID){
            throw new LuaException("Flux Controller has not been connected to any network yet.");
        }

        FluxNetwork network = FluxNetworkData.getNetwork(networkId);
        return network;
    }

    @Override
    @Nonnull
    public String getType() {
        return "fluxController";
    }

    public static class Provider implements IPeripheralProvider{
        @Override
        @Nonnull
        public LazyOptional<IPeripheral> getPeripheral(@Nonnull Level level, @Nonnull BlockPos blockPos,
                @Nonnull Direction direction) {
                    BlockEntity tile = level.getBlockEntity(blockPos);
                    if(tile instanceof TileFluxController){
                        return LazyOptional.of(()->new FluxControllerPeripheral(tile));
                    }
                    return LazyOptional.empty();
        }
        
    }

}
