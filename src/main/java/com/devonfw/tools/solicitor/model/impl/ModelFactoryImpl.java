/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.webcontent.InMemoryMapWebContentProvider;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.impl.inventory.ApplicationComponentImpl;
import com.devonfw.tools.solicitor.model.impl.inventory.NormalizedLicenseImpl;
import com.devonfw.tools.solicitor.model.impl.inventory.RawLicenseImpl;
import com.devonfw.tools.solicitor.model.impl.masterdata.ApplicationImpl;
import com.devonfw.tools.solicitor.model.impl.masterdata.EngagementImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.devonfw.tools.solicitor.model.masterdata.EngagementType;
import com.devonfw.tools.solicitor.model.masterdata.GoToMarketModel;

@Component
public class ModelFactoryImpl extends ModelFactory {
    private static final Logger LOG =
            LoggerFactory.getLogger(ModelFactoryImpl.class);

    @Autowired
    private InMemoryMapWebContentProvider licenseContentProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public NormalizedLicense newNormalizedLicense() {

        NormalizedLicenseImpl result = new NormalizedLicenseImpl();
        result.setLicenseContentProvider(licenseContentProvider);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NormalizedLicense newNormalizedLicense(RawLicense rawLicense) {

        NormalizedLicenseImpl result = new NormalizedLicenseImpl(rawLicense);
        result.setLicenseContentProvider(licenseContentProvider);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RawLicense newRawLicense() {

        return new RawLicenseImpl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationComponent newApplicationComponent() {

        return new ApplicationComponentImpl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Application newApplication(String name, String releaseId,
            String releaseDate, String sourceRepo,
            String programmingEcosystem) {

        return new ApplicationImpl(name, releaseId, releaseDate, sourceRepo,
                programmingEcosystem);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Engagement newEngagement(String engagementName,
            EngagementType engagementType, String clientName,
            GoToMarketModel goToMarketModel) {

        return new EngagementImpl(engagementName, engagementType, clientName,
                goToMarketModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Object> getAllModelObjects(Engagement engagement) {

        Map<String, AbstractDataRowSource> resultMap = new TreeMap<>();
        EngagementImpl eg = (EngagementImpl) engagement;
        resultMap.put(eg.getId(), eg);
        for (Application application : eg.getApplications()) {
            ApplicationImpl ap = (ApplicationImpl) application;
            resultMap.put(ap.getId(), ap);
            for (ApplicationComponent applicationComponent : ap
                    .getApplicationComponents()) {
                ApplicationComponentImpl ac =
                        (ApplicationComponentImpl) applicationComponent;
                resultMap.put(ac.getId(), ac);
                for (RawLicense rawLicense : ac.getRawLicenses()) {
                    RawLicenseImpl rl = (RawLicenseImpl) rawLicense;
                    resultMap.put(rl.getId(), rl);
                }
                for (NormalizedLicense normalizedLicense : ac
                        .getNormalizedLicenses()) {
                    NormalizedLicenseImpl nl =
                            (NormalizedLicenseImpl) normalizedLicense;
                    resultMap.put(nl.getId(), nl);
                }
            }
        }
        return Collections.unmodifiableCollection(resultMap.values());
    }

}
