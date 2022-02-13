/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.npmlicensechecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.AbstractReader;
import com.devonfw.tools.solicitor.reader.Reader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * A {@link Reader} which reads data generated by the
 * <a href="https://www.npmjs.com/package/license-checker">NPM License
 * Checker</a>. The reader tries to "guess" the licenseUrl in a similar way as
 * the <a href="https://www.npmjs.com/package/npm-license-crawler">NPM License
 * Crawler</a> does. (Which uses a fork of the NPM License Checker under the
 * hood.)
 */
@Component
public class NpmLicenseCheckerReader extends AbstractReader implements Reader {

    /**
     * The supported type of this {@link Reader}.
     */
    public static final String SUPPORTED_TYPE = "npm-license-checker";

    /** {@inheritDoc} */
    @Override
    public Set<String> getSupportedTypes() {

        return Collections.singleton(SUPPORTED_TYPE);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    public void readInventory(String type, String sourceUrl, Application application, UsagePattern usagePattern,
            String repoType, Map<String,String> configuration) {

        int componentCount = 0;
        int licenseCount = 0;

        // According to tutorial https://github.com/FasterXML/jackson-databind/
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            Map l = mapper.readValue(inputStreamFactory.createInputStreamFor(sourceUrl), Map.class);
            for (Object key : l.keySet()) {
                String name = (String) key;
                Map attributes = (Map) l.get(key);
                String repo = (String) attributes.get("repository");
                String path = (String) attributes.get("path");
                String licenseFile = (String) attributes.get("licenseFile");
                String licenseUrl = estimateLicenseUrl(repo, path, licenseFile);
                String homePage = (String) attributes.get("url");
                if (homePage == null || homePage.isEmpty()) {
                    homePage = repo;
                }

                Object lic = attributes.get("licenses");
                List<String> licenseList;
                if (lic != null) {
                    if (lic instanceof List) {
                        licenseList = new ArrayList<>();
                        for (Object entry : (List) lic) {
                            licenseList.add((String) entry);
                        }

                    } else {
                        licenseList = Collections.singletonList((String) lic);
                    }
                } else {
                    licenseList = Collections.emptyList();
                }

                ApplicationComponent appComponent = getModelFactory().newApplicationComponent();
                appComponent.setApplication(application);
                componentCount++;
                String[] module = name.split("@");
                if (name.startsWith("@")) {
                    appComponent.setArtifactId("@" + module[module.length - 2]);
                } else {
                    appComponent.setArtifactId(module[module.length - 2]);
                }
                appComponent.setVersion(module[module.length - 1]);
                appComponent.setUsagePattern(usagePattern);
                appComponent.setGroupId("");
                appComponent.setOssHomepage(homePage);
                appComponent.setRepoType(repoType);
                if (licenseList.isEmpty()) {
                    // add empty raw license if no license info attached
                    addRawLicense(appComponent, null, null, sourceUrl);
                } else {
                    for (String cl : licenseList) {
                        licenseCount++;
                        addRawLicense(appComponent, cl, licenseUrl, sourceUrl);
                    }
                }

            }
            doLogging(sourceUrl, application, componentCount, licenseCount);
        } catch (IOException e) {
            throw new SolicitorRuntimeException(
                    "Could not read npm-license-checker inventory source '" + sourceUrl + "'", e);
        }

    }

    private String estimateLicenseUrl(String repo, String path, String licenseFile) {

        if (repo == null || repo.isEmpty()) {
            return null;
        }
        if (path == null || path.isEmpty() || //
                licenseFile == null || licenseFile.isEmpty()) {
            return repo;
        }

        if (repo.contains("github.com") && licenseFile.startsWith(path)) {
            String licenseRelative = licenseFile.replace(path, "").replace("\\", "/");
            if (repo.endsWith("/")) {
                repo = repo.substring(0, repo.length() - 1);
            }
            if (repo.contains("github.com")) {
                return repo.replace("git://", "https://") + "/raw/master" + licenseRelative;
            }
        }
        return repo;
    }

}
