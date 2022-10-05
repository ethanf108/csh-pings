package edu.rit.csh.pings.exchange;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Paged<T> {

    private List<? extends T> elements;
    private long totalElements;
}
