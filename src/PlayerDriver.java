
public class PlayerDriver {
	public static void main(String[] args){
		Player player = new Player();
		System.out.println("setMyNameで「電情太郎」を設定する");
		player.setMyName("電情太郎");
		System.out.println("getName出力: " + player.getMyName());
		
		System.out.println("setYourNameで「電情太郎」を設定する");
		player.setYourName("電情次郎");
		System.out.println("getName出力: " + player.getYourName());
		
		System.out.println("isMyTurnにtrueを設定");
		player.setMyTurn(true);
		if(player.isMyTurn()){
			System.out.println("isMyTurnにはtrueが設定されています");
		}
		else{
			System.out.println("isMyTurnにはfalseが設定されています");
		}
		
		System.out.println("isWhiteにfalseを設定");
		player.setWhite(false);
		if(player.isWhite()){
			System.out.println("isWhiteにはtrueが設定されています");
		}
		else{
			System.out.println("isWhiteにはfalseが設定されています");
		}
		
		System.out.println("setHandiで「3」を設定する");
		player.setHandi(3);
		System.out.println("getHandi出力: " + player.getHandi());
		
	}
}
