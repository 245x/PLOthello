import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends JFrame implements ActionListener {
	enum Scene {
		login, handi, check, othello, revenge
	};

	static HandiCap handiCap;
	static Player player;
	static Othello othello;
	static Check check;
	static Revenge revenge;

	static Scene nowScene = Scene.login;

	private static Container c;
	private static JTextField field;
	private JLabel label1;
	private JLabel label2;

	private static String ipAddress;

	private Receiver receiver; // データ受信用オブジェクト
	private static PrintWriter out;// データ送信用オブジェクト

	private Font font;
	private Font emptyFont;

	public Client() {
		c = getContentPane();

		Panel panel1 = new Panel();
		panel1.setLayout(null);

		c.add(panel1);

		font = new Font(Font.DIALOG, Font.PLAIN, 16);
		emptyFont = new Font(Font.DIALOG, Font.PLAIN, 10);

		label2 = new JLabel("名前を入力してください");
		label2.setFont(font);
		label2.setBounds(20, 10, 280, 30);
		panel1.add(label2);

		field = new JTextField(6);
		field.setBounds(10, 50, 260, 30);
		panel1.add(field);

		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(this);
		btnOK.setBounds(50, 90, 70, 30);
		panel1.add(btnOK);

		JButton btnCancel = new JButton("取消");
		btnCancel.addActionListener(this);
		btnCancel.setBounds(150, 90, 70, 30);
		panel1.add(btnCancel);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 170);
		setResizable(false);
		setTitle("ログイン認証");
		setVisible(true);

		connectServer(ipAddress, 10000);
	}

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		ipAddress = args[0];
		player = new Player();

		Client client = new Client();
	}

	public void connectServer(String ipAddress, int port) { // サーバに接続
		Socket socket = null;
		try {
			socket = new Socket(ipAddress, port); // サーバ(ipAddress, port)に接続
			out = new PrintWriter(socket.getOutputStream(), true); // データ送信用オブジェクトの用意
			receiver = new Receiver(socket); // 受信用オブジェクトの準備
			receiver.start();
		} catch (UnknownHostException e) {
			System.err.println("ホストのIPアドレスが判定できません: " + e);
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("サーバ接続時にエラーが発生しました: " + e);
			System.exit(-1);
		}
	}

	public static void sendMessage(String msg) { // サーバに操作情報を送信
		out.println(msg);// 送信データをバッファに書き出す
		out.flush();// 送信データを送る
		System.out.println("サーバにメッセージ " + msg + " を送信しました"); // テスト標準出力
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		String str = e.getActionCommand();

		if (str.equals("OK")) {
			if (player.getMyName() == null) {
				if (field.getText().equals("")) {
					player.setMyName("No Name");
					sendMessage("No Name");
				} else {
					player.setMyName(field.getText());
					sendMessage(field.getText());
				}
				label2.setText("相手の接続を待っています・・・");
			}
			if (player.getMyName() != null && player.getYourName() != null) {
				setVisible(false);
				handiCap = new HandiCap();
			}
			
		} else if (str.equals("取消")) {
			System.exit(NORMAL);
		}
	}

	public void receiveMessage(String message) {
		System.out.println("ログイン画面：" + message + "を受信しました");

		if (message.equals("接続切れ")) {
			JOptionPane.showMessageDialog(null, "相手の接続が切れたのでログイン画面に戻ります");
		} else {
			player.setYourName(message);

			if (player.getMyName() != null && player.getYourName() != null) {
				setVisible(false);
				handiCap = new HandiCap();
				nowScene = Scene.handi;
			}
		}
	}

	public class Receiver extends Thread {
		private InputStreamReader sisr; // 受信データ用文字ストリーム
		private BufferedReader br; // 文字ストリーム用のバッファ

		private String message = null;

		Receiver(Socket socket) {
			try {
				sisr = new InputStreamReader(socket.getInputStream()); // 受信したバイトデータを文字ストリームに
				br = new BufferedReader(sisr);// 文字ストリームをバッファリングする
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}

		public void run() { // メッセージの受信
			String inputLine = null;
			try {
				while (true) {// データを受信し続ける
					inputLine = br.readLine();// 受信データを一行分読み込む

					if (inputLine != null) {
						message = inputLine;

						switch (nowScene) {
						case login:
							receiveMessage(message);
							break;
						case handi:
							handiCap.receiveMessage(message);
							break;
						case check:
							check.receiveMessage(message);
							break;
						case othello:
							othello.receiveMessage(message);
							break;
						case revenge:
							revenge.receiveMessage(message);
							break;
						}
					}
				}
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
	}
}
