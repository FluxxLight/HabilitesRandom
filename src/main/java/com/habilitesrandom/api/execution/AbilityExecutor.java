package com.habilitesrandom.api.execution;

/** Server-only behavior supplied by a Plus for one registered ability id. */
@FunctionalInterface
public interface AbilityExecutor {
    AbilityExecutionResult execute(AbilityExecutionContext context);
}
