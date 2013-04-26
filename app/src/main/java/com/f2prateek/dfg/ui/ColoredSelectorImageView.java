/*
 * Copyright 2013 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.f2prateek.dfg.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.f2prateek.dfg.R;

public class ColoredSelectorImageView extends ImageView {

    public ColoredSelectorImageView(Context context) {
        super(context);
    }

    public ColoredSelectorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColoredSelectorImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && isEnabled())
            setColorFilter(getResources().getColor(R.color.holo_blue_dark), PorterDuff.Mode.OVERLAY);
        if (event.getAction() == MotionEvent.ACTION_UP)
            setColorFilter(null);
        return super.onTouchEvent(event);
    }

}
