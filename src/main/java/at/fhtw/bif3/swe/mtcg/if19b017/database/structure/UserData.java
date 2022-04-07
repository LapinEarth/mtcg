package at.fhtw.bif3.swe.mtcg.if19b017.database.structure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@AllArgsConstructor
@Data
@NoArgsConstructor

public class UserData implements Serializable
{
    private String userName;
    private String password;
    private Integer coins;
    private Boolean logInStatus;
    private Integer eloPoints;
    private Integer gamesPlayed;
    private Integer wins;
    private Integer losses;
}
