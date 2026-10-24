package com.habilitesrandom.core;

import com.habilitesrandom.api.HabilitesRandomApi;
import com.habilitesrandom.registry.*;
import com.habilitesrandom.api.player.*;
import com.habilitesrandom.data.*;
import com.habilitesrandom.util.ProfileValues;
import net.minecraft.server.level.ServerPlayer;

/**
 * Single Core composition root. Companion mods talk to {@link #api()}; nothing here imports a Plus.
 */
public final class CoreServices {
    public static final int API_VERSION = 1;
    public static final int MINIMUM_COMPATIBLE_API = 1;

    private static final ContentCatalog CATALOG = new ContentCatalog();
    private static final HabilitesRandomApi API = new CoreApi();
    private static final CharacterRegistry CHARACTERS = new CharacterRegistry(CATALOG);
    private static final AbilityRegistry ABILITIES = new AbilityRegistry(CATALOG);
    private static final TransformationRegistry TRANSFORMATIONS = new TransformationRegistry(CATALOG);
    private static final ExecutionRegistry EXECUTIONS = new ExecutionRegistry();
    private static final PlayerApi PLAYERS = new CorePlayerApi();

    private CoreServices() {
    }

    public static ContentCatalog catalog() {
        return CATALOG;
    }

    public static CharacterRegistry characters() { return CHARACTERS; }
    public static AbilityRegistry abilities() { return ABILITIES; }
    public static TransformationRegistry transformations() { return TRANSFORMATIONS; }

    public static ExecutionRegistry executions() { return EXECUTIONS; }
    public static PlayerApi players() { return PLAYERS; }

    public static HabilitesRandomApi api() {
        return API;
    }

    public static boolean supports(int requestedApiVersion) {
        return requestedApiVersion >= MINIMUM_COMPATIBLE_API && requestedApiVersion <= API_VERSION;
    }

    private record CoreApi() implements HabilitesRandomApi {
        @Override
        public int apiVersion() {
            return API_VERSION;
        }

        @Override
        public ContentCatalog catalog() {
            return CATALOG;
        }

        @Override public CharacterRegistry characters() { return CHARACTERS; }
        @Override public AbilityRegistry abilities() { return ABILITIES; }
        @Override public TransformationRegistry transformations() { return TRANSFORMATIONS; }
        @Override public PlayerApi players() { return PLAYERS; }

        @Override
        public boolean supports(int requestedApiVersion) {
            return CoreServices.supports(requestedApiVersion);
        }
    }
    private static final class CorePlayerApi implements PlayerApi {
        @Override public PlayerStateView view(ServerPlayer player) {
            if (player == null || ServerRuntime.server() == null) return new PlayerStateView(java.util.Optional.empty(), java.util.List.of(), java.util.Optional.empty(), false, 0, 1, "", 0, false);
            PlayerProfile p=PlayerDataManager.get(ServerRuntime.server(),player.getUUID());
            var cid=ProfileValues.parseId(p.identity().characterId());var character=cid==null?java.util.Optional.<com.habilitesrandom.api.CharacterDefinition>empty():CHARACTERS.get(cid);
            var abilities=p.loadout().abilityIds().stream().map(ProfileValues::parseId).filter(java.util.Objects::nonNull).map(ABILITIES::get).flatMap(java.util.Optional::stream).toList();
            var fid=ProfileValues.parseId(p.loadout().transformationId());var form=fid==null?java.util.Optional.<com.habilitesrandom.api.TransformationDefinition>empty():TRANSFORMATIONS.get(fid);
            long day=com.habilitesrandom.transformation.PowerfulFormRuntime.currentDay(ServerRuntime.server());var availability=com.habilitesrandom.transformation.PowerfulFormService.availability(p,day,System.currentTimeMillis());
            return new PlayerStateView(character,abilities,form,availability.active(),availability.used(),availability.limit(),p.progress().rerollState().name(),p.loadout().badLuckCounter(),p.loadout().powerfulFruitActive());
        }
        @Override public boolean hasCharacter(ServerPlayer player){return view(player).character().isPresent();}
        @Override public boolean isPowerfulFormActive(ServerPlayer player){return view(player).powerfulFormActive();}
    }

}
