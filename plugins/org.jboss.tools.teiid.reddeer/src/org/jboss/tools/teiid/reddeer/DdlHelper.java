package org.jboss.tools.teiid.reddeer;

import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.tools.teiid.reddeer.dialog.GenerateDynamicVdbDialog;
import org.jboss.tools.teiid.reddeer.requirement.TeiidServerRequirement;
import org.jboss.tools.teiid.reddeer.view.ModelExplorer;
import org.jboss.tools.teiid.reddeer.view.ServersViewExt;
import org.jboss.tools.teiid.reddeer.wizard.exports.DDLTeiidExportWizard;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.xml.sax.InputSource;

public class DdlHelper {
	
	@Rule
	private ErrorCollector collector;
	
	public DdlHelper(ErrorCollector collector){
		this.collector = collector;
	}
	
	public String createDynamicVdb(String sourceProject, String staticVdbName, String dynamicVdbName) {
		GenerateDynamicVdbDialog wizard = new ModelExplorer().generateDynamicVDB(sourceProject, staticVdbName);
		wizard.setName(dynamicVdbName)
				.setFileName(dynamicVdbName)
				.setLocation(sourceProject)
				.next()
				.generate();
		String contents = wizard.getContents();
		wizard.finish();
		return contents;
	}
	
	public String exportDDL(String sourceProject, String model, String targetProject) {
		new ModelExplorer().selectItem(sourceProject,model + ".xmi");
	
		DDLTeiidExportWizard.openWizard()
				.setLocation(sourceProject,model + ".xmi")
				.setNameInSource(false)
				.setNativeType(false)
				.nextPage()
				.exportToWorkspace(model, targetProject)
				.finish();
		new DefaultTreeItem(targetProject,model).doubleClick();
		
		return new DefaultStyledText().getText();
	}
	
	public String getXPath(String xml, String path) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			return xpath.evaluate(path, new InputSource(new StringReader(xml)));
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("error evaluating xpath");
		}
		return null;
	}
	
	public String deploy(String project, String dynamicVDB,TeiidServerRequirement teiidServer) {
		new ModelExplorer().deployVdb(project, dynamicVDB);
		new ServersViewExt().refreshServer(teiidServer.getName());
		return new ServersViewExt().getVdbStatus(teiidServer.getName(), dynamicVDB);
		
	}
	
}
