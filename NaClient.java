import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
public class NaClient extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//宣告圖片
	Image imgbackground;
	Image imgBottom;
	//宣告背景畫布
	DrawablePanel mainPanel;
	DrawablePanel panBottom;
    //宣告Socket變數 
	Socket    Mysock;
	//顯示區域
	JTextArea   showtext = new JTextArea(15,45); 
	//輸入區域
	JTextField   input = new JTextField(20);
	//宣告畫布
	JPanel logPanel  = new JPanel();
	//宣號兩個frame一個登入另一個是聊天室主畫面
	JFrame frame =new JFrame("登入");
	JFrame frame2 =new JFrame("聊天室");
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	//宣告一些標籤與使用者輸入物件
	JLabel    jname   = new JLabel("使用者名稱：");   
	JLabel    jip  = new JLabel("ip：");   
	JTextField   jtname   = new JTextField("使用者1號",10);
	JTextField   jtip   = new JTextField("127.0.0.1",10);
	JLabel    userstate  = new JLabel("請輸入資料"); 
	//宣告使用者名子及位置ip
	String    username;
	String    ip="";
	//宣告輸出及輸入變數
	BufferedReader  Breader;           
	PrintStream  Pwriter;
	
	//主程式開始
	public static void main(String[] args){
		NaClient client = new NaClient();       
	}
	//設定登入宣告視窗物件 
	NaClient (){
		//載入圖片
		LoadImage();
		//設定登入背景圖片
		panBottom = new DrawablePanel(imgBottom);
		panBottom.setBounds(0, 0, 300, 450);
		panBottom.setLayout(null);
		//建立視窗JFrame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setSize(300,450);
		frame.setResizable(false);
		frame.getContentPane().add(panBottom);
		frame.setVisible(true);
		//設定物件位置與大小
		jname.setBounds(20,60,80,20);
		jtname.setBounds(110,60,100,20);
		jip.setBounds(75,90,100,20);
		jtip.setBounds(110,90,100,20);
		
		//加入到JPanel
		panBottom.add(jname);
		panBottom.add(jtname);         
		panBottom.add(jip);
		panBottom.add(jtip); 
		
		//登入按鈕
		JButton logbutton = new JButton("登入");
		//按下設定監聽事件
		logbutton.addActionListener(this);
		//設定按鈕位置
		logbutton.setBounds(100,130,100,30);
		//加入畫布
		panBottom.add(logbutton);

		//離開 
		addWindowListener(new WindowAdapter()      
		{
			public void windowClosing(WindowEvent e){
				System.out.println("離開聊天室");
				System.exit(0);
		   }
		});	


	}
	//----------------------------------------------------------背景
	 public void LoadImage() {
		 	imgBottom = toolkit.getImage(NaClient.class
			        .getResource("login2.png"));
		    imgbackground = toolkit.getImage(NaClient.class
		        .getResource("login.png"));
		  }
	//設立連線
	private void MakeConnection(){
		try{
			 //請求建立連線
			 Mysock = new Socket(ip,8888);      
			 //建立I/O資料流與取得輸入資料流
			 InputStreamReader inReader =  new InputStreamReader(Mysock.getInputStream());  
			 //放入暫存區
			 Breader = new BufferedReader(inReader);    
			 //取得Socket的輸出資料流
			   
			 Pwriter = new PrintStream(Mysock.getOutputStream());
			 //連線成功
			 userstate.setText("網路建立-連線成功"); 
			   
			}catch(IOException ex ){
			 System.out.println("建立連線失敗");
			}
	}
	//設定聊天室視窗物件
	public void Myclient(){
		//載入圖片
		LoadImage();
		mainPanel = new DrawablePanel(imgbackground);// 主畫面
		mainPanel.setBounds(0, 0, 300, 300);
		//JButton("送出")
		JButton sButton = new JButton("送出");
		//設定按下監聽事件
		sButton.addActionListener(this);  
		//對話區域-----
		//設定換行條件與物件不可編輯
		showtext.setLineWrap(true);         
		showtext.setWrapStyleWord(true); 
		showtext.setEditable(false); 
		//JScrollPane  
		JScrollPane Scroller = new JScrollPane(showtext);
		//設定垂直滾動  
		Scroller.setVerticalScrollBarPolicy(
		  ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); 
		//設定水平滾動
		Scroller.setHorizontalScrollBarPolicy(
		  ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		//聊天室畫布加入該有物件
		mainPanel.add(Scroller);
		mainPanel.add(input);
		mainPanel.add(sButton);
		//設定聊天室frame物件
		frame2.getContentPane().add(mainPanel);
		frame2.setSize(600,450);
		frame2.setVisible(true);
		frame2.setResizable(false);
		//離開 
		addWindowListener(new WindowAdapter()      
		{
		   public void windowClosing(WindowEvent e){
			   System.out.println("離開聊天室");
			   System.exit(0);
		   }
		});
	}

	
	//按下按鈕之動作事件
	public void actionPerformed(ActionEvent e){
		String str=e.getActionCommand(); 
		//如果按下的是登入按鈕
		if(str.equals("登入")){
			//設定名字
			username = jtname.getText();
			//設定ip，此程式目前無作用
			ip  = jtip.getText();
			//狀態
			userstate.setText("設定"+username+":"+ip); 
			//建立連線----
			MakeConnection();
			//建立接收資料執行緒----
			Thread readerTh = new Thread(new InReader());  
			readerTh.start();
			frame.setVisible(false);
			Myclient();
		}else if(str.equals("送出")){  
			try{//送出資料
				Date date = new Date() ;
				String gs = date.toString();
				String[] AfterSplit = gs.split(" ");

				Pwriter.println(("["+AfterSplit[3]+"] "+username+":"+input.getText())); 
				//刷新該串流的緩衝。
				Pwriter.flush();         
		    }catch(Exception ex ){
		    	System.out.println("送出資料失敗");
		    }
			//清完輸入欄位
		    input.setText("");
		}
	}
	//接收資料
		public class InReader implements Runnable{
			public void run(){
				String message;
				try{
					//把接收到的資料顯示在showtext上
					while ((message = Breader.readLine()) != null){
						showtext.append(message+'\n');
					}
				}catch(Exception ex ){ex.printStackTrace();}
			}
		} 
}