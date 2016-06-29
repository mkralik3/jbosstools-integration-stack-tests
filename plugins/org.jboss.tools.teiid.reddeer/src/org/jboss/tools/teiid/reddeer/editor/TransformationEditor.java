package org.jboss.tools.teiid.reddeer.editor;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.swt.impl.button.CheckBox;
import org.jboss.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.teiid.reddeer.dialog.CriteriaBuilderDialog;
import org.jboss.tools.teiid.reddeer.dialog.ExpressionBuilderDialog;
import org.jboss.tools.teiid.reddeer.dialog.ReconcilerDialog;

public class TransformationEditor {
	private static final Logger log = Logger.getLogger(TransformationEditor.class);
	
	public static final String SELECT = "SELECT";
	public static final String UPDATE = "UPDATE";
	public static final String INSERT = "INSERT";
	public static final String DELETE = "DELETE";
	
	public void insertAndValidateSql(String sql){
		insertAndValidateSql(SELECT,sql);
	}
	
	public void insertAndValidateSql(String operation,String sql){
		setOperation(operation);
		setTransformation(sql);
		saveAndValidateSql();
		AbstractWait.sleep(TimePeriod.SHORT);
		new WorkbenchShell();
	}
	
	/**
	 * @param operation use TransformationEditor.<operation>
	 */
	public void setOperation(String operation){
		try{
			new DefaultCTabItem(operation).activate();
		}catch(Exception ex){
			new DefaultCTabItem(operation + " (default)").activate();
		}
		new CheckBox("Use Default").click();
		
	}
	
	public void setTransformation(String sql){
		new DefaultStyledText(0).setText(sql);
	}
	
	public void saveAndValidateSql(){
		new DefaultToolItem("Save/Validate SQL").click();
	}
	
	public void close() {
		new DefaultToolItem("Close").click();
		AbstractWait.sleep(TimePeriod.SHORT);
	}
	
	public CriteriaBuilderDialog openCriteriaBuilder() {
		log.info("Opening Criteria Builder Dialog");
		new DefaultToolItem("Criteria Builder").click();
		return new CriteriaBuilderDialog();
	}
	
	public ExpressionBuilderDialog openExpressionBuilder(){
		log.info("Opening Expression Builder Dialog");
		new DefaultToolItem("Expression Builder").click();
		return new ExpressionBuilderDialog();
	}
	
	public ReconcilerDialog openReconciler() {
		log.info("Opening Reconciler Dialog");
		new DefaultToolItem("Reconcile Transformation SQL with Target Columns").click();
		return new ReconcilerDialog();
	}

}
