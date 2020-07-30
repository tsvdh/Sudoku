package core.misc;

public class OverrideException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Trying to override a value which has already been set.";
    }
}
