package sample.magics.com.myapplication2;

public class CommonUtils {

    public static final String MAIN_URL = "http://api.suninfotechnologies.in/";
    public static final String LOGIN = MAIN_URL + "task/CustomerLogin?";
    public static final String REGISTER = MAIN_URL + "task/UserRegistration?";
    public static final String SERVICE_TYPE = MAIN_URL + "task/ServiceType?partyid=0";
    public static final String STATES = MAIN_URL + "GetStates";
    public static final String ADD_TASK = MAIN_URL + "task/AddTask?";

    public static boolean checkValidation(String input) {
        if(input.contains("@")) {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
        } else {
            if (android.util.Patterns.PHONE.matcher(input).matches()){
                return false;
            } else {
                return false;
            }
            //return android.util.Patterns.PHONE.matcher(input).matches();
        }
    }
}
