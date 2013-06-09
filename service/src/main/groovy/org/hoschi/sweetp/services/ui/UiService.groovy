package org.hoschi.sweetp.services.ui

import groovy.json.JsonBuilder
import groovy.swing.SwingBuilder
import groovy.util.logging.Log4j
import org.hoschi.sweetp.services.base.ServiceParameter

import java.awt.event.WindowListener
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
                                summary: 'Shows a simple dialog with a title, a message and a password input.'
                        ],
                        returns: 'Password the user typed into the dialog input field.'
                ]]
        ])
        config = json.toString()
    }



    String dialogPassword(Map params) {
        assert params.title
        assert params.message

        def swing = new SwingBuilder()
        def lock = new Object()
        String passwordText = '';

        // create frame and show it
        JFrame frame = swing.frame(title: params.title, defaultCloseOperation: JFrame.HIDE_ON_CLOSE, pack: true, show: false) {
            vbox {
                textlabel = label(params.message)
                passwd = passwordField()
                button(
                        text: 'OK',
                        actionPerformed: {
                            synchronized (lock) {
                                // save password to outside thread
                                passwordText = passwd.password.toString()
                                // notify main thread, we are finished here
                                lock.notifyAll();
                            }
                        }
                )
            }
        }

        // add closing listener
        frame.addWindowListener([
                windowActivated: {},
                windowClosing: {
                    synchronized (lock) {
                        // reset text, closing is like abort
                        passwordText = null;
                        // release lock, main thread executes now
                        lock.notifyAll();
                    }
                },
                windowDeactivated: {},
                windowDeiconified: {},
                windowIconified: {},
                windowOpened: {},
                windowClosed: {

                }
        ] as WindowListener)

        // show window after setup
        frame.show()

        // wait for UI to finish
        synchronized (lock) {
            while (frame.isVisible()) {
                lock.wait();
            }
        }

        // close window
        frame.dispose()

        passwordText
    }

    // just for testing without sweetp server
    static void main(String[] args) {
        UiService ui = new UiService()
        ui.dialogPassword([title: 'foo', message: 'message']);
    }
}
