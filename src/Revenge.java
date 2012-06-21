import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Revenge extends JFrame implements ActionListener {

	private Container c1;
	private JLabel label1;
	private JLabel label2;
	private JButton btnYes;
	private JButton btnNo;

	private Font font;

	private boolean isPushButton = false;

	Revenge() {
		c1 = getContentPane();

		c1.setLayout(new FlowLayout());

		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();

		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		panel2.setLayout(new FlowLayout());

		font = new Font(Font.DIALOG, Font.BOLD, 16);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// ウィンドウを閉じる場合の処理
		setTitle("再戦確認");// ウィンドウのタイトル
		setSize(300, 200);// ウィンドウのサイズを設定

		JLabel empty1 = new JLabel("    ");
		panel1.add(empty1);

		label1 = new JLabel("再戦しますか？");
		label1.setFont(font);
		panel1.add(label1);

		JLabel empty2 = new JLabel("   ");
		panel1.add(empty2);

		c1.add(panel1);

		label2 = new JLabel("<HTML>※どちらか一方が拒否した場合<br>　ログイン画面に戻ります。");
		label2.setFont(font);
		panel1.add(label2);

		btnYes = new JButton("はい");
		btnYes.addActionListener(this);
		panel2.add(btnYes);

		btnNo = new JButton("いいえ");
		btnNo.addActionListener(this);
		panel2.add(btnNo);

		c1.add(panel2);

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		String str = e.getActionCommand();

		if (!isPushButton) {
			if (str.equals("はい")) {
				Client.sendMessage(str);

			} else if (str.equals("いいえ")) {
				Client.sendMessage(str);
			}

			label1.setText("相手の選択を待っています・・・");
			isPushButton = true;
		}
	}

	public void receiveMessage(String message) {
		System.out.println("再戦確認：" + message + "を受信しました。");

		if (message.equals("接続切れ")) {
			Client.sendMessage("戻る");
			JOptionPane.showMessageDialog(null, "相手の接続が切れたのでログイン画面に戻ります");
			setVisible(false);
			Client client = new Client();
			Client.player = new Player();
			Client.nowScene = Client.Scene.login;

		} else {
			if (message.equals("はい")) {
				setVisible(false);
				Client.handiCap = new HandiCap();
				Client.nowScene = Client.Scene.handi;
			} else if (message.equals("いいえ")) {
				setVisible(false);

				// Cliant.player = new Player();
				Client.player.setMyName(null);
				Client.player.setYourName(null);
				new Client();
				Client.nowScene = Client.Scene.login;
			}
		}
	}

	public static void main(String[] args) {
		new Revenge();
	}
}
