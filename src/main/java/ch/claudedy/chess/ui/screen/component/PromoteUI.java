package ch.claudedy.chess.ui.screen.component;

import ch.claudedy.chess.model.enumeration.PieceType;
import ch.claudedy.chess.ui.listener.PromoteDoneListener;
import lombok.experimental.Accessors;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@Accessors(fluent = true)
public class PromoteUI extends JDialog {

    private final List<PromoteDoneListener> listeners = new ArrayList<>();

    public PromoteUI() {
        setLayout(new FlowLayout());
        setBounds(new Rectangle(500, 300, 400, 300));

        JLabel promoteText = new JLabel("Select a promote piece");
        add(promoteText);

        JList<String> selection = new JList<>(new String[]{PieceType.QUEEN.name(), PieceType.ROOK.name(), PieceType.BISHOP.name(), PieceType.KNIGHT.name()});
        selection.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        selection.setSelectedIndex(0);
        selection.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                listeners.forEach(listener -> listener.onPromoteDoneListener(PieceType.valueOf(selection.getSelectedValue())));
            }
        });
        add(selection);
    }

    public void addPromoteDoneListener(PromoteDoneListener listener) {
        listeners.add(listener);
    }
}
