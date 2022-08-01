package com.make.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String salt;

    private String sex;
    private Integer age;
    private String address;
    private String img;
    private String phone;
    private String card;

    /** 作为外键合适 */
    private Integer banJiId;
    private Integer xueYuanId;
    private Integer teacherId;

    // 非数据库列 班级名字
    @TableField(exist = false)
    private String banJiName;

    // 非数据库列 学院名字
    @TableField(exist = false)
    private String xueYuanName;

    // 非数据库列 老师名字
    @TableField(exist = false)
    private String teacherName;

}
