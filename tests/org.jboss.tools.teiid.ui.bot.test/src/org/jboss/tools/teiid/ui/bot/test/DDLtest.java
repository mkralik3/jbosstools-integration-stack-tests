package org.jboss.tools.teiid.ui.bot.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.jboss.reddeer.core.condition.ShellWithTextIsActive;
import org.jboss.reddeer.junit.execution.annotation.RunIf;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.requirements.server.ServerReqState;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.tools.teiid.reddeer.connection.TeiidJDBCHelper;
import org.jboss.tools.teiid.reddeer.editor.ModelEditor;
import org.jboss.tools.teiid.reddeer.editor.TableEditor;
import org.jboss.tools.teiid.reddeer.editor.TransformationEditor;
import org.jboss.tools.teiid.reddeer.manager.ImportManager;
import org.jboss.tools.common.reddeer.JiraClient;
import org.jboss.tools.common.reddeer.condition.IssueIsClosed;
import org.jboss.tools.common.reddeer.condition.IssueIsClosed.Jira;
import org.jboss.tools.teiid.reddeer.connection.ConnectionProfileConstants;
import org.jboss.tools.teiid.reddeer.connection.ResourceFileHelper;
import org.jboss.tools.teiid.reddeer.perspective.TeiidPerspective;
import org.jboss.tools.teiid.reddeer.requirement.TeiidServerRequirement;
import org.jboss.tools.teiid.reddeer.requirement.TeiidServerRequirement.TeiidServer;
import org.jboss.tools.teiid.reddeer.view.ModelExplorer;
import org.jboss.tools.teiid.reddeer.wizard.DDLTeiidExportWizard;
import org.jboss.tools.teiid.reddeer.wizard.DDLTeiidImportWizard;
import org.jboss.tools.teiid.reddeer.wizard.ImportGeneralItemWizard;
import org.jboss.tools.teiid.reddeer.wizard.VdbWizard;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author mkralik
 */

@RunWith(RedDeerSuite.class)
@OpenPerspective(TeiidPerspective.class)
@TeiidServer(state = ServerReqState.RUNNING, connectionProfiles={
		ConnectionProfileConstants.ORACLE_11G_PARTS_SUPPLIER,
})

public class DDLtest {
	@InjectRequirement
	private static TeiidServerRequirement teiidServer;

	
	private static TeiidBot teiidBot = new TeiidBot();
	private ResourceFileHelper fileHelper = new ResourceFileHelper();

	
	private static final String PROJECT_NAME = "DDLimport";
	private static final String NAME_SOURCE_MODEL = "Products";
	private static final String NAME_VDB = "ddlVDB";
	private static final String NAME_EXPORTED_DDL = "exportedDDL.ddl";
	private static final String NAME_ORIGINAL_FOLDER = "originalModels";
	private static final String PATH_TO_LIB = "resources/ddl/";
	
	@Before
	public void importProject() {
		new ModelExplorer().importProject(PROJECT_NAME);
		new ModelExplorer().changeConnectionProfile(ConnectionProfileConstants.ORACLE_11G_PARTS_SUPPLIER, PROJECT_NAME, NAME_SOURCE_MODEL);
	}	
	
	@After
	public void clearProject(){
		new ModelExplorer().deleteAllProjectsSafely();
	}
	
	@Ignore
	@Test
	public void importSourceAndView(){
		
	}
	@Ignore
	@Test
	public void exportSourceAndView(){
		
	}
	
	@Test
	public void importGlobalTable(){
		String nameViewModel = "globalViewModel";
		
		importDDL(nameViewModel,PATH_TO_LIB +"globalViewModel.ddl");
		
		//check if table was set to temp
		new ModelExplorer().openModelEditor(PROJECT_NAME,nameViewModel + ".xmi");
		TableEditor tableEditor = new TableEditor(nameViewModel + ".xmi", "Package Diagram");
		tableEditor.openTab("Base Tables");
		assertTrue(tableEditor.getCellText(0, "relational:Global Temp Table").equals("true"));

		//set supports update
		if(tableEditor.getCellText(0, "Supports Update").equals("false")){
			tableEditor.setCellCCombo(0, "Supports Update", "true");
			if (new ShellWithTextIsActive("Table 'Supports Update' Property Changed").test()){
				new PushButton("Yes").click();
			}
			teiidBot.saveAll();
		}
		
		createVDB(nameViewModel);
		
		TeiidJDBCHelper jdbchelper = new TeiidJDBCHelper(teiidServer, NAME_VDB);
		try {
			List<ResultSet> resultSets = jdbchelper.executeMultiQuery(
					"select * from \"globalViewModel\".\"tempTable\";",
					"INSERT INTO \"globalViewModel\".\"tempTable\" VALUES ('testID1',10,true);",
					"INSERT INTO \"globalViewModel\".\"tempTable\" VALUES ('testID2','10',false);",
					"select * from \"globalViewModel\".\"tempTable\";"
					);
			//first select is empty
			assertFalse(resultSets.get(0).isBeforeFirst()); 
			//test global temp table
			ResultSet rs = resultSets.get(1);
 			rs.next();
 			assertEquals("testID1", rs.getString(1));
 			assertEquals("true", rs.getString(3));
 			rs.next();
 			assertEquals("testID2", rs.getString(1));
 			assertEquals("false", rs.getString(3));
 		} catch (SQLException e) {
 			fail(e.getMessage());
 		}
		jdbchelper.closeConnection();
	}
	
	@Test
	public void exportGlobalTable(){
		String viewOriginalModel = "globalViewModel";

		exportDDL(viewOriginalModel);
		
		assertTrue(checkIfModelsAreSame(PATH_TO_LIB + "globalViewModel.ddl", NAME_EXPORTED_DDL));
	}

	@Test
	public void importRestProcedure(){
		String nameViewModel = "procedureRestViewModel";
		
		importDDL(nameViewModel, PATH_TO_LIB + "procedureRestViewModel.ddl");

		if(!new JiraClient().isIssueClosed("TEIIDDES-2859")){
			new ModelExplorer().openModelEditor(PROJECT_NAME,nameViewModel+".xmi","ProductsInfo");
			TransformationEditor tEditor = new ModelEditor(nameViewModel+".xmi").openTransformationEditor();
			tEditor.insertAndValidateSql(TransformationEditor.INSERT,fileHelper.getSql("DDLtest/insertREST"));
			tEditor.insertAndValidateSql(TransformationEditor.DELETE,fileHelper.getSql("DDLtest/deleteREST"));
			teiidBot.saveAll();

			new ModelExplorer().openModelEditor(PROJECT_NAME,nameViewModel+".xmi","addProduct");
			tEditor = new ModelEditor(nameViewModel+".xmi").openTransformationEditor();
			tEditor.saveAndValidateSql();
			teiidBot.saveAll();
		}
		
		//check if rest method was set
		new ModelExplorer().openModelEditor(PROJECT_NAME,nameViewModel + ".xmi");
		TableEditor tableEditor = new TableEditor(nameViewModel + ".xmi", "Package Diagram");
		tableEditor.openTab("Procedures");
		assertTrue(tableEditor.getCellText(0, "REST:Rest Method").equals("GET"));
		assertTrue(tableEditor.getCellText(0, "REST:URI").equals("product/{instr_id}"));
		assertTrue(tableEditor.getCellText(1, "REST:Rest Method").equals("POST"));
		assertTrue(tableEditor.getCellText(1, "REST:URI").equals("product/"));

		createVDB(nameViewModel);
		
		TeiidJDBCHelper jdbchelper = new TeiidJDBCHelper(teiidServer, NAME_VDB);
		try {
			List<ResultSet> resultSets = jdbchelper.executeMultiQuery(
					"select * from ( exec \"procedureRestViewModel\".\"getProduct\"('PRD01095') ) AS X_X;",
					"select * from ( exec \"procedureRestViewModel\".\"addProduct\"('TEIID','TEIID','TEIID','false','false','false','false') ) AS X_X;"
					);
			ResultSet rs = resultSets.get(0);
			rs.next();
			assertTrue(rs.getString(1).contains("<Products><Product><INSTR_ID>PRD01095</INSTR_ID><SYMBOL_TYPE>1</SYMBOL_TYPE><SYMBOL>INTC</SYMBOL><NAME>Intel Corporation3</NAME>"));
			rs = resultSets.get(1);
			rs.next();
			assertEquals("<response>Operation&lt;undefined>Successful!</response>", rs.getString(1));
		} catch (SQLException e) {
 			fail(e.getMessage());
 		}finally{
 			try{
 				jdbchelper.executeQueryNoResultSet("DELETE FROM ProductsInfo WHERE INSTR_ID LIKE 'TEIID'");
 			} catch (SQLException e) {
 	 			fail(e.getMessage() + "\n delete data manually! ");
 	 		}
 		}
		jdbchelper.closeConnection();
	}

	@Test
	public void exportRestProcedure(){
		/*TODO add update and insert trigger to the ddl*/
		String viewOriginalModel = "procedureRestViewModel";

		exportDDL(viewOriginalModel);
		
		assertTrue(checkIfModelsAreSame(PATH_TO_LIB + "procedureRestViewModel.ddl", NAME_EXPORTED_DDL));
	}
	
	@Test
	public void importProcedureReturns(){
		String nameViewModel = "procedureReturnsViewModel";
		
		importDDL(nameViewModel, PATH_TO_LIB + "procedureReturnsViewModel.ddl");

		createVDB(nameViewModel);

		TeiidJDBCHelper jdbchelper = new TeiidJDBCHelper(teiidServer, NAME_VDB);
		try {
			List<ResultSet> resultSets = jdbchelper.executeMultiQuery(
					"select * from ( exec \"procedureReturnsViewModel\".\"getProduct\"('PRD01095') ) AS X_X ;",
					"select * from ( exec \"procedureReturnsViewModel\".\"selfProc\"('hello') ) AS X_X ;"
					);
			ResultSet rs = resultSets.get(0);
			rs.next();
			assertEquals("Intel Corporation3", rs.getString(1));
			rs = resultSets.get(1);
			rs.next();
			assertEquals("hello", rs.getString(1));
 		} catch (SQLException e) {
 			fail(e.getMessage());
 		}
		jdbchelper.closeConnection();
	}
	
	@Test
	public void exportProcedureReturns(){
		String viewOriginalModel = "procedureReturnsViewModel";

		exportDDL(viewOriginalModel);
		
		assertTrue(checkIfModelsAreSame(PATH_TO_LIB + "procedureReturnsViewModel.ddl", NAME_EXPORTED_DDL));
	}
	
	@Test
	public void importUDF(){
		String nameViewModel = "udfViewModel";
		String UDF_LIB_PATH = "target/proc-udf/MyTestUdf/lib/"; 
		String UDF = "MyTestUdf-1.0-SNAPSHOT.jar";
		
		importDDL(nameViewModel, PATH_TO_LIB + "udfViewModel.ddl");
		
		//set UDF jar path
		Properties props = new Properties();
		props.setProperty("dirName", UDF_LIB_PATH);
		props.setProperty("intoFolder", PROJECT_NAME);
		props.setProperty("file", UDF);
		props.setProperty("createTopLevel", "true");
		new ImportManager().importGeneralItem(ImportGeneralItemWizard.Type.FILE_SYSTEM, props);
		new ModelExplorer().openModelEditor(PROJECT_NAME,nameViewModel + ".xmi");
		setUDFpath(nameViewModel,PROJECT_NAME,"lib",UDF);
		
		new ModelExplorer().openModelEditor(PROJECT_NAME,nameViewModel + ".xmi");
		TableEditor tableEditor = new TableEditor(nameViewModel + ".xmi", "Package Diagram");
		tableEditor.openTab("Procedures");
		assertTrue(tableEditor.getCellText(0, "relational:Function Category").equals("TEST_FUNCTIONS"));
		assertTrue(tableEditor.getCellText(0, "relational:Java Class").equals("userdefinedfunctions.ConcatNull"));
		assertTrue(tableEditor.getCellText(0, "relational:Java Method").equals("concatNull"));
		assertTrue(tableEditor.getCellText(0, "relational:UDF Jar Path").equals("lib/"+UDF));
		
		createVDB(nameViewModel);

		TeiidJDBCHelper jdbchelper = new TeiidJDBCHelper(teiidServer, NAME_VDB);
		try {
			ResultSet rs = jdbchelper.executeQueryWithResultSet("SELECT * FROM UdfView WHERE namebusiness LIKE 'General%'");
			rs.next();
			assertEquals("General Electric Company: ", rs.getString(1));
			rs.next();
			assertEquals("General Motors Corporation: ", rs.getString(1));
			rs.next();
			assertEquals("General Motors Corporation: automobiles", rs.getString(1));
			
 		} catch (SQLException e) {
 			fail(e.getMessage());
 		}
		jdbchelper.closeConnection();
	}
	
	@Test
	public void exportUDF(){
		String viewOriginalModel = "udfViewModel";
		String UDF_LIB_PATH = "target/proc-udf/MyTestUdf/lib/"; 
		String UDF = "MyTestUdf-1.0-SNAPSHOT.jar";
		
		Properties props = new Properties();
		props.setProperty("dirName", UDF_LIB_PATH);
		props.setProperty("intoFolder", PROJECT_NAME);
		props.setProperty("file", UDF);
		props.setProperty("createTopLevel", "true");
		new ImportManager().importGeneralItem(ImportGeneralItemWizard.Type.FILE_SYSTEM, props);
		new ModelExplorer().openModelEditor(PROJECT_NAME,"originalModels",viewOriginalModel + ".xmi");
		setUDFpath(viewOriginalModel,PROJECT_NAME,"lib",UDF);
		
		exportDDL(viewOriginalModel);
		
		assertTrue(checkIfModelsAreSame(PATH_TO_LIB + "udfViewModel.ddl", NAME_EXPORTED_DDL));
	}
	
	@Ignore
	@Test
	public void importUniqueConstraint(){
		
	}
	
	@Ignore
	@Test
	public void exportUniqueConstraint(){
		
	}
	
	@Test
	public void importAccessPattern(){
		String nameViewModel = "accessPatternViewModel";
		
		importDDL(nameViewModel, PATH_TO_LIB + "accessPatternViewModel.ddl");
		
		new ModelExplorer().openModelEditor(PROJECT_NAME,nameViewModel + ".xmi");
		TableEditor tableEditor = new TableEditor(nameViewModel + ".xmi", "Package Diagram");
		tableEditor.openTab("Access Patterns");
		assertTrue(tableEditor.getCellText(0, "Columns").equals("NAME : string(60)"));
		
		createVDB(nameViewModel);

		TeiidJDBCHelper jdbchelper = new TeiidJDBCHelper(teiidServer, NAME_VDB);
		try {
			ResultSet rs = jdbchelper.executeQueryWithResultSet("select * from accessPatternViewModel.productData where accessPatternViewModel.productData.NAME = 'SAP AG'");
			rs.next();
			assertEquals("PRD01034", rs.getString(1));
			assertEquals("SAP AG", rs.getString(2));
 		} catch (SQLException e) {
 			fail(e.getMessage());
 		}
		jdbchelper.closeConnection();
	}
	
	@Ignore //TODO jira close run if
	@Test
	@Jira("TEIIDDES-2788")
	@RunIf(conditionClass = IssueIsClosed.class)
	public void exportAccessPattern(){
		String viewOriginalModel = "accessPatternViewModel";

		exportDDL(viewOriginalModel);
		
		assertTrue(checkIfModelsAreSame(PATH_TO_LIB + "accessPatternViewModel.ddl", NAME_EXPORTED_DDL));
	}
	
	@Ignore
	@Test
	public void importMaterialized(){
		
	}
	
	@Ignore
	@Test
	public void exportMaterialized(){
		
	}
	
	private void createVDB(String nameViewModel){
		VdbWizard wizardVDB = new VdbWizard();
		wizardVDB.open();
		wizardVDB.setLocation(PROJECT_NAME)
				 .setName(NAME_VDB)
				 .addModel(PROJECT_NAME,nameViewModel + ".xmi")
				 .finish();
		new ModelExplorer().deployVdb(PROJECT_NAME, NAME_VDB);
	}
	
	private void importDDL(String nameMoel, String pathToDDL){
		DDLTeiidImportWizard importWizard = new DDLTeiidImportWizard();
		importWizard.open();
		importWizard.setPath(pathToDDL)
					.setFolder(PROJECT_NAME)
					.setName(nameMoel)
					.setModelType(DDLTeiidImportWizard.View_Type)
					.generateValidDefaultSQL(true)
					.next();
		importWizard.finish();
	}
	
	private void exportDDL(String originalModelName){
		DDLTeiidExportWizard exportDDL = new DDLTeiidExportWizard();		
		exportDDL.open();
		exportDDL.setLocation(PROJECT_NAME,NAME_ORIGINAL_FOLDER,originalModelName + ".xmi")
	     		 .next();
		exportDDL.exportToWorkspace(NAME_EXPORTED_DDL, PROJECT_NAME)
			     .finish();
	}
	
	private boolean checkIfModelsAreSame(String pathToOriginalModel, String nameExportedModel){
		new ModelExplorer().getModelProject(PROJECT_NAME).open();
		new DefaultTreeItem(PROJECT_NAME,nameExportedModel).doubleClick();
		
		String generetedText = new DefaultStyledText().getText();
		generetedText=generetedText.replaceAll("\r|\n|\t", "");
		
		String expectedText = null;
		try {
			expectedText = convertFileToString(new File(pathToOriginalModel));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		expectedText=expectedText.replaceAll("\r|\n|\t", "");
		return expectedText.equals(generetedText); 
	}
	
	private String convertFileToString(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append('\n');
		}
		reader.close();
		return stringBuilder.toString();
	}
	
	private void setUDFpath(String viewModel, String... pathToUDF){
		TableEditor tableEditor = new TableEditor(viewModel + ".xmi", "Package Diagram");
		tableEditor.openTab("Procedures");
		tableEditor.clickOnButton(0, "relational:UDF Jar Path");
		
		new DefaultShell("Select UDF jar");
		new PushButton("OK").click();
		
		new DefaultShell("Choose UDF jar");
		new DefaultTreeItem(pathToUDF).select();
		new PushButton("OK").click();
		teiidBot.saveAll();
	}
}
