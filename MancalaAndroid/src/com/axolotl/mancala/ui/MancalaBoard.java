package com.axolotl.mancala.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.axolotl.mancala.R;
import com.axolotl.mancala.game.Game;
import com.axolotl.mancala.game.GameMode;
import com.axolotl.mancala.game.PlayerNumber;
import com.axolotl.mancala.model.Board;
import com.axolotl.mancala.model.Pit;
import com.axolotl.mancala.model.Store;

/**
 * Represents the mancala board
 * @author matt
 */
public class MancalaBoard extends View {

	// a handler for delay events
	private final Handler mHandler = new Handler();
	
	// The painters associated to player one
	private PitPainters mPlayerOnePainters;

	// The painters associated to player two
	private PitPainters mPlayerTwoPainters;
	
	// The painters associated to the computer player
	private PitPainters mComputerPainters;
	
	// The painter for the board background
	private Paint mBackgroundPaint;
	
	// A reference to the game
	private Game mGame;
	
	// A reference to the board
	private Board mBoard;
	
	// A map of hollows, to their associated ui counterpart
	private Map<Pit, PitView> mPitMap;
	
	// The list of highlighted hollows
	private List<Pit> mHighlightedPits;
	
	// The queued highlighter operations
	private List<List<Pit>> mQueuedHighlighters = new ArrayList<List<Pit>>();
	
	// The index of the current pit to highlight
	private int mPitToHighlight = -1;
	
	// Animation flag
	private boolean mIsAnimating;
	
	// A listener to notify when an animation completes
	private OnAnimationCompleteListener mAnimationCompleteListener; 
	
	// The current player
	private PlayerNumber mCurrentPlayer;

	/**
	 * Creates a new instance of the view
	 * 
	 * @param context
	 * 		  The current context
	 */
	public MancalaBoard(Context context) {
		super(context);
		initView();		
	}

	/**
	 * Creates a new instance of the view
	 * 
	 * @param context
	 * 		  The current context
	 * 
	 * @param attr
	 * 		  Attributes passed in from the current layout
	 */
	public MancalaBoard(Context context, AttributeSet attr) {
		super(context, attr);
		initView();
	}
	
	/**
	 * Sets the OnAnimationCompleteListener reference
	 * @param listener The listener to invoke
	 */
	public void setOnAnimationCompleteListener(OnAnimationCompleteListener listener) {
		mAnimationCompleteListener = listener;
	}

	/**
	 * Determines whether an animation is currently occurring
	 * @return
	 */
	public boolean isAnimating() {
		return mIsAnimating;
	}
	
	/**
	 * Sets the game reference for this board
	 * @param game
	 */
	public void setGame(Game game) {

		// Did we already have a game in play?
		boolean hasLaidOut = mBoard != null;
		
		// Store board and game references
		mBoard = game.getBoard();
		mGame = game;

		// If the view has already been laid out, we can position the components now using
		// the current width and height
		if (hasLaidOut)
			layoutBoard(getWidth(), getHeight());

		invalidate();
	}
	
	/**
	 * Gets the current player
	 * @return
	 */
	public PlayerNumber getCurrentPlayer() {
		return mCurrentPlayer;
	}
	
	/**
	 * Sets the current player
	 * @param player
	 */
	public void setCurrentPlayer(PlayerNumber player) {
		mCurrentPlayer = player;
	}
	
	/**
	 * Sets highlighting on the given hollows
	 * 
	 * @param changedPits
	 * 		  The hollows to highlight
	 * 
	 * @param highlightDelay
	 * 		  The amount of time to retain the highlighting for
	 */
	public void highlightPits(List<Pit> changedPits, int highlightDelay) {
		
		if (mIsAnimating) {
			mQueuedHighlighters.add(new ArrayList<Pit>(changedPits));
		} else {
			mHighlightedPits.clear();
			mPitToHighlight = -1;
			mHighlightedPits.addAll(changedPits);
			
			mHandler.removeCallbacks(mAnimateMove);
			mHandler.postDelayed(mAnimateMove, 300);
		}				
	}

	/**
	 * Handles the touch event to play a move on the board
	 * 
	 * @param event 
	 * 			The motion event
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		try {
			
			if (mIsAnimating) {
				return true;
			}
			
			if (event.getAction() != MotionEvent.ACTION_DOWN) {
                return true;
            }
			
			if (mAnimationCompleteListener != null) {
				mAnimationCompleteListener.onAnimationStart();
			}
			
			float x = event.getX();
			float y = event.getY();
	
			// Test each hollow to see if we have a valid, playable hit
			for (PitView h : mPitMap.values()) {
				
				if (h.getRect().contains(x, y) && h.canPlay()) {
					
					// found a valid hollow, so play it
					mGame.getStrategy().makeMove(h.getPit());
					return true;
				}
			}
	
			return super.onTouchEvent(event);
		}
		catch (Exception ex) {
			Log.e("MancalaBoard", ex.toString());
			return super.onTouchEvent(event);
		}
	}

	/**
	 * Handles the laying out of this view
	 * 
	 * @param changed Indicates whether the size has changed
	 * 
	 * @param left The left position of the view
	 * 
	 * @param top The top position of the view
	 * 
	 * @param right The right position of the view
	 * 
	 * @param bottom The bottom position of the view
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		layoutBoard(right - left, bottom - top);
	}

	/**
	 * Handles the size changed event to resize the board
	 * 
	 * @param w The width
	 * 
	 * @param h The height
	 * 
	 * @param oldw The previous width
	 * 
	 * @param oldh The previous height
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		layoutBoard(w, h);
	}

	/**
	 * Handles the measuring of this view based on the current board state
	 * 
	 * @param widthMeasureSpec The width given by the parent
	 * 
	 * @param heightMeasureSpec The height given by the parent
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		if (mBoard != null) {

			int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		    int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		    
		    float fudgeFactor = parentWidth > parentHeight ? 0.7f :1.0f;
		    this.setMeasuredDimension(parentWidth, (int)(parentHeight * fudgeFactor));
		}
	}

	/**
	 * The main draw method for the view, delegating the actual hollow drawing to the PitView
	 * 
	 * @param canvas The canvas to draw into
	 */
	@Override
	protected void onDraw(Canvas canvas) {

		canvas.drawRect(this.getLeft(), this.getTop(), this.getRight(), this.getBottom(), mBackgroundPaint);

		if (mBoard == null) {
			return;
		}
		for (PitView h : mPitMap.values()) {
			h.draw(canvas);
		}
	}
	
	private boolean isLandscapeMode() {
		WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getOrientation() == 1;
	}
	
	/**
	 * Lays out the hollows on the board based on the given board size
	 * 
	 * @param width
	 *            The board width
	 * @param height
	 *            The board height
	 */
	private void layoutBoard(int width, int height) {

		// Initialise the hollow / view map
		mPitMap.clear();
		
		for (Pit pit : mBoard.getPits()) {
			PitView view = new PitView(pit);
			mPitMap.put(pit, view);
		}
		
		Store playerOneStore = mBoard.getPlayersStore(PlayerNumber.One);
		Store playerTwoStore = mBoard.getPlayersStore(PlayerNumber.Two);

		List<Pit> playerOnePits = mBoard.getPlayersPits(PlayerNumber.One);

		if (isLandscapeMode()) {
			layoutBoardInLandscapeMode(width, 
					height, 
					playerOneStore, 
					playerTwoStore, 
					playerOnePits);
		} else {
			layoutBoardInPortraitMode(width, 
					height, 
					playerOneStore, 
					playerTwoStore, 
					playerOnePits);
		}
		
		
	}
	
	/**
	 * Lays the board out with the assumption that the phone is in landscape mode
	 * 
	 * @param width The view width
	 * 
	 * @param height The view height
	 * 
	 * @param playerOneStore Player 1's store
	 * 
	 * @param playerTwoStore Player 2's store
	 * 
	 * @param playerOnePits Player 1's pits
	 */
	private void layoutBoardInPortraitMode(int width, 
			int height,
			Store playerOneStore, 
			Store playerTwoStore, 
			List<Pit> playerOnePits) {
		
		final float hollowWidth = width / 2;
		final float hollowHeight = height /  (mBoard.getNumberOfHollowsPerPlayer() + 2);
	
		final float playerOneLeft = 0f;
		final float playerTwoLeft = hollowWidth;
		
		float currentY = hollowHeight;
	
		for (Pit playerOnePit : playerOnePits) {

			PitView playerOneView = mPitMap.get(playerOnePit);
			
			if (playerOneView == null) {
				continue;
			}
			Pit playerTwoPit = mBoard.getAdjacentPit(playerOnePit);
			
			if (playerTwoPit == null) {
				continue;
			}

			PitView playerTwoView = mPitMap.get(playerTwoPit);
			
			if (playerTwoView == null) {
				continue;
			}

			playerOneView.setDimensions(playerOneLeft, currentY, hollowWidth, hollowHeight);
			playerTwoView.setDimensions(playerTwoLeft, currentY, hollowWidth, hollowHeight);
			
			currentY += hollowHeight;
		}
		
		PitView playerOneViewStore = mPitMap.get(playerOneStore);
		PitView playerTwoViewStore = mPitMap.get(playerTwoStore);

		playerTwoViewStore.setDimensions(0f, 0f, width, hollowHeight);
		playerOneViewStore.setDimensions(0f, currentY, width, hollowHeight);
		
	}

	/**
	 * Lays the board out with the assumption that the phone is in landscape mode
	 * 
	 * @param width The view width
	 * 
	 * @param height The view height
	 * 
	 * @param playerOneStore Player 1's store
	 * 
	 * @param playerTwoStore Player 2's store
	 * 
	 * @param playerOnePits Player 1's pits
	 */
	private void layoutBoardInLandscapeMode(int width, 
			int height,
			Store playerOneStore, 
			Store playerTwoStore, 
			List<Pit> playerOnePits) {
		
		final float hollowWidth = width	/ (mBoard.getNumberOfHollowsPerPlayer() + 2);
		final float hollowHeight = height / 2;

		final float playerTwoTop = 0f;
		final float playerOneTop = hollowHeight;

		float currentX = hollowWidth;

		for (Pit playerOnePit : playerOnePits) {

			PitView playerOneView = mPitMap.get(playerOnePit);
			
			if (playerOneView == null) {
				continue;
			}
			Pit playerTwoPit = mBoard.getAdjacentPit(playerOnePit);
			
			if (playerTwoPit == null) {
				continue;
			}

			PitView playerTwoView = mPitMap.get(playerTwoPit);
			
			if (playerTwoView == null) {
				continue;
			}

			playerTwoView.setDimensions(currentX, playerTwoTop, hollowWidth, hollowHeight);
			playerOneView.setDimensions(currentX, playerOneTop, hollowWidth, hollowHeight);

			currentX += hollowWidth;
		}

		PitView playerOneViewStore = mPitMap.get(playerOneStore);
		PitView playerTwoViewStore = mPitMap.get(playerTwoStore);

		playerTwoViewStore.setDimensions(0, 0f, hollowWidth, height);
		playerOneViewStore.setDimensions(currentX, 0f, hollowWidth, height);
	}

	/**
	 * Initialises the resources and painters for this view
	 */
	private void initView() {

		mHighlightedPits = new ArrayList<Pit>();
				
		int boardBackgroundColour = getContext().getResources().getColor(R.color.board_background_colour);		
		mBackgroundPaint = new Paint();
		mBackgroundPaint.setColor(boardBackgroundColour);
		
		// Set up the pit painter instance for each of the possible player types
		mPlayerOnePainters = new PitPainters(R.color.player_1_pit_text_colour, 
		        R.color.hollow_background_colour_player_one, 
		        R.color.hollow_background_colour_highlight_player_one, 
		        R.color.hollow_border_colour_player_one,
		        R.color.hollow_disabled_background_colour_highlight_player_one,
		        R.color.player_1_disabled_text_colour, 
		        R.color.hollow_disabled_border_colour_player_one);
		
		mPlayerTwoPainters = new PitPainters(R.color.player_2_pit_text_colour, 
                R.color.hollow_background_colour_player_two, 
                R.color.hollow_background_colour_highlight_player_two, 
                R.color.hollow_border_colour_player_two,
                R.color.hollow_disabled_background_colour_highlight_player_two,
                R.color.player_2_disabled_text_colour, 
                R.color.hollow_disabled_border_colour_player_two);
		
		mComputerPainters = new PitPainters(R.color.computer_pit_text_colour, 
                R.color.hollow_background_colour_player_computer, 
                R.color.hollow_background_colour_highlight_player_computer, 
                R.color.hollow_border_colour_player_computer,
                R.color.hollow_disabled_background_colour_highlight_player_computer,
                R.color.computer_disabled_text_colour, 
                R.color.hollow_disabled_border_colour_player_computer);
		
		mPitMap = new HashMap<Pit, PitView>();
	}

	// call back to clear highlighting
	private Runnable mAnimateMove = new Runnable() {		
		public void run() {
			
			mPitToHighlight++;
			mIsAnimating = true;			
			mHandler.removeCallbacks(mAnimateMove);			
			if (mPitToHighlight >= mHighlightedPits.size()) {	
				
				mHighlightedPits.clear();
				mPitToHighlight = -1;
				mIsAnimating = false;
				
				if (mQueuedHighlighters.size() > 0) {
					
					mHighlightedPits = mQueuedHighlighters.remove(0);
					mHandler.postDelayed(mAnimateMove, 400);
					
				} else if (mAnimationCompleteListener != null) {
					mAnimationCompleteListener.onAnimationComplete();
				}
				
			} else {
				
				mHandler.postDelayed(mAnimateMove, 400);
			}
			invalidate();			
		}
	};	
	
	/**
	 * A lightweight class used for rendering a hollow into the view.
	 * This class uses it's parent painters to avoid unnecessary construction of painters per hollow
	 */
	private class PitView {

		// The hollow that this view represents
		private final Pit mPit;
		
		// The rectangle that this hollow is positioned in
		private RectF mRect;

		/**
		 * Constructs a new instance of the pit view
		 * 
		 * @param pit
		 * 		  The hollow that this view represents
		 */
		public PitView(Pit pit) {
			mPit = pit;
		}

		/**
		 * Gets a reference to the pit
		 * @return The hollow that this view represents
		 */
		public Pit getPit() {
			return mPit;
		}

		/**
		 * Sets the dimensions of this pit
		 * 
		 * @param left 
		 * 		  The left position
		 * 
		 * @param top
		 * 		  The right position
		 * 
		 * @param width
		 * 		  The hollow width
		 * 
		 * @param height
		 * 		  The hollow height
		 */
		public void setDimensions(float left, float top, float width, float height) {
			mRect = new RectF(left + 1, top + 1, (left + width) - 2, (top + height) - 2);
		}

		/**
		 * Gets the rectangle for this pit view
		 * 
		 * @return The rectangle
		 */
		public RectF getRect() {
			return mRect;
		}

		/**
		 * Determines whether this hollow is currently playable based this pit is associated
		 * to the current player, and has at least one marble positioned inside it.  The pit
		 * must also not be a Store type either.
		 * 
		 * @return True if this pit is playable
		 */
		public boolean canPlay() {
			
			// TODO: This code currently assumes that only player 1 is playable.
			
			return !(mPit instanceof Store) &&
					(mPit.getNumberOfMarbles() > 0) && 
					(mGame.getStrategy().getCurrentPlayer() == mPit.getPlayerNumber());
		}

		/**
		 * The main drawing code for this pit view
		 * 
		 * @param canvas
		 * 		  The canvas to draw into.
		 */
		public void draw(Canvas canvas) {

			// Null check just in case..
			if (mRect == null || mPit == null) {
				return;
			}
			
			final float Radius = 10.0f;
			
			Paint textPaint = getTextPainter();
			
			try {
				
				Paint back = getBackgroundPaint();
				Paint border = getBorderPaint();
	
				// Fill the outer rectangle - this acts as a border until I can work out how better to do this
				// or possibly to use a bitmap for the background
				canvas.drawRoundRect(mRect, Radius, Radius, border);
				
				// Deflate the rectangle and draw the inner round rectangle
				canvas.drawRoundRect(new RectF(mRect.left + 2, mRect.top + 2, mRect.right - 2, mRect.bottom - 2), Radius, Radius, back);
	
				// Create a string to indicate the number of marbles in this pit
				String marbleText = getPitViewString();
	
				// Measure the string, and center inside the pit
				FontMetrics metrics = textPaint.getFontMetrics();
	
				float height = metrics.top + metrics.bottom;
				float width = textPaint.measureText(marbleText) / 2f;
				float textLeft = mRect.centerX() - (width);
				float textTop = mRect.centerY() - (height / 2f);
				
				canvas.drawText(marbleText, textLeft + width, textTop, textPaint);
				
			} catch (Exception e) {
				Log.e("MancalaBoard", e.toString());
			}
		}

		/**
		 * Returns the string to draw into the pit.  If the curif (mPit instanceof Store &amp;&amp; mIsAnimating) {
		 *		return "" + mPit.getPreviousMarbleCount();
		 *	}rent pit is
		 * in the list of pits to highlight, but the indexer for the highlighter sequence
		 * has not yet reached the pit, the previous marble count will be returned
		 * instead of the current.
		 * @return
		 */
		private String getPitViewString() {
		
			boolean showPreviousMarbleCount = false;
			
			if (mHighlightedPits.size() > 0) {
				for (int index = mPitToHighlight + 1; index < mHighlightedPits.size(); index++) {
					if (mHighlightedPits.get(index) == mPit) {
						showPreviousMarbleCount = true;
						break;
					}
				}
			}
			return showPreviousMarbleCount ? "" + mPit.getPreviousMarbleCount() : "" + mPit.getNumberOfMarbles();
		}
		
		/**
		 * Returns the painter to use for the text of this view
		 * 
		 * @return
		 */
		private Paint getTextPainter() {
		    
		    PitPainters painters = getPainters();
		    
		    return mPit.getPlayerNumber() != getCurrentPlayer() ? 
                    painters.getDisabledTextPainter() :
                        painters.getTextPainter();
		}

		/**
		 * Determines the paint to use for this view based on the player
		 * 
		 * @return The paint to use
		 */
		private Paint getBorderPaint() {
			
		    PitPainters painter = getPainters();
			
		    return mPit.getPlayerNumber() != getCurrentPlayer() ? 
		            painter.getDisabledBorderPainter() :
		                painter.getBorderPainter();
		}
		
		/**
         * Determines the background painter for this pit based on the current game state
         * 
         * @return The painter to use for the background
         */
        private Paint getBackgroundPaint() {
            
            PitPainters painters = getPainters();
            
            if (mPitToHighlight >= 0 && mHighlightedPits.get(mPitToHighlight) == mPit) {
                
                return painters.getBackgroundHighlightPainter();        
                
            } else {
                
                return mPit.getPlayerNumber() != getCurrentPlayer() ? 
                        painters.getDisabledBackgroundPainter() :
                            painters.getBackgroundPainter();                
            }
        }
        
		/**
		 * Returns the pit painter for the current pit
		 * @return
		 */
		private PitPainters getPainters() {
	        switch (mPit.getPlayerNumber()) {            
            case One:
            default:
                return mPlayerOnePainters;        
            
            case Two:
                return mGame.getGameMode() == GameMode.TwoPlayer ? mPlayerTwoPainters : mComputerPainters;
            }       
		}		
	}
	
	/**
	 * Class to represent the different paint types associated with drawing a single pit
	 * 
	 */
	class PitPainters {
	   
	    // The paint used for drawing text
	    private final Paint mTextPainter;
	    
	    // The paint used for drawing text
        private final Paint mDisabledTextPainter;
	    
	    // The paint used for drawing the background
	    private final Paint mBackgroundPainter;
	    
	    // The paint used for drawing ther background when in it's highlighted state
	    private final Paint mBackgroundHighlightPainter;
	    
	    // The paint used for drawing ther background when in it's disabled state
	    private final Paint mBackgroundDisabledPainter;
	    
	    // The paint used for drawing a border
	    private final Paint mBorderPainter;
	    
	    // The paint used for drawing a border when in a disabled state
	    private final Paint mDisabledBorderPainter;
	    
	    
	    public PitPainters(int textPainterId, 
                int backgroundPainterId, 
                int backgroundHighlightPainterId, 
                int borderPainterId,
                int disabledBackgroundPainterId,
                int disabledTextPainterId,
                int disabledBorderPainterId) {
            
            mTextPainter = createTextPaint(textPainterId);
            mDisabledTextPainter = createTextPaint(disabledTextPainterId);
            
            mBackgroundPainter = createPaint(backgroundPainterId);
            mBackgroundHighlightPainter = createPaint(backgroundHighlightPainterId);
            mBackgroundDisabledPainter = createPaint(disabledBackgroundPainterId);
            
            mBorderPainter = createPaint(borderPainterId);
            mDisabledBorderPainter = createPaint(disabledBorderPainterId);
            
            DashPathEffect dashedEffect = new DashPathEffect(new float[] {5, 5}, 1);
            mDisabledBorderPainter.setPathEffect(dashedEffect);
            mDisabledBorderPainter.setStyle(Style.STROKE);
            mDisabledBorderPainter.setStrokeWidth(3);
        }
	    
	    /**
         * Gets the text painter
         * @return The painter for this aspect of the pit view
         */
	    public Paint getTextPainter() {
	        return mTextPainter;
	    }
	    
	    /**
         * Gets the disabled text painter
         * @return The painter for this aspect of the pit view
         */
	    public Paint getDisabledTextPainter() {
	        return mDisabledTextPainter;
	    }
	    
	    /**
         * Gets the background painter
         * @return The painter for this aspect of the pit view
         */
	    public Paint getBackgroundPainter() {
	        return mBackgroundPainter;
	    }
	    
	    /**
         * Gets the background highlight painter
         * @return The painter for this aspect of the pit view
         */
	    public Paint getBackgroundHighlightPainter() {
	        return mBackgroundHighlightPainter;
	    }
	    
	    /**
         * Gets the disabled background painter
         * @return The painter for this aspect of the pit view
         */
        public Paint getDisabledBackgroundPainter() {
            return mBackgroundDisabledPainter;
        }
	    
	    /**
         * Gets the border painter
         * @return The painter for this aspect of the pit view
         */
	    public Paint getBorderPainter() {
	        return mBorderPainter;
	    }
	    
	    /**
	     * Gets the disabled border painter
	     * @return The painter for this aspect of the pit view
	     */
	    public Paint getDisabledBorderPainter() {
	        return mDisabledBorderPainter;
	    }
	    	    
	    /**
	     * Simple paint factory for initialising a paint instance with anti aliasing and the given colour
	     * 
	     * @param colour 
	     *      The colour to set on the paint instance
	     * 
	     * @return The created paint instance
	     */
	    private Paint createPaint(int colour) {
	        
	        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	        paint.setColor(getResources().getColor(colour));
	        return paint;
	        
	    }
	    
	    /**
	     * Creates a text painter based on the given colour identifier
	     * @param colour The colour id
	     * @return The text painter to use
	     */
	    private Paint createTextPaint(int colour) {
	        Paint textPainter = createPaint(colour);
	        textPainter.setTextAlign(Align.CENTER);
	        textPainter.setTextSize(20);
	        return textPainter;
	    }
	}
}
