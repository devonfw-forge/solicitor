/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

import java.text.DecimalFormat;

/**
 * Enum which defines all log messages of levels INFO and higher.
 */
public enum LogMessages {

  CALLED(0, "Solicitor called with command line arguments: '{}'"), //
  STARTING(1, "Solicitor starts, Version:{}, Buildnumber:{}, Builddate:{}"), //
  COMPLETED(2, "Solicitor processing completed in {} ms"), //
  ABORTED(3, "Solicitor processing aborted"), //
  COPYING_RESOURCE(4, "Copying resource '{}' to file '{}'"), //
  READING_CONFIG(5, "Reading Solicitor configuration ({}) from resource '{}'"), //
  CREATING_ENGAGEMENT(6, "Defining Engagement '{}' in Solicitor data model"), //
  CREATING_APPLICATION(7, "Defining Application '{}' in Solicitor data model"), //
  LOADING_DATAMODEL(8, "Loading Solicitor data model from '{}' (overwriting any possibly existing data)"), //
  LOADING_DIFF(9, "Loading old Solicitor data model for performing difference report from '{}'"), //
  SAVING_DATAMODEL(10, "Saving Solicitor data model to '{}'"), //
  READING_INVENTORY(11, "Reading {} ApplicationComponents / {} Licenses for Application '{}' from '{}'"), //
  LOAD_RULES(12, "Loading Rules of type '{}' from source '{}' with template '{}' for Rule Group '{}'"), //
  ADDING_FACTS(13, "{} Facts have been added to the Drools working memory, starting Rule Engine ..."), //
  RULE_ENGINE_FINISHED(14, "Rule Engine processing completed, {} rules have been fired"), //
  PREPARING_FOR_WRITER(15, "Preparing to write report with writer '{}' using template '{}' to file '{}'"), //
  FINISHED_WRITER(16, "Finished writing report with writer '{}' using template '{}' to file '{}'"), //
  INIT_SQL(17, "Initializing SQL reporting database with Solicitor model data"), //
  INIT_SQL_OLD(18, "Initializing SQL reporting database with OLD Solicitor model data"), //
  EXECUTE_SQL(19, "Creating data of result table '{}' by executing SQL statement given in '{}'"), //
  CREATING_DIFF(20, "Calculating DIFF information for result table '{}'"), //
  FILE_EXISTS(21, "At least '{}' already exists. Please remove existing files and retry."), //
  PROJECT_CREATED(22,
      "Project file structure created. See '{}' for details. You might take this as starting point for your project setup."), //
  FULL_CONFIG_EXTRACTED(23,
      "Complete base configuration saved to filesystem. File '{}' is the base configuration file."), //
  CLI_EXCEPTION(24, "Exception when processing command line arguments: {}"), //
  RULE_GROUP_FINISHED(25, "Processing of rule group '{}' finished. {} rules fired in {} ms"), //
  TAKING_RULE_CONFIG(26, "Merging config: Taking rule config from {}"), //
  TAKING_WRITER_CONFIG(27, "Merging config: Taking writer config from {}"), //
  EXTENSION_PRESENT(28, "Solicitor extension present. Artifact:{}, Version:{}, Buildnumber:{}, Builddate:{}"), //
  PLACEHOLDER_INFO(29, "Placeholder '{}' in configuration will be replaced by '{}'"), //
  COULD_NOT_CREATE_CACHE(30,
      "Could not create directory '{}' for caching downloaded web resources. Could not write data to file cache."), //
  CREATED_DIRECTORY(31, "Created directory '{}' which did not yet exist"), //
  SKIPPING_RULEGROUP(32, "Optional RuleGroup '{}' SKIPPED as there is no rule file '{}'"), //
  UNSUPPORTED_CONFIG_VERSION(33, "Unsupported config file '{}' format; version needs to be '{}' but is '{}'"), //
  SQL_RETURNED_NO_DATA(34,
      "The SQL statement referenced by '{}' did not return any data. This might cause trouble in reporting if not handled correctly"), //
  EXTENSION_EXPECTATION_FAILED(35, "The extension expects Solicitor in version range {} which is not fulfilled"), //
  EXTENSION_BANNER_1(36, "{}"), //
  EXTENSION_BANNER_2(37, "{}"), //
  NO_EXTENSION_DISCLAIMER(38,
      "You are running Solicitor without Extension. Be aware that builtin rulesets are samples only!"), //
  FIRING_RULE(39, "Firing rule: [RuleGroup:{}],[Rule:{}] - {}"), //
  SKIP_DOWNLOAD(40, "Downloading resources is disabled, content of url '{}' will be empty."), //
  CORRELATION_KEY_NULL(41, "Correlation Key '{}' is NULL; Make sure that correlations keys are never NULL."), //
  DEPRECATIONS_ACTIVE(42,
      "Deprecated features are available ({}). Please check if this is necessary. Look for message code '{}' to check for actually used deprecated features."), //
  UNAVAILABLE_DEPRECATED_FEATURE(43,
      "This feature is deprecated and no longer available. Details: {}. For backward compatibility you might temporary activate it by setting '{}'."), //
  USING_DEPRECATED_FEATURE_FORCED(44,
      "You are using a deprecated feature which is only available because you set {}. You should ASAP migrate your project as this might be unavailable in future versions. Details: {}."), //
  MISSING_INVENTORY_INPUT_FILE(45,
      "Input file {} for Application {} not accessible, continuing anyway. (Property solicitor.tolerate-missing-input=true)"), //
  USING_DEPRECATED_FEATURE(46,
      "You are using a deprecated feature which might be removed soon. You should ASAP migrate your project as this might be unavailable in future versions. Details: {}."), //
  COULD_NOT_DOWNLOAD_CONTENT(47, "Downloading content from URL '{}' did not succeed. Exception was: {}."), //
  COULD_NOT_DOWNLOAD_CONTENT_MALFORMED_URL(48,
      "Downloading content from URL '{}' did not succeed because the URL is malfomed"), //
  SHORTENING_XLS_CELL_CONTENT(49, "Shortening text content for XLS"), //
  REPLACING_EXCESSIVE_HTML_CONTENT(50,
      "At least one license text contained a large amount of raw HTML and was substituted by placeholder text '{}'"), //
  MULTIPLE_DECISIONTABLES(51, "Multiple decision tables in both .xls and .csv format. Prioritizing '{}.xls'."), //
  ADDING_ADDITIONALWRITER_CONFIG(52, "Merging config: Adding additional writers to base config from {}"), //
  NOT_A_VALID_NPM_PACKAGE_IDENTIFIER(53, "{} is not a valid identifier for an NPM package"), //
  SCANCODE_PROCESSOR_STARTING(54,
      "Experimental feature ACTIVE: Start enriching the inventory data with Scancode data (as far as available)"), //
  SCANCODE_FEATURE_DEACTIVATED(55,
      "The experimental feature for enriching the inventory with scancode data is DEACTIVATED"), //
  COMPONENT_INFO_READ(56, "External component information was read for {} out of {} ApplicationComponents"), //
  CURATIONS_NOT_EXISTING(57, "Curations file '{}' not found. No curations will be applied."), //
  CURATIONS_PROCESSING(58, "Curations file '{}' exists. Applying curations."), //
  COMPONENTINFO_NO_LICENSES(59, "ComponentInfo for '{}' does not contain any license. Keeping licenses from Reader."), //
  CLASSPATHEXCEPTION_WITHOUT_GPL(60, "ClassPathException was found but no GPL License exists for {}"), //
  CLASSPATHEXCEPTION_MULTIPLE_GPL(61, "ClassPathException was found but there are multiple GPL Licenses for {}"), //
  CYCLONEDX_UNSUPPORTED_PURL(62,
      "The CycloneDX file contains the PackageURL '{}' with unsupported type which will be ignored. Solicitor reports might be incomplete."), //
  SCANCODE_AUTOMAPPING_STARTED(63,
      "Attempting to automatically map scancode license id to create NormalizedLicense objects. Blacklist: '{}', Ignorelist: '{}'"), //
  SCANCODE_AUTOMAPPING_FEATURE_DEACTIVATED(64,
      "The feature of attempting to automatically map scancode license ids is DEACTIVATED"), //
  SCANCODE_NO_MAPPING(65,
      "The license info '{}' from Scancode could not be mapped to OSS-SPDX or SCANCODE type license info"), //
  SCANCODE_MAPPING_STATISTICS(66,
      "Statistics for automatic mapping of scancode license ids to NormalizedLicenses: Total processed: {}, skipped due to blacklist: {}, "
          + "skipped due to unkown SPDX: {}, mapped using type SCANCODE: {}, mapped using type OSS-SPDX: {}, mapped to IGNORE: {}"), //
  NOT_A_VALID_NPM_PACKAGE_NAME(67, "{} is not a valid name for an NPM package"), //
  SCANCODE_ISSUE_DETECTION_REGEX(68,
      "The list of regular expressions for detecting licenses from scancode having issues is set to '{}'"), //
  MODERN_YARN_VIRTUAL_PACKAGE(69,
      "When reading yarn license info from file '{}' there was at least one virtual package encountered. Check if package resolution is correct"), //
  MODERN_YARN_PATCHED_PACKAGE(70,
      "When reading yarn license info from file '{}' there was at least one patched package encountered. Processing only the base package, not the patched version."), //
  FAILED_READING_FILE(71, "Reading file '{}' failed");

  private final String message;

  private final String label;

  /**
   * Private constructor.
   *
   * @param code the numeric message code
   * @param message the log message; might contain placeholders in logback format
   */
  private LogMessages(int code, String message) {

    this.label = "[SOLI-" + (new DecimalFormat("000")).format(code) + "]";
    this.message = message;
  }

  /**
   * Gets the complete message.
   *
   * @return the complete message
   */
  public String msg() {

    return this.label + " " + this.message;
  }

  /**
   * Gets the message label.
   *
   * @return the message label
   */
  public String label() {

    return this.label;
  }
}
