import java.net.*;
import java.io.*;

public class Server {
	private int port; //サーバの待ち受けポート
	private boolean[] online; // オンライン状態管理用配列
	private PrintWriter[] out; // データ送信用オブジェクト
	private Receiver[] receiver; //データ受信用オブジェクト
	private int state = 0; //画面の遷移状態チェック
	private int n = 0; //playerNo用int
	private String[] name; //ユーザ名保持
	private int count; //ハンデ折衝回数
	private String[] handicap; //ハンデ選択
	private String[] choose; //ハンデ確認、再戦での選択(選択中・する・しない)
	
	// コンストラクタ
	public Server(int port) {
		this.port = port;
		initialize();
	}
	
	//属性初期化
	public void initialize(){
		out = new PrintWriter[2];
		receiver = new Receiver[2];
		online = new boolean[2];
		name = new String[2];
		handicap = new String[2];
		choose = new String[2];
		for(int i = 0; i<2;i++){
			n=0;
			online[i] = false;
			name[i] = handicap[i] = choose[i] = null;
			state = 0;
			count = 2;
		}
	}
	//------------------------------
	//データ受信用スレッド(内部クラス)
	class Receiver extends Thread{
		private InputStreamReader isr;
		private BufferedReader br;
		private int playerNo; //プレイヤ識別番号
		private boolean move;	//先手後手
		public boolean blinker;
		//コンストラクタ
		Receiver(Socket sock, int playerNo){
			try{
				this.playerNo = playerNo;
				isr = new InputStreamReader(sock.getInputStream());
				br = new BufferedReader(isr);
				move = true;
			}catch(IOException e){
				System.err.println("データ受信時エラー:" + e);
			}
		}
		
		// メソッド
		public void run() { // Thread拡張
			try {
				while (true) { // データ受信継続
					if (state != 3)
						printStatus();
					String inputLine = br.readLine();// 一行読み込み
					if (inputLine != null) {
						/* 状態で実行関数の切り替え */
						if (inputLine.equals("戻る")) {
							online[playerNo] = false;
							printStatus();
							initialize();
							break;
						}
						switch (state) {
						case 0: // クライアントログイン画面で受信
							logIn(inputLine, playerNo);
							break;
						case 1: // クライアント折衝画面で受信
							handi(inputLine, playerNo);
							break;
						case 2: // クライアントハンデ確認画面で受信
							choose(inputLine, playerNo);
							break;
						case 3: { // クライアント対局画面で受信
							if (move) {
								forwardMessage(inputLine, playerNo); // もう一方に転送
								move = false;
							}
							break;
						}
						case 4: // クライアント再戦画面で受信
							choose(inputLine, playerNo % 2);
							break;
						}
						stateChange(); // state変更用関数
					}

				}
			} catch (IOException e) {// 接続切れ
				System.err.println("プレイヤ" + playerNo + "の接続が切れました.");
				online[playerNo] = false;
				printStatus();
				if (online[(playerNo + 1) % 2]) {// 相手が接続状態なら(名前入力が済んでいれば)
					statusCut(playerNo); // 接続切れをを相手に出力
				} else {// 相手が接続していなければ
					initialize();// 初期化
				}
			}
		}
	}


	// -----------------------------------

	// Serverメソッド
	public void acceptClient() { // クライアントの接続(サーバ起動)
		try {
			System.out.println("サーバ起動.");
			ServerSocket ss = new ServerSocket(port); // サーバソケットを用意
			while (true) {
				Socket sock = ss.accept(); // 新規接続を受け付け
				if (online[0]) {
					n = 1;
				}
				online[n] = true;
				//System.out.println("クライアント" + n + "と接続しました.");// テスト用出力
				receiver[n] = new Receiver(sock, n); // ソケットとNoで受信用意
				out[n] = new PrintWriter(sock.getOutputStream(), true);
				receiver[n].start();
			}
		} catch (Exception e) {
			System.err.println("ソケット作成時エラー:" + e);
		}
	}


	// ----------------------------------------
	
	public void stateChange() { // state変更・送信など
		if (state == 0 && name[0] != null && name[1] != null) { // どちらもログインしていたら
			stateChangeMessage(name[1], name[0]); // 互いの名前を交換
			state = 1; // ハンデ折衝に移行

		}
		if (state == 1 && handicap[0] != null && handicap[1] != null) {// どちらもハンデ選択がすんだら
			stateChangeMessage(handicap[1], handicap[0]);
			state = 2; // 確認画面に移行

		}
		if (state == 2 && choose[0] != null && choose[1] != null) { // 確認が済んだら
			if (choose[0].equals("はい") && choose[1].equals("はい")) { // どちらも了承したら
				stateChangeMessage("はい", "はい");
				checkhandi(); // 折衝結果を出力
				state = 3; // 対局に移行

			} else if (choose[0].equals("いいえ") || choose[1].equals("いいえ")) { // どちらかが拒否したら
				stateChangeMessage("いいえ", "いいえ");
				
				if (count != 0) { // 2回以内なら
					handicap[0] = handicap[1] = choose[0] = choose[1] = null; // ハンデ情報・選択初期化
					state = 1; // ハンデ選択画面に戻る
					count--;
					
				} else if (count == 0) { // 3回目ならハンデなし戦
					handicap[0] = handicap[1] = "0"; // ハンデ無しで対局
					checkhandi();
					state = 3;
				}
			}
		}
		if (state == 4 && choose[0] != null && choose[1] != null) { // 再戦確認
			if (choose[0].equals("はい") && choose[1].equals("はい")) { // どちらも希望
				stateChangeMessage("はい", "はい");
				state = 1; // ハンデ折衝に移行
				handicap[0] = handicap[1] = choose[0] = choose[1] = null; // 初期化
			} else { // どちらかが再戦拒否
				stateChangeMessage("いいえ", "いいえ");
				initialize();
			}
		}
	}
	
	// --------------------------------------------
	// 出力用メソッド
	public void stateChangeMessage(String msg0, String msg1) { // 引数の文字列をクライアントに返す
		out[0].println(msg0);
		out[0].flush();
		System.out.println("クライアント0に" + msg0 + "を送信します");
		out[1].println(msg1);
		out[1].flush();
		System.out.println("クライアント1に" + msg1 + "を送信します");
	}
	
	// クライアント接続状態
	public void printStatus(){
		String msg;
		for(int i = 0;i<2;i++){
			if(online[i] == true && name[i] != null)
				msg = "接続中.";
			else
				msg = "接続なし.";
			System.out.println("クライアント "+ i + " "+ msg);
		}
	}
	
	//接続切れ
	public void statusCut(int No) {
		int opponentNo = (No+1) % 2; // 相手playerNo(0or1)
			out[opponentNo].println("接続切れ");
			out[opponentNo].flush();
	}
	
	/*
	 * +0は黒、先手 -0は白、後手 +1は黒、後手 ・・・ -1は白、先手 ・・・
	 */
	// ハンデ折衝・出力/+は後手、-は先手
	public void checkhandi() {
		if ((!handicap[0].equals("0") && !handicap[1].equals("0"))
				|| (handicap[0].equals("0") && handicap[1].equals("0"))) { // どっちも0じゃない、つまりハンデ希望するならハンデなし戦
			if (receiver[0].playerNo < receiver[1].playerNo) { // 数が小さいほうが黒でハンデ無し戦
				stateChangeMessage("+0", "-0");
				System.out.println("+0と-0を送信しました.");
				receiver[1].move = false; // receiver[0]は黒で先手
				
			} else {
				stateChangeMessage("-0", "+0");
				System.out.println("-0と+0を送信しました.");
				receiver[0].move = false;	// receiver[0]は白で後手
			}
			
		} else if (handicap[0].equals("0")) { // 片方が不要なら,receiver[0]はハンデを与える白：先手
			stateChangeMessage("-" + handicap[1], "+" + handicap[1]);
			System.out.println("+"+handicap[1]+"と-"+handicap[1]+"を送信しました.");
			receiver[1].move = false;	//ハンデをもらう方は後手
			
		} else if (handicap[1].equals("0")) {
			stateChangeMessage("+" + handicap[0], "-" + handicap[0]);
			System.out.println("+"+handicap[0]+"と-"+handicap[0]+"を送信しました.");
			receiver[0].move = false;
		}
		System.out.println("ハンデの折衝を終了しました.対局に移行します.");
		choose[0] = choose[1] = null;
		count = 2;
	}
	
	
	//---------------------------------------
	//受信用メソッド
	//(0)ログイン画面
	public void logIn(String msg, int No){
		name[No] = msg;
		System.out.println("クライアント"+No+"からユーザ名を受信しました.");
		online[No] = true;	//名前取得時に接続したことにする
	}
	
	//(1)ハンデ折衝
	public void handi(String msg, int No){
		handicap[No] = msg; //ハンデ格納
		System.out.println("クライアント"+No+"からハンデ情報を受信しました.");
	}
	
	//(2,4)折衝確認,再戦確認
	public void choose(String msg,int No){
		choose[No] = msg;	//選択格納
		System.out.println("クライアント"+No+"から確認選択を受信しました.");
		System.out.println("テスト用出力："+choose[No]);
	}
	
	// (3)操作情報の転送
	public void forwardMessage(String msg, int No) {
		out[(No + 1) % 2].println(msg);
		out[(No + 1) % 2].flush(); // 相手にそのまま転送
		if (msg.equals("終了")) { // 投了・終了判定
			System.out.println("対局を終了します.");
			state = 4;
		}
		receiver[(No + 1) % 2].move = true;
	}
	
	//main
	public static void main(String[] args){
		Server server = new Server(10000); //待ち受けポート
		server.acceptClient(); //クライアント受け入れを開始
	}
}
