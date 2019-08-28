/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.ruleengine.drools;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.common.InputStreamFactory;

@Component
public class DroolsDecisionTableReader implements DroolsRulesReader {

    @Autowired
    private InputStreamFactory inputStreamFactory;

    public DroolsDecisionTableReader() {

        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean accept(String type) {

        return "dt".equals(type);
    }

    @Override
    public void readRules(String ruleSource, String templateSource,
            String decription, KieBaseModel baseModel,
            Collection<Resource> resources) {

        String ruleUuid =
                "com/devonfw/tools/solicitor/rules/" + UUID.randomUUID().toString();
        String templateUuid =
                "com/devonfw/tools/solicitor/rules/" + UUID.randomUUID().toString();

        baseModel.addRuleTemplate(ruleUuid, templateUuid, 2, 1);
        Resource dt;
        try {
            dt = ResourceFactory.newInputStreamResource(
                    inputStreamFactory.createInputStreamFor(ruleSource));
        } catch (IOException e) {
            throw new SolicitorRuntimeException(
                    "Could not open decision table xls resource '" + ruleSource
                            + "'for reading");
        }
        // Resource dt = ResourceFactory.newClassPathResource(ruleSource,
        // getClass());
        dt.setSourcePath(ruleUuid);
        dt.setSourcePath(ruleUuid);
        dt.setResourceType(ResourceType.DTABLE);
        dt.setConfiguration(new DecisionTableConfigurationImpl()); // if this is
                                                                   // not done
                                                                   // then the
                                                                   // system
                                                                   // fails to
        // compile the rules
        resources.add(dt);
        try {
            dt = ResourceFactory.newInputStreamResource(
                    inputStreamFactory.createInputStreamFor(templateSource));
        } catch (IOException e) {
            throw new SolicitorRuntimeException(
                    "Could not open rule template resource '" + templateSource
                            + "'for reading");
        }
        dt.setSourcePath(templateUuid);
        dt.setSourcePath(templateUuid);
        dt.setResourceType(ResourceType.DRT);
        resources.add(dt);
    }

}
