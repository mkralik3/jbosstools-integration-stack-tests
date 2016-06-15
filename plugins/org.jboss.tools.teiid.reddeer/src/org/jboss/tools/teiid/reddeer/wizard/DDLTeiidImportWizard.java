package org.jboss.tools.teiid.reddeer.wizard;

import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.ShellWithTextIsActive;
import org.jboss.reddeer.jface.wizard.ImportWizardDialog;
import org.jboss.reddeer.swt.api.Button;
import org.jboss.reddeer.swt.impl.button.CheckBox;
import org.jboss.reddeer.swt.impl.button.FinishButton;
import org.jboss.reddeer.swt.impl.button.NextButton;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.combo.DefaultCombo;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.tab.DefaultTabItem;
import org.jboss.reddeer.swt.impl.text.DefaultText;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.tools.teiid.reddeer.condition.IsInProgress;

/**
 * Wizard for import teiid DDL
 * @author mkralik
 */
public class DDLTeiidImportWizard  extends ImportWizardDialog{

	public static final String DIALOG_TITLE = "Import Teiid DDL";

	public static final String Source_Type = "Source Model";
	public static final String View_Type = "View Model";
	
	public DDLTeiidImportWizard() {
		super("Teiid Designer","DDL File (Teiid) >> Source or View Model");
	}
	
	public DDLTeiidImportWizard activate(){
		new DefaultShell(DIALOG_TITLE);
		return this;
	}
	
	public DDLTeiidImportWizard setPath(String path){
		activate();
		new DefaultCombo(0).setText(path);
		return this;
	}
	
	public DDLTeiidImportWizard setFolder(String... folder){
		activate();
		new PushButton(2).click();
		new DefaultTreeItem(folder).select();
		new PushButton("OK").click();
		return this;
	}
	
	public DDLTeiidImportWizard setName(String name){
		activate();
		new DefaultText(1).setText(name);
		return this;
	}
	/**
	 * @param modelType - use DDLTeiidImportWizard.Source_Type or DDLTeiidImportWizard.View_Type
	 */
	public DDLTeiidImportWizard setModelType(String modelType){
		activate();
		new DefaultCombo(1).setText(modelType);
		return this;
	}
	
	public DDLTeiidImportWizard generateValidDefaultSQL(boolean check){
		activate();
		CheckBox checkBox = new CheckBox("Generate valid default SQL (SELECT null AS column_name, etc....)");
		if(check != checkBox.isChecked()){
			checkBox.click();
		}
		return this;
	}

	public DDLTeiidImportWizard setDescriptionOfModel(boolean check){
		activate();
		new DefaultTabItem("Options").activate();
		CheckBox checkBox = new CheckBox("Set description of model entities to corresponding DDL statement");
		if(check != checkBox.isChecked()){
			checkBox.click();
		}
		new DefaultTabItem("Model Definition").activate();
		return this;
	}

	public DDLTeiidImportWizard createModelEntities(boolean check){
		activate();
		new DefaultTabItem("Options").activate();
		CheckBox checkBox = new CheckBox("Create model entities for DDL defined by unsupported DML (e.g., Views)");
		if(check != checkBox.isChecked()){
			checkBox.click();
		}
		new DefaultTabItem("Model Definition").activate();
		return this;
	}	

	public DDLTeiidImportWizard filterUniqueConstraints(boolean check){
		activate();
		new DefaultTabItem("Options").activate();
		CheckBox checkBox = new CheckBox("Filter unique constraints already defined as a primary key (Teiid Only)");
		if(check != checkBox.isChecked()){
			checkBox.click();
		}
		new DefaultTabItem("Model Definition").activate();
		return this;
	}
	@Override
	public void next(){
		Button button = new NextButton();
		button.click();
		new WaitWhile(new IsInProgress(), TimePeriod.LONG);
	}
	@Override
	public void finish(){
		FinishButton button = new FinishButton();
		button.click();
		if(new ShellWithTextIsActive("Table 'Supports Update' Property Changed").test()){
			new PushButton("OK").click();
		}
		new WaitWhile(new IsInProgress(), TimePeriod.LONG);
	}
}