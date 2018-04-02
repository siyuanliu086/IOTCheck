package com.iss.iotcheck;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.iss.iotcheck.plugin.IProcessing;


public class MainWindow {
    private JFrame frame;
    private JTextArea infoTextArea;
    private JRadioButton waterRadioButton, airRadioButton, gasButton;
    private JLabel checkResult;
    private List<JRadioButton> radioButtonList;
    private JScrollPane resultPanel;
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    MainWindow window = new MainWindow();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MainWindow() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        radioButtonList = new ArrayList<>();
        
        frame = new JFrame("软通IOT数据校验工具");
        frame.setBounds(100, 100, 750, 492);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/images/iot_icon.png"));
        frame.setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(220, 220, 220));
        mainPanel.setBounds(0, 0, 744, 464);
        frame.getContentPane().add(mainPanel);
        mainPanel.setLayout(null);
        
        Color transparent = new Color(00, 0, 0, 0);
        // =======水、气选择按钮=======
        waterRadioButton = new JRadioButton("地表水");
        waterRadioButton.setBackground(transparent);
        waterRadioButton.setBounds(247, 5, 121, 23);
        mainPanel.add(waterRadioButton);
        radioButtonList.add(waterRadioButton);
        waterRadioButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                // 手动互斥
                for(JRadioButton radio : radioButtonList) {
                    if(!"地表水".equals(radio.getText())) {                        
                        radio.setSelected(false);
                    }
                }
            }
        });
        
        airRadioButton = new JRadioButton("大气");
        airRadioButton.setBackground(transparent);
        airRadioButton.setBounds(117, 5, 121, 23);
        mainPanel.add(airRadioButton);
        airRadioButton.setSelected(true);
        radioButtonList.add(airRadioButton);
        airRadioButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                // 手动互斥
                if(airRadioButton.isSelected()) {
                    waterRadioButton.setSelected(false);
                }
            }
        });
        
        gasButton = new JRadioButton("废气");
        gasButton.setBackground(new Color(0, 0, 0, 0));
        gasButton.setBounds(378, 5, 121, 23);
        mainPanel.add(gasButton);
        radioButtonList.add(gasButton);
        gasButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                // 手动互斥
                if(airRadioButton.isSelected()) {
                    waterRadioButton.setSelected(false);
                }
            }
        });
        
        JLabel label = new JLabel("校验数据包：");
        label.setBackground(Color.DARK_GRAY);
        label.setBounds(10, 34, 97, 15);
        mainPanel.add(label);
        
        JButton button = new JButton("校验");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("-----校验");
                String result = "0";
                int type = 0;
                String mess = infoTextArea.getText().toString().trim();
                if(mess == null || mess.length() == 0) {
                    result = "-1";
                    setCheckResult(result);
                    return;
                } 
                
                if(waterRadioButton.isSelected()) {
                    type = Integer.valueOf(IProcessing.SURFACE_WATER_MONITOR_212);
                } else if(airRadioButton.isSelected()) {
                    type = Integer.valueOf(IProcessing.AIR_MONITOR_MONITOR_212);
                } else {
                    result = "-2";
                    setCheckResult(result);
                    return;
                }
                result = IOTCheck.checkMessage(mess, type);
                setCheckResult(result);
            }
        });
        button.setBounds(613, 166, 93, 23);
        mainPanel.add(button);
        
        infoTextArea = new JTextArea();
        infoTextArea.setBackground(Color.WHITE);
        infoTextArea.setBounds(117, 34, 589, 122);
        infoTextArea.setLineWrap(true);
        infoTextArea.setText("##0223ST=22;CN=2011;PW=123456;MN=C037800AM2011;CP=&&DataTime=20180328235000;SO2-Rtd=19.0,SO2-Flag=N;NO2-Rtd=25.0,NO2-Flag=N;CO-Rtd=0.687,CO-Flag=N;O3-Rtd=76.0,O3-Flag=N;PM25-Rtd=75.0,PM25-Flag=N;PM10-Rtd=374.0,PM10-Flag=N&&4380");
        mainPanel.add(infoTextArea);
        
        JLabel tipLabel = new JLabel("校验结果：");
        tipLabel.setBackground(Color.DARK_GRAY);
        tipLabel.setBounds(10, 202, 97, 15);
        mainPanel.add(tipLabel);
        
        checkResult = new JLabel("结果");
        checkResult.setBounds(117, 202, 588, 252);
        
        resultPanel = new JScrollPane(checkResult);
        resultPanel.setBounds(115, 200, 590, 254);

        //resultPanel.add(new JScrollPane(checkResult));
//        JScrollPane jscrolJanel = new JScrollPane(
//                checkResult, JScrollPane., JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        resultPanel.add(jscrolJanel);
        mainPanel.add(resultPanel);
    }
    
    private void setCheckResult(String result) {
        switch (result) {
        case "-2":
            checkResult.setText("请选择设备类型！");
            break;
        case "-1":
            checkResult.setText("请输入检测数据包！");
            break;
        case IOTCheck.RE_CODE_OK://0
            checkResult.setText("校验成功！");
            break;
        case IOTCheck.RE_CRC_ERR://1
            checkResult.setText("数据错误！（CRC校验算法错误）");
            break;
        case IOTCheck.RE_EMPTY_CHAR_ERR://2
            checkResult.setText("数据错误！（不允许包含空格）");
            break;
        case IOTCheck.RE_212_OTHER_ERR://2
            checkResult.setText("数据错误！");
            break;
        case IOTCheck.RE_MN_ERR://3
            checkResult.setText("设备号错误！（字母加数字构成或者数字长度超过10位）");
            break;
        case IOTCheck.RE_HEADER_ERR://4
            checkResult.setText("协议头部描述错误！(包头：固定为 ##+数据段长度+ST系统类型)");
            break;
        default:
            checkResult.setText(result);
        }
        
    }
}
