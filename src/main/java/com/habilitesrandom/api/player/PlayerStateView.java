package com.habilitesrandom.api.player;
import com.habilitesrandom.api.*;import java.util.*;
/** Immutable API projection; no mutable SavedData structure is exposed. */
public record PlayerStateView(Optional<CharacterDefinition> character,List<AbilityDefinition> abilities,Optional<TransformationDefinition> powerfulForm,boolean powerfulFormActive,int formUsesToday,int formDailyLimit,String rerollState,int antiLuckStreak,boolean powerfulFruitActive){public PlayerStateView{character=character==null?Optional.empty():character;abilities=abilities==null?List.of():List.copyOf(abilities);powerfulForm=powerfulForm==null?Optional.empty():powerfulForm;rerollState=rerollState==null?"":rerollState;}}
