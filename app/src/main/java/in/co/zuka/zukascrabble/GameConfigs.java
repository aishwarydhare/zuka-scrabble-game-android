package in.co.zuka.zukascrabble;


import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

public class GameConfigs implements Parcelable {


    public static final Creator<GameConfigs> CREATOR = new Creator<GameConfigs>() {
        public GameConfigs createFromParcel(Parcel in) {
            return new GameConfigs(in);
        }

        public GameConfigs[] newArray(int size) {
            return new GameConfigs[size];
        }
    };
    static final int D_NUM_PLAYERS = 2;
    private Player[] players;
    private int numPlayers;
    private String[] wordsPool;

    public GameConfigs(String[] wordsPool) {
        this.setNumPlayers(D_NUM_PLAYERS);
        players = new Player[D_NUM_PLAYERS];
        players[0] = new Player("Player1", Color.BLUE, 0, null);
        players[1] = new Player("Player2", Color.RED, 1, null);
        this.wordsPool = wordsPool.clone();
    }

    public GameConfigs(int numPlayers, String[] names, int[] colors, String[] wordsPool) {
        this.setNumPlayers(numPlayers);
        players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player(names[i], colors[i], i, null);
        }
        this.wordsPool = wordsPool.clone();
    }

    public String[] getWordsPool() {
        return wordsPool;
    }

    public void setWordsPool(String[] wordsPool) {
        this.wordsPool = wordsPool;
    }

    public GameConfigs(Parcel in) {
        readFromParcel(in);
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public Player[] getPlayersList() {
        return players;
    }

    public void readFromParcel(Parcel in) {

        this.numPlayers = in.readInt();
        players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++)
            players[i] = new Player(in.readString(), in.readInt(), in.readInt(), null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            dest.writeString(players[i].getNickname());
            dest.writeInt(players[i].getColor());
            dest.writeInt(players[i].getPlayerID());
        }
    }

}
