package uz.elmurodov.entity;


import lombok.*;
import uz.elmurodov.enums.state.BookState;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ReceivedBook extends Book {
    private String fileId;
    private String ownerId;
    private BookState state;


}
