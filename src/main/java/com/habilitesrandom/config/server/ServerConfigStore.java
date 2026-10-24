package com.habilitesrandom.config.server;
import com.google.gson.*;import com.habilitesrandom.*;import net.fabricmc.loader.api.FabricLoader;import java.io.*;import java.nio.charset.StandardCharsets;import java.nio.file.*;
/** Reloadable JSON policy; malformed files fall back safely without stopping the server. */
public final class ServerConfigStore {private static final Gson GSON=new GsonBuilder().setPrettyPrinting().create();private static volatile ServerConfig config=load();private ServerConfigStore(){}
 public static ServerConfig get(){return config;} public static synchronized void reload(){config=load();}
 private static ServerConfig load(){Path p=FabricLoader.getInstance().getConfigDir().resolve(HabilitesRandom.MOD_ID).resolve("server.json");ServerConfig c=new ServerConfig();try{Files.createDirectories(p.getParent());if(Files.isRegularFile(p)){try(var r=Files.newBufferedReader(p,StandardCharsets.UTF_8)){ServerConfig read=GSON.fromJson(r,ServerConfig.class);if(read!=null)c=read;}}c.sanitize();try(var w=Files.newBufferedWriter(p,StandardCharsets.UTF_8)){GSON.toJson(c,w);}}catch(Exception e){HabilitesRandom.LOGGER.warn("Could not load server policy",e);}return c;}
}
