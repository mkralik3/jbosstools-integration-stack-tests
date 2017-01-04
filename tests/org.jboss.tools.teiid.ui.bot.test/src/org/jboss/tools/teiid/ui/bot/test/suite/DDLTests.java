package org.jboss.tools.teiid.ui.bot.test.suite;

import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.teiid.ui.bot.test.ddl.DynamicVdbTestDeprecated;
import org.jboss.tools.teiid.ui.bot.test.ddl.ImportDDLtestDeprecated;
import org.jboss.tools.teiid.ui.bot.test.ddl.SourceAccessPattern;
import org.jboss.tools.teiid.ui.bot.test.ddl.SourceColumns;
import org.jboss.tools.teiid.ui.bot.test.ddl.SourcePrimaryKey;
import org.jboss.tools.teiid.ui.bot.test.ddl.SourceTableSettings;
import org.jboss.tools.teiid.ui.bot.test.ddl.SourceUniqueConstraint;
import org.jboss.tools.teiid.ui.bot.test.ddl.ViewAccessPattern;
import org.jboss.tools.teiid.ui.bot.test.ddl.ViewColumns;
import org.jboss.tools.teiid.ui.bot.test.ddl.ViewPrimaryKey;
import org.jboss.tools.teiid.ui.bot.test.ddl.ViewProcedureSettings;
import org.jboss.tools.teiid.ui.bot.test.ddl.ViewTableSettings;
import org.jboss.tools.teiid.ui.bot.test.ddl.ViewUniqueConstraint;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@SuiteClasses({
//	ImportDDLtestDeprecated.class,
//	DynamicVdbTestDeprecated.class,
	SourceAccessPattern.class,
	SourceColumns.class,
	SourcePrimaryKey.class,
	SourceTableSettings.class,
//	SourceUniqueConstraint.class,
	ViewAccessPattern.class,
	ViewColumns.class,
	ViewPrimaryKey.class,
	ViewProcedureSettings.class,
	ViewTableSettings.class/*,
	ViewUniqueConstraint.class*/
})
@RunWith(RedDeerSuite.class)
public class DDLTests {}
