package com.make.vo;

import com.make.entity.XueYuan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XueYuanVo extends XueYuan {
    private Integer page;
    private Integer limit;
}
