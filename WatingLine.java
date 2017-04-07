package doorController;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * ��������ѭ�������û��������󣬲�������Ӧ�����½���Ӧ��socket���߳�
 * 
 * @author Mottled
 *
 */
public class WatingLine implements Runnable {
	server m;

	public WatingLine(server m) {
		this.m = m;
	}

	public void run() {
		while (true) {
			Socket unknowSocket = null;
			try {
				unknowSocket = m.getSrvSocket().accept();
			} catch (IOException e) {
				System.out.println("tcpServer��������ʱ����һ������");
				e.printStackTrace();
			}
			System.out.println("һ���ͻ��˳������ӣ�ip��ַΪ��" + unknowSocket.getInetAddress());
			Thread handle = new Thread(new HandleSocket(unknowSocket,m));
			handle.start();

		}
	}

	class HandleSocket implements Runnable {
		private Socket s = null;
		private BufferedReader in = null;
		private String handleCode = null;
		private DataOutputStream out = null;
		server m;
		
		public HandleSocket(Socket s,server m) {
			this.s = s;
			this.m = m;
		}

		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				out = new DataOutputStream(s.getOutputStream());
			} catch (IOException e) {
				System.out.println("������ͻ��˵���Ϣͨ��ʱ����һ������,�����ǿͻ����ѶϿ�����");
				e.printStackTrace();

				try {
					s.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				return;
			}

			try {
				handleCode = in.readLine();
				System.out.println(handleCode);
			} catch (IOException e) {
				System.out.println("���ܴӿͻ��˷��͵�ʶ����ʱ����һ������,�����ǿͻ����ѶϿ�����");

				try {
					in.close();
					out.close();
					s.close();
				} catch (IOException e1) {
					// TODO �Զ����ɵ� catch ��
					e1.printStackTrace();
				}

				e.printStackTrace();
				return;
			}

			if (handleCode != null) {
				// ���ǿ��У����ٶ�ȡ��һ�С�
				while (handleCode.matches("^[\\s&&[^\\n]]*\\n$")) {
					try {
						handleCode = in.readLine();
					} catch (IOException e) {
						System.out.println("���ܴӿͻ��˷��͵�ʶ����ʱ����һ������");

						try {
							in.close();
							out.close();
							s.close();
						} catch (IOException e1) {
							// TODO �Զ����ɵ� catch ��
							e1.printStackTrace();
						}

						e.printStackTrace();
						return;
					}
				}
				//���ǿ��У����ȡָ��
				if(handleCode.matches("a")){
					System.out.println("���յ��ͻ������ݣ�code:"+handleCode);
					if(this.m.device == null)return;
					BufferedWriter br;
					try {
						br = new BufferedWriter(new OutputStreamWriter(this.m.device.getOutputStream()));
						br.write("a");
						br.flush();
					} catch (IOException e) {
						// TODO �Զ����ɵ� catch ��
						System.out.println("���豸�˵����ӳ������⣡");
					}

				}
				if(handleCode.matches("d")){
					System.out.println("���豸���ӳɹ�,code:"+handleCode);
					this.m.device = s;
				}
				

			}

			// ��������D,R,L��ͷ���ַ������͹ر�socket����������
			else {
				try {
					in.close();
					out.close();
					s.close();
				} catch (IOException e1) {
					// TODO �Զ����ɵ� catch ��
					e1.printStackTrace();
				}

				return;
			}

		}
	}

}
