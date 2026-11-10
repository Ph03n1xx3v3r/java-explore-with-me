package ru.practicum.ewm.main.exception;
    public class ApiException extends RuntimeException {

    private final org.springframework.http.HttpStatus status;

    public ApiException(org.springframework.http.HttpStatus status, String message) {
super(message);this.status = status;
}


    public org.springframework.http.HttpStatus getStatus() {
return status;
}

}
