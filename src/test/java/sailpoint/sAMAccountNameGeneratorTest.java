package sailpoint;

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import sailpoint.object.Application;
import sailpoint.object.Identity;
import sailpoint.rdk.utils.RuleXmlUtils;
import sailpoint.server.IdnRuleUtil;
import sailpoint.tools.GeneralException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class sAMAccountNameGeneratorTest {
    Logger log = LogManager.getLogger(sAMAccountNameGeneratorTest.class);

    private static final String RULE_FILENAME = "src/main/resources/rules/Rule - AttributeGenerator - sAMAccountNameGenerator.xml";

    @Test
    public void testUsernameGeneratorWhereFirstAndLastNameValid () throws GeneralException, EvalError {
        Interpreter i = new Interpreter();

        IdnRuleUtil idn = mock();
        when(idn.accountExistsByDisplayName(any(), any())).thenReturn(false);

        Application application = mock(Application.class);
        when(application.getName()).thenReturn("Active Directory [source]");

        Identity identity = mock(Identity.class);
        when(identity.getStringAttribute("firstname")).thenReturn("John");
        when(identity.getStringAttribute("lastname")).thenReturn("Smith");
        when(identity.getStringAttribute("employeeType")).thenReturn("CORPSTAFF");
        String result = "";

        i.set("log", log);
        i.set("idn", idn);
        i.set("application", application);
        i.set("identity", identity);

        String source = RuleXmlUtils.readRuleSourceFromFilePath(RULE_FILENAME);
        result = (String) i.eval(source);

        assertNotNull(result);
        assertEquals("JSmith", result);

        log.info("Beanshell script returned: " + result);

    }
}