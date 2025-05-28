package de.lunx.setup;

import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;

import de.lunx.data.Configuration;
import java.util.List;

public class Setup {
    private static boolean setup = false;

    public static void setup(Terminal terminal, Configuration configuration)
    {
        Completer completer = new StringsCompleter(List.of("3305"));
        LineReader reader = LineReaderBuilder.builder().terminal(terminal).completer(completer).build();
        System.out.println("\uD83D\uDD25 > Welcome to the TobbQL Database Setup!");
        String port = reader.readLine("\uD83D\uDC40 > Please choose a port for the database: ");
        String host = reader.readLine("\uD83D\uDC40 > Please choose a host address for the database: ");
        System.out.println("\uD83D\uDC40 > Setup completed!");
        configuration.setPort(Integer.parseInt(port));
        configuration.setHostAddress(host);
        setup = true;
    }

    public static boolean setupCompleted(){
        return setup;
    }


}
