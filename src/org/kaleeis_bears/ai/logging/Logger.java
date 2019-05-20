package org.kaleeis_bears.ai.logging;

public interface Logger {
  /**
   * Fourni un moyen d'afficher des messages dans la console uniquement lorsque le programme est en mode DÉBUG.
   * Cela permet un légér gain de performance (si la partie se joue vite par exemple).
   * NOTE : Un retour à la ligne est automatiquement ajouté.
   *
   * @param message Texte à écrire.
   */
  void debug(String message);

  boolean isDebugEnabled();

  void setDebugEnabled(boolean enabled);

  default void exception(String type, String message) {
    exception(type, message, null);
  }

  default void exception(String type, Throwable exception) {
    exception(type, exception.getMessage(), exception);
  }

  void exception(String type, String message, Throwable exception);
}
