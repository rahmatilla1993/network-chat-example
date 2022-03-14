package connection;

import java.io.*;
import java.net.Socket;

public class TCPConnection {

    private final TCPConnectionListener eventListener;
    private final Socket socket;
    private final BufferedWriter out;
    private Thread thread;

    public TCPConnection(TCPConnectionListener eventListener, String ip_address, Integer port) throws IOException {
        this(eventListener, new Socket(ip_address, port));
    }

    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.socket = socket;
        this.eventListener = eventListener;

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        eventListener.onConnectionReady(this);
        thread = new Thread(() -> {
            try {
                while (!thread.isInterrupted()) {
                    eventListener.onReceiveString(TCPConnection.this, in.readLine());
                }
            } catch (IOException e) {
                eventListener.onException(TCPConnection.this, e);
            } finally {
                eventListener.onDisconnect(TCPConnection.this);
            }
        });
        thread.start();
    }

    public synchronized void sendMessage(String msg) {
        try {
            out.write(msg + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + " : " + socket.getPort();
    }
}
