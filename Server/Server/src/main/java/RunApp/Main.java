package RunApp;

import mechanic.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.open();
        server.start();
    }
}
