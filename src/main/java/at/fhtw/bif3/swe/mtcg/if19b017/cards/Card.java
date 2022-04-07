package at.fhtw.bif3.swe.mtcg.if19b017.cards;

import lombok.*;

@Getter
@Setter
@ToString

public class Card
{
    private String name;
    private Element element;
    private final double damage;

    public Card(String name, Element element, final double damage)
    {
        this.name = name;
        this.element = element;
        this.damage = damage;
    }
}
