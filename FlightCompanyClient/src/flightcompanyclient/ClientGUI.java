package flightcompanyclient;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import java.awt.CardLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.JCheckBox;

public class ClientGUI extends JFrame {

	private JLayeredPane layeredPane;
	private JPanel panelMain;
	private JPanel panelReg;
	private JPanel contentPane;
	private JPanel panelLogin;
	private JPanel panelRoutesAdmin;
	private JPanel panelRoutes;
	private JButton BookBTN;
	private JButton RemoveBTN;
	private JButton CancelBTN;
	private JTextField textField_NicknameLogin;
	private JTextField textField_PasswordLogin;
	private JTextField textField_Name;
	private JTextField textField_Surname;
	private JTextField textField_NicknameReg;
	private JTextField textFieldFligthIDBook;
	private JTextField textFieldFlightIDCancel;
	private JTextField textField_AmountCharge;
	private JTextField textField_FlightIDAdmin;
	private JTextField textField_FlightIDAdd;
	private JTextField textField_PlaneModelAdd;
	private JTextField textField_DepCityAdd;
	private JTextField textField_ArrCity;
	private JTextField textField_DepTimeAdd;
	private JTextField textField_Delay;
	private JTextField textField_Deal;
	private JButton EnterLoginBTN;
	private JTextPane textPaneRoutes;
	private JTextPane textPaneRoutesAdmin;
	private JButton btnEnterReg;
	private JTextField textField_PasswordReg;
	private JCheckBox chckbxAdmin;
	private JTextPane textPaneRoutesBooked;
	private JButton ChargeBTN;
	private JButton btnEnterAdd;
	private JTextField textField_FlightIdAdmin;
	private JButton btnDelay;
	private JButton btnDeal;
	private JButton btnLogout;
	private JButton btnAccount;
	private JButton btnAdd;
	private JButton btnRoutes;

	private RPCClient rpc;
	private String nickname, response;
	private boolean admin;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI frame = new ClientGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	private void switchPanel(JPanel p) {
		layeredPane.removeAll();
		layeredPane.add(p);
		layeredPane.revalidate();
		layeredPane.repaint();
	}
	
	/**
	 * Create the frame.
	 */
	public ClientGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		layeredPane = new JLayeredPane();
		layeredPane.setBounds(15, 100, 648, 328);
		contentPane.add(layeredPane);
		layeredPane.setLayout(new CardLayout(0, 0));
		
		panelMain = new JPanel();
		panelMain.setBackground(Color.WHITE);
		layeredPane.add(panelMain, "name_790356599619100");
		panelMain.setLayout(null);
		
		JButton btnLogin = new JButton("LOGIN");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanel(panelLogin);
			}
		});
		btnLogin.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnLogin.setBounds(15, 283, 290, 29);
		panelMain.add(btnLogin);
		
		JButton btnRegistration = new JButton("REGISTRATION");
		btnRegistration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				switchPanel(panelReg);
			}
		});
		btnRegistration.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnRegistration.setBounds(343, 283, 290, 29);
		panelMain.add(btnRegistration);
		
		JLabel lblRabbitMQ = new JLabel("");
		lblRabbitMQ.setBounds(15, 36, 618, 200);
		panelMain.add(lblRabbitMQ);
		lblRabbitMQ.setIcon(new ImageIcon("RabbitMQ_logo.png"));
		
		panelLogin = new JPanel();
		layeredPane.add(panelLogin, "name_549009494437700");
		panelLogin.setLayout(null);
		
		JLabel lblUsername = new JLabel("NICKNAME");
		lblUsername.setFont(new Font("Times New Roman", Font.BOLD, 18));
		lblUsername.setBounds(15, 54, 618, 41);
		panelLogin.add(lblUsername);
		
		textField_NicknameLogin = new JTextField();
		textField_NicknameLogin.setFont(new Font("Tahoma", Font.PLAIN, 18));
		textField_NicknameLogin.setBounds(15, 98, 618, 48);
		panelLogin.add(textField_NicknameLogin);
		textField_NicknameLogin.setColumns(10);
		
		JLabel lblPassword = new JLabel("PASSWORD");
		lblPassword.setFont(new Font("Times New Roman", Font.BOLD, 18));
		lblPassword.setBounds(15, 162, 618, 41);
		panelLogin.add(lblPassword);
		
		textField_PasswordLogin = new JTextField();
		textField_PasswordLogin.setFont(new Font("Tahoma", Font.PLAIN, 18));
		textField_PasswordLogin.setColumns(10);
		textField_PasswordLogin.setBounds(15, 206, 618, 48);
		panelLogin.add(textField_PasswordLogin);
		
		EnterLoginBTN = new JButton("ENTER");
		EnterLoginBTN.setBounds(267, 283, 115, 29);
		panelLogin.add(EnterLoginBTN);
		
		panelRoutes = new JPanel();
		layeredPane.add(panelRoutes, "name_549039527560999");
		panelRoutes.setLayout(null);
		
		textPaneRoutes = new JTextPane();
		textPaneRoutes.setText("Routes list...");
		textPaneRoutes.setEditable(false);
		textPaneRoutes.setBounds(0, 0, 648, 286);
		panelRoutes.add(textPaneRoutes);
		
		textFieldFligthIDBook = new JTextField();
		textFieldFligthIDBook.setBounds(120, 293, 397, 30);
		panelRoutes.add(textFieldFligthIDBook);
		textFieldFligthIDBook.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Flight ID");
		lblNewLabel.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblNewLabel.setBounds(0, 293, 115, 30);
		panelRoutes.add(lblNewLabel);
		
		BookBTN = new JButton("Book");
		BookBTN.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		BookBTN.setBounds(533, 293, 115, 30);
		panelRoutes.add(BookBTN);
		
		panelRoutesAdmin = new JPanel();
		layeredPane.add(panelRoutesAdmin, "name_549042519377600");
		panelRoutesAdmin.setLayout(null);
		
		textPaneRoutesAdmin = new JTextPane();
		textPaneRoutesAdmin.setText("Routes list...");
		textPaneRoutesAdmin.setEditable(false);
		textPaneRoutesAdmin.setBounds(0, 0, 648, 286);
		panelRoutesAdmin.add(textPaneRoutesAdmin);
		
		textField_FlightIDAdmin = new JTextField();
		textField_FlightIDAdmin.setColumns(10);
		textField_FlightIDAdmin.setBounds(120, 293, 397, 30);
		panelRoutesAdmin.add(textField_FlightIDAdmin);
		
		JLabel lblNewLabel_3 = new JLabel("Flight ID");
		lblNewLabel_3.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblNewLabel_3.setBounds(0, 293, 115, 30);
		panelRoutesAdmin.add(lblNewLabel_3);
		
		RemoveBTN = new JButton("Remove");
		RemoveBTN.setBounds(533, 293, 115, 29);
		panelRoutesAdmin.add(RemoveBTN);
		RemoveBTN.setFont(new Font("Times New Roman", Font.BOLD, 20));
		
		panelReg = new JPanel();
		layeredPane.add(panelReg, "name_792233717878300");
		panelReg.setLayout(null);
		
		JLabel lblName = new JLabel("Name");
		lblName.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblName.setBounds(15, 19, 161, 30);
		panelReg.add(lblName);
		
		textField_Name = new JTextField();
		textField_Name.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_Name.setBounds(191, 19, 442, 30);
		panelReg.add(textField_Name);
		textField_Name.setColumns(10);
		
		JLabel lblSurname = new JLabel("Surname");
		lblSurname.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblSurname.setBounds(15, 70, 161, 30);
		panelReg.add(lblSurname);
		
		textField_Surname = new JTextField();
		textField_Surname.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_Surname.setColumns(10);
		textField_Surname.setBounds(191, 70, 442, 30);
		panelReg.add(textField_Surname);
		
		JLabel lblNickname = new JLabel("Nickname");
		lblNickname.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblNickname.setBounds(15, 123, 161, 30);
		panelReg.add(lblNickname);
		
		textField_NicknameReg = new JTextField();
		textField_NicknameReg.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_NicknameReg.setColumns(10);
		textField_NicknameReg.setBounds(191, 123, 442, 30);
		panelReg.add(textField_NicknameReg);
		
		JLabel lblPassword_1 = new JLabel("Password");
		lblPassword_1.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblPassword_1.setBounds(15, 181, 161, 30);
		panelReg.add(lblPassword_1);
		
		btnEnterReg = new JButton("ENTER");
		btnEnterReg.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnEnterReg.setBounds(261, 283, 115, 29);
		panelReg.add(btnEnterReg);
		
		textField_PasswordReg = new JTextField();
		textField_PasswordReg.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_PasswordReg.setColumns(10);
		textField_PasswordReg.setBounds(191, 183, 442, 30);
		panelReg.add(textField_PasswordReg);
		
		chckbxAdmin = new JCheckBox(" Admin");
		chckbxAdmin.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		chckbxAdmin.setBounds(15, 225, 214, 51);
		panelReg.add(chckbxAdmin);
		
		JPanel panelAcc = new JPanel();
		panelAcc.setLayout(null);
		layeredPane.add(panelAcc, "name_793591725563100");
		
		textPaneRoutesBooked = new JTextPane();
		textPaneRoutesBooked.setText("Booked routes list...");
		textPaneRoutesBooked.setEditable(false);
		textPaneRoutesBooked.setBounds(0, 26, 648, 224);
		panelAcc.add(textPaneRoutesBooked);
		
		textFieldFlightIDCancel = new JTextField();
		textFieldFlightIDCancel.setColumns(10);
		textFieldFlightIDCancel.setBounds(120, 259, 397, 30);
		panelAcc.add(textFieldFlightIDCancel);
		
		JLabel lblNewLabel_2 = new JLabel("Flight ID");
		lblNewLabel_2.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblNewLabel_2.setBounds(0, 259, 115, 30);
		panelAcc.add(lblNewLabel_2);
		
		CancelBTN = new JButton("Cancel");
		CancelBTN.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		CancelBTN.setBounds(533, 259, 115, 29);
		panelAcc.add(CancelBTN);
		
		JLabel lblNewLabel_2_1 = new JLabel("Amount");
		lblNewLabel_2_1.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblNewLabel_2_1.setBounds(0, 298, 115, 30);
		panelAcc.add(lblNewLabel_2_1);
		
		textField_AmountCharge = new JTextField();
		textField_AmountCharge.setColumns(10);
		textField_AmountCharge.setBounds(120, 298, 397, 30);
		panelAcc.add(textField_AmountCharge);
		
		ChargeBTN = new JButton("Charge");
		ChargeBTN.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		ChargeBTN.setBounds(533, 298, 115, 29);
		panelAcc.add(ChargeBTN);
		
		JLabel lblNewLabel_1 = new JLabel("Routes booked");
		lblNewLabel_1.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		lblNewLabel_1.setBounds(0, 0, 143, 20);
		panelAcc.add(lblNewLabel_1);
		
		JPanel panelAdd = new JPanel();
		layeredPane.add(panelAdd, "name_794760049019300");
		panelAdd.setLayout(null);
		
		JLabel lblFlightID = new JLabel("Flight ID");
		lblFlightID.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblFlightID.setBounds(15, 16, 116, 29);
		panelAdd.add(lblFlightID);
		
		textField_FlightIDAdd = new JTextField();
		textField_FlightIDAdd.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_FlightIDAdd.setBounds(174, 20, 459, 26);
		panelAdd.add(textField_FlightIDAdd);
		textField_FlightIDAdd.setColumns(10);
		
		JLabel lblPlaneModel = new JLabel("Plane Model");
		lblPlaneModel.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblPlaneModel.setBounds(15, 61, 144, 29);
		panelAdd.add(lblPlaneModel);
		
		textField_PlaneModelAdd = new JTextField();
		textField_PlaneModelAdd.setToolTipText("BOEING_737, AIRBUS_A320, EMBRAER");
		textField_PlaneModelAdd.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_PlaneModelAdd.setColumns(10);
		textField_PlaneModelAdd.setBounds(174, 65, 459, 26);
		panelAdd.add(textField_PlaneModelAdd);
		
		JLabel lblDepCity = new JLabel("Dep City");
		lblDepCity.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblDepCity.setBounds(15, 106, 116, 29);
		panelAdd.add(lblDepCity);
		
		textField_DepCityAdd = new JTextField();
		textField_DepCityAdd.setToolTipText("NAPOLI, MILANO, ROMA, FIRENZE, TORINO, LECCE, PALERMO, CAGLIARI");
		textField_DepCityAdd.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_DepCityAdd.setColumns(10);
		textField_DepCityAdd.setBounds(174, 110, 459, 26);
		panelAdd.add(textField_DepCityAdd);
		
		JLabel lblArrCity = new JLabel("Arr City");
		lblArrCity.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblArrCity.setBounds(15, 151, 116, 29);
		panelAdd.add(lblArrCity);
		
		textField_ArrCity = new JTextField();
		textField_ArrCity.setToolTipText("NAPOLI, MILANO, ROMA, FIRENZE, TORINO, LECCE, PALERMO, CAGLIARI");
		textField_ArrCity.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_ArrCity.setColumns(10);
		textField_ArrCity.setBounds(174, 155, 459, 26);
		panelAdd.add(textField_ArrCity);
		
		JLabel lblDepTime = new JLabel("Dep Time");
		lblDepTime.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblDepTime.setBounds(15, 196, 116, 29);
		panelAdd.add(lblDepTime);
		
		textField_DepTimeAdd = new JTextField();
		textField_DepTimeAdd.setToolTipText("yyyy-MM-dd HH:mm");
		textField_DepTimeAdd.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_DepTimeAdd.setColumns(10);
		textField_DepTimeAdd.setBounds(174, 200, 459, 26);
		panelAdd.add(textField_DepTimeAdd);
		
		btnEnterAdd = new JButton("ENTER");
		btnEnterAdd.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		btnEnterAdd.setBounds(263, 283, 115, 29);
		panelAdd.add(btnEnterAdd);
		
		JPanel panelAccAdmin = new JPanel();
		layeredPane.add(panelAccAdmin, "name_795598805360000");
		panelAccAdmin.setLayout(null);
		
		JLabel lblNewLabel_4 = new JLabel("Flight ID");
		lblNewLabel_4.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblNewLabel_4.setBounds(15, 105, 154, 29);
		panelAccAdmin.add(lblNewLabel_4);
		
		textField_FlightIdAdmin = new JTextField();
		textField_FlightIdAdmin.setBounds(184, 105, 449, 30);
		panelAccAdmin.add(textField_FlightIdAdmin);
		textField_FlightIdAdmin.setColumns(10);
		
		btnDelay = new JButton("SET DELAY");
		btnDelay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnDelay.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		btnDelay.setBounds(15, 249, 280, 29);
		panelAccAdmin.add(btnDelay);
		
		btnDeal = new JButton("SET DEAL");
		btnDeal.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		btnDeal.setBounds(353, 249, 280, 29);
		panelAccAdmin.add(btnDeal);
		
		JLabel lblNewLabel_5 = new JLabel("Minutes");
		lblNewLabel_5.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblNewLabel_5.setBounds(15, 182, 280, 29);
		panelAccAdmin.add(lblNewLabel_5);
		
		textField_Delay = new JTextField();
		textField_Delay.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_Delay.setBounds(15, 212, 280, 35);
		panelAccAdmin.add(textField_Delay);
		textField_Delay.setColumns(10);
		
		JLabel lblNewLabel_5_1 = new JLabel("Deal percentage");
		lblNewLabel_5_1.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblNewLabel_5_1.setBounds(353, 182, 280, 29);
		panelAccAdmin.add(lblNewLabel_5_1);
		
		textField_Deal = new JTextField();
		textField_Deal.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_Deal.setColumns(10);
		textField_Deal.setBounds(353, 212, 280, 35);
		panelAccAdmin.add(textField_Deal);
		
		btnRoutes = new JButton("ROUTES");
		btnRoutes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(admin)
					switchPanel(panelRoutesAdmin);
				else
					switchPanel(panelRoutes);
			}
		});
		btnRoutes.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnRoutes.setBounds(368, 16, 145, 29);
		contentPane.add(btnRoutes);
		
		btnAccount = new JButton("ACCOUNT");
		btnAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(admin)
					switchPanel(panelAccAdmin);
				else
					switchPanel(panelAcc);
			}
		});
		btnAccount.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnAccount.setBounds(528, 16, 135, 29);
		contentPane.add(btnAccount);
		
		btnAdd = new JButton("ADD");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanel(panelAdd);
			}
		});
		btnAdd.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnAdd.setBounds(368, 55, 145, 29);
		contentPane.add(btnAdd);
		
		btnLogout = new JButton("LOGOUT");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JSONObject jo = new JSONObject();
				try {
					jo.put("command", "logout");
	    			jo.put("nickname", nickname); 
	    			response = rpc.call(jo.toString());
				} catch (Exception e1) {
					System.out.println(e1);
				}
				if(response.equals("User logout completed"))
					switchPanel(panelMain);
				else
					JOptionPane.showMessageDialog(null, response);
			}
		});
		btnLogout.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnLogout.setBounds(528, 54, 135, 29);
		contentPane.add(btnLogout);
		
		/*
        try {
        	rpc = new RPCClient();
        } catch (IOException | TimeoutException | JSONException | InterruptedException e) {
        	System.out.println(e);
        }*/
	}
}
