import java.net.*;
import java.io.*;

public class Server {
	private int port; //�T�[�o�̑҂��󂯃|�[�g
	private boolean[] online; // �I�����C����ԊǗ��p�z��
	private PrintWriter[] out; // �f�[�^���M�p�I�u�W�F�N�g
	private Receiver[] receiver; //�f�[�^��M�p�I�u�W�F�N�g
	private int state = 0; //��ʂ̑J�ڏ�ԃ`�F�b�N
	private int n = 0; //playerNo�pint
	private String[] name; //���[�U���ێ�
	private int count; //�n���f�܏Չ�
	private String[] handicap; //�n���f�I��
	private String[] choose; //�n���f�m�F�A�Đ�ł̑I��(�I�𒆁E����E���Ȃ�)
	
	// �R���X�g���N�^
	public Server(int port) {
		this.port = port;
		initialize();
	}
	
	//����������
	public void initialize(){
		out = new PrintWriter[2];
		receiver = new Receiver[2];
		online = new boolean[2];
		name = new String[2];
		handicap = new String[2];
		choose = new String[2];
		for(int i = 0; i<2;i++){
			n=0;
			online[i] = false;
			name[i] = handicap[i] = choose[i] = null;
			state = 0;
			count = 2;
		}
	}
	//------------------------------
	//�f�[�^��M�p�X���b�h(�����N���X)
	class Receiver extends Thread{
		private InputStreamReader isr;
		private BufferedReader br;
		private int playerNo; //�v���C�����ʔԍ�
		private boolean move;	//�����
		public boolean blinker;
		//�R���X�g���N�^
		Receiver(Socket sock, int playerNo){
			try{
				this.playerNo = playerNo;
				isr = new InputStreamReader(sock.getInputStream());
				br = new BufferedReader(isr);
				move = true;
			}catch(IOException e){
				System.err.println("�f�[�^��M���G���[:" + e);
			}
		}
		
		// ���\�b�h
		public void run() { // Thread�g��
			try {
				while (true) { // �f�[�^��M�p��
					if (state != 3)
						printStatus();
					String inputLine = br.readLine();// ��s�ǂݍ���
					if (inputLine != null) {
						/* ��ԂŎ��s�֐��̐؂�ւ� */
						if (inputLine.equals("�߂�")) {
							online[playerNo] = false;
							printStatus();
							initialize();
							break;
						}
						switch (state) {
						case 0: // �N���C�A���g���O�C����ʂŎ�M
							logIn(inputLine, playerNo);
							break;
						case 1: // �N���C�A���g�܏Չ�ʂŎ�M
							handi(inputLine, playerNo);
							break;
						case 2: // �N���C�A���g�n���f�m�F��ʂŎ�M
							choose(inputLine, playerNo);
							break;
						case 3: { // �N���C�A���g�΋ǉ�ʂŎ�M
							if (move) {
								forwardMessage(inputLine, playerNo); // ��������ɓ]��
								move = false;
							}
							break;
						}
						case 4: // �N���C�A���g�Đ��ʂŎ�M
							choose(inputLine, playerNo % 2);
							break;
						}
						stateChange(); // state�ύX�p�֐�
					}

				}
			} catch (IOException e) {// �ڑ��؂�
				System.err.println("�v���C��" + playerNo + "�̐ڑ����؂�܂���.");
				online[playerNo] = false;
				printStatus();
				if (online[(playerNo + 1) % 2]) {// ���肪�ڑ���ԂȂ�(���O���͂��ς�ł����)
					statusCut(playerNo); // �ڑ��؂���𑊎�ɏo��
				} else {// ���肪�ڑ����Ă��Ȃ����
					initialize();// ������
				}
			}
		}
	}


	// -----------------------------------

	// Server���\�b�h
	public void acceptClient() { // �N���C�A���g�̐ڑ�(�T�[�o�N��)
		try {
			System.out.println("�T�[�o�N��.");
			ServerSocket ss = new ServerSocket(port); // �T�[�o�\�P�b�g��p��
			while (true) {
				Socket sock = ss.accept(); // �V�K�ڑ����󂯕t��
				if (online[0]) {
					n = 1;
				}
				online[n] = true;
				//System.out.println("�N���C�A���g" + n + "�Ɛڑ����܂���.");// �e�X�g�p�o��
				receiver[n] = new Receiver(sock, n); // �\�P�b�g��No�Ŏ�M�p��
				out[n] = new PrintWriter(sock.getOutputStream(), true);
				receiver[n].start();
			}
		} catch (Exception e) {
			System.err.println("�\�P�b�g�쐬���G���[:" + e);
		}
	}


	// ----------------------------------------
	
	public void stateChange() { // state�ύX�E���M�Ȃ�
		if (state == 0 && name[0] != null && name[1] != null) { // �ǂ�������O�C�����Ă�����
			stateChangeMessage(name[1], name[0]); // �݂��̖��O������
			state = 1; // �n���f�܏ՂɈڍs

		}
		if (state == 1 && handicap[0] != null && handicap[1] != null) {// �ǂ�����n���f�I�������񂾂�
			stateChangeMessage(handicap[1], handicap[0]);
			state = 2; // �m�F��ʂɈڍs

		}
		if (state == 2 && choose[0] != null && choose[1] != null) { // �m�F���ς񂾂�
			if (choose[0].equals("�͂�") && choose[1].equals("�͂�")) { // �ǂ��������������
				stateChangeMessage("�͂�", "�͂�");
				checkhandi(); // �܏Ռ��ʂ��o��
				state = 3; // �΋ǂɈڍs

			} else if (choose[0].equals("������") || choose[1].equals("������")) { // �ǂ��炩�����ۂ�����
				stateChangeMessage("������", "������");
				
				if (count != 0) { // 2��ȓ��Ȃ�
					handicap[0] = handicap[1] = choose[0] = choose[1] = null; // �n���f���E�I��������
					state = 1; // �n���f�I����ʂɖ߂�
					count--;
					
				} else if (count == 0) { // 3��ڂȂ�n���f�Ȃ���
					handicap[0] = handicap[1] = "0"; // �n���f�����ő΋�
					checkhandi();
					state = 3;
				}
			}
		}
		if (state == 4 && choose[0] != null && choose[1] != null) { // �Đ�m�F
			if (choose[0].equals("�͂�") && choose[1].equals("�͂�")) { // �ǂ������]
				stateChangeMessage("�͂�", "�͂�");
				state = 1; // �n���f�܏ՂɈڍs
				handicap[0] = handicap[1] = choose[0] = choose[1] = null; // ������
			} else { // �ǂ��炩���Đ틑��
				stateChangeMessage("������", "������");
				initialize();
			}
		}
	}
	
	// --------------------------------------------
	// �o�͗p���\�b�h
	public void stateChangeMessage(String msg0, String msg1) { // �����̕�������N���C�A���g�ɕԂ�
		out[0].println(msg0);
		out[0].flush();
		System.out.println("�N���C�A���g0��" + msg0 + "�𑗐M���܂�");
		out[1].println(msg1);
		out[1].flush();
		System.out.println("�N���C�A���g1��" + msg1 + "�𑗐M���܂�");
	}
	
	// �N���C�A���g�ڑ����
	public void printStatus(){
		String msg;
		for(int i = 0;i<2;i++){
			if(online[i] == true && name[i] != null)
				msg = "�ڑ���.";
			else
				msg = "�ڑ��Ȃ�.";
			System.out.println("�N���C�A���g "+ i + " "+ msg);
		}
	}
	
	//�ڑ��؂�
	public void statusCut(int No) {
		int opponentNo = (No+1) % 2; // ����playerNo(0or1)
			out[opponentNo].println("�ڑ��؂�");
			out[opponentNo].flush();
	}
	
	/*
	 * +0�͍��A��� -0�͔��A��� +1�͍��A��� �E�E�E -1�͔��A��� �E�E�E
	 */
	// �n���f�܏ՁE�o��/+�͌��A-�͐��
	public void checkhandi() {
		if ((!handicap[0].equals("0") && !handicap[1].equals("0"))
				|| (handicap[0].equals("0") && handicap[1].equals("0"))) { // �ǂ�����0����Ȃ��A�܂�n���f��]����Ȃ�n���f�Ȃ���
			if (receiver[0].playerNo < receiver[1].playerNo) { // �����������ق������Ńn���f������
				stateChangeMessage("+0", "-0");
				System.out.println("+0��-0�𑗐M���܂���.");
				receiver[1].move = false; // receiver[0]�͍��Ő��
				
			} else {
				stateChangeMessage("-0", "+0");
				System.out.println("-0��+0�𑗐M���܂���.");
				receiver[0].move = false;	// receiver[0]�͔��Ō��
			}
			
		} else if (handicap[0].equals("0")) { // �Е����s�v�Ȃ�,receiver[0]�̓n���f��^���锒�F���
			stateChangeMessage("-" + handicap[1], "+" + handicap[1]);
			System.out.println("+"+handicap[1]+"��-"+handicap[1]+"�𑗐M���܂���.");
			receiver[1].move = false;	//�n���f�����炤���͌��
			
		} else if (handicap[1].equals("0")) {
			stateChangeMessage("+" + handicap[0], "-" + handicap[0]);
			System.out.println("+"+handicap[0]+"��-"+handicap[0]+"�𑗐M���܂���.");
			receiver[0].move = false;
		}
		System.out.println("�n���f�̐܏Ղ��I�����܂���.�΋ǂɈڍs���܂�.");
		choose[0] = choose[1] = null;
		count = 2;
	}
	
	
	//---------------------------------------
	//��M�p���\�b�h
	//(0)���O�C�����
	public void logIn(String msg, int No){
		name[No] = msg;
		System.out.println("�N���C�A���g"+No+"���烆�[�U������M���܂���.");
		online[No] = true;	//���O�擾���ɐڑ��������Ƃɂ���
	}
	
	//(1)�n���f�܏�
	public void handi(String msg, int No){
		handicap[No] = msg; //�n���f�i�[
		System.out.println("�N���C�A���g"+No+"����n���f������M���܂���.");
	}
	
	//(2,4)�܏Պm�F,�Đ�m�F
	public void choose(String msg,int No){
		choose[No] = msg;	//�I���i�[
		System.out.println("�N���C�A���g"+No+"����m�F�I������M���܂���.");
		System.out.println("�e�X�g�p�o�́F"+choose[No]);
	}
	
	// (3)������̓]��
	public void forwardMessage(String msg, int No) {
		out[(No + 1) % 2].println(msg);
		out[(No + 1) % 2].flush(); // ����ɂ��̂܂ܓ]��
		if (msg.equals("�I��")) { // �����E�I������
			System.out.println("�΋ǂ��I�����܂�.");
			state = 4;
		}
		receiver[(No + 1) % 2].move = true;
	}
	
	//main
	public static void main(String[] args){
		Server server = new Server(10000); //�҂��󂯃|�[�g
		server.acceptClient(); //�N���C�A���g�󂯓�����J�n
	}
}
