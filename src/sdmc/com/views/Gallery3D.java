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
	private Camera camera = new Camera();// �����
	private int maxRotationAngle = 60;// ���ת���Ƕ�
	private int maxZoom = 60;// z������ƶ���,�൱������
	private int coveflowCenter;// �뾶ֵ

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

	// ����gallery��ÿ��ͼƬ����ת(��д��gallery�з���)
	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {
		// ȡ�õ�ǰ��view�İ뾶ֵ
		final int childCenter = getCenterOfView(child);
		System.out.println("childCenter��" + childCenter);
		final int childWidth = child.getWidth();
		// ��ת�Ƕ�
		int rotationAngle = 0;
		// ����ת��״̬
		t.clear();
		// ����ת������
		t.setTransformationType(Transformation.TYPE_BOTH);
		// ���ͼƬλ������λ�ò���Ҫ������ת
		if (childCenter == coveflowCenter) {
			transformImageBitmap((ImageView) child, t, 0);
		} else {
			// ����ͼƬ��gallery�е�λ��������ͼƬ����ת�Ƕ�
			rotationAngle = (int) (((float) (coveflowCenter - childCenter) / childWidth) * maxRotationAngle);
			System.out.println("rotationAngle:" + rotationAngle);
			// �����ת�ǶȾ���ֵ���������ת�Ƕȷ��أ�-mMaxRotationAngle��mMaxRotationAngle;��
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
		// ��Ч�����б���
		camera.save();

		// ������ת�Ƕȵľ���ֵ
		// final int rotation = Math.abs(rotationAngle);

		// ��Z���������ƶ�camera���ӽǣ�ʵ��Ч��Ϊ�Ŵ�ͼƬ��
		// �����Y�����ƶ�����ͼƬ�����ƶ���X���϶�ӦͼƬ�����ƶ���
		camera.translate(0f, 0f, maxZoom);
		// // ��ȷ�����ſ���
		// if (rotation < mMaxRotationAngle) {
		// float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
		// mCamera.translate(0.0f, 0.0f, zoomAmount);
		// }
		// �����Y������ת����ӦͼƬ�������﷭ת��
		// �����X������ת����ӦͼƬ�������﷭ת��
		camera.rotateY(rotationAngle);
		final Matrix imageMatrix = t.getMatrix();
		camera.getMatrix(imageMatrix);
		camera.restore();

		// ������ת���ĵ�
		// ͼƬ�߶�,�õ�gallery�ĸ߶�
		// ͼƬ���,�õ�ͼƬ���;����ͼƬ�ĸ߶�ȡ����,����-1,��֪��ʲôԭ��
		final int imageWidth = child.getLayoutParams().width;

		imageMatrix.preTranslate(-(imageWidth / 2), -height / 2);
		imageMatrix.postTranslate((imageWidth / 2), height / 2);
		camera.save();
	}

}

