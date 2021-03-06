package org.jboss.tools.bpmn2.reddeer.editor.graphiti;

import org.eclipse.gef.EditPart;
import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.core.lookup.ShellLookup;
import org.eclipse.reddeer.common.util.Display;
import org.eclipse.reddeer.graphiti.impl.graphitieditpart.AbstractGraphitiEditPart;
import org.eclipse.reddeer.swt.api.Shell;
import org.eclipse.reddeer.swt.exception.SWTLayerException;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.tab.DefaultTabItem;
import org.jboss.tools.bpmn2.reddeer.properties.setup.SetUpAble;

/**
 * Class for setting element properties
 * 
 * @author jomarko
 *
 */
public class PropertiesGraphitiEditPart extends AbstractGraphitiEditPart {

	public PropertiesGraphitiEditPart(EditPart editPart) {
		super(editPart);
	}

	public void setUpTabs(SetUpAble... tabs) {

		getContextButton("Show Properties").click();
		Shell shell = new DefaultShell();
		final String shellLabel = shell.getText();
		shell.setFocus();
		try {
			for (SetUpAble tab : tabs) {

				if (tab != null) {
					try {
						new DefaultTabItem(tab.getTabLabel()).activate();
					} catch (SWTLayerException e) {
						// General tab is showed only if has element more tabs in properties view
					}
					tab.setUpCTab();
				}
			}
		} catch (SWTLayerException | CoreLayerException e) {
			// Maybe some other popups are still opened
			Display.syncExec(new Runnable() {

				@Override
				public void run() {
					org.eclipse.swt.widgets.Shell[] shells = ShellLookup.getInstance().getShells();
					if (shells != null) {
						for (int i = shells.length - 1; i > 1; i--) {
							shells[i].close();
						}
					}
				}
			});
		} finally {
			new DefaultShell(shellLabel);
			new PushButton("OK").click();
		}
	}
}
