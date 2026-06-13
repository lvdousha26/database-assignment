package com.mingbo.pojo;



import java.util.List;

//分页查询的JavaBean

public class PageVO<T> {
    // 总记录数
    private Long totalCount;
    // 当前页数据
    private List<T> rows;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "PageBean{" +
                "totalCount=" + totalCount +
                ", rows=" + rows +
                '}';
    }
}
