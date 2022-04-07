package at.fhtw.bif3.swe.mtcg.if19b017.cards;

import lombok.*;

@Getter
@Setter

public class SpellCard extends Card
{
    public SpellCard(String name, Element element, double damage)
    {
        super(name, element, damage);
    }
}
