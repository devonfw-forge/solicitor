package com.devonfw.tools.solicitor.reader.cyclonedx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorPackageURLException;
import com.devonfw.tools.solicitor.common.packageurl.impl.DelegatingPackageURLHandlerImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.AbstractReader;
import com.devonfw.tools.solicitor.reader.Reader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * A {@link Reader} which reads data produced by the <a href="https://github.com/CycloneDX/cdxgen">CDXGEN Tool</a>.
 */
@Component
public class CyclonedxReader extends AbstractReader implements Reader {

  private static final Logger LOG = LoggerFactory.getLogger(CyclonedxReader.class);

/**
 * The supported type of this {@link Reader}.
 */
public static final String SUPPORTED_TYPE = "cyclonedx";

@Autowired
private DelegatingPackageURLHandlerImpl delegatingPackageURLHandler;

public void setDelegatingPackageURLHandler(DelegatingPackageURLHandlerImpl delegatingPackageURLHandler) {
	this.delegatingPackageURLHandler = delegatingPackageURLHandler;
}

/** {@inheritDoc} */
@Override
public Set<String> getSupportedTypes() {
	return Collections.singleton(SUPPORTED_TYPE);
}

@Override
public void readInventory(String type, String sourceUrl, Application application, UsagePattern usagePattern,
    String repoType, Map<String, String> configuration) {

  int componentCount = 0;
  int licenseCount = 0;
  InputStream is;
	try {
		is = this.inputStreamFactory.createInputStreamFor(sourceUrl);
	} catch (IOException e1) {
		throw new SolicitorRuntimeException("Could not open inventory source '" + sourceUrl + "' for reading", e1);
	}
  // According to tutorial https://github.com/FasterXML/jackson-databind/
  ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  try { 
    JsonNode rootNode = mapper.readTree(is);

    // Get all components in SBOM
    JsonNode componentsNode = rootNode.get("components");
    if (componentsNode != null) {
    	for (JsonNode componentNode : componentsNode) {
  
    		 String groupId = componentNode.get("group").asText();
         String artifactId = componentNode.get("name").asText();
         String version = componentNode.get("version").asText();
         String purl = componentNode.get("purl").asText();

         // fill appComponent
         ApplicationComponent appComponent = getModelFactory().newApplicationComponent();
         appComponent.setApplication(application);
         componentCount++;

         appComponent.setGroupId(groupId);
         appComponent.setArtifactId(artifactId);
         appComponent.setVersion(version);
         appComponent.setUsagePattern(usagePattern);
         appComponent.setRepoType(repoType);
         
		     try {
		     		 // check if handler exists for the package type defined in purl
		         if(!delegatingPackageURLHandler.sourceDownloadUrlFor(purl).isEmpty()) {
		         	appComponent.setPackageUrl(purl);
		         }
		     }catch (SolicitorPackageURLException ex) {       
		     		LOG.warn("WARNING: {}", ex.getMessage());
		     }
		     // Fill license information
		     JsonNode licensesNode = componentNode.get("licenses");				//licenses
         
         // case if no licenses field exists
         if(licensesNode == null) {
        	 addRawLicense(appComponent, null, null, sourceUrl);
         }
         // case if licenses field exists but is empty
         else if (licensesNode != null && licensesNode.isEmpty()) {
        	 addRawLicense(appComponent, null, null, sourceUrl);
         }
         // case if licenses field exists and contains licenses
         else if (licensesNode != null && licensesNode.isEmpty()==false) {
             // Iterate over each "license" object within the "licenses" array
             for (JsonNode licenseNode : licensesNode) {
            	 // Declared License can be written either in "id" or "name" field. Prefer "id" as its written in SPDX format.
               if (licenseNode.get("license").has("id")) {
              	 	if(licenseNode.get("license").has("url")) {
              	 		addRawLicense(appComponent, licenseNode.get("license").get("id").asText(), licenseNode.get("license").get("url").asText(), sourceUrl);
              	 	}
              	 	else {
              	 		addRawLicense(appComponent, licenseNode.get("license").get("id").asText(), null, sourceUrl);
              	 	}
               } 
               else if (licenseNode.get("license").has("name")) {
                 	if(licenseNode.get("license").has("url")) {
                 			addRawLicense(appComponent, licenseNode.get("license").get("name").asText(), licenseNode.get("license").get("url").asText(), sourceUrl);
                 	}
                 	else {
                 		addRawLicense(appComponent, licenseNode.get("license").get("name").asText(), null, sourceUrl);
                 	}
               }
             }
         }
    	 }
    }
    doLogging(sourceUrl, application, componentCount, licenseCount);
  } catch (IOException e) {
    throw new SolicitorRuntimeException("Could not read CycloneDx inventory source '" + sourceUrl + "'", e);
  }

}














//@Override
//public void readInventory(String type, String sourceUrl, Application application, UsagePattern usagePattern,
//		String repoType, Map<String, String> configuration) {
//		
//    int components = 0;
//    int licenses = 0;
//    InputStream is;
//
//    try {
//        is = this.inputStreamFactory.createInputStreamFor(sourceUrl);
//      } catch (IOException e1) {
//        throw new SolicitorRuntimeException("Could not open inventory source '" + sourceUrl + "' for reading", e1);
//      }
//    
//    // Create a JSON parser instance
//    JsonParser parser = new JsonParser();
//
//    try {
//   	// Parse the sbom.json file into a Bom object
//	Bom bom = parser.parse(is);
//	
//    // Access the list of components in the Bom
//    for (org.cyclonedx.model.Component component : bom.getComponents()) {
//        ApplicationComponent appComponent = getModelFactory().newApplicationComponent();
//        appComponent.setApplication(application);
//        appComponent.setGroupId(component.getGroup());
//        appComponent.setArtifactId(component.getName());
//        appComponent.setVersion(component.getVersion());
//        appComponent.setUsagePattern(usagePattern);
//        appComponent.setRepoType(repoType);
//        
//        try {
//        	// check if handler exists for the package type defined in purl
//            if(!delegatingPackageURLHandler.sourceDownloadUrlFor(component.getPurl()).isEmpty()) {
//            	appComponent.setPackageUrl(component.getPurl());
//            }
//        }catch (SolicitorPackageURLException ex) {       
//        	LOG.warn("WARNING: {}", ex.getMessage());
//        }
//        components++;
//        
//        // in case no license field exists, insert an empty entry
//        if (component.getLicenseChoice() == null) {	
//            addRawLicense(appComponent, null, null, sourceUrl);
//        } 
//        else {
//              // in case license field exists but empty, insert an empty entry.
//        	  if(component.getLicenseChoice().getLicenses() == null){		
//                  addRawLicense(appComponent, null, null, sourceUrl);
//        	  }
//        	  else {
//        		  	// in case license field exists and contains licenses, insert the license
//	            	// Declared License can be written either in "id" or "name" field. Prefer "id" as its written in SPDX format.
//		                for (org.cyclonedx.model.License lic : component.getLicenseChoice().getLicenses()) {
//		                	if (lic.getId()!=null) {
//		                        addRawLicense(appComponent, lic.getId(), lic.getUrl(), sourceUrl);
//		                	}
//		                	else if (lic.getName()!=null) {
//		                		addRawLicense(appComponent, lic.getName(), lic.getUrl(), sourceUrl);
//		                	}
//		                }
//            	  }
//            }
//        }
//	    }
//        catch (ParseException e) {
//			e.printStackTrace();
//		}
//	    doLogging(sourceUrl, application, components, licenses);
//	}

}
