package com.philipe.app.classes;

public class ReportItem {

    public String produto;
    public long quantidade;
    public long total;

    public ReportItem(String produto, long quantidade, long total) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.total = total;
    }

}
