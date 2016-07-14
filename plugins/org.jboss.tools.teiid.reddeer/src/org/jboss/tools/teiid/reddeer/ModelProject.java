package org.jboss.tools.teiid.reddeer;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.eclipse.core.resources.Project;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.teiid.reddeer.wizard.imports.TeiidImportWizard;

/**
 * This class represents a model project.
 * 
 * @author apodhrad
 * 
 */
public class ModelProject {

	private Project project;

	public ModelProject(Project project) {
		this.project = project;
	}

	public void open(String... path) {
		project.getProjectItem(path).open();
	}

	public File getFile() {
		String wsPath = ResourcesPlugin.getWorkspace().getRoot().getLocationURI().getPath();
		return new File(wsPath, project.getName());
	}

}
