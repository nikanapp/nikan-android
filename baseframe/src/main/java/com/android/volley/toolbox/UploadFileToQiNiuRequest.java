package com.android.volley.toolbox;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/23.
 */
public class UploadFileToQiNiuRequest extends UploadFileRequest {

    private String uploadtoken;

    private String filekey ; //上传到云存储的文件的key

    private String filePath;

    public UploadFileToQiNiuRequest(String uploadtoken, String filekey, String filePath, Listener listener) {
        super(listener);
        this.uploadtoken = uploadtoken;
        this.filekey = filekey;
        this.filePath = filePath;
    }

    public String getUploadtoken() {
        return uploadtoken;
    }

    public void setUploadtoken(String uploadtoken) {
        this.uploadtoken = uploadtoken;
    }

    public String getFilekey() {
        return filekey;
    }

    public void setFilekey(String filekey) {
        this.filekey = filekey;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
