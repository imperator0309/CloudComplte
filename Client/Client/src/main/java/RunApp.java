import GUI.ClientController;
import GUI.ClientGUI;

public class RunApp {
    public static void main(String[] args) {
        ClientGUI clientGUI = new ClientGUI();
        new ClientController(clientGUI);
    }
}
