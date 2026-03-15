/*
 * Copyright (c) 2025 SignalWire
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 */
package com.signalwire.agents.contexts;

import java.util.*;

/**
 * Represents a single step within a context.
 * <p>
 * Steps can use either raw text or POM-style sections for their prompt content.
 * All setter methods return {@code this} for fluent chaining.
 */
public class Step {

    private final String name;
    private String text;
    private String stepCriteria;
    private Object functions; // String "none" or List<String>
    private List<String> validSteps;
    private List<String> validContexts;
    private final List<Map<String, Object>> sections;
    private GatherInfo gatherInfo;

    // Step behavior flags
    private boolean end;
    private boolean skipUserTurn;
    private boolean skipToNextStep;

    // Reset object for context switching from steps
    private String resetSystemPrompt;
    private String resetUserPrompt;
    private boolean resetConsolidate;
    private boolean resetFullReset;

    public Step(String name) {
        this.name = name;
        this.sections = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    /**
     * Set the step's prompt text directly.
     */
    public Step setText(String text) {
        if (!sections.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot use setText() when POM sections have been added. Use one approach or the other.");
        }
        this.text = text;
        return this;
    }

    /**
     * Add a POM section to the step.
     */
    public Step addSection(String title, String body) {
        if (text != null) {
            throw new IllegalStateException(
                    "Cannot add POM sections when setText() has been used. Use one approach or the other.");
        }
        Map<String, Object> section = new LinkedHashMap<>();
        section.put("title", title);
        section.put("body", body);
        sections.add(section);
        return this;
    }

    /**
     * Add a POM section with bullet points.
     */
    public Step addBullets(String title, List<String> bullets) {
        if (text != null) {
            throw new IllegalStateException(
                    "Cannot add POM sections when setText() has been used. Use one approach or the other.");
        }
        Map<String, Object> section = new LinkedHashMap<>();
        section.put("title", title);
        section.put("bullets", bullets);
        sections.add(section);
        return this;
    }

    public Step setStepCriteria(String criteria) {
        this.stepCriteria = criteria;
        return this;
    }

    /**
     * Set which functions are available. "none" to disable all, or a List of function names.
     */
    public Step setFunctions(Object functions) {
        this.functions = functions;
        return this;
    }

    public Step setValidSteps(List<String> steps) {
        this.validSteps = steps;
        return this;
    }

    public Step setValidContexts(List<String> contexts) {
        this.validContexts = contexts;
        return this;
    }

    public Step setEnd(boolean end) {
        this.end = end;
        return this;
    }

    public Step setSkipUserTurn(boolean skip) {
        this.skipUserTurn = skip;
        return this;
    }

    public Step setSkipToNextStep(boolean skip) {
        this.skipToNextStep = skip;
        return this;
    }

    /**
     * Enable info gathering for this step.
     */
    public Step setGatherInfo(String outputKey, String completionAction, String prompt) {
        this.gatherInfo = new GatherInfo(outputKey, completionAction, prompt);
        return this;
    }

    /**
     * Add a question to this step's gather_info configuration.
     * setGatherInfo() must be called first.
     */
    public Step addGatherQuestion(String key, String question, String type,
                                  boolean confirm, String prompt, List<String> functions) {
        if (gatherInfo == null) {
            throw new IllegalStateException("Must call setGatherInfo() before addGatherQuestion()");
        }
        gatherInfo.addQuestion(key, question, type, confirm, prompt, functions);
        return this;
    }

    public Step addGatherQuestion(String key, String question) {
        return addGatherQuestion(key, question, "string", false, null, null);
    }

    public Step clearSections() {
        sections.clear();
        text = null;
        return this;
    }

    public Step setResetSystemPrompt(String systemPrompt) {
        this.resetSystemPrompt = systemPrompt;
        return this;
    }

    public Step setResetUserPrompt(String userPrompt) {
        this.resetUserPrompt = userPrompt;
        return this;
    }

    public Step setResetConsolidate(boolean consolidate) {
        this.resetConsolidate = consolidate;
        return this;
    }

    public Step setResetFullReset(boolean fullReset) {
        this.resetFullReset = fullReset;
        return this;
    }

    // Package-private accessors for validation
    List<String> getValidSteps() {
        return validSteps;
    }

    List<String> getValidContexts() {
        return validContexts;
    }

    GatherInfo getGatherInfo() {
        return gatherInfo;
    }

    /**
     * Render the step's prompt text from either raw text or POM sections.
     */
    String renderText() {
        if (text != null) {
            return text;
        }
        if (sections.isEmpty()) {
            throw new IllegalStateException("Step '" + name + "' has no text or POM sections defined");
        }
        var sb = new StringBuilder();
        for (int i = 0; i < sections.size(); i++) {
            var section = sections.get(i);
            String title = (String) section.get("title");
            sb.append("## ").append(title).append('\n');
            if (section.containsKey("bullets")) {
                @SuppressWarnings("unchecked")
                List<String> bullets = (List<String>) section.get("bullets");
                for (String bullet : bullets) {
                    sb.append("- ").append(bullet).append('\n');
                }
            } else {
                sb.append(section.get("body")).append('\n');
            }
            if (i < sections.size() - 1) {
                sb.append('\n');
            }
        }
        return sb.toString().stripTrailing();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", name);
        map.put("text", renderText());

        if (stepCriteria != null) map.put("step_criteria", stepCriteria);
        if (functions != null) map.put("functions", functions);
        if (validSteps != null) map.put("valid_steps", validSteps);
        if (validContexts != null) map.put("valid_contexts", validContexts);
        if (end) map.put("end", true);
        if (skipUserTurn) map.put("skip_user_turn", true);
        if (skipToNextStep) map.put("skip_to_next_step", true);

        // Reset object
        Map<String, Object> resetObj = new LinkedHashMap<>();
        if (resetSystemPrompt != null) resetObj.put("system_prompt", resetSystemPrompt);
        if (resetUserPrompt != null) resetObj.put("user_prompt", resetUserPrompt);
        if (resetConsolidate) resetObj.put("consolidate", true);
        if (resetFullReset) resetObj.put("full_reset", true);
        if (!resetObj.isEmpty()) map.put("reset", resetObj);

        if (gatherInfo != null) map.put("gather_info", gatherInfo.toMap());

        return map;
    }
}
