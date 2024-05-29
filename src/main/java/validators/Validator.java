package validators;

public class Validator {
    public static boolean isDataValid(String w1,String w2,String w3){
        return (w1!=null) && (w2!=null) && (w3!=null) && (!w1.isEmpty()) &&(!w2.isEmpty()) &&(!w3.isEmpty());
    }
}
