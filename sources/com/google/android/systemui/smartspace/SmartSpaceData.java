package com.google.android.systemui.smartspace;

import android.util.Log;

public class SmartSpaceData {
    SmartSpaceCard mCurrentCard;
    SmartSpaceCard mWeatherCard;

    public boolean hasWeather() {
        return this.mWeatherCard != null;
    }

    public boolean hasCurrent() {
        return this.mCurrentCard != null;
    }

    public long getExpirationRemainingMillis() {
        long expiration;
        long currentTimeMillis = System.currentTimeMillis();
        if (hasCurrent() && hasWeather()) {
            expiration = Math.min(this.mCurrentCard.getExpiration(), this.mWeatherCard.getExpiration());
        } else if (hasCurrent()) {
            expiration = this.mCurrentCard.getExpiration();
        } else if (!hasWeather()) {
            return 0;
        } else {
            expiration = this.mWeatherCard.getExpiration();
        }
        return expiration - currentTimeMillis;
    }

    public long getExpiresAtMillis() {
        if (hasCurrent() && hasWeather()) {
            return Math.min(this.mCurrentCard.getExpiration(), this.mWeatherCard.getExpiration());
        }
        if (hasCurrent()) {
            return this.mCurrentCard.getExpiration();
        }
        if (hasWeather()) {
            return this.mWeatherCard.getExpiration();
        }
        return 0;
    }

    public void clear() {
        this.mWeatherCard = null;
        this.mCurrentCard = null;
    }

    public boolean handleExpire() {
        boolean z;
        String str = "SmartspaceData";
        if (!hasWeather() || !this.mWeatherCard.isExpired()) {
            z = false;
        } else {
            if (SmartSpaceController.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("weather expired ");
                sb.append(this.mWeatherCard.getExpiration());
                Log.d(str, sb.toString());
            }
            this.mWeatherCard = null;
            z = true;
        }
        if (!hasCurrent() || !this.mCurrentCard.isExpired()) {
            return z;
        }
        if (SmartSpaceController.DEBUG) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("current expired ");
            sb2.append(this.mCurrentCard.getExpiration());
            Log.d(str, sb2.toString());
        }
        this.mCurrentCard = null;
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(this.mCurrentCard);
        sb.append(",");
        sb.append(this.mWeatherCard);
        sb.append("}");
        return sb.toString();
    }

    public SmartSpaceCard getWeatherCard() {
        return this.mWeatherCard;
    }

    public SmartSpaceCard getCurrentCard() {
        return this.mCurrentCard;
    }
}
