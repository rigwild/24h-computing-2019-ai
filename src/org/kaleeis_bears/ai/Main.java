package org.kaleeis_bears.ai;

import org.kaleeis_bears.ai.commandline.CommandLineDefinition;
import org.kaleeis_bears.ai.controllers.Controller;
import org.kaleeis_bears.ai.controllers.ControllerFactory;
import org.kaleeis_bears.ai.logging.ConsoleLogger;
import org.kaleeis_bears.ai.logging.Logger;

@Deprecated
public class Main {

  static private final ControllerFactory<?> CONTROLLER_FACTORY = new org.kaleeis_bears.ai.le_havre_2019.LeHavreController();

  public static void main(String[] args) {
    final Logger logger = new ConsoleLogger(CONTROLLER_FACTORY.isDebugDefault());

    CommandLineDefinition.CommandLineResult result = null;
    CommandLineDefinition definition = null;
    try {
      logger.debug("Création de la définition des arguments de la ligne de commande...");
      definition = CONTROLLER_FACTORY.getCommandLineDefinition();
    } catch (Throwable ex) {
      logger.exception("INTERNE", ex);
      System.exit(3);
    }
    try {
      logger.debug("Lecture des arguments...");
      result = definition.parse(args);
    } catch (IllegalArgumentException ex) {
      logger.exception("ARGUMENT", ex);
    } catch (Throwable ex) {
      logger.exception("LIGNE DE COMMANDE", ex);
    }
    if (result == null) {
      logger.debug("Lecture de la ligne de commande incomplète.");
      if (definition != null) {
        logger.debug("Affichage du message d'aide.");
        if (args.length != 0) {
          System.err.println();
        }
        System.err.println(definition.help());
      }
      logger.debug("Éxécution terminée. Code de sortie : 2");
      System.exit(2);
    }

    logger.debug("Création du controleur...");
    final Controller controller = CONTROLLER_FACTORY.build(result);

    if (controller == null) {
      logger.debug("Création échouée. Code de sortie : 1");
      System.exit(1);
    }

    controller.run();
  }

  /**********/

  // LECTURE DES PARAMETRES + MESSAGE D'AIDE

//	static void help(CommandLineDefinition definition) {
//		System.out.println("Epreuve reseau - 24H DUT INFO 24/05/19 - " + TEAM_NAME);
//		System.out.println();
//		System.out.println("Syntaxe : ./epreuve_reseau <options>" + definition);
//		System.out.println();
//		System.out.print(definition.help());
//	}

//	static Object[] parseArgs(final String[] args, CommandLineDefinition definition) {
//		Object[] positionalResult = new Object[definition.args.length];
//    int positionalArgNo = 0;
//		String arg;
//		for (int i = 0; i < args.length; i++) {
//			arg = args[i];
//			if (arg.startsWith("-"))
//				if (arg.startsWith("--"))
//					switch (arg.substring(2))
//					{
//						case "debug":
//							DEBUG = true;
//							break;
//            case "nom":
//              if (i + 1 == args.length)
//              {
//                exception("ARGUMENT", "Vous devez spécifier un nom d'équipe après l'option « " + arg + " ».");
//                return null;
//              }
//              TEAM_NAME = args[++i];
//              break;
//            case "ia":
//              if (i + 1 == args.length)
//              {
//                exception("ARGUMENT", "Vous devez spécifier un nom d'IA après l'option « " + arg + " ».");
//                return null;
//              }
//              AI_NAME = args[++i];
//              break;
//
//						default:
//						  exception("ARGUMENT", "Option « " + arg + " » non reconnue.");
//							return null;
//					}
//				else
//					for (int j = 1; j < arg.length(); j++)
//						switch (arg.charAt(j))
//						{
//							case 'd':
//								DEBUG = true;
//								break;
//              case 'n':
//                if (i + 1 == args.length)
//                {
//                  exception("ARGUMENT", "Vous devez spécifier un nom d'équipe après l'option « -" + arg.charAt(j) + " ».");
//                  return null;
//                }
//                TEAM_NAME = args[++i];
//                break;
//              case 'i':
//                if (i + 1 == args.length)
//                {
//                  exception("ARGUMENT", "Vous devez spécifier un nom d'IA après l'option « -" + arg.charAt(j) + " ».");
//                  return null;
//                }
//                AI_NAME = args[++i];
//                break;
//
//							default:
//							  exception("ARGUMENT", "Option « -" + arg.charAt(j) + " » non reconnue.");
//								return null;
//						}
//			else
//			{
//			  if (positionalArgNo >= definition.args.length) {
//          exception("ARGUMENT", "L'argument « " + arg + " » est en trop.");
//          return null;
//        }
//        RequiredArgDefinition<?> argDefinition = definition.args[positionalArgNo];
//			  try {
//          positionalResult[positionalArgNo] = argDefinition.compute(arg);
//        } catch (IndexOutOfBoundsException ex) {
//          exception("SYNTAXE", argDefinition.name + " ne peut prendre la valeur « " + arg + " » : " + ex.getMessage(), ex);
//          return null;
//        } catch (NumberFormatException ex) {
//          exception("SYNTAXE", argDefinition.name + " doit être un nombre", ex);
//          return null;
//        } catch (Throwable ex) {
//          exception("SYNTAXE", ex.getMessage(), ex);
//          return null;
//        }
//				positionalArgNo++;
//			}
//		}
//		if (args.length != 0 && positionalArgNo  < positionalResult.length) {
//		  StringBuilder builder = new StringBuilder();
//		  for (int i = positionalArgNo; i < positionalResult.length; i++)
//		    builder.append((i == positionalArgNo ? "" : ", ") + definition.args[i].name);
//      exception("ARGUMENT", (positionalResult.length - positionalArgNo) + " argument(s) manquant(s) : " + builder);
//    }
//		return (positionalArgNo >= positionalResult.length) ? positionalResult : null;
//	}

}