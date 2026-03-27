/*
 * Copyright (c) 2025 SignalWire
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 */
package com.signalwire.sdk.contexts;

import java.util.*;

/**
 * Main builder class for creating contexts and steps.
 * <p>
 * Provides validation for the contexts/steps hierarchy and serialization to SWML format.
 */
public class ContextBuilder {

    static final int MAX_CONTEXTS = 50;

    private final Map<String, Context> contexts;
    private final List<String> contextOrder;

    public ContextBuilder() {
        this.contexts = new LinkedHashMap<>();
        this.contextOrder = new ArrayList<>();
    }

    /**
     * Add a new context.
     */
    public Context addContext(String name) {
        if (contexts.containsKey(name)) {
            throw new IllegalStateException("Context '" + name + "' already exists");
        }
        if (contexts.size() >= MAX_CONTEXTS) {
            throw new IllegalStateException("Maximum number of contexts (" + MAX_CONTEXTS + ") exceeded");
        }
        Context context = new Context(name);
        contexts.put(name, context);
        contextOrder.add(name);
        return context;
    }

    /**
     * Get an existing context by name.
     */
    public Context getContext(String name) {
        return contexts.get(name);
    }

    public boolean isEmpty() {
        return contexts.isEmpty();
    }

    /**
     * Validate the contexts configuration.
     *
     * @throws IllegalStateException if validation fails
     */
    public void validate() {
        if (contexts.isEmpty()) {
            throw new IllegalStateException("At least one context must be defined");
        }

        // Single context must be named "default"
        if (contexts.size() == 1) {
            String contextName = contexts.keySet().iterator().next();
            if (!"default".equals(contextName)) {
                throw new IllegalStateException("When using a single context, it must be named 'default'");
            }
        }

        // Validate each context has at least one step
        for (var entry : contexts.entrySet()) {
            String contextName = entry.getKey();
            Context context = entry.getValue();
            if (context.getSteps().isEmpty()) {
                throw new IllegalStateException("Context '" + contextName + "' must have at least one step");
            }
        }

        // Validate step references in valid_steps
        for (var ctxEntry : contexts.entrySet()) {
            String contextName = ctxEntry.getKey();
            Context context = ctxEntry.getValue();
            for (var stepEntry : context.getSteps().entrySet()) {
                String stepName = stepEntry.getKey();
                Step step = stepEntry.getValue();
                if (step.getValidSteps() != null) {
                    for (String validStep : step.getValidSteps()) {
                        if (!"next".equals(validStep) && !context.getSteps().containsKey(validStep)) {
                            throw new IllegalStateException(
                                    "Step '" + stepName + "' in context '" + contextName
                                            + "' references unknown step '" + validStep + "'");
                        }
                    }
                }
            }
        }

        // Validate context references in valid_contexts (context-level)
        for (var ctxEntry : contexts.entrySet()) {
            String contextName = ctxEntry.getKey();
            Context context = ctxEntry.getValue();
            if (context.getValidContexts() != null) {
                for (String validCtx : context.getValidContexts()) {
                    if (!contexts.containsKey(validCtx)) {
                        throw new IllegalStateException(
                                "Context '" + contextName + "' references unknown context '" + validCtx + "'");
                    }
                }
            }
        }

        // Validate context references in valid_contexts (step-level)
        for (var ctxEntry : contexts.entrySet()) {
            String contextName = ctxEntry.getKey();
            Context context = ctxEntry.getValue();
            for (var stepEntry : context.getSteps().entrySet()) {
                String stepName = stepEntry.getKey();
                Step step = stepEntry.getValue();
                if (step.getValidContexts() != null) {
                    for (String validCtx : step.getValidContexts()) {
                        if (!contexts.containsKey(validCtx)) {
                            throw new IllegalStateException(
                                    "Step '" + stepName + "' in context '" + contextName
                                            + "' references unknown context '" + validCtx + "'");
                        }
                    }
                }
            }
        }

        // Validate gather_info configurations
        for (var ctxEntry : contexts.entrySet()) {
            String contextName = ctxEntry.getKey();
            Context context = ctxEntry.getValue();
            for (var stepEntry : context.getSteps().entrySet()) {
                String stepName = stepEntry.getKey();
                Step step = stepEntry.getValue();
                GatherInfo gi = step.getGatherInfo();
                if (gi != null) {
                    if (gi.getQuestions().isEmpty()) {
                        throw new IllegalStateException(
                                "Step '" + stepName + "' in context '" + contextName
                                        + "' has gather_info with no questions");
                    }
                    // Check for duplicate keys
                    Set<String> seenKeys = new HashSet<>();
                    for (GatherQuestion q : gi.getQuestions()) {
                        if (!seenKeys.add(q.getKey())) {
                            throw new IllegalStateException(
                                    "Step '" + stepName + "' in context '" + contextName
                                            + "' has duplicate gather_info question key '" + q.getKey() + "'");
                        }
                    }
                    // Validate completion_action
                    String action = gi.getCompletionAction();
                    if (action != null) {
                        if ("next_step".equals(action)) {
                            int stepIdx = context.getStepOrder().indexOf(stepName);
                            if (stepIdx >= context.getStepOrder().size() - 1) {
                                throw new IllegalStateException(
                                        "Step '" + stepName + "' in context '" + contextName
                                                + "' has gather_info completion_action='next_step' but it is the last step");
                            }
                        } else if (!context.getSteps().containsKey(action)) {
                            throw new IllegalStateException(
                                    "Step '" + stepName + "' in context '" + contextName
                                            + "' has gather_info completion_action='" + action
                                            + "' but step '" + action + "' does not exist");
                        }
                    }
                }
            }
        }
    }

    /**
     * Convert all contexts to a Map for SWML generation.
     * Validates before converting.
     */
    public Map<String, Object> toMap() {
        validate();
        Map<String, Object> result = new LinkedHashMap<>();
        for (String name : contextOrder) {
            result.put(name, contexts.get(name).toMap());
        }
        return result;
    }
}
