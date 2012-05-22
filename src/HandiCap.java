//�p�b�P�[�W�̃C���|�[�g
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

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// �E�B���h�E�����ꍇ�̏���
		setTitle("�n���f�B�L���b�v");// �E�B���h�E�̃^�C�g��
		setSize(420, 460);// �E�B���h�E�̃T�C�Y��ݒ�

		nameFont = new Font(Font.DIALOG, Font.BOLD, 20);

		player1 = new JLabel(" ���[�U��(����):" + Cliant.player.getMyName());
		player1.setFont(nameFont);
		player2 = new JLabel(" ���[�U��(����):" + Cliant.player.getYourName());
		player2.setFont(nameFont);

		panel.add(player1);
		panel.add(player2);

		JLabel emptyLabel1 = new JLabel("   ");
		panel.add(emptyLabel1);

		txt1 = new JLabel("    ���Ȃ��̃n���f��I�����Ă�������");
		panel.add(txt1);

		JLabel emptyLabel2 = new JLabel("   ");
		panel.add(emptyLabel2);

		radio[0] = new JRadioButton("�s�v", true);

		radio[1] = new JRadioButton("������������");
		b1 = new JLabel("�@�|�΂̐��������Ƃ����Ȃ��̏���");

		radio[2] = new JRadioButton("1�q��");
		b2 = new JLabel("�@�|����̋��ɂ��Ȃ��̐΂�u���đ΋ǂ��J�n����B");

		radio[3] = new JRadioButton("2�q��");
		b3 = new JLabel("�@�|����ƉE���̋��ɂ��Ȃ��̐� ��u���đ΋ǂ��J�n����B");

		radio[4] = new JRadioButton("3�q��");
		b4 = new JLabel("�@�|����ƉE���A�E��̋��ɂ��Ȃ��̐΂�u���đ΋ǂ��J�n����B");

		radio[5] = new JRadioButton("4�q��");
		b5 = new JLabel("�@�|4�����S�Ă̋��ɂ��Ȃ��̐΂�u���đ΋ǂ��J�n����B");

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

		txt2 = new JLabel("   ���ǂ�����n���f����]����ꍇ�n���f�Ȃ��Ƃ���B");
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
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
				txt1.setText("    ����̃n���f�I����҂��Ă��܂��E�E�E");
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
		System.out.println("�n���f�I����ʁF" + message + "����M���܂���");

		if (message.equals("�ڑ��؂�")) {
			Cliant.sendMessage("�߂�");
			JOptionPane.showMessageDialog(null, "����̐ڑ����؂ꂽ�̂Ń��O�C����ʂɖ߂�܂�");
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