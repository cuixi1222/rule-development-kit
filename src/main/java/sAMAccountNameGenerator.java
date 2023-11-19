import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sailpoint.object.Application;
import sailpoint.object.Field;
import sailpoint.object.Identity;
import sailpoint.server.IdnRuleUtil;
import sailpoint.tools.GeneralException;

public class sAMAccountNameGenerator {
    Logger log = LogManager.getLogger(UsernameGenerator.class);
    Identity identity = new Identity();
    Application application = new Application();
    IdnRuleUtil idn;
    Field field = new Field();

    int maxIteration = 99;

     // function to generate sAMAccountName, check if it is unique and return the value
    public String generateUsername(String firstpart, String secondpart, int iteration) throws Exception{

        String username ;
        // construct username by adding firstpart and secondpart and iteration number
        if(iteration > 0){
            username = firstpart + secondpart + String.valueOf(iteration);
        }else{
            username =  firstpart + secondpart;
        }

        // check if username is unique, if yes return it, if not add iteration number to it
        if(isUnique(username)){
            return username;
        } else if(iteration < maxIteration){
            return generateUsername(firstpart, secondpart, (iteration + 1));
        }else{
            return null;
        }
    }

    public String sAMAccountNameGenerator() throws Exception{
         //max length of sAMAccountName is 20 characters
        String sAMAccountName = null;
    
        String firstName = StringUtils.trimToNull(identity.getStringAttribute("firstname"));
        String lastName = StringUtils.trimToNull(identity.getStringAttribute("lastname"));
        String employeeType = StringUtils.trimToNull(identity.getStringAttribute("employeeType"));
        String firstNameInitial = null;

        log.error("sAMAccountName Generation : " + firstName + " " + lastName + " " + employeeType);

        //make sure all firstname and lastname and employeeType are not null
        if (firstName !=null && lastName != null && employeeType != null) {
            if (employeeType.contains("CORP") || employeeType.contains("APTC")) {
                //it is a corp user [initial firstname][Lastname][uniqueNumber]
                firstNameInitial = firstName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase().substring(0,1);
                sAMAccountName = generateUsername(firstNameInitial, lastName, 0);
            }else{
                // it is a regional user [first 7 letter of surname][initial firstname][uniqueNumber]
                lastName = lastName.replaceAll("[^a-zA-Z0-9]", "");
                if (lastName.length() > 7) {
                lastName = lastName.substring(0, 7);
                }
                firstNameInitial = firstName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase().substring(0,1);
                sAMAccountName = generateUsername(lastName, firstNameInitial, 0);
            }
            }
        return sAMAccountName;
    }

    public boolean isUnique(String username) throws GeneralException {
        return !idn.accountExistsByDisplayName(application.getName(), username);
    }
}
