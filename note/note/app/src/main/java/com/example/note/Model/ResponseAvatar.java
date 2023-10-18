package com.example.note.Model;

public class ResponseAvatar {
    private boolean status;
    private AvatarUrl avatar;

    public ResponseAvatar(boolean status, AvatarUrl avatar) {
        this.status = status;
        this.avatar = avatar;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public AvatarUrl getAvatar() {
        return avatar;
    }

    public void setAvatar(AvatarUrl avatar) {
        this.avatar = avatar;
    }
}
