package at.fhtw.bif3.swe.mtcg.if19b017.database;

import at.fhtw.bif3.swe.mtcg.if19b017.cards.*;
import at.fhtw.bif3.swe.mtcg.if19b017.users.User;
import java.util.*;

public interface Dao<U, C, S>
{
    User getUser(String username);

    void saveUser(U u);

    void saveCard(C c);

    String battle(String username);

    void updateUserAfterBattle(String username, int win, int loss, int elopoints);

    Boolean openNewBattle(String username);

    Boolean joinBattle(String username, Integer id);

    List<Integer> activeBattles();

    String getInitiatingUser(String joining_user);

    List<Card> getUserCards(String username);

    List<Card> getUserDeck(String username);

    boolean createUserDeck(String[] cardIds, String userName);

    void addPackage(String cardId);

    Boolean PackageExist();

    void acquirePackage(String userName);

    void subtractCoins(String username);

    void deletePackage(ArrayList<String> card_ids);

    int getUserCoins(String username);

    String[] getUserProfile(String username);

    boolean updateUserProfile(String username, String profilename,String bio, String image);

    Boolean checkUserExistence(String username);

    List<S> getScoreboard(String username);

    boolean logInOutUser(Boolean status, String username, String password);

    Boolean isLoggedIn(String username);

    void deleteClosedBattle(String initiator, String joiner);
}
