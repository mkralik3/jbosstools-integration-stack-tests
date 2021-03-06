package org.jboss.tools.teiid.reddeer.wizard.imports;

import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.button.NextButton;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.button.RadioButton;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;
import org.eclipse.reddeer.swt.impl.group.DefaultGroup;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;

/**
 * Wizard for importing relational model from XML
 * 
 * @author apodhrad
 * 
 */
public class XMLImportWizard extends TeiidImportWizard {

	public static final String DIALOG_TITLE = "Import From XML File Source";

	public static final String LOCAL = "XML file on local file system";
	public static final String REMOTE = "XML file via remote URL";

	
	private XMLImportWizard() {
		super(DIALOG_TITLE, "File Source (XML) >> Source and View Model");
		log.info("XML import wizard is opened");
	}

	public static XMLImportWizard getInstance(){
		return new XMLImportWizard();
	}
	
	public static XMLImportWizard openWizard(){
		XMLImportWizard wizard = new XMLImportWizard();
		wizard.open();
		return wizard;
	}
	
	public XMLImportWizard nextPage(){
		log.info("Go to next wizard page");
		new NextButton().click();
		return this;
	}
	
	public XMLImportWizard activateWizard() {
		new DefaultShell(DIALOG_TITLE);
		return this;
	}
	
	/**
	 * 
	 * @param dialect use one of dialects (DDLCustomImportWizard.TEIID, DDLCustomImportWizard.ORACLE ...)
	 */
	
	/**
	 * @param importMode use one of import mode (XMLImportWizard.LOCAL or XMLImportWizard.REMOTE)
	 */
	public XMLImportWizard setImportMode(String importMode) {
		log.info("Set import mode to : '" + importMode + "'");
		activateWizard();
		new RadioButton(importMode).click();
		return this;	
	}
	
	public XMLImportWizard setDataFileSource(String dataFileSource) {
		log.info("Set data file source: '" + dataFileSource + "'");
		activateWizard();
		new DefaultCombo(0).setSelection(dataFileSource);
		return this;
	}
	
	public XMLImportWizard setSourceModelName(String sourceModelName) {
		log.info("Set source model name to: '" + sourceModelName + "'");
		activateWizard();
		new LabeledText(new DefaultGroup("Source Model Definition"), "Name:").setText(sourceModelName);
		return this;
	}
	
	public XMLImportWizard autoCreateDataSource(boolean checked) {
		log.info("Auto-Create Data Source is : '" + checked + "'");
		activateWizard();
		CheckBox checkBox = new CheckBox("Auto-create Data Source");
		if(checked != checkBox.isChecked()){
			checkBox.click();
		}
		return this;
	}
	
    public XMLImportWizard setJndiName(String jndiName) {
        log.info("Set JNDI name to: '" + jndiName + "'");
		activateWizard();
        jndiName = (jndiName.contains("java:/")) ? jndiName : "java:/" + jndiName;
        new LabeledText("JNDI Name").setText(jndiName);
		return this;
	}
	
	public XMLImportWizard setRootPath(String rootPath) {
		log.info("Set root path to: '" + rootPath + "'");
		activateWizard();
		new LabeledText("Root Path").setText(rootPath);
		return this;
	}
	
	public XMLImportWizard addElement(String element) {
		log.info("Add element: '" + element + "'");
		activateWizard();
		new DefaultTreeItem(new DefaultTree(0), element.split("/")).select();
		new PushButton("Add").click();
		return this;
	}
	
	public XMLImportWizard setViewModelName(String viewModelName) {
		log.info("Set view model name to: '" + viewModelName + "'");
		activateWizard();
		new LabeledText(new DefaultGroup("View Model Definition"), "Name:").setText(viewModelName);
		return this;
	}
	
	public XMLImportWizard setViewTableName(String viewTableName) {
		log.info("Set view table name to: '" + viewTableName + "'");
		activateWizard();
		new LabeledText(new DefaultGroup("View Model Definition"), "New view table name:").setText(viewTableName);
		return this;
	}
}
