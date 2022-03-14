package client;

import connection.TCPConnection;
import connection.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Client extends JFrame implements TCPConnectionListener {

    private final String ip_address = "localhost";
    private final Integer port = 8080;

    private final JTextField username = new JTextField();
    private final JTextArea log = new JTextArea();
    private final JTextField inputField = new JTextField();
    private TCPConnection tcpConnection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }

    private Client() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        log.setEditable(false);
        setVisible(true);
        log.setLineWrap(true);

        inputField.addActionListener(actionEvent -> {
            String msg = inputField.getText();
            if (msg.equals("")) {
                return;
            }
            inputField.setText(null);
            tcpConnection.sendMessage(username.getText() + " : " + msg);
        });

        add(username, BorderLayout.NORTH);
        add(log, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        try {
            tcpConnection = new TCPConnection(this, ip_address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void printMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            log.append(msg + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String message) {
        printMessage(message);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection exception : " + e);
    }
}
