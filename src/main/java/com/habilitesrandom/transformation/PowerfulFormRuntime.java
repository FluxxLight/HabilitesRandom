package com.habilitesrandom.transformation;
import com.habilitesrandom.data.*;import com.habilitesrandom.network.NetworkSync;import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
/** Checks active forms once per second; daily availability itself is computed lazily from Overworld time. */
public final class PowerfulFormRuntime {private static int ticks;private PowerfulFormRuntime(){}
 public static void register(){ServerTickEvents.END_SERVER_TICK.register(server->{if(++ticks%20!=0)return;long now=System.currentTimeMillis();for(var player:server.getPlayerList().getPlayers()){PlayerProfile p=PlayerDataManager.get(server,player.getUUID());if(p.loadout().powerfulFormActiveUntilMillis()>0&&(!player.isAlive()||p.loadout().powerfulFormActiveUntilMillis()<=now)){PlayerDataManager.update(server,player.getUUID(),PowerfulFormService.finish(p));NetworkSync.sendProfile(player);}}});}
 public static long currentDay(net.minecraft.server.MinecraftServer server){var overworld=server.getLevel(net.minecraft.world.level.Level.OVERWORLD);return overworld==null?0:PowerfulFormService.overworldDay(overworld.getOverworldClockTime());}
}
