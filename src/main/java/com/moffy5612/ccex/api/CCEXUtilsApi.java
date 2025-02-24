package com.moffy5612.ccex.api;

import java.util.List;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.moffy5612.ccex.CCEX;

import dan200.computercraft.api.lua.IComputerSystem;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.ILuaAPIFactory;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;

public class CCEXUtilsApi implements ILuaAPI{

    private String getJSONValue(Object value){
        if(value instanceof String){
            return "\"" + value.toString().replace("\"", "\\\"") + "\"";
        } else {
            return value.toString();
        }
    }

    private String serialize(Map<?,?>table){
        List<String> values = new ArrayList<>();
        boolean isList = true;
        for(Entry<?, ?> o : table.entrySet()){
            if(!(o.getKey() instanceof Double)){
                isList = false;
            }
            if(o.getValue() instanceof Map){
                values.add("\""+o.getKey()+"\":"+serialize(((Map<?,?>)o.getValue())));
            } else {
                values.add("\""+o.getKey()+"\":"+getJSONValue(o.getValue()));                
            }
        }
        if(isList){
            List<String> listValues = new ArrayList<>();
            for(Object o: table.values()){
                if(o instanceof Map){
                    listValues.add(serialize((Map<?,?>)o));
                } else {
                    listValues.add(getJSONValue(o));
                }
            }
            return "["+String.join("," , listValues)+"]";
        }else{
            return "{"+String.join(",", values)+"}";
        }
    }

    @LuaFunction
    public String serializeJSON(Map<?,?>table) throws LuaException{
        return serialize(table);
    }

    @LuaFunction
    public final Object deserializeJSON(String json) throws LuaException{
        Gson gson = new Gson();
        Type type = new TypeToken<Object>(){}.getType();

        Object temp = gson.fromJson(json, type);

        if(temp instanceof Map){
            Map<Object, Object> resultMap = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>)temp).entrySet()) {
                if(entry.getKey() instanceof String){
                    try {
                        Double key = Double.parseDouble((String)entry.getKey());
                        resultMap.put(key.doubleValue(), entry.getValue());
                    } catch (NumberFormatException e) {
                        resultMap.put(entry.getKey(),entry.getValue());
                    }
                }
            }
            return resultMap;
        } else if(temp instanceof List){
            return (List<?>)temp;
        }

        return temp;
    }

    @Override
    public String[] getNames() {
        return new String[]{"ccexUtils"};
    }

    public static class Factory implements ILuaAPIFactory{

        @Override
        @Nullable
        public ILuaAPI create(@Nonnull IComputerSystem computer) {
          return new CCEXUtilsApi();  
        }
        
    }
}
