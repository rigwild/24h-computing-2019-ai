package org.kaleeis_bears.ai.le_havre_2019;

import org.kaleeis_bears.ai.logging.Logger;

public class StateMachineExample {

  //#region ETATS

  public interface State {
    String execute(); // FIXME : Passer le controlleur ici ?
  }

  public interface StateCompletion {
    void done();
  }

  //#endregion

  //#region IMPLEMENTATION

  public static class SaySomething extends AbstractState {
    private final String something;
    private final StateCompletion onDone;

    public SaySomething(StateController controller, String something, StateCompletion onDone) {
      super(controller);
      this.something = something;
      this.onDone = onDone;
    }

    @Override
    public String execute() {
      this.onDone.done();
      return this.something;
    }
  }

  public static class InitialState extends AbstractState {
    private final Logger logger;
    private final int maxCallCount;
    private int callCounts = 0;

    public InitialState(StateController controller, Logger logger, int maxCallCount) {
      super(controller);
      this.logger = logger;
      this.maxCallCount = maxCallCount;
    }

    @Override
    public String execute() {
      this.logger.debug("Appel n°" + (++this.callCounts));
      if (this.callCounts >= this.maxCallCount)
        this.controller.setState(null); // Plus d'état suivant -> terminé.
      else
        this.controller.setState(
            new SaySomething(
                this.controller,
                "Appel n°" + this.callCounts,
                () -> this.controller.setState(InitialState.this) // On ne recrée pas une nouvelle instance, on conserve donc l'état (le nombre d'appels).
            )
        );
      return this.controller.execute(); // Attention : peut causer des appels récursifs et lever des `StackOverflowException`s.
    }
  }

  public static abstract class AbstractState implements State {
    protected final StateController controller;

    public AbstractState(StateController controller) {
      this.controller = controller;
    }
  }

  public static class StateController implements State {
    private State currentState;

    public void setState(State state) {
      this.currentState = state;
    }

    public String execute() {
      return this.currentState.execute();
    }
  }

  // #endregion

}
