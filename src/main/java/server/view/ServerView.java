package server.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import server.controller.ControllerServer;
import server.controller.Server;
import server.model.ModelActions;

import javax.swing.*;
    /**
     * GUI of server's controller
     *
     * @author Veleri Rechembei
     * @version %I%, %G%
     */
public class ServerView extends JFrame implements View{
    private JPanel                  viewPanel;
    private Font                    font;
    private static JTextArea        memo;
    private JButton                 startButton;
    private Server                  server;
    private JButton                 gracefulReload;
    private boolean                 clickStart;
    /**
     * Constructor of server GUI.
     */
    public ServerView() {
        super("Server");
        this.createGUI();
        clickStart=false;
    }

     /**
     * Method for set server.
     * @param server is server.
    */
    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    /**
     * Method for get server.
     * @return server.
     */
    @Override
     public Server getServer() {
        return server;
     }

     /**
     * Method for get boolean status of server.
     * @return <code>true</code> if server is running.
      *         <code>false</code> if server is stopped.
      */
     @Override
     public boolean isServerStart() {
       return clickStart;
      }

     /**
     * Method for set status of server.
     @param isStart is status for server.
                    <code>true</code> if server is running.
                    <code>false</code> if server is stopped.
     */
     @Override
     public void setServerStart(boolean isStart) {
        this.clickStart = isStart;
     }

    /**
     * Method, that create GUI of server.
     */
    public void createGUI() {
        final JFrame frame = new JFrame();
        frame.setTitle("Server");
        frame.setResizable(false);
        font = new Font("Verdana", Font.PLAIN, 14);
        viewPanel = new JPanel();

        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new BorderLayout());
        memo = new JTextArea(19, 37);
        memo.setLineWrap(true);


        generalPanel.add(memo, BorderLayout.PAGE_START);
        generalPanel.add(createButtonPanel(), BorderLayout.PAGE_END);
        JScrollPane jsp1 = new JScrollPane(memo);
        generalPanel.add(jsp1);

        viewPanel.add(generalPanel);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                Object[] options = {"Yes", "No"};
                int n = JOptionPane
                        .showOptionDialog(event.getWindow(), "Are you sure  want stopping the server?",
                                "Confirmation", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, options,
                                options[0]);
                if (n == 0) {
                    try {
                        server.stop();
                        System.exit(2);
                    }
                    catch (IOException  e){
                        server.catchGuiException(e);
                    }
                    catch (NullPointerException  e){
                        System.exit(2);
                    }
                }
            }
        });

        frame.add(viewPanel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(new Dimension(450, 395));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    /**
     * Method, that create button panel for server GUI.
     * @return button panel for server GUI.
     */
    private JPanel createButtonPanel(){

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 1));

        startButton = new JButton("Start");
        startButton.setFont(font);
        buttonPanel.add(startButton);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = server.startGUI();
                startButton.setText(text);
            }
        });

        gracefulReload = new JButton("Graceful reload");
        gracefulReload.setFont(font);
        buttonPanel.add(gracefulReload);
        gracefulReload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    gracefulReload();
                    display("Graceful Reload is successful.");
            }
        });
        return buttonPanel;
    }

    private void gracefulReload(){
        this.server.gracefulReload();
    }

    /**
     * Method for display information on GUI.
     * @param display is String message.
     */
    @Override
     public  void display(String display){
        String text = memo.getText();
        text += "\n" +display;
        memo.setText(text);
    }

    }
