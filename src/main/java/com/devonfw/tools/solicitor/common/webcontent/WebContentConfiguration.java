/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO ohecker: This type ...
 *
 * @author <a href="TODO@sdm.de">TODO</a>
 * @version $Revision$
 */
@Configuration
public class WebContentConfiguration {

    @Value("${solicitor.classpath-license-cache-locations}")
    private String[] cachePaths;

    @Value("${webcontent.skipdownload}")
    private boolean skipdownload;

    @Bean
    public ContentFactory<WebContent> webContentFactory() {

        return new WebContentFactory();
    }

    @Bean
    public InMemoryMapContentProvider<WebContent> inMemoryMapWebContentProvider() {

        return new InMemoryMapContentProvider<>(classpathWebContentProvider());

    }

    @Bean
    public ClasspathContentProvider<WebContent> classpathWebContentProvider() {

        return new ClasspathContentProvider<>(webContentFactory(), filesystemCachingWebContentProvider(),
                this.cachePaths);
    }

    @Bean
    public FilesystemCachingContentProvider<WebContent> filesystemCachingWebContentProvider() {

        return new FilesystemCachingContentProvider<>(webContentFactory(), directUrlWebContentProvider(), "licenses");
    }

    @Bean
    public DirectUrlWebContentProvider directUrlWebContentProvider() {

        return new DirectUrlWebContentProvider(this.skipdownload);
    }
}
