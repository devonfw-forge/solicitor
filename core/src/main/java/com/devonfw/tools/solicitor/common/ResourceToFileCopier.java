/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.config.ConfigFactory;

/**
 * Copies one or more resources to the file system.
 */
@Component
public class ResourceToFileCopier {
  private static final Logger LOG = LoggerFactory.getLogger(ResourceToFileCopier.class);

  @Autowired
  ConfigFactory configFactory;

  @Value("${solicitor.extension-userguide-source}")
  String extensionUserguideSource;

  @Value("${solicitor.extension-userguide-target}")
  String extensionUserguideTarget;

  /**
   * Represents a single copy operation to be performed
   */
  private static class CopyOperation {
    private String source;

    private String target;

    /**
     * The Constructor.
     *
     * @param source the URL of the source to copy from
     * @param target the target (filename)
     */
    public CopyOperation(String source, String target) {

      this.source = source;
      this.target = target;
    }

    /**
     * This method gets the field <code>source</code>.
     *
     * @return the field source
     */
    public String getSource() {

      return this.source;
    }

    /**
     * This method sets the field <code>source</code>.
     *
     * @param source the new value of the field source
     */
    public void setSource(String source) {

      this.source = source;
    }

    /**
     * This method gets the field <code>target</code>.
     *
     * @return the field target
     */
    public String getTarget() {

      return this.target;
    }

    /**
     * This method sets the field <code>target</code>.
     *
     * @param target the new value of the field target
     */
    public void setTarget(String target) {

      this.target = target;
    }

  }

  /**
   * A Builder for creating a sequence of copy operations.
   */
  private class CopySequenceBuilder {
    private List<CopyOperation> operations;

    /**
     * The constructor.
     */
    public CopySequenceBuilder() {

      this.operations = new ArrayList<>();
    }

    /**
     * Schedule a copy operation.
     *
     * @param source the source to copy from (URL)
     * @param target the target to copy to (filename)
     * @return the builder (to allow method chaining)
     */
    public CopySequenceBuilder withCopyOperation(String source, String target) {

      this.operations.add(new CopyOperation(source, target));
      return this;
    }

    /**
     * Execute pattern replacement in all target filenames of the scheduled copy operations. This uses
     * {@link String#replaceAll(String, String)}.
     *
     * @param pattern the pattern to replace (regex)
     * @param replacement the replacement for the pattern; observe that some characters have special meaning due to
     *        regex functionality
     * @return the builder (to allow method chaining)
     * @see String#replaceAll(String, String)
     * @see Matcher#replaceAll(String)
     */
    public CopySequenceBuilder replaceInTarget(String pattern, String replacement) {

      for (CopyOperation operation : this.operations) {
        operation.setTarget(operation.getTarget().replaceAll(pattern, replacement));
      }
      return this;
    }

    /**
     * Execute the scheduled copy operations.
     */
    public void execute() {

      for (CopyOperation operation : this.operations) {
        File targetFile = new File(operation.getTarget());
        if (targetFile.exists()) {
          LOG.error(LogMessages.FILE_EXISTS.msg(), operation.getTarget());
          throw new SolicitorRuntimeException("Nothing extracted because this would overwrite existing files");
        }

      }

      for (CopyOperation operation : this.operations) {
        copyResourceToFile(operation.getSource(), operation.getTarget());
      }
    }
  }

  /**
   * Depicts different groups of resources which might be copied.
   */
  public static enum ResourceGroup {
    /**
     * The User Guide.
     */
    USERGUIDE,

    /**
     * (Full) Configuration structure for new projects.
     */
    PROJECT_FILES,

    /**
     * All configuration (configuration file, decision tables, templates)
     */
    FULL_BASE_CONFIG
  }

  @Autowired
  private InputStreamFactory inputStreamFactory;

  /**
   * Constructor.
   */
  public ResourceToFileCopier() {

  }

  /**
   * Copies all resources of the given group to the filesystem.
   *
   * @param resourceGroup a {@link ResourceGroup} which identifies the files to copy.
   * @param targetDir the target directory - will be created if it does not exist
   * @return some String possible used in subsequent logging - e.g. the name of the target resource
   */
  public String copyReourcesToFile(ResourceGroup resourceGroup, String targetDir) {

    String returnString;

    switch (resourceGroup) {
      case USERGUIDE:
        CopySequenceBuilder csb1 = new CopySequenceBuilder()
            .withCopyOperation("classpath:solicitor_userguide.pdf", "target/solicitor_userguide.pdf")
            .withCopyOperation("classpath:solicitor_licenseinfo.html", "target/solicitor_licenseinfo.html");
        optionallyAddExtensionUserguide(csb1);
        csb1.replaceInTarget("target", targetDir).execute();
        returnString = "solicitor_userguide.pdf";
        break;
      case PROJECT_FILES:
        new CopySequenceBuilder().withCopyOperation("classpath:starters/solicitor.cfg", "new_project/solicitor.cfg")
            .withCopyOperation("classpath:starters/input/licenses_starter.xml",
                "new_project/input/licenses_starter.xml")
            .withCopyOperation("classpath:starters/rules/LegalEvaluationProject.xls",
                "new_project/rules/LegalEvaluationProject.xls")
            .withCopyOperation("classpath:starters/rules/LegalPreEvaluationProject.xls",
                "new_project/rules/LegalPreEvaluationProject.xls")
            .withCopyOperation("classpath:starters/rules/LicenseAssignmentProject.xls",
                "new_project/rules/LicenseAssignmentProject.xls")
            .withCopyOperation("classpath:starters/rules/LicenseNameMappingProject.xls",
                "new_project/rules/LicenseNameMappingProject.xls")
            .withCopyOperation("classpath:starters/rules/LicenseSelectionProject.xls",
                "new_project/rules/LicenseSelectionProject.xls")
            .withCopyOperation("classpath:starters/rules/MultiLicenseSelectionProject.xls",
                "new_project/rules/MultiLicenseSelectionProject.xls")
            .withCopyOperation("classpath:starters/readme.txt", "new_project/readme.txt")
            .replaceInTarget("new_project", targetDir).execute();
        returnString = targetDir + "/readme.txt";
        break;
      case FULL_BASE_CONFIG:
        List<String> urls = this.configFactory.findAllClasspathResourcesInBaseConfig();
        CopySequenceBuilder csb = new CopySequenceBuilder();
        for (String url : urls) {
          csb.withCopyOperation(url, url);
        }
        csb.replaceInTarget("classpath:", targetDir + "/");
        csb.execute();
        returnString = urls.get(0).replace("classpath:", targetDir + "/");
        break;
      default:
        throw new SolicitorRuntimeException("Uuups, this should never happen.");
    }

    return returnString;
  }

  /**
   * Optionally add Extension user guide to the list of documents to be extracted
   *
   * @param csb builder to add the dcouemnt to
   */
  private void optionallyAddExtensionUserguide(CopySequenceBuilder csb) {

    boolean sourceDefined = this.extensionUserguideSource != null && !this.extensionUserguideSource.isEmpty();
    boolean targetDefined = this.extensionUserguideTarget != null && !this.extensionUserguideTarget.isEmpty();

    if (sourceDefined != targetDefined) {
      throw new SolicitorRuntimeException(
          "Properties solicitor.extension-userguide-source and solicitor.extension-userguide-target "
              + "need to be either both defined or both undefined");
    }
    if (sourceDefined && targetDefined) {
      csb.withCopyOperation(this.extensionUserguideSource, this.extensionUserguideTarget);
    }
  }

  /**
   * Copies a single resource to the file system.
   *
   * @param resourceUrl the URL of the resource to be used as source
   * @param fileName the name of the target file.
   */
  public void copyResourceToFile(String resourceUrl, String fileName) {

    File outputFile = new File(fileName);
    if (outputFile.exists()) {
      LOG.error(LogMessages.FILE_EXISTS.msg(), fileName);
      throw new SolicitorRuntimeException("Extracting of files aborted because target already exists");
    }
    LOG.info(LogMessages.COPYING_RESOURCE.msg(), resourceUrl, fileName);
    if (outputFile.getParentFile() != null) {
      // create needed directories if not yet existing
      outputFile.getParentFile().mkdirs();
    }
    try (InputStream inputStream = this.inputStreamFactory.createInputStreamFor(resourceUrl);
        OutputStream outputStream = new FileOutputStream(outputFile)) {
      byte[] buffer = new byte[1024];
      int length;
      while ((length = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, length);
      }
    } catch (IOException e) {
      throw new SolicitorRuntimeException("Could not copy resource to file", e);
    }

  }

}
