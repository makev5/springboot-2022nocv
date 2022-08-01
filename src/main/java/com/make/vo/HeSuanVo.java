package com.make.vo;

import com.make.entity.HeSuan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeSuanVo extends HeSuan {
    private Integer page;
    private Integer limit;
}
