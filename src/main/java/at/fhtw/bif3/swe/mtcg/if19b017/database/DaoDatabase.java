package at.fhtw.bif3.swe.mtcg.if19b017.database;

import at.fhtw.bif3.swe.mtcg.if19b017.BattleLogic;
import at.fhtw.bif3.swe.mtcg.if19b017.cards.*;
import at.fhtw.bif3.swe.mtcg.if19b017.database.structure.*;
import at.fhtw.bif3.swe.mtcg.if19b017.users.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DaoDatabase implements Dao<UserData, CardData, UserScore>
{
    public static void initDb()
    {
        // Drop/recreate the Database
        try (Connection connection = DatabaseConnection.getInstance().connect(""))
        {
            DatabaseConnection.executeSql(connection, "DROP DATABASE mtcg_database", true );
            DatabaseConnection.executeSql(connection,  "CREATE DATABASE mtcg_database", true );
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }

        // Creating all Tables
        try
        {
            DatabaseConnection.getInstance().executeSql("""
                    CREATE TABLE IF NOT EXISTS users (
                        userName TEXT PRIMARY KEY NOT NULL,
                        password TEXT NOT NULL,
                        coins INT NOT NULL,
                        eloPoints INT NOT NULL,
                        gamesPlayed INT NOT NULL,
                        wins INT NOT NULL,
                        losses INT NOT NULL,
                        isLoggedIn BOOLEAN NOT NULL,
                        profilename TEXT,
                        bio TEXT,
                        image TEXT
                    );

                    CREATE TABLE IF NOT EXISTS cards (
                        id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        damage FLOAT NOT NULL,
                        element TEXT NOT NULL,
                        type TEXT NOT NULL,
                        userName TEXT,
                        isDeck BOOLEAN NOT NULL,
                        CONSTRAINT users
                              FOREIGN KEY(userName)
                        	  REFERENCES users(userName) ON DELETE CASCADE
                    );
                    
                    CREATE TABLE IF NOT EXISTS packages(
                        id SERIAL PRIMARY KEY,
                        card_id TEXT NOT NULL,
                        CONSTRAINT cards
                              FOREIGN KEY(card_id)
                        	  REFERENCES cards(id)
                    );
                    
                    CREATE TABLE IF NOT EXISTS current_battles (
                        id SERIAL PRIMARY KEY,
                        initiating_user TEXT UNIQUE NOT NULL,
                        joining_user TEXT UNIQUE,
                        CONSTRAINT users
                            FOREIGN KEY(initiating_user)
                            REFERENCES users(username),
                            FOREIGN KEY(joining_user)
                            REFERENCES users(username)
                    );
                   
                    
                    """);
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
    }

    @Override
    public String battle(String username)
    {
        String log = "";
        List<Integer> openBattles = activeBattles();
        if (openBattles.size() > 0)
        {
            //Joining open Battle first in line
            if(joinBattle(username, openBattles.get(0)))
            {
                //Preparing for Battle start
                String initiating_user = getInitiatingUser(username);
                ArrayList<Card> userDeck1 = getUserDeck(initiating_user);
                ArrayList<Card> userDeck2 = getUserDeck(username);
                userDeck1.stream().forEach(element -> System.out.println(element.getName()));
                userDeck2.stream().forEach(element -> System.out.println(element.getName()));
                User user1 = getUser(initiating_user);
                User user2 = getUser(username);
                BattleLogic battle = new BattleLogic(user1, user2, userDeck1, userDeck2);
                battle.fight();
                String winner = battle.getWinner();
                deleteClosedBattle(initiating_user, username);
                log = battle.getHistory();
                if (winner.equals("Draw"))
                {
                    //Draw
                    updateUserAfterBattle(user1.getUsername(), 0, 0, -1);
                    updateUserAfterBattle(user2.getUsername(), 0, 0, -1);
                }else if(winner.equals(user1.getUsername()))
                    {
                        //Player 1 won
                        updateUserAfterBattle(user1.getUsername(), 1,0, 3);
                        updateUserAfterBattle(user2.getUsername(), 0, 1, -5);
                    }else if (winner.equals(user2.getUsername()))
                        {
                            //Player 2 won
                            updateUserAfterBattle(user1.getUsername(), 0, 1, -5);
                            updateUserAfterBattle(user2.getUsername(), 1, 0, 3);
                        }
                return log;
            }
        }else
            {
                //Trying to open new battle
                if(openNewBattle(username))
                {
                    System.out.println("New Battle opened");
                }else
                    {
                        System.out.println("Couldnt open battle");
                    }
            }
        return log;
    }

    @Override
    public void updateUserAfterBattle(String username, int win, int loss, int elopoints)
    {
        //Updating User stats like wins/losses and elopoints
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                UPDATE users
                SET gamesplayed = gamesplayed + 1,
                    wins = wins + ?,
                    losses = losses + ?,
                    elopoints = elopoints + ?
                WHERE username = ?;
                """ ))
        {
            statement.setInt(1, win);
            statement.setInt(2, loss);
            statement.setInt(3, elopoints);
            statement.setString(4, username);
            statement.execute();
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
    }

    @Override
    public ArrayList<Card> getUserDeck(String username)
    {
        //Get all Cards from the User that are in his deck
        ArrayList<Card> userDeck = new ArrayList<>();
        try (PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                SELECT name, element, damage, type
                FROM cards
                Where userName = ? AND isDeck = true
                """))
        {
            statement.setString(1, username );
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next())
            {
                if (resultSet.getString(4).toLowerCase(Locale.ROOT).contains("spell"))
                {
                    userDeck.add(new SpellCard(resultSet.getString(1), Element.valueOf(resultSet.getString( 2)), resultSet.getDouble( 3)));
                }else
                    {
                        userDeck.add(new MonsterCard(resultSet.getString(1), Element.valueOf(resultSet.getString( 2)), resultSet.getDouble( 3), Monster.valueOf(resultSet.getString(4))));
                    }
            }
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        return userDeck;
    }

    @Override
    public Boolean openNewBattle(String username){
        //Open a new battle
        try (PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                INSERT INTO current_battles
                (initiating_user)
                VALUES (?);
                """ ))
        {
            statement.setString(1, username );
            statement.execute();
            return true;
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        return false;
    }

    @Override
    public Boolean joinBattle(String username, Integer id){
        //Add user to Battle
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                UPDATE current_battles
                SET joining_user = ?
                WHERE id = ?
                """ ))
        {
            statement.setString(1, username);
            statement.setInt(2, id);
            statement.execute();
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        return true;
    }

    @Override
    public List<Integer> activeBattles(){
        //Get List of active Battles
        List<Integer> openBattles = new ArrayList<>();
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                SELECT id
                FROM current_battles
                WHERE joining_user IS null AND
                      initiating_user IS NOT null
                      LIMIT 1;
                """))
        {
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next() )
            {
                //Add found open Battles to the list
                openBattles.add(resultSet.getInt(1));
            }
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        System.out.println("LIST SIZE:" + openBattles.size());
        return openBattles;
    }

    @Override
    public String getInitiatingUser(String joining_user){
        //Get User that started the battle
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                SELECT initiating_user
                FROM current_battles
                WHERE joining_user = ?
                """))
        {
            statement.setString(1, joining_user);
            ResultSet resultSet = statement.executeQuery();
            if( resultSet.next() )
            {
                return resultSet.getString(1);
            }
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        return "";
    }

    @Override
    public List<Card> getUserCards(String username)
    {
        //Get all Cards from a specific User
        List<Card> userCards = new ArrayList<>();
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                SELECT name, element, damage
                FROM cards
                Where userName = ?
                """))
        {
            statement.setString(1, username );
            ResultSet resultSet = statement.executeQuery();
            while( resultSet.next() )
            {
                userCards.add( new Card(resultSet.getString(1), Element.valueOf(resultSet.getString( 2).toUpperCase(Locale.ROOT)), resultSet.getDouble( 3)));
            }
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        return userCards;
    }

    @Override
    public boolean createUserDeck(String[] cardIds, String userName)
    {
        //Add Cards to the Users Deck
        Boolean query_status = false;
        for (int i = 0; i < cardIds.length; i++)
        {
            System.out.println("DB QUERY CARD: " + cardIds[i]);
            try (PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                    UPDATE cards
                    SET isDeck = true
                    WHERE id = ? AND userName = ?
                    """))
            {
                statement.setString(1, cardIds[i]);
                statement.setString(2, userName);
                statement.execute();
                query_status = true;
            }catch (SQLException throwables)
                {
                    throwables.printStackTrace();
                }
        }
        return query_status;
    }

    @Override
    public void addPackage(String cardId)
    {
        //Adds Cards to packages
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                INSERT INTO packages
                (card_id)
                VALUES (?);
                """ ))
        {
            statement.setString(1, cardId );
            statement.execute();
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
    }

    @Override
    public Boolean PackageExist()
    {
        //Checks if atleast one Package exists
        boolean status = false;
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                SELECT * FROM packages
                """ ))
        {
            ResultSet resultSet =  statement.executeQuery();
            while( resultSet.next() )
            {
                status = true;
            }
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        return status;
    }

    @Override
    public void acquirePackage(String userName)
    {
        //User buys a package
        ArrayList<String> cardIds = new ArrayList<>();
        //Gets the Cards from the Package
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                SELECT card_id FROM packages LIMIT 5;
                """ ))
        {
            ResultSet resultSet = statement.executeQuery();
            while( resultSet.next() )
            {
                cardIds.add(resultSet.getString(1));
            }
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        //Deletes the acquired cards from the package
        for (int i = 0; i < cardIds.size(); i++){
            try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                UPDATE cards
                SET userName = ?
                WHERE id = ?;
                """ ))
            {
                statement.setString(1, userName);
                statement.setString( 2, cardIds.get(i));
                statement.execute();
                deletePackage(cardIds);
            }catch (SQLException throwables)
                {
                    throwables.printStackTrace();
                }
        }
        //Cost of the package
        subtractCoins(userName);
    }

    @Override
    public void subtractCoins(String username)
    {
        //Removes 5 coins from User (cost of the package)
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                UPDATE users
                SET coins = coins - 5
                WHERE username = ?;
                """ ))
        {
            statement.setString(1, username);
            statement.execute();
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
    }

    @Override
    public void deletePackage(ArrayList<String> card_ids)
    {
        //Deletes cards from packages
        for (int i = 0; i < card_ids.size(); i++)
        {
            try (PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                    DELETE FROM packages
                    WHERE card_id = ?;
                    """))
            {
                statement.setString(1, card_ids.get(i));
                statement.execute();
            }catch (SQLException throwables)
                {
                    throwables.printStackTrace();
                }
        }
    }

    @Override
    public int getUserCoins(String username)
    {
        //Gets the ammount of coins a user has
        int coins = 0;
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                SELECT coins
                FROM users
                WHERE username=?
                """))
        {
            statement.setString( 1, username );
            ResultSet resultSet = statement.executeQuery();
            if( resultSet.next() )
            {
                coins = resultSet.getInt(1);
            }
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        return coins;
    }

    @Override
    public User getUser(String username)
    {
        //Get data from a specific user
        String userName = "", password = "";
        int coins = 0, eloPoints = 0, gamesPlayed = 0, wins = 0, losses = 0;
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                SELECT userName, password, coins, eloPoints, gamesPlayed, wins, losses
                FROM users
                WHERE username=?
                """))
        {
            statement.setString( 1, username );
            ResultSet resultSet = statement.executeQuery();
            if( resultSet.next() )
            {
                userName = resultSet.getString(1);
                password = resultSet.getString(2);
                coins = resultSet.getInt(3);
                eloPoints = resultSet.getInt(4);
                gamesPlayed = resultSet.getInt(5);
                wins = resultSet.getInt(6);
                losses = resultSet.getInt(7);
            }
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        return new User(userName, password, coins, eloPoints, gamesPlayed, wins, losses);
    }

    @Override
    public String[] getUserProfile(String username)
    {
        String[] profile = new String[3];
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                SELECT profilename, bio, image
                FROM users
                WHERE username=?
                """))
        {
            statement.setString( 1, username );
            ResultSet resultSet = statement.executeQuery();
            if( resultSet.next() )
            {
                profile[0] = resultSet.getString(1);
                profile[1] = resultSet.getString(2);
                profile[2] = resultSet.getString(3);
            }
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        return profile;
    }

    @Override
    public boolean updateUserProfile(String username, String profilename,String bio, String image)
    {
        //Updates the data of a user
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                UPDATE users
                SET profilename = ?, bio = ?, image = ?
                WHERE username = ?;
                """))
        {
            statement.setString(1, profilename );
            statement.setString( 2, bio );
            statement.setString(3, image );
            statement.setString(4, username );
            statement.execute();
        } catch (SQLException throwables)
            {
                throwables.printStackTrace();
                return false;
            }
        return true;
    }

    @Override
    public Boolean checkUserExistence(String username)
    {
        //Checks if a user exists
        Boolean status = false;
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                SELECT 1
                FROM users
                WHERE username=?
                LIMIT 1;
                """))
        {
            statement.setString( 1, username );
            ResultSet resultSet = statement.executeQuery();
            if( resultSet.next() )
            {
                status = true;
            }
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        return status;
    }

    @Override
    public List<UserScore> getScoreboard(String username)
    {
        //Create Scoreboard with the Data from the Database
        List<UserScore> userScores = new ArrayList<>();
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                SELECT userName, eloPoints
                FROM users
                ORDER BY eloPoints DESC
                """))
        {
            ResultSet resultSet = statement.executeQuery();
            while( resultSet.next() )
            {
                userScores.add( new UserScore(resultSet.getString(1), resultSet.getInt( 2 )) );
            }
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        return userScores;
    }

    @Override
    public void saveUser(UserData userData)
    {
        //Save User into the Database
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                INSERT INTO users
                (userName, password, coins, isLoggedIn, eloPoints, gamesPlayed, wins, losses)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """ ))
        {
            statement.setString(1, userData.getUserName() );
            statement.setString( 2, userData.getPassword() );
            statement.setInt( 3, userData.getCoins() );
            statement.setBoolean( 4, userData.getLogInStatus() );
            statement.setInt( 5, userData.getEloPoints() );
            statement.setInt( 6, userData.getGamesPlayed() );
            statement.setInt( 7, userData.getWins() );
            statement.setInt( 8, userData.getLosses() );
            statement.execute();
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
    }

    @Override
    public boolean logInOutUser(Boolean status, String username, String password)
    {
        //Login/Logout User if Username and Password are correct
        Boolean query_status = false;
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                UPDATE users
                SET isLoggedIn = ?
                WHERE username = ? AND
                      password = ?
                      returning username;
                """))
        {
            statement.setBoolean(1, status);
            statement.setString( 2, username);
            statement.setString(3, password);
            ResultSet resultSet = statement.executeQuery();
            //if user exists and name and password right
            if(resultSet.next() )
            {
                query_status = true;
            }
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        return query_status;
    }

    @Override
    public Boolean isLoggedIn(String username)
    {
        //Check if User is logged in
        Boolean logIn_status = false;
        try (PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                SELECT isLoggedIn
                FROM users
                WHERE username=?
                """))
        {
            statement.setString( 1, username );
            ResultSet resultSet = statement.executeQuery();
            if( resultSet.next() )
            {
                logIn_status = resultSet.getBoolean(1);
            }
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        return logIn_status;
    }

    @Override
    public void deleteClosedBattle(String initiator, String joiner)
    {
        //Delete closed Battle from the Database
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                DELETE FROM current_battles
                WHERE initiating_user = ? AND joining_user = ?;
                """))
        {
            statement.setString( 1, initiator );
            statement.setString( 2, joiner );
            statement.execute();
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
    }

    @Override
    public void saveCard(CardData cardData)
    {
        //Save cards into the Database
        try ( PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement("""
                INSERT INTO cards
                (id, name, damage, element, type, isDeck)
                VALUES (?, ?, ?, ?, ?, ?);
                """ ))
        {
            statement.setString( 1, cardData.getId());
            statement.setString(2, cardData.getName());
            statement.setDouble( 3, cardData.getDAMAGE());
            statement.setString(4,cardData.getElement());
            statement.setString(5,cardData.getType());
            statement.setBoolean(6,cardData.getIsDeck());
            statement.execute();
        }catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
    }
}
