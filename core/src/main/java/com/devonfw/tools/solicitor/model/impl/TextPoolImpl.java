package com.devonfw.tools.solicitor.model.impl;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Implementation of a {@link TextPool}.
 *
 */
public class TextPoolImpl implements TextPool {

  /** The key used for <code>null</code> values. */
  private static final String NULL_KEY = "NULL_KEY";

  private Map<String, String> dataMap;

  /**
   * @return dataMap
   */
  public Map<String, String> getDataMap() {

    return this.dataMap;
  }

  /**
   * @param dataMap new value of {@link #getDataMap}.
   */
  public void setDataMap(Map<String, String> dataMap) {

    this.dataMap = dataMap;
  }

  /**
   * The constructor.
   */
  public TextPoolImpl() {

    this.dataMap = new TreeMap<>();

  }

  @Override
  public String store(String text) {

    // special handling of null (null values never get stored in the map)
    if (text == null) {
      return NULL_KEY;
    }

    String key = DigestUtils.sha256Hex(text);
    if (!this.dataMap.containsKey(key)) {
      // we ignore the possibility of hash collisions (same key, different text); the probability of any additional line
      // of code introducing a bug is much higher than the likelihood of a hash collision
      this.dataMap.put(key, text);
    }
    return key;
  }

  @Override
  public String retrieve(String key) {

    if (key == null) {
      throw new NullPointerException("Key for pool must not be null");
    }
    if (NULL_KEY.equals(key)) {
      return null;
    }
    String result = this.dataMap.get(key);
    if (result == null) {
      throw new NoSuchElementException("No data in text pool for key '" + key + "'");
    }
    return result;
  }

}
