package ru.infotecs.studentsService.util.reader;

import ru.infotecs.studentsService.Main;
import ru.infotecs.studentsService.util.reader.exception.IncompleteOperationException;

public class LongReader extends ReaderAbs<Long> {
    LongReader() {}

    @Override
    public Long read(String consoleOut) throws IncompleteOperationException {
        while (ReaderAbs.inputIsNotSystem(consoleOut)) {
            if (lastInput.matches("^\\d+$")) return Long.parseLong(lastInput);
            Main.WRITER.writeMessage("Введите число...");
        }
        throw new IncompleteOperationException();
    }
}
