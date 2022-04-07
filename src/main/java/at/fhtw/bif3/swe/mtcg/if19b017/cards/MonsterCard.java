package at.fhtw.bif3.swe.mtcg.if19b017.cards;

import lombok.*;

@Getter
@Setter

public class MonsterCard extends Card
{
    private Monster monster;

    public MonsterCard(String name, Element element, double damage, Monster monster)
    {
        super(name, element, damage);
        this.monster = monster;
    }
}
