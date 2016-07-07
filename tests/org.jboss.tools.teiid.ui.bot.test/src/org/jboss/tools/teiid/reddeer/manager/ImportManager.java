package org.jboss.tools.teiid.reddeer.manager;

import java.util.Properties;

import org.jboss.tools.teiid.reddeer.wizard.imports.SalesforceImportWizard;

public class ImportManager {

	/**
	 * Import metadata from Salesforce account
	 * 
	 * @param propsFile
	 */
	public void importFromSalesForce(String projectName, String modelName, String connectionProfile, Properties props) {
		SalesforceImportWizard sfWizard = new SalesforceImportWizard();
		sfWizard.setConnectionProfile(connectionProfile);
		sfWizard.setModelName(modelName);
		sfWizard.setProjectName(projectName);

		String objects = null;
		if ((objects = props.getProperty("deselectedObjects")) != null) {
			sfWizard.setDeselectedObjects(objects.split(","));
		} else if ((objects = props.getProperty("selectedObjects")) != null) {
			sfWizard.setSelectedObjects(objects.split(","));
		}

		sfWizard.execute();
	}
}
