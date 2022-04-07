package at.fhtw.bif3.swe.mtcg.if19b017;

import at.fhtw.bif3.swe.mtcg.if19b017.cards.*;
import at.fhtw.bif3.swe.mtcg.if19b017.users.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public class BattleLogic
{
    private final User user1;
    private final User user2;
    private ArrayList<Card> deck1;
    private ArrayList<Card> deck2;
    private String history = "";
    private String winner;

    public BattleLogic(User user1, User user2, ArrayList<Card> deck1, ArrayList<Card> deck2){
        this.user1 = user1;
        this.user2 = user2;
        this.deck1 = deck1;
        this.deck2 = deck2;
    }

    public void fight()
    {
        deck1.stream().sorted(Comparator.comparingDouble(Card::getDamage).reversed()).collect(Collectors.toList()).forEach(element -> System.out.println(element.getName() + " Dmg:" + element.getDamage()));
        deck2.stream().sorted(Comparator.comparingDouble(Card::getDamage).reversed()).collect(Collectors.toList()).forEach(element -> System.out.println(element.getName() + " Dmg:" + element.getDamage()));
        Collections.shuffle(deck2);
        Collections.shuffle(deck2);
        for(int i = 1; i < 101; i++)
        {
            //Draws a random card from each players deck and than compares them
            if (i == 100 || deck1.size() == 0 && deck2.size() == 0)
            {
                winner = "Draw";
                history = history + "\n Game endet in a draw! \n";
                break;
            }else if(deck1.size() != 0 && deck2.size() != 0)
                {
                    compareCards(i);
                }else if(deck2.size() == 0)
                    {
                        winner = user1.getUsername();
                        history = history + "\n Game winner is: " + user1.getUsername();
                        break;
                    }else if(deck1.size() == 0)
                        {
                            winner = user2.getUsername();
                            history = history + "\n Game winner is: " + user2.getUsername();
                            break;
                        }
        }
    }

    public void compareCards(int round)
    {
        int user1DrawnCard = (int)(Math.random() * deck1.size());
        int user2DrawnCard = (int)(Math.random() * deck2.size());

        Card card1 = deck1.get(user1DrawnCard);
        Card card2 = deck2.get(user2DrawnCard);

        if (checkSpecialities(card1, card2, user1DrawnCard, user2DrawnCard, (int) (Math.random() * 100)))
        {
            //Dmg calculation is done in the checkSpecialities method so nothing happens in here
        }else if(deck1.get(user1DrawnCard) instanceof SpellCard || deck2.get(user2DrawnCard) instanceof SpellCard)
            {
                compareByElements(card1, card2, user1DrawnCard, user2DrawnCard);
            }else if(card1 instanceof MonsterCard && card2 instanceof MonsterCard)
                {
                    compareDMG(card1.getDamage(), card2.getDamage(), user1DrawnCard, user2DrawnCard);
                }
    }

    private void compareByElements(Card card1, Card card2, int user1DrawnCard, int user2DrawnCard)
    {
        //Method to compare Dmg if a spell is in the fight
        if((card1.getElement() == Element.FIRE) && (card2.getElement() == Element.NORMAL))
        {
            compareDMG((card1.getDamage() * 2), (card2.getDamage() / 2), user1DrawnCard, user2DrawnCard);
        } else if((card2.getElement() == Element.FIRE) && (card1.getElement() == Element.NORMAL))
            {
                compareDMG((card1.getDamage() / 2), (card2.getDamage() * 2), user1DrawnCard, user2DrawnCard);
            } else if((card1.getElement() == Element.WATER) && (card2.getElement() == Element.FIRE))
                {
                    compareDMG((card1.getDamage() * 2), (card2.getDamage() / 2), user1DrawnCard, user2DrawnCard);
                } else if((card2.getElement() == Element.WATER) && (card1.getElement() == Element.FIRE))
                    {
                        compareDMG((card1.getDamage() / 2), (card2.getDamage() * 2), user1DrawnCard, user2DrawnCard);
                    } else if ((card1.getElement() == Element.NORMAL) && (card2.getElement() == Element.WATER))
                        {
                            compareDMG((card1.getDamage() * 2), (card2.getDamage() / 2), user1DrawnCard, user2DrawnCard);
                        } else if ((card2.getElement() == Element.NORMAL) && (card1.getElement() == Element.WATER))
                            {
                                compareDMG((card1.getDamage() / 2), (card2.getDamage() * 2), user1DrawnCard, user2DrawnCard);
                            } else
                                {
                                    compareDMG(card1.getDamage(), card2.getDamage(), user1DrawnCard, user2DrawnCard);
                                }
    }


    public boolean checkSpecialities(Card card1, Card card2, int user1DrawnCard, int user2DrawnCard, int random)
    {
        //Checks for Speciality rules
//----------------------------------------------------------------------------------------------------------------------
        //MONSTER ONLY
//----------------------------------------------------------------------------------------------------------------------
        if (card1 instanceof MonsterCard && card2 instanceof MonsterCard)
        {
            if(random == 0)
            {
                compareDMG(0, 0, user1DrawnCard, user2DrawnCard);
                return true;
            }else
                {
//----------------------------------------------------------------------------------------------------------------------
                    //Check if Goblin vs Dragon Speciality
                    if (((MonsterCard) card1).getMonster().equals(Monster.DRAGON) && ((MonsterCard) card2).getMonster().equals(Monster.GOBLIN))
                    {
                        compareDMG(card1.getDamage(), 0, user1DrawnCard, user2DrawnCard);
                        return true;
                    } else if (((MonsterCard) card2).getMonster().equals(Monster.DRAGON) && ((MonsterCard) card1).getMonster().equals(Monster.GOBLIN))
                        {
                            compareDMG(0, card2.getDamage(), user1DrawnCard, user2DrawnCard);
                            return true;
//----------------------------------------------------------------------------------------------------------------------
                            //Check if Wizard vs Ork Speciality
                        } else if (((MonsterCard) card1).getMonster().equals(Monster.WIZARD) && ((MonsterCard) card2).getMonster().equals(Monster.ORK))
                            {
                                compareDMG(card1.getDamage(), 0, user1DrawnCard, user2DrawnCard);
                                return true;
                            } else if (((MonsterCard) card2).getMonster().equals(Monster.WIZARD) && ((MonsterCard) card1).getMonster().equals(Monster.ORK))
                                {
                                    compareDMG(0, card2.getDamage(), user1DrawnCard, user2DrawnCard);
                                    return true;
//----------------------------------------------------------------------------------------------------------------------
                                    //check if FireElves vs Dragon Speciality
                                } else if ((((MonsterCard) card1).getMonster().equals(Monster.ELF) && card1.getElement().equals(Element.FIRE)) && ((MonsterCard) card2).getMonster().equals(Monster.DRAGON))
                                    {
                                        compareDMG(card1.getDamage(), 0, user1DrawnCard, user2DrawnCard);
                                        return true;
                                    } else if ((((MonsterCard) card2).getMonster().equals(Monster.ELF) && card2.getElement().equals(Element.FIRE)) && ((MonsterCard) card1).getMonster().equals(Monster.DRAGON))
                                        {
                                            compareDMG(0, card2.getDamage(), user1DrawnCard, user2DrawnCard);
                                            return true;
                                        }
                }
//----------------------------------------------------------------------------------------------------------------------
        //WITH SPELLS
//----------------------------------------------------------------------------------------------------------------------
            //Check if WaterSpell vs Knight Speciality
        }else if((card1 instanceof SpellCard && card1.getElement().equals(Element.WATER)) && (card2 instanceof MonsterCard && ((MonsterCard) card2).getMonster().equals(Monster.KNIGHT)))
            {
                compareDMG(card1.getDamage(), 0, user1DrawnCard, user2DrawnCard);
                return true;
            }else if((card2 instanceof SpellCard && card2.getElement().equals(Element.WATER)) && (card1 instanceof MonsterCard && ((MonsterCard) card1).getMonster().equals(Monster.KNIGHT)))
                {
                    compareDMG(0, card2.getDamage(), user1DrawnCard, user2DrawnCard);
                    return true;
//----------------------------------------------------------------------------------------------------------------------
                    //Check if Kraken vs Spell Speciality
                } else if((card1 instanceof MonsterCard && ((MonsterCard) card1).getMonster().equals(Monster.KRAKEN)) && card2 instanceof SpellCard)
                        {
                            compareDMG(card1.getDamage(), 0, user1DrawnCard, user2DrawnCard);
                            return true;
                        }else if((card2 instanceof MonsterCard && ((MonsterCard) card2).getMonster().equals(Monster.KRAKEN)) && card1 instanceof SpellCard)
                            {
                                compareDMG(0, card2.getDamage(), user1DrawnCard, user2DrawnCard);
                                return true;
                            }
        return false;
    }

    private void compareDMG(double dmg1, double dmg2, int user1DrawnCard, int user2DrawnCard)
    {
        //Compare Dmg of the 2 Users dmg1=User1, dmg2=User2
        if (dmg1 > dmg2)
        {
            showWinner(user1, user1DrawnCard, user2DrawnCard);
            deck1.add(deck2.get(user2DrawnCard));
            deck2.remove(deck2.get(user2DrawnCard));
        }else if (dmg2 > dmg1)
            {
                showWinner(user2, user1DrawnCard, user2DrawnCard);
                deck2.add(deck1.get(user1DrawnCard));
                deck1.remove(deck1.get(user1DrawnCard));
            }else
                {
                    showDraw(user1DrawnCard, user2DrawnCard);
                }
    }

    public void showWinner(User winner, int user1DrawnCard, int user2DrawnCard)
    {
        history = history + "\n [Round Winner: " + winner.getUsername() + " Fight Details: " + user1.getUsername() + " "+  deck1.get(user1DrawnCard).getName() + " " + deck1.get(user1DrawnCard).getDamage() + " vs " + user2.getUsername() + " " + deck2.get(user2DrawnCard).getName() + " " +  deck2.get(user2DrawnCard).getDamage() + "]";
    }

    public void showDraw(int user1DrawnCard, int user2DrawnCard)
    {
        history = history + "\n [Round Winner: Draw Fight Details: " + user1.getUsername() + " "+  deck1.get(user1DrawnCard).getName() + " " + deck1.get(user1DrawnCard).getDamage() + " vs " + user2.getUsername() + " " + deck2.get(user2DrawnCard).getName() + " " +  deck2.get(user2DrawnCard).getDamage() + "]";
    }

    public String getWinner()
    {
        return winner;
    }

    public String getHistory()
    {
        return history;
    }
}
