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
            "The SQL statement referenced by '{}' did not return any data. This might cause trouble in reporting if not handled correctly"); //

    private final String completeMessage;

    /**
     * Private constructor.
     * 
     * @param code the numeric message code
     * @param message the log message; might contain placeholders in logback
     *        format
     */
    private LogMessages(int code, String message) {

        completeMessage = "[SOLI-" + (new DecimalFormat("000")).format(code) + "] " + message;
    }

    /**
     * Gets the complete message.
     * 
     * @return the complete message
     */
    public String msg() {

        return completeMessage;
    }

}
