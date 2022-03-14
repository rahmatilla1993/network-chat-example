package server;

import connection.TCPConnection;
import connection.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements TCPConnectionListener {
    public static void main(String[] args) {
        new Server();
    }

    private final List<TCPConnection> connections=new ArrayList<>();

    private Server() {
        System.out.println("Server Running...");
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            while (true){
                Socket clientSocket = serverSocket.accept();
                new TCPConnection(this, clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("Client connected: "+tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String message) {
        sendToAllConnections(message);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    private void sendToAllConnections(String message){
        System.out.println(message);
        for (TCPConnection connection : connections) {
            connection.sendMessage(message);
        }
    }
}
