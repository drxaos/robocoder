package robo2d.game.programs;

public class ProgramLoadException extends Exception {
    public ProgramLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProgramLoadException(Throwable cause) {
        super(cause);
    }
}
