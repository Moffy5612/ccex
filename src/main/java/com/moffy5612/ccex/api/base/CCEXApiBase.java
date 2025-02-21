package com.moffy5612.ccex.api.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dan200.computercraft.api.lua.IComputerSystem;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.ILuaAPIFactory;

public abstract class CCEXApiBase implements ILuaAPI{

    public CCEXApiBase(){
        
    }

    public Factory getFactory(){
        return new Factory(this);
    }

    private class Factory implements ILuaAPIFactory{

        private CCEXApiBase api;

        public Factory(CCEXApiBase api){
            this.api = api;
        }

        @Override
        @Nullable
        public ILuaAPI create(@Nonnull IComputerSystem computerSystem) {
            return api;
        }
    }
}
