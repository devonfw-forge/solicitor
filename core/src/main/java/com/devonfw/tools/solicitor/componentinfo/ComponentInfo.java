package com.devonfw.tools.solicitor.componentinfo;

import java.util.Collection;

/**
 * Data structure which holds information about a component which comes from an external data source, like the results
 * of a scancode scan.
 *
 */
public interface ComponentInfo {

  /**
   * Gets all copyrights.
   *
   * @return the copyrights
   */
  Collection<String> getCopyrights();

  /**
   * Gets all licenses.
   *
   * @return all licenses
   */
  Collection<? extends LicenseInfo> getLicenses();

  /**
   * Gets the path to the notice file (if any)
   *
   * @return path to the notice file
   */
  String getNoticeFilePath();

  /**
   * Gets the url of the projects homepage,
   *
   * @return url to the projects homepage
   */
  String getUrl();

  /**
   * Gets the url of the source code repository.
   *
   * @return sourceRepoUrl to the license text
   */
  String getSourceRepoUrl();

}