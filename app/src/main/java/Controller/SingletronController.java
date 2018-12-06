package Controller;

public class SingletronController {
    private static SingletronController INSTANCE = null;

    // other instance variables can be here

    private SingletronController() {};

    public static SingletronController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SingletronController();
        }
        return(INSTANCE);
    }

}
