public class Player {

	private String myName = null;
	private String yourName = null;
	private boolean isWhite;
	private boolean isMyTurn;
	private int handi;

	public Player() {
		setWhite(true);
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}

	public String getMyName() {
		return myName;
	}

	public void setYourName(String yourName) {
		this.yourName = yourName;
	}

	public String getYourName() {
		return yourName;
	}

	public boolean isWhite() {
		return isWhite;
	}

	public void setWhite(boolean isWhite) {
		this.isWhite = isWhite;
	}

	public void setMyTurn(boolean isMyTurn) {
		this.isMyTurn = isMyTurn;
	}

	public boolean isMyTurn() {
		return isMyTurn;
	}

	public void setHandi(int handi) {
		this.handi = handi;
	}

	public int getHandi() {
		return handi;
	}
}
