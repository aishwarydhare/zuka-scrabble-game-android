package in.co.zuka.zukascrabble;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class TilePool {

    public final static String BONUS_LETTERS = "ZXQJ";

    private Map<Character, Integer> tilesMap;
    private int numOfTiles;
    private String[] wordsPool;
    private int maxP;

    public TilePool(String[] wordsPool) {
        this.wordsPool = wordsPool;
        this.initializeMap();
        this.setNumOfTiles(calculateNumOfTiles());
    }

    public TilePool(TilePool tp, String[] wordsPool) {
        this.wordsPool = wordsPool;
        this.setNumOfTiles(tp.getNumOfTiles());
        tilesMap = new HashMap<>();
        tilesMap.putAll(tilesMap);
    }

    public Map<Character, Integer> getTilesMap() {
        return tilesMap;
    }

    public int calculateNumOfTiles() {
        return tilesMap.size();
    }

    public void putTile(Tile t) {
        tilesMap.put(t.getLetter(), (tilesMap.get(t.getLetter()) + 1));
    }

    public Tile switchTile(Tile tile) {
        tilesMap.put(tile.getLetter(), (tilesMap.get(tile.getLetter()) + 1));
        return getTile();
    }

    public boolean hasMoreTiles() {
        return (numOfTiles > 0) ? true : false;
    }

    public int getNumOfTiles() {
        return this.numOfTiles;
    }

    public void setNumOfTiles(int numOfTiles) {
        this.numOfTiles = numOfTiles;
    }

    public Tile getTile() {
        Random r = new Random();
        int p = r.nextInt(maxP+1);
        for (Map.Entry<Character, Integer> entry : tilesMap.entrySet()) {
            if (p < entry.getValue()) {
                numOfTiles = calculateNumOfTiles();
                return new Tile(entry.getKey());
            }
        }
        return null;
    }

    private void initializeMap() {
        double totalChars = 0.0;
        HashMap<Character, Integer> charOccurrenceMap = new HashMap<>();
        tilesMap = new HashMap<>();

        for(String s : this.wordsPool){
            // letter - occurrence probability map
            for(char c : s.toCharArray()){
                totalChars += 1;
                if (charOccurrenceMap.containsKey(c)) {
                    charOccurrenceMap.put(c, charOccurrenceMap.get(c) + 1);
                } else {
                    charOccurrenceMap.put(c, 1);

                }
            }
        }

        int occurrenceP, lastP = 0;
        for(Map.Entry<Character, Integer> entry : charOccurrenceMap.entrySet()){
            occurrenceP = (int) ((entry.getValue()/totalChars) * 100);
            occurrenceP = occurrenceP > 1 ? occurrenceP : 1;
            occurrenceP = occurrenceP + lastP;
            lastP = occurrenceP;
            tilesMap.put(entry.getKey(), occurrenceP);
        }

        tilesMap = Utils.sortByValue(tilesMap);
        maxP = lastP;

//        tilesMap.put('A', 8);
//        tilesMap.put('B', 3);
//        tilesMap.put('C', 5);
//        tilesMap.put('D', 4);
//        tilesMap.put('E', 11);
//        tilesMap.put('F', 2);
//        tilesMap.put('G', 3);
//        tilesMap.put('H', 3);
//        tilesMap.put('I', 8);
//        tilesMap.put('J', 1);
//        tilesMap.put('K', 1);
//        tilesMap.put('L', 5);
//        tilesMap.put('M', 3);
//        tilesMap.put('N', 6);
//        tilesMap.put('O', 7);
//        tilesMap.put('P', 4);
//        tilesMap.put('Q', 1);
//        tilesMap.put('R', 8);
//        tilesMap.put('S', 6);
//        tilesMap.put('T', 7);
//        tilesMap.put('U', 4);
//        tilesMap.put('V', 1);
//        tilesMap.put('W', 1);
//        tilesMap.put('X', 1);
//        tilesMap.put('Y', 2);
//        tilesMap.put('Z', 1);
    }
}
