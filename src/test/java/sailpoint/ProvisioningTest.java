package sailpoint;

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import sailpoint.object.*;
import sailpoint.rdk.utils.RuleXmlUtils;
import sailpoint.tools.GeneralException;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProvisioningTest {
    Logger log = LogManager.getLogger(ProvisioningTest.class);

    private static final String RULE_FILENAME = "src/main/resources/rules/Rule - BeforeProvisioning - BSC-ServiceNow.xml";

    @Test
    public void testProvisioning () throws GeneralException, EvalError {
        Interpreter i = new Interpreter();

        ProvisioningPlan plan = mock(ProvisioningPlan.class);

        AccountRequest accountRequest = mock(AccountRequest.class);
        when(accountRequest.getOp()).thenReturn(ProvisioningPlan.ObjectOperation.Modify);
        // add attribute request for company.u_beeline_supplier_code
        AttributeRequest attributeRequest = mock(AttributeRequest.class);
        when(attributeRequest.getName()).thenReturn("company.u_beeline_supplier_code");
        when(attributeRequest.getValue()).thenReturn("1234567890");
        when(accountRequest.getAttributeRequests()).thenReturn(List.of(attributeRequest));
        //mock up accountRequest.getApplicationName()
        when(accountRequest.getApplicationName()).thenReturn("ServiceNow");
        //mock up AccountRequest.Operation op = accountRequest.getOperation();
        when(accountRequest.getOperation()).thenReturn(AccountRequest.Operation.Modify);

        List<AccountRequest> accountRequests = new ArrayList<>();
        accountRequests.add(accountRequest);


        when(plan.getAccountRequests()).thenReturn(accountRequests);

        //mock up this call
        //Identity identity = plan.getIdentity();
        //String cloudLifecycleState = identity.getAttribute("cloudLifecycleState");

        Identity identity = mock(Identity.class);
        when(identity.getAttribute("cloudLifecycleState")).thenReturn("Active");
        when(plan.getIdentity()).thenReturn(identity);

        String result = "";

        i.set("log", log);
        i.set("plan", plan);

        String source = RuleXmlUtils.readRuleSourceFromFilePath(RULE_FILENAME);
        result = (String) i.eval(source);

        log.info("Beanshell script returned: " + result);
    }
}
