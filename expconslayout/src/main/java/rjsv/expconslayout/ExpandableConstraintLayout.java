package rjsv.expconslayout;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;

import rjsv.expconslayout.enumerators.ExpandableConstraintLayoutListenerStatus;
import rjsv.expconslayout.enumerators.ExpandableConstraintLayoutStatus;

import static rjsv.expconslayout.enumerators.ExpandableConstraintLayoutListenerStatus.AnimationEnd;
import static rjsv.expconslayout.enumerators.ExpandableConstraintLayoutListenerStatus.AnimationStart;
import static rjsv.expconslayout.enumerators.ExpandableConstraintLayoutListenerStatus.Closed;
import static rjsv.expconslayout.enumerators.ExpandableConstraintLayoutListenerStatus.Opened;
import static rjsv.expconslayout.enumerators.ExpandableConstraintLayoutListenerStatus.PreClose;
import static rjsv.expconslayout.enumerators.ExpandableConstraintLayoutListenerStatus.PreOpen;
import static rjsv.expconslayout.enumerators.ExpandableConstraintLayoutStatus.COLLAPSING;
import static rjsv.expconslayout.enumerators.ExpandableConstraintLayoutStatus.EXPANDING;
import static rjsv.expconslayout.enumerators.ExpandableConstraintLayoutStatus.IDLE;

/**
 * Description
 * The type Expandable linear layout.
 *
 * @author <a href="mailto:rvfts@hotmail.com"/>
 * @version $Revision : 1 $
 */

@SuppressWarnings("ResourceType")
public class ExpandableConstraintLayout extends ConstraintLayout {

    private int animationDuration;
    private boolean isVertical;
    private float expansion;
    private float displacement;
    private TimeInterpolator interpolator;
    private ExpandableConstraintLayoutListener animationListener;
    private ValueAnimator valueAnimator;
    private ExpandableConstraintLayoutStatus currentStatus = IDLE;

    // Constructors
    public ExpandableConstraintLayout(Context context) {
        this(context, null, 0);
    }

    public ExpandableConstraintLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // default values
        animationDuration = 200;
        interpolator = new FastOutSlowInInterpolator();
        isVertical = true;
        displacement = 1;
        expansion = 1;
    }

    // Overridden Methods
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int size = isVertical ? height : width;
        setVisibility(expansion == 0 && size == 0 ? INVISIBLE : VISIBLE);
        int expansionDelta = size - Math.round(size * expansion);
        // translate all children before measuring the parent
        if (displacement > 0) {
            float displacementDelta = expansionDelta * displacement;
            for (int i = 0; i < getChildCount(); i++) {
                if (isVertical) {
                    getChildAt(i).setTranslationY(-displacementDelta);
                } else {
                    int direction = -1;
                    getChildAt(i).setTranslationX(direction * displacementDelta);
                }
            }
        }
        if (isVertical) {
            setMeasuredDimension(width, height - expansionDelta);
        } else {
            setMeasuredDimension(width - expansionDelta, height);
        }
    }

    // General Methods
    public void toggle() {
        if (isExpanded()) {
            collapse();
        } else {
            expand();
        }
    }

    public void expand() {
        shouldExpand(true, true);
    }

    public void collapse() {
        shouldExpand(false, true);
    }

    private void shouldExpand(boolean shouldExpand, boolean shouldAnimate) {
        if (shouldExpand && (currentStatus == EXPANDING || expansion == 1)) {
            return;
        }
        if (!shouldExpand && (currentStatus == COLLAPSING || expansion == 0)) {
            return;
        }
        int newExpansion = shouldExpand ? 1 : 0;
        if (shouldAnimate) {
            animateExpansion(newExpansion);
        } else {
            setExpansion(newExpansion);
        }
    }

    private void setExpansion(float newExpansion) {
        // Nothing to do here
        if (this.expansion == newExpansion) {
            return;
        }
        this.expansion = newExpansion;
        setVisibility(expansion == 0 ? INVISIBLE : VISIBLE);
        requestLayout();
    }

    private void animateExpansion(final int newExpansion) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
        valueAnimator = ValueAnimator.ofFloat(expansion, newExpansion);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.setDuration(animationDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setExpansion((float) valueAnimator.getAnimatedValue());
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                reportListenerStatus(AnimationStart);
                if (newExpansion > 0) {
                    reportListenerStatus(PreOpen);
                    currentStatus = EXPANDING;
                } else {
                    reportListenerStatus(PreClose);
                    currentStatus = COLLAPSING;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                reportListenerStatus(AnimationEnd);
                if (currentStatus == EXPANDING) {
                    reportListenerStatus(Opened);
                } else {
                    reportListenerStatus(Closed);
                }
                currentStatus = IDLE;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentStatus = IDLE;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.start();
    }

    public boolean isExpanded() {
        return expansion == 1;
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }

    public TimeInterpolator getInterpolator() {
        return interpolator;
    }

    public int getAnimationDuration() {
        return animationDuration;
    }

    public void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
    }

    // Listener Related
    public void setAnimationListener(@NonNull ExpandableConstraintLayoutListener listener) {
        this.animationListener = listener;
    }

    public void reportListenerStatus(ExpandableConstraintLayoutListenerStatus status) {
        if (status != null && animationListener != null) {
            switch (status) {
                case PreOpen:
                    animationListener.onPreOpen();
                    break;
                case PreClose:
                    animationListener.onPreClose();
                    break;
                case Opened:
                    animationListener.onOpened();
                    break;
                case Closed:
                    animationListener.onClosed();
                    break;
                case AnimationStart:
                    animationListener.onAnimationStart(currentStatus);
                    break;
                case AnimationEnd:
                    animationListener.onAnimationEnd(currentStatus);
                    break;
                default:
                    break;
            }
        }
    }

}
