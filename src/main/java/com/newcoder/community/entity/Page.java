package com.newcoder.community.entity;

// 封装分页相关信息
public class Page {
    // 当前页码
    private int current = 1;
    // 每页上限
    private int limit = 10;
    // 数据总数（用于计算有多少页）
    private int rows;
    // 查询路径
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1)
            this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100)
            this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0)
            this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行，数据库查询时需要用到
     * @return
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 获取总页码，页面上显示的页码不能超过这个
     * @return
     */
    public int getTotal() {
        if(rows % limit != 0) {
            return rows / limit + 1;
        }
        return rows / limit;
    }

    /**
     * 获取当前页一段的起始页码
     * @return
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取当前页一段的结束页码
     * @return
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
