package at.fhtw.bif3.swe.mtcg.if19b017.server.response;

import at.fhtw.bif3.swe.mtcg.if19b017.database.DaoDatabase;
import at.fhtw.bif3.swe.mtcg.if19b017.database.structure.*;
import at.fhtw.bif3.swe.mtcg.if19b017.users.User;
import at.fhtw.bif3.swe.mtcg.if19b017.server.request.Request;
import at.fhtw.bif3.swe.mtcg.if19b017.server.request.RequestHttp;
import at.fhtw.bif3.swe.mtcg.if19b017.server.Helper;
import java.util.*;

public class ResponseHandler
{
    private static String responseText="[]\n";
    private static String code = "200";

    public static Response handleRequest(Request request)
    {
        responseText="[]\n";
        System.out.println("TEST");
        System.out.println("BODY: " + request.getBody());
//----------------------------------------------------------------------------------------------------------------------
        //POST METHODS TO INPUT DATA
//----------------------------------------------------------------------------------------------------------------------
        if(request.getRequestHttp() == RequestHttp.POST)
        {
            DaoDatabase daoDb = new DaoDatabase();
//----------------------------------------------------------------------------------------------------------------------
            if (request.getPathname().equals("/users") )
            {
                HashMap jsonMap = Helper.parseJson(request.getBody());
                if(daoDb.checkUserExistence(jsonMap.get("Username").toString()))
                {
                    responseText = "User already exists";
                }else
                    {
                        daoDb.saveUser(new UserData(jsonMap.get("Username").toString(),jsonMap.get("Password").toString(),20, false,100,0,0,0));
                        responseText = "User created";
                    }
//----------------------------------------------------------------------------------------------------------------------
            }else if(request.getPathname().equals("/sessions"))
                {
                    HashMap jsonMap = Helper.parseJson(request.getBody());
                    if(daoDb.logInOutUser(true, jsonMap.get("Username").toString(), jsonMap.get("Password").toString()))
                    {
                        responseText = "You are now logged in as: " + jsonMap.get("Username").toString();
                    }else
                        {
                            responseText = "Login failed";
                        }
//----------------------------------------------------------------------------------------------------------------------
                }else if(request.getPathname().equals("/packages") && Helper.checkToken("admin", request.getToken()))
                    {
                        if(daoDb.isLoggedIn(Helper.nameFromToken(request.getToken())))
                        {
                            List<PackageData> jsonMap = Helper.jsonToListForPackage(request.getBody());
                            jsonMap.forEach(x -> daoDb.saveCard(Helper.getCardsFromPackage(x)));
                            jsonMap.forEach(x -> daoDb.addPackage(x.getId()));
                            responseText = "Package created";
                        }
//----------------------------------------------------------------------------------------------------------------------
                    }else if(request.getPathname().equals("/transactions/packages"))
                        {
                            String username = Helper.nameFromToken(request.getToken());
                            if(daoDb.isLoggedIn(username))
                            {
                                if(daoDb.PackageExist())
                                {
                                    if (daoDb.getUserCoins(username) >= 5)
                                    {
                                        daoDb.acquirePackage(Helper.nameFromToken(request.getToken()));
                                        responseText = "Package acquired";
                                    }else
                                        {
                                            responseText = "Not enough coins";
                                        }
                                }else
                                    {
                                        responseText = "No package found";
                                    }
                            }else
                                {
                                    responseText = "You are not logged in";
                                }
//----------------------------------------------------------------------------------------------------------------------
                        }else if(request.getPathname().equals("/battles"))
                            {
                                if(request.getToken() != null)
                                {
                                    String username = Helper.nameFromToken(request.getToken());
                                    if (daoDb.isLoggedIn(username))
                                    {
                                        if(daoDb.getUserDeck(username).size() == 4)
                                        {
                                            String log = daoDb.battle(username);
                                            responseText = log;
                                            code = "200";
                                        }else
                                            {
                                                responseText = "Not enough cards in deck";
                                                code = "404";
                                            }
                                    }else
                                        {
                                            responseText = "You are not logged in";
                                            code = "404";
                                        }
                                }else
                                    {
                                        responseText = "No token";
                                        code = "404";
                                    }
                            }
//----------------------------------------------------------------------------------------------------------------------
            //GET METHODS TO GET DATA
//----------------------------------------------------------------------------------------------------------------------
        }else if(request.getRequestHttp() == RequestHttp.GET)
            {
                DaoDatabase daoDb = new DaoDatabase();
//----------------------------------------------------------------------------------------------------------------------
                if(request.getPathname().equals("/cards"))
                {
                    if(request.getToken() != null)
                    {
                        String username = Helper.nameFromToken(request.getToken());
                        if (daoDb.isLoggedIn(username))
                        {
                            responseText = Helper.writeListToJsonArray(daoDb.getUserCards(username));
                        }else
                            {
                                responseText ="You are not logged in";
                            }
                    }else
                        {
                            responseText = "No token";
                            code = "404";
                        }
//----------------------------------------------------------------------------------------------------------------------
                }else if(request.getPathname().equals("/deck"))
                    {
                        if(request.getToken() != null)
                        {
                            String username = Helper.nameFromToken(request.getToken());
                            if (daoDb.isLoggedIn(username))
                            {
                                responseText = Helper.writeListToJsonArray(daoDb.getUserDeck(username));
                            }else
                                {
                                    responseText ="You are not logged in";
                                }
                        }else
                            {
                                responseText = "No token";
                                code = "200";
                            }
//----------------------------------------------------------------------------------------------------------------------
                    }else if(request.getPathname().contains("/users/"))
                        {
                            String[] name = request.getPathname().split("/");
                            if(request.getToken() != null)
                            {
                                String username = Helper.nameFromToken(request.getToken());
                                if(daoDb.checkUserExistence(name[2]) && name[2].equals(username))
                                {
                                    if (daoDb.isLoggedIn(username))
                                    {
                                        String[] profile = daoDb.getUserProfile(username);
                                        responseText = "Name: " + profile[0] + " Bio: " + profile[1] + " Image: " + profile[2];
                                        code = "200";
                                    }else
                                        {
                                            responseText ="You are not logged in";
                                        }
                                }else
                                    {
                                        responseText = "Wrong user or token";
                                    }
                            }
//----------------------------------------------------------------------------------------------------------------------
                        }else if(request.getPathname().equals("/stats"))
                            {
                                if(request.getToken() != null)
                                {
                                    String username = Helper.nameFromToken(request.getToken());
                                    if (daoDb.isLoggedIn(username))
                                    {
                                        User user = daoDb.getUser(username);
                                        responseText = user.getUsername() +" Coins: " + user.getCoins()+ " Elo points: " + user.getEloPoints() + " Games played: "+ user.getGamesPlayed() + " Wins: "+ user.getWins() + " losses: " + user.getLosses();
                                    }else
                                        {
                                            responseText ="You are not logged in";
                                        }
                                }else
                                    {
                                        responseText = "No token";
                                        code = "404";
                                    }
//----------------------------------------------------------------------------------------------------------------------
                            }else if (request.getPathname().equals("/score"))
                                {
                                    if(request.getToken() != null)
                                    {
                                        String username = Helper.nameFromToken(request.getToken());
                                        if (daoDb.isLoggedIn(username))
                                        {
                                            List<UserScore> scores = daoDb.getScoreboard(username);
                                            responseText = Helper.writeScoreListToJsonArray(scores);
                                        }
                                    }else
                                    {
                                        responseText = "No token";
                                        code = "404";
                                    }
                                }
//----------------------------------------------------------------------------------------------------------------------
            //PUT METHODS FOR UPDATES
//----------------------------------------------------------------------------------------------------------------------
        }else if(request.getRequestHttp() == RequestHttp.PUT)
            {
                DaoDatabase daoDb = new DaoDatabase();
//----------------------------------------------------------------------------------------------------------------------
            if(request.getPathname().equals("/deck"))
            {
                if(request.getToken() != null) {
                    if (daoDb.isLoggedIn(Helper.nameFromToken(request.getToken()))) {
                        String[] cardIds = Helper.getCardIdsForDeck(request.getBody());
                        if (cardIds.length == 4) {
                            if (daoDb.createUserDeck(cardIds, Helper.nameFromToken(request.getToken()))) {
                                responseText = "Deck set";
                                code = "200";
                            } else {
                                responseText = "Deck not set";
                                code = "404";
                            }
                        } else {
                            responseText = "please pick exactly 4 cards";
                            code = "404";
                        }
                    }
                }else{
                    responseText = "no token";
                    code = "404";
                }
//----------------------------------------------------------------------------------------------------------------------
            }else if(request.getPathname().contains("/users/"))
                {
                    String[] name = request.getPathname().split("/");
                    if(request.getToken() != null)
                    {
                        String username = Helper.nameFromToken(request.getToken());
                        if(daoDb.checkUserExistence(name[2]) && name[2].equals(username))
                        {
                            if (daoDb.isLoggedIn(username))
                            {
                                HashMap jsonMap = Helper.parseJson(request.getBody());
                                if(daoDb.updateUserProfile(username , jsonMap.get("Name").toString(), jsonMap.get("Bio").toString(), jsonMap.get("Image").toString()))
                                {
                                    responseText = "Profile updated";
                                    code = "200";
                                }else
                                    {
                                        responseText = "Profile not found";
                                        code = "404";
                                    }

                            }else
                                {
                                    responseText = "not logged in";
                                    code = "404";
                                }

                        }else
                            {
                                responseText = "Wrong user or token";
                                code = "404";
                            }
                    }
                }
            }
        return new Response(code, responseText);
    }
}
