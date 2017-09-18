# ExpandableConstraintLayout

![Current Version](https://img.shields.io/badge/Current%20Version-1.0.0-brightgreen.svg)
[![](https://jitpack.io/v/rjsvieira/expandableConstraintLayout.svg)](https://jitpack.io/#rjsvieira/expandableConstraintLayout)
![Minimum SDK](https://img.shields.io/badge/minSdkVersion%20-14-blue.svg)

<img src="images/expconslayout.gif">


<h2> Description </h2>

The ExpandableConstraintLayout presents itself as a wrapper around Google's ConstraintLayout with the small feature of toggling (expansion/collapse).
This class was implemented based on @cmfsotelo's idea of ExpandableLinearLayout

<h2> Project Inclusion </h2>
Include it in your project

1) In your root/build.gradle

```groovy
allprojects {
  repositories {
  ...
  maven { url 'https://jitpack.io' }
  }
}
```

2) In your app/build.gradle

```groovy
dependencies {
  compile 'com.github.rjsvieira:rjsvieira:expandableConstraintLayout:1.0.0'
}
```

<h2> Initialization </h2>

Initialize your ExpandaConstraintLayout just like any other ConstraintLayout

```xml
ExpandableConstraintLayout ecl = (ExpandableConstraintLayout) findViewById(R.id.ecl);
```

<h2> Configuration </h2>
The user can define the duration of the expansion/collapse as well as the Interpolator used to animate the values

```java
void setInterpolator(TimeInterpolator interpolator)
void setAnimationDuration(int animationDuration)
```

<h2> Interaction </h2>
The interaction methods are really basic and intuitive. In short, they stand for :

```java
void toggle()
void expand()
void collapse()
```

<h2> Listener </h2>

The ExpandableConstraintLayout allows the implementation of a listener :

```java
setAnimationListener(@NonNull ExpandableConstraintLayoutListener listener)
```
This listener will track the following events : 

```java
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
```

