import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Othello extends JFrame implements MouseListener, ActionListener {
	private int[] field; // 1����,2����
	private static int row = 8; // �}�X��

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

		buttonArray = new JButton[row * row];// �{�^���̔z����쐬

		paint();

		JLabel label1 = new JLabel();
		if (Client.player.isWhite()) {
			label1.setText("��" + Client.player.getMyName() + "(����)");
		} else {
			label1.setText("��" + Client.player.getMyName() + "(����)");
		}
		label1.setFont(font);
		label1.setBounds(0, 0, 300, 50);

		JLabel label2 = new JLabel();
		if (Client.player.isWhite()) {
			label2.setText("��" + Client.player.getYourName() + "(����)");
		} else {
			label2.setText("��" + Client.player.getYourName() + "(����)");
		}
		label2.setFont(font);
		label2.setBounds(0, 50, 200, 50);

		panel2.add(label1);
		panel2.add(label2);

		logArea = new JTextArea(5, 20);

		if (Client.player.isMyTurn()) {
			logArea.setText("���Ȃ��̎�Ԃł�");
		} else {
			logArea.setText("����̎�Ԃł�");
		}

		JScrollPane scrollpane = new JScrollPane(logArea);
		scrollpane.setBounds(0, 110, 340, 180);

		panel2.add(scrollpane);

		JButton btnFinish = new JButton("����");
		btnFinish.addActionListener(this);
		btnFinish.setBounds(130, 310, 80, 40);
		panel2.add(btnFinish);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(row * 45 * 2 + 30, row * 45 + 40);
		setTitle("�I�Z��");
		setVisible(true);

		setResizable(false);

		checkPutAvailable();
	}

	public void paint() {
		panel1.removeAll();

		for (int i = 0; i < row * row; i++) {
			if (field[i] == 0) {
				buttonArray[i] = new JButton(boardIcon);
			}// �Ֆʏ�Ԃɉ������A�C�R����ݒ�
			if (field[i] == 1) {
				buttonArray[i] = new JButton(blackIcon);
			}// �Ֆʏ�Ԃɉ������A�C�R����ݒ�
			if (field[i] == 2) {
				buttonArray[i] = new JButton(whiteIcon);
			}// �Ֆʏ�Ԃɉ������A�C�R����ݒ�
				// panel1.remove(buttonArray[i]);
			panel1.add(buttonArray[i]);// �{�^���̔z����y�C���ɓ\��t��
			// �{�^����z�u����
			int x = (i % row) * 45;
			int y = (int) (i / row) * 45;
			buttonArray[i].setBounds(x, y, 45, 45);// �{�^���̑傫���ƈʒu��ݒ肷��D
			buttonArray[i].addMouseListener(this);// �}�E�X�����F���ł���悤�ɂ���
			buttonArray[i].setActionCommand(Integer.toString(i));// �{�^�������ʂ��邽�߂̖��O(�ԍ�)��t������
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		if (Client.player.isMyTurn()) {
			JButton theButton = (JButton) e.getComponent();// �N���b�N�����I�u�W�F�N�g�𓾂�D�L���X�g��Y�ꂸ��
			String command = theButton.getActionCommand();// �{�^���̖��O�����o��

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

				Client.sendMessage(command); // �e�X�g�p�Ƀ��b�Z�[�W�𑗐M

				// �I���������ǂ����̃`�F�b�N
				checkFinish();

				if (isFinish) {
					Client.sendMessage("�I��");

					logArea.setText(logArea.getText() + "\n�I�����܂����B");
					logArea.setCaretPosition(logArea.getDocument().getLength());

					countNum();
					String result = null;
					if (countBlack < countWhite) {
						if (Client.player.isWhite()) {
							result = "���Ȃ��̏����ł�";
						} else {
							result = "���Ȃ��̕����ł�";
						}
					} else if (countBlack > countWhite) {
						if (Client.player.isWhite()) {
							result = "���Ȃ��̕����ł�";
						} else {
							result = "���Ȃ��̏����ł�";
						}
					} else if (countBlack == countWhite) {
						if (Client.player.getHandi() > 0) {
							if (Client.player.isWhite()) {
								result = "���Ȃ��̕����ł�";
							} else {
								result = "���Ȃ��̏����ł�";
							}
						} else {
							result = "���������ł�";
						}
					}
					JOptionPane.showMessageDialog(null, "����" + countWhite
							+ "�A����" + countBlack + "�ŁA" + result
							+ "\n�u�����v�������ƍĐ�m�F�ƂȂ�܂�");

					setVisible(false);

					Client.nowScene = Client.Scene.revenge;

					Client.revenge = new Revenge();
				} else {
					Client.player.setMyTurn(false);
					logArea.setText(logArea.getText() + "\n����̎�Ԃł�");
					logArea.setCaretPosition(logArea.getDocument().getLength());
				}
			}
		}

	}

	public void receiveMessage(String message) {
		System.out.println("�I�Z����ʁF" + message + "����M���܂���");

		if (message.equals("�ڑ��؂�")) {
			Client.sendMessage("�߂�");
			JOptionPane.showMessageDialog(null, "����̐ڑ����؂ꂽ�̂Ń��O�C����ʂɖ߂�܂�");
			setVisible(false);
			Client cliant = new Client();
			Client.player = new Player();
			Client.nowScene = Client.Scene.login;

		} else {
			if (!Client.player.isMyTurn()) {
				if (message.equals("�p�X")) {
					logArea.setText(logArea.getText()
							+ "\n���肪�΂�u����ꏊ���Ȃ��������߁A�p�X���܂����B");
					logArea.setCaretPosition(logArea.getDocument().getLength());

				} else if (message.equals("����")) {
					Client.sendMessage("�I��");
					logArea.setText(logArea.getText() + "\n���肪������I�����܂����B");
					logArea.setCaretPosition(logArea.getDocument().getLength());
					JOptionPane.showMessageDialog(null,
							"���肪�����������߁A���Ȃ��̏����ł�\n�u�����v�������ƍĐ�m�F�ƂȂ�܂�");
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
				logArea.setText(logArea.getText() + "\n���Ȃ��̎�Ԃł�");
				logArea.setCaretPosition(logArea.getDocument().getLength());

				checkPutAvailable();

				// �I���������ǂ����̃`�F�b�N
				checkFinish();

				if (isFinish) {
					System.out.println("�I������ɓ���܂���");

					Client.sendMessage("�I��");
					logArea.setText(logArea.getText() + "\n�I�����܂����B");
					logArea.setCaretPosition(logArea.getDocument().getLength());

					countNum();
					String result = null;
					if (countBlack < countWhite) {
						if (Client.player.isWhite()) {
							result = "���Ȃ��̏����ł�";
						} else {
							result = "���Ȃ��̕����ł�";
						}
					} else if (countBlack > countWhite) {
						if (Client.player.isWhite()) {
							result = "���Ȃ��̕����ł�";
						} else {
							result = "���Ȃ��̏����ł�";
						}
					} else if (countBlack == countWhite) {
						if (Client.player.getHandi() > 0) {
							if (Client.player.isWhite()) {
								result = "���Ȃ��̕����ł�";
							} else {
								result = "���Ȃ��̏����ł�";
							}
						} else {
							result = "���������ł�";
						}
					}
					JOptionPane.showMessageDialog(null, "����" + countWhite
							+ "�A����" + countBlack + "�ŁA" + result
							+ "\n�u�����v�������ƍĐ�m�F�ƂȂ�܂�");

					setVisible(false);
					Client.nowScene = Client.Scene.revenge;
					Client.revenge = new Revenge();
				}

				// �p�X���邩�ǂ����̃`�F�b�N
				checkMyPass();

				if (isMyPass && !isFinish) {
					Client.player.setMyTurn(false);
					logArea.setText(logArea.getText()
							+ "\n���Ȃ����΂�u����ꏊ���Ȃ��������߁A�p�X���܂����B" + "\n����̎�Ԃł�");
					logArea.setCaretPosition(logArea.getDocument().getLength());
					checkPutAvailable();

					Client.sendMessage("�p�X");
				}
			}
		}

	}

	private void countNum() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u

		if (Client.player.isMyTurn()) {
			JButton theButton = (JButton) arg0.getComponent();// �N���b�N�����I�u�W�F�N�g�𓾂�D�L���X�g��Y�ꂸ��
			String command = theButton.getActionCommand();// �{�^���̖��O�����o��

			int num = Integer.valueOf(command);
			if (isCanPutField[num] == true) {
				buttonArray[num].setBackground(yellowColor);
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		JButton theButton = (JButton) arg0.getComponent();// �N���b�N�����I�u�W�F�N�g�𓾂�D�L���X�g��Y�ꂸ��
		String command = theButton.getActionCommand();// �{�^���̖��O�����o��

		int num = Integer.valueOf(command);
		if (isCanPutField[num] == true) {
			buttonArray[num].setBackground(color);
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		String str = e.getActionCommand();

		if (Client.player.isMyTurn()) {
			if (str.equals("����")) {
				Client.sendMessage("����");

				logArea.setText(logArea.getText() + "\n������I�����܂����B");
				logArea.setCaretPosition(logArea.getDocument().getLength());

				JOptionPane.showMessageDialog(null,
						"���Ȃ��͓����������߁A���Ȃ��̕����ł�\n�u�����v�������ƍĐ�m�F�ƂȂ�܂�");

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
