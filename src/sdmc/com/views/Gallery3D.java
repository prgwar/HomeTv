package sdmc.com.views;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

@SuppressWarnings("deprecation")
public class Gallery3D extends Gallery {
	private Camera camera = new Camera();// 相机类
	private int maxRotationAngle = 60;// 最大转动角度
	private int maxZoom = 60;// z方向的移动至,相当于缩放
	private int coveflowCenter;// 半径值

	private int height;

	public Gallery3D(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setStaticTransformationsEnabled(true);
	}

	public Gallery3D(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setStaticTransformationsEnabled(true);
	}

	public Gallery3D(Context context) {
		super(context);
		this.setStaticTransformationsEnabled(true);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		height = getHeight();
	}

	public int getMaxRotationAngle() {
		return maxRotationAngle;
	}

	public void setMaxRotationAngle(int maxRotationAngle) {
		this.maxRotationAngle = maxRotationAngle;
	}

	public int getMaxZoom() {
		return maxZoom;
	}

	public void setMaxZoom(int maxZoom) {
		this.maxZoom = maxZoom;
	}

	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
				+ getPaddingLeft();
	}

	private static int getCenterOfView(View view) {
		System.out.println("view left :" + view.getLeft());
		System.out.println("view width :" + view.getWidth());
		return view.getLeft() + view.getWidth() / 2;
	}

	// 控制gallery中每个图片的旋转(重写的gallery中方法)
	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {
		// 取得当前子view的半径值
		final int childCenter = getCenterOfView(child);
		System.out.println("childCenter：" + childCenter);
		final int childWidth = child.getWidth();
		// 旋转角度
		int rotationAngle = 0;
		// 重置转换状态
		t.clear();
		// 设置转换类型
		t.setTransformationType(Transformation.TYPE_BOTH);
		// 如果图片位于中心位置不需要进行旋转
		if (childCenter == coveflowCenter) {
			transformImageBitmap((ImageView) child, t, 0);
		} else {
			// 根据图片在gallery中的位置来计算图片的旋转角度
			rotationAngle = (int) (((float) (coveflowCenter - childCenter) / childWidth) * maxRotationAngle);
			System.out.println("rotationAngle:" + rotationAngle);
			// 如果旋转角度绝对值大于最大旋转角度返回（-mMaxRotationAngle或mMaxRotationAngle;）
			if (Math.abs(rotationAngle) > maxRotationAngle) {
				rotationAngle = (rotationAngle < 0) ? -maxRotationAngle
						: maxRotationAngle;
			}
			transformImageBitmap((ImageView) child, t, rotationAngle);
		}
		return true;
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		coveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void transformImageBitmap(ImageView child, Transformation t,
			int rotationAngle) {
		// 对效果进行保存
		camera.save();

		// 返回旋转角度的绝对值
		// final int rotation = Math.abs(rotationAngle);

		// 在Z轴上正向移动camera的视角，实际效果为放大图片。
		// 如果在Y轴上移动，则图片上下移动；X轴上对应图片左右移动。
		camera.translate(0f, 0f, maxZoom);
		// // 精确的缩放控制
		// if (rotation < mMaxRotationAngle) {
		// float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
		// mCamera.translate(0.0f, 0.0f, zoomAmount);
		// }
		// 如果在Y轴上旋转，对应图片竖向向里翻转。
		// 如果在X轴上旋转，对应图片横向向里翻转。
		camera.rotateY(rotationAngle);
		final Matrix imageMatrix = t.getMatrix();
		camera.getMatrix(imageMatrix);
		camera.restore();

		// 设置旋转中心点
		// 图片高度,用的gallery的高度
		// 图片宽度,用的图片宽度;这里图片的高度取不到,都是-1,不知道什么原因
		final int imageWidth = child.getLayoutParams().width;

		imageMatrix.preTranslate(-(imageWidth / 2), -height / 2);
		imageMatrix.postTranslate((imageWidth / 2), height / 2);
		camera.save();
	}

}

