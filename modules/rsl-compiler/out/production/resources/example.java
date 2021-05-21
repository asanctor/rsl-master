
package rsl.core;

import rsl.core.coremodel.RSL_Resource;

public class PDFResource extends RSL_Resource {

    private int fileSize;
    private int pageCount;

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
    
}
