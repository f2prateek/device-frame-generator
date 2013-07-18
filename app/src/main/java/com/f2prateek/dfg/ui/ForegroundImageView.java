/*
 * Copyright 2013 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.f2prateek.dfg.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.f2prateek.dfg.R;

public class ForegroundImageView extends ImageView {

  private Drawable foreground;
  private Rect foregroundBounds = new Rect();

  public ForegroundImageView(Context context) {
    this(context, null);
  }

  public ForegroundImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ForegroundImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    TypedArray a =
        context.obtainStyledAttributes(attrs, R.styleable.ForegroundImageView, defStyle, 0);
    foreground = a.getDrawable(R.styleable.ForegroundImageView_android_foreground);
    foreground.setCallback(this);

    a.recycle();
  }

  @Override
  protected boolean verifyDrawable(Drawable who) {
    return super.verifyDrawable(who) || (who == foreground);
  }

  @Override
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    if (foreground != null) {
      int[] drawableState = getDrawableState();
      foreground.setState(drawableState);
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    final Rect frameBounds = foregroundBounds;
    if (frameBounds.isEmpty()) {
      final int w = getWidth();
      final int h = getHeight();

      frameBounds.set(0, 0, w, h);
      foreground.setBounds(frameBounds);
    }

    foreground.draw(canvas);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    foregroundBounds.setEmpty();
  }
}
