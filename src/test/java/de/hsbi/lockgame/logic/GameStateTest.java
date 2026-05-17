package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.CellType;
import de.hsbi.lockgame.model.Direction;
import de.hsbi.lockgame.model.Level;
import de.hsbi.lockgame.model.Pin;
import de.hsbi.lockgame.model.Position;
import de.hsbi.lockgame.model.Snake;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    private Level create_empty_level(int width, int height) {
        CellType[][] cells = new CellType[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = CellType.EMPTY;
            }
        }

        return new Level(width, height, cells, List.of(), new Position(0, 0));
    }

    @Test
    void constructor_speichert_werte() {
        // given
        Level level = create_empty_level(3, 3);
        Snake snake = new Snake(List.of(new Position(1, 1)));
        List<Pin> pins = List.of();
        GameState.Status status = GameState.Status.RUNNING;
        Direction direction = Direction.RIGHT;

        // when
        GameState state = new GameState(level, snake, pins, status, direction);

        // then
        assertSame(level, state.level());
        assertSame(snake, state.snake());
        assertSame(pins, state.pins());
        assertEquals(status, state.status());
        assertEquals(direction, state.pendingDirection());
    }

    @Test
    void tick_macht_nichts_wenn_status_nicht_running_ist() {
        // given
        Level level = create_empty_level(3, 3);
        Snake snake = new Snake(List.of(new Position(1, 1)));
        GameState state = new GameState(level, snake, List.of(), GameState.Status.WON, Direction.RIGHT);

        // when
        GameState result = state.tick();

        // then
        assertSame(state, result);
    }

    @Test
    void tick_macht_nichts_wenn_richtung_none_ist() {
        // given
        Level level = create_empty_level(3, 3);
        Snake snake = new Snake(List.of(new Position(1, 1)));
        GameState state = new GameState(level, snake, List.of(), GameState.Status.RUNNING, Direction.NONE);

        // when
        GameState result = state.tick();

        // then
        assertSame(state, result);
    }

    @Test
    void tick_bewegt_schlange_auf_freies_feld() {
        // given
        Level level = create_empty_level(3, 3);
        Snake snake = new Snake(List.of(new Position(1, 1)));
        GameState state = new GameState(level, snake, List.of(), GameState.Status.RUNNING, Direction.RIGHT);

        // when
        GameState result = state.tick();

        // then
        assertEquals(GameState.Status.RUNNING, result.status());
        assertEquals(Direction.NONE, result.pendingDirection());
        assertEquals(2, result.snake().body().size());
        assertEquals(2, result.snake().head().x());
        assertEquals(1, result.snake().head().y());
    }

    @Test
    void tick_verliert_wenn_schlange_spielfeld_verlaesst() {
        // given
        Level level = create_empty_level(2, 2);
        Snake snake = new Snake(List.of(new Position(0, 0)));
        GameState state = new GameState(level, snake, List.of(), GameState.Status.RUNNING, Direction.LEFT);

        // when
        GameState result = state.tick();

        // then
        assertEquals(GameState.Status.LOST_OUT_OF_BOUNDS, result.status());
    }

    @Test
    void tick_blockiert_bei_wand() {
        // given
        CellType[][] cells = new CellType[3][3];

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                cells[x][y] = CellType.EMPTY;
            }
        }

        cells[1][0] = CellType.WALL;

        Level level = new Level(3, 3, cells, List.of(), new Position(0, 0));
        Snake snake = new Snake(List.of(new Position(0, 0)));
        GameState state = new GameState(level, snake, List.of(), GameState.Status.RUNNING, Direction.RIGHT);

        // when
        GameState result = state.tick();

        // then
        assertEquals(GameState.Status.RUNNING, result.status());
        assertEquals(Direction.NONE, result.pendingDirection());
        assertSame(snake, result.snake());
    }

    @Test
    void tick_verliert_bei_selbstkollision() {
        // given
        Level level = create_empty_level(4, 4);

        Snake snake = new Snake(List.of(
            new Position(1, 1),
            new Position(2, 1),
            new Position(2, 2)
        ));

        GameState state = new GameState(level, snake, List.of(), GameState.Status.RUNNING, Direction.RIGHT);

        // when
        GameState result = state.tick();

        // then
        assertEquals(GameState.Status.LOST_SELF_COLLISION, result.status());
    }

    @Test
    void tick_blockiert_bei_pin_slot_ohne_pin() {
        // given
        CellType[][] cells = new CellType[3][3];

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                cells[x][y] = CellType.EMPTY;
            }
        }

        cells[1][0] = CellType.PIN_SLOT;

        Level level = new Level(3, 3, cells, List.of(), new Position(0, 0));
        Snake snake = new Snake(List.of(new Position(0, 0)));
        GameState state = new GameState(level, snake, List.of(), GameState.Status.RUNNING, Direction.RIGHT);

        // when
        GameState result = state.tick();

        // then
        assertEquals(GameState.Status.RUNNING, result.status());
        assertEquals(Direction.NONE, result.pendingDirection());
        assertSame(snake, result.snake());
    }

    @Test
    void tick_blockiert_bei_high_pin() {
        // given
        CellType[][] cells = new CellType[3][3];

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                cells[x][y] = CellType.EMPTY;
            }
        }

        cells[1][0] = CellType.PIN_SLOT;

        Pin pin = new Pin(new Position(1, 0), Pin.State.HIGH, Direction.RIGHT);
        List<Pin> pins = List.of(pin);

        Level level = new Level(3, 3, cells, pins, new Position(0, 0));
        Snake snake = new Snake(List.of(new Position(0, 0)));
        GameState state = new GameState(level, snake, pins, GameState.Status.RUNNING, Direction.RIGHT);

        // when
        GameState result = state.tick();

        // then
        assertEquals(GameState.Status.RUNNING, result.status());
        assertEquals(Direction.NONE, result.pendingDirection());
        assertEquals(Pin.State.HIGH, result.pins().get(0).state());
        assertSame(snake, result.snake());
    }

    @Test
    void tick_aktiviert_low_pin_und_gewinnt() {
        // given
        CellType[][] cells = new CellType[3][3];

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                cells[x][y] = CellType.EMPTY;
            }
        }

        cells[1][0] = CellType.PIN_SLOT;

        Pin pin = new Pin(new Position(1, 0), Pin.State.LOW, Direction.RIGHT);
        List<Pin> pins = List.of(pin);

        Level level = new Level(3, 3, cells, pins, new Position(0, 0));
        Snake snake = new Snake(List.of(new Position(0, 0)));
        GameState state = new GameState(level, snake, pins, GameState.Status.RUNNING, Direction.RIGHT);

        // when
        GameState result = state.tick();

        // then
        assertEquals(GameState.Status.WON, result.status());
        assertEquals(Direction.NONE, result.pendingDirection());
        assertEquals(Pin.State.HIGH, result.pins().get(0).state());
        assertSame(snake, result.snake());
    }
}
