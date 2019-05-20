package org.kaleeis_bears.ai.controllers;

import org.kaleeis_bears.ai.logging.Logger;
import org.kaleeis_bears.ai.protocol.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkController implements Controller {
  private final Logger logger;
  private final Protocol protocol;
  private final String host;
  private final int port;

  public NetworkController(Logger logger, String host, int port, Protocol protocol) {
    this.logger = logger;
    this.protocol = protocol;
    this.host = host;
    this.port = port;
  }

  @Override
  public void run() {
    try (final Socket socket = new Socket(this.host, this.port)) {
      System.out.println("CONNECTÉ AU SERVEUR. DEBUT DE LA PARTIE.");

      final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      final PrintWriter writer = new PrintWriter(socket.getOutputStream());
      
      String message;
      while ((message = reader.readLine()) != null) {
        message = this.protocol.handle(message);
        if (message != null)
          writer.println(message);
      }

      System.out.println("PARTIE TERMINÉE.");
    } catch (UnknownHostException | NoRouteToHostException e) {
      this.logger.exception("RESEAU", "Hôte introuvable", e);
    } catch (IOException e) {
      this.logger.exception("RESEAU", "Déconnecté du serveur", e);
    }
  }
}