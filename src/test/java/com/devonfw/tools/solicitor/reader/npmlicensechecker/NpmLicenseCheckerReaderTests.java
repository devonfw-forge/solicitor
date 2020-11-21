/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.npmlicensechecker;

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

public class NpmLicenseCheckerReaderTests {
    private static final Logger LOG = LoggerFactory.getLogger(NpmLicenseCheckerReaderTests.class);

    Application application;

    public NpmLicenseCheckerReaderTests() {

        ModelFactory modelFactory = new ModelFactoryImpl();

        application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Angular");
        NpmLicenseCheckerReader gr = new NpmLicenseCheckerReader();
        gr.setModelFactory(modelFactory);
        gr.setInputStreamFactory(new FileInputStreamFactory());
        gr.readInventory("npm-license-checker", "src/test/resources/npmLicenseCheckerReport.json", application,
                UsagePattern.DYNAMIC_LINKING, "npm");

    }

    @Test
    public void findArtifact() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("foo") && //
                    ap.getVersion().equals("0.0.1")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void readFile() {

        LOG.info(application.toString());
    }

    @Test
    public void testFindLicenseIfSingle() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("foo") && ap.getRawLicenses().get(0).getDeclaredLicense().equals("MIT")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testFindLicensesIfMultiple() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("foo-bar")
                    && ap.getRawLicenses().get(0).getDeclaredLicense().matches("AFLv2.1|BSD")
                    && ap.getRawLicenses().get(1).getDeclaredLicense().matches("AFLv2.1|BSD")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testHomepageWhichIsGiven() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("foo") && ap.getOssHomepage().equals("http://www.somebody.com/")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testHomepageFromRepo() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("foo-bar")
                    && ap.getOssHomepage().equals("https://github.com/nobody/foo-bar")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testLicenseUrlIfLicenseFileFoundAndGithub() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("foo") && ap.getRawLicenses().get(0).getLicenseUrl()
                    .equals("https://github.com/somebody/foo/raw/master/LICENSE")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testLicenseUrlIfNoLicenseFile() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("foo-bar")
                    && ap.getRawLicenses().get(0).getLicenseUrl().equals("https://github.com/nobody/foo-bar")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

}
