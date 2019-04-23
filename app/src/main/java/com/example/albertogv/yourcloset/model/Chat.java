package com.example.albertogv.yourcloset.model;

import android.net.Uri;

public class Chat {
    public String productPhotoUrl;
    public String productName;
    public String lastMessage;
    public String sellerPhotoUrl;
    public String sellerDispalyName;
    public String sellerUid;
    public String buyerUid;
    public String getPhotoBuyer() {
        return photoBuyer;
    }

    public void setPhotoBuyer(String photoBuyer) {
        this.photoBuyer = photoBuyer;
    }

    public String photoBuyer;
    public String buyerDisplayName;



}
