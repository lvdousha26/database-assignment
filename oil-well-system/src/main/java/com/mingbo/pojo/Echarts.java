package com.mingbo.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Echarts {
    private Integer id;
    private String uname;
    private String uvalue;
    private String statsType;
    private Integer activityCount;
    private Integer volunteerCount;
    private Integer participationCount;
    private Integer supplyUsedCount;
}
