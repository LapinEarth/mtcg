package at.fhtw.bif3.swe.mtcg.if19b017.database.structure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor

public class UserScore
{
    private String Name;
    private int elopoints;
}
