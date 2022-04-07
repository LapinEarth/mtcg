package at.fhtw.bif3.swe.mtcg.if19b017.users;

import lombok.*;

@Getter
@Setter
@ToString

public class User
{
    private String username;
    private String password;
    private int coins;
    private int eloPoints;
    private int gamesPlayed;
    private int wins;
    private int losses;

    public User(String username, String password, int coins, int eloPoints, int gamesPlayed, int wins, int losses)
    {
        this.username = username;
        this.password = password;
        this.coins = coins;
        this.eloPoints = eloPoints;
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.losses = losses;
    }
}
