#效果图：

![layout-2016-09-01-182947.png](http://upload-images.jianshu.io/upload_images/1633382-5a54c3d1f30cc563.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#balabala

故事是这样的：
某天，不懂UI从哪里搞得图片，要求程序员实现金额文本里元的字体较大，分的字体较小，而且前面的前缀样式还不定。为了满足UI妹子的需求，程序员就快快的用三个TextView外套一个LinearLayout实现了。
but,写完不久，发现妹子在另外的页面也用了这样的样式，但字体颜色不同。而且，更可恶的是，有些页面的金额元和分的字体大小又一样了，连前缀也没了。程序员一言不合就绘制了一个MoneyView，一劳永逸。
哼，再也不用被她为所欲为的需求搞得焦头烂额了。
#思路
根据妹子已给出的图，我猜，没错，我是猜的。鬼懂她什么时候想想又乱改了。so，从以下几个方面去规定这个MoneyView应该具备怎么样的样式:
* 元和分能自定义大小
* 前缀能自定义文本、颜色和大小
* 前缀与文本能自定义padding
* 小数点能自定义左右间隔
* 金额能自定义颜色
* 允许设置千分符

#attrs
其实还可能有更多的，不过她如果这么过分的话，我就继承ViewGroup的子类了，哼。
好了，根据上述规定，程序员写出了下列attr：
` <!--金额 样式  元大 角分小 $25.33-->
    <declare-styleable name="MoneyView">
        <!--金额-->
        <attr name="money_text" format="string"/>
        <!--金额颜色-->
        <attr name="money_color" format="color"/>
        <!--元大小-->
        <attr name="yuan_size" format="dimension"/>
        <!--分大小-->
        <attr name="cent_size" format="dimension"/>
        <!--前缀文本-->
        <attr name="prefix_text" format="string"/>
        <!--前缀大小-->
        <attr name="prefix_size" format="dimension"/>
        <!--前缀颜色-->
        <attr name="prefix_color" format="color"/>
        <!--前缀右边距-->
        <attr name="prefix_padding" format="dimension"/>
        <!--小数点左边距-->
        <attr name="point_padding_left" format="dimension"/>
        <!--小数点右边距-->
        <attr name="point_padding_right" format="dimension"/>
        <!--是否使用千分符-->
        <attr name="grouping" format="boolean"/>
    </declare-styleable>
` 
#constructor
没错，各位看官也可以有这样的思路去自定义View，先想好可能的拓展，再列出attr，最后才开始写代码。
好，既然我们写完了attr，就开始去学代码了。先new 一个Class 名字叫做MoneyView，然后复写其三个构造方法：

 

       `public MoneyView ( Context context ) {
                this ( context, null );
        }

        public MoneyView ( Context context, AttributeSet attrs ) {
                this ( context, attrs, 0 );
        }

        public MoneyView ( Context context, AttributeSet attrs, int defStyle ) {
                super ( context, attrs, defStyle );
                init ( context, attrs, defStyle );
        }

        private void init ( Context context, AttributeSet attrs, int defStyle ) {
                TypedArray typedArray = context.getTheme ( ).obtainStyledAttributes ( attrs, R.styleable.MoneyView, defStyle, 0 );

                mMoneyText = typedArray.getString ( R.styleable.MoneyView_money_text );
                mMoneyColor = typedArray.getColor ( R.styleable.MoneyView_money_color, mMoneyColor );
                mYuanSize = typedArray.getDimensionPixelSize ( R.styleable.MoneyView_yuan_size, mYuanSize );
                mCentSize = typedArray.getDimensionPixelSize ( R.styleable.MoneyView_cent_size, mCentSize );

                mPrefix = typedArray.getString ( R.styleable.MoneyView_prefix_text );
                mPrefixSize = typedArray.getDimensionPixelSize ( R.styleable.MoneyView_prefix_size, mPrefixSize );
                mPrefixColor = typedArray.getColor ( R.styleable.MoneyView_prefix_color, mPrefixColor );
                mPrefixPadding = typedArray.getDimensionPixelSize ( R.styleable.MoneyView_prefix_padding, mPrefixPadding );

                mPointPaddingLeft = typedArray.getDimensionPixelSize ( R.styleable.MoneyView_point_padding_left, mPointPaddingLeft );
                mPointPaddingRight = typedArray.getDimensionPixelSize ( R.styleable.MoneyView_point_padding_right, mPointPaddingRight );
                mIsGroupingUsed = typedArray.getBoolean ( R.styleable.MoneyView_grouping, false );
                typedArray.recycle ( );
        }`

通过TypedArray 获取我们刚才写的attr
`public int getDimensionPixelSize(int index, int defValue) {`
用户不输入我们就给予默认值。默认在声明成员属性处已经给出。
#onMeasure
那，现在我们也获取到用户设置的属性了，现在就要调用onMeasure去计算这个自定义MoneyView占据的宽高了。（代码有删减）
`

	@Override
        protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {
                int width;
                int height;

                //如果使用千分符
                if ( mIsGroupingUsed ) {
                        mYuan = NumberFormat.getInstance ( ).format ( Long.valueOf ( mYuan ) );
                }
                //获取元小数点、的占据宽高
                mPaint.setTextSize ( mYuanSize );
                mPaint.getTextBounds ( mYuan, 0, mYuan.length ( ), mYuanBound );
                mPaint.getTextBounds ( POINT, 0, POINT.length ( ), mPointBound );
                //获取分占据宽高
                mPaint.setTextSize ( mCentSize );
                mPaint.getTextBounds ( mCent, 0, mCent.length ( ), mCentBound );
                //获取前缀占据宽高
                mPaint.setTextSize ( mPrefixSize );
                mPaint.getTextBounds ( mPrefix, 0, mPrefix.length ( ), mPrefixBound );
                /**
                 * 设置高度
                 */
                //获取最大字号
                int maxSize = Math.max ( mYuanSize, mCentSize );
                maxSize = Math.max ( maxSize, mPrefixSize );
                mPaint.setTextSize ( maxSize );
                //获取基线距离底部
                maxDescent = mPaint.getFontMetrics ( ).descent;
                int maxHeight = Math.max ( mYuanBound.height ( ), mCentBound.height ( ) );
                maxHeight = Math.max ( maxHeight, mPrefixBound.height ( ) );
                //文本占据的高度
                mTextHeight =  maxHeight + ( int ) ( maxDescent + 0.5f );

                specMode = MeasureSpec.getMode ( heightMeasureSpec );
                specSize = MeasureSpec.getSize ( heightMeasureSpec );
                if ( specMode == MeasureSpec.EXACTLY ) {
                        height = specSize;
                } else {
                        height = mTextHeight;
                }
                setMeasuredDimension ( width, height );
        }
`
我们分别计算前缀，元，分占据的宽高，然后对比，取最高的一个作为MoneyView的高。
这里获取了最大文本的基线值maxDescent。这里需要注意一点，canvas.drawText是根据**baseLine（基线）**绘制的，这和我们小时候用四线纸去写字母一个意思，如下图：

![e038aae657b1832ecc32c336c6075ffc.jpg](http://upload-images.jianshu.io/upload_images/1633382-3d481176069c4df0.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#onDraw
所以我们再onDraw()时要在Y轴上加上基线距离底部的值，才是我们需要绘制的，**绘制过程会将文本居中。**
`

     @Override
     protected void onDraw ( Canvas canvas ) {
		//绘制X坐标
		int drawX = ( getMeasuredWidth ( ) - mTextWidth ) / 2;
		float drawY = ( getMeasuredHeight ( ) + mTextHeight ) / 2 - maxDescent;

		//绘制前缀
		mPaint.setColor ( mPrefixColor );
		mPaint.setTextSize ( mPrefixSize );
		canvas.drawText ( mPrefix, drawX, drawY, mPaint );
		//绘制元
		drawX += mPrefixBound.width ( ) + mPrefixPadding;
		mPaint.setColor ( mMoneyColor );
		mPaint.setTextSize ( mYuanSize );
		canvas.drawText ( mYuan, drawX, drawY, mPaint );
		//绘制小数点
		drawX += mYuanBound.width ( ) + mPointPaddingLeft;
		canvas.drawText ( POINT, drawX, drawY, mPaint );
		//绘制分
		drawX += mPointPaddingRight;
		mPaint.setTextSize ( mCentSize );
		canvas.drawText ( mCent, drawX, drawY, mPaint );
     }`
OK，那这个MoneyView 就可以拿来用了，这是全部代码的地址：
[github：https://github.com/cchao1024/MoneyView](https://github.com/cchao1024/MoneyView)
笔者试图将他放到Jcenter，但是gradlew install 时报 GBK什么鬼，没解决，算了，代码也不多。
如果看官需要使用该View。只需将github上monveyView/res/attrs 里面的moneyView 复制到你项目的Attrs  ,再复制整个MoneyView 到你项目的里，然后编写layout的时候引用，就可能跑的欢快了。like this：
`

    <com.github.cchao.MoneyView
	      android:layout_width="wrap_content"
	      android:layout_height="wrap_content"
	      money:money_text="789456.123"/>
 
`
显示效果就是效果图的第一个。