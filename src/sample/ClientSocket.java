package sample;

import java.net.Socket;

public class ClientSocket {
    private Socket s;

    public ClientSocket(Socket s) {
        this.s = s;
    }

    public Socket getS() {
        return s;
    }
}
