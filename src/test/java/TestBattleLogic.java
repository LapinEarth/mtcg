import at.fhtw.bif3.swe.mtcg.if19b017.BattleLogic;
import at.fhtw.bif3.swe.mtcg.if19b017.cards.*;
import at.fhtw.bif3.swe.mtcg.if19b017.users.User;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class TestBattleLogic
{
    User mockedUser1 = new User("TestUser1", "testPassword", 20,0,0,0,0);
    User mockedUser2 = new User("TestUser2", "testPassword", 20,0,0,0,0);
    ArrayList<Card> deck1 = new ArrayList<>();
    ArrayList<Card> deck2 = new ArrayList<>();
    BattleLogic battleLogic;

    @Test
    @DisplayName("Check if Speciality is there")
    public void checkIfSpecialityDragonGoblin()
    {
        MonsterCard monster1 = new MonsterCard("FireDragon", Element.FIRE, 15, Monster.DRAGON);
        MonsterCard monster2 = new MonsterCard("FireGoblin", Element.FIRE, 15, Monster.GOBLIN);

        deck1.add(monster1);
        deck2.add(monster2);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        assertTrue("Fight should contain a speciality", battleLogic.checkSpecialities(monster1, monster2,0,0, 99));
    }

    @Test
    @DisplayName("Check if Speciality is there")
    public void checkIfSpecialityWaterSpellKnight()
    {
        MonsterCard monster1 = new MonsterCard("NormalKnight", Element.NORMAL, 15, Monster.KNIGHT);
        SpellCard spell = new SpellCard("WaterSpell", Element.WATER, 65);

        deck1.add(monster1);
        deck2.add(spell);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        assertTrue("Fight should contain a speciality", battleLogic.checkSpecialities(monster1, spell,0,0, 99));
    }

    @Test
    @DisplayName("Check if Speciality is there")
    public void checkIfSpecialityWizardOrk()
    {
        MonsterCard monster1 = new MonsterCard("WaterWizard", Element.WATER, 15, Monster.WIZARD);
        MonsterCard monster2 = new MonsterCard("NormalOrk", Element.NORMAL, 15, Monster.ORK);

        deck1.add(monster1);
        deck2.add(monster2);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        assertTrue("Fight should contain a speciality", battleLogic.checkSpecialities(monster1, monster2,0,0, 99));
    }

    @Test
    @DisplayName("Check if Speciality is there")
    public void checkIfSpecialityDragonElf()
    {
        MonsterCard monster1 = new MonsterCard("FireDragon", Element.FIRE, 15, Monster.DRAGON);
        MonsterCard monster2 = new MonsterCard("FireElves", Element.FIRE, 15, Monster.ELF);
        SpellCard spell = new SpellCard("WaterSpell", Element.WATER, 65);

        deck1.add(monster1);
        deck2.add(monster2);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        assertTrue("Fight should contain a speciality", battleLogic.checkSpecialities(monster1, monster2,0,0, 99));
    }

    @Test
    @DisplayName("Check if Speciality is there")
    public void checkIfSpecialityKrakenSpell()
    {
        MonsterCard monster1 = new MonsterCard("FireDragon", Element.WATER, 15, Monster.KRAKEN);
        SpellCard spell = new SpellCard("WaterSpell", Element.WATER, 65);

        deck1.add(monster1);
        deck2.add(spell);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        assertTrue("Fight should contain a speciality", battleLogic.checkSpecialities(monster1, spell,0,0, 99));
    }

    @Test
    @DisplayName("Check if Speciality is there")
    public void checkIfSpecialityLove()
    {
        MonsterCard monster1 = new MonsterCard("WaterDragon", Element.WATER, 15, Monster.DRAGON);
        MonsterCard monster2 = new MonsterCard("WaterDragon", Element.WATER, 15, Monster.DRAGON);

        deck1.add(monster1);
        deck2.add(monster2);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        assertTrue("There should be a speciality", battleLogic.checkSpecialities(monster1, monster2,0,0, 0));
    }

    @Test
    @DisplayName("Check if Speciality is there")
    public void checkIfNoSpeciality()
    {
        MonsterCard monster1 = new MonsterCard("WaterDragon", Element.WATER, 15, Monster.DRAGON);
        MonsterCard monster2 = new MonsterCard("WaterDragon", Element.WATER, 15, Monster.DRAGON);

        deck1.add(monster1);
        deck2.add(monster2);
        battleLogic = new BattleLogic(mockedUser1, mockedUser2, deck1, deck2);
        assertFalse("There should be no speciality", battleLogic.checkSpecialities(monster1, monster2,0,0, 99));
    }
}
