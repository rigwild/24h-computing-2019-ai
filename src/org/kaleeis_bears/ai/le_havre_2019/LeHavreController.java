package org.kaleeis_bears.ai.le_havre_2019;

import org.kaleeis_bears.ai.commandline.CommandLineDefinition;
import org.kaleeis_bears.ai.commandline.arguments.ArgList;
import org.kaleeis_bears.ai.commandline.arguments.OptionalArgDefinition;
import org.kaleeis_bears.ai.commandline.arguments.RequiredArgDefinition;
import org.kaleeis_bears.ai.commandline.options.OptionDefinition;
import org.kaleeis_bears.ai.controllers.ControllerFactory;
import org.kaleeis_bears.ai.controllers.NetworkController;
import org.kaleeis_bears.ai.logging.ConsoleLogger;
import org.kaleeis_bears.ai.logging.Logger;

import java.lang.reflect.InvocationTargetException;

// TODO : Trouver une façon d'instancier l'IA tout en gardant une certaine abstraction
@Deprecated
public class LeHavreController implements ControllerFactory<NetworkController> {

  public static final String TEAM_NAME = "Kaleeis Bears";

  public static final boolean DEBUG = false;

  public static final String AI_NAME_DEFAULT = "AI_v00_EXAMPLE";
  public static final String AI_PACKAGE_ROOT = "org.kaleeis_bears.ai.le_havre_2019.implementations";

  private static final CommandLineDefinition COMMAND_LINE_DEFINITION = new CommandLineDefinition(
      "Epreuve reseau - 24H DUT INFO 24/05/19 - " + TEAM_NAME,
      "./epreuve_reseau",
      new OptionDefinition[]{
          new OptionDefinition('d', "debug", "Active le mode déboguage.", null),
          new OptionDefinition('n', "nom", "Change le nom de l'équipe. Par défaut : " + TEAM_NAME,
              new ArgList(new RequiredArgDefinition[]{
                  new RequiredArgDefinition<>("NOM", "Nom de l'équipe ", CommandLineDefinition::IDENTITY_VALIDATOR)
              }, null)
          )
      },
      new RequiredArgDefinition[]{
          new RequiredArgDefinition<>("HÔTE", "Hôte du serveur du concours.", CommandLineDefinition::IDENTITY_VALIDATOR),
          new RequiredArgDefinition<>("PORT", "Port du serveur du concours.", CommandLineDefinition.INTEGER_RANGE_VALIDATOR(1, 65536))
      },
      new OptionalArgDefinition[]{
          new OptionalArgDefinition<>("VERSION_IA", AI_NAME_DEFAULT, "Version de l'IA à utiliser.", CommandLineDefinition::IDENTITY_VALIDATOR)
      }
  );

  static BaseIA instantiate(String name) throws IllegalAccessException, InstantiationException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException {
    return (BaseIA) Class
        .forName(AI_PACKAGE_ROOT + "." + name)
        .getConstructor()
        .newInstance();
  }

  @Override
  public boolean isDebugDefault() {
    return DEBUG;
  }

  @Override
  public CommandLineDefinition getCommandLineDefinition() {
    return COMMAND_LINE_DEFINITION;
  }

  @Override
  public NetworkController build(CommandLineDefinition.CommandLineResult args) {
    Logger logger = new ConsoleLogger(args.isOptionSet(0));
    String AI_NAME = (String) args.getArgument(2);
    AI_NAME = AI_NAME == null ? AI_NAME_DEFAULT : AI_NAME;
    try {
      final BaseIA ai = instantiate(AI_NAME);
      return new NetworkController(logger, (String) args.getArgument(0), (Integer) args.getArgument(1), new AI24hProtocol(logger, ai));
    } catch (InvocationTargetException itex) {
      logger.exception("NON GÉRÉE", "Une exception est survenue lors de la création de l'IA", itex.getCause());
    } catch (ClassNotFoundException cnfex) {
      logger.exception("ARGUMENT", "L'IA « " + AI_NAME + " » n'existe pas (classe introuvable)", cnfex);
    } catch (NoSuchMethodException nsmex) {
      logger.exception("CODE", "La classe IA « " + AI_NAME + " » n'a pas de constructeur publique sans paramètres", nsmex);
    } catch (IllegalAccessException iaex) {
      logger.exception("CODE", "La classe IA « " + AI_NAME + " » doit être publique", iaex);
    } catch (Throwable ex) {
      logger.exception("INATTENDUE", "Impossible de créer l'IA", ex);
    }
    return null;
  }
}