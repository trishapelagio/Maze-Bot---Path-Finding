public class Point { // This class stores the coordinates of a space
	public final int x;
	public final int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof Point) {
			Point pt = (Point)other;
			return pt.x == x && pt.y == y;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return x * 3 + y * 257;
	} // Increases efficiency in comparison
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}