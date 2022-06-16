package com.example.mod_2_homework_7.client;

import java.io.*;
import java.net.Socket;

public class ChatClient {

    private Socket socket;
    BufferedReader in=null;
    PrintWriter out=null;

    private final ChatController controller;

    public ChatClient(ChatController controller) {
        this.controller = controller;

    }

    public void openConnection() throws IOException {
        socket = new Socket("localhost", 8189);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());

        new Thread(() -> {

            try {
                waitAuth();
                readMessages();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }).start();


    }

    private void waitAuth() throws IOException {
        while (true) {
            final String message = in.readLine();
            if (message.startsWith("/authok")) {
                final String[] split = message.split("\\p{Blank}+");
                final String nick = split[1];
                controller.setAuth(true);
                controller.addMessage("Успешная авторизация под ником " + nick);
                break;

            }
        }

    }

    private void closeConnection() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (out != null) {
            out.close();
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readMessages() throws IOException {
        while (true) {
            final String message = in.readLine();
            if ("/end".equals(message)) {
                controller.setAuth(false);
                break;
            }
            controller.addMessage(message);
        }
    }

    public void sendMessage(String message) {
        out.write(message);
    }
}
