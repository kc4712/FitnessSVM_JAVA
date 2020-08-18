package kr.co.greencomm.middleware.provider;

import android.content.ContentValues;
import android.net.Uri;

/**
 * Created by jeyang on 2016-08-31.
 */
public class ProviderValues {
    private Uri uri;
    private String[] projection;
    private String selection;
    private String[] selectionArgs;
    private String sortOrder;
    private ContentValues values;

    public ProviderValues() {

    }

    public ProviderValues(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, ContentValues values) {
        this.uri = uri;
        this.projection = projection;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.sortOrder = sortOrder;
        this.values = values;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String[] getProjection() {
        return projection;
    }

    public void setProjection(String[] projection) {
        this.projection = projection;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String[] getSelectionArgs() {
        return selectionArgs;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        this.selectionArgs = selectionArgs;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public ContentValues getValues() {
        return values;
    }

    public void setValues(ContentValues values) {
        this.values = values;
    }
}
