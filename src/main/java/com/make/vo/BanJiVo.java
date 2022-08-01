package com.make.vo;

import com.make.entity.BanJi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BanJiVo extends BanJi {
    private Integer page;
    private Integer limit;
}
