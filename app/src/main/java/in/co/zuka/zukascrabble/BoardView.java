package in.co.zuka.zukascrabble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.PopupWindow;

import java.util.Iterator;

public class BoardView extends SurfaceView implements Callback {

    private static final float MIN_FONT_DIPS = 14.0F;
    private static final int[] STACK_COLOR = new int[]{-1, -13909330, -13909439, -6314473, -3708080};
    private static int BOARD_SIZE = 10;
    protected int defaultFontSize;
    private Dimensions dimensions;
    private Button endTurn;

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            boolean clickable = msg.getData().getBoolean("clickable");
            if (clickable) {
                BoardView.this.endTurn.setClickable(true);
                BoardView.this.endTurn.setEnabled(true);
//                BoardView.this.endTurn.setImageDrawable(getResources().getDrawable(R.drawable.end_turn_available));
            } else {
                BoardView.this.endTurn.setClickable(false);
                BoardView.this.endTurn.setEnabled(false);
//                BoardView.this.endTurn.setImageDrawable(getResources().getDrawable(R.drawable.end_turn));
            }

        }
    };

    private BoardView.DrawingThread thread;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder = this.getHolder();
        holder.addCallback(this);
        this.thread = new BoardView.DrawingThread(holder, this.handler);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        float scale = this.getResources().getDisplayMetrics().density;
        this.defaultFontSize = (int) (14.0F * scale + 0.5F);
        this.thread.setDefaultFontSize(this.defaultFontSize);
        this.dimensions = this.calculateDimensions(this.getWidth(), this.getHeight());
        this.thread.setDimensions(this.dimensions);
        this.thread.setRunning(true);
        this.thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        this.thread.setRunning(false);

        while (retry) {
            try {
                this.thread.join();
                retry = false;
            } catch (InterruptedException var4) {
            }
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        int evt = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        this.thread.setEventInfo(evt, x, y);

        try {
            Thread.sleep(16L);
        } catch (InterruptedException var6) {
            var6.printStackTrace();
        }

        return true;
    }

    public void setEndTurn(Button end) {
        this.endTurn = end;
        this.endTurn.setClickable(false);
    }

    public void setSwitchMode(boolean b) {
        this.thread.setSwitchMode(b);
    }

    public Dimensions getDimensions() {
        return this.dimensions;
    }

    public void setGameEngine(GameEngine ge) {
        this.thread.setGameEngine(ge);
    }

    public void setPopupWindow(PopupWindow popUp) {
        this.thread.setPopupWindow(popUp);
    }

    private Dimensions calculateDimensions(int width, int height) {
        Dimensions dims = new Dimensions();
        dims.setTotalWidth(width);
        dims.setTotalHeight(height);
        int cellSize = width / BOARD_SIZE;
        int maxCellSize = 3 * this.defaultFontSize;
        if (maxCellSize < cellSize) {
            cellSize = maxCellSize;
        }

        int bWidth = BOARD_SIZE * cellSize;
        int padding = (width - bWidth) / 2;
        int scHeight = this.defaultFontSize * 2;
        int tHeight = cellSize * 3;
        int top;
        if (height >= bWidth + scHeight + tHeight) {
            top = (height - bWidth - scHeight - tHeight) / 2;
        } else {
            top = 0;
            tHeight = 4 * this.defaultFontSize;
            if (height - bWidth - tHeight > tHeight) {
                tHeight = height - bWidth - tHeight;
                scHeight = height - bWidth - tHeight;
            } else {
                scHeight = height - bWidth - tHeight;
            }
        }

        dims.setCellSize(cellSize);
        dims.setBoardheight(bWidth);
        dims.setPadding(padding);
        dims.setScoreHeight(scHeight);
        dims.setTop(top);
        dims.setTrayHeight(tHeight);
        return dims;
    }

    class DrawingThread extends Thread {
        private static final int TRAY_AREA = 1;
        private static final int BOARD_AREA = 2;
        private static final int SCORE_AREA = 3;
        private static final String STACK_MESSAGE = "Tile Stack";
        private boolean mRun;
        private SurfaceHolder mSurfaceHolder;
        private Dimensions dims;
        private GameEngine ge;
        private int defaultFontS;
        private PopupWindow pw;
        private int event;
        private int eventX;
        private int eventY;
        private int prevEventX;
        private int prevEventY;
        private Handler mHandler;
        private boolean switchMode;
        private boolean stackOpen;
        private boolean tileIsMoved;
        private boolean boardTileIsMoved;
        private TileStack openStack;
        private Tile movingTile;
        private Rect[] tilesTray;
        private Paint fillScorePaint;
        private Paint fillTrayPaint;
        private Paint fillBoardPaint;
        private Paint strokePaint;
        private Paint tileFillPaint;
        private Paint tileStrokePaint;
        private Paint tileTextPaint;
        private Paint centralSquarePaint;
        private Paint scorePaint;
        private Paint selectedTilePaint;
        private Paint underneathCellPaint;
        private Paint stackPanePaint;
        private Paint stackTextPaint;
        private int selectedTileNum;
        private int selectedBoardTileX;
        private int selectedBoardTileY;
        private int movingTileX;
        private int movingTileY;
        private int topLeftX;
        private int topLeftY;

        public DrawingThread(SurfaceHolder holder, Handler handler) {
            this.mSurfaceHolder = holder;
            this.mRun = false;
            this.switchMode = false;
            this.tileIsMoved = false;
            this.boardTileIsMoved = false;
            this.stackOpen = false;
            this.openStack = new TileStack();
            this.event = this.eventX = this.eventY = -1;
            this.prevEventX = this.prevEventY = -1;
            this.selectedTileNum = -1;
            this.selectedBoardTileX = -1;
            this.selectedBoardTileY = -1;
            this.movingTileX = -1;
            this.movingTileY = -1;
            this.topLeftX = -1;
            this.topLeftY = -1;
            this.mHandler = handler;
            this.dims = new Dimensions();
            this.movingTile = new Tile();
            this.defaultFontS = 14;
            this.ge = null;
            this.tilesTray = new Rect[1];
            this.paintInitialisation();
            BoardView.this.setFocusable(true);
        }

        public void setRunning(boolean b) {
            this.mRun = b;
        }

        public void run() {
            while (this.mRun) {
                Canvas c = null;

                try {
                    c = this.mSurfaceHolder.lockCanvas((Rect) null);
                    synchronized (this.mSurfaceHolder) {
                        this.updateGame();
                        this.doDraw(c);
                    }
                } finally {
                    if (c != null) {
                        this.mSurfaceHolder.unlockCanvasAndPost(c);
                    }

                }
            }
        }

        public void setSwitchMode(boolean b) {
            synchronized (this.mSurfaceHolder) {
                this.switchMode = b;
            }
        }

        public void setGameEngine(GameEngine ge) {
            synchronized (this.mSurfaceHolder) {
                this.ge = ge;
            }
        }

        public void setDimensions(Dimensions dimensions) {
            synchronized (this.mSurfaceHolder) {
                this.dims = dimensions;
            }
        }

        public void setEventInfo(int evt, int x, int y) {
            synchronized (this.mSurfaceHolder) {
                this.event = evt;
                this.eventX = x;
                this.eventY = y;
            }
        }

        public void setDefaultFontSize(int dfs) {
            synchronized (this.mSurfaceHolder) {
                this.defaultFontS = dfs;
            }
        }

        public void setPopupWindow(PopupWindow popUp) {
            synchronized (this.mSurfaceHolder) {
                this.pw = popUp;
            }
        }

        private void updateGame() {
            if (this.ge != null) {
                Board b;
                label84:
                switch (this.event) {
                    case 0: // ACTION_DOWN
                        int area = this.getArea(this.eventX, this.eventY);
                        switch (area) {
                            case 1: // other
                                this.handleTrayClick(this.eventX, this.eventY);
                                if (this.switchMode && this.selectedTileNum != -1) {
                                    this.switchMode = false;
                                    this.pw.dismiss();
                                    this.ge.makeSwitch(this.selectedTileNum);
                                    this.selectedTileNum = -1;
                                }
                                break label84;
                            case 2: // board touch
                                this.handleBoardClick(this.eventX, this.eventY);
                            case 3: // score touch
                            default:
                                break label84;
                        }
                    case 1: // ACTION_UP
                        this.stackOpen = false;
                        this.openStack = null;
                        if (this.tileIsMoved) {
                            b = this.ge.getBoard();
                            int turnx = this.ge.getPlayerTurn();
                            Tray tx = this.ge.getPlayer(turnx).getTray();
                            if (this.getArea(this.eventX, this.eventY) == 2) {
                                int i = this.findCellRow(this.eventY);
                                int j = this.findCellCol(this.eventX);
                                if (b.canAddTile(i, j, this.movingTile)) {
                                    b.addTile(this.movingTile, i, j);
                                    if (!this.boardTileIsMoved) {
                                        tx.addTempRemovedTile(this.movingTile, this.selectedTileNum);
                                        tx.useTile(this.selectedTileNum);
                                    }

//                                    SoundManager.playSound(2, 1.0F, 0);
                                } else {
                                    if (this.boardTileIsMoved) {
                                        b.addTile(this.movingTile, this.selectedBoardTileX, this.selectedBoardTileY);
                                    } else {
                                        tx.addTempRemovedTile(this.movingTile, this.selectedTileNum);
                                    }

//                                    SoundManager.playSound(3, 1.0F, 0);
                                }
                            } else if (this.getArea(this.eventX, this.eventY) == 1) {
                                if (this.boardTileIsMoved) {
                                    tx.addTile(this.movingTile);
//                                    SoundManager.playSound(3, 1.0F, 0);
                                } else if (this.selectedTileNum != -1) {
                                    tx.addTempRemovedTile(this.movingTile, this.selectedTileNum);
//                                    SoundManager.playSound(3, 1.0F, 0);
                                }
                            } else if (this.selectedTileNum != -1) {
                                tx.addTempRemovedTile(this.movingTile, this.selectedTileNum);
//                                SoundManager.playSound(3, 1.0F, 0);
                            } else if (this.selectedBoardTileX != -1 && this.selectedBoardTileY != -1) {
                                b.addTile(this.movingTile, this.selectedBoardTileX, this.selectedBoardTileY);
//                                SoundManager.playSound(3, 1.0F, 0);
                            }

                            Message msg;
                            Bundle bundle;

                            boolean isValid = false;
                            if (this.ge.getBoard().isValidPlacement()) {
                                String word = this.ge.getBoard().getWord();
                                Log.d("CUS", "isValidWord: "+ word);
                                if(word.length() > 0 && this.ge.isValidWord(word)) {
                                    isValid = true;
                                }
                            }

                            if(isValid){
                                msg = this.mHandler.obtainMessage();
                                bundle = new Bundle();
                                bundle.putBoolean("clickable", true);
                                msg.setData(bundle);
                                this.mHandler.sendMessage(msg);
                            } else {
                                msg = this.mHandler.obtainMessage();
                                bundle = new Bundle();
                                bundle.putBoolean("clickable", false);
                                msg.setData(bundle);
                                this.mHandler.sendMessage(msg);
                            }

                            this.undoMovingChanges();
                        } else {
                            this.undoMovingChanges();
                        }
                        break;
                    case 2: // ACTION_MOVE
                        if (Math.abs(this.prevEventX - this.eventX) < this.dims.getCellSize() / 20) {
                            return;
                        }

                        if (Math.abs(this.prevEventY - this.eventY) < this.dims.getCellSize() / 20) {
                            return;
                        }

                        this.stackOpen = false;
                        this.openStack = null;
                        if (this.selectedTileNum != -1) {
                            int turn = this.ge.getPlayerTurn();
                            Tray t = this.ge.getPlayer(turn).getTray();
                            if (!this.tileIsMoved) {
                                this.movingTile = t.temporaryRemoveTile(this.selectedTileNum);
                            }

                            this.tileIsMoved = true;
                            this.movingTileX = this.getMovingTileXPos(this.eventX);
                            this.movingTileY = this.getMovingTileYPos(this.eventY);
                        } else if (this.selectedBoardTileX != -1 && this.selectedBoardTileY != -1) {
                            b = this.ge.getBoard();
                            if (!this.tileIsMoved) {
                                this.movingTile = b.removeTile(this.selectedBoardTileX, this.selectedBoardTileY);
                            }

                            this.tileIsMoved = true;
                            this.boardTileIsMoved = true;
                            this.movingTileX = this.getMovingTileXPos(this.eventX);
                            this.movingTileY = this.getMovingTileYPos(this.eventY);
                        }
                }

                this.prevEventX = this.eventX;
                this.prevEventY = this.eventY;
            }
        }

        private void doDraw(Canvas canvas) {
            if (this.ge != null) {
                //todo: fix its top
                Rect bRect = new Rect(0, this.dims.getScoreHeight(), this.dims.getTotalWidth(), this.dims.getScoreHeight() + this.dims.getBoardheight());
                canvas.drawRect(bRect, this.fillBoardPaint);

                Rect scRect = new Rect(0, 0, this.dims.getTotalWidth(), this.dims.getTotalHeight() - this.dims.getBoardheight() - this.dims.getTrayHeight());
                canvas.drawRect(scRect, this.fillScorePaint);
                Rect tRect = new Rect(0, this.dims.getScoreHeight() + this.dims.getBoardheight(), this.dims.getTotalWidth(), this.dims.getTotalHeight());
                canvas.drawRect(tRect, this.fillTrayPaint);
                canvas.drawRect(scRect, this.fillScorePaint);
                this.drawTray(canvas);
                this.drawBoard(canvas);
                this.drawScore(canvas);
                this.drawMovingTile(canvas);
                if (this.stackOpen) {
                    this.drawStack(canvas, this.topLeftX, this.topLeftY, this.openStack);
                }

            }
        }

        private void drawStack(Canvas canvas, int x, int y, TileStack ts) {
            int size = ts.getSize();
            int height = size * this.dims.getCellSize() + 2 * this.dims.getCellSize();
            int width = this.dims.getCellSize() * 2;
            Rect paneRect = new Rect(x, y, x + width, y + height);
            canvas.drawRect(paneRect, this.stackPanePaint);
            this.stackPanePaint.setStyle(Style.STROKE);
            this.stackPanePaint.setColor(getResources().getColor(R.color.black));
            canvas.drawRect(paneRect, this.stackPanePaint);
            this.stackPanePaint.setStyle(Style.FILL);
            this.stackPanePaint.setColor(getResources().getColor(R.color.bluegrey));
            canvas.drawText("Tile Stack", (float) (x + width / 2), (float) (y + this.dims.getCellSize() / 2), this.stackTextPaint);
            canvas.drawLine((float) x, (float) (y + this.dims.getCellSize() / 2 + 4), (float) (x + width), (float) (y + this.dims.getCellSize() / 2 + 4), this.stackTextPaint);
            Iterator<Tile> iter = ts.getStackIterator();
            int i = 1;
            float txtSize = this.stackTextPaint.getTextSize();
            this.stackTextPaint.setTextAlign(Align.LEFT);
            this.stackTextPaint.setTextSize((float) (2 * this.defaultFontS));

            while (iter.hasNext()) {
                Tile t = iter.next();
                canvas.drawText(Integer.toString(i), (float) (x + this.dims.getCellSize() / 4), (float) (y + i * this.dims.getCellSize() + 5 * this.dims.getCellSize() / 6), this.stackTextPaint);
                this.tileFillPaint.setColor(getResources().getColor(R.color.test));
                this.drawTile(canvas, x + 3 * this.dims.getCellSize() / 4, y + (size + 1 - i) * this.dims.getCellSize(), x + 3 * this.dims.getCellSize() / 4 + this.dims.getCellSize(), y + (size + 1 - i) * this.dims.getCellSize() + this.dims.getCellSize(), Character.toString(t.getLetter()));
                this.tileFillPaint.setColor(getResources().getColor(R.color.white));
                ++i;
            }

            this.stackTextPaint.setTextAlign(Align.CENTER);
            this.stackTextPaint.setTextSize(txtSize);
        }

        private void drawMovingTile(Canvas canvas) {
            if (this.tileIsMoved) {
                this.tileTextPaint.setTextSize((float) (this.defaultFontS * 2));
                this.drawTile(canvas, this.movingTileX - this.dims.getCellSize() / 2, this.movingTileY - this.dims.getCellSize() / 2, this.movingTileX + this.dims.getCellSize() / 2, this.movingTileY + this.dims.getCellSize() / 2, Character.toString(this.movingTile.getLetter()));
                if (this.getArea(this.movingTileX, this.movingTileY) == 2) {
                    int i = this.findCellRow(this.movingTileY);
                    int j = this.findCellCol(this.movingTileX);
                    Board b = this.ge.getBoard();
                    if (b.canAddTile(i, j, this.movingTile)) {
                        this.underneathCellPaint.setColor(getResources().getColor(R.color.yellow));
                    } else {
                        this.underneathCellPaint.setColor(getResources().getColor(R.color.bluegrey));
                    }

                    int left = this.dims.getPadding() + j * this.dims.getCellSize();
                    int right = left + this.dims.getCellSize();
                    int top = this.dims.getScoreHeight() + i * this.dims.getCellSize();
                    int bottom = top + this.dims.getCellSize();
                    Rect underRect = new Rect(left, top, right, bottom);
                    canvas.drawRect(underRect, this.underneathCellPaint);
                }
            }

        }

        private void drawScore(Canvas canvas) {
            if (this.ge != null) {
                int numOfPlayers = this.ge.getNumPlayers();
                int maxHeight = this.dims.getScoreHeight();
                float scoreWidth = (float) (this.dims.getTotalWidth() / numOfPlayers);
                this.scorePaint.setTextAlign(Align.CENTER);

                for (int i = 0; i < numOfPlayers; ++i) {
                    if (i == this.ge.getPlayerTurn()) {
                        this.scorePaint.setTextSize(1.3F * (float) this.defaultFontS);
                    }

                    this.scorePaint.setColor(this.ge.getPlayer(i).getColor());
                    canvas.drawText(this.ge.getPlayer(i).getNickname() + ":" + this.ge.getPlayer(i).getScore(), (float) i * scoreWidth + scoreWidth / 2.0F, (float) (3 * maxHeight / 4), this.scorePaint);
                    this.scorePaint.setTextSize((float) this.defaultFontS);
                }

            }
        }

        private void drawBoard(Canvas canvas) {
            int bHeight = this.dims.getBoardheight();
            int bWidth = bHeight;
            int sHeight = this.dims.getScoreHeight();
            int padding = this.dims.getPadding();
            int left = this.dims.getPadding() + (BoardView.BOARD_SIZE / 2 - 1) * this.dims.getCellSize();
            int top = this.dims.getScoreHeight() + (BoardView.BOARD_SIZE / 2 - 1) * this.dims.getCellSize();
            int bottom = 2 * this.dims.getCellSize() + top;
            int right = 2 * this.dims.getCellSize() + left;
            Rect centralRect = new Rect(left, top, right, bottom);
            canvas.drawRect(centralRect, this.centralSquarePaint);

            int i;
            int y;
            for (i = 0; i <= BOARD_SIZE; ++i) {
                y = i * this.dims.getCellSize();
                canvas.drawLine((float) (padding + y), (float) sHeight, (float) (padding + y), (float) (sHeight + bHeight), this.strokePaint);
            }

            for (i = 0; i <= BOARD_SIZE; ++i) {
                y = i * this.dims.getCellSize();
                canvas.drawLine((float) padding, (float) (sHeight + y), (float) (padding + bWidth), (float) (sHeight + y), this.strokePaint);
            }

            if (this.ge != null) {
                Board b = this.ge.getBoard();
                TileStack[][] ts = b.getTilePlacement();

                for (int ix = 0; ix < ts.length; ++ix) {
                    for (int j = 0; j < ts[ix].length; ++j) {
                        if (b.hasTile(ix, j)) {
                            if (this.selectedBoardTileX == ix && this.selectedBoardTileY == j) {
                                this.tileFillPaint.setColor(getResources().getColor(R.color.yellow));
                            } else if (b.getTile(ix, j).getAge() == b.getTurn()) {
                                this.tileFillPaint.setColor(getResources().getColor(R.color.test));
                            } else {
                                this.tileFillPaint.setColor(getResources().getColor(R.color.white));
                            }

                            left = this.dims.getPadding() + j * this.dims.getCellSize();
                            right = left + this.dims.getCellSize();
                            top = this.dims.getScoreHeight() + ix * this.dims.getCellSize();
                            bottom = top + this.dims.getCellSize();
                            this.tileTextPaint.setTextSize((float) (2 * this.defaultFontS));
                            this.drawTile(canvas, left, top, right, bottom, Character.toString(ts[ix][j].getTop().getLetter()));
                            this.tileFillPaint.setColor(getResources().getColor(R.color.white));
                        }
                    }
                }

            }
        }

        private void drawTray(Canvas canvas) {
            int turn = this.ge.getPlayerTurn();
            Tray t = this.ge.getPlayer(turn).getTray();
            int tileSize = this.dims.getTotalWidth() / 7;
            if (tileSize >= this.dims.getTrayHeight()) {
                tileSize = 4 * this.dims.getTrayHeight() / 5;
            }

            int bot_border = (this.dims.getTrayHeight() - tileSize) / 2;
            int space = (this.dims.getTotalWidth() - tileSize * 7) / 8;
            int reps = t.getNumUnusedTiles();
            this.tilesTray = new Rect[reps];

            for (int i = 0; i < reps; ++i) {
                if (t.getTile(i) != null) {
                    if (this.selectedTileNum == i) {
                        this.tileFillPaint.setColor(getResources().getColor(R.color.yellow));
                    }

                    this.tileTextPaint.setTextSize((float) (this.defaultFontS * 3));
                    this.tilesTray[i] = this.drawTile(canvas, i * tileSize + (i + 1) * space, this.dims.getTotalHeight() - tileSize - bot_border, i * tileSize + (i + 1) * space + tileSize, this.dims.getTotalHeight() - bot_border, t.getTileLetter(i));
                    this.tileFillPaint.setColor(getResources().getColor(R.color.white));
                }
            }

        }

        private Rect drawTile(Canvas canvas, int left, int top, int right, int bottom, String text) {
            Rect tileRect = new Rect(left, top, right, bottom);
            canvas.drawRect(tileRect, this.tileFillPaint);
            canvas.drawRect(tileRect, this.tileStrokePaint);
            canvas.drawText(text, (float) (left + (right - left) / 2), (float) (bottom - (bottom - top) / 5), this.tileTextPaint);
            return tileRect;
        }

        private void undoMovingChanges() {
            this.tileIsMoved = false;
            this.boardTileIsMoved = false;
            this.selectedTileNum = -1;
            this.selectedBoardTileX = -1;
            this.selectedBoardTileY = -1;
        }

        private int getMovingTileXPos(int x) {
            if (x < this.dims.getCellSize() / 2) {
                return this.dims.getCellSize() / 2;
            } else {
                return x > this.dims.getTotalWidth() - this.dims.getCellSize() / 2 ? this.dims.getTotalWidth() - this.dims.getCellSize() / 2 : x;
            }
        }

        private int getMovingTileYPos(int y) {
            if (y < this.dims.getCellSize() / 2) {
                return this.dims.getCellSize() / 2;
            } else {
                return y > this.dims.getTotalHeight() - this.dims.getCellSize() / 2 ? this.dims.getTotalHeight() - this.dims.getCellSize() / 2 : y;
            }
        }

        private void handleBoardClick(int x, int y) {
            Board b = this.ge.getBoard();
            int turn = b.getTurn();
            int i = this.findCellRow(y);
            int j = this.findCellCol(x);
            Tile t = b.getTile(i, j);
            if (t != null) {
                this.stackOpen = true;
                this.openStack = this.ge.getBoard().getTilePlacement()[i][j];
                this.calculateStackCoords(x, y);
                if (t.getAge() == turn) {
                    this.selectedBoardTileX = i;
                    this.selectedBoardTileY = j;
                }
            }

        }

        private void handleTrayClick(int x, int y) {
            for (int i = 0; i < this.tilesTray.length; ++i) {
                if (this.tilesTray[i].contains(x, y)) {
                    this.selectedTileNum = i;
                }
            }

        }

        private void calculateStackCoords(int x, int y) {
            int size = this.openStack.getSize();
            int height = size * this.dims.getCellSize() + 2 * this.dims.getCellSize();
            if (y + height <= this.dims.getBoardheight() + this.dims.getScoreHeight()) {
                this.topLeftY = y;
            } else {
                this.topLeftY = this.dims.getBoardheight() + this.dims.getScoreHeight() - height;
            }

            if (x >= this.dims.getTotalWidth() - x) {
                this.topLeftX = x - this.dims.getCellSize() * 3;
            } else {
                this.topLeftX = x + this.dims.getCellSize();
            }

        }

        private int getArea(int x, int y) {
            if (y <= BoardView.this.getDimensions().getScoreHeight()) {
                return 3;
            } else if(y > BoardView.this.getDimensions().getScoreHeight()
                    && y <= BoardView.this.getDimensions().getScoreHeight() + BoardView.this.getDimensions().getBoardheight()) {
                return 2;
            } else {
                return 1;
            }
        }

        private int findCellRow(int y) {
            for (int i = 0; i < BoardView.BOARD_SIZE; ++i) {
                if (y >= this.dims.getScoreHeight() + i * this.dims.getCellSize() && y <= this.dims.getScoreHeight() + i * this.dims.getCellSize() + this.dims.getCellSize()) {
                    return i;
                }
            }

            return 0;
        }

        private int findCellCol(int x) {
            for (int i = 0; i < BoardView.BOARD_SIZE; ++i) {
                if (x >= this.dims.getPadding() + i * this.dims.getCellSize() && x <= this.dims.getPadding() + i * this.dims.getCellSize() + this.dims.getCellSize()) {
                    return i;
                }
            }

            return 0;
        }

        private void paintInitialisation() {
            this.fillScorePaint = new Paint(1);
            this.fillScorePaint.setColor(getResources().getColor(R.color.white));
            this.fillTrayPaint = new Paint();
            this.fillTrayPaint.setColor(getResources().getColor(R.color.tray_bg));
            this.fillBoardPaint = new Paint();
            this.fillBoardPaint.setColor(getResources().getColor(R.color.board_bg));
            this.strokePaint = new Paint();
            this.strokePaint.setStyle(Style.STROKE);
            this.strokePaint.setColor(getResources().getColor(R.color.black));
            this.tileStrokePaint = new Paint();
            this.tileStrokePaint.setStyle(Style.STROKE);
            this.tileStrokePaint.setColor(getResources().getColor(R.color.black));
            this.tileFillPaint = new Paint();
            this.tileFillPaint.setStyle(Style.FILL);
            this.tileFillPaint.setColor(getResources().getColor(R.color.white));
            this.tileTextPaint = new Paint(1);
            this.tileTextPaint.setColor(getResources().getColor(R.color.black));
            this.tileTextPaint.setStyle(Style.STROKE);
            this.tileTextPaint.setTextAlign(Align.CENTER);
            this.stackPanePaint = new Paint();
            this.stackPanePaint.setStyle(Style.FILL);
            this.stackPanePaint.setColor(getResources().getColor(R.color.bluegrey));
            this.stackTextPaint = new Paint(1);
            this.stackTextPaint.setColor(getResources().getColor(R.color.black));
            this.stackTextPaint.setTextAlign(Align.CENTER);
            this.stackTextPaint.setStyle(Style.STROKE);
            this.selectedTilePaint = new Paint();
            this.selectedTilePaint.setStyle(Style.FILL);
            this.selectedTilePaint.setColor(getResources().getColor(R.color.bluegrey));
            this.underneathCellPaint = new Paint();
            this.underneathCellPaint.setStyle(Style.STROKE);
            this.underneathCellPaint.setColor(getResources().getColor(R.color.bluegrey));
            this.centralSquarePaint = new Paint();
            this.centralSquarePaint.setColor(getResources().getColor(R.color.colorAccent));
            this.scorePaint = new Paint(1);
            float curWidth = this.tileStrokePaint.getStrokeWidth();
            curWidth *= 2.0F;
            if (curWidth < 2.0F) {
                curWidth = 2.0F;
            }

            this.tileStrokePaint.setStrokeWidth(curWidth);
        }
    }
}
