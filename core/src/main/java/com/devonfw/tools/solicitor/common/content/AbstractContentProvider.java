/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.content;

/**
 * Abstract {@link ContentProvider} which allows to create instances of the
 * provided classes.
 *
 * @param <C> type of the objects provided
 */
public abstract class AbstractContentProvider<C extends Content> implements ContentProvider<C> {
    private ContentFactory<C> contentFactory;

    /**
     * The Constructor.
     *
     * @param contentFactory the factory for creating instances of C
     */
    public AbstractContentProvider(ContentFactory<C> contentFactory) {

        this.contentFactory = contentFactory;

    }

    /**
     * Create an instance of C.
     *
     * @param string a string representing the instance
     * @return an instance of C
     */
    public C createContentFromString(String string) {

        return this.contentFactory.fromString(string);
    }

    /**
     * Creates an empty instance of C.
     *
     * @return an empty instance of C
     */
    public C createEmptyContent() {

        return this.contentFactory.emptyContent();
    }
}
