
public class PlayerDriver {
	public static void main(String[] args){
		Player player = new Player();
		System.out.println("setMyName�Łu�d��Y�v��ݒ肷��");
		player.setMyName("�d��Y");
		System.out.println("getName�o��: " + player.getMyName());
		
		System.out.println("setYourName�Łu�d��Y�v��ݒ肷��");
		player.setYourName("�d��Y");
		System.out.println("getName�o��: " + player.getYourName());
		
		System.out.println("isMyTurn��true��ݒ�");
		player.setMyTurn(true);
		if(player.isMyTurn()){
			System.out.println("isMyTurn�ɂ�true���ݒ肳��Ă��܂�");
		}
		else{
			System.out.println("isMyTurn�ɂ�false���ݒ肳��Ă��܂�");
		}
		
		System.out.println("isWhite��false��ݒ�");
		player.setWhite(false);
		if(player.isWhite()){
			System.out.println("isWhite�ɂ�true���ݒ肳��Ă��܂�");
		}
		else{
			System.out.println("isWhite�ɂ�false���ݒ肳��Ă��܂�");
		}
		
		System.out.println("setHandi�Łu3�v��ݒ肷��");
		player.setHandi(3);
		System.out.println("getHandi�o��: " + player.getHandi());
		
	}
}
