package ru.infotecs.studentsService.util.reader;

import ru.infotecs.studentsService.Main;
import ru.infotecs.studentsService.util.reader.exception.IncompleteOperationException;

public final class ReaderFactory {
    public static final Reader<String> STRING_READER = new StringReader();
    public static final Reader<Long> LONG_READER = new LongReader();
    public static final Reader<String> READER = new StringReader() {
        @Override
        public String read(String consoleOut) throws IncompleteOperationException {
            if (consoleOut.length() > 0) Main.WRITER.writeMessage(consoleOut);
            return lastInput;
        }
    };
}
