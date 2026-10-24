package com.habilitesrandom.api.player;
import net.minecraft.server.level.ServerPlayer;
public interface PlayerApi {PlayerStateView view(ServerPlayer player);boolean hasCharacter(ServerPlayer player);boolean isPowerfulFormActive(ServerPlayer player);}
