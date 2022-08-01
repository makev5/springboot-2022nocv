package com.make.vo;

import com.make.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuVo extends Menu {
    private Integer page=1;
    private Integer limit=10;
}
