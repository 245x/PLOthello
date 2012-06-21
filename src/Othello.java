import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Othello extends JFrame implements MouseListener, ActionListener {
	private int[] field; // 1が黒,2が白
	private static int row = 8; // マス目

	private JButton[] buttonArray;
	private ImageIcon blackIcon, whiteIcon, boardIcon;

	private Container c;
	private Container cOthello;
	private JPanel panel1;
	private JPanel panel2;

	private JTextArea logArea;
	private Color color;
	private Color yellowColor;

	private boolean[] isCanPutField;
	private boolean[] isReverseField;

	private int tempx;
	private int tempy;

	private boolean isMyPass = false;
	private boolean isFinish;
	private int countBlack;
	private int countWhite;

	private Font font;

	public void checkCanPutField() {
		for (int i = 0; i < row * row; i++) {
			if (isCanPutField[i]) {
				System.out.printf("1");
			} else {
				System.out.printf("0");
			}

			if (i % row == row - 1) {
				System.out.printf("\n");
			}
		}
	}

	Othello(int handi) {

		field = new int[row * row];
		isCanPutField = new boolean[row * row];
		isReverseField = new boolean[row * row];

		switch (Client.player.getHandi()) {
		case 2:
			field[0] = 1;
			break;
		case 3:
			field[0] = 1;
			field[63] = 1;
			break;
		case 4:
			field[0] = 1;
			field[63] = 1;
			field[7] = 1;
			break;
		case 5:
			field[0] = 1;
			field[63] = 1;
			field[7] = 1;
			field[56] = 1;
			break;
		default:
			break;
		}

		field[27] = 2;
		field[35] = 1;
		field[28] = 1;
		field[36] = 2;

		whiteIcon = new ImageIcon("White.png");
		blackIcon = new ImageIcon("Black.png");
		boardIcon = new ImageIcon("GreenFrame.png");

		color = new Color(0, 192, 0);
		yellowColor = new Color(224, 224, 0);

		font = new Font(Font.DIALOG, Font.PLAIN, 32);

		c = getContentPane();
		panel1 = new JPanel();
		panel2 = new JPanel();

		c.add(panel1);
		c.add(panel2);

		c.setLayout(new BoxLayout(c, BoxLayout.X_AXIS));

		panel1.setLayout(null);
		panel2.setLayout(null);

		panel2.setSize(370, 400);

		buttonArray = new JButton[row * row];// ボタンの配列を作成

		paint();

		JLabel label1 = new JLabel();
		if (Client.player.isWhite()) {
			label1.setText("○" + Client.player.getMyName() + "(自分)");
		} else {
			label1.setText("●" + Client.player.getMyName() + "(自分)");
		}
		label1.setFont(font);
		label1.setBounds(0, 0, 300, 50);

		JLabel label2 = new JLabel();
		if (Client.player.isWhite()) {
			label2.setText("●" + Client.player.getYourName() + "(相手)");
		} else {
			label2.setText("○" + Client.player.getYourName() + "(相手)");
		}
		label2.setFont(font);
		label2.setBounds(0, 50, 200, 50);

		panel2.add(label1);
		panel2.add(label2);

		logArea = new JTextArea(5, 20);

		if (Client.player.isMyTurn()) {
			logArea.setText("あなたの手番です");
		} else {
			logArea.setText("相手の手番です");
		}

		JScrollPane scrollpane = new JScrollPane(logArea);
		scrollpane.setBounds(0, 110, 340, 180);

		panel2.add(scrollpane);

		JButton btnFinish = new JButton("投了");
		btnFinish.addActionListener(this);
		btnFinish.setBounds(130, 310, 80, 40);
		panel2.add(btnFinish);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(row * 45 * 2 + 30, row * 45 + 40);
		setTitle("オセロ");
		setVisible(true);

		setResizable(false);

		checkPutAvailable();
	}

	public void paint() {
		panel1.removeAll();

		for (int i = 0; i < row * row; i++) {
			if (field[i] == 0) {
				buttonArray[i] = new JButton(boardIcon);
			}// 盤面状態に応じたアイコンを設定
			if (field[i] == 1) {
				buttonArray[i] = new JButton(blackIcon);
			}// 盤面状態に応じたアイコンを設定
			if (field[i] == 2) {
				buttonArray[i] = new JButton(whiteIcon);
			}// 盤面状態に応じたアイコンを設定
				// panel1.remove(buttonArray[i]);
			panel1.add(buttonArray[i]);// ボタンの配列をペインに貼り付け
			// ボタンを配置する
			int x = (i % row) * 45;
			int y = (int) (i / row) * 45;
			buttonArray[i].setBounds(x, y, 45, 45);// ボタンの大きさと位置を設定する．
			buttonArray[i].addMouseListener(this);// マウス操作を認識できるようにする
			buttonArray[i].setActionCommand(Integer.toString(i));// ボタンを識別するための名前(番号)を付加する
			buttonArray[i].setBackground(color);
			buttonArray[i].setBorder(new LineBorder(Color.black, 1));
		}
	}

	public void checkPutAvailable() {
		isCanPutField = new boolean[row * row];
		for (int i = 0; i < row * row; i++) {
			if (field[i] == 0) {
				int x = i % row;
				int y = i / row;
				int myColor, yourColor;
				if (Client.player.isWhite()) {
					myColor = 2;
					yourColor = 1;
				} else {
					myColor = 1;
					yourColor = 2;
				}

				boolean isPassYourColor;
				for (int j = 0; j < row; j++) {
					tempx = x;
					tempy = y;
					isPassYourColor = false;

					checkNext(j);

					while (tempx >= 0 && tempy >= 0 && tempx < row
							&& tempy < row) {

						if (field[tempy * row + tempx] == yourColor) {
							isPassYourColor = true;
						} else if (isPassYourColor
								&& field[tempy * row + tempx] == myColor) {
							isCanPutField[i] = true;
						} else {
							break;
						}

						if (isCanPutField[i])
							break;

						checkNext(j);
					}
					if (isCanPutField[i])
						break;
				}
			}
		}
		checkCanPutField();
	}

	public void checkNext(int j) {
		if (j == 0) {
			tempx -= 1;
			tempy -= 1;
		} else if (j == 1) {
			tempy -= 1;
		} else if (j == 2) {
			tempx += 1;
			tempy -= 1;
		} else if (j == 3) {
			tempx += 1;
		} else if (j == 4) {
			tempx += 1;
			tempy += 1;
		} else if (j == 5) {
			tempy += 1;
		} else if (j == 6) {
			tempx -= 1;
			tempy += 1;
		} else if (j == 7) {
			tempx -= 1;
		}
	}

	public void reverse(int num) {
		int x = num % row;
		int y = num / row;
		int myColor, yourColor;
		if (Client.player.isMyTurn()) {
			if (Client.player.isWhite()) {
				myColor = 2;
				yourColor = 1;
			} else {
				myColor = 1;
				yourColor = 2;
			}
		} else {
			if (Client.player.isWhite()) {
				myColor = 1;
				yourColor = 2;
			} else {
				myColor = 2;
				yourColor = 1;
			}
		}

		boolean isPassYourColor;
		for (int j = 0; j < row; j++) {
			tempx = x;
			tempy = y;
			isPassYourColor = false;

			checkNext(j);

			while (tempx >= 0 && tempy >= 0 && tempx < row && tempy < row) {
				if (field[tempy * row + tempx] == yourColor) {
					isPassYourColor = true;
				} else if (isPassYourColor
						&& field[tempy * row + tempx] == myColor) {
					doReverse(tempx, tempy, num, j, myColor);
					break;
				} else {
					break;
				}
				checkNext(j);
			}
		}
	}

	public void doReverse(int endx, int endy, int num, int j, int myColor) {
		tempx = num % row;
		tempy = num / row;

		do {
			checkNext(j);
			field[tempy * row + tempx] = myColor;

		} while (!(tempx == endx && tempy == endy));

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		if (Client.player.isMyTurn()) {
			JButton theButton = (JButton) e.getComponent();// クリックしたオブジェクトを得る．キャストを忘れずに
			String command = theButton.getActionCommand();// ボタンの名前を取り出す

			int num = Integer.parseInt(command);
			if (isCanPutField[num]) {
				if (Client.player.isWhite()) {
					field[num] = 2;
				} else {
					field[num] = 1;
				}
				reverse(num);

				paint();

				checkPutAvailable();

				Client.sendMessage(command); // テスト用にメッセージを送信

				// 終了したかどうかのチェック
				checkFinish();

				if (isFinish) {
					Client.sendMessage("終了");

					logArea.setText(logArea.getText() + "\n終了しました。");
					logArea.setCaretPosition(logArea.getDocument().getLength());

					countNum();
					String result = null;
					if (countBlack < countWhite) {
						if (Client.player.isWhite()) {
							result = "あなたの勝利です";
						} else {
							result = "あなたの負けです";
						}
					} else if (countBlack > countWhite) {
						if (Client.player.isWhite()) {
							result = "あなたの負けです";
						} else {
							result = "あなたの勝利です";
						}
					} else if (countBlack == countWhite) {
						if (Client.player.getHandi() > 0) {
							if (Client.player.isWhite()) {
								result = "あなたの負けです";
							} else {
								result = "あなたの勝利です";
							}
						} else {
							result = "引き分けです";
						}
					}
					JOptionPane.showMessageDialog(null, "白石" + countWhite
							+ "個、黒石" + countBlack + "個で、" + result
							+ "\n「了解」を押すと再戦確認となります");

					setVisible(false);

					Client.nowScene = Client.Scene.revenge;

					Client.revenge = new Revenge();
				} else {
					Client.player.setMyTurn(false);
					logArea.setText(logArea.getText() + "\n相手の手番です");
					logArea.setCaretPosition(logArea.getDocument().getLength());
				}
			}
		}

	}

	public void receiveMessage(String message) {
		System.out.println("オセロ画面：" + message + "を受信しました");

		if (message.equals("接続切れ")) {
			Client.sendMessage("戻る");
			JOptionPane.showMessageDialog(null, "相手の接続が切れたのでログイン画面に戻ります");
			setVisible(false);
			Client cliant = new Client();
			Client.player = new Player();
			Client.nowScene = Client.Scene.login;

		} else {
			if (!Client.player.isMyTurn()) {
				if (message.equals("パス")) {
					logArea.setText(logArea.getText()
							+ "\n相手が石を置ける場所がなかったため、パスしました。");
					logArea.setCaretPosition(logArea.getDocument().getLength());

				} else if (message.equals("投了")) {
					Client.sendMessage("終了");
					logArea.setText(logArea.getText() + "\n相手が投了を選択しました。");
					logArea.setCaretPosition(logArea.getDocument().getLength());
					JOptionPane.showMessageDialog(null,
							"相手が投了したため、あなたの勝ちです\n「了解」を押すと再戦確認となります");
					setVisible(false);
					Client.nowScene = Client.Scene.revenge;
					Client.revenge = new Revenge();
				} else {
					int num = Integer.parseInt(message);

					if (Client.player.isWhite()) {
						field[num] = 1;
					} else {
						field[num] = 2;
					}
					reverse(num);
					paint();
				}
				Client.player.setMyTurn(true);
				logArea.setText(logArea.getText() + "\nあなたの手番です");
				logArea.setCaretPosition(logArea.getDocument().getLength());

				checkPutAvailable();

				// 終了したかどうかのチェック
				checkFinish();

				if (isFinish) {
					System.out.println("終了判定に入りました");

					Client.sendMessage("終了");
					logArea.setText(logArea.getText() + "\n終了しました。");
					logArea.setCaretPosition(logArea.getDocument().getLength());

					countNum();
					String result = null;
					if (countBlack < countWhite) {
						if (Client.player.isWhite()) {
							result = "あなたの勝利です";
						} else {
							result = "あなたの負けです";
						}
					} else if (countBlack > countWhite) {
						if (Client.player.isWhite()) {
							result = "あなたの負けです";
						} else {
							result = "あなたの勝利です";
						}
					} else if (countBlack == countWhite) {
						if (Client.player.getHandi() > 0) {
							if (Client.player.isWhite()) {
								result = "あなたの負けです";
							} else {
								result = "あなたの勝利です";
							}
						} else {
							result = "引き分けです";
						}
					}
					JOptionPane.showMessageDialog(null, "白石" + countWhite
							+ "個、黒石" + countBlack + "個で、" + result
							+ "\n「了解」を押すと再戦確認となります");

					setVisible(false);
					Client.nowScene = Client.Scene.revenge;
					Client.revenge = new Revenge();
				}

				// パスするかどうかのチェック
				checkMyPass();

				if (isMyPass && !isFinish) {
					Client.player.setMyTurn(false);
					logArea.setText(logArea.getText()
							+ "\nあなたが石を置ける場所がなかったため、パスしました。" + "\n相手の手番です");
					logArea.setCaretPosition(logArea.getDocument().getLength());
					checkPutAvailable();

					Client.sendMessage("パス");
				}
			}
		}

	}

	private void countNum() {
		// TODO 自動生成されたメソッド・スタブ
		countBlack = 0;
		countWhite = 0;
		for (int i = 0; i < row * row; i++) {
			if (field[i] == 1) {
				countBlack++;
			} else if (field[i] == 2) {
				countWhite++;
			}
		}

	}

	private void checkMyPass() {
		// TODO 自動生成されたメソッド・スタブ
		isMyPass = false;

		for (int i = 0; i < row * row; i++) {
			if (isCanPutField[i] == true) {
				return;
			}
		}
		isMyPass = true;
	}

	private void checkFinish() {
		isFinish = false;

		for (int i = 0; i < row * row; i++) {
			if (field[i] == 0) {
				return;
			}
		}
		isFinish = true;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ

		if (Client.player.isMyTurn()) {
			JButton theButton = (JButton) arg0.getComponent();// クリックしたオブジェクトを得る．キャストを忘れずに
			String command = theButton.getActionCommand();// ボタンの名前を取り出す

			int num = Integer.valueOf(command);
			if (isCanPutField[num] == true) {
				buttonArray[num].setBackground(yellowColor);
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		JButton theButton = (JButton) arg0.getComponent();// クリックしたオブジェクトを得る．キャストを忘れずに
		String command = theButton.getActionCommand();// ボタンの名前を取り出す

		int num = Integer.valueOf(command);
		if (isCanPutField[num] == true) {
			buttonArray[num].setBackground(color);
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		String str = e.getActionCommand();

		if (Client.player.isMyTurn()) {
			if (str.equals("投了")) {
				Client.sendMessage("投了");

				logArea.setText(logArea.getText() + "\n投了を選択しました。");
				logArea.setCaretPosition(logArea.getDocument().getLength());

				JOptionPane.showMessageDialog(null,
						"あなたは投了したため、あなたの負けです\n「了解」を押すと再戦確認となります");

				setVisible(false);

				Client.nowScene = Client.Scene.revenge;

				Client.revenge = new Revenge();
			}
		}

	}

	public static void main(String[] args) {
		Client client = new Client();
		client.setVisible(false);
		Client.player = new Player();
		Client.player.setHandi(0);
		new Othello(0);
	}
}
