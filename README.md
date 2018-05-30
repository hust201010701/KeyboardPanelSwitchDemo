android keyboard键盘和panel面板切换时，屏幕闪烁解决方案

# Android键盘操作总结

Android 键盘相关常见问题有：

1. 限制输入框内字数，超过字数不让输入，并且提示
2. 点击外部区域键盘自动收起
3. 如何获取键盘高度
4. 键盘与面板的切换冲突

下面将对上述问题各个击破。

## 1. 限制输入框内字数，超过字数不让输入，并且提示

```java
etReply.setFilters(new InputFilter[]{new InputFilter() {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (source.length() + dest.length() > COMMENT_MAX_NUM) {
            Crouton.makeText(AppUtils.getString(R.string.infodetail_comment_limit), Style.ALERT).show();
        }
        return null;
    }
}, new InputFilter.LengthFilter(COMMENT_MAX_NUM)});
```

## 2. 点击外部区域键盘自动收起

如果当前页面是Activity那么可以直接重写`dispatchTouchEvent`方法。在`ACTION_DOWN`事件时，判断点击的坐标是否在输入框坐标的上面，如果是那么调用隐藏键盘的方法。

如果当前页面是Fragment，那么Fragment中增加一个`dispatchTouchEvent`方法，内部逻辑同上，在Fragment所依赖的Activity代码中将`dispatchTouchEvent`事件透传给Fragment的`dispatchTouchEvent`，如果键盘需要隐藏，Fragment的`dispatchTouchEvent`方法需要返回`true`，表示消费本次所有触摸事件，不再继续传递。

## 3. 如何获取键盘高度

首先需要知道一点，键盘高度不是固定的。用户使用不同的输入法，高度可能不一样；甚至有些输入法，可以直接调节输入法面板的高度。

### 3.1 有没有系统的api可以供我们获取键盘高度？
没有。

### 3.2 有什么方法可以间接获取键盘高度？
系统给我们提供了一个页面布局变化的监听器`OnGlobalLayoutListener `，这个监听器可以通知我们布局发生改变，我们可以在此时获取自己的高度，再通过屏幕宽度和状态栏高度等间接计算出键盘的高度。

- 那么就有一个问题，`OnGlobalLayoutListener `接收到变化动作时，一定是键盘弹出或消失么？

	> 不一定。
- 那为什么使用`OnGlobalLayoutListener `可以监听键盘的状态？

	> 我们知道每个view的宽高变化都会导致`OnGlobalLayoutListener `的触发，但是对当前Activity的Window对象中的DecorView进行监听时，一般来说，DecorView的尺寸不会发生变化，发生变化的主要原因就是键盘的收起和展开，这时候加上简单的判断（变化超过某个阈值）就可以获取键盘的高度，以及是否弹起。
	
- 有没有使用`OnGlobalLayoutListener `监听键盘失效的情景？

	> 在Android7.0上，我们可以使用多任务键开启分屏/多窗口模式，当我们开启分屏之后，调整分屏的分界线时，都会触发`DecorView`的`OnGlobalLayoutListener`，但是此时键盘并未触发任何动作；而且，当我们点击某个输入框之后，键盘在分屏模式下会变成悬浮模式，不会挤压Activity的控件，所以当键盘弹出或收起时，`OnGlobalLayoutListener `不会接收到任何事件。这就导致了`OnGlobalLayoutListener `完全失效。还有一些其他的场景导致监听键盘事件失效的情景，暂时想不起来，可以在评论处补充。

### 3.3 获取键盘高度

在一般情况下，我们对`Activity`的`PhoneWindow`中`DecorView`的布局变化进行监听，一般来说，变化值超过`60dp`就可以认为是键盘弹出或收起了。而且在非全屏主题下，
键盘高度 = 屏幕高度 - 状态栏高度 - 主视图高度 - 标题栏高度， 于是我们可以通过下面代码间接计算出键盘高度。

```java
mDecorView = this.getActivity().getWindow().getDecorView();
mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
    @Override
    public void onGlobalLayout() {
        Rect rect = new Rect();
        mDecorView.getWindowVisibleDisplayFrame(rect);
        // 不能使用decorView.getHeight()获取decorview的高度，获取的高度不会发生变化
        int displayHeight = rect.bottom;
        if (Math.abs(displayHeight - mOldDecorViewHeight) > dp60) {
            mOldDecorViewHeight =displayHeight;
            int rootHeight = mRoot.getHeight();
            int statusBarHeight = getStatusBarHeight();
            int screenHeight = getScreenHeight();
            int titleBarHeight = getTitleBarHeight();
            //在非全屏模式下， 键盘高度 = 屏幕高度 - 状态栏高度 - 主视图高度 - 标题栏高度
            int keyboardHeight = screenHeight - statusBarHeight - rootHeight - titleBarHeight;
            Log.i("lxc", "keyboardHeight ---> " + keyboardHeight + "  键盘:  " + (keyboardHeight > 0 ? "弹出" : "收起"));
        }
    }
};
mDecorView.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
```

当我点击输入框弹出和收起键盘时，会出现下面的log日志：

'''console
05-29 15:22:50.368 8982-8982/com.orzangleli.myapplication I/lxc: keyboardHeight ---> 0  键盘:  收起
05-29 15:22:51.736 8982-8982/com.orzangleli.myapplication I/lxc: keyboardHeight ---> 873  键盘:  弹出
05-29 15:22:52.739 8982-8982/com.orzangleli.myapplication I/lxc: keyboardHeight ---> 0  键盘:  收起
05-29 15:22:53.892 8982-8982/com.orzangleli.myapplication I/lxc: keyboardHeight ---> 873  键盘:  弹出
'''

## 4. 键盘与面板的切换冲突

### 4.1 问题描述

在IM聊天页面，通常下面会做成类似于微信的样式(点击后可切换表情面板和键盘)。点击表情按钮，会弹出表情面板，且表情按钮变成键盘模式;再次点击键盘模式，或者点击输入框，会弹出输入框，并收起表情面板。以下篇幅均称表情面板为面板。

![http://7mnnry.com1.z0.glb.clouddn.com/%E4%BC%81%E4%B8%9A%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_675ab96e-f694-4ac6-a3cf-ee34fa618729.png](http://7mnnry.com1.z0.glb.clouddn.com/%E4%BC%81%E4%B8%9A%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_675ab96e-f694-4ac6-a3cf-ee34fa618729.png)

### 4.2 常规思路

通常这样的页面布局是一个RecyclerView+输入区域。输入区域在RecyclerView下面，所以整个布局可以使用垂直的LinearLayout。键盘模式我们选择`adjustResize`。

**常规的逻辑如下**：

- 输入区域包含输入框和下面的表情面板，默认表情面板的`visibility`为`GONE`。
- 点击表情按钮时，面板的可见性为`VISIBLE`；收起输入法键盘；按钮图片变为键盘模式。
- 再次点击键盘模式按钮，面板的可见性为`GONE`；展开输入法键盘；按钮推盘变成表情模式。

我们按照上述思路写下关键代码：

```java
private void initView(View root) {
    mInputEt = root.findViewById(R.id.et_input);
    mFaceBtn = root.findViewById(R.id.btn_face);
    mPanel = root.findViewById(R.id.panel);

    mFaceBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPanel.getVisibility() == View.VISIBLE) {
                mPanel.setVisibility(View.GONE);
                mFaceBtn.setImageResource(R.drawable.emoji_download_icon);
                openKeyBoard(mInputEt);
            } else {
                mPanel.setVisibility(View.VISIBLE);
                mFaceBtn.setImageResource(R.drawable.zz_chat_reply_keyboard);
                closeKeyBoard(mInputEt);
            }
        }
    });
}
```

运行看看结果：

![http://7mnnry.com1.z0.glb.clouddn.com/face1.gif](http://7mnnry.com1.z0.glb.clouddn.com/face1.gif)

出现了奇怪的一帧：

![http://7mnnry.com1.z0.glb.clouddn.com/face2.png?imageMogr2/thumbnail/!50p](http://7mnnry.com1.z0.glb.clouddn.com/face2.png?imageMogr2/thumbnail/!50p)

**结论：**

当屏幕中已显示键盘时，点击表情按钮弹出面板前，需要隐藏键盘，但是隐藏键盘我们只是调用的一个远程服务（`Context.INPUT_METHOD_SERVICE`），它是何时执行我们无法控制（一般来说，涉及跨进程通信，所以执行顺序肯定是在面板显示之后），所以我们无论我们先调用隐藏键盘api再显示表情面板，还是先显示表情面板在调用隐藏键盘的api都会出现这一帧现象，给人的感觉就是闪烁了一下。

## 解决方案

因此，我们隐藏和显示表情面板的时机不是点击表情按钮时就立刻执行，而是需要等到输入法面板完全显示或完全隐藏后再进行。

这里可能涉及到一些监听键盘弹起/隐藏操作和获取键盘高度的知识。可以参见上一节小结[如何获取键盘高度](如何获取键盘高度)。我们获取的键盘高度每次更新后直接存储在`SharedPreferences`，某些应用需要重新将弹起的面板高度重新设置为与键盘高度相同，如微信就需要记录键盘的高度。但也不是每个应用都需要面板与键盘高度一致，如果你的应用不需要可以不用看如何获取键盘高度。

如果我们在OnGlobalLayoutListener中监听键盘的弹出或收起，并根据相应状态设置面板的隐藏或显示时会出现一些闪烁的问题（代码可以看[Demo](https://github.com/hust201010701/KeyboardPanelSwitchDemo)中的半解决切换键盘冲突）。因为闪烁的时间很短，所以录制gif的时候无法看到，有兴趣的可以运行Demo中半解决切换键盘冲突方案。

以键盘弹起为例，我们的流程是这样的：

触发键盘弹起  -->   OnGlobalLayoutListener接收到布局变化   -->    此时键盘已经完全弹起   --> X -->  隐藏表情面板

这个流程中的`X`指的是bug出现的时候，键盘完全弹起时，表情键盘并没有立即隐藏，而是随后隐藏的，这就导致了半解决冲突的微弱闪烁的现象。

- 为什么会出现这样的微弱的闪烁?

> 我们来看看ViewGroup的测量过程。ViewGroup测量时，会先去遍历测量所有的子View的尺寸，然后结合ViewGroup的测量模式计算出合适的尺寸。在我们这个案例里，当表情面板已经展开时，如果切换到键盘，首页键盘会挤压整个布局，也就是我们说的ViewGroup的布局，但是此时执行ViewGroup的onMeasure时，里面的表情面板仍然是可见的。然后我们在`OnGlobalLayoutListener`的回调里将表情面板的可见性设为`GONE`， 但此时已经和键盘刚展开时已经不是同一帧了，所以看到了微弱的闪烁效果。

 
根据上面的分析，我们需要在键盘收起时的那一帧中，测量ViewGroup尺寸时，直接重新测量的面板控件的尺寸就可以了。我们把表情区域放进一个自定义的布局控件`KBPanelConflictLayout`，整个页面的根布局设为自定义控件`KBRootConflictLayout`（代码可以看[Demo](https://github.com/hust201010701/KeyboardPanelSwitchDemo)中的解决切换键盘冲突）。

在`KBRootConflictLayout`的`onMeasure`方法中，根据布局高度变化是否超过某个阈值来判断是否键盘弹起。

```java
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    preNotifyChild(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
}
    
private void preNotifyChild(int width, int height) {
    if (mOldHeight < 0) {
        mOldHeight = height;
        return ;
    }
    int deltaY = height - mOldHeight;
    mOldHeight = height;
    int minKeyboardHeight = 180;
    if (Math.abs(deltaY) >= minKeyboardHeight) {
        if (deltaY < 0) {
            // 键盘弹起
            if (mKBPanelConflictLayout != null) {
                // 隐藏面板
                mKBPanelConflictLayout.setHide();
            }
        } else {
            // 键盘收起
            if (mKBPanelConflictLayout != null) {
                // 显示面板
                mKBPanelConflictLayout.setShow();
            }
        }
    }
}
```


在`KBPanelConflictLayout`的`onMeasure`方法中，我们根据是否隐藏状态来判断是否需要把键盘的高度变为0.

```java
 @Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (mHide) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
    }
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
}

public void setHide() {
    this.mHide = true;
    setVisibility(View.GONE);
}

public void setShow() {
    this.mHide = false;
    setVisibility(View.VISIBLE);
}
```

这样在测试下，看看没有闪烁冲突的效果图吧。

![http://7xvdj7.com1.z0.glb.clouddn.com/jiejue.gif](http://7xvdj7.com1.z0.glb.clouddn.com/jiejue.gif)

如果你把上述代码整理优化下，加上对RelativeLayout和FrameLayout的支持，对设置是否调整面板高度与键盘一致的支持，对多面板的切换的支持，提供一些工具类给用户直接调用，那就是2000+star的[https://github.com/Jacksgong/JKeyboardPanelSwitch](https://github.com/Jacksgong/JKeyboardPanelSwitch)项目了。

附上本文Demo地址，欢迎点心：

[https://github.com/hust201010701/KeyboardPanelSwitchDemo](https://github.com/hust201010701/KeyboardPanelSwitchDemo)










