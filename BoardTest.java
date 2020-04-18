import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

	private Board board;

	private Piece stick;
	private Piece square;
	private Piece pyramid;

	@BeforeEach
	protected void setUp() {
		board = new Board(6, 8);

		stick = new Piece(Piece.STICK_STR);
		square = new Piece(Piece.SQUARE_STR);
		pyramid = new Piece(Piece.PYRAMID_STR);
	}

	@Test
	public void getWidthTest() {
		assertEquals(6, board.getWidth());
	}

	@Test
	public void getHeightTest() {
		assertEquals(8, board.getHeight());
	}

	@Test
	public void placeTest() {
		int status;

		status = board.place(stick, 0, 0);
		assertEquals(board.PLACE_OK, status);
		board.commit();

		status = board.place(square, 1, 0);
		assertEquals(board.PLACE_OK, status);
		board.commit();

		status = board.place(pyramid, 3, 0);
		assertEquals(board.PLACE_ROW_FILLED, status);
		board.commit();

		status = board.place(stick, 0, 5);
		assertEquals(board.PLACE_OUT_BOUNDS, status);
		board.commit();

		status = board.place(square, 3, 1);
		assertEquals(board.PLACE_BAD, status);

		assertThrows(RuntimeException.class, () -> board.place(pyramid, 0, 0));
	}

	@Test
	public void getMaxHeightTest() {
		assertEquals(0, board.getMaxHeight());

		board.place(stick, 0, 0);
		board.commit();
		assertEquals(4, board.getMaxHeight());

		board.place(square, 1, 0);
		board.commit();
		assertEquals(4, board.getMaxHeight());

		board.place(pyramid, 2, 5);
		board.commit();
		assertEquals(7, board.getMaxHeight());
	}

	@Test
	public void getColumnHeightTest() {
		for (int i = 0; i < board.getWidth(); i++) {
			assertEquals(0, board.getColumnHeight(i));
		}

		board.place(stick, 0, 0);
		board.commit();
		board.place(square, 1, 0);
		board.commit();
		board.place(pyramid, 2, 5);
		board.commit();

		assertEquals(4, board.getColumnHeight(0));
		assertEquals(2, board.getColumnHeight(1));
		assertEquals(6, board.getColumnHeight(2));
		assertEquals(7, board.getColumnHeight(3));
		assertEquals(6, board.getColumnHeight(4));
		assertEquals(0, board.getColumnHeight(5));
	}

	@Test
	public void getRowWidthTest() {
		for (int i = 0; i < board.getHeight(); i++) {
			assertEquals(0, board.getRowWidth(i));
		}

		board.place(stick, 0, 0);
		board.commit();
		board.place(square, 1, 0);
		board.commit();
		board.place(pyramid, 2, 5);
		board.commit();

		assertEquals(3, board.getRowWidth(0));
		assertEquals(3, board.getRowWidth(1));
		assertEquals(1, board.getRowWidth(2));
		assertEquals(1, board.getRowWidth(3));
		assertEquals(0, board.getRowWidth(4));
		assertEquals(3, board.getRowWidth(5));
		assertEquals(1, board.getRowWidth(6));
		assertEquals(0, board.getRowWidth(7));
	}

	@Test
	public void getGridTest() {
		assertFalse(board.getGrid(0, 0));

		board.place(stick, 0, 0);
		board.commit();

		assertTrue(board.getGrid(0, 1));
		assertFalse(board.getGrid(1, 0));

		assertTrue(board.getGrid(-1, 0));
		assertTrue(board.getGrid(0, -1));
		assertTrue(board.getGrid(8, 0));
		assertTrue(board.getGrid(0, 8));
	}

	@Test
	public void dropHeightTest() {
		assertEquals(0, board.dropHeight(stick, 0));

		board.place(stick, 0, 0);
		board.commit();

		assertEquals(4, board.dropHeight(square, 0));
		assertEquals(0, board.dropHeight(square, 1));

		board.place(square, 1, 0);
		board.commit();

		assertEquals(4, board.dropHeight(pyramid, 0));
		assertEquals(2, board.dropHeight(pyramid, 1));
		assertEquals(0, board.dropHeight(pyramid, 3));
	}

	@Test
	public void clearRowsTest() {
		int rowsCleared;

		rowsCleared = board.clearRows();
		assertEquals(0, rowsCleared);

		board.commit();
		board.place(stick, 0, 0);
		rowsCleared = board.clearRows();
		assertEquals(0, rowsCleared);

		board.commit();
		board.place(square, 1, 0);
		rowsCleared = board.clearRows();
		assertEquals(0, rowsCleared);

		board.commit();
		board.place(pyramid, 3, 0);
		rowsCleared = board.clearRows();
		assertEquals(1, rowsCleared);

		rowsCleared = board.clearRows();
		assertEquals(0, rowsCleared);

		board.commit();
		board.place(square, 1, 1);
		rowsCleared = board.clearRows();
		assertEquals(0, rowsCleared);

		board.commit();
		board.place(square, 3, 1);
		rowsCleared = board.clearRows();
		assertEquals(0, rowsCleared);

		board.commit();
		board.place(stick, 5, 1);
		rowsCleared = board.clearRows();
		assertEquals(2, rowsCleared);

		rowsCleared = board.clearRows();
		assertEquals(0, rowsCleared);

		assertEquals(1, board.getColumnHeight(0));
		assertEquals(1, board.getColumnHeight(1));
		assertEquals(1, board.getColumnHeight(2));
		assertEquals(0, board.getColumnHeight(3));
		assertEquals(1, board.getColumnHeight(4));
		assertEquals(3, board.getColumnHeight(5));

		assertEquals(4, board.getRowWidth(0));
		assertEquals(1, board.getRowWidth(1));
		assertEquals(1, board.getRowWidth(2));
		assertEquals(0, board.getRowWidth(3));
		assertEquals(0, board.getRowWidth(4));
		assertEquals(0, board.getRowWidth(5));
		assertEquals(0, board.getRowWidth(6));
		assertEquals(0, board.getRowWidth(7));

		assertEquals(3, board.getMaxHeight());
	}

	@Test
	public void undoTest() {
		board.place(stick, 0, 0);
		board.undo();

		for (int i = 0; i < board.getWidth(); i++) {
			assertEquals(0, board.getColumnHeight(i));
		}
		for (int i = 0; i < board.getHeight(); i++) {
			assertEquals(0, board.getRowWidth(i));
		}
		assertEquals(0, board.getMaxHeight());

		board.place(stick, 0, 0);
		board.commit();
		board.undo();

		for (int i = 0; i < board.getWidth(); i++) {
			if (i == 0) {
				assertEquals(4, board.getColumnHeight(i));
			} else {
				assertEquals(0, board.getColumnHeight(i));
			}
		}
		for (int i = 0; i < board.getHeight(); i++) {
			if (i < 4) {
				assertEquals(1, board.getRowWidth(i));
			} else {
				assertEquals(0, board.getRowWidth(i));
			}
		}
		assertEquals(4, board.getMaxHeight());

		board.place(square, 1, 0);
		board.commit();

		board.place(pyramid, 3, 0);
		board.clearRows();
		board.undo();

		for (int i = 0; i < board.getWidth(); i++) {
			if (i == 0) {
				assertEquals(4, board.getColumnHeight(i));
			} else if (i < 3){
				assertEquals(2, board.getColumnHeight(i));
			} else {
				assertEquals(0, board.getColumnHeight(i));
			}
		}
		for (int i = 0; i < board.getHeight(); i++) {
			if (i < 2) {
				assertEquals(3, board.getRowWidth(i));
			} else if (i < 4) {
				assertEquals(1, board.getRowWidth(i));
			} else {
				assertEquals(0, board.getRowWidth(i));
			}
		}
		assertEquals(4, board.getMaxHeight());

		board.place(pyramid, 3, 0);
		board.clearRows();
		board.commit();
		board.undo();

		assertEquals(3, board.getColumnHeight(0));
		assertEquals(1, board.getColumnHeight(1));
		assertEquals(1, board.getColumnHeight(2));
		assertEquals(0, board.getColumnHeight(3));
		assertEquals(1, board.getColumnHeight(4));
		assertEquals(0, board.getColumnHeight(5));

		assertEquals(4, board.getRowWidth(0));
		assertEquals(1, board.getRowWidth(1));
		assertEquals(1, board.getRowWidth(2));
		assertEquals(0, board.getRowWidth(3));
		assertEquals(0, board.getRowWidth(4));
		assertEquals(0, board.getRowWidth(5));
		assertEquals(0, board.getRowWidth(6));
		assertEquals(0, board.getRowWidth(7));

		assertEquals(3, board.getMaxHeight());
	}

	@Test
	public void toStringTest() {
		assertEquals("|      |\n" +
				"|      |\n" +
				"|      |\n" +
				"|      |\n" +
				"|      |\n" +
				"|      |\n" +
				"|      |\n" +
				"|      |\n" +
				"--------", board.toString());

		board.place(stick, 0, 0);
		board.commit();

		assertEquals("|      |\n" +
				"|      |\n" +
				"|      |\n" +
				"|      |\n" +
				"|+     |\n" +
				"|+     |\n" +
				"|+     |\n" +
				"|+     |\n" +
				"--------", board.toString());
	}

	@Test
	public void debugTest() {
		board.turnOffDebugMode();
		board.sanityCheck();

		board.turnOnDebugMode();
		board.sanityCheck();
	}

}
