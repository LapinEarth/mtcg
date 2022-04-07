import at.fhtw.bif3.swe.mtcg.if19b017.BattleLogic;
import at.fhtw.bif3.swe.mtcg.if19b017.cards.*;
import at.fhtw.bif3.swe.mtcg.if19b017.users.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import static org.junit.Assert.assertTrue;



@ExtendWith(MockitoExtension.class)
public class TestBattle
{
    User mockedUser1 = new User("TestUser1", "testPassword", 20,0,0,0,0);
    User mockedUser2 = new User("TestUser2", "testPassword", 20,0,0,0,0);
    ArrayList<Card> deck1 = new ArrayList<>();
    ArrayList<Card> deck2 = new ArrayList<>();
    BattleLogic battleLogic;

    @Test
    @DisplayName("Test if deck goes -1 for user1")
    public  void deckSizeTest1()
    {
        MonsterCard monster1 = new MonsterCard("FireDragon", Element.FIRE, 15, Monster.DRAGON);
        SpellCard spell1 = new SpellCard("WaterSpell", Element.WATER, 65);
        deck1.add(spell1);
        deck2.add(monster1);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        battleLogic.fight();
        assertTrue("Deck sizes should be: Deck1 size = 2 | Deck2 size = 0", deck1.size() == 2 && deck2.size() == 0);
    }

    @Test
    @DisplayName("Test if deck goes -1 for user2")
    public  void deckSizeTest2()
    {
        MonsterCard monster1 = new MonsterCard("FireDragon", Element.FIRE, 15, Monster.DRAGON);
        SpellCard spell1 = new SpellCard("WaterSpell", Element.WATER, 65);
        deck1.add(monster1);
        deck2.add(spell1);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        System.out.println(deck1.size());
        System.out.println(deck2.size());
        battleLogic.fight();
        assertTrue("Deck sizes should be: Deck1 size = 0 | Deck2 size = 2", deck2.size() == 2 && deck1.size() == 0);
    }

    @Test
    @DisplayName("Test if draw possible")
    public  void drawTest()
    {
        MonsterCard monster1 = new MonsterCard("FireDragon", Element.FIRE, 15, Monster.DRAGON);
        MonsterCard monster2 = new MonsterCard("FireDragon", Element.FIRE, 15, Monster.DRAGON);
        deck1.add(monster1);
        deck2.add(monster2);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        System.out.println(deck1.size());
        System.out.println(deck2.size());
        battleLogic.fight();
        assertTrue("Draw should be possible", deck1.size() == 1);
    }

    @Test
    @DisplayName("Test if Water Spell is stronger than Fire Monster")
    public  void FightWaterSpellVsFireMonster()
    {
        MonsterCard monster1 = new MonsterCard("FireDragon", Element.FIRE, 55, Monster.DRAGON);
        SpellCard spell1 = new SpellCard("WaterSpell", Element.WATER, 15);
        deck1.add(spell1);
        deck2.add(monster1);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        System.out.println(deck1.size());
        System.out.println(deck2.size());
        battleLogic.fight();
        assertTrue("Water Spell should be stronger than Fire Monster", deck1.size() == 2 && deck2.size() == 0);
    }

    @Test
    @DisplayName("Test if Normal Monster is stronger than Water Spell")
    public  void FightNormalMonsterVsWaterSpell()
    {
        MonsterCard monster1 = new MonsterCard("NormalGoblin", Element.NORMAL, 55, Monster.GOBLIN);
        SpellCard spell1 = new SpellCard("WaterSpell", Element.WATER, 15);
        deck1.add(monster1);
        deck2.add(spell1);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        System.out.println(deck1.size());
        System.out.println(deck2.size());
        battleLogic.fight();
        assertTrue("Normal Monster should be stronger than Water Spell", deck1.size() == 2 && deck2.size() == 0);
    }

    @Test
    @DisplayName("Test if Fire Monster is stronger than Normal Spell")
    public  void FightFireMonsterVsNormalSpell()
    {
        MonsterCard monster1 = new MonsterCard("FireDragon", Element.FIRE, 15, Monster.DRAGON);
        SpellCard spell1 = new SpellCard("NormalSpell", Element.NORMAL, 55);
        deck1.add(monster1);
        deck2.add(spell1);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        System.out.println(deck1.size());
        System.out.println(deck2.size());
        battleLogic.fight();
        assertTrue("Fire should be stronger than Normal", deck1.size() == 2 && deck2.size() == 0);
    }

    @Test
    @DisplayName("Test if Fire Spell is stronger than Normal Spell")
    public  void FightWaterSpellVsFireSpell()
    {
        SpellCard spell1 = new SpellCard("WaterSpell", Element.WATER, 15);
        SpellCard spell2 = new SpellCard("FireSpell", Element.FIRE, 55);
        deck1.add(spell1);
        deck2.add(spell2);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        System.out.println(deck1.size());
        System.out.println(deck2.size());
        battleLogic.fight();
        assertTrue("Water Spell should be stronger than Fire Spell", deck1.size() == 2 && deck2.size() == 0);
    }

    @Test
    @DisplayName("Test if Fire Spell is stronger than Normal Spell")
    public  void FightFireSpellVsNormalSpell()
    {
        SpellCard spell1 = new SpellCard("FireSpell", Element.FIRE, 15);
        SpellCard spell2 = new SpellCard("NormalSpell", Element.NORMAL, 55);
        deck1.add(spell1);
        deck2.add(spell2);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        System.out.println(deck1.size());
        System.out.println(deck2.size());
        battleLogic.fight();
        assertTrue("Fire Spell should be stronger than Normal Spell", deck1.size() == 2 && deck2.size() == 0);
    }

    @Test
    @DisplayName("Test if Fire Spell is stronger than Normal Spell")
    public  void FightNormalSpellVsWaterSpell()
    {
        SpellCard spell1 = new SpellCard("NormalSpell", Element.NORMAL, 15);
        SpellCard spell2 = new SpellCard("WaterSpell", Element.WATER, 55);
        deck1.add(spell1);
        deck2.add(spell2);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        System.out.println(deck1.size());
        System.out.println(deck2.size());
        battleLogic.fight();
        assertTrue("Fire Spell should be stronger than Normal Spell", deck1.size() == 2 && deck2.size() == 0);
    }
}
