/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.config.RuleConfig;
import com.devonfw.tools.solicitor.config.WriterConfig;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.Reader;

/**
 * Holder for the Solicitor configuration.
 */
@Component
public class SolicitorSetup {

    /**
     * Holder for the setup of a {@link Reader}.
     */
    public static class ReaderSetup {
        private String type;

        private String source;

        private Application application;

        private UsagePattern usagePattern;

        private String repoType;

        /**
         * This method gets the field <code>application</code>.
         *
         * @return the field application
         */
        public Application getApplication() {

            return this.application;
        }

        /**
         * This method gets the field <code>source</code>.
         *
         * @return the field source
         */
        public String getSource() {

            return this.source;
        }

        /**
         * This method gets the field <code>type</code>.
         *
         * @return the field type
         */
        public String getType() {

            return this.type;
        }

        /**
         * This method gets the field <code>usagePattern</code>.
         *
         * @return the field usagePattern
         */
        public UsagePattern getUsagePattern() {

            return this.usagePattern;
        }

        /**
         * This method gets the field <code>repoType</code>.
         *
         * @return the field repoType
         */
        public String getRepoType() {

            return this.repoType;
        }

        /**
         * This method sets the field <code>application</code>.
         *
         * @param application the new value of the field application
         */
        public void setApplication(Application application) {

            this.application = application;
        }

        /**
         * This method sets the field <code>source</code>.
         *
         * @param source the new value of the field source
         */
        public void setSource(String source) {

            this.source = source;
        }

        /**
         * This method sets the field <code>type</code>.
         *
         * @param type the new value of the field type
         */
        public void setType(String type) {

            this.type = type;
        }

        /**
         * This method sets the field <code>usagePattern</code>.
         *
         * @param usagePattern the new value of the field usagePattern
         */
        public void setUsagePattern(UsagePattern usagePattern) {

            this.usagePattern = usagePattern;
        }

        /**
         * This method sets the field <code>repoType</code>.
         *
         * @param repoType the new value of the field repoType
         */
        public void setRepoType(String repoType) {

            this.repoType = repoType;
        }
    }

    private List<ReaderSetup> readerSetups = new ArrayList<>();

    private List<RuleConfig> ruleSetups = new ArrayList<>();

    private List<WriterConfig> writerSetups = new ArrayList<>();

    /**
     * This method gets the field <code>readerSetups</code>.
     *
     * @return the field readerSetups
     */
    public List<ReaderSetup> getReaderSetups() {

        return this.readerSetups;
    }

    /**
     * This method gets the field <code>ruleSetups</code>.
     *
     * @return the field ruleSetups
     */
    public List<RuleConfig> getRuleSetups() {

        return this.ruleSetups;
    }

    /**
     * This method gets the field <code>writerSetups</code>.
     *
     * @return the field writerSetups
     */
    public List<WriterConfig> getWriterSetups() {

        return this.writerSetups;
    }

    /**
     * This method sets the field <code>readerSetups</code>.
     *
     * @param readerSetups the new value of the field readerSetups
     */
    public void setReaderSetups(List<ReaderSetup> readerSetups) {

        this.readerSetups = readerSetups;
    }

    /**
     * This method sets the field <code>ruleSetups</code>.
     *
     * @param ruleSetups the new value of the field ruleSetups
     */
    public void setRuleSetups(List<RuleConfig> ruleSetups) {

        this.ruleSetups = ruleSetups;
    }

    /**
     * This method sets the field <code>writerSetups</code>.
     *
     * @param writerSetups the new value of the field writerSetups
     */
    public void setWriterSetups(List<WriterConfig> writerSetups) {

        this.writerSetups = writerSetups;
    }

}
