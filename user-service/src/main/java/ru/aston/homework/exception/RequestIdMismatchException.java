package ru.aston.homework.exception;

public class RequestIdMismatchException extends RuntimeException {

    public RequestIdMismatchException(Long pathId, Long bodyId) {
        super("ID в URL (" + pathId + ") не совпадает с ID в запросе (" + bodyId + ")");
    }
}