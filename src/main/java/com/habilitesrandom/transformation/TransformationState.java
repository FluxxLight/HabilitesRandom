package com.habilitesrandom.transformation;
import net.minecraft.resources.Identifier;
/** Runtime form interval; daily restrictions are layered on this foundation in Prompt 5. */
public record TransformationState(Identifier transformationId,long startedAtMillis,long endsAtMillis){public TransformationState{if(transformationId==null||endsAtMillis<=startedAtMillis)throw new IllegalArgumentException("Invalid active transformation");}public boolean active(long now){return now<endsAtMillis;}}
