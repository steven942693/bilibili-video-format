package com.steven;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Hello world!
 */
public class App extends JFrame {
    static File input_directory = null;
    static File output_directory = null;
    static JProgressBar peer_progressBar;
    static JProgressBar progressBar;
    static JButton button;
    static JLabel current_File;
    static boolean flag ;


    static {
        // 加载UI
        try {
//            去除顶部设置按钮
            UIManager.put("RootPane.setupButtonVisible", false);
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
            // jframe对象需要放在加载UI主题包之后,初始化,否则可能会造成部分效果设置不生效的问题
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new App(), "UI主题包设置异常!!!");
        }
    }

    public App() {
        setTitle("哔哩哔哩视频整理工具");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        //        JFrame设置图标
        ImageIcon icon = new ImageIcon(App.class.getResource("/imgs/bililogo.png"));
        setIconImage(icon.getImage());

//        JFrame加载背景图片
        ImageIcon bg = new ImageIcon(App.class.getResource("/imgs/bg.jpg"));
        JLabel bg_label = new JLabel(bg);
        bg_label.setBounds(0, 0, 400,600);
//        把背景图片放入JFrame上
        getLayeredPane().add(bg_label, new Integer(Integer.MIN_VALUE));


//        ----------------
        JPanel panel = new JPanel(null);
//        panel.setBackground(new Color(98, 244, 244));
        panel.setOpaque(false);

        JLabel input_label = new JLabel("视频目录 : ");
        input_label.setBounds(10, 10, 120, 40);
        setMyCSS(input_label, 18, new Color(6, 138, 225));

        JTextField jfc_input = new JTextField("--选择视频目录--");
        jfc_input.setBounds(120, 10, 180, 40);
        setMyCSS(jfc_input, 18, new Color(6, 138, 225));



        /*directory = jfc.getSelectedFile();
        if (out_location != null && directory!= null){
            out_location.setText(directory.toString());
        }*/

        JLabel output_label = new JLabel("输出目录 : ");
        output_label.setBounds(10, 90, 120, 40);
        setMyCSS(output_label, 18, new Color(6, 138, 225));

        JTextField jfc_output = new JTextField("--选择输出目录--");
        jfc_output.setBounds(120, 90, 180, 40);
        setMyCSS(jfc_output, 18, new Color(6, 138, 225));

        // 进度条
        progressBar = new JProgressBar();
        progressBar.setBounds(25, 170, 300, 40);
        setMyCSS(progressBar, 18, new Color(24, 245, 7));
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        progressBar.setString("总进度 : 0%");
//        progressBar.setIndeterminate(true);

        // 单独进度
        peer_progressBar = new JProgressBar();
        peer_progressBar.setBounds(25, 250, 300, 40);
        setMyCSS(peer_progressBar, 18, new Color(219, 76, 10));
        peer_progressBar.setStringPainted(true);
        peer_progressBar.setValue(0);
        peer_progressBar.setString("当前视频进度 : 0%");

        current_File = new JLabel("-----------------------",JLabel.CENTER);
        current_File.setBounds(25, 330, 300, 40);
        setMyCSS(current_File, 18, new Color(247, 7, 151));


        // 开始按钮
        button = new JButton("< 开 始 >");
        button.setBounds(25, 450, 300, 40);
        setMyCSS(button, 25, new Color(72, 240, 6));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    @Override
                    public void run() {
                        if ("< 开 始 >".equals(button.getText())) {
                            button.setText("< 取 消 >");
                            button.setForeground(Color.RED);
                            flag = true;
                            Main main = new Main();
                            try {
                                main.start(input_directory,output_directory);
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }else {
                            button.setText("< 开 始 >");
                            button.setForeground(Color.GREEN);
                            flag = false;
                            JOptionPane.showMessageDialog(button,"已取消操作!!!");
                            repaint();
                            repaint();
                            repaint();
                            progressBar.setValue(0);
                            progressBar.setString("总进度 : 0%");
                            peer_progressBar.setValue(0);
                            peer_progressBar.setString("当前视频进度 : 0%");
                            current_File.setText("-----------------------");
                        }

                    }
                }.start();

            }
        });

        jfc_input.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Thread(){
                    @Override
                    public void run() {
                        JFileChooser jfc = new JFileChooser();
                        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        jfc.showDialog(input_label, "选择视频目录");
                        input_directory = jfc.getSelectedFile();
                        if (jfc_input != null && input_directory!= null){
                            jfc_input.setText(input_directory.toString());
                        }
                    }
                }.start();

            }
        });

        jfc_output.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Thread(){
                    @Override
                    public void run() {
                        JFileChooser jfc = new JFileChooser();
                        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        jfc.showDialog(input_label, "选择输出目录");
                        output_directory = jfc.getSelectedFile();
                        if (jfc_output != null && output_directory!= null){
                            jfc_output.setText(output_directory.toString());
                        }
                    }
                }.start();

            }
        });

        panel.add(input_label);
        panel.add(jfc_input);
        panel.add(output_label);
        panel.add(jfc_output);
        panel.add(progressBar);
        panel.add(peer_progressBar);
        panel.add(current_File);
        panel.add(button);

        add(panel);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public int toInt(Double d) {
        return (int) d.doubleValue();
    }

    public void setMyCSS(Component component, int fontSize, Color color) {
        component.setFont(new Font("楷体", Font.BOLD, fontSize));
        component.setForeground(color);
    }

    public static void main(String[] args) {
        new App();
    }
}
