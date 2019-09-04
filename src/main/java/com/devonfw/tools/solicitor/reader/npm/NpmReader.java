package com.devonfw.tools.solicitor.reader.npm;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.Reader;

import lombok.Setter;

@Component
public class NpmReader implements Reader {

	@Autowired
    @Setter
    private InputStreamFactory inputStreamFactory;

	@Override
	public boolean accept(String type) {
		return "npm".equals(type);
	}

	@Override
	public void readInventory(String sourceUrl, Application application, UsagePattern usagePattern) {
		InputStream is;
        try {
            is = inputStreamFactory.createInputStreamFor(sourceUrl);

            java.io.Reader reader = new InputStreamReader(is);

            for (CSVRecord record : CSVFormat.newFormat(',').withQuote('\"').parse(reader)) {
            	if(record.get(0).contains("module name")) continue;

            	ApplicationComponent appComponent = new ApplicationComponent();
                String[] module = record.get(0).split("@");
                appComponent.setArtifactId(module[module.length-2]);
                appComponent.setVersion(module[module.length-1]);
            	appComponent.setApplication(application);
                appComponent.setUsagePattern(usagePattern);
                appComponent.setGroupId(record.get(4));
                appComponent.setOssHomepage(record.get(2));
                RawLicense mlic = new RawLicense();
                mlic.setApplicationComponent(appComponent);
        		mlic.setDeclaredLicense(record.get(1));
                mlic.setLicenseUrl(record.get(3));
            }
        } catch (IOException e) {
            throw new SolicitorRuntimeException(
                    "Could not read NPM inventory source +'" + sourceUrl + "'",e);
        }
	}
}
