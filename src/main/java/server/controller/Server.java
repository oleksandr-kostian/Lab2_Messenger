package server.controller;

import org.xml.sax.SAXException;
import java.io.IOException;

/**
 * Interface for server's controller.
 *
 * @author Veleri Rechembei
 * @version %I%, %G%
 */
public interface Server {
    /**
     * Method, that start GUI of server.
     * @return String value <code>start</code> if server is running,
     *         String value <code>stop</code>  if server is stopped.
     */
    String startGUI();
    /**
     * Method for display information on servers GUI or console, if GUI is null and on info message of lOG.
     * @param message is String information of server.
     */
    void displayInfoLog(String message);
    /**
     * Method for write exception on servers GUI to LOG error message.
     * @param message is exception of server.
     */
    void catchGuiException(Exception message);
    /**
     * Method for start work of server.
     * @throws IOException if port don't build; wrong of client's socket.
     * @throws SAXException if ServerThread has mistake of xml.
     */
    void run()throws IOException, SAXException;
    /**
     * Method for stop server's controller.
     * @throws IOException if steams has error.
     */
    void stop()throws IOException;
    /**
     * Method for start gracefulReload.
     */
    void gracefulReload();


}
