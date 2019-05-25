package org.kaleeis_bears.ai.le_havre_2019;

import org.kaleeis_bears.ai.logging.Logger;

import java.io.IOException;
import java.net.*;

public class LeHavreProtocol {
  private static final int MAX_BUFFER_SIZE = 300;

  private final InetAddress remoteAdress;
  private final DatagramSocket socket;
  private final Logger logger;
  private final String teamName;
  private final int port;
  private final byte[] buffer = new byte[MAX_BUFFER_SIZE];

  private World world = null;

  public LeHavreProtocol(final Logger logger, final String teamName, final String host, final int port) throws UnknownHostException, SocketException {
    this.logger = logger;
    this.teamName = teamName;
    this.port = port;
    this.socket = new DatagramSocket();
    this.remoteAdress = InetAddress.getByName(host);
    this.socket.connect(this.remoteAdress, port);
  }

  private String receive() throws IOException {
    DatagramPacket packet;
    do {
      packet = new DatagramPacket(this.buffer, MAX_BUFFER_SIZE);
      socket.receive(packet);
    } while (!packet.getAddress().equals(this.remoteAdress));
    final String message = new String(packet.getData(), 0, packet.getLength());
    this.logger.debug("RECV : " + message);
    return message;
  }

  private void send(String message) throws IOException {
    this.logger.debug("SEND : " + message);
    final byte[] data = message.getBytes();
    socket.send(new DatagramPacket(data, data.length, this.remoteAdress, this.port));
  }

  private Character findSeparatorChar(final String message) {
    final int dashIndex = message.indexOf('-');
    final int colonIndex = message.indexOf(':');
    if (colonIndex == -1 || (dashIndex != -1 && dashIndex < colonIndex))
      return '-';
    else if (dashIndex == -1 || colonIndex < dashIndex)
      return ':';
    return null;
  }

  public void run() throws IOException {
    this.send(this.teamName);
    while (true) {
      final String message = this.receive();
      final Character separator = this.findSeparatorChar(message);
      final int idIndex = 2;
      final int commentIndex = separator == null ? -1 : message.indexOf(separator, idIndex + 1);
      final String
          comment = commentIndex == -1 ? message.substring(idIndex) : message.substring(idIndex, commentIndex),
          args = commentIndex == -1 ? null : message.substring(commentIndex + 1);
      switch (message.substring(0, idIndex)) {
        case "01":
          final int mapHeaderIndex = message.indexOf('=');
          if (mapHeaderIndex == -1)
            logger.debug("Prêt. En attente de l'autre joueur...");
          else {
            logger.debug("Carte reçue. Chargement du monde...");
            final String map = message.substring(mapHeaderIndex + 1);
            this.world = new World(map);
            if (logger.isDebugEnabled())
              System.out.println(this.world.toString());
          }
          break;
        case "10":
          final CoffeeCellView action = this.world.play();
          if (logger.isDebugEnabled())
            System.out.println(this.world.toString());
          if (action == null)
            logger.debug("Pas de résultat de jeu trouvé, on n'envoie rien...");
          else {
            this.send(action.encode());
            this.world.place(action, false);
            logger.debug("On joue en " + action + " (" + action.encode() + ").");
          }
          break;

        case "20":
          final CoffeeCellView opponentAction = this.world.get(args);
          this.world.place(opponentAction, true);
          logger.debug("Le joueur adverse a joué en " + opponentAction + ".");
          break;

        case "21":
          logger.debug("Oups... Notre coup n'a pas été accepté x(");
          this.world.rejectPending();
          break;
        case "22":
          logger.debug("L'adversaire est nul, il n'a pas réussi à jouer :D");
          break;

        case "88":
          logger.debug("Partie terminée.");
          return;
      }
    }
  }
}
