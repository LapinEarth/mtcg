package at.fhtw.bif3.swe.mtcg.if19b017;

import java.io.IOException;
import at.fhtw.bif3.swe.mtcg.if19b017.database.DaoDatabase;
import at.fhtw.bif3.swe.mtcg.if19b017.server.Server;

public class Main
{
    private static DaoDatabase daodb;

    public static void main(String[] args)
    {
        daodb = new DaoDatabase();
        DaoDatabase.initDb();
        Server server = new Server();
        try
        {
            server.start(10001);
        }catch (IOException e)
            {
                e.printStackTrace();
            }
    }
}
