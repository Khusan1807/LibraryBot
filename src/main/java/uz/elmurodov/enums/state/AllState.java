package uz.elmurodov.enums.state;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Narzullayev Husan, сб 14:39. 25.12.2021
 */

@Getter
@AllArgsConstructor
public enum AllState {
    FULL_NAME("full_name"),
    AGE("age"),
    GENDER("gender"),
    PHONE_NUMBER("phone_number"),
    AUTHORIZED("authorized"),
    SEARCH("search"),
    PUT("put"),
    EXIT("exit");

    private final String name;


    public static AllState findByText(String text){
        for (AllState value : AllState.values()) {
            if(value.getName().equalsIgnoreCase(text)){
                return value;
            }
        }
    return null;
    }

}
