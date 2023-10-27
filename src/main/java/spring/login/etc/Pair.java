package spring.login.etc;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair <S, T>{
    private S first;
    private T second;
}
