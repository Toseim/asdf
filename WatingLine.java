package doorController;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * 用来不断循环接受用户连接请求，并作出相应处理，新建相应的socket的线程
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
				System.out.println("tcpServer接收数据时发生一个错误");
				e.printStackTrace();
			}
			System.out.println("一个客户端尝试连接，ip地址为：" + unknowSocket.getInetAddress());
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
				System.out.println("创建与客户端的信息通道时发生一个错误,可能是客户端已断开连接");
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
				System.out.println("接受从客户端发送的识别码时发生一个错误,可能是客户端已断开连接");

				try {
					in.close();
					out.close();
					s.close();
				} catch (IOException e1) {
					// TODO 自动生成的 catch 块
					e1.printStackTrace();
				}

				e.printStackTrace();
				return;
			}

			if (handleCode != null) {
				// 若是空行，则再读取下一行。
				while (handleCode.matches("^[\\s&&[^\\n]]*\\n$")) {
					try {
						handleCode = in.readLine();
					} catch (IOException e) {
						System.out.println("接受从客户端发送的识别码时发生一个错误");

						try {
							in.close();
							out.close();
							s.close();
						} catch (IOException e1) {
							// TODO 自动生成的 catch 块
							e1.printStackTrace();
						}

						e.printStackTrace();
						return;
					}
				}
				//若非空行，则读取指令
				if(handleCode.matches("a")){
					System.out.println("接收到客户端数据！code:"+handleCode);
					if(this.m.device == null)return;
					BufferedWriter br;
					try {
						br = new BufferedWriter(new OutputStreamWriter(this.m.device.getOutputStream()));
						br.write("a");
						br.flush();
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						System.out.println("与设备端的连接出现问题！");
					}

				}
				if(handleCode.matches("d")){
					System.out.println("与设备连接成功,code:"+handleCode);
					this.m.device = s;
				}
				

			}

			// 若不是以D,R,L开头的字符串，就关闭socket，结束进程
			else {
				try {
					in.close();
					out.close();
					s.close();
				} catch (IOException e1) {
					// TODO 自动生成的 catch 块
					e1.printStackTrace();
				}

				return;
			}

		}
	}

}
