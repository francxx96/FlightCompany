package flightcompanyclient;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implements the graphical interface used by the client to access the flight company's services
 * @author Emilio, Francesco
 */
public class ClientGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final String dataTimeRegex = "^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]) (0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$";

	private JLayeredPane layeredPane;
	private JPanel panelMain;
	private JPanel panelRegistration;
	private JPanel contentPane;
	private JPanel panelLogin;
	private JPanel panelRoutes;
	private JTextField textField_NicknameLogin;
	private JTextField textField_PasswordLogin;
	private JTextField textField_NameReg;
	private JTextField textField_SurnameReg;
	private JTextField textField_NicknameReg;
	private JTextField textField_FlightIDCustomer;
	private JTextField textField_AmountCharge;
	private JTextField textField_FlightIDAdd;
	private JTextField textField_PlaneModelAdd;
	private JTextField textField_DepCityAdd;
	private JTextField textField_ArrCityAdd;
	private JTextField textField_DepTimeAdd;
	private JTextField textField_Delay;
	private JTextField textField_Deal;
	private JTextField textField_PasswordReg;
	private JCheckBox chckbxAdminReg;
	private JTextField textField_FlightIDAdmin;
	private JButton btnLogout;
	private JButton btnAccount;
	private JButton btnAdd;
	private JButton btnRoutes;

	private RPCClient rpc;
	private String nickname, response;
	private boolean admin;
	private JPanel panelAccAdmin;
	private JPanel panelAdd;
	private JPanel panelAccCustomer;
	private JTextField textField_DepCitySearch;
	private JTextField textField_ArrCitySearch;
	private JTextField textField_DepTimeSearch;
	private TextArea textAreaBooked;
	private JLabel lblNicknameMain;
	private JLabel lblAmount;
	
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
	
	/**
	 * Shows a panel with the notification received
	 * @param msg content of the notification
	 */
	public static void showNotify(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}
	
	/**
	 * Switch the current visible panel with the input panel
	 * @param p the panel to set
	 */
	private void switchPanel(JPanel p) {
		layeredPane.removeAll();
		layeredPane.add(p);
		layeredPane.revalidate();
		layeredPane.repaint();
	}
	
	/**
	 * Create the frame and starts the application
	 */
	public ClientGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
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
		btnLogin.setForeground(new Color(255, 99, 71));
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanel(panelLogin);
			}
		});
		btnLogin.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnLogin.setBounds(15, 283, 290, 29);
		panelMain.add(btnLogin);
		
		JButton btnRegistration = new JButton("REGISTRATION");
		btnRegistration.setForeground(new Color(255, 99, 71));
		btnRegistration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				switchPanel(panelRegistration);
			}
		});
		btnRegistration.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnRegistration.setBounds(343, 283, 290, 29);
		panelMain.add(btnRegistration);
		
		JLabel lblRabbitMQ = new JLabel("");
		lblRabbitMQ.setBounds(15, 36, 618, 200);
		panelMain.add(lblRabbitMQ);
		lblRabbitMQ.setIcon(new ImageIcon(ClientGUI.class.getResource("/RabbitMQ_logo.png")));
		
		panelLogin = new JPanel();
		layeredPane.add(panelLogin, "name_549009494437700");
		panelLogin.setLayout(null);
		
		JLabel lblNicknameLogin = new JLabel("NICKNAME");
		lblNicknameLogin.setFont(new Font("Times New Roman", Font.BOLD, 18));
		lblNicknameLogin.setBounds(15, 54, 618, 41);
		panelLogin.add(lblNicknameLogin);
		
		textField_NicknameLogin = new JTextField();
		textField_NicknameLogin.setFont(new Font("Tahoma", Font.PLAIN, 18));
		textField_NicknameLogin.setBounds(15, 98, 618, 48);
		panelLogin.add(textField_NicknameLogin);
		textField_NicknameLogin.setColumns(10);
		
		JLabel lblPasswordLogin = new JLabel("PASSWORD");
		lblPasswordLogin.setFont(new Font("Times New Roman", Font.BOLD, 18));
		lblPasswordLogin.setBounds(15, 162, 618, 41);
		panelLogin.add(lblPasswordLogin);
		
		textField_PasswordLogin = new JTextField();
		textField_PasswordLogin.setFont(new Font("Tahoma", Font.PLAIN, 18));
		textField_PasswordLogin.setColumns(10);
		textField_PasswordLogin.setBounds(15, 206, 618, 48);
		panelLogin.add(textField_PasswordLogin);
		
		JButton btnEnterLogin = new JButton("ENTER");
		btnEnterLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField_NicknameLogin.getText().matches("^[a-zA-Z0-9]{2,}$")){
					JOptionPane.showMessageDialog(null, "Please enter a nickname with only alphanumeric characters (at least two)");
					return;
				}
				if(!textField_PasswordLogin.getText().matches("^.{5,}$")){
					JOptionPane.showMessageDialog(null, "Please enter a password of at least 5 characters");
					return;
				}
				
				JSONObject jo = new JSONObject();
				try {
					jo.put("command", "login");
	    			jo.put("nickname", textField_NicknameLogin.getText());
	    			jo.put("password", textField_PasswordLogin.getText());
	    			response = rpc.call(jo.toString());
	    			JOptionPane.showMessageDialog(null, response);
	    			
	    			if(response.equals("Admin login completed")) {
	    				nickname = jo.getString("nickname");
	    				admin = true;
		    			btnAccount.setVisible(true);
		    			btnRoutes.setVisible(true);
		    			btnAdd.setVisible(true);
		    			btnLogout.setVisible(true);
		    			lblNicknameMain.setText(nickname);
		    			lblNicknameMain.setVisible(true);
		    			switchPanel(panelAccAdmin);
	    			} else if(response.equals("Customer login completed")) {
	    				nickname = jo.getString("nickname");
	    				admin = false;	
	    				rpc.subscribeNotification();
	    				btnAccount.setVisible(true);
		    			btnRoutes.setVisible(true);
						btnLogout.setVisible(true);
		    			lblNicknameMain.setText(nickname);
		    			lblNicknameMain.setVisible(true);
		    			
						jo = new JSONObject();
						try {
		        			jo.put("command", "bookedFlight");
		        			jo.put("nickname", nickname);
			    			response = rpc.call(jo.toString());
			    			
							if(response.contains("[Error]"))
								JOptionPane.showMessageDialog(null, response);
							else {
								textAreaBooked.setText(response);
							}
						} catch (Exception ex) {
							System.out.println(ex);
						}
						
						jo = new JSONObject();
						try {
		        			jo.put("command", "charge");
		        			jo.put("amount", "0");
		        			jo.put("nickname", nickname);
			    			response = rpc.call(jo.toString());
			    			
			    			if(response.contains("[Error]"))
								JOptionPane.showMessageDialog(null, response);
							else {
			    				String total = response.substring(response.lastIndexOf(" "));
			    				lblAmount.setText(total);
							}
						} catch (Exception ex) {
							System.out.println(ex);
						}
						
		    			switchPanel(panelAccCustomer);
	    			}
	    			
	    			textField_NicknameLogin.setText("");
	    			textField_PasswordLogin.setText("");
	    				
				} catch (Exception ex) {
					System.out.println(ex);
				}
			}
		});
		btnEnterLogin.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnEnterLogin.setBounds(267, 283, 115, 29);
		panelLogin.add(btnEnterLogin);
		
		panelRoutes = new JPanel();
		layeredPane.add(panelRoutes, "name_549039527560999");
		panelRoutes.setLayout(null);
		
		TextArea textAreaRoutes = new TextArea();
		textAreaRoutes.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		textAreaRoutes.setBounds(0, 0, 648, 328);
		panelRoutes.add(textAreaRoutes);
		
		panelRegistration = new JPanel();
		layeredPane.add(panelRegistration, "name_792233717878300");
		panelRegistration.setLayout(null);
		
		JLabel lblName = new JLabel("Name");
		lblName.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblName.setBounds(15, 19, 161, 30);
		panelRegistration.add(lblName);
		
		textField_NameReg = new JTextField();
		textField_NameReg.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_NameReg.setBounds(191, 19, 442, 30);
		panelRegistration.add(textField_NameReg);
		textField_NameReg.setColumns(10);
		
		JLabel lblSurname = new JLabel("Surname");
		lblSurname.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblSurname.setBounds(15, 70, 161, 30);
		panelRegistration.add(lblSurname);
		
		textField_SurnameReg = new JTextField();
		textField_SurnameReg.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_SurnameReg.setColumns(10);
		textField_SurnameReg.setBounds(191, 70, 442, 30);
		panelRegistration.add(textField_SurnameReg);
		
		JLabel lblNicknameReg = new JLabel("Nickname");
		lblNicknameReg.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblNicknameReg.setBounds(15, 123, 161, 30);
		panelRegistration.add(lblNicknameReg);
		
		textField_NicknameReg = new JTextField();
		textField_NicknameReg.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_NicknameReg.setColumns(10);
		textField_NicknameReg.setBounds(191, 123, 442, 30);
		panelRegistration.add(textField_NicknameReg);
		
		JLabel lblPasswordReg = new JLabel("Password");
		lblPasswordReg.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblPasswordReg.setBounds(15, 181, 161, 30);
		panelRegistration.add(lblPasswordReg);
		
		JButton btnEnterReg = new JButton("ENTER");
		btnEnterReg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(!textField_NameReg.getText().matches("^[A-Z]{1}[a-z]{1,20}$")){
					JOptionPane.showMessageDialog(null, "Please enter a name of at least two letters without special characters");
					return;
				}	
				if(!textField_SurnameReg.getText().matches("^[A-Z]{1}[a-z]{1,20}$")){
					JOptionPane.showMessageDialog(null, "Please enter a surname of at least two letters without special characters");
					return;
				}
				if(!textField_NicknameReg.getText().matches("^[a-zA-Z0-9]{2,}$")){
					JOptionPane.showMessageDialog(null, "Please enter a nickname with only alphanumeric characters (at least two)");
					return;
				}
				if(!textField_PasswordReg.getText().matches("^.{5,}$")){
					JOptionPane.showMessageDialog(null, "Please enter a password of at least 5 characters");
					return;
				}
				
				JSONObject jo = new JSONObject();
				try {
					jo.put("command", "registration");
					jo.put("name", textField_NameReg.getText());
					jo.put("surname", textField_SurnameReg.getText());
        			jo.put("nickname", textField_NicknameReg.getText());
        			jo.put("password", textField_PasswordReg.getText());  
        			jo.put("admin", chckbxAdminReg.isSelected() + ""); 
	    			response = rpc.call(jo.toString());
	    			JOptionPane.showMessageDialog(null, response);
	    			
					if(!response.contains("[Error]")) {
	    				textField_NameReg.setText("");
	    				textField_SurnameReg.setText("");
	    				textField_NicknameReg.setText("");
	    				textField_PasswordReg.setText("");
	    				chckbxAdminReg.setSelected(false);
	    				switchPanel(panelLogin);
	    			} 
				} catch (Exception ex) {
					System.out.println(ex);
				}
			}
		});
		btnEnterReg.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnEnterReg.setBounds(261, 283, 115, 29);
		panelRegistration.add(btnEnterReg);
		
		textField_PasswordReg = new JTextField();
		textField_PasswordReg.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_PasswordReg.setColumns(10);
		textField_PasswordReg.setBounds(191, 183, 442, 30);
		panelRegistration.add(textField_PasswordReg);
		
		chckbxAdminReg = new JCheckBox(" Admin");
		chckbxAdminReg.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		chckbxAdminReg.setBounds(15, 225, 214, 51);
		panelRegistration.add(chckbxAdminReg);
		
		panelAccCustomer = new JPanel();
		panelAccCustomer.setLayout(null);
		layeredPane.add(panelAccCustomer, "name_793591725563100");
		
		textField_FlightIDCustomer = new JTextField();
		textField_FlightIDCustomer.setColumns(10);
		textField_FlightIDCustomer.setBounds(120, 259, 283, 30);
		panelAccCustomer.add(textField_FlightIDCustomer);
		
		JLabel lblNewLabel_2 = new JLabel("Flight ID");
		lblNewLabel_2.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblNewLabel_2.setBounds(0, 259, 115, 30);
		panelAccCustomer.add(lblNewLabel_2);
		
		JButton CancelBTN = new JButton("Cancel");
		CancelBTN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField_FlightIDCustomer.getText().matches("^[0-9]+$")){
					JOptionPane.showMessageDialog(null, "Please enter a numeric flight ID");
					return;
				}
				
				JSONObject jo = new JSONObject();
				try {
        			jo.put("command", "cancelFlight");
	    			jo.put("flightId", textField_FlightIDCustomer.getText());
	    			jo.put("nickname", nickname);
	    			response = rpc.call(jo.toString());
	    			JOptionPane.showMessageDialog(null, response);
	    			
	    			if(!response.contains("[Error]")) {
	    				textField_FlightIDCustomer.setText("");
	    				jo = new JSONObject();
						try {
		        			jo.put("command", "bookedFlight");
		        			jo.put("nickname", nickname);
			    			response = rpc.call(jo.toString());
			    			
							if(!response.contains("[Error]")) {
								textAreaBooked.setText(response);
							}
						} catch (Exception ex) {
							System.out.println(ex);
						}
						
						jo = new JSONObject();
						try {
		        			jo.put("command", "charge");
		        			jo.put("amount", "0");
		        			jo.put("nickname", nickname);
			    			response = rpc.call(jo.toString());
			    			
			    			if(!response.contains("[Error]")) {
			    				String total = response.substring(response.lastIndexOf(" "));
			    				lblAmount.setText(total);
							}
						} catch (Exception ex) {
							System.out.println(ex);
						}
	    			} 
				} catch (Exception ex) {
					System.out.println(ex);
				}
			}
		});
		CancelBTN.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		CancelBTN.setBounds(533, 259, 115, 30);
		panelAccCustomer.add(CancelBTN);
		
		JLabel lblNewLabel_2_1 = new JLabel("Amount");
		lblNewLabel_2_1.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblNewLabel_2_1.setBounds(0, 298, 115, 30);
		panelAccCustomer.add(lblNewLabel_2_1);
		
		textField_AmountCharge = new JTextField();
		textField_AmountCharge.setColumns(10);
		textField_AmountCharge.setBounds(273, 298, 130, 30);
		panelAccCustomer.add(textField_AmountCharge);
		
		JButton ChargeBTN = new JButton("Charge");
		ChargeBTN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField_AmountCharge.getText().matches("^[0-9]+(,[0,9]{1,2})?$")){
					JOptionPane.showMessageDialog(null, "Please complete amount correctly");
					return;
				}
				
				JSONObject jo = new JSONObject();
				try {
        			jo.put("command", "charge");
        			jo.put("amount", textField_AmountCharge.getText());
        			jo.put("nickname", nickname);
	    			response = rpc.call(jo.toString());
	    			
	    			if(!response.contains("[Error]")) {
	    				String total = response.substring(response.lastIndexOf(" "));
						JOptionPane.showMessageDialog(null, response);
	    				textField_AmountCharge.setText("");
	    				lblAmount.setText(total);
					}
				} catch (Exception ex) {
					System.out.println(ex);
				}
			}
		});
		ChargeBTN.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		ChargeBTN.setBounds(418, 298, 230, 29);
		panelAccCustomer.add(ChargeBTN);
		
		JButton BookBTN = new JButton("Book");
		BookBTN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField_FlightIDCustomer.getText().matches("^[0-9]+$")){
					JOptionPane.showMessageDialog(null, "Please enter a numeric flight ID");
					return;
				}
				
				JSONObject jo = new JSONObject();
				try {
					jo.put("command", "bookFlight");
	    			jo.put("flightId", textField_FlightIDCustomer.getText());
	    			jo.put("nickname", nickname);
	    			response = rpc.call(jo.toString());
	    			JOptionPane.showMessageDialog(null, response);
	    			
					if(!response.contains("[Error]")) {
	    				textField_FlightIDCustomer.setText("");
	    				jo = new JSONObject();
						try {
		        			jo.put("command", "bookedFlight");
		        			jo.put("nickname", nickname);
			    			response = rpc.call(jo.toString());
			    			
							if(!response.contains("[Error]")) {
								textAreaBooked.setText(response);
							}
						} catch (Exception ex) {
							System.out.println(ex);
						}
						
						jo = new JSONObject();
						try {
		        			jo.put("command", "charge");
		        			jo.put("amount", "0");
		        			jo.put("nickname", nickname);
			    			response = rpc.call(jo.toString());
			    			
			    			if(!response.contains("[Error]")) {
			    				String total = response.substring(response.lastIndexOf(" "));
			    				lblAmount.setText(total);
							}
						} catch (Exception ex) {
							System.out.println(ex);
						}
	    			} 
				} catch (Exception ex) {
					System.out.println(ex);
				}
			}
		});
		BookBTN.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		BookBTN.setBounds(418, 259, 115, 30);
		panelAccCustomer.add(BookBTN);
		
		textAreaBooked = new TextArea();
		textAreaBooked.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		textAreaBooked.setBounds(0, 0, 648, 253);
		panelAccCustomer.add(textAreaBooked);
		
		lblAmount = new JLabel("0,00");
		lblAmount.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblAmount.setBounds(120, 298, 138, 30);
		panelAccCustomer.add(lblAmount);
		
		panelAdd = new JPanel();
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
		
		textField_ArrCityAdd = new JTextField();
		textField_ArrCityAdd.setToolTipText("NAPOLI, MILANO, ROMA, FIRENZE, TORINO, LECCE, PALERMO, CAGLIARI");
		textField_ArrCityAdd.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_ArrCityAdd.setColumns(10);
		textField_ArrCityAdd.setBounds(174, 155, 459, 26);
		panelAdd.add(textField_ArrCityAdd);
		
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
		
		JButton btnEnterAdd = new JButton("ENTER");
		btnEnterAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField_FlightIDAdd.getText().matches("^[0-9]+$") | 
						textField_PlaneModelAdd.getText().matches("") | 
						textField_DepCityAdd.getText().matches("") | 
						textField_ArrCityAdd.getText().matches("") | 
						!textField_DepTimeAdd.getText().matches(dataTimeRegex)){
					JOptionPane.showMessageDialog(null, "Please complete all fields correctly");
					return;
				}
				
				JSONObject jo = new JSONObject();
				try {
					jo.put("command", "addFlight");
        			jo.put("flightId", textField_FlightIDAdd.getText());
        			jo.put("planeModel", textField_PlaneModelAdd.getText());
					jo.put("depCity", textField_DepCityAdd.getText());
					jo.put("arrCity", textField_ArrCityAdd.getText()); 
					jo.put("depTime", textField_DepTimeAdd.getText());
        			jo.put("nickname", nickname);
	    			
	    			response = rpc.call(jo.toString());
	    			JOptionPane.showMessageDialog(null, response);
	    			
					if(!response.contains("[Error]")) {
						textField_FlightIDAdd.setText(""); 
						textField_PlaneModelAdd.setText("");
						textField_DepCityAdd.setText("");
						textField_ArrCityAdd.setText(""); 
						textField_DepTimeAdd.setText("");
	    			} 
				} catch (Exception ex) {
					System.out.println(ex);
				}
			}
		});
		btnEnterAdd.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnEnterAdd.setBounds(263, 283, 115, 29);
		panelAdd.add(btnEnterAdd);
		
		panelAccAdmin = new JPanel();
		layeredPane.add(panelAccAdmin, "name_795598805360000");
		panelAccAdmin.setLayout(null);
		
		JLabel lblNewLabel_4 = new JLabel("Flight ID to modify");
		lblNewLabel_4.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblNewLabel_4.setBounds(15, 105, 250, 29);
		panelAccAdmin.add(lblNewLabel_4);
		
		textField_FlightIDAdmin = new JTextField();
		textField_FlightIDAdmin.setBounds(280, 105, 353, 30);
		panelAccAdmin.add(textField_FlightIDAdmin);
		textField_FlightIDAdmin.setColumns(10);
		
		JButton btnDelay = new JButton("SET DELAY");
		btnDelay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField_FlightIDAdmin.getText().matches("^[0-9]+$") |
						!textField_Delay.getText().matches("^[0-9]+$")){
					JOptionPane.showMessageDialog(null, "Please complete fields correctly");
					return;
				}
				
				JSONObject jo = new JSONObject();
				try {
        			jo.put("command", "putDelay");
        			jo.put("flightId", textField_FlightIDAdmin.getText());
        			jo.put("minutes", textField_Delay.getText());
        			jo.put("nickname", nickname);
	    			response = rpc.call(jo.toString());
					JOptionPane.showMessageDialog(null, response);
					
					if(!response.contains("[Error]")) {
						textField_FlightIDAdmin.setText("");
						textField_Delay.setText("");
					}
					
				} catch (Exception ex) {
					System.out.println(ex);
				}	
			}
		});
		btnDelay.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		btnDelay.setBounds(15, 249, 280, 29);
		panelAccAdmin.add(btnDelay);
		
		JButton btnDeal = new JButton("SET DEAL");
		btnDeal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField_FlightIDAdmin.getText().matches("^[0-9]+$") |
						!textField_Deal.getText().matches("^[0-9]+(,[0-9]+)?$")){
					JOptionPane.showMessageDialog(null, "Please complete fields correctly");
					return;
				}
				
				JSONObject jo = new JSONObject();
				try {
        			jo.put("command", "putDeal");
        			jo.put("flightId", textField_FlightIDAdmin.getText());
        			jo.put("dealPerc", textField_Deal.getText());
        			jo.put("nickname", nickname);
	    			response = rpc.call(jo.toString());
					JOptionPane.showMessageDialog(null, response);
					
					if(!response.contains("[Error]")) {
						textField_FlightIDAdmin.setText("");
						textField_Deal.setText("");
					}
					
				} catch (Exception ex) {
					System.out.println(ex);
				}	
			}
		});
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
		
		JButton btnRemoveFligth = new JButton("REMOVE FLIGHT");
		btnRemoveFligth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField_FlightIDAdmin.getText().matches("^[0-9]+$")){
					JOptionPane.showMessageDialog(null, "Please complete the field correctly");
					return;
				}
				
				JSONObject jo = new JSONObject();
				try {
        			jo.put("command", "removeFlight");
        			jo.put("flightId", textField_FlightIDAdmin.getText());
        			jo.put("nickname", nickname);
	    			response = rpc.call(jo.toString());
					JOptionPane.showMessageDialog(null, response);
					
					if(!response.contains("[Error]")) {
						textField_FlightIDAdmin.setText("");
					}
					
				} catch (Exception ex) {
					System.out.println(ex);
				}	
			}
		});
		btnRemoveFligth.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnRemoveFligth.setBounds(15, 299, 618, 29);
		panelAccAdmin.add(btnRemoveFligth);
		
		JPanel panelSearchRoutes = new JPanel();
		layeredPane.add(panelSearchRoutes, "name_1052909412293300");
		panelSearchRoutes.setLayout(null);
		
		JLabel lblDepCity_1 = new JLabel("Dep City");
		lblDepCity_1.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblDepCity_1.setBounds(15, 68, 116, 29);
		panelSearchRoutes.add(lblDepCity_1);
		
		textField_DepCitySearch = new JTextField();
		textField_DepCitySearch.setToolTipText("NAPOLI, MILANO, ROMA, FIRENZE, TORINO, LECCE, PALERMO, CAGLIARI");
		textField_DepCitySearch.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_DepCitySearch.setColumns(10);
		textField_DepCitySearch.setBounds(174, 72, 459, 26);
		panelSearchRoutes.add(textField_DepCitySearch);
		
		JLabel lblArrCity_1 = new JLabel("Arr City");
		lblArrCity_1.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblArrCity_1.setBounds(15, 113, 116, 29);
		panelSearchRoutes.add(lblArrCity_1);
		
		textField_ArrCitySearch = new JTextField();
		textField_ArrCitySearch.setToolTipText("NAPOLI, MILANO, ROMA, FIRENZE, TORINO, LECCE, PALERMO, CAGLIARI");
		textField_ArrCitySearch.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_ArrCitySearch.setColumns(10);
		textField_ArrCitySearch.setBounds(174, 117, 459, 26);
		panelSearchRoutes.add(textField_ArrCitySearch);
		
		JLabel lblDepTime_1 = new JLabel("Dep Time");
		lblDepTime_1.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		lblDepTime_1.setBounds(15, 158, 116, 29);
		panelSearchRoutes.add(lblDepTime_1);
		
		textField_DepTimeSearch = new JTextField();
		textField_DepTimeSearch.setToolTipText("yyyy-MM-dd HH:mm");
		textField_DepTimeSearch.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textField_DepTimeSearch.setColumns(10);
		textField_DepTimeSearch.setBounds(174, 162, 459, 26);
		panelSearchRoutes.add(textField_DepTimeSearch);
		
		JButton btnSearchRoutes = new JButton("SEARCH ROUTES");
		btnSearchRoutes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(textField_DepCitySearch.getText().matches("") | 
						textField_ArrCitySearch.getText().matches("") | 
						!textField_DepTimeSearch.getText().matches(dataTimeRegex)){
					JOptionPane.showMessageDialog(null, "Please complete all fields correctly");
					return;
				}
				
				JSONObject jo = new JSONObject();
				try {
        			jo.put("command", "searchRoutes");
        			jo.put("depCity", textField_DepCitySearch.getText());
        			jo.put("arrCity", textField_ArrCitySearch.getText()); 
        			jo.put("depTime", textField_DepTimeSearch.getText()); //LocalDateTime
	    			response = rpc.call(jo.toString());
					
	    			textField_DepCitySearch.setText("");
        			textField_ArrCitySearch.setText(""); 
        			textField_DepTimeSearch.setText("");
        			textAreaRoutes.setText(response);
					switchPanel(panelRoutes);
				} catch (Exception ex) {
					System.out.println(ex);
				}
			}
		});
		btnSearchRoutes.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnSearchRoutes.setBounds(139, 245, 350, 29);
		panelSearchRoutes.add(btnSearchRoutes);
		
		btnRoutes = new JButton("ROUTES");
		btnRoutes.setForeground(new Color(255, 99, 71));
		btnRoutes.setBackground(SystemColor.menu);
		btnRoutes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JSONObject jo = new JSONObject();
				if(admin) {
					try {
						jo.put("command", "allFlights");
						response = rpc.call(jo.toString());
						textAreaRoutes.setText(response);
						switchPanel(panelRoutes);
					} catch (Exception ex) {
						System.out.println(ex);
					}
				}
				else {
					switchPanel(panelSearchRoutes);
				}
			}
		});
		btnRoutes.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnRoutes.setBounds(369, 17, 145, 29);
		contentPane.add(btnRoutes);
		
		btnAccount = new JButton("ACCOUNT");
		btnAccount.setForeground(new Color(255, 99, 71));
		btnAccount.setBackground(SystemColor.menu);
		btnAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(admin)
					switchPanel(panelAccAdmin);
				else {
					JSONObject jo = new JSONObject();
					try {
	        			jo.put("command", "bookedFlight");
	        			jo.put("nickname", nickname);
		    			response = rpc.call(jo.toString());
		    			
						if(!response.contains("[Error]")) {
							textAreaBooked.setText(response);
						}
					} catch (Exception ex) {
						System.out.println(ex);
					}
					
					jo = new JSONObject();
					try {
	        			jo.put("command", "charge");
	        			jo.put("amount", "0");
	        			jo.put("nickname", nickname);
		    			response = rpc.call(jo.toString());
		    			
		    			if(!response.contains("[Error]")) {
		    				String total = response.substring(response.lastIndexOf(" "));
		    				lblAmount.setText(total);
						}
					} catch (Exception ex) {
						System.out.println(ex);
					}
					switchPanel(panelAccCustomer);
				}
			}
		});
		btnAccount.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnAccount.setBounds(528, 16, 135, 29);
		contentPane.add(btnAccount);
		
		btnAdd = new JButton("ADD");
		btnAdd.setForeground(new Color(255, 99, 71));
		btnAdd.setBackground(SystemColor.menu);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanel(panelAdd);
			}
		});
		btnAdd.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnAdd.setBounds(368, 54, 145, 29);
		contentPane.add(btnAdd);
		
		btnLogout = new JButton("LOGOUT");
		btnLogout.setForeground(new Color(255, 99, 71));
		btnLogout.setBackground(SystemColor.menu);
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JSONObject jo = new JSONObject();
				try {
					jo.put("command", "logout");
	    			jo.put("nickname", nickname); 
	    			response = rpc.call(jo.toString());
					JOptionPane.showMessageDialog(null, response);
					
					if(!response.contains("[Error]")) {
						btnAccount.setVisible(false);
						btnRoutes.setVisible(false);
						btnAdd.setVisible(false);
						btnLogout.setVisible(false);
						nickname = "";
		    			lblNicknameMain.setText(nickname);
		    			lblNicknameMain.setVisible(false);
						switchPanel(panelMain);
					}
					
				} catch (Exception ex) {
					System.out.println(ex);
				}	
			}
		});
		btnLogout.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnLogout.setBounds(528, 54, 135, 29);
		contentPane.add(btnLogout);
		
		JLabel lblLogo = new JLabel("");
		lblLogo.setBounds(0, 0, 150, 95);
		contentPane.add(lblLogo);
		lblLogo.setIcon(new ImageIcon(ClientGUI.class.getResource("/RabbitMQ.png")));
		
		lblNicknameMain = new JLabel("Nickname");
		lblNicknameMain.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		lblNicknameMain.setBounds(104, 54, 182, 30);
		contentPane.add(lblNicknameMain);
		
        lblNicknameMain.setVisible(false);
		btnAccount.setVisible(false);
		btnRoutes.setVisible(false);
		btnAdd.setVisible(false);
		btnLogout.setVisible(false);
		
        try {
        	rpc = new RPCClient();
        } catch (IOException | TimeoutException | JSONException | InterruptedException e) {
        	System.out.println(e);
        }
	}
}
