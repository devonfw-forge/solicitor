/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.gradle;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.FileInputStreamFactory;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;

public class GradleReaderTests {
    private static final Logger LOG =
            LoggerFactory.getLogger(GradleReaderTests.class);

    Application application;

    public GradleReaderTests() {

        ModelFactory modelFactory = new ModelFactoryImpl();

        application = modelFactory.newApplication("testApp", "0.0.0.TEST",
                "1.1.2111", "http://bla.com", "Java8");
        GradleReader gr = new GradleReader();
        gr.setModelFactory(modelFactory);
        gr.setInputStreamFactory(new FileInputStreamFactory());
        gr.readInventory("src/test/resources/licenseReport.json", application,
                UsagePattern.DYNAMIC_LINKING);

    }

    @Test
    public void readFile() {

        LOG.info(application.toString());
    }

    @Test
    public void findArtifact() {

        List<ApplicationComponent> lapc =
                application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId()
                    .equals("org.apache.xmlbeans:xmlbeans:3.0.2")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testFindLicense() {

        List<ApplicationComponent> lapc =
                application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("io.vavr:vavr-jackson:0.9.3")
                    && ap.getRawLicenses().get(0).getDeclaredLicense().equals(
                            "The Apache Software License, Version 2.0")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testFindLicense2() {

        List<ApplicationComponent> lapc =
                application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("com.github.virtuald:curvesapi:1.05")
                    && ap.getRawLicenses().get(0).getDeclaredLicense()
                            .equals("BSD License")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testFindLicense3() {

        List<ApplicationComponent> lapc =
                application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId()
                    .equals("com.fasterxml.jackson.core:jackson-core:2.9.4")
                    && ap.getVersion().equals("2.9.4")
                    && ap.getRawLicenses().get(0).getDeclaredLicense().equals(
                            "The Apache Software License, Version 2.0")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
}
