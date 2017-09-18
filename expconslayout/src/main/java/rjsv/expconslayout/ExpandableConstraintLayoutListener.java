package rjsv.expconslayout;

import rjsv.expconslayout.enumerators.ExpandableConstraintLayoutStatus;

/**
 * @author <a href="mailto:rvfts@hotmail.com"/>
 * @version $Revision : 1 $
 */

public interface ExpandableConstraintLayoutListener {
    /**
     * Notifies the start of the animation.
     * Sync from android.animation.Animator.AnimatorListener.onAnimationStart(Animator animation)
     */
    void onAnimationStart(ExpandableConstraintLayoutStatus status);

    /**
     * Notifies the end of the animation.
     * Sync from android.animation.Animator.AnimatorListener.onAnimationEnd(Animator animation)
     */
    void onAnimationEnd(ExpandableConstraintLayoutStatus status);

    /**
     * Notifies the layout is going to open.
     */
    void onPreOpen();

    /**
     * Notifies the layout is going to equal close size.
     */
    void onPreClose();

    /**
     * Notifies the layout opened.
     */
    void onOpened();

    /**
     * Notifies the layout size equal closed size.
     */
    void onClosed();

}