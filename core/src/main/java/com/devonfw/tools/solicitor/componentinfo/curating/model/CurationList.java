package com.devonfw.tools.solicitor.componentinfo.curating.model;

import java.util.List;

/**
 * Holds a list of {@link ComponentInfoCuration}s.
 *
 */
public class CurationList {

  List<ComponentInfoCuration> artifacts;

  /**
   * @return artifacts
   */
  public List<ComponentInfoCuration> getArtifacts() {

    return this.artifacts;
  }

  /**
   * @param artifacts new value of {@link #getArtifacts}.
   */
  public void setArtifacts(List<ComponentInfoCuration> artifacts) {

    this.artifacts = artifacts;
  }

  /**
   * The constructor.
   */
  public CurationList() {

  }

}
