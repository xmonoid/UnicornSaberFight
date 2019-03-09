package vrn.edinorog.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class DeleteObjectDto {

    private Long objectId;
    private Boolean cascadeDelete;

}