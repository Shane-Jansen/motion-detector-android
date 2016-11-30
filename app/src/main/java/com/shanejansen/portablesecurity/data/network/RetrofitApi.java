package com.shanejansen.portablesecurity.data.network;

import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Shane Jansen on 12/31/15.
 *
 * Abstract used to instantiate Retrofit objects.
 */
public abstract class RetrofitApi {
  protected abstract String getBaseUrl();

  public Retrofit getRetrofit() {
    /*Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
      @Override public boolean shouldSkipField(FieldAttributes f) {
        return f.getDeclaringClass().equals(RealmObject.class);
      }

      @Override public boolean shouldSkipClass(Class<?> clazz) {
        return false;
      }
    }).create();*/

    return new Retrofit.Builder().baseUrl(getBaseUrl())
        .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
        .build();
  }
}
