package com.albpintado.elemenst.lineitem;

public class LineItemDto {

    private String content;

    private String creationDate;

    private Long lineListId;

    private boolean completedStatus;

    private boolean pinnedStatus;

    public String getContent() {
        return content;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public Long getLineListId() {
        return lineListId;
    }

    public boolean getCompletedStatus() {
        return completedStatus;
    }

    public boolean getPinnedStatus() {
        return pinnedStatus;
    }
}
