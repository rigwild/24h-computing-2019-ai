package org.kaleeis_bears.ia_24h_2019;

import java.lang.reflect.InvocationTargetException;

public class Main {

	static private final String IA_PACKAGE_ROOT = "org.kaleeis_bears.ia_24h_2019.implementations";
	static private final String IA_PAR_DEFAUT = "IA_v00_EXEMPLE";

	static public String NOM_EQUIPE = "Kaleeis Bears";
	static public boolean DEBUG = false;

	static String hote, nomIA = IA_PAR_DEFAUT;
	static int port;

	public static void main(String[] args) {
		DEBUG = false;
		if (!parseArgs(args, 2))
		{
			help();
			System.exit(2);
		}

		BaseIA ia = null;

		try
		{
			ia = instantiate(nomIA, hote, port);
		}
		catch (InvocationTargetException itex)
		{
			Throwable ex = itex.getCause();
			if (ex instanceof java.net.SocketException)
				exception("RÉSEAU", "La connexion avec le serveur a échouée", ex);
			else
				exception("NON GÉRÉE", "Une exception est survenue lors de la création de l'IA", ex);
		}
		catch (ClassNotFoundException cnfex) {
			exception("ARGUMENT", "L'IA « " + nomIA + " » n'existe pas (classe introuvable)", cnfex);
		}
		catch (NoSuchMethodException nsmex) {
			exception("CODE", "La classe IA « " + nomIA + " » n'a pas de constructeur publique dont les paramètres sont (String, int, boolean)", nsmex);
		}
		catch (IllegalAccessException iaex) {
			exception("CODE", "La classe IA « " + nomIA + " » doit être publique", iaex);
		}
		catch (Exception ex) {
			exception("INATTENDUE", "Impossible de créer l'IA", ex);
		}

		if (ia != null)
		{
			System.out.println("CONNECTÉ AU SERVEUR. DEBUT DE LA PARTIE.");
			try
			{
				// ia.run();
				System.out.println("PARTIE TERMINÉE.");
				return;
			}
			catch (Exception ex) {
				exception("ERREUR INATTENDUE", "Impossible de lancer l'IA", ex);
			}
		}
		System.exit(1);
	}

	/**********/

	// GESTION DU DÉBOGAGE

	/**
	 * Fourni un moyen d'afficher des messages dans la console uniquement lorsque le programme est en mode DÉBUG.
	 * Cela permet un légér gain de performance (si la partie se joue vite par exemple).
	 * NOTE : Un retour à la ligne est automatiquement ajouté.
	 * @param message Texte à écrire dans la console.
	 */
	public static void debug(String message) {
		if (DEBUG)
			System.out.println(message);
	}

	public static void exception(String type, String message) {
		exception(type, message, null);
	}
	public static void exception(String type, Throwable exception) {
		exception(type, null, exception);
	}
	public static void exception(String type, String message, Throwable exception) {
		System.err.print("ERREUR " + type);
		if (message != null)
			System.err.print(" : " + message);
		if (DEBUG && exception != null)
		{
			System.err.print(" : ");
			exception.printStackTrace();
		}
		else if (message.charAt(message.length() - 1) != '.')
			System.err.println(".");
	}

	/**********/

	// CHARGEMENT DYNAMIQUE DE LA CLASSE DE L'IA À UTILISER

	static BaseIA instantiate(String name, String hote, int port) throws IllegalAccessException, InstantiationException, NoSuchMethodException, ClassNotFoundException, java.lang.reflect.InvocationTargetException {
		return (BaseIA)Class
			.forName(IA_PACKAGE_ROOT + "." + name)
			.getConstructor(String.class, int.class)
			.newInstance(hote, port);
	}

	/**********/

	// LECTURE DES PARAMETRES + MESSAGE D'AIDE

	static void help() {
		System.out.println("Epreuve reseau - 24H DUT INFO 25/05/18 - " + NOM_EQUIPE);
		System.out.println();
		System.out.println("Syntaxe : ./epreuve_reseau <options> [hote] [port] <version_ia>");
		System.out.println();
		System.out.println("Où [hote] est le nom d'hote ou l'IP du serveur et [port] le port où est hébergé le serveur.");
		System.out.println();
		System.out.println("Options possibles :");
		System.out.println(" -d, --debug     : Active le mode DÉBOGAGE (plus de messages dans la console).");
		System.out.println(" -n, --nom [NOM] : Utilise un autre nom pour d'équipe.");
		System.out.println();
		System.out.println("Si <version_ia> est omit, la classe « " + IA_PAR_DEFAUT + " » est utilisée par défaut.");
	}

	static boolean parseArgs(final String[] args, final int requiredPositionalArg) {
		int positionalArgNo = 0;
		String arg;
		for (int i = 0; i < args.length; i++) {
			arg = args[i];
			if (arg.startsWith("-"))
				if (arg.startsWith("--"))
					switch (arg.substring(2))
					{
						case "debug":
							DEBUG = true;
							break;
						case "nom":
							if (i + 1 == args.length)
							{
								System.err.println("ERREUR ARGUMENT : Vous devez spécifier un nom d'équipe après l'option « " + arg + " ».");
								System.err.println();
								return false;
							}
							NOM_EQUIPE = args[++i];
							break;

						default:
							System.err.println("ERREUR ARGUMENT : Option « " + arg + " » non reconnue.");
							System.err.println();
							return false;
					}
				else
					for (int j = 1; j < arg.length(); j++)
						switch (arg.charAt(j))
						{
							case 'd':
								DEBUG = true;
								break;
							case 'n':
								if (i + 1 == args.length)
								{
									System.err.println("ERREUR ARGUMENT : Vous devez spécifier un nom d'équipe après l'option « -" + arg.charAt(j) + " ».");
									System.err.println();
									return false;
								}
								NOM_EQUIPE = args[++i];
								break;

							default:
								System.err.println("ERREUR ARGUMENT : Option « -" + arg.charAt(j) + " » non reconnue.");
								System.err.println();
								return false;
						}
			else
			{
				switch (positionalArgNo)
				{
					case 0:
						hote = arg;
						break;
					case 1:
						try
						{
							port = Integer.parseInt(arg);
							if (port > 0 && port <= 65535)
								break;
							else
							{
								System.err.println("ERREUR SYNTAXE : le numéro du port doit être compris entre 1 et 65535 (inclus).");
								System.err.println();
							}
						}
						catch (Exception ex) {
							System.err.println("ERREUR SYNTAXE : le numéro du port doit être un nombre.");
							System.err.println();
						}
						return false;

					case 2:
						nomIA = arg;
						break;

					default:
						System.err.println("ERREUR ARGUMENT : L'argument « " + arg + " » est en trop.");
						System.err.println();
						return false;
				}
				positionalArgNo++;
			}
		}
		if (positionalArgNo < requiredPositionalArg)
		{
			System.err.println("ERREUR : " + (requiredPositionalArg - positionalArgNo) + " argument manquant.");
			System.err.println();
		}
		return (positionalArgNo >= requiredPositionalArg);
	}
	
}