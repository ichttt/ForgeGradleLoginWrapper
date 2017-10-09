package ichttt.gradle.forgelogin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.CountDownLatch;

public final class GradleLoginGUI implements ActionListener, KeyListener {
    private static final CountDownLatch latch = new CountDownLatch(1);
    private static String password;

    private static final JFrame frame = new JFrame("Minecraft Login");
    private static final JPanel panel = new JPanel(new GridLayout(2, 1));
    private static JPasswordField field = new JPasswordField();

    public static CountDownLatch create() {
        frame.setContentPane(panel);
        GradleLoginGUI loginGUI = new GradleLoginGUI();
        field.addKeyListener(loginGUI);
        panel.add(field);
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(loginGUI);
        panel.add(okBtn);
        frame.setMinimumSize(new Dimension(320, 200));
        centerComponent(frame);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return latch;
    }

    private static void centerComponent(Window comp) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        comp.setLocation((screenSize.width - comp.getWidth())/ 2 , (screenSize.height - comp.getHeight())/2);
    }

    public static String getPasswordAndDiscard() {
        String s = password;
        password = null;
        frame.dispose();
        panel.removeAll();
        field = null;
        return s;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        checkPW();
    }

    private static void checkPW() {
        password = new String(field.getPassword());
        if (!password.equals(""))
            latch.countDown();
        else
            JOptionPane.showMessageDialog(frame, "No password specified!");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            checkPW();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
