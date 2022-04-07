import at.fhtw.bif3.swe.mtcg.if19b017.cards.Card;
import at.fhtw.bif3.swe.mtcg.if19b017.database.DaoDatabase;
import at.fhtw.bif3.swe.mtcg.if19b017.database.structure.CardData;
import at.fhtw.bif3.swe.mtcg.if19b017.database.structure.UserData;
import at.fhtw.bif3.swe.mtcg.if19b017.users.User;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class TestDatabase
{
    DaoDatabase daoDb = new DaoDatabase();

    @Test
    public void createUser()
    {
        daoDb.initDb();
        daoDb.saveUser(new UserData("UnitTestUserCreateUserTest", "12345678",20, false,100,0,0,0));
        User user = daoDb.getUser("UnitTestUserCreateUserTest");
        assertTrue("saveUser and getUser should work", user.getUsername().equals("UnitTestUserCreateUserTest"));
    }

    @Test
    public void getCardsFromUserTest()
    {
        daoDb.initDb();

        daoDb.saveUser(new UserData("UnitTestUserGetCardsTest", "12345678",20, false,100,0,0,0));

        daoDb.saveCard(new CardData("randomTestId1", "WaterKraken", "Water", "Kraken", 50, "", false ));
        daoDb.saveCard(new CardData("randomTestId2", "FireSpell", "Fire", "Spell", 60, "", false ));
        daoDb.saveCard(new CardData("randomTestId3", "NormalKnight", "Normal", "Knight", 70, "", false ));
        daoDb.saveCard(new CardData("randomTestId4", "WaterGoblin", "Water", "Goblin", 55, "", false ));

        daoDb.addPackage("randomTestId1");
        daoDb.addPackage("randomTestId2");
        daoDb.addPackage("randomTestId3");
        daoDb.addPackage("randomTestId4");

        daoDb.acquirePackage("UnitTestUserGetCardsTest");

        List<Card> cards = daoDb.getUserCards("UnitTestUserGetCardsTest");
        cards.stream().forEach(element -> System.out.println("Name: " + element.getName()));
        assertAll("Check cards", () -> assertEquals(cards.get(0).getName(), "WaterKraken", "Name should be WaterKraken"), () -> assertEquals(cards.get(1).getName(), "FireSpell", "ame should be FireSpell"), () -> assertEquals(cards.get(2).getName(), "NormalKnight", "ame should be NormalKnight"), () -> assertEquals(cards.get(3).getName(), "WaterGoblin", "ame should be WaterGoblin"));
    }

    @Test
    public void loggedInTest()
    {
        daoDb.initDb();
        daoDb.saveUser(new UserData("UnitTestUserLoggedInTest", "12345678",20, false,100,0,0,0));

        User user = daoDb.getUser("UnitTestUserLoggedInTest");
        daoDb.logInOutUser(true, user.getUsername(), user.getPassword());
        assertTrue("User login Status should be true, saveUser and getUser should work too", daoDb.isLoggedIn(user.getUsername()));
    }

    @Test
    public void loggedOutTest()
    {
        daoDb.initDb();
        daoDb.saveUser(new UserData("UnitTestUserLoggedOutTest", "12345678",20, false,100,0,0,0));
        User user = daoDb.getUser("UnitTestUserLoggedOutTest");
        assertFalse("User login Status should be true, saveUser and getUser should work too", daoDb.isLoggedIn(user.getUsername()));
    }

    @Test
    public void coinsTest()
    {
        daoDb.initDb();
        daoDb.saveUser(new UserData("UnitTestUserCoinsTest1", "12345678",20, false,100,0,0,0));
        daoDb.subtractCoins("UnitTestUserCoinsTest1");

        User user1 = daoDb.getUser("UnitTestUserCoinsTest1");

        assertTrue("user1 should have 15 coins", user1.getCoins() == 15);
    }
}
