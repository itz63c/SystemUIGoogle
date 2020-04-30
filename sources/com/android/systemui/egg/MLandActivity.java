package com.android.systemui.egg;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;

public class MLandActivity extends Activity {
    MLand mLand;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C2013R$layout.mland);
        MLand mLand2 = (MLand) findViewById(C2011R$id.world);
        this.mLand = mLand2;
        mLand2.setScoreFieldHolder((ViewGroup) findViewById(C2011R$id.scores));
        this.mLand.setSplash(findViewById(C2011R$id.welcome));
        int size = this.mLand.getGameControllers().size();
        if (size > 0) {
            this.mLand.setupPlayers(size);
        }
    }

    public void updateSplashPlayers() {
        int numPlayers = this.mLand.getNumPlayers();
        View findViewById = findViewById(C2011R$id.player_minus_button);
        View findViewById2 = findViewById(C2011R$id.player_plus_button);
        if (numPlayers == 1) {
            findViewById.setVisibility(4);
            findViewById2.setVisibility(0);
            findViewById2.requestFocus();
        } else if (numPlayers == 6) {
            findViewById.setVisibility(0);
            findViewById2.setVisibility(4);
            findViewById.requestFocus();
        } else {
            findViewById.setVisibility(0);
            findViewById2.setVisibility(0);
        }
    }

    public void onPause() {
        this.mLand.stop();
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        this.mLand.onAttachedToWindow();
        updateSplashPlayers();
        this.mLand.showSplash();
    }

    public void playerMinus(View view) {
        this.mLand.removePlayer();
        updateSplashPlayers();
    }

    public void playerPlus(View view) {
        this.mLand.addPlayer();
        updateSplashPlayers();
    }

    public void startButtonPressed(View view) {
        findViewById(C2011R$id.player_minus_button).setVisibility(4);
        findViewById(C2011R$id.player_plus_button).setVisibility(4);
        this.mLand.start(true);
    }
}
