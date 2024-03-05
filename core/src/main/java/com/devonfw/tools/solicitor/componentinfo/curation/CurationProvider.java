package com.devonfw.tools.solicitor.componentinfo.curation;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;

/**
 * Provides curation data for {@link ComponentInfo} data.
 *
 */
public interface CurationProvider {

  /**
   * Return the curation data for a given package.
   *
   * @param packageUrl identifies the package
   * @param curationDataSelector identifies which source should be used for the curation data. <code>null</code>
   *        indicates that the default should be used. The special value "none" indicates that no curations should be
   *        returned.
   * @return the curation data if it exists. <code>null</code> if no curation exist for the package or the
   *         curationDataSelector was given as "none".
   * @throws ComponentInfoAdapterException if something unexpected happens
   * @throws ComponentInfoAdapterNonExistingCurationDataSelectorException if the specified curationDataSelector could
   *         not be resolved
   */
  ComponentInfoCuration findCurations(String packageUrl, String curationDataSelector)
      throws ComponentInfoAdapterException, ComponentInfoAdapterNonExistingCurationDataSelectorException;

}