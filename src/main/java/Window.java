import com.sun.jna.platform.win32.WinDef.HWND;

public class Window {
	public HWND hWnd;

	public String wText;
	public int x;
	public int y;
	public int width;
	public int height;

	public Window() {
	}

	public Window(HWND hWnd, String wText) {
		this.hWnd = hWnd;
		this.wText = wText;
	}

	public Window(String wText) {
		this.hWnd = null;
		this.wText = wText;
	}

	public void setPos(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public String getwText() {
		return wText;
	}

	public void setwText(String wText) {
		this.wText = wText;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public String toString() {
		return this.wText + " - x: " + this.x + " y: " + this.y + " width: " + this.width + " height: " + this.height;
	}
}
