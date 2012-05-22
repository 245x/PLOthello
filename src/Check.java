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

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// �E�B���h�E�����ꍇ�̏���
		setTitle("�m�F���");// �E�B���h�E�̃^�C�g��
		setSize(400, 310);// �E�B���h�E�̃T�C�Y��ݒ�

		JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));
		c1.add(panel1);

		font = new Font(Font.DIALOG, Font.BOLD, 22);

		txt1 = new JLabel("�����F  ");
		if (myHandi == 0) {
			txt1.setText(Cliant.player.getMyName() + "�F�s�v");
		} else if (myHandi == 1) {
			txt1.setText(Cliant.player.getMyName() + "�F������������");
		} else {
			txt1.setText(Cliant.player.getMyName() + "�F"
					+ String.valueOf(myHandi - 1) + "�q��");
		}
		txt1.setFont(font);
		panel1.add(txt1);

		txt2 = new JLabel("����F  ");
		if (yourHandi == 0) {
			txt2.setText(Cliant.player.getYourName() + "�F�s�v");
		} else if (yourHandi == 1) {
			txt2.setText(Cliant.player.getYourName() + "�F������������");
		} else {
			txt2.setText(Cliant.player.getYourName() + "�F"
					+ String.valueOf(yourHandi - 1) + "�q��");
		}
		txt2.setFont(font);
		panel1.add(txt2);

		JLabel empty1 = new JLabel(" ");
		panel1.add(empty1);

		txt3 = new JLabel("����");
		if (resultHandi == 0) {
			infoMsg = "�n���f�Ȃ��ő΋ǂ��܂��B";
			txt3.setText("�n���f�Ȃ��ő΋ǂ��܂��B��낵���ł����H");
		} else if (resultHandi == 1) {
			infoMsg = "�����������ɂ��Ȃ��̏����ƂȂ�܂��B";
			txt3.setText("�����������ɂ��Ȃ��̏����ƂȂ�܂��B��낵���ł����H");
		} else if (resultHandi == -1) {
			infoMsg = "�����������ɑ���̏����ƂȂ�܂��B";
			txt3.setText("�����������ɑ���̏����ƂȂ�܂��B��낵���ł����H");
		} else if (resultHandi > 1) {
			infoMsg = "���Ȃ���" + String.valueOf(resultHandi - 1)
					+ "�q�̃n���f���^�����܂��B";
			txt3.setText("���Ȃ���" + String.valueOf(resultHandi - 1)
					+ "�q�̃n���f���^�����܂��B��낵���ł����H");
		} else if (resultHandi < -1) {
			infoMsg = "�����" + String.valueOf(-1 * resultHandi - 1)
					+ "�q�̃n���f���^�����܂��B";
			txt3.setText("�����" + String.valueOf(-1 * resultHandi - 1)
					+ "�q�̃n���f���^�����܂��B��낵���ł����H");
		}
		panel1.add(txt3);

		JLabel empty2 = new JLabel(" ");
		panel1.add(empty2);

		txt4 = new JLabel("<HTML>�ǂ��炩������u�������v��I�񂾏ꍇ��<br>   �n���f�I����ʂɖ߂�܂��B");
		panel1.add(txt4);
		JLabel empty4 = new JLabel("  ");
		panel1.add(empty4);

		txt5 = new JLabel(
				"<HTML>���n���f�ɂ��Ă̓��ӂ�3��A���œ����Ȃ������ꍇ��<br>   �n���f�Ȃ��ŊJ�n���܂��B");
		panel1.add(txt5);

		JLabel empty3 = new JLabel(" ");
		panel1.add(empty3);

		txt6 = new JLabel("�n���f�̊�]�͂���" + String.valueOf(2 - count++) + "��o���܂��B");
		panel1.add(txt6);

		JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout());
		c1.add(panel2);

		b1 = new JButton("�͂�");
		b2 = new JButton("������");

		b1.addActionListener(this);
		b2.addActionListener(this);

		panel2.add(b1);
		panel2.add(b2);

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		String str = e.getActionCommand();

		if (!isSendFinish) {
			if (str.equals("�͂�")) {
				Cliant.sendMessage("�͂�");
				isSendFinish = true;

			} else if (str.equals("������")) {
				Cliant.sendMessage("������");
				isSendFinish = true;
			}

			txt3.setText("����̑I����҂��Ă��܂��E�E�E");
		}

	}

	public void receiveMessage(String message) {
		System.out.println("�m�F��ʁF" + message + "����M���܂���");

		if (message.equals("�ڑ��؂�")) {
			Cliant.sendMessage("�߂�");
			JOptionPane.showMessageDialog(null, "����̐ڑ����؂ꂽ�̂Ń��O�C����ʂɖ߂�܂�");
			setVisible(false);
			count = 0;
			new Cliant();
			Cliant.player = new Player();
			Cliant.nowScene = Cliant.Scene.login;

		} else {
			if (resultCheck == null) {
				if (message.equals("�͂�")) {
					resultCheck = message;
				} else if (message.equals("������") && count < 3) {
					setVisible(false);
					Cliant.handiCap = new HandiCap();
					Cliant.nowScene = Cliant.Scene.handi;
				} else if (message.equals("������") && count >= 3) {
					isCountOver = true;
					resultCheck = message;
				}

			} else {
				/*
				 * +���n���f���󂯎�鑤�A�܂���(��) -���n���f��n�����A�܂���(��) �n���f�Ȃ��Ȃ�+0,-0����M
				 * +�����A-�����œ��� +0�Ȃ獕�̐��A-0�Ȃ甒�̌��
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
					JOptionPane.showMessageDialog(this, "�n���f�ɂ��Ă̓��ӂ��R��A����"
							+ "\n�����Ȃ��������߁A�n���f�Ȃ���ƂȂ�܂��B");
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