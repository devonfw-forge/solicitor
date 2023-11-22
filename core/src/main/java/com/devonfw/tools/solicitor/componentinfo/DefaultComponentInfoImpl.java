package com.devonfw.tools.solicitor.componentinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default POJO implementation of a {@link ComponentInfo}.
 *
 */
public class DefaultComponentInfoImpl implements ComponentInfo {

  private String packageUrl;

  private boolean dataAvailable;

  private String dataStatus;

  private List<String> traceabilityNotes;

  private DefaultComponentInfoDataImpl componentInfoData;

  /**
   * The constructor.
   */
  public DefaultComponentInfoImpl() {

    this.traceabilityNotes = new ArrayList<>();
  }

  /**
   * Copy-Constructor. Allows to construct an instance of the class from another {@link ComponentInfo} instance. Members
   * are deep copied. Any changes to the new instance do not affect the original source.
   *
   * @param source the inctance to copy the data from
   *
   */
  public DefaultComponentInfoImpl(ComponentInfo source) {

    this();
    this.packageUrl = source.getPackageUrl();
    this.dataAvailable = source.isDataAvailable();
    this.dataStatus = source.getDataStatus();
    for (String traceabilityNote : source.getTraceabilityNotes()) {
      addTraceabillityNote(traceabilityNote);
    }
    if (this.dataAvailable) {
      this.componentInfoData = new DefaultComponentInfoDataImpl(source.getComponentInfoData());
    }
  }

  /**
   * @param packageUrl new value of {@link #getPackageUrl}.
   */
  public void setPackageUrl(String packageUrl) {

    this.packageUrl = packageUrl;
  }

  /**
   * @param dataAvailable new value of {@link #isDataAvailable}.
   */
  public void setDataAvailable(boolean dataAvailable) {

    this.dataAvailable = dataAvailable;
  }

  /**
   * @param dataStatus new value of {@link #getDataStatus}.
   */
  public void setDataStatus(String dataStatus) {

    this.dataStatus = dataStatus;
  }

  /**
   * @param componentInfoData new value of {@link #getComponentInfoData}.
   */
  public void setComponentInfoData(DefaultComponentInfoDataImpl componentInfoData) {

    this.componentInfoData = componentInfoData;
  }

  @Override
  public String getPackageUrl() {

    return this.packageUrl;
  }

  @Override
  public String getDataStatus() {

    return this.dataStatus;
  }

  /**
   * @return dataAvailable
   */
  @Override
  public boolean isDataAvailable() {

    return this.dataAvailable;
  }

  /**
   * @return componentInfoData
   */
  @Override
  public DefaultComponentInfoDataImpl getComponentInfoData() {

    return this.componentInfoData;
  }

  @Override
  public List<String> getTraceabilityNotes() {

    return Collections.unmodifiableList(this.traceabilityNotes);
  }

  /**
   * Clears the list of tracebilityNotes.
   */
  public void clearTraceabilityNotes() {

    this.traceabilityNotes = new ArrayList<>();
  }

  /**
   * Appends a new traceabilityNote to the already existing list.
   *
   * @param traceabilityNote the note to append
   */
  public void addTraceabillityNote(String traceabilityNote) {

    this.traceabilityNotes.add(traceabilityNote);
  }

}
