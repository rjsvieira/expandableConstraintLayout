package rjsv.expconslayout

import rjsv.expconslayout.enumerators.ExpandableConstraintLayoutStatus

/**
 * @author [](mailto:rvfts@hotmail.com)
 * @version $Revision : 1 $
 */

interface ExpandableConstraintLayoutListener {
    /**
     * Notifies the start of the animation.
     * Sync from android.animation.Animator.AnimatorListener.onAnimationStart(Animator animation)
     */
    fun onAnimationStart(status: ExpandableConstraintLayoutStatus)

    /**
     * Notifies the end of the animation.
     * Sync from android.animation.Animator.AnimatorListener.onAnimationEnd(Animator animation)
     */
    fun onAnimationEnd(status: ExpandableConstraintLayoutStatus)

    /**
     * Notifies the layout is going to open.
     */
    fun onPreOpen()

    /**
     * Notifies the layout is going to equal close size.
     */
    fun onPreClose()

    /**
     * Notifies the layout opened.
     */
    fun onOpened()

    /**
     * Notifies the layout size equal closed size.
     */
    fun onClosed()

}