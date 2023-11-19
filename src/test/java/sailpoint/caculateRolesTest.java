package sailpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import sailpoint.server.IdnRuleUtil;

import bsh.EvalError;
import bsh.Interpreter;
import sailpoint.rdk.utils.RuleXmlUtils;
import sailpoint.rule.Account;
import sailpoint.tools.GeneralException;

public class caculateRolesTest {
    Logger log = LogManager.getLogger(ManagerCorrelationTest.class);

    private static final String RULE_FILENAME = "src/main/resources/rules/Rule - BuildMap - caculateRoles.xml";

    @Test
    public void testThatNewColumnIsAdded () throws GeneralException, EvalError {
        Interpreter i = new Interpreter();

        //mock up this following data
        //id,staffName,position,department,ward,startDate,endDate,roleName,staffNumber
        //1,Kate Smith,Resident Medical Officer,Emergency Services,ED,01/11/2023,05/11/2023,,E2023005
        //2,Kate Smith,Resident Medical Officer,Emergency Services,ED,16/11/2023,30/11/2023,,E2023005

        List<String> columns = Arrays.asList("department","ward","startDate","endDate","roleName");
        List<String> rows = Arrays.asList("Emergency Services","ED","01/11/2023","05/12/2023","");

        //mock up the attributes
        //Map<String,Object> attributes = mock(Map.class<String,Object>());
        Map<String,Object> attributes = mock(Map.class);
        when(attributes.get("roleName")).thenReturn("Role1");

        //mock up the account
        Account acct = mock(Account.class);
        when(acct.getAttributes()).thenReturn(attributes);

        //mock up the idn call
        IdnRuleUtil idn = mock(IdnRuleUtil.class);
        when(idn.getFirstAccount("Rostering CSV [source]","Emergency Services-ED")).thenReturn(acct);


        i.set("log", log);
        i.set("cols", columns);
        i.set("record", rows);
        i.set("idn", idn);

        String source = RuleXmlUtils.readRuleSourceFromFilePath(RULE_FILENAME);

        @SuppressWarnings("unchecked")
        Map<String, String> result = (Map<String, String>) i.eval(source);

        log.info(result);

    }
}