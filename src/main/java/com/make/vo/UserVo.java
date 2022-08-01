package com.make.vo;

import com.make.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo extends User {
    private Integer page = 1;
    private Integer limit = 10;
}
