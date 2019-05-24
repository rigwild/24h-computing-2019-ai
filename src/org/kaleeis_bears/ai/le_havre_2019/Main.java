package org.kaleeis_bears.ai.le_havre_2019;

import org.kaleeis_bears.ai.commandline.CommandLineDefinition;
import org.kaleeis_bears.ai.commandline.arguments.OptionalArgDefinition;
import org.kaleeis_bears.ai.commandline.arguments.RequiredArgDefinition;
import org.kaleeis_bears.ai.commandline.options.OptionDefinition;
import org.kaleeis_bears.ai.logging.CategoryLogger;
import org.kaleeis_bears.ai.logging.ConsoleLogger;
import org.kaleeis_bears.ai.logging.Logger;

import java.io.IOException;
import java.net.UnknownHostException;

public class Main {

  public static final String TEAM_NAME = "Kaleeis Bears";

  //#region BOOTSTRAP
  private static final CommandLineDefinition COMMAND_LINE_DEFINITION = new CommandLineDefinition(
      "Epreuve reseau - 24H DUT INFO 24/05/19 - " + TEAM_NAME,
      "./epreuve_reseau",
      new OptionDefinition[]{
          new OptionDefinition('d', "debug", "Active le mode déboguage.", null)//,
      },
      new RequiredArgDefinition[]{
          new RequiredArgDefinition<>("HÔTE", "Hôte du serveur du concours.", CommandLineDefinition::IDENTITY_VALIDATOR),
          new RequiredArgDefinition<>("PORT", "Port du serveur du concours.", CommandLineDefinition.INTEGER_RANGE_VALIDATOR(1, 65536))
      },
      new OptionalArgDefinition[]{
          new OptionalArgDefinition<>("NOM_EQUIPE", TEAM_NAME, "Nom d'équipe à utiliser.", CommandLineDefinition::IDENTITY_VALIDATOR)
      }
  );

  static void run(Logger defaultLogger, String teamName, String host, int port) {
    final CategoryLogger networkLogger = new CategoryLogger(defaultLogger, "réseau");
//    final CategoryLogger aiLogger = new CategoryLogger(defaultLogger, "ai");

    networkLogger.debug("Tentative de connexion à " + host + ":" + port + "...");

    try {

      final LeHavreProtocol protocol = new LeHavreProtocol(networkLogger, teamName == null ? TEAM_NAME : teamName, host, port);

      protocol.run();

    } catch (UnknownHostException e) {
      networkLogger.exception("Hôte introuvable", e);
    } catch (IOException e) {
      networkLogger.exception("Connexion interrompue", e);
    }

  }

  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println(COMMAND_LINE_DEFINITION.help());
      System.exit(2);
    }
    final Logger logger = new ConsoleLogger();
    CommandLineDefinition.CommandLineResult commandLineResult = null;
    try {
      commandLineResult = COMMAND_LINE_DEFINITION.parse(args);
    } catch (NumberFormatException ex) {
      logger.exception("ARGUMENT", ex);
    } catch (IllegalArgumentException ex) {
      logger.exception("SYNTAXE", ex);
    } catch (Throwable ex) {
      logger.exception("INATTENDUE", ex);
    }
    if (commandLineResult == null) {
      System.err.println();
      System.err.println(COMMAND_LINE_DEFINITION.help());
      System.exit(1);
    } else {
      logger.setDebugEnabled(commandLineResult.isOptionSet(0));
      run(
          logger,
          ((String) commandLineResult.getArgument(2)),
          ((String) commandLineResult.getArgument(0)),
          ((Integer) commandLineResult.getArgument(1))
      );
    }
  }

  //#endregion
}
