//パッケージのインポート
import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

public class HandiCap extends JFrame implements ActionListener {
	private JLabel player1, player2;
	private JLabel txt1, txt2;
	private JLabel b1, b2, b3, b4, b5;
	private JRadioButton[] radio;
	private JButton button;
	private Container c1;

	private Font nameFont;

	private int myHandi = -1;
	private int yourHandi = -1;
	private int resultHandi = -1;

	public HandiCap() {
		c1 = getContentPane();

		c1.setLayout(new FlowLayout());
		// c1.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		c1.add(panel);

		radio = new JRadioButton[6];

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// ウィンドウを閉じる場合の処理
		setTitle("ハンディキャップ");// ウィンドウのタイトル
		setSize(420, 460);// ウィンドウのサイズを設定

		nameFont = new Font(Font.DIALOG, Font.BOLD, 20);

		player1 = new JLabel(" ユーザ名(自分):" + Cliant.player.getMyName());
		player1.setFont(nameFont);
		player2 = new JLabel(" ユーザ名(相手):" + Cliant.player.getYourName());
		player2.setFont(nameFont);

		panel.add(player1);
		panel.add(player2);

		JLabel emptyLabel1 = new JLabel("   ");
		panel.add(emptyLabel1);

		txt1 = new JLabel("    あなたのハンデを選択してください");
		panel.add(txt1);

		JLabel emptyLabel2 = new JLabel("   ");
		panel.add(emptyLabel2);

		radio[0] = new JRadioButton("不要", true);

		radio[1] = new JRadioButton("引き分け勝ち");
		b1 = new JLabel("　－石の数が同じときあなたの勝利");

		radio[2] = new JRadioButton("1子局");
		b2 = new JLabel("　－左上の隅にあなたの石を置いて対局を開始する。");

		radio[3] = new JRadioButton("2子局");
		b3 = new JLabel("　－左上と右下の隅にあなたの石 を置いて対局を開始する。");

		radio[4] = new JRadioButton("3子局");
		b4 = new JLabel("　－左上と右下、右上の隅にあなたの石を置いて対局を開始する。");

		radio[5] = new JRadioButton("4子局");
		b5 = new JLabel("　－4ヶ所全ての隅にあなたの石を置いて対局を開始する。");

		ButtonGroup gr = new ButtonGroup();
		gr.add(radio[0]);
		gr.add(radio[1]);
		gr.add(radio[2]);
		gr.add(radio[3]);
		gr.add(radio[4]);
		gr.add(radio[5]);

		panel.add(radio[0]);

		panel.add(radio[1]);
		panel.add(b1);
		panel.add(radio[2]);
		panel.add(b2);
		panel.add(radio[3]);
		panel.add(b3);
		panel.add(radio[4]);
		panel.add(b4);
		panel.add(radio[5]);
		panel.add(b5);

		JLabel emptyLabel3 = new JLabel("   ");
		panel.add(emptyLabel3);

		txt2 = new JLabel("   ※どちらもハンデを希望する場合ハンデなしとする。");
		panel.add(txt2);

		JLabel emptyLabel4 = new JLabel("   ");
		panel.add(emptyLabel4);

		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		c1.add(panel2);

		button = new JButton("OK");
		button.addActionListener(this);
		// button.setBounds(0, 0, 150, 40);
		panel2.add(button);

		setVisible(true);
		setResizable(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		String str = e.getActionCommand();
		int i;

		if (str.equals("OK")) {
			for (i = 0; i < radio.length; i++) {
				if (radio[i].isSelected()) {
					break;
				}
			}

			if (myHandi == -1) {
				myHandi = i;
				Cliant.sendMessage(String.valueOf(i));
				txt1.setText("    相手のハンデ選択を待っています・・・");
			}

			/*
			 * if(myHandi != -1 && yourHandi != -1){ if(myHandi == 0){
			 * resultHandi = yourHandi; } else if(yourHandi == 0){ resultHandi =
			 * myHandi; } else{ resultHandi = 0; }
			 * 
			 * setVisible(false); Cliant.check = new Check(myHandi, yourHandi,
			 * resultHandi); }
			 */
		}
	}

	public void receiveMessage(String message) {
		System.out.println("ハンデ選択画面：" + message + "を受信しました");

		if (message.equals("接続切れ")) {
			Cliant.sendMessage("戻る");
			JOptionPane.showMessageDialog(null, "相手の接続が切れたのでログイン画面に戻ります");
			setVisible(false);
			Cliant cliant = new Cliant();
			Cliant.player = new Player();
			Cliant.nowScene = Cliant.Scene.login;

		} else {
			if (yourHandi == -1) {
				yourHandi = Integer.parseInt(message);
			}

			if (myHandi != -1 && yourHandi != -1) {
				if (myHandi == 0) {
					resultHandi = -1 * yourHandi;
				} else if (yourHandi == 0) {
					resultHandi = myHandi;
				} else {
					resultHandi = 0;
				}

				setVisible(false);
				Cliant.check = new Check(myHandi, yourHandi, resultHandi);
				Cliant.nowScene = Cliant.Scene.check;
			}
		}
	}

	public static void main(String[] args) {
		Cliant cliant = new Cliant();
		cliant.player = new Player();
		cliant.setVisible(false);
		new HandiCap();
	}
}