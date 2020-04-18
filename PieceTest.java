import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class PieceTest {

	private Piece[] pieces;
	private Piece square0, square1;
	private Piece l0, l1, l2, l3, l4;

	@BeforeEach
	protected void setUp() {
		pieces = Piece.getPieces();

		square0 = new Piece(Piece.SQUARE_STR);
		square1 = square0.computeNextRotation();

		l0 = new Piece(Piece.L1_STR);
		l1 = l0.computeNextRotation();
		l2 = l1.computeNextRotation();
		l3 = l2.computeNextRotation();
		l4 = l3.computeNextRotation();

		assertThrows(RuntimeException.class, () -> new Piece("a"));
	}

	@Test
	public void getWidthTest() {
		assertEquals(2, square0.getWidth());
		assertEquals(2, square1.getWidth());

		assertEquals(2, l0.getWidth());
		assertEquals(3, l1.getWidth());
		assertEquals(2, l2.getWidth());
		assertEquals(3, l3.getWidth());
		assertEquals(2, l4.getWidth());
	}

	@Test
	public void getHeightTest() {
		assertEquals(2, square0.getHeight());
		assertEquals(2, square1.getHeight());

		assertEquals(3, l0.getHeight());
		assertEquals(2, l1.getHeight());
		assertEquals(3, l2.getHeight());
		assertEquals(2, l3.getHeight());
		assertEquals(3, l4.getHeight());
	}

	@Test
	public void getSkirtTest() {
		assertArrayEquals(new int[]{0, 0}, square0.getSkirt());
		assertArrayEquals(new int[]{0, 0}, square1.getSkirt());

		assertArrayEquals(new int[]{0, 0},    l0.getSkirt());
		assertArrayEquals(new int[]{0, 0, 0}, l1.getSkirt());
		assertArrayEquals(new int[]{2, 0},    l2.getSkirt());
		assertArrayEquals(new int[]{0, 1, 1}, l3.getSkirt());
		assertArrayEquals(new int[]{0, 0},    l4.getSkirt());
	}

	@Test
	public void equalsTest() {
		assertFalse(square0.equals(null));
		assertFalse(square0.equals(new Piece("")));

		assertTrue(square0.equals(square0));
		assertTrue(square0.equals(square1));

		assertFalse(l0.equals(null));
		assertFalse(l0.equals(new Piece("")));

		assertTrue(l0.equals(l0));
		assertFalse(l0.equals(l1));
		assertFalse(l0.equals(l2));
		assertFalse(l0.equals(l3));
		assertTrue(l0.equals(l4));
	}

	@Test
	public void fastRotationTest() {
		Piece square = pieces[Piece.SQUARE];
		assertTrue(square0.equals(square));
		assertTrue(square1.equals(square.fastRotation()));

		Piece l = pieces[Piece.L1];
		assertTrue(l0.equals(l));
		assertTrue(l1.equals(l.fastRotation()));
		assertTrue(l2.equals(l.fastRotation().fastRotation()));
		assertTrue(l3.equals(l.fastRotation().fastRotation().fastRotation()));
		assertTrue(l4.equals(l.fastRotation().fastRotation().fastRotation().fastRotation()));
	}

}