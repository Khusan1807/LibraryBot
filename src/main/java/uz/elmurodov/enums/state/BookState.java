package uz.elmurodov.enums.state;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Narzullayev Husan, вс 0:08. 02.01.2022
 */

@Getter
@AllArgsConstructor
public enum   BookState {
        ID("id"),
        DESCRIPTION("description"),
        CATEGORY("category"),
        AUTHOR("author"),
        ACCEPTED("accepted");

        private final String name;

    public static BookState findBookStateByText(String text){
        for (BookState value : BookState.values()) {
            if(value.getName().equalsIgnoreCase(text)){
                return value;
            }
        }
        return null;
    }

}

