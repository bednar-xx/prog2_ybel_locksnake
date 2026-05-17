package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.*;
import java.util.ArrayList;
import java.util.List;

public final class GameState {

  private final Level level;
  private final Snake snake;
  private final List<Pin> pins;
  private final Status status;
  private final Direction pendingDirection;

  public GameState(
      Level level, Snake snake, List<Pin> pins, Status status, Direction pendingDirection) {
    // TODO: lege einen neuen GameState mit den übergebenen Informationen an
    this.level = level;
    this.snake = snake;
    this.pins = pins;
    this.status = status;
    this.pendingDirection = pendingDirection;
    // throw new UnsupportedOperationException("method not implemented yet");
  }

  public Level level() {
    // TODO: Getter
    return level;
    // throw new UnsupportedOperationException("method not implemented yet");
  }

  public Snake snake() {
    // TODO: Getter
    return snake;
    // throw new UnsupportedOperationException("method not implemented yet");
  }

  public List<Pin> pins() {
    // TODO: Getter
    return pins;
    // throw new UnsupportedOperationException("method not implemented yet");
  }

  public Status status() {
    // TODO: Getter
    return status;
    // throw new UnsupportedOperationException("method not implemented yet");
  }

  public Direction pendingDirection() {
    // TODO: Getter
    return pendingDirection;
    // throw new UnsupportedOperationException("method not implemented yet");
  }

  public GameState tick() {
    // TODO: diese Methode lässt das Spiel einen Schritt laufen (berechnet den Spielzustand im
    // nächsten Schritt)

    // TODO: early exit: wenn das Spiel nicht läuft oder keine Blickrichtung gesetzt ist: keine
    // Änderung
    if (!status.isRunning() || Direction.NONE.equals(pendingDirection)) {
      return this;
    }

    // TODO: prüfe die folgenden Bedingungen:
    Position nextHead = snake.nextHead(pendingDirection);
    // (a) Schlange würde das Spielfeld verlassen: Spiel verloren
    if (!level.isInside(nextHead)) {
      return new GameState(level, snake, pins, Status.LOST_OUT_OF_BOUNDS, pendingDirection);
    }

    // (b) Schlange würde in ein Wandelement gehen: Blockiert (keine Bewegung, Blickrichtung
    // "none")
    if (level.cellAt(nextHead) == CellType.WALL) {
      return new GameState(level, snake, pins, status, Direction.NONE);
    }

    // (c) Schlange beisst sich: Spiel verloren
    if (snake.occupies(nextHead)) {
      return new GameState(level, snake, pins, Status.LOST_SELF_COLLISION, pendingDirection);
    }
    for (Position snakeBody : snake.body()) {
      if (snakeBody.x() == nextHead.x() && snakeBody.y() == nextHead.y()) {
        return new GameState(level, snake, pins, Status.LOST_SELF_COLLISION, pendingDirection);
      }
    }

    // (d) Schlange würde auf einen Pin gehen (Pin bereits gesetzt oder Schlange kommt nicht in
    // der Aktivierungsrichtung): Blockiert (keine Bewegung, Blickrichtung "none")
    if (level.cellAt(nextHead) == CellType.PIN_SLOT) {
      Pin searchedPin = null;
      for (Pin pin : pins) {
        if (pin.position().x() == nextHead.x() && pin.position().y() == nextHead.y()) {
          searchedPin = pin;
          break;
        }
      }
      if (searchedPin == null) {
        return new GameState(level, snake, pins, status, Direction.NONE);
      }
      if (searchedPin.state() == Pin.State.HIGH
          || searchedPin.activationDirection() != pendingDirection) {
        return new GameState(level, snake, pins, status, Direction.NONE);
      }

      // TODO: aktiviere einen noch nicht gesetzten Pin, wenn die Schlange in der richtigen
      // Richtung
      // auf den Pin gehen würde (die Schlange darf dabei aber nicht auf den Pin gehen)
      List<Pin> newPins = new ArrayList<>();
      for (Pin pin : pins) {
        if (!pin.equals(searchedPin)) {
          newPins.add(pin);
        } else {
          newPins.add(searchedPin.withState(Pin.State.HIGH));
        }
      }
      int lowPin = 0;
      for (Pin pin2 : newPins) {
        if (pin2.state() == Pin.State.LOW) {
          lowPin++;
        }
      }
      if (lowPin == 0) {
        return new GameState(level, snake, newPins, Status.WON, Direction.NONE);
      } else {
        return new GameState(level, snake, newPins, status, Direction.NONE);
      }
    }
    // TODO: anderenfalls: bewege die Schlange um einen Schritt in Blickrichtung (falls gesetzt)
    return new GameState(level, snake.grow(pendingDirection), pins, status, Direction.NONE);
    // throw new UnsupportedOperationException("method not implemented yet");
  }

  public enum Status {
    RUNNING,
    WON,
    LOST_SELF_COLLISION,
    LOST_OUT_OF_BOUNDS;

    public boolean isRunning() {
      return this == RUNNING;
    }
  }
}
