package at.fhtw.bif3.swe.mtcg.if19b017.database.structure;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor

public class CardData implements Serializable
{
    private String id;
    private String name;
    private String element;
    private String type;
    private double DAMAGE;
    private String userName;
    private Boolean isDeck;
}
