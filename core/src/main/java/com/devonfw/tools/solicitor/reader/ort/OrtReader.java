/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.ort;

import java.io.IOException;
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
 * <a href="https://github.com/oss-review-toolkit/ort#analyzer">ORT-Analyzer</a> component.
 */
@Component
public class OrtReader extends AbstractReader implements Reader {

  /**
   * The supported type of this {@link Reader}.
   */
  public static final String SUPPORTED_TYPE = "ort";

  /** {@inheritDoc} */
  @Override
  public Set<String> getSupportedTypes() {

    return Collections.singleton(SUPPORTED_TYPE);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public void readInventory(String type, String sourceUrl, Application application, UsagePattern usagePattern,
      String repoType, Map<String, String> configuration) {

    int componentCount = 0;
    int licenseCount = 0;

    // According to tutorial https://github.com/FasterXML/jackson-databind/
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    try {
      Map l = mapper.readValue(this.inputStreamFactory.createInputStreamFor(sourceUrl), Map.class);
      Map analyzer = (Map) l.get("analyzer");
      Map result = (Map) analyzer.get("result");
      List packages = (List) result.get("packages");

      for (int i = 0; i < packages.size(); i++) {
        Map iterator = (Map) packages.get(i);
        Map singlePackage = (Map) iterator.get("package");
        String id = (String) singlePackage.get("id");
        Map vcsProcessed = (Map) singlePackage.get("vcs_processed");
        String repo = (String) vcsProcessed.get("url");
        String pURL = (String) singlePackage.get("purl");

        String homePage = (String) singlePackage.get("homepage_url");

        ApplicationComponent appComponent = getModelFactory().newApplicationComponent();
        appComponent.setApplication(application);
        componentCount++;

        // resolve id into groupId/artifactId/version/repoType
        String[] resolvedId = id.split(":");
        String trueRepoType = resolvedId[0];
        String groupId = resolvedId[1];
        String artifactId = resolvedId[2];
        String version = resolvedId[3];

        appComponent.setGroupId(groupId);
        appComponent.setArtifactId(artifactId);
        appComponent.setVersion(version);
        appComponent.setUsagePattern(usagePattern);
        appComponent.setOssHomepage(homePage);
        appComponent.setSourceRepoUrl(repo);
        appComponent.setRepoType(trueRepoType);
        appComponent.setPackageUrl(pURL);

        // manage multiple declared licenses
        List lic = (List) singlePackage.get("declared_licenses");
        if (lic.isEmpty()) {
          // add empty raw license if no license info attached
          addRawLicense(appComponent, null, repo, sourceUrl);
        } else {
          for (Object cl : lic) {
            licenseCount++;
            addRawLicense(appComponent, cl.toString(), repo, sourceUrl);
          }
        }
        doLogging(sourceUrl, application, componentCount, licenseCount);
      }
    } catch (IOException e) {
      throw new SolicitorRuntimeException("Could not read ort license inventory source '" + sourceUrl + "'", e);
    }
  }
}
