package ichttt.gradle.forgelogin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.CountDownLatch;

public final class LoginGUI implements ActionListener, KeyListener {
    private final CountDownLatch latch = new CountDownLatch(1);
    private final JFrame frame = new JFrame("Minecraft Login");
    private final JPanel login = new JPanel(new GridLayout(2, 1));
    private String password;
    private JPasswordField field = new JPasswordField();

    public LoginGUI() {
        frame.setContentPane(login);
        field.addKeyListener(this);
        login.add(field);
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(this);
        login.add(okBtn);
        frame.setMinimumSize(new Dimension(320, 200));
        centerComponent(frame);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    private static void centerComponent(Window comp) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        comp.setLocation((screenSize.width - comp.getWidth()) / 2, (screenSize.height - comp.getHeight()) / 2);
    }

    public String getPasswordAndDiscard() {
        String s = password;
        frame.dispose();
        login.removeAll();
        return s;
    }

    private void checkPW() {
        password = new String(field.getPassword());
        if (!password.equals(""))
            latch.countDown();
        else
            if (JOptionPane.showConfirmDialog(frame, "No password specified, do you want to start offline?", "Question", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                latch.countDown();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        checkPW();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            checkPW();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
