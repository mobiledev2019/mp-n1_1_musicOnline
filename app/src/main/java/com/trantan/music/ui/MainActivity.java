package com.trantan.music.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.trantan.music.R;
import com.trantan.music.data.Track;
import com.trantan.music.data.source.TrackRepository;
import com.trantan.music.data.source.TracksDataSource;
import com.trantan.music.data.source.local.DataBaseHelper;
import com.trantan.music.data.source.local.LocalDataSource;
import com.trantan.music.data.source.remote.TracksRemoteDataSource;
import com.trantan.music.service.music.PlayService;
import com.trantan.music.ui.discover.DiscoverFragment;
import com.trantan.music.ui.mymusic.MyMusicFragment;
import com.trantan.music.ui.setting.SettingFragment;

import java.util.List;

public class MainActivity extends BaseAcivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final int MY_REQUEST_READ_STORAGE = 1;
    private static final String MY_FRAGMENT_TAG = "MY_FRAGMENT";
    private BottomNavigationView mNavigationView;
    private boolean isReadAble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
        startPlayService();
        synFavorite();
    }

    private void initUi() {
        isReadAble = false;
        initMiniPlay(R.id.view_mini_play);
        mNavigationView = findViewById(R.id.navigation);
        mManager.beginTransaction().replace(R.id.frag_main, new DiscoverFragment(), MY_FRAGMENT_TAG)
                .commit();
        mNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkReadStoragePremission();
        checkCurrentDisplayFragment();
    }

    private void checkCurrentDisplayFragment() {
        Fragment fragment = mManager.findFragmentByTag(MY_FRAGMENT_TAG);
        if (fragment.isVisible()) {
//            Log.d(TAG, "checkCurrentDisplayFragment: "+fragment.getClass());
            if (fragment instanceof DiscoverFragment) {
                mNavigationView.setSelectedItemId(R.id.menu_discover);
            }
            if (fragment instanceof MyMusicFragment) {
                mNavigationView.setSelectedItemId(R.id.menu_my_music);
            }
            if (fragment instanceof SettingFragment) {
                mNavigationView.setSelectedItemId(R.id.menu_settings);
            }
        }
    }

    private void startPlayService() {
        Intent intent = PlayService.getIntent(this);
        startService(intent);
    }

    @Override
    public void showMiniPlayFragment(boolean isShow) {
        View view = findViewById(R.id.view_mini_play);
        if (isShow) view.setVisibility(View.VISIBLE);
        else view.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_discover:
                if (mNavigationView.getSelectedItemId() == R.id.menu_discover) return true;
                mManager.beginTransaction().replace(R.id.frag_main, new DiscoverFragment(), MY_FRAGMENT_TAG)
                        .commit();
                return true;
            case R.id.menu_my_music:
                if (!isReadAble) return false;
                if (mNavigationView.getSelectedItemId() == R.id.menu_my_music) return true;
                mManager.beginTransaction().replace(R.id.frag_main, new MyMusicFragment(), MY_FRAGMENT_TAG)
                        .commit();
                return true;
            case R.id.menu_settings:
                if (mNavigationView.getSelectedItemId() == R.id.menu_settings) return true;
                mManager.beginTransaction().replace(R.id.frag_main, SettingFragment.newInstance(), MY_FRAGMENT_TAG)
                        .commit();
                return true;
        }
        return false;
    }

    private void checkReadStoragePremission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            isReadAble = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO},
                    MY_REQUEST_READ_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != MY_REQUEST_READ_STORAGE) return;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isReadAble = true;
        } else {
            Toast.makeText(this, getString(R.string.err_permission_deni), Toast.LENGTH_SHORT).show();
        }
        if (grantResults.length > 1 && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.err_permission_record_audio_deni), Toast.LENGTH_SHORT).show();
        }
    }

    private void synFavorite() {
        new Thread(() -> {
            TrackRepository trackRepository = TrackRepository.getInstance(TracksRemoteDataSource.getInstance(),
                    LocalDataSource.getInstance(getApplicationContext()));
            trackRepository.getFavoritesOnline(new TracksDataSource.FavoriteCallback() {
                @Override
                public void onFavoritesGetted(List<Track> tracks) {
                    trackRepository.deleteFavoriteDb();
                    for(Track track : tracks){
                        new DataBaseHelper(getApplicationContext()).addFavorite(track);
                    }
                }

                @Override
                public void onFavoriteAdded() {

                }

                @Override
                public void onFavoriteRemoved() {

                }

                @Override
                public void onFailure() {

                }

                @Override
                public void isFavorite(Boolean isFavorite) {

                }
            });
        }).start();

    }
}
