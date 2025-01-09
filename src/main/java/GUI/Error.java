package GUI;

import javax.swing.*;
import java.awt.*;

public class Error {
    Error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    Error(String msg, Component frame) {
        JOptionPane.showMessageDialog(frame, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    Error(Throwable e) {
        if (e.getCause().getCause() == null) {
            e = e.getCause();
        } else if (e.getCause() != null){
            e = e.getCause().getCause();
        }
        JOptionPane.showMessageDialog(null, e.getMessage(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
    }

    Error(Throwable e, Component frame) {
        if (e.getCause().getCause() == null) {
            e = e.getCause();
        } else if (e.getCause() != null){
            e = e.getCause().getCause();
        }
        JOptionPane.showMessageDialog(frame, e.getMessage(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
    }
}
