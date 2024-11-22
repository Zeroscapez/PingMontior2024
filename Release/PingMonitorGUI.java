import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PingMonitorGUI extends JFrame {
    private JTextArea statusTextArea;
    private JTextField ipAddressTextField;
    private JButton controlButton;
    private boolean isMonitoring = false;
    private Thread pingThread;

    public PingMonitorGUI() {
        setTitle("Ping Monitor");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.setLayout(new BorderLayout());

        statusTextArea = new JTextArea(); // Text Area
        statusTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        ipAddressTextField = new JTextField("Enter IP Address");
        panel.add(ipAddressTextField, BorderLayout.NORTH);

        controlButton = new JButton("Start");
        controlButton.setBackground(Color.GREEN);
        panel.add(controlButton, BorderLayout.SOUTH);

        controlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePingMonitoring();
            }
        });
    }

    private void togglePingMonitoring() { // Information for changing Start/Stop Button
        if (!isMonitoring) {
            startPingMonitoring();
            controlButton.setText("Stop");
            controlButton.setBackground(Color.RED);
            ipAddressTextField.setEnabled(false); // Disable the text field when monitoring is started
        } else {
            stopPingMonitoring();
            controlButton.setText("Start");
            controlButton.setBackground(Color.GREEN);
            ipAddressTextField.setEnabled(true); // Enable the text field when monitoring is stopped
        }
    }

    private void startPingMonitoring() {
        String ipAddress = ipAddressTextField.getText();
        int timeout = 5000;

        isMonitoring = true;
        pingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isMonitoring) {
                    try {
                        InetAddress inetAddress = InetAddress.getByName(ipAddress);

                        if (inetAddress.isReachable(timeout)) {
                            long pingTime = System.currentTimeMillis();
                            String formattedPingTime = String.format("%02d", pingTime % 100); // Format Info for Ping
                                                                                              // text
                            String pingResult = "Ping successful! Ping time: " + formattedPingTime + "ms\n";
                            appendToStatusTextArea(pingResult);
                        } else {
                            String pingResult = "Ping failed!\n";
                            appendToStatusTextArea(pingResult);
                        }
                    } catch (UnknownHostException e) {
                        String pingResult = "Invalid IP address: " + ipAddress + "\n";
                        appendToStatusTextArea(pingResult);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(2000); // Ping every 2 Seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        pingThread.start();
    }

    private void stopPingMonitoring() {
        isMonitoring = false;
        if (pingThread != null) {
            pingThread.interrupt();
        }
    }

    private void appendToStatusTextArea(String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                statusTextArea.append(text);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PingMonitorGUI().setVisible(true);
            }
        });
    }
}
