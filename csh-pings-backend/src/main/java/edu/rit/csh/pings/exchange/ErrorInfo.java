package edu.rit.csh.pings.exchange;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ErrorInfo {

    private String message;
    private Throwable stackTrace;

    public ErrorInfo(String message) {
        this(message, null);
    }
}
