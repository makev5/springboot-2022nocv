package com.make.vo;

import com.make.entity.Vaccine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccineVo extends Vaccine {
    private Integer page = 1;
    private Integer limit = 10;
}
