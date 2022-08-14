package ru.infotecs.studentsService.util.ftp;

import ru.infotecs.studentsService.util.ftp.exception.LoadException;

import java.util.Set;

public interface FtpConnect<T> {
    Set<T> download() throws LoadException;
    void upload(Set<T> list) throws LoadException;
}
