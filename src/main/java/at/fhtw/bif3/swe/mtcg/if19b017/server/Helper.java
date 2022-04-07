package at.fhtw.bif3.swe.mtcg.if19b017.server;

import at.fhtw.bif3.swe.mtcg.if19b017.cards.*;
import at.fhtw.bif3.swe.mtcg.if19b017.database.structure.CardData;
import at.fhtw.bif3.swe.mtcg.if19b017.database.structure.PackageData;
//import at.fhtw.bif3.swe.mtcg.if19b017.database.structure.Trademarked;
import at.fhtw.bif3.swe.mtcg.if19b017.database.structure.UserScore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;


public class Helper {
    public static ArrayList<PackageData> jsonToListForPackage(String body) {
        final ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<PackageData> langList = null;
        try {
            langList = objectMapper.readValue(body, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return langList;
    }

    public static HashMap parseJson(String body){
        ObjectMapper mapper = new ObjectMapper();

        TypeReference<HashMap<String,Object>> typeRef
                = new TypeReference<HashMap<String,Object>>() {};
        HashMap<String,Object> o = null;
        try {
            o = mapper.readValue(body, typeRef);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return o;
    }

    public static boolean checkToken(String username, String token){
        String[] splitted = token.split("-mtcgToken");
        if (username.equals(splitted[0])){
            System.out.println("SPLITTED: " + splitted[0]);
            return true;
        }else {
            return false;
        }
    }

    public static String nameFromToken(String token){
        String[] splitted = token.split("-mtcgToken");
        return splitted[0];
    }

    public static CardData getCardsFromPackage(PackageData card){
        Element element;
        Boolean isSpell = false;
        Monster monster = null;

        //Get typ/monsterTyp
        if (card.getName().toLowerCase(Locale.ROOT).contains("spell")){
            System.out.println("its a spell: " + card.getName());
            isSpell = true;
        }else if(card.getName().toLowerCase(Locale.ROOT).contains("goblin")){
            monster = Monster.GOBLIN;
        }else if(card.getName().toLowerCase(Locale.ROOT).contains("dragon")){
            monster = Monster.DRAGON;
        }else if(card.getName().toLowerCase(Locale.ROOT).contains("wizard")){
            monster = Monster.WIZARD;
        }else if(card.getName().toLowerCase(Locale.ROOT).contains("ork")){
            monster = Monster.ORK;
        }else if(card.getName().toLowerCase(Locale.ROOT).contains("knight")){
            monster = Monster.KNIGHT;
        }else if(card.getName().toLowerCase(Locale.ROOT).contains("kraken")){
            monster = Monster.KRAKEN;
        }else if(card.getName().toLowerCase(Locale.ROOT).contains("elf")){
            monster = Monster.ELF;
        }//else if(card.getName().toLowerCase(Locale.ROOT).contains("troll")){
           // type = Monster.TROLL;
        //}

        //Get element
        if (card.getName().toLowerCase(Locale.ROOT).contains("water")){
            element = Element.WATER;
        }else if (card.getName().toLowerCase(Locale.ROOT).contains("fire")){
            element = Element.FIRE;
        }
        //elseif (card.getName().toLowerCase(Locale.ROOT).contains("normal")){
            //element = Element.NORMAL;
       //}
        else {
            element = Element.NORMAL;
        }

        if(isSpell){
            return new CardData(card.getId(), card.getName(), element.toString(), "Spell", card.getDamage(), "",false);
        }else {
            return new CardData(card.getId(), card.getName(), element.toString(), monster.toString(), card.getDamage(), "",false);
        }
    }

    //List to Json for Server Output to User
    public static String writeListToJsonArray(List<Card> cardList) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(out, cardList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final byte[] data = out.toByteArray();
        System.out.println(new String(data));
        return new String(data);
    }

    public static String writeScoreListToJsonArray(List<UserScore> scores) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(out, scores);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final byte[] data = out.toByteArray();
        System.out.println(new String(data));
        return new String(data);
    }

    /*
    public static String writeTradeDealsListToJsonArray(List<Trademarked> scores) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(out, scores);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final byte[] data = out.toByteArray();
        System.out.println(new String(data));
        return new String(data);
    }
*/

    public static String[] getCardIdsForDeck(String cardList) {
        ObjectMapper objectMapper = new ObjectMapper();

        //
        /*try {
            List<String> list = objectMapper.readValue(cardList, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/
        String[] cardArray = new String[0];
        try {
            cardArray = objectMapper.readValue(cardList, String[].class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < cardArray.length; i++){
            System.out.println("CARD FOR DECK: " + cardArray[i]);
        }
        return cardArray;
    }



}
