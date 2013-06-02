package org.hoschi.sweetp.services.ui

import groovy.json.JsonBuilder
import groovy.swing.SwingBuilder
import groovy.util.logging.Log4j
import org.hoschi.sweetp.services.base.ServiceParameter

import javax.swing.JFrame

/**
 * @author Stefan Gojan
 */
@Log4j
class UiService {
    String config

    /**
     * Build config and dependencies.
     */
    UiService() {
        JsonBuilder json = new JsonBuilder([
                ['/ui/dialog/password': [
                        method: 'dialogPassword',
                        params: [
                                title: ServiceParameter.ONE,
                                message: ServiceParameter.ONE,
                        ],
                        description: [
                                summary: "Shows a simple dialog with a title, a message and a password input."
                        ],
                        returns: "Password the user typed into the dialog input field."
                ]]
        ])
        config = json.toString()
    }



    public String dialogPassword(Map params) {
        assert params.title
        assert params.message

        def swing = new SwingBuilder()
        def lock = new Object()
        String passwordText = "";

        // create frame and show it
        def frame = swing.frame(title: params.title, defaultCloseOperation: JFrame.EXIT_ON_CLOSE, pack: true, show: true) {
            vbox {
                textlabel = label(params.message)
                passwd = passwordField()
                button(
                        text: 'OK',
                        actionPerformed: {
                            synchronized (lock) {
                                // save password to outside thread
                                passwordText = passwd.password.toString()
                                // close window
                                dispose()
                                lock.notify();
                            }
                        }
                )
            }
        }

        // bind swing frame execution to this thread so it waits till the frame is invisible
        Thread t = new Thread() {
            public void run() {
                synchronized (lock) {
                    while (frame.isVisible())
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }
            }
        };
        t.start();

        // bind the waiting threat to the current
        t.join()

        println "parent"
        return passwordText
    }

    // just for testing without sweetp server
    public static void main(String[] args) {
        def ui = new UiService()
        ui.dialogPassword([title: "foo", message: "message"]);
    }
}
