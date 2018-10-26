package rjsv.expconslayout

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.util.AttributeSet
import android.view.View
import rjsv.expconslayout.enumerators.ExpandableConstraintLayoutListenerStatus
import rjsv.expconslayout.enumerators.ExpandableConstraintLayoutListenerStatus.*
import rjsv.expconslayout.enumerators.ExpandableConstraintLayoutStatus.*

/**
 * Description
 * The type Expandable linear layout.
 *
 * @author [](mailto:rvfts@hotmail.com)
 * @version $Revision : 1 $
 */

class ExpandableConstraintLayout constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    var animationDuration: Int = 0
    private val isVertical: Boolean
    private var expansion: Float = 0.toFloat()
    private val displacement: Float
    var interpolator: TimeInterpolator? = null
    private var animationListener: ExpandableConstraintLayoutListener? = null
    private var valueAnimator: ValueAnimator? = null
    private var currentStatus = IDLE

    val isExpanded: Boolean
        get() = expansion == 1f

    init {
        // default values
        animationDuration = 200
        interpolator = FastOutSlowInInterpolator()
        isVertical = true
        displacement = 1f
        expansion = 1f
    }

    // Overridden Methods
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight
        val size = if (isVertical) height else width
        visibility = if (expansion == 0f && size == 0) View.INVISIBLE else View.VISIBLE
        val expansionDelta = size - Math.round(size * expansion)
        // translate all children before measuring the parent
        if (displacement > 0) {
            val displacementDelta = expansionDelta * displacement
            for (i in 0 until childCount) {
                if (isVertical) {
                    getChildAt(i).translationY = -displacementDelta
                } else {
                    val direction = -1
                    getChildAt(i).translationX = direction * displacementDelta
                }
            }
        }
        if (isVertical) {
            setMeasuredDimension(width, height - expansionDelta)
        } else {
            setMeasuredDimension(width - expansionDelta, height)
        }
    }

    // General Methods
    fun toggle() {
        if (isExpanded) {
            collapse()
        } else {
            expand()
        }
    }

    fun expand() {
        shouldExpand(true, true)
    }

    fun collapse() {
        shouldExpand(false, true)
    }

    private fun shouldExpand(shouldExpand: Boolean, shouldAnimate: Boolean) {
        if (shouldExpand && (currentStatus == EXPANDING || expansion == 1f)) {
            return
        }
        if (!shouldExpand && (currentStatus == COLLAPSING || expansion == 0f)) {
            return
        }
        val newExpansion = if (shouldExpand) 1F else 0F
        if (shouldAnimate) {
            animateExpansion(newExpansion)
        } else {
            setExpansion(newExpansion)
        }
    }

    private fun setExpansion(newExpansion: Float) {
        // Nothing to do here
        if (this.expansion == newExpansion) {
            return
        }
        this.expansion = newExpansion
        visibility = if (expansion == 0f) View.INVISIBLE else View.VISIBLE
        requestLayout()
    }

    private fun animateExpansion(newExpansion: Float) {
        if (valueAnimator != null) {
            valueAnimator?.cancel()
            valueAnimator = null
        }
        valueAnimator = ValueAnimator.ofFloat(expansion, newExpansion)
        valueAnimator?.interpolator = interpolator
        valueAnimator?.duration = animationDuration.toLong()
        valueAnimator?.addUpdateListener { valueAnimator -> setExpansion(valueAnimator.animatedValue as Float) }
        valueAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                reportListenerStatus(AnimationStart)
                currentStatus = if (newExpansion <= 0) {
                    reportListenerStatus(PreClose)
                    COLLAPSING
                } else {
                    reportListenerStatus(PreOpen)
                    EXPANDING
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                reportListenerStatus(AnimationEnd)
                if (currentStatus == EXPANDING) {
                    reportListenerStatus(Opened)
                } else {
                    reportListenerStatus(Closed)
                }
                currentStatus = IDLE
            }

            override fun onAnimationCancel(animation: Animator) {
                currentStatus = IDLE
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
        valueAnimator?.start()
    }

    // Listener Related
    fun setAnimationListener(listener: ExpandableConstraintLayoutListener) {
        this.animationListener = listener
    }

    fun reportListenerStatus(status: ExpandableConstraintLayoutListenerStatus?) {
        if (status != null && animationListener != null) {
            when (status) {
                PreOpen -> animationListener!!.onPreOpen()
                PreClose -> animationListener!!.onPreClose()
                Opened -> animationListener!!.onOpened()
                Closed -> animationListener!!.onClosed()
                AnimationStart -> animationListener!!.onAnimationStart(currentStatus)
                AnimationEnd -> animationListener!!.onAnimationEnd(currentStatus)
                else -> {
                }
            }
        }
    }

}// Constructors
