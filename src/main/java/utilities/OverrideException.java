package utilities;

public class OverrideException extends Exception{

    @Override
    public String getMessage() {
        return "Trying to override a value which has already been set.";
    }
}
