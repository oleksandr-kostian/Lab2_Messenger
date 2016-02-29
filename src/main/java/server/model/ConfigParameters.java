package server.model;

/**
 * Class that describes parameters of configuration file.
 */
class  ConfigParameters {
    private boolean isGUI;
    private int     port;
    private boolean isLog;
    private String  levelLog;

    /**
     * Method that return true, if log include.
     * @return <code>true</code> if log include,
     *         <code>false</code> if log do not include.
     */
    public boolean isLog() {
        return isLog;
    }

    /**
     * Method for set ststus for log
     * @param log <code>true</code> if log include,
     *            <code>false</code> if log do not include.
     */
    public void setLog(boolean log) {
        isLog = log;
    }

    /**
     * Method that return level of log.
     * @return level of log.
     */
    public String getLevelLog() {
        return levelLog;
    }

    /**
     * Method that set level of log.
     * @param levelLog log level for set.
     */
    public void setLevelLog(String levelLog) {
        this.levelLog = levelLog;
    }

    /**
     * Method that return port, which program listen.
     * @return umber of port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Method that set port, which program listen.
     * @param port number of port.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Method that return type of interface.
     * @return <code>true</code> if it is GUI,
     *         <code>false</code> if it is console.
     */
    public boolean isGUI() {
        return isGUI;
    }

    /**
     * Method for set type of interface.
     * @param GUI <code>true</code> if it is GUI,
     *            <code>false</code> if it is console.
     */
    public void setGUI(boolean GUI) {
        isGUI = GUI;
    }


}
