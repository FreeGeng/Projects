import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class NaServer extends JFrame implements ActionListener {
	// 先定義執行序名字,之後才會分配值給它
	Thread primaryThread;
	// 定義vector名字
	public static Vector systemMessage;
	// 定義一個string
	public String localSocketAddress;
	// 定義一個JLabel
	JLabel showWelcome = new JLabel("---------------Server端---------------");
	// 顯示記錄區域
	JTextArea showtext = new JTextArea(15, 50);
	// 創造主畫面,一個JFrame
	JFrame serverFrame = new JFrame("Server監控視窗");
	// 取得serverControl的輸入
	String serverInput;

	// 整個server的程式進入點
	public static void main(String args[]) {
		new NaServer().setUpTheConnection();
	}

	public void setUpTheConnection() {

		// 將元件加入主畫面
		serverFrame.add(showWelcome);
		// 在主畫面中加入layout,以便加上scroller
		serverFrame.setLayout(new BorderLayout(300, 0));
		// 當行的長度大於所分派的寬度時，將換行
		showtext.setLineWrap(true);
		// 當行的長度大於所分派的寬度時，將在邊界（空白）處換行
		showtext.setWrapStyleWord(true);
		// 不可編輯的
		showtext.setEditable(false);
		// 顯示初始文字
		showtext.append("------------記錄系統內容------------\n");

		// JScrollPane
		JScrollPane Scroller = new JScrollPane();
		// 設定垂直滾動
		Scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		// 設定水平滾動
		Scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		Scroller.setViewportView(showtext);
		// 增加滾動
		serverFrame.add(Scroller, BorderLayout.CENTER);
		// layout上方顯示文字
		serverFrame.add(showWelcome, BorderLayout.NORTH);
		// 設定框架大小
		serverFrame.setSize(300, 450);
		// 固定框架大小
		serverFrame.setResizable(false);
		// 讓框架顯現
		serverFrame.setVisible(true);

		// 儲存訊息用的vector
		systemMessage = new Vector();
		try {

			// 產生socket port
			ServerSocket serverSocket = new ServerSocket(8888);
			while (true) {
				// 等待是否有外部連線請求
				Socket socketSide = serverSocket.accept();
				// 建立系統的input/output,此部分是用來取得socketSide的輸出資料
				PrintStream PSwriter = new PrintStream(
						socketSide.getOutputStream());
				// 將元件加入vector
				systemMessage.add(PSwriter);

				// 使用執行緒,如此便可以實現多人連線
				// 此部分負責建立最主要的執行緒
				// 把connectionProcedure這個class new出來
				connectionProcedure connectionProcedure = new connectionProcedure(
						socketSide);
				// 將new出來的class分配進去執行序
				primaryThread = new Thread(connectionProcedure);
				// 啟動主要執行緒
				primaryThread.start();
				// 顯示訊息於server端
				showtext.append("----------------------------------\n");
				showtext.append("一個新使用者加入聊天室了！\n");
				showtext.append("----------------------------------\n");
				showtext.append("目前主機位址：" + socketSide.getLocalSocketAddress()
						+ ("\n") + "目前連線位址：" + socketSide.getInetAddress()
						+ ("\n") + "目前連線埠號：" + socketSide.getLocalPort()
						+ ("\n") + "新成員主機位址："
						+ socketSide.getRemoteSocketAddress() + ("\n")
						+ "目前連線人數：" + (primaryThread.activeCount() - 2)
						+ ("\n"));
				showtext.append("----------------------------------\n");

				// 顯示訊息於client端
				// 先使用string來儲存想要顯示的訊息
				// 再利用擁有推播功能的function:toEveryOne來推播給每位client
				String line = "----------------系統訊息-----------------";
				String welcomeMessage = "一個新使用者加入聊天室了！";
				String onlineMessage = "目前連線人數:";
				String onlinePeopleNum = Integer.toString(primaryThread
						.activeCount() - 2) + "位";
				String line2 = "----------------------------------------";
				connectionProcedure.toEveryOne(line);
				connectionProcedure.toEveryOne(welcomeMessage);
				connectionProcedure.toEveryOne(onlineMessage);
				connectionProcedure.toEveryOne(onlinePeopleNum);
				connectionProcedure.toEveryOne(line2);

			}

		} catch (Exception ex) {
			showtext.append("連線失敗！請檢查是否有正常開啟serverSide&clientSide");
		}
	}

	// 此class負責連線之後的處理程序
	public class connectionProcedure implements Runnable {

		// 此部分是用以暫存資料，採用bufferReader
		BufferedReader bufferedReader;
		// 建立一個暫存socket
		Socket tempSocket;

		// 此部分負責建立起接收的任務
		public connectionProcedure(Socket socketSide) {
			try {
				tempSocket = socketSide;
				// 建立系統的input/output,此部分是用來取得socketSide的輸入資料
				InputStreamReader ISReader = new InputStreamReader(
						tempSocket.getInputStream());

				// new出bufferReader
				bufferedReader = new BufferedReader(ISReader);
			} catch (Exception ex) {
				showtext.append("連線失敗！請檢查是否有正常開啟serverSide&clientSide");
			}
		}

		// 此部分負責執行執行緒
		@Override
		public void run() {
			String Message;
			try {
				// 讀取使用者輸入資料
				while ((Message = bufferedReader.readLine()) != null) {
					showtext.append("來自於使用者：" + Message + "\n");
					toEveryOne(Message);
				}
				// 顯示系統訊息於server端介面
				showtext.append("----------------------------------\n");
				showtext.append("一個使用者離開聊天室了！\n");
				showtext.append("目前連線人數：" + (primaryThread.activeCount() - 3)
						+ ("\n"));
				showtext.append("----------------------------------\n");

				// 顯示系統訊息於client端介面
				// 先使用string來儲存想要顯示的訊息
				// 再利用擁有推播功能的function:toEveryOne來推播給每位client
				String line = "----------------系統訊息-----------------";
				String exitMessage = "一個使用者離開聊天室了！";
				String onlineMessage = "目前連線人數:";
				String onlinePeopleNum = Integer.toString(primaryThread
						.activeCount() - 3) + "位";
				String line2 = "----------------------------------------";
				toEveryOne(line);
				toEveryOne(exitMessage);
				toEveryOne(onlineMessage);
				toEveryOne(onlinePeopleNum);
				toEveryOne(line2);

			} catch (Exception ex) {
			}
		}

		// 此部分用以告訴每位使用者聊天訊息，也就是進行推播的工作。
		public void toEveryOne(String Message) {
			// 使用iterator來存取並輸出集合內的資料
			Iterator iterator = systemMessage.iterator();
			// 判斷集合內部是否還有資料
			while (iterator.hasNext()) {
				try {
					// 取得集合內資料
					PrintStream writer = (PrintStream) iterator.next();
					// 印出
					writer.println(Message);
					// 重新更新緩衝區。
					writer.flush();

				} catch (Exception ex) {
					showtext.append("連線失敗！請檢查是否有正常開啟serverSide&clientSide");
				}
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
