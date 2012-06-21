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

	private Receiver receiver; // �f�[�^��M�p�I�u�W�F�N�g
	private static PrintWriter out;// �f�[�^���M�p�I�u�W�F�N�g

	private Font font;
	private Font emptyFont;

	public Client() {
		c = getContentPane();

		Panel panel1 = new Panel();
		panel1.setLayout(null);

		c.add(panel1);

		font = new Font(Font.DIALOG, Font.PLAIN, 16);
		emptyFont = new Font(Font.DIALOG, Font.PLAIN, 10);

		label2 = new JLabel("���O����͂��Ă�������");
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

		JButton btnCancel = new JButton("���");
		btnCancel.addActionListener(this);
		btnCancel.setBounds(150, 90, 70, 30);
		panel1.add(btnCancel);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 170);
		setResizable(false);
		setTitle("���O�C���F��");
		setVisible(true);

		connectServer(ipAddress, 10000);
	}

	public static void main(String[] args) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		ipAddress = args[0];
		player = new Player();

		Client client = new Client();
	}

	public void connectServer(String ipAddress, int port) { // �T�[�o�ɐڑ�
		Socket socket = null;
		try {
			socket = new Socket(ipAddress, port); // �T�[�o(ipAddress, port)�ɐڑ�
			out = new PrintWriter(socket.getOutputStream(), true); // �f�[�^���M�p�I�u�W�F�N�g�̗p��
			receiver = new Receiver(socket); // ��M�p�I�u�W�F�N�g�̏���
			receiver.start();
		} catch (UnknownHostException e) {
			System.err.println("�z�X�g��IP�A�h���X������ł��܂���: " + e);
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("�T�[�o�ڑ����ɃG���[���������܂���: " + e);
			System.exit(-1);
		}
	}

	public static void sendMessage(String msg) { // �T�[�o�ɑ�����𑗐M
		out.println(msg);// ���M�f�[�^���o�b�t�@�ɏ����o��
		out.flush();// ���M�f�[�^�𑗂�
		System.out.println("�T�[�o�Ƀ��b�Z�[�W " + msg + " �𑗐M���܂���"); // �e�X�g�W���o��
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
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
				label2.setText("����̐ڑ���҂��Ă��܂��E�E�E");
			}
			if (player.getMyName() != null && player.getYourName() != null) {
				setVisible(false);
				handiCap = new HandiCap();
			}
			
		} else if (str.equals("���")) {
			System.exit(NORMAL);
		}
	}

	public void receiveMessage(String message) {
		System.out.println("���O�C����ʁF" + message + "����M���܂���");

		if (message.equals("�ڑ��؂�")) {
			JOptionPane.showMessageDialog(null, "����̐ڑ����؂ꂽ�̂Ń��O�C����ʂɖ߂�܂�");
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
		private InputStreamReader sisr; // ��M�f�[�^�p�����X�g���[��
		private BufferedReader br; // �����X�g���[���p�̃o�b�t�@

		private String message = null;

		Receiver(Socket socket) {
			try {
				sisr = new InputStreamReader(socket.getInputStream()); // ��M�����o�C�g�f�[�^�𕶎��X�g���[����
				br = new BufferedReader(sisr);// �����X�g���[�����o�b�t�@�����O����
			} catch (IOException e) {
				System.err.println("�f�[�^��M���ɃG���[���������܂���: " + e);
			}
		}

		public void run() { // ���b�Z�[�W�̎�M
			String inputLine = null;
			try {
				while (true) {// �f�[�^����M��������
					inputLine = br.readLine();// ��M�f�[�^����s���ǂݍ���

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
				System.err.println("�f�[�^��M���ɃG���[���������܂���: " + e);
			}
		}
	}
}
