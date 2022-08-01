package com.make.vo;

import com.make.entity.NocvNews;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NocvNewsVo extends NocvNews {
    private Integer page=1;
    private Integer limit=10;
}
