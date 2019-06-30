package Domain;

public class Level {

    private int level;
    private int currXp;
    private int neededXp;
    private final static int MAX_LEVEL = 50;

    public Level(){
        level = 1;
        neededXp = 100;
    }

    public void setLevel(int l){level = l;}
    public void setCurrXp(int c){currXp = c;}
    public void setNeededXp(int n){neededXp = n;}

    public int getLevel(){return level;}
    public int getCurrXp(){return currXp;}
    public int getNeededXp(){return neededXp;}

    public void addXp(int newXp){
        if(level <= MAX_LEVEL) {
            currXp += newXp;
        }
        while(currXp >= neededXp && level < MAX_LEVEL){
            levelUp();
        }
    }

    private void levelUp(){
        currXp = currXp - neededXp;
        neededXp = (int) Math.floor(neededXp + neededXp*0.11);
        level++;
    }

}
