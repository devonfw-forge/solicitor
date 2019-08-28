/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RuleConfig {
    @JsonProperty
    private String type;

    @JsonProperty
    private String ruleSource;

    @JsonProperty
    private String templateSource;

    @JsonProperty
    private String agendaGroup;

    @JsonProperty
    private String description;
}
