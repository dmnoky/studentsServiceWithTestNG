package ru.infotecs.studentsService.util.ftp;

import ru.infotecs.studentsService.util.ftp.exception.LoadException;
import ru.infotecs.studentsService.util.json.mapper.ObjectReader;
import ru.infotecs.studentsService.util.json.mapper.ObjectWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static ru.infotecs.studentsService.Main.WRITER;

public class FtpConnectJdk<T> implements FtpConnect<T> {
    /** Предположительный формат: ftp://user:password@localhost:8080/students.json */
    private static final String FTP_URL_FORMAT = "ftp://%s:%s@%s/%s";
    private final ObjectReader<T> studentsReader;
    private final ObjectWriter<T> studentsWriter;
    private final URL url;
    private URLConnection urlConnection;

    /**	Клиент должен уметь работать с FTP-сервером в двух режимах: активном и пассивном */
    public FtpConnectJdk(String login, String password, String address, String fileJsonName, Class<T> clazz) throws IOException {
        url = new URL(String.format(FTP_URL_FORMAT, login, password, address, fileJsonName));
        url.openConnection().connect(); // проверка соеденения
        studentsReader = new ObjectReader<>(clazz);
        studentsWriter = new ObjectWriter<>(clazz);
    }

    private String jsonDownload() throws IOException {
        urlConnection = url.openConnection();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            return contentBuilder.toString();
        }
    }

    @Override
    public Set<T> download() throws LoadException {
        try {
            return studentsReader.read(jsonDownload());
        } catch (IOException e) {
            WRITER.writeErrMessage("Ошибка загрузки списка", e);
            throw new LoadException(e);
        }
    }

    @Override
    public void upload(Set<T> set) throws LoadException {
        try {
            byte[] bytes = studentsWriter.mapping(set).toString().getBytes(StandardCharsets.UTF_8);
            urlConnection = url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json");
            try (OutputStream outputStream = urlConnection.getOutputStream()) {
                outputStream.write(bytes);
                outputStream.flush();
            }
        } catch (IOException e) {
            WRITER.writeErrMessage("Ошибка записи списка", e);
            throw new LoadException(e);
        }
    }

}
