package ru.infotecs.studentsService.util.reader;

import ru.infotecs.studentsService.util.reader.exception.IncompleteOperationException;

public class StringReader extends ReaderAbs<String> {
    StringReader() {}

    @Override
    public String read(String consoleOut) throws IncompleteOperationException {
        if (ReaderAbs.inputIsNotSystem(consoleOut)) return lastInput;
        throw new IncompleteOperationException();
    }
}
