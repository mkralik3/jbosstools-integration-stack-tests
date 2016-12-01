package org.jboss.tools.teiid.ui.bot.test.suite;

import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.teiid.ui.bot.test.ddl.DynamicVdbTestDeprecated;
import org.jboss.tools.teiid.ui.bot.test.ddl.ImportDDLtestDeprecated;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@SuiteClasses({
	ImportDDLtestDeprecated.class,
	DynamicVdbTestDeprecated.class})
@RunWith(RedDeerSuite.class)
public class DDLTests {}
