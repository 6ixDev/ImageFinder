package de.techgamez.pleezon;

public class PictureNotFoundOnScreenException extends RuntimeException{
    /**
     * @Author Pleezon
     */
    PictureNotFoundOnScreenException(String errorMessage) {
        super(errorMessage);
    }
}
