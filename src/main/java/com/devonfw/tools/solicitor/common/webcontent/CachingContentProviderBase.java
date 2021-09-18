/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Scanner;

import net.bytebuddy.implementation.Implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.devonfw.tools.solicitor.common.UrlInputStreamFactory;

/**
 * Abstract base {@link Implementation} of {@link ContentProvider}s which first
 * try to load the content from some cache. If they are not able to load the
 * content from the cache they will delegate to some other
 * {@link ContentProvider} for further handling.
 *
 */
public abstract class CachingContentProviderBase<C extends Content> implements ContentProvider<C> {

    private static final Logger LOG = LoggerFactory.getLogger(CachingContentProviderBase.class);

    @Autowired
    private UrlInputStreamFactory urlInputStreamFactory;

    private ContentFactory<C> contentFactory;

    private ContentProvider<C> nextContentProvider;

    /**
     * Constructor.
     */
    public CachingContentProviderBase(ContentFactory<C> contentFactory, ContentProvider<C> nextContentProvider) {

        this.contentFactory = contentFactory;
        this.nextContentProvider = this.nextContentProvider;

    }

    /**
     * Determine the URL to take in the cache for loading the content.
     *
     * @param key the cache key of the content.
     * @return the URL to take for looking up the content in the cache
     */
    protected abstract Collection<String> getCacheUrls(String key);

    /**
     * Calculate the key for the given web content URL.
     *
     * @param url the URL of the web content
     * @return the cache key
     */
    public String getKey(String url) {

        /**
         * Normalize URL to http
         */
        if (url.startsWith("https")) {
            url = url.replace("https", "http");
        }
        String result = url.replaceAll("\\W", "_");
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * Tries to load the web content from the resource found via the URLs
     * returned by {@link #getCacheUrls(String)}. First hit will be returned. If
     * this does not succeed, then delegate further processing to
     * {@link CachingContentProviderBase#loadFromNext(String)}.
     */
    @Override
    public C getContentForUri(String url) {

        String key = getKey(url);
        Collection<String> classPathUrls = getCacheUrls(key);

        for (String classPathUrl : classPathUrls) {
            try (InputStream is = this.urlInputStreamFactory.createInputStreamFor(classPathUrl);
                    Scanner s = new Scanner(is)) {
                s.useDelimiter("\\A");
                String result = s.hasNext() ? s.next() : "";
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Content for url '" + url + "' found at '" + classPathUrl + "'");
                }
                return this.contentFactory.fromString(result);
            } catch (FileNotFoundException fnfe) {
                LOG.debug("Content for url '" + url + "' NOT found at '" + classPathUrl + "'");
            } catch (IOException e) {
                LOG.debug("Could not retieve content for url '" + url + "' from '" + classPathUrl + "'", e);
            }
        }
        return loadFromNext(url);
    }

    /**
     * Method for loading the requested web content from the next new
     * {@link ContentProvider} defined in the chain.
     *
     * @param url the URL of the requests web content
     * @return the content of the web content given by the URL
     */
    protected C loadFromNext(String url) {

        return this.nextContentProvider.getContentForUri(url);
    }

}
