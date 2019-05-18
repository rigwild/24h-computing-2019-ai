package org.kaleeis_bears.ia_24h_2019.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.kaleeis_bears.ia_24h_2019.BaseIA;
import org.kaleeis_bears.ia_24h_2019.Controller;
import org.kaleeis_bears.ia_24h_2019.Main;
import org.kaleeis_bears.ia_24h_2019.protocol.Protocol;

public class NetworkController implements Controller {
  private final Protocol protocol;
  private final String host;
  private final int port;

  public NetworkController(String host, int port, Protocol protocol) {
    this.protocol = protocol;
    this.host = host;
    this.port = port;
  }

  @Override
  public void run(BaseIA ai) {
    try (final Socket socket = new Socket(this.host, this.port)) {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      final PrintWriter writer = new PrintWriter(socket.getOutputStream());
      
      String message;
      while ((message = reader.readLine()) != null)
        this.protocol.handle(writer, message);
      
    } catch (IOException e) {
      Main.exception(e.getClass().getName(), "Déconnecté du serveur", e);
    }
  }
}