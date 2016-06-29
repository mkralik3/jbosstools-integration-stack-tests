package org.jboss.tools.teiid.ui.bot.test.suite;

import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.teiid.ui.bot.test.DynamicVdbTest;
import org.jboss.tools.teiid.ui.bot.test.DDLtest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@SuiteClasses({
	DDLtest.class,
	DynamicVdbTest.class})
@RunWith(RedDeerSuite.class)
public class DDLTests {

}
