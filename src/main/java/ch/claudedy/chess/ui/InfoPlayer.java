package ch.claudedy.chess.ui;

import ch.claudedy.chess.basis.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Accessors(fluent = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InfoPlayer implements Serializable {
    private String name;
    private Color color;
    private boolean isComputer;
}
