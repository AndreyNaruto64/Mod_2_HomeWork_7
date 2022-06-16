package com.example.mod_2_homework_7.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private ChatServer server;
    BufferedReader in = null;
    PrintWriter out = null;
    private String nick;
    private AuthService authService;

    public ClientHandler(Socket socket, ChatServer server, AuthService authService) {


        try {

            this.socket = socket;
            this.server = server;
            this.authService = authService;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
            new Thread(() -> {
                try {
                    authenticate();
                    readMessages();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void authenticate() { // /auth login1 pass1
        while (true) {
            try {
                final String message = in.readLine();
                if (message.startsWith("/auth")) {
                    final String[] split = message.split("\\p{Blank}+");
                    final String login = split[1];
                    final String password = split[2];
                    final String nick = authService.getNickByLoginAndPassword(login, password);
                    if (nick != null) {
                        if (server.isNickBusy(nick)) {
                            sendMessage("Пользователь уже авторизован");
                            continue;
                        }
                        sendMessage("/authok " + nick);
                        this.nick = nick;
                        server.broadcast("Пользователь " + nick + "зашел в чат");
                        server.subscribe(this);
                        break;
                    } else {
                        sendMessage("Неверные логин и пароль");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void closeConnection() {
        sendMessage("/end");
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
            server.unsubscribe(this);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.write(message);
    }

    private void readMessages() {
        while (true) {
            try {
                final String message = in.readLine();
                if ("/end".equals(message)) {
                    break;
                }
                server.broadcast(nick + ": " + message);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String getNick() {
        return nick;
    }
}
