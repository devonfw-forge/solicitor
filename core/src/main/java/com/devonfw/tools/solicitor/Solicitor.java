/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorCliProcessor.CommandLineOptions;
import com.devonfw.tools.solicitor.SolicitorSetup.ReaderSetup;
import com.devonfw.tools.solicitor.common.DeprecationChecker;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.MavenVersionHelper;
import com.devonfw.tools.solicitor.common.ResourceToFileCopier;
import com.devonfw.tools.solicitor.common.ResourceToFileCopier.ResourceGroup;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.config.ConfigFactory;
import com.devonfw.tools.solicitor.lifecycle.LifecycleListenerHolder;
import com.devonfw.tools.solicitor.model.ModelImporterExporter;
import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.reader.Reader;
import com.devonfw.tools.solicitor.reader.ReaderFactory;
import com.devonfw.tools.solicitor.writer.WriterFacade;

/**
 * The central bean of the Solicitor application.
 */
@Component
public class Solicitor {

  private static final Logger LOG = LoggerFactory.getLogger(Solicitor.class);

  private static final String INPUT_DATA_MISSING = " (INPUT DATA MISSING!)";

  @Autowired
  private SolicitorVersion solicitorVersion;

  @Autowired
  private SolicitorSetup solicitorSetup;

  @Autowired
  private ConfigFactory configFactory;

  @Autowired
  private ReaderFactory readerFactory;

  @Autowired
  private InventoryProcessor[] inventoryProcessors;

  @Autowired
  private WriterFacade writerFacade;

  @Autowired
  private ResourceToFileCopier resourceToFileCopier;

  @Autowired
  private ModelImporterExporter modelImporterExporter;

  @Autowired
  private LifecycleListenerHolder lifecycleListenerHolder;

  @Autowired
  private DeprecationChecker deprecationChecker;

  private boolean tolerateMissingInput = false;

  @Value("${solicitor.tolerate-missing-input}")
  public void setTolerateMissingInput(boolean tolerateMissingInput) {

    this.tolerateMissingInput = tolerateMissingInput;
  }

  /**
   * Copy the user guide to the current working directory.
   */
  private void extractUserguide() {

    this.resourceToFileCopier.copyReourcesToFile(ResourceGroup.USERGUIDE, ".");
  }

  /**
   * Create a new basic project configuration.
   *
   * @param targetDir the directory where the userguide should be stored
   */
  private void wizard(String targetDir) {

    String location = this.resourceToFileCopier.copyReourcesToFile(ResourceGroup.PROJECT_FILES, targetDir);
    LOG.info(LogMessages.PROJECT_CREATED.msg(), location);
  }

  /**
   * Save all sample configuration files to the working directory.
   *
   * @param targetDir the directory where the userguide should be stored
   */
  private void extractConfig(String targetDir) {

    String location = this.resourceToFileCopier.copyReourcesToFile(ResourceGroup.FULL_BASE_CONFIG, targetDir);
    LOG.info(LogMessages.FULL_CONFIG_EXTRACTED.msg(), location);
  }

  /**
   * Perform the main flow of processing.
   *
   * @param clo the parsed command line options
   */
  private void mainProcessing(CommandLineOptions clo) {

    checkJavaVersion();
    ModelRoot modelRoot = this.configFactory.createConfig(clo.configUrl);
    this.lifecycleListenerHolder.modelRootInitialized(modelRoot);
    if (clo.load) {
      LOG.info(LogMessages.LOADING_DATAMODEL.msg(), clo.pathForLoad);
      modelRoot = this.modelImporterExporter.loadModel(clo.pathForLoad);
    } else {
      readInventory();
      this.lifecycleListenerHolder.afterReadingInventory(modelRoot);
      runInventoryProcessors(modelRoot);
      modelRoot.completeData();
      this.lifecycleListenerHolder.afterModelProcessing(modelRoot);
    }
    if (clo.save) {
      LOG.info(LogMessages.SAVING_DATAMODEL.msg(), clo.pathForSave);
      this.modelImporterExporter.saveModel(modelRoot, clo.pathForSave);
    }
    ModelRoot oldModelRoot = null;
    if (clo.diff) {
      LOG.info(LogMessages.LOADING_DIFF.msg(), clo.pathForDiff);
      oldModelRoot = this.modelImporterExporter.loadModel(clo.pathForDiff);
    }
    this.writerFacade.writeResult(modelRoot, oldModelRoot);
    this.lifecycleListenerHolder.endOfMainProcessing(modelRoot);
  }

  /**
   * Checks the java version and possibly issue deprecation error or warning.
   */
  private void checkJavaVersion() {

    String javaVersion = System.getProperty("java.version");
    // we just check for the prefix "1." because from Java 9 on the version number does no longer start with "1."
    if (javaVersion.startsWith("1.")) {
      this.deprecationChecker.check(false,
          "Running Solicitor on Java 8 is deprecated. Yours is '" + javaVersion + "'. Switch to Java 11!");
    }

  }

  /**
   * Read the inventory of {@link ApplicationComponent}s and their declared licenses.
   */
  private void readInventory() {

    for (ReaderSetup readerSetup : this.solicitorSetup.getReaderSetups()) {
      Reader reader = this.readerFactory.readerFor(readerSetup.getType());
      try {
        reader.readInventory(readerSetup.getType(), readerSetup.getSource(), readerSetup.getApplication(),
            readerSetup.getUsagePattern(), readerSetup.getRepoType(), readerSetup.getConfiguration());
      } catch (SolicitorRuntimeException sre) {
        if (this.tolerateMissingInput && sre.getCause() instanceof FileNotFoundException) {
          Application app = readerSetup.getApplication();
          LOG.warn(LogMessages.MISSING_INVENTORY_INPUT_FILE.msg(), readerSetup.getSource(), app.getName());
          markApplicationMissingData(app);
        } else {
          throw sre;
        }
      }
    }

  }

  /**
   * Execute the inventoryProcessors.
   *
   * @param modelRoot the root of the data model
   */
  private void runInventoryProcessors(ModelRoot modelRoot) {

    for (InventoryProcessor inventoryProcessor : this.inventoryProcessors) {
      inventoryProcessor.processInventory(modelRoot);
    }
  }

  private void markApplicationMissingData(Application app) {

    String appName = app.getName();
    if (!appName.endsWith(INPUT_DATA_MISSING)) {
      app.setName(appName + INPUT_DATA_MISSING);
    }
    app.setProgrammingEcosystem(INPUT_DATA_MISSING);
    app.setReleaseDate(INPUT_DATA_MISSING);
    app.setReleaseId(INPUT_DATA_MISSING);
    app.setSourceRepo(INPUT_DATA_MISSING);
  }

  /**
   * Starts processing as given by the command line options.
   *
   * @param clo the parsed command line options
   * @param rawCommandline the raw (unparsed) command line; for logging puposes only
   */
  public void run(CommandLineOptions clo, String rawCommandline) {

    LOG.info(LogMessages.CALLED.msg(), rawCommandline);

    long startTime = System.currentTimeMillis();
    boolean doMainProcessing = true;
    SolicitorVersion sv = this.solicitorVersion;
    LOG.info(LogMessages.STARTING.msg(), sv.getVersion(), sv.getGithash(), sv.getBuilddate());
    if (sv.isExtensionPresent()) {
      activateExtension(sv);
    } else {
      LOG.warn(LogMessages.NO_EXTENSION_DISCLAIMER.msg());
    }

    if (clo.extractUserGuide) {
      extractUserguide();
      doMainProcessing = false;
    }
    if (clo.extractConfig) {
      extractConfig(clo.targetDir);
      doMainProcessing = false;
    } else if (clo.wizard) {
      wizard(clo.targetDir);
      doMainProcessing = false;
    }
    if (doMainProcessing) {
      mainProcessing(clo);
    }
    LOG.info(LogMessages.COMPLETED.msg(), System.currentTimeMillis() - startTime);
  }

  /**
   * Checks the extension and logs info.
   *
   * @param sv the object which holds info about the extension
   * @throws SolicitorRuntimeException if the extension is incompatible with this Solicitor version
   */
  private void activateExtension(SolicitorVersion sv) {

    LOG.info(LogMessages.EXTENSION_PRESENT.msg(), sv.getExtensionArtifact(), sv.getExtensionVersion(),
        sv.getExtensionGithash(), sv.getExtensionBuilddate());
    checkExtensionVersionRequirements(sv);
    if (sv.getExtensionMessage1() != null && !sv.getExtensionMessage1().isEmpty()) {
      LOG.info(LogMessages.EXTENSION_BANNER_1.msg(), sv.getExtensionMessage1());
    }
    if (sv.getExtensionMessage2() != null && !sv.getExtensionMessage2().isEmpty()) {
      LOG.info(LogMessages.EXTENSION_BANNER_2.msg(), sv.getExtensionMessage2());
    }
  }

  /**
   * Checks if Solicitor complies to the version expectations found in the extension.
   *
   * @param sv the object which holds info about the extension
   * @throws SolicitorRuntimeException if Solicitor does not comply to the version requirements given in the extension
   */
  private void checkExtensionVersionRequirements(SolicitorVersion sv) {

    final String expectedVersionRange = sv.getExtensionExpectedSolicitorVersionRange();
    if (expectedVersionRange != null && !expectedVersionRange.isEmpty()) {
      final String version = sv.getVersion();
      boolean versionOk = MavenVersionHelper.checkVersionRange(version, expectedVersionRange);
      if (!versionOk) {
        LOG.error(LogMessages.EXTENSION_EXPECTATION_FAILED.msg(), expectedVersionRange);
        throw new SolicitorRuntimeException("Solicitor does not match version requirements of the extension");
      }
    }

  }

}
