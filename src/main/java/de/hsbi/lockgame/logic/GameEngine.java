package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.*;
import de.hsbi.lockgame.ui.GamePanel;
import java.util.ArrayList;
import java.util.List;

// TODO: Die GameEngine verwaltet den GameState.

// TODO: Die GameEngine wird durch den Timer im main() getriggert ("tick") und lässt den GameState
// daraufhin einen Schritt ausführen. Dann müssen alle für den GameState registrierten Observer
// benachrichtigt werden, damit das Spielfeld neu gezeichnet werden kann o.ä.

// TODO: Die GameEngine beobachtet die Tastatureingaben (gesetzt in GamePanel.setupKeyBindings()),
// die in Direction übersetzt und an GameEngine.update() übergeben werden. Wenn es eine neue Eingabe
// gibt, wird die "update"-Methode von GameEngine aufgerufen, und die GameEngine muss die
// Blickrichtung der Schlange aktualisieren und diese GameState-Änderung den für den GameState
// registrierten Observer mitteilen.

// TODO: Die GameEngine ist ein Observer für Direction: GameEngine.update(Direction)
// TODO: Die GameEngine ist ein Observable für GameState: GamePanel.update(GameState)
public final class GameEngine {
  private GamePanel gamePanel;
  private GameState gameState;

  public GameEngine(Level level) {
    // TODO: lege eine neue GameEngine mit den übergebenen Informationen an
    List<Position> snakeStart = new ArrayList<>();
    snakeStart.add(level.snakeStart());
    Snake snake = new Snake(snakeStart);
    this.gameState =
        new GameState(level, snake, level.pins(), GameState.Status.RUNNING, Direction.NONE);
    // throw new UnsupportedOperationException("method not implemented yet");
  }

  public GameState state() {
    // TODO: gebe den aktuellen Spielzustand zurück
    return gameState;
    // throw new UnsupportedOperationException("method not implemented yet");
  }

  public void setGamePanel(GamePanel panel) {
    // TODO: Setter
    this.gamePanel = panel;
    // throw new UnsupportedOperationException("method not implemented yet");
  }

  public void update(Direction d) {
    // TODO: aktualisiere den Blickwinkel der Schlange (GameState)
    gameState =
        new GameState(
            gameState.level(), gameState.snake(), gameState.pins(), gameState.status(), d);
    // TODO: benachrichtige alle Observer und gibt den neuen Spielzustand mit (Neuzeichnen der
    // Spielfläche)
    if (gamePanel != null) {
      gamePanel.update(gameState);
    }
    // throw new UnsupportedOperationException("method not implemented yet");
  }

  public void tick() {
    // TODO: lass das Spiel (den GameState) einen Schritt ("tick") machen
    gameState = gameState.tick();
    // TODO: benachrichtige alle Observer und gibt den neuen Spielzustand mit (Neuzeichnen der
    // Spielfläche)
    if (gamePanel != null) {
      gamePanel.update(gameState);
    }
    // throw new UnsupportedOperationException("method not implemented yet");
  }
}
