package com.example.mod_2_homework_7.server;

import java.io.Closeable;

public interface AuthService extends Closeable {

    String getNickByLoginAndPassword(String login, String password);

}
