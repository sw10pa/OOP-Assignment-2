import java.util.*;

public class Board {

	private int width;
	private int height;

	private boolean[][] grid;
	private int[] widths;
	private int[] heights;
	private int maxHeight;

	private boolean[][] backupGrid;
	private int[] backupWidths;
	private int[] backupHeights;
	private int backupMaxHeight;

	private boolean committed = true;
	private boolean DEBUG = true;

	public Board(int width, int height) {
		this.width = width;
		this.height = height;

		grid = new boolean[width][height];
		widths = new int[height];
		heights = new int[width];
		maxHeight = 0;

		backupGrid = new boolean[width][height];
		backupWidths = new int[height];
		backupHeights = new int[width];
		backupMaxHeight = 0;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void sanityCheck() {
		if (DEBUG) {
			int maxHeightChecker = 0;
			int[] widthsChecker = new int[height];
			int[] heightsChecker = new int[width];

			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					if (isFilled(i, j)) {
						widthsChecker[j]++;
						heightsChecker[i] = j + 1;
					}
				}
				maxHeightChecker = Math.max(maxHeightChecker, heightsChecker[i]);
			}

			if (maxHeightChecker != maxHeight)				throw new RuntimeException("Incorrect maxHeight!");
			if (!Arrays.equals(widthsChecker, widths))		throw new RuntimeException("Incorrect widths!");
			if (!Arrays.equals(heightsChecker, heights))	throw new RuntimeException("Incorrect heights!");
		}
	}

	public int dropHeight(Piece piece, int x) {
		int dropY = 0;
		for (int i = 0; i < piece.getWidth(); i++) {
			int currY = heights[x + i] - piece.getSkirt()[i];
			dropY = Math.max(dropY, currY);
		}
		return dropY;
	}

	public int getColumnHeight(int x) {
		return heights[x];
	}

	public int getRowWidth(int y) {
		 return widths[y];
	}

	public boolean getGrid(int x, int y) {
		return (!inBounds(x, y) || isFilled(x, y));
	}

	public static final int PLACE_OK		 = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD		 = 3;

	public int place(Piece piece, int x, int y) {
		if (!committed) throw new RuntimeException("The board must be in the committed state before place() is called!");

		int status = PLACE_OK;
		committed = false;
		backup();

		for (TPoint tp : piece.getBody()) {
			int placeX = tp.x + x;
			int placeY = tp.y + y;

			if (!inBounds(placeX, placeY)) return PLACE_OUT_BOUNDS;
			if ( isFilled(placeX, placeY)) return PLACE_BAD;

			grid[placeX][placeY] = true;

			widths[placeY]++;
			heights[placeX] = Math.max(heights[placeX], placeY + 1);
			maxHeight = Math.max(maxHeight, heights[placeX]);

			if (widths[placeY] == width) status = PLACE_ROW_FILLED;
		}

		sanityCheck();
		return status;
	}

	public int clearRows() {
		if (committed) backup();
		int rowsCleared = 0;
		committed = false;

		int toRow, fromRow;

		for (toRow = 0; toRow < maxHeight; toRow++) {
			if (widths[toRow] == width) break;
		}

		for (fromRow = toRow; fromRow < maxHeight; fromRow++) {
			if (widths[fromRow] < width) {
				for (int i = 0; i < width; i++) {
					grid[i][toRow] = grid[i][fromRow];
				}
				widths[toRow] = widths[fromRow];
				toRow++;
			} else {
				rowsCleared++;
			}
		}

		for (int i = 0; i < width; i++) {
			Arrays.fill(grid[i], toRow, height,false);
		}
		Arrays.fill(widths, toRow, height, 0);

		updateHeights();

		sanityCheck();
		return rowsCleared;
	}

	public void undo() {
		if (committed) return;

		boolean[][] tempGrid = grid;
		grid = backupGrid;
		backupGrid = tempGrid;

		int[] tempWidths = widths;
		widths = backupWidths;
		backupWidths = tempWidths;

		int[] tempHeights = heights;
		heights = backupHeights;
		backupHeights = tempHeights;

		int tempMaxHeight = maxHeight;
		maxHeight = backupMaxHeight;
		backupMaxHeight = tempMaxHeight;

		commit();
	}

	public void commit() {
		committed = true;
	}

	public void turnOnDebugMode() {
		DEBUG = true;
	}

	public void turnOffDebugMode() {
		DEBUG = false;
	}

	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height - 1; y >= 0; y--) {
			buff.append('|');
			for (int x = 0; x < width; x++) {
				if (getGrid(x,y)) {
					buff.append('+');
				} else {
					buff.append(' ');
				}
			}
			buff.append("|\n");
		}
		for (int x = 0; x < width + 2; x++) buff.append('-');
		return buff.toString();
	}

	private void backup() {
		for (int i = 0; i < width; i++) System.arraycopy(grid[i], 0, backupGrid[i], 0, height);
		System.arraycopy(widths, 0, backupWidths, 0, height);
		System.arraycopy(heights, 0, backupHeights, 0, width);
		backupMaxHeight = maxHeight;
	}

	private void updateHeights() {
		maxHeight = 0;
		for (int i = 0; i < width; i++) {
			for(int j = heights[i] - 1; j >= 0; j--) {
				if(isFilled(i, j)) {
					heights[i] = j + 1;
					break;
				}
				if (j == 0) heights[i] = 0;
			}
			maxHeight = Math.max(maxHeight, heights[i]);
		}
	}

	private boolean inBounds(int x, int y) {
		return (x >= 0 && x < width && y >= 0 && y < height);
	}

	private boolean isFilled(int x, int y) {
		return grid[x][y];
	}

}
