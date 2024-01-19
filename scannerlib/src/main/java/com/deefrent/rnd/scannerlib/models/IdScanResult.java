package com.deefrent.rnd.scannerlib.models;

import android.graphics.Bitmap;

public class IdScanResult {
    private CardDetails details;
    private Bitmap face;
    private Bitmap card;

    public CardDetails getDetails() {
        return details;
    }

    public void setDetails(CardDetails details) {
        this.details = details;
    }

    public Bitmap getFace() {
        return face;
    }

    public void setFace(Bitmap face) {
        this.face = face;
    }

    public Bitmap getCard() { return card; }

    public void setCard(Bitmap card) { this.card = card; }
}
