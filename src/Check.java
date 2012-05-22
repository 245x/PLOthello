import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

public class Check extends JFrame implements ActionListener {
	private JLabel txt1, txt2, txt3, txt4, txt5, txt6;
	private JButton b1, b2;
	private Container c1;

	private String resultCheck = null;

	private boolean isMyTurn;
	private boolean isWhite;

	private static int count = 0;

	private boolean isCountOver = false;

	private String infoMsg;

	private boolean isSendFinish = false;

	private Font font;

	public Check(int myHandi, int yourHandi, int resultHandi) {
		c1 = getContentPane();

		c1.setLayout(new FlowLayout());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// ウィンドウを閉じる場合の処理
		setTitle("確認画面");// ウィンドウのタイトル
		setSize(400, 310);// ウィンドウのサイズを設定

		JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));
		c1.add(panel1);

		font = new Font(Font.DIALOG, Font.BOLD, 22);

		txt1 = new JLabel("自分：  ");
		if (myHandi == 0) {
			txt1.setText(Cliant.player.getMyName() + "：不要");
		} else if (myHandi == 1) {
			txt1.setText(Cliant.player.getMyName() + "：引き分け勝ち");
		} else {
			txt1.setText(Cliant.player.getMyName() + "："
					+ String.valueOf(myHandi - 1) + "子局");
		}
		txt1.setFont(font);
		panel1.add(txt1);

		txt2 = new JLabel("相手：  ");
		if (yourHandi == 0) {
			txt2.setText(Cliant.player.getYourName() + "：不要");
		} else if (yourHandi == 1) {
			txt2.setText(Cliant.player.getYourName() + "：引き分け勝ち");
		} else {
			txt2.setText(Cliant.player.getYourName() + "："
					+ String.valueOf(yourHandi - 1) + "子局");
		}
		txt2.setFont(font);
		panel1.add(txt2);

		JLabel empty1 = new JLabel(" ");
		panel1.add(empty1);

		txt3 = new JLabel("結果");
		if (resultHandi == 0) {
			infoMsg = "ハンデなしで対局します。";
			txt3.setText("ハンデなしで対局します。よろしいですか？");
		} else if (resultHandi == 1) {
			infoMsg = "引き分け時にあなたの勝利となります。";
			txt3.setText("引き分け時にあなたの勝利となります。よろしいですか？");
		} else if (resultHandi == -1) {
			infoMsg = "引き分け時に相手の勝利となります。";
			txt3.setText("引き分け時に相手の勝利となります。よろしいですか？");
		} else if (resultHandi > 1) {
			infoMsg = "あなたに" + String.valueOf(resultHandi - 1)
					+ "子のハンデが与えられます。";
			txt3.setText("あなたに" + String.valueOf(resultHandi - 1)
					+ "子のハンデが与えられます。よろしいですか？");
		} else if (resultHandi < -1) {
			infoMsg = "相手に" + String.valueOf(-1 * resultHandi - 1)
					+ "子のハンデが与えられます。";
			txt3.setText("相手に" + String.valueOf(-1 * resultHandi - 1)
					+ "子のハンデが与えられます。よろしいですか？");
		}
		panel1.add(txt3);

		JLabel empty2 = new JLabel(" ");
		panel1.add(empty2);

		txt4 = new JLabel("<HTML>どちらか一方が「いいえ」を選んだ場合は<br>   ハンデ選択画面に戻ります。");
		panel1.add(txt4);
		JLabel empty4 = new JLabel("  ");
		panel1.add(empty4);

		txt5 = new JLabel(
				"<HTML>※ハンデについての同意が3回連続で得られなかった場合は<br>   ハンデなしで開始します。");
		panel1.add(txt5);

		JLabel empty3 = new JLabel(" ");
		panel1.add(empty3);

		txt6 = new JLabel("ハンデの希望はあと" + String.valueOf(2 - count++) + "回出せます。");
		panel1.add(txt6);

		JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout());
		c1.add(panel2);

		b1 = new JButton("はい");
		b2 = new JButton("いいえ");

		b1.addActionListener(this);
		b2.addActionListener(this);

		panel2.add(b1);
		panel2.add(b2);

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		String str = e.getActionCommand();

		if (!isSendFinish) {
			if (str.equals("はい")) {
				Cliant.sendMessage("はい");
				isSendFinish = true;

			} else if (str.equals("いいえ")) {
				Cliant.sendMessage("いいえ");
				isSendFinish = true;
			}

			txt3.setText("相手の選択を待っています・・・");
		}

	}

	public void receiveMessage(String message) {
		System.out.println("確認画面：" + message + "を受信しました");

		if (message.equals("接続切れ")) {
			Cliant.sendMessage("戻る");
			JOptionPane.showMessageDialog(null, "相手の接続が切れたのでログイン画面に戻ります");
			setVisible(false);
			count = 0;
			new Cliant();
			Cliant.player = new Player();
			Cliant.nowScene = Cliant.Scene.login;

		} else {
			if (resultCheck == null) {
				if (message.equals("はい")) {
					resultCheck = message;
				} else if (message.equals("いいえ") && count < 3) {
					setVisible(false);
					Cliant.handiCap = new HandiCap();
					Cliant.nowScene = Cliant.Scene.handi;
				} else if (message.equals("いいえ") && count >= 3) {
					isCountOver = true;
					resultCheck = message;
				}

			} else {
				/*
				 * +がハンデを受け取る側、つまり後手(白) -がハンデを渡す側、つまり先手(黒) ハンデなしなら+0,-0を受信
				 * +が黒、-が白で統一 +0なら黒の先手、-0なら白の後手
				 */
				int handi = Integer.parseInt(message.substring(1));

				if (message.equals("+0")) {
					handi = 0;

				} else if (message.equals("-0")) {
					handi = 0;

				}

				if (message.charAt(0) == '+') {
					isWhite = false;
				} else if (message.charAt(0) == '-') {
					isWhite = true;
				}

				if (message.charAt(1) == '0') {
					if (isWhite) {
						isMyTurn = false;
					} else {
						isMyTurn = true;
					}
				} else {
					if (isWhite) {
						isMyTurn = true;
					} else {
						isMyTurn = false;
					}
				}
				Cliant.player.setWhite(isWhite);
				Cliant.player.setMyTurn(isMyTurn);
				Cliant.player.setHandi(handi);

				if (isCountOver) {
					JOptionPane.showMessageDialog(this, "ハンデについての同意が３回連続で"
							+ "\n得られなかったため、ハンデなし戦となります。");
				} else {
					JOptionPane.showMessageDialog(this, infoMsg);
				}
				count = 0;

				setVisible(false);
				Cliant.othello = new Othello(handi);
				Cliant.nowScene = Cliant.Scene.othello;
			}
		}
	}

	public static void main(String[] args) {
		Cliant cliant = new Cliant();
		cliant.player = new Player();
		cliant.setVisible(false);
		new Check(0, 0, 0);
	}
}