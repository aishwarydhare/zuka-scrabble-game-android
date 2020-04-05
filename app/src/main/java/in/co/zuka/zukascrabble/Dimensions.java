package in.co.zuka.zukascrabble;

public class Dimensions {
    private int boardheight;
    private int scoreHeight;
    private int trayHeight;
    private int padding;
    private int top;
    private int totalWidth;
    private int totalHeight;
    private int cellSize;

    public Dimensions() {
        this.boardheight = this.scoreHeight = this.trayHeight = this.padding = this.top = this.totalHeight = this.totalWidth = this.cellSize = 0;
    }

    public Dimensions(Dimensions dimensions) {
        this.boardheight = dimensions.getBoardheight();
        this.scoreHeight = dimensions.getScoreHeight();
        this.trayHeight = dimensions.getTrayHeight();
        this.padding = dimensions.getPadding();
        this.top = dimensions.getTop();
        this.totalHeight = dimensions.getTotalHeight();
        this.totalWidth = dimensions.getTotalWidth();
        this.cellSize = dimensions.getCellSize();
    }

    public int getBoardheight() {
        return this.boardheight;
    }

    public void setBoardheight(int boardheight) {
        this.boardheight = boardheight;
    }

    public int getScoreHeight() {
        return this.scoreHeight;
    }

    public void setScoreHeight(int scoreHeight) {
        this.scoreHeight = scoreHeight;
    }

    public int getTrayHeight() {
        return this.trayHeight;
    }

    public void setTrayHeight(int trayHeight) {
        this.trayHeight = trayHeight;
    }

    public int getPadding() {
        return this.padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public int getTop() {
        return this.top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getTotalWidth() {
        return this.totalWidth;
    }

    public void setTotalWidth(int totalWidth) {
        this.totalWidth = totalWidth;
    }

    public int getTotalHeight() {
        return this.totalHeight;
    }

    public void setTotalHeight(int totalHeight) {
        this.totalHeight = totalHeight;
    }

    public int getCellSize() {
        return this.cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }
}
