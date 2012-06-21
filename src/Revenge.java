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

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// �E�B���h�E�����ꍇ�̏���
		setTitle("�Đ�m�F");// �E�B���h�E�̃^�C�g��
		setSize(300, 200);// �E�B���h�E�̃T�C�Y��ݒ�

		JLabel empty1 = new JLabel("    ");
		panel1.add(empty1);

		label1 = new JLabel("�Đ킵�܂����H");
		label1.setFont(font);
		panel1.add(label1);

		JLabel empty2 = new JLabel("   ");
		panel1.add(empty2);

		c1.add(panel1);

		label2 = new JLabel("<HTML>���ǂ��炩��������ۂ����ꍇ<br>�@���O�C����ʂɖ߂�܂��B");
		label2.setFont(font);
		panel1.add(label2);

		btnYes = new JButton("�͂�");
		btnYes.addActionListener(this);
		panel2.add(btnYes);

		btnNo = new JButton("������");
		btnNo.addActionListener(this);
		panel2.add(btnNo);

		c1.add(panel2);

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		String str = e.getActionCommand();

		if (!isPushButton) {
			if (str.equals("�͂�")) {
				Client.sendMessage(str);

			} else if (str.equals("������")) {
				Client.sendMessage(str);
			}

			label1.setText("����̑I����҂��Ă��܂��E�E�E");
			isPushButton = true;
		}
	}

	public void receiveMessage(String message) {
		System.out.println("�Đ�m�F�F" + message + "����M���܂����B");

		if (message.equals("�ڑ��؂�")) {
			Client.sendMessage("�߂�");
			JOptionPane.showMessageDialog(null, "����̐ڑ����؂ꂽ�̂Ń��O�C����ʂɖ߂�܂�");
			setVisible(false);
			Client client = new Client();
			Client.player = new Player();
			Client.nowScene = Client.Scene.login;

		} else {
			if (message.equals("�͂�")) {
				setVisible(false);
				Client.handiCap = new HandiCap();
				Client.nowScene = Client.Scene.handi;
			} else if (message.equals("������")) {
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
