package com.shanejansen.portablesecurity.ui.common;

import android.os.Bundle;
import android.view.View;
import butterknife.ButterKnife;
import com.shanejansen.mvpandroid.fragments.MvpFragment;
import com.shanejansen.mvpandroid.mvp.BasePresenter;
import com.shanejansen.mvpandroid.mvp.BaseView;
import com.shanejansen.mvpandroid.mvp.BaseViewModel;

/**
 * Created by Shane Jansen on 12/2/16.
 */
public abstract class AppMvpFragment<M extends BaseViewModel, V extends BaseView, P extends BasePresenter>
    extends MvpFragment<M, V, P> {
  @Override protected void onViewInflated(View v, Bundle savedInstanceState) {
    super.onViewInflated(v, savedInstanceState);
    ButterKnife.bind(this, v);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
  }
}
