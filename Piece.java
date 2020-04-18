import java.util.*;

public class Piece {

	private TPoint[] body;
	private int[] skirt;
	private int width;
	private int height;
	private Piece next;

	static private Piece[] pieces;

	public Piece(TPoint[] points) {
		body = Arrays.copyOf(points, points.length);
		computeSizes();
		createSkirt();
	}

	private void computeSizes() {
		for (TPoint tp : body) {
			width = Math.max(width, tp.x + 1);
			height = Math.max(height, tp.y + 1);
		}
	}

	private void createSkirt() {
		skirt = new int[width];
		Arrays.fill(skirt, height);
		for (TPoint tp : body) {
			skirt[tp.x] = Math.min(skirt[tp.x], tp.y);
		}
	}

	public Piece(String points) {
		this(parsePoints(points));
	}

	public TPoint[] getBody() {
		return body;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int[] getSkirt() {
		return skirt;
	}

	public Piece computeNextRotation() {
		TPoint[] rotatedPoints = new TPoint[body.length];
		for (int i = 0; i < body.length; i++) {
			TPoint tp = body[i];
			rotatedPoints[i] = rotatedPoint(tp);
		}
		return new Piece(rotatedPoints);
	}

	private TPoint rotatedPoint(TPoint tp) {
		int x = height - tp.y - 1;
		int y = tp.x;
		return new TPoint(x, y);
	}

	public Piece fastRotation() {
		return next;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Piece)) return false;
		if (obj == this) return true;

		Piece other = (Piece)obj;

		ArrayList<TPoint> body1 = new ArrayList<>(Arrays.asList(this.getBody()));
		ArrayList<TPoint> body2 = new ArrayList<>(Arrays.asList(other.getBody()));

		return (body1.containsAll(body2) && body2.containsAll(body1));
	}

	public static final int STICK	= 0;
	public static final int L1		= 1;
	public static final int L2		= 2;
	public static final int S1		= 3;
	public static final int S2		= 4;
	public static final int SQUARE	= 5;
	public static final int PYRAMID	= 6;

	public static final String STICK_STR	= "0 0	0 1	 0 2  0 3";
	public static final String L1_STR		= "0 0	0 1	 0 2  1 0";
	public static final String L2_STR		= "0 0	1 0  1 1  1 2";
	public static final String S1_STR		= "0 0	1 0	 1 1  2 1";
	public static final String S2_STR		= "0 1	1 1  1 0  2 0";
	public static final String SQUARE_STR	= "0 0  0 1  1 0  1 1";
	public static final String PYRAMID_STR	= "0 0  1 0  1 1  2 0";

	public static Piece[] getPieces() {
		if (Piece.pieces == null) {
			Piece.pieces = new Piece[] {
				makeFastRotations(new Piece(STICK_STR)),
				makeFastRotations(new Piece(L1_STR)),
				makeFastRotations(new Piece(L2_STR)),
				makeFastRotations(new Piece(S1_STR)),
				makeFastRotations(new Piece(S2_STR)),
				makeFastRotations(new Piece(SQUARE_STR)),
				makeFastRotations(new Piece(PYRAMID_STR)),
			};
		}
		return Piece.pieces;
	}

	private static Piece makeFastRotations(Piece root) {
		Piece currRotation = root;
		Piece nextRotation = root.computeNextRotation();
		while (true) {
			if (nextRotation.equals(root)) {
				currRotation.next = root;
				break;
			}
			currRotation.next = nextRotation;

			currRotation = nextRotation;
			nextRotation = nextRotation.computeNextRotation();
		}
		return root;
	}

	private static TPoint[] parsePoints(String string) {
		List<TPoint> points = new ArrayList<>();
		StringTokenizer tok = new StringTokenizer(string);
		try {
			while(tok.hasMoreTokens()) {
				int x = Integer.parseInt(tok.nextToken());
				int y = Integer.parseInt(tok.nextToken());
				points.add(new TPoint(x, y));
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException("Could not parse x,y string:" + string);
		}

		TPoint[] array = points.toArray(new TPoint[0]);
		return array;
	}

}
