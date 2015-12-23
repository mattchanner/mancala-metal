package com.axolotl.mancala.ui;

/**
 * A listener interface for animation complete notifications
 * @author matt
 *
 */
public interface OnAnimationCompleteListener {

	/**
	 * Raised when an animation has completed
	 */
	void onAnimationComplete();
	
	/**
	 * Raised when an animation has begun
	 */
	void onAnimationStart();
	
}
