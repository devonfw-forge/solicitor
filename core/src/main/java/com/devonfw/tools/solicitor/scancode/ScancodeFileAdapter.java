package com.devonfw.tools.solicitor.scancode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Adapter for reading Scancode information for a package from a file, applying any given curations and returning the
 * information as a {@link ComponentScancodeInfos} object.
 */
@Component
public class ScancodeFileAdapter implements ScancodeAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(ScancodeFileAdapter.class);

  private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  private String repoBasePath;

  private String curationsFileName;

  private double minLicenseScore;

  private double minLicensefilePercentage;

  private boolean curationsExistenceLogged;

  @Autowired
  private AllKindsPackageURLHandler packageURLHandler;

  /**
   * The constructor.
   */

  public ScancodeFileAdapter() {

  }

  /**
   * Sets repoBasePath.
   *
   * @param repoBasePath new value of repoBasePath.
   */
  @Value("${solicitor.scancode.repo-base-path}")
  public void setRepoBasePath(String repoBasePath) {

    this.repoBasePath = repoBasePath;
  }

  /**
   * Sets curationsFileName.
   *
   * @param curationsFileName new value of curationsFileName.
   */
  @Value("${solicitor.scancode.curations-filename}")
  public void setCurationsFileName(String curationsFileName) {

    this.curationsFileName = curationsFileName;
  }

  /**
   * Sets minLicenseScore.
   *
   * @param minLicenseScore new value of minLicenseScore.
   */
  @Value("${solicitor.scancode.min-license-score}")
  public void setMinLicenseScore(double minLicenseScore) {

    this.minLicenseScore = minLicenseScore;
  }

  /**
   * Sets minLicensefilePercentage.
   *
   * @param minLicensefilePercentage new value of minLicensefilePercentage.
   */
  @Value("${solicitor.scancode.min-licensefile-percentage}")
  public void setMinLicensefilePercentage(double minLicensefilePercentage) {

    this.minLicensefilePercentage = minLicensefilePercentage;
  }

  /**
   * Retrieves the Scancode information and curations for a package identified by the given package URL. Returns the
   * data as a {@link ComponentScancodeInfos} object.
   *
   * @param packageUrl The identifier of the package for which information is requested
   * @return the data derived from the scancode results after applying any defined curations. <code>null</code> is
   *         returned if no data is available,
   * @throws ScancodeException if there was an exception when reading the data. In case that there is no data available
   *         no exception will be thrown. Instead <code>null</code> will be return in such a case.
   */
  @Override
  public ComponentScancodeInfos getComponentScancodeInfos(String packageUrl) throws ScancodeException {

    ComponentScancodeInfos componentScancodeInfos = determineScancodeInformation(packageUrl);
    if (componentScancodeInfos == null) {
      return null;
    }
    applyCurations(packageUrl, componentScancodeInfos);

    return componentScancodeInfos;

  }

  /**
   * Read scancode information for the given package from local file storage.
   *
   * @param packageUrl The package url of the package
   * @return the read scancode information, <code>null</code> if no information was found
   * @throws ScancodeException if there was an exception when reading the data. In case that there is no data available
   *         no exception will be thrown. Instead <code>null</code> will be return in such a case.
   */
  private ComponentScancodeInfos determineScancodeInformation(String packageUrl) throws ScancodeException {

    ComponentScancodeInfos componentScancodeInfos = new ComponentScancodeInfos(this.minLicenseScore,
        this.minLicensefilePercentage);
    String packagePathPart = this.packageURLHandler.pathFor(packageUrl);
    String path = this.repoBasePath + "/" + packagePathPart + "/scancode.json";

    File scanCodeFile = new File(path);
    if (!scanCodeFile.exists()) {
      LOG.debug("No Scancode info available for PackageURL '{}'", packageUrl);
      return null;
    }
    LOG.debug("Found Scancode info for PackageURL '{}'", packageUrl);
    try (InputStream is = new FileInputStream(scanCodeFile)) {

      JsonNode scancodeJson = mapper.readTree(is);

      for (JsonNode file : scancodeJson.get("files")) {
        if ("directory".equals(file.get("type").asText())) {
          continue;
        }
        if (file.get("path").asText().contains("/NOTICE")) {
          componentScancodeInfos.addNoticeFilePath("file:" + this.repoBasePath + "/"
              + this.packageURLHandler.pathFor(packageUrl) + "/" + file.get("path").asText(), 100.0);
        }
        for (JsonNode cr : file.get("copyrights")) {
          componentScancodeInfos.addCopyright(cr.get("value").asText());
        }

        for (JsonNode li : file.get("licenses")) {
          String licenseid = li.get("key").asText();
          String licenseName = li.get("spdx_license_key").asText();
          String licenseDefaultUrl = li.get("scancode_text_url").asText();
          licenseDefaultUrl = normalizeLicenseUrl(packageUrl, licenseDefaultUrl);
          double score = li.get("score").asDouble();
          String licenseFilePath = file.get("path").asText();
          licenseFilePath = normalizeLicenseUrl(packageUrl, licenseFilePath);

          componentScancodeInfos.addLicense(licenseid, licenseName, licenseDefaultUrl, score, licenseFilePath,
              file.get("percentage_of_license_text").asDouble());
        }
      }
      LOG.debug("Scancode info for package {}: {} license, {} copyrights, {} NOTICE files", packageUrl,
          componentScancodeInfos.getLicenses().size(), componentScancodeInfos.getCopyrights().size(),
          componentScancodeInfos.getNoticeFilePath() != null ? 1 : 0);

    } catch (IOException e) {
      throw new ScancodeException("Could not read Scancode JSON", e);
    }
    return componentScancodeInfos;
  }

  /**
   * Checks for the existence of curations for the given package in the local file system and applies them to the
   * component scancode infos.
   *
   * @param packageUrl the identifier of the package
   * @param componentScancodeInfos the componentScancodeInfos to curate
   * @throws ScancodeException if the curations could not be read
   */
  private void applyCurations(String packageUrl, ComponentScancodeInfos componentScancodeInfos)
      throws ScancodeException {

    String packagePathPart = this.packageURLHandler.pathFor(packageUrl);

    File curationsFile = new File(this.curationsFileName);
    if (!curationsFile.exists()) {
      if (!this.curationsExistenceLogged) {
        // log only once
        this.curationsExistenceLogged = true;
        LOG.info(LogMessages.CURATIONS_NOT_EXISTING.msg(), this.curationsFileName);
      }
    } else {
      if (!this.curationsExistenceLogged) {
        // log only once
        this.curationsExistenceLogged = true;
        LOG.info(LogMessages.CURATIONS_PROCESSING.msg(), this.curationsFileName);
      }
      try (InputStream isc = new FileInputStream(this.curationsFileName)) {

        JsonNode curationsObj = yamlMapper.readTree(isc);

        for (JsonNode curations : curationsObj.get("artifacts")) {
          String component = curations.get("name").asText();
          if (component.equals(packagePathPart)) {
            ComponentScancodeInfos oneComponent = componentScancodeInfos;
            if (curations.get("copyrights") != null) {
              oneComponent.clearCopyrights();
              int authorCount = curations.get("copyrights").size();
              for (int i = 0; i < authorCount; i++) {
                oneComponent.addCopyright(curations.get("copyrights").get(i).asText());
              }
            }
            if (curations.get("url") != null) {
              String url = curations.get("url").asText();
              oneComponent.setUrl(url);
            }
            if (curations.get("licenses") != null) {
              oneComponent.clearLicenses();
              for (JsonNode licenseNode : curations.get("licenses")) {
                String license = licenseNode.get("license").asText();
                String url = licenseNode.get("url").asText();
                oneComponent.addLicense(license, license, "", 110, url, 110);
              }
            }
          }
        }

      } catch (IOException e) {
        throw new ScancodeException("Could not read Curations JSON", e);
      }

    }

  }

  /**
   * Adjustment of license paths/urls so that they might retrieved
   *
   * @param packageUrl package url of the package
   * @param licenseFilePath the original path or URL
   * @return the adjustes patjh or url as a url
   */
  private String normalizeLicenseUrl(String packageUrl, String licenseFilePath) {

    String adjustedLicenseFilePath;
    if (licenseFilePath != null) {
      if (licenseFilePath.startsWith("http")) {
        // TODO
        adjustedLicenseFilePath = licenseFilePath.replace("github.com", "raw.github.com").replace("/tree", "");
      } else {
        adjustedLicenseFilePath = "file:" + this.repoBasePath + "/" + this.packageURLHandler.pathFor(packageUrl) + "/"
            + licenseFilePath;
        LOG.debug("LOCAL LICENSE: " + licenseFilePath);
      }
    } else {
      adjustedLicenseFilePath = null;
      // licenseFilePath = null;// "??????";// defaultGithubLicenseURL(repo);
      LOG.debug("NONLOCAL LICENSE: {} (was: {})" + adjustedLicenseFilePath, licenseFilePath);
    }
    return adjustedLicenseFilePath;
  }

}
