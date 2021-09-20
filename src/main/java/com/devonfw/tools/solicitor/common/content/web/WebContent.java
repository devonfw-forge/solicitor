/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.content.web;

import com.devonfw.tools.solicitor.common.content.Content;

/**
 * A {@link Content} which represents a web resource, e.g. a license text which might be accessed via a given URL.
 */
public class WebContent implements Content {
  private String content;

  /**
   * The Constructor.
   *
   * @param content the (string) payload of the content
   */
  public WebContent(String content) {

    this.content = content;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String asString() {

    return this.content;
  }

  /**
   * Gets the wrapped content.
   *
   * @return the content
   */
  public String getContent() {

    return this.content;
  }

}
